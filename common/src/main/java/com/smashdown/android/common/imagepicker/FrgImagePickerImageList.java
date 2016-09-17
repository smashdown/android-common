package com.smashdown.android.common.imagepicker;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.smashdown.android.common.R;
import com.smashdown.android.common.R2;
import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.imagepicker.model.HSImageItem;
import com.smashdown.android.common.ui.HSBaseFragment;
import com.smashdown.android.common.util.AndroidUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrgImagePickerImageList extends HSBaseFragment {
    private SuperRecyclerView          mRvImageList;
    private ImageAdapter               mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    int mMinCount = 1;
    int mMaxCount = 1;

    // TODO: onsavestate

    private List<HSImageFolderItem> mFolders;
    private List<HSImageItem>       mSelectedImageItems;

    public static FrgImagePickerImageList newInstance(int minCount, int maxCount) {
        FrgImagePickerImageList frg = new FrgImagePickerImageList();

        Bundle bundle = new Bundle();
        bundle.putInt(HSImagePickerActivity.KEY_MIN_COUNT, minCount);
        bundle.putInt(HSImagePickerActivity.KEY_MAX_COUNT, maxCount);

        frg.setArguments(bundle);

        return frg;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFolders = ((HSImagePickerable) getActivity()).getFolderList();
        mSelectedImageItems = ((HSImagePickerable) getActivity()).getSelectedImageItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setContentView(inflater, container, R.layout.frg_image_picker_image_list, this);

        return view;
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        if (bundle == null) {
            mMinCount = getArguments().getInt(HSImagePickerActivity.KEY_MIN_COUNT, 1);
            mMaxCount = getArguments().getInt(HSImagePickerActivity.KEY_MAX_COUNT, 1);
        }
        return true;
    }

    @Override
    protected boolean setupUI() {
        int spanCount = 3;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 5;
        }

        mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        mRvImageList.setLayoutManager(mLayoutManager);

        mAdapter = new ImageAdapter();
        mRvImageList.setAdapter(mAdapter);

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((GridLayoutManager) mLayoutManager).setSpanCount(5);
        } else {
            ((GridLayoutManager) mLayoutManager).setSpanCount(3);
        }
    }

    @Override
    public boolean updateData() {
        return false;
    }

    @Override
    public boolean updateUI() {
        mAdapter.notifyDataSetChanged();

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
            int mSelectedFolderIndex = ((HSImagePickerable) getActivity()).getSelectedFolderIndex();
            if (mSelectedFolderIndex > -1) {
                Glide.with(getActivity()).load(new File(mFolders.get(mSelectedFolderIndex).images.get(position).data)).centerCrop().into(holder.ivImage);

                if (mSelectedImageItems.contains(mFolders.get(mSelectedFolderIndex).images.get(position))) {
                    holder.cbCheckBox.setChecked(true);
                } else {
                    holder.cbCheckBox.setChecked(false);
                }
                holder.itemView.setTag(R.id.ivImage, holder.cbCheckBox);
            }
        }

        @Override
        public int getItemCount() {
            int mSelectedFolderIndex = ((HSImagePickerable) getActivity()).getSelectedFolderIndex();
            if (mSelectedFolderIndex > -1) {
                return mFolders.get(mSelectedFolderIndex).images.size();
            } else {
                return 0;
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R2.id.ivImage)    ImageView ivImage;
        @BindView(R2.id.cbCheckBox) CheckBox  cbCheckBox;

        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int selectedFolderIndex = ((HSImagePickerable) getActivity()).getSelectedFolderIndex();
            int pos = getAdapterPosition();

            HSImageItem imageItem = mFolders.get(selectedFolderIndex).images.get(pos);

            if (mSelectedImageItems.contains(imageItem)) {
                ((HSImagePickerable) getActivity()).onImageUnelected(imageItem);
            } else {
                if (mSelectedImageItems.size() + 1 > mMaxCount) {
                    AndroidUtils.toast(getActivity(), R.string.hs_err_exceed_max_image_count);
                    return;
                } else {
                    ((HSImagePickerable) getActivity()).onImageSelected(imageItem);
                }
            }

            if (view.getTag(R.id.ivImage) != null)
                ((CheckBox) view.getTag(R.id.ivImage)).toggle();
        }
    }
}