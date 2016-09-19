package com.smashdown.android.common.hsrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smashdown.android.common.R;

import icepick.Icepick;
import icepick.State;

/**
 * Support only vertical
 */
public class HSRecyclerView extends FrameLayout {
    public enum HSRecyclerViewStatus {
        LOADING, FAILED, SUCCEED
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    // Recycler View
    protected View                       mLlRecyclerView;
    protected SwipeRefreshLayout         mSrlList;
    protected RecyclerView               mRvList;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected View                       mLlLoadingInside;

    // Empty View
    protected View      mViewEmpty;
    protected ImageView mIvEmptyLogo;
    protected TextView  mTvEmpty;

    // Loading View
    protected View mViewLoading;

    // Failed View
    protected View      mViewFailed;
    protected ImageView mIvFailed;
    protected TextView  mTvFailed;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private OnLoadMoreListener                   mLoadMoreListener;

    @State boolean mEnabledPullToRefresh = false;
    @State boolean mEnabledLoadMore      = false;

    @State boolean              mCanLoadMore   = false;
    @State boolean              mIsDoanloading = false;
    @State HSRecyclerViewStatus status         = HSRecyclerViewStatus.SUCCEED;

    @State int REFRESH_COUNT = 20;

    public HSRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public HSRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HSRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = setupUI(context);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.HSRecyclerView,
                    0, 0);

            try {
                // empty log
                int emptyLogoViewId = a.getResourceId(R.styleable.HSRecyclerView_emptyLog, -1);
                if (emptyLogoViewId > 0) {
                    mIvEmptyLogo.setImageResource(emptyLogoViewId);
                }

                // empty message
                String emptyMessage = a.getString(R.styleable.HSRecyclerView_emptyString);
                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::Init() - emptyMessage=" + emptyMessage);
                if (!TextUtils.isEmpty(emptyMessage)) {
                    mTvEmpty.setText(emptyMessage);
                }

                // failed message
                String failedMessage = a.getString(R.styleable.HSRecyclerView_failedString);
                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::Init() - failedMessage=" + failedMessage);
                if (!TextUtils.isEmpty(failedMessage)) {
                    mTvFailed.setText(failedMessage);
                }

                // failed logo
                int failedLogoViewId = a.getResourceId(R.styleable.HSRecyclerView_failedLogo, -1);
                if (failedLogoViewId > 0) {
                    mIvFailed.setImageResource(failedLogoViewId);
                }

                // enable refresh on empty view
                boolean enableRefreshOnEmptyView = a.getBoolean(R.styleable.HSRecyclerView_enableRefreshEmptyView, false);
                if (enableRefreshOnEmptyView)
                    mViewEmpty.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    });

                // enable refresh on failed view
                boolean enableRefreshOnFailedView = a.getBoolean(R.styleable.HSRecyclerView_enableRefreshFailedView, false);
                if (enableRefreshOnFailedView)
                    mViewFailed.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    });

                // load more item count
                REFRESH_COUNT = a.getInt(R.styleable.HSRecyclerView_refreshItemCount, 20);

                // enable pull to refresh
                mEnabledPullToRefresh = a.getBoolean(R.styleable.HSRecyclerView_enablePullToRefresh, false);
                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::Init() - mEnabledPullToRefresh=" + mEnabledPullToRefresh);
                mSrlList.setEnabled(mEnabledPullToRefresh);

                // enable load more
                mEnabledLoadMore = a.getBoolean(R.styleable.HSRecyclerView_enableLoadMore, false);
                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::Init() - mEnabledLoadMore=" + mEnabledLoadMore);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                a.recycle();
            }
        }

        addView(view);
    }

    private View setupUI(Context context) {
        View view = inflate(getContext(), R.layout.hs_recycler_view, null);

        mSrlList = (SwipeRefreshLayout) view.findViewById(R.id.mSrlList);
        mRvList = (RecyclerView) view.findViewById(R.id.mRvList);
        mLlRecyclerView = view.findViewById(R.id.mLlRecyclerView);
        mLlLoadingInside = view.findViewById(R.id.mLlLoadingInside);

        // Empty View
        mViewEmpty = view.findViewById(R.id.mViewEmpty);
        mIvEmptyLogo = (ImageView) view.findViewById(R.id.mIvEmptyLogo);
        mTvEmpty = (TextView) view.findViewById(R.id.mTvEmpty);

        // Loading View
        mViewLoading = view.findViewById(R.id.mViewLoading);

        // Failed View
        mViewFailed = view.findViewById(R.id.mViewFailed);
        mIvFailed = (ImageView) view.findViewById(R.id.mIvFailed);
        mTvFailed = (TextView) view.findViewById(R.id.mTvFailed);

        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) {
                    // Scrolled On Top
                } else if (!recyclerView.canScrollVertically(1)) {
                    if (!mEnabledLoadMore) {
                        Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::OnScrollListener() NO LOAD MORE cuz - mEnabledLoadMore=" + mEnabledLoadMore);
                        return;
                    }
                    if (!mCanLoadMore) {
                        Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::OnScrollListener() NO LOAD MORE cuz - mCanLoadMore=" + mCanLoadMore);
                        return;
                    }
                    if (mIsDoanloading) {
                        Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::OnScrollListener() NO LOAD MORE cuz - mIsDoanloading=" + mIsDoanloading);
                        return;
                    }
                    if (mLoadMoreListener == null) {
                        Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::OnScrollListener() NO LOAD MORE cuz - mLoadMoreListener=" + mLoadMoreListener);
                        return;
                    }
                    mIsDoanloading = true;
                    mLoadMoreListener.onLoadMore();
                } else if (dy < 0) {
                    // Scrolled Up
                } else if (dy > 0) {
                    // Scrolled Down
                }
//
//                if (mLayoutManager instanceof LinearLayoutManager) {
//                    int totalItem = mLayoutManager.getItemCount();
//                    int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
//
//                    if (totalItem >= REFRESH_COUNT && lastVisibleItem == totalItem - 1) {
//
//                    }
//                } else {
//
//                }
            }
        });

        return view;
    }


    public void setAdapter(RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter) {
        if (adapter == null) {
            Log.e("JJY", "adapter is null");
            return;
        }

        mLayoutManager = layoutManager;
        mRvList.setLayoutManager(mLayoutManager);
        mRvList.setAdapter(adapter);
    }

    public void setStatus(HSRecyclerViewStatus status, int lastAddedItemCount) {
        mSrlList.setRefreshing(false); // because we don't use it.

        this.status = status;

        Log.d("JJY", "mRvList=" + mRvList);
        Log.d("JJY", "mRvList.getAdapter()=" + mRvList.getAdapter());

        switch (status) {
            case LOADING:
                mIsDoanloading = true;
                if (mRvList.getAdapter().getItemCount() > 0) {
                    showRecyclerView();
                } else {
                    showLoadingView();
                }
                break;
            case FAILED:
                mIsDoanloading = false;
                showFailedView();
                break;
            case SUCCEED:
                mIsDoanloading = false;

                mRvList.getAdapter().notifyDataSetChanged();
                if (mRvList.getAdapter().getItemCount() > 0) {
                    showRecyclerView();
                } else {
                    showEmptyView();
                }

                if (lastAddedItemCount != -1) {
                    if (lastAddedItemCount < REFRESH_COUNT) {
                        mCanLoadMore = false;
                    } else {
                        mCanLoadMore = true;
                    }
                }

                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::setStatus() status=" + status.name() + ", currentItemCount=" + mRvList.getAdapter().getItemCount() + ", mCanLoadMore=" + mCanLoadMore);
                break;
        }
        if (mIsDoanloading && mLlRecyclerView.getVisibility() == View.VISIBLE) {
            mLlLoadingInside.setVisibility(View.VISIBLE);
        } else {
            mLlLoadingInside.setVisibility(View.GONE);
        }
    }

    private void showLoadingView() {
        mViewLoading.setVisibility(View.VISIBLE);
        mViewFailed.setVisibility(View.GONE);
        mViewEmpty.setVisibility(View.GONE);
        mLlRecyclerView.setVisibility(View.GONE);
    }

    private void showFailedView() {
        mViewLoading.setVisibility(View.GONE);
        mViewFailed.setVisibility(View.VISIBLE);
        mViewEmpty.setVisibility(View.GONE);
        mLlRecyclerView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        mViewFailed.setVisibility(View.GONE);
        mViewLoading.setVisibility(View.GONE);
        mViewEmpty.setVisibility(View.VISIBLE);
        mLlRecyclerView.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        mViewLoading.setVisibility(View.GONE);
        mViewFailed.setVisibility(View.GONE);
        mViewEmpty.setVisibility(View.GONE);
        mLlRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
        mSrlList.setOnRefreshListener(this.mRefreshListener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void refresh() {
        if (mRefreshListener != null)
            this.mRefreshListener.onRefresh();
    }

    public void setEmptyMessage(String message) {
        if (mTvEmpty != null) {
            mTvEmpty.setText(message);
        }
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return this.mLayoutManager;
    }

}