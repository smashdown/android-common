package com.smashdown.android.common.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smashdown.android.common.R;
import com.smashdown.android.common.adapter.listener.HSRefreshListener;

public class EmptyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public View     root;
    public TextView tvEmptyMessage;

    HSRefreshListener mRefreshListener;

    public EmptyViewHolder(View v, HSRefreshListener refreshListener) {
        super(v);

        root = v.findViewById(R.id.root);
        root.setOnClickListener(this);
        tvEmptyMessage = (TextView) v.findViewById(R.id.tvEmptyMessage);
        this.mRefreshListener = refreshListener;
    }

    @Override
    public void onClick(View v) {
        if (mRefreshListener != null)
            mRefreshListener.onRefresh();
    }
}