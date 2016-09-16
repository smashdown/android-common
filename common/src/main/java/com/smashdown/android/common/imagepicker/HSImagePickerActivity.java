package com.smashdown.android.common.imagepicker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.smashdown.android.common.R;
import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.imagepicker.model.HSImageItem;
import com.smashdown.android.common.imagepicker.util.MediaStoreImageUtil;
import com.smashdown.android.common.ui.HSBaseActivity;
import com.smashdown.android.common.ui.HSBaseFragment;
import com.smashdown.android.common.util.AndroidUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class HSImagePickerActivity extends HSBaseActivity implements HSImagePickerable, ViewPager.OnPageChangeListener {
    private static final int REQ_READ_EXTERNAL_STORAGE = 100;

    public static final String KEY_MIN_COUNT = "min_count";
    public static final String KEY_MAX_COUNT = "max_count";

    public static final String EXTRA_IMAGE_URIS = "extra_image_uris";

    private Toolbar toolbar;

    private View                 mLoadingLayout;
    private View                 mEmptyLayout;
    private ViewPager            mViewPager;
    private SectionsPagerAdapter mPagerAdapter;

    int mMinCount = 1;
    int mMaxCount = 1;

    private List<HSImageFolderItem> mFolders            = new ArrayList<>();
    private List<HSImageItem>       mSelectedImageItems = new ArrayList<>();
    private int                     mSelectedFolderIdx  = -1;

    public static Intent getIntent(Context context, int minCount, int maxCount) {
        Intent intent = new Intent(context, HSImagePickerActivity.class);

        intent.putExtra(KEY_MIN_COUNT, minCount);
        intent.putExtra(KEY_MAX_COUNT, maxCount);

        return intent;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateData();
            } else {
                AndroidUtils.toast(this, R.string.hs_permission_rationale_external_storage);
                finish();
//                mLoadingLayout.setVisibility(View.GONE);
//                mEmptyLayout.setVisibility(View.VISIBLE);
//                mViewPager.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_picker, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);
        if (mViewPager.getCurrentItem() == 0)
            item.setVisible(false);
        else
            item.setVisible(true);

        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_done) {
            if (mSelectedImageItems.size() < mMinCount) {
                AndroidUtils.toast(this, String.format(getString(R.string.hs_err_min_image_count_with_value), mMinCount));
            } else {
                Intent intent = new Intent();

                ArrayList<Uri> result = new ArrayList<>();
                for (HSImageItem i : mSelectedImageItems) {
                    result.add(Uri.parse(i.data));
                }
                intent.putParcelableArrayListExtra(HSImagePickerActivity.EXTRA_IMAGE_URIS, result);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return true;
    }

    @Override
    protected boolean setupUI(Bundle bundle) {
        setContentView(R.layout.image_picker_activity, this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mEmptyLayout = findViewById(R.id.empty_layout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        setupActionBar();

        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mLoadingLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);

        return false;
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
        if (!mayRequestExternalStorage()) {
            return false;
        }

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                mLoadingLayout.setVisibility(View.VISIBLE);
                mEmptyLayout.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... params) {
                mFolders.clear();
                mFolders.add(new HSImageFolderItem(getString(R.string.hs_all_images), new ArrayList<HSImageItem>()));

                List<HSImageItem> images = MediaStoreImageUtil.getAllImage(HSImagePickerActivity.this);
                for (HSImageItem i : images) {
                    String folderName = i.bucketDisplayName;
                    HSImageFolderItem folder = new HSImageFolderItem(folderName, null);
                    if (!mFolders.contains(folder)) {
                        folder.images = new ArrayList<>();
                        mFolders.add(folder);
                    }
                }
                for (HSImageItem i : images) {
                    mFolders.get(0).images.add(i);
                    for (HSImageFolderItem item : mFolders) {
                        if (item.name.equals(i.bucketDisplayName)) {
                            item.images.add(i);
                            break;
                        }
                    }
                }

                return "";
            }

            @Override
            protected void onPostExecute(String s) {

                if (mFolders.size() > 0) {
                    mLoadingLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);


                    mViewPager.setAdapter(mPagerAdapter);
                    mViewPager.addOnPageChangeListener(HSImagePickerActivity.this);

                    mViewPager.setCurrentItem(0);
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                }

                //                if (mRvFolderList != null) {
                //                    mAdapter.notifyDataSetChanged();
                //
                //                    mRvFolderList.hideProgress();
                //                    mRvFolderList.showRecycler();
                //                } else {
                //                    Log.e("TFApp", "recyclerview is null");
                //                }
            }
        }.execute("");

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

    @Override
    public void onImageFolderSelected(int pos) {
        mSelectedFolderIdx = pos;

        int curPos = mViewPager.getCurrentItem();
        if (curPos < mPagerAdapter.getCount() - 1)
            mViewPager.setCurrentItem(curPos + 1);
    }

    @Override
    public void onImageSelected(HSImageItem item) {
        mSelectedImageItems.add(item);
        updateActionBar();
    }

    @Override
    public void onImageUnelected(HSImageItem item) {
        mSelectedImageItems.remove(item);
        updateActionBar();
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        invalidateOptionsMenu();

        if (position == 0) {
            mSelectedImageItems.clear();
            mSelectedFolderIdx = -1;
        } else {
            if (mPagerAdapter.getFragments().get(position) != null) {
                ((HSBaseFragment) mPagerAdapter.getFragments().get(position)).updateUI();
            }
        }
        updateActionBar();
    }

    private void updateActionBar() {
        if (mViewPager.getCurrentItem() == 0) {
            getSupportActionBar().setTitle(R.string.hs_select_image);
        } else {
            if (mSelectedFolderIdx > -1)
                getSupportActionBar().setTitle(String.format("%s (%d/%d)", mFolders.get(mSelectedFolderIdx).name, mSelectedImageItems.size(), mMaxCount));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public List<HSImageFolderItem> getFolderList() {
        return mFolders;
    }

    @Override
    public List<HSImageItem> getSelectedImageItems() {
        return mSelectedImageItems;
    }

    @Override
    public Integer getSelectedFolderIndex() {
        return mSelectedFolderIdx;
    }
}