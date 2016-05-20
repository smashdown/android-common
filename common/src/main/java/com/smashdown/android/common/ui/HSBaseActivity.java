package com.smashdown.android.common.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;
import com.smashdown.android.common.R;
import com.smashdown.android.common.event.HSEventCloseApp;
import com.smashdown.android.common.event.HSEventNetworkConnected;
import com.smashdown.android.common.event.HSEventNetworkDisconnected;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public abstract class HSBaseActivity extends AppCompatActivity {
    protected MaterialDialog mProgressDialog;
    @State    boolean        mIsProgressDialogShowing;

    protected abstract boolean setupData(Bundle savedInstanceState);

    protected abstract boolean setupUI(Bundle savedInstanceState);

    protected abstract boolean updateData();

    protected abstract boolean updateUI();

    protected abstract void onNetworkConnected();

    protected abstract void onNetworkDisconnected();

    protected abstract boolean useDefaultTransitionAnimation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.i("onCreate - " + getActivityName());

        // init modules
        initProgressDialog();
        EventBus.getDefault().register(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (setupData(savedInstanceState)) {
            setupUI(savedInstanceState);
            updateData();
            updateUI();
        } else {
            finish();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    private void initProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    protected void setContentView(int layoutID, Activity target) {
        setContentView(layoutID);
        ButterKnife.bind(target);
        if (useDefaultTransitionAnimation())
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public String getActivityName() {
        return getClass().getSimpleName();
    }

    public void showProgressDialog(String message) {
        mProgressDialog.dismiss();
        if (!TextUtils.isEmpty(message))
            mProgressDialog.setContent(message);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Subscribe
    public void onEvent(HSEventCloseApp event) {
        LogUtils.e("onEvent - HSEventCloseApp activity=" + getClass().getSimpleName());
        super.finish();
    }

    @Subscribe
    public void onEvent(HSEventNetworkConnected event) {
        LogUtils.e("onEvent - HSEventNetworkConnected activity=" + getClass().getSimpleName());
        onNetworkConnected();
    }

    @Subscribe
    public void onEvent(HSEventNetworkDisconnected event) {
        LogUtils.e("onEvent - HSEventNetworkDisconnected activity=" + getClass().getSimpleName());
        onNetworkDisconnected();
    }

    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }
}
