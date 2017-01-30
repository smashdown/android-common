package com.smashdown.android.common.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.smashdown.android.common.R;
import com.smashdown.android.common.event.HSEventCloseApp;
import com.smashdown.android.common.event.HSEventNetworkConnected;
import com.smashdown.android.common.event.HSEventNetworkDisconnected;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public abstract class HSBaseActivity extends AppCompatActivity {
    public enum HSTransitionDirection {
        NONE, FROM_RIGHT_TO_LEFT, FROM_LEFT_TO_RIGHT, FROM_TOP_TO_BOTTOM, FROM_BOTTOM_TO_TOP
    }

    protected MaterialDialog mProgressDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract boolean setupData(Bundle savedInstanceState);

    protected abstract boolean setupUI(Bundle savedInstanceState);

    public abstract boolean updateData();

    public abstract boolean updateUI();

    protected void onNetworkConnected() {
    }

    protected void onNetworkDisconnected() {
    }

    protected HSTransitionDirection getTransitionAnimationDirection() {
        return HSTransitionDirection.FROM_RIGHT_TO_LEFT;
    }

    public String getTag() {
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // init modules
        initProgressDialog();
        EventBus.getDefault().register(this);

        switch (getTransitionAnimationDirection()) {
            case FROM_RIGHT_TO_LEFT:
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case FROM_LEFT_TO_RIGHT:
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case FROM_TOP_TO_BOTTOM:
                overridePendingTransition(R.anim.pull_in_top, R.anim.push_out_bottom);
                break;
            case FROM_BOTTOM_TO_TOP:
                overridePendingTransition(R.anim.pull_in_bottom, R.anim.push_out_top);
                break;
            case NONE:
                // no animation
                break;
        }

        if (setupData(savedInstanceState)) {
            setContentView(getLayoutId());
            ButterKnife.bind(this);
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
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .content("Loading...")
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    public void showProgressDialog(int stringResId) {
        showProgressDialog(getString(stringResId));
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            if (!TextUtils.isEmpty(message))
                mProgressDialog.setContent(message);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void finish() {
        super.finish();

        switch (getTransitionAnimationDirection()) {
            case FROM_RIGHT_TO_LEFT:
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case FROM_LEFT_TO_RIGHT:
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;
            case FROM_TOP_TO_BOTTOM:
                overridePendingTransition(R.anim.pull_in_bottom, R.anim.push_out_top);
                break;
            case FROM_BOTTOM_TO_TOP:
                overridePendingTransition(R.anim.pull_in_top, R.anim.push_out_bottom);
                break;
        }
    }

    @Subscribe
    public void onEvent(HSEventCloseApp event) {
        Log.e("JJY", "onEvent - HSEventCloseApp activity=" + getClass().getSimpleName());
        super.finish();
    }

    @Subscribe
    public void onEvent(HSEventNetworkConnected event) {
        Log.e("JJY", "onEvent - HSEventNetworkConnected activity=" + getClass().getSimpleName());
        onNetworkConnected();
    }

    @Subscribe
    public void onEvent(HSEventNetworkDisconnected event) {
        Log.e("JJY", "onEvent - HSEventNetworkDisconnected activity=" + getClass().getSimpleName());
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
