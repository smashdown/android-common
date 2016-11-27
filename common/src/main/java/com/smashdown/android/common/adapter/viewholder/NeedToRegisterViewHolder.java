package com.smashdown.android.common.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smashdown.android.common.R;
import com.smashdown.android.common.adapter.listener.HSRefreshListener;

public class NeedToRegisterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public View     root;
    public TextView tvNeedToRegisterMessage;
    public Button   btnRegister;

    HSRefreshListener mRefreshListener;

    public NeedToRegisterViewHolder(View v, HSRefreshListener refreshListener) {
        super(v);

        root = v.findViewById(R.id.root);
        tvNeedToRegisterMessage = (TextView) v.findViewById(R.id.tvFailedMessage);
        btnRegister = (Button) v.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        this.mRefreshListener = refreshListener;
    }

    @Override
    public void onClick(View v) {
        if (mRefreshListener != null)
            mRefreshListener.onNeedToLogin();
    }
}