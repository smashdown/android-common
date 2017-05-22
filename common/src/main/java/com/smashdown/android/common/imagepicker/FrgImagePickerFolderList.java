package com.smashdown.android.common.imagepicker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.smashdown.android.common.imagepicker.util.MediaStoreImageUtil;
import com.smashdown.android.common.ui.HSBaseFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FrgImagePickerFolderList extends HSBaseFragment {
    private RecyclerView       mRvFolderList;
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

    @Override
    protected int getLayoutId() {
        return R.layout.frg_image_picker_folder_list;
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRvFolderList = (RecyclerView) view.findViewById(R.id.rvFolderList);

        mAdapter = new ImageFolderAdapter();
        mRvFolderList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvFolderList.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected boolean setupUI() {
        return true;
    }

    @Override
    public boolean updateData() {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... params) {
                mFolders.clear();

                // 읽기 불가능 한 파일 제거,
                List<HSImageFolderItem> bucketList = MediaStoreImageUtil.getBucketList(getActivity());
                List<HSImageFolderItem> existingFiles = new ArrayList<>();
                for (HSImageFolderItem item : bucketList) {
                    File testFile = new File(item.lastData);
                    if (testFile.exists())
                        existingFiles.add(item);
                }
                mFolders.addAll(existingFiles);

                HSImageFolderItem allImageFolder = new HSImageFolderItem();
                allImageFolder.bucketDisplayName = getString(R.string.hs_all_images);

                bindLastItem(allImageFolder, mFolders);
                if (!TextUtils.isEmpty(allImageFolder.lastData))
                    mFolders.add(0, allImageFolder);

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                updateUI();
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
        mAdapter.notifyDataSetChanged();

        return false;
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

            // Log.d("JJY", "Glide loading=" + folder.lastData);
            Glide.with(getActivity()).load(new File(folder.lastData)).centerCrop().listener(new RequestListener<File, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                    e.printStackTrace();

                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
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