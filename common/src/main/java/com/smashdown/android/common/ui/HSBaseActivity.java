package com.smashdown.android.common.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
    private static final int REQ_PERMISSION_ALL = 9900;

    protected MaterialDialog mProgressDialog;
    @State    boolean        mIsProgressDialogShowing;

    protected abstract boolean setupData(Bundle savedInstanceState);

    public abstract boolean updateData();

    protected abstract int getLayoutId();

    protected abstract boolean setupUI(Bundle savedInstanceState);

    public abstract boolean updateUI();


    protected abstract void onNetworkConnected();

    protected abstract void onNetworkDisconnected();

    protected abstract boolean useDefaultTransitionAnimation();

    public String getTag() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init modules
        initProgressDialog();
        EventBus.getDefault().register(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // TODO: direction 다양하게
        if (useDefaultTransitionAnimation())
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        if (setupData(savedInstanceState)) {
            setContentView(getLayoutId());
            ButterKnife.bind(this);
            setupUI(savedInstanceState);

            updateData();
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

    public String getActivityName() {
        return getClass().getSimpleName();
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
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
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
