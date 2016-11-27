package com.smashdown.android.common.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smashdown.android.common.R;


public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public View     root;
    public TextView tvLoadingMessage;

    public LoadingViewHolder(View v) {
        super(v);

        root = v.findViewById(R.id.root);
        tvLoadingMessage = (TextView) v.findViewById(R.id.tvLoadingMessage);
    }
}