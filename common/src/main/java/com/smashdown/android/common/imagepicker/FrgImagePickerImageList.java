package com.smashdown.android.common.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smashdown.android.common.R;
import com.smashdown.android.common.hsrecyclerview.HSRecyclerView;
import com.smashdown.android.common.imagepicker.event.HSEventImageFolderSelected;
import com.smashdown.android.common.imagepicker.model.HSImageItem;
import com.smashdown.android.common.imagepicker.util.MediaStoreImageUtil;
import com.smashdown.android.common.ui.HSBaseActivity;
import com.smashdown.android.common.ui.HSBaseFragment;
import com.smashdown.android.common.util.AndroidUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FrgImagePickerImageList extends HSBaseFragment {
    private HSRecyclerView mRvImageList;
    private ImageAdapter   mAdapter;

    int mMinCount = 1;
    int mMaxCount = 1;

    private List<HSImageItem> mImages             = new ArrayList<>();
    private List<HSImageItem> mSelectedImageItems = new ArrayList<>();

    public static FrgImagePickerImageList newInstance(int minCount, int maxCount) {
        FrgImagePickerImageList frg = new FrgImagePickerImageList();

        Bundle bundle = new Bundle();
        bundle.putInt(HSImagePickerActivity.KEY_MIN_COUNT, minCount);
        bundle.putInt(HSImagePickerActivity.KEY_MAX_COUNT, maxCount);

        frg.setArguments(bundle);

        return frg;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image_picker, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (mSelectedImageItems.size() < mMinCount) {
                AndroidUtils.toast(getActivity(), String.format(getString(R.string.hs_err_min_image_count_with_value), mMinCount));
            } else {
                Intent intent = new Intent();

                ArrayList<Uri> result = new ArrayList<>();
                for (HSImageItem i : mSelectedImageItems) {
                    result.add(Uri.parse("file://" + i.data));
                }
                intent.putParcelableArrayListExtra(HSImagePickerActivity.EXTRA_IMAGE_URIS, result);

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_image_picker_image_list;
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        setHasOptionsMenu(true);

        if (bundle == null) {
            mMinCount = getArguments().getInt(HSImagePickerActivity.KEY_MIN_COUNT, 1);
            mMaxCount = getArguments().getInt(HSImagePickerActivity.KEY_MAX_COUNT, 1);
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRvImageList = (HSRecyclerView) view.findViewById(R.id.rvImageList);
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 3;
        mAdapter = new ImageAdapter();
        mRvImageList.setAdapter(new GridLayoutManager(getActivity(), spanCount), mAdapter);

        return view;
    }

    @Override
    protected boolean setupUI() {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((GridLayoutManager) mRvImageList.getLayoutManager()).setSpanCount(5);
        } else {
            ((GridLayoutManager) mRvImageList.getLayoutManager()).setSpanCount(3);
        }
    }

    @Override
    public boolean updateData() {
        return false;
    }

    @Override
    public boolean updateUI() {
        mRvImageList.setStatus(HSRecyclerView.HSRecyclerViewStatus.SUCCEED, -1);

        return false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    class ImageAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        public ImageAdapter() {
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_frg_image_picker_image_list_item, null);
            ItemViewHolder rcv = new ItemViewHolder(layoutView);

            return rcv;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            HSImageItem image = mImages.get(position);

            Glide.with(getActivity()).load(new File(image.data)).centerCrop().into(holder.ivImage);

            if (mSelectedImageItems.contains(image)) {
                holder.cbCheckBox.setChecked(true);
            } else {
                holder.cbCheckBox.setChecked(false);
            }

            holder.itemView.setTag(R.id.ivImage, holder.cbCheckBox);
            holder.itemView.setTag(R.id.cbCheckBox, image);
        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImage;
        CheckBox  cbCheckBox;

        public ItemViewHolder(View v) {
            super(v);

            ivImage = (ImageView) v.findViewById(R.id.ivImage);
            v.setOnClickListener(this);

            cbCheckBox = (CheckBox) v.findViewById(R.id.cbCheckBox);
        }

        @Override
        public void onClick(View view) {
            if (view.getTag(R.id.ivImage) != null) {
                HSImageItem imageItem = ((HSImageItem) view.getTag(R.id.cbCheckBox));
                CheckBox checkBox = ((CheckBox) view.getTag(R.id.ivImage));
                checkBox.toggle();

                if (checkBox.isChecked()) {
                    if (mSelectedImageItems.size() >= mMaxCount) {
                        checkBox.toggle();
                        AndroidUtils.toast(getActivity(), R.string.hs_err_exceed_max_image_count);
                        return;
                    }
                    mSelectedImageItems.add(imageItem);
                } else {
                    mSelectedImageItems.remove(imageItem);
                }
                updateActionBar(null);
            }
        }
    }

    private void updateActionBar(String title) {
        if (!TextUtils.isEmpty(title))
            ((HSBaseActivity) getActivity()).getSupportActionBar().setTitle(title);

        ((HSBaseActivity) getActivity()).getSupportActionBar().setSubtitle(String.format(getString(R.string.hs_image_selected_with_value), mSelectedImageItems.size(), mMaxCount));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HSEventImageFolderSelected event) {
        mSelectedImageItems.clear();
        updateActionBar(event.item.bucketDisplayName);

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                mRvImageList.setStatus(HSRecyclerView.HSRecyclerViewStatus.LOADING, -1);
            }

            @Override
            protected String doInBackground(String... params) {
                mImages.clear();
                mImages.addAll(MediaStoreImageUtil.getImagesByFolderName(getActivity(), params[0]));

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                mRvImageList.setStatus(HSRecyclerView.HSRecyclerViewStatus.SUCCEED, mImages.size());
            }
        }.execute(event.item.bucketDisplayName);

    }
}