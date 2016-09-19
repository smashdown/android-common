package com.smashdown.android.common.imagepicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.smashdown.android.common.R;
import com.smashdown.android.common.imagepicker.event.HSEventImageFolderSelected;
import com.smashdown.android.common.ui.HSBaseActivity;
import com.smashdown.android.common.util.AndroidUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class HSImagePickerActivity extends HSBaseActivity implements ViewPager.OnPageChangeListener {
    private static final int REQ_READ_EXTERNAL_STORAGE = 100;

    public static final String KEY_MIN_COUNT = "min_count";
    public static final String KEY_MAX_COUNT = "max_count";

    public static final String EXTRA_IMAGE_URIS = "extra_image_uris";

    protected Toolbar              toolbar;
    protected ViewPager            mViewPager;
    private   SectionsPagerAdapter mPagerAdapter;

    int mMinCount = 1;
    int mMaxCount = 1;

    public static Intent getIntent(Context context, int minCount, int maxCount) {
        Intent intent = new Intent(context, HSImagePickerActivity.class);

        intent.putExtra(KEY_MIN_COUNT, minCount);
        intent.putExtra(KEY_MAX_COUNT, maxCount);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean setupData(Bundle bundle) {
        if (bundle == null) {
            mMinCount = getIntent().getIntExtra(KEY_MIN_COUNT, 1);
            mMaxCount = getIntent().getIntExtra(KEY_MAX_COUNT, 1);
        }

        return true;
    }

    @Override
    protected boolean setupUI(Bundle bundle) {
        setContentView(R.layout.image_picker_activity, this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();

        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.addOnPageChangeListener(this);

        if (mayRequestExternalStorage()) {
            mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
        }

        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mPagerAdapter);
            } else {
                AndroidUtils.toast(this, R.string.hs_permission_rationale_external_storage);
                finish();
            }
        }
    }

    private boolean mayRequestExternalStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            new MaterialDialog.Builder(this)
                    .content(R.string.hs_permission_rationale_external_storage)
                    .cancelable(false)
                    .positiveText(android.R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQ_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQ_READ_EXTERNAL_STORAGE);
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        // Setup actionbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.hs_select_image);
    }

    @Override
    protected boolean updateData() {


        return false;
    }

    @Override
    protected boolean updateUI() {
        return true;
    }

    @Override
    protected void onNetworkConnected() {

    }

    @Override
    protected void onNetworkDisconnected() {

    }

    @Override
    public void onBackPressed() {
        int curPos = mViewPager.getCurrentItem();
        if (curPos > 0) {
            mViewPager.setCurrentItem(curPos - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected boolean useDefaultTransitionAnimation() {
        return true;
    }

    @Override
    public String getTag() {
        return null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Map<Integer, Fragment> mFragments = new HashMap<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frg = null;
            switch (position) {
                case 0:
                    frg = FrgImagePickerFolderList.newInstance();
                    break;
                case 1:
                    frg = FrgImagePickerImageList.newInstance(mMinCount, mMaxCount);
                    break;
            }
            mFragments.put(position, frg);
            return frg;
        }

        public Map<Integer, Fragment> getFragments() {
            return mFragments;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String pageTitle = "";
            return pageTitle;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HSEventImageFolderSelected event) {
        int curPos = mViewPager.getCurrentItem();
        if (curPos < mPagerAdapter.getCount() - 1)
            mViewPager.setCurrentItem(curPos + 1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateActionBar();
    }

    private void updateActionBar() {
        if (mViewPager.getCurrentItem() == 0) {
            getSupportActionBar().setTitle(R.string.hs_select_image);
            getSupportActionBar().setSubtitle(null);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}