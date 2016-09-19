package com.smashdown.android.common.imagepicker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.smashdown.android.common.R;
import com.smashdown.android.common.imagepicker.event.HSEventImageFolderSelected;
import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.imagepicker.model.HSImageItem;
import com.smashdown.android.common.imagepicker.util.MediaStoreImageUtil;
import com.smashdown.android.common.ui.HSBaseFragment;
import com.smashdown.android.common.hsrecyclerview.HSRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FrgImagePickerFolderList extends HSBaseFragment {
    private HSRecyclerView     mRvFolderList;
    private ImageFolderAdapter mAdapter;

    private List<HSImageFolderItem> mFolders = new ArrayList<>();

    public static FrgImagePickerFolderList newInstance() {
        FrgImagePickerFolderList frg = new FrgImagePickerFolderList();
        return frg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_image_picker_folder_list, container, false);
        mRvFolderList = (HSRecyclerView) rootView.findViewById(R.id.mRvFolderList);

        setupUI();

        return rootView;
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        return true;
    }


    @Override
    protected boolean setupUI() {

        mAdapter = new ImageFolderAdapter();
        mRvFolderList.setAdapter(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false), mAdapter);

        return true;
    }

    @Override
    public boolean updateData() {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                mRvFolderList.setStatus(HSRecyclerView.HSRecyclerViewStatus.LOADING, -1);
            }

            @Override
            protected String doInBackground(String... params) {
                mFolders.clear();

                mFolders.addAll(MediaStoreImageUtil.getBucketList(getActivity()));

                HSImageFolderItem allImageFolder = new HSImageFolderItem();
                allImageFolder.bucketDisplayName = getString(R.string.hs_all_images);

                bindLastItem(allImageFolder, mFolders);
                if (!TextUtils.isEmpty(allImageFolder.lastData))
                    mFolders.add(0, allImageFolder);

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                mRvFolderList.setStatus(HSRecyclerView.HSRecyclerViewStatus.SUCCEED, mFolders.size());
            }

            private void bindLastItem(HSImageFolderItem allFolder, List<HSImageFolderItem> folders) {
                if (folders != null && folders.size() > 0) {
                    allFolder.imageCount = 0;
                    allFolder.bucketId = folders.get(0).bucketId;
                    allFolder.lastData = folders.get(0).lastData;
                    allFolder.lastDateTaken = folders.get(0).lastDateTaken;

                    for (HSImageFolderItem item : folders) {
                        allFolder.imageCount += item.imageCount;
                        if (item.lastDateTaken > allFolder.lastDateTaken) {
                            allFolder.bucketId = item.bucketId;
                            allFolder.lastData = item.lastData;
                            allFolder.lastDateTaken = item.lastDateTaken;
                        }
                    }
                }
            }
        }.execute("");

        return true;
    }

    @Override
    public boolean updateUI() {
        return false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    class ImageFolderAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        public ImageFolderAdapter() {
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_frg_image_picker_folder_list_item, null);
            ItemViewHolder rcv = new ItemViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            HSImageFolderItem folder = mFolders.get(position);

            holder.tvFolderName.setText(folder.bucketDisplayName);
            holder.tvImageCount.setText(String.valueOf(folder.imageCount));

            Log.d("JJY", "Glide loading=" + folder.lastData);
            Glide.with(getActivity()).load("file:" + folder.lastData).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    e.printStackTrace();
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.ivImage);

            holder.root.setTag(R.id.root, folder);
        }

        @Override
        public int getItemCount() {
            return mFolders.size();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View      root;
        ImageView ivImage;
        TextView  tvFolderName;
        TextView  tvImageCount;

        public ItemViewHolder(View v) {
            super(v);

            root = v.findViewById(R.id.root);
            root.setOnClickListener(this);

            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            tvFolderName = (TextView) v.findViewById(R.id.tvFolderName);
            tvImageCount = (TextView) v.findViewById(R.id.tvImageCount);
        }

        @Override
        public void onClick(View view) {
            if (view.getTag(R.id.root) != null) {
                EventBus.getDefault().post(new HSEventImageFolderSelected((HSImageFolderItem) (view.getTag(R.id.root))));
            } else {
                Log.e("JJY", "tag is null");
            }
        }
    }
}