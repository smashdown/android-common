package com.smashdown.android.common.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.smashdown.android.common.event.HSEventEmpty;
import com.smashdown.android.common.mvp.BaseView;
import com.smashdown.android.common.util.AndroidUtils;
import com.smashdown.android.common.util.UiUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class HSBaseFragment extends Fragment implements BaseView {
    protected MaterialDialog mProgressDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract boolean setupData(Bundle savedInstanceState);

    protected abstract boolean setupUI();

    public abstract boolean updateData();

    public abstract boolean updateUI();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init modules
        EventBus.getDefault().register(this);
        initProgressDialog();

        // init data
        setupData(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        setupUI();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateData();
        updateUI();
    }

    private void initProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(getActivity())
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onEvent(HSEventEmpty event) {
    }

    @Override
    public void showProgressDialog(int stringResId) {
        showProgressDialog(getString(stringResId));
    }

    @Override
    public void showProgressDialog(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            if (!TextUtils.isEmpty(message))
                mProgressDialog.setContent(message);
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void toast(int stringResId) {
        UiUtil.toast(getActivity(), stringResId);
    }

    @Override
    public void toast(String str) {
        UiUtil.toast(getActivity(), str);
    }

    @Override
    public void hideKeyboard() {
        UiUtil.hideKeyboard(getActivity());
    }
}