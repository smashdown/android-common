package com.smashdown.android.common.imagepicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.smashdown.android.common.R;
import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.ui.HSBaseFragment;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FrgImagePickerFolderList extends HSBaseFragment {


    private SuperRecyclerView          mRvFolderList;
    private ImageFolderAdapter         mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<HSImageFolderItem> mFolders;

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
        View view = setContentView(inflater, container, R.layout.frg_image_picker_folder_list, this);

        return view;
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        mFolders = ((HSImagePickerable) getActivity()).getFolderList();

        return true;
    }

    @Override
    protected boolean setupUI() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvFolderList.setLayoutManager(mLayoutManager);

        mAdapter = new ImageFolderAdapter();
        mRvFolderList.setAdapter(mAdapter);

        return true;
    }

    @Override
    public boolean updateData() {
        return false;
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
            holder.tvFolderName.setText(mFolders.get(position).name);
            holder.tvImageCount.setText(String.valueOf(mFolders.get(position).images.size()));

            Glide.with(getActivity()).load(new File(mFolders.get(position).images.get(0).data)).centerCrop().into(holder.ivImage);
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
            int pos = getAdapterPosition();
            ((HSImagePickerable) getActivity()).onImageFolderSelected(pos);
        }
    }


}