package com.smashdown.android.common.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.smashdown.android.common.R;
import com.smashdown.android.common.event.HSEventCloseApp;
import com.smashdown.android.common.event.HSEventNetworkConnected;
import com.smashdown.android.common.event.HSEventNetworkDisconnected;
import com.smashdown.android.common.event.HSEventPermissionAllGranted;
import com.smashdown.android.common.event.HSEventPermissionDenied;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public abstract class HSBaseActivity extends AppCompatActivity {
    public enum HSTransitionDirection {
        NONE, FROM_RIGHT_TO_LEFT, FROM_LEFT_TO_RIGHT, FROM_TOP_TO_BOTTOM, FROM_BOTTOM_TO_TOP
    }

    private static final int REQ_PERMISSION_ALL = 9900;

    private HSTransitionDirection mTransitionAnamationDirection = HSTransitionDirection.FROM_RIGHT_TO_LEFT;
    protected MaterialDialog mProgressDialog;
    @State    boolean        mIsProgressDialogShowing;

    protected abstract boolean setupData(Bundle savedInstanceState);

    public abstract boolean updateData();

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract boolean setupUI(Bundle savedInstanceState);

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
        Icepick.restoreInstanceState(this, savedInstanceState);

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
        Icepick.saveInstanceState(this, outState);
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

    public boolean checkPermissions(String[] neededPermission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (neededPermission == null || neededPermission.length < 1) {
            return true;
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : neededPermission) {
            int permissionStatus = ContextCompat.checkSelfPermission(this, permission);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                Log.i("JJY", "checkPermissions() - need " + permission);
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            Log.i("JJY", "checkPermissions() - send request size=" + listPermissionsNeeded.size());
            return false;
        }
        return true;
    }

    public boolean checkAndRequestPermissions(String[] neededPermission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (neededPermission == null || neededPermission.length < 1) {
            return true;
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : neededPermission) {
            int permissionStatus = ContextCompat.checkSelfPermission(this, permission);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                Log.i("JJY", "checkAndRequestPermissions() - need " + permission);
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            Log.i("JJY", "checkAndRequestPermissions() - send request size=" + listPermissionsNeeded.size());
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQ_PERMISSION_ALL);
            return false;
        }
        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION_ALL) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new HSEventPermissionDenied(permissions[i]));
                    return;
                }
            }
        }
        EventBus.getDefault().post(new HSEventPermissionAllGranted(Arrays.asList(permissions)));
    }
}
