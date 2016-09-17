package com.smashdown.android.common.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class HSRecyclerView extends FrameLayout {
    public enum HSRecyclerViewStatus {
        LOADING, FAILED, SUCCEED
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    SwipeRefreshLayout mSrlList;
    RecyclerView       mRvList;
    private LinearLayoutManager mLayoutManager;

    // Empty View
    View      mViewEmpty;
    ImageView mIvEmptyLogo;
    TextView  mTvEmpty;

    // Loading View
    View mViewLoading;

    // Failed View
    View      mViewFailed;
    ImageView mIvFailed;
    TextView  mTvFailed;

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
        View view = inflate(getContext(), R.layout.hs_recycler_view, null);

        mSrlList = (SwipeRefreshLayout) view.findViewById(R.id.mSrlList);
        mRvList = (RecyclerView) view.findViewById(R.id.mRvList);

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

        mLayoutManager = new LinearLayoutManager(context);
        mRvList.setLayoutManager(mLayoutManager);
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItem = mLayoutManager.getItemCount();
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                if (totalItem >= 20 && lastVisibleItem == totalItem - 1) {
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
                }
            }
        });

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

                // enable reverseLayout
                boolean reverseLayout = a.getBoolean(R.styleable.HSRecyclerView_reverseLayout, false);
                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::Init() - reverseLayout=" + reverseLayout);
                mLayoutManager.setReverseLayout(reverseLayout);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                a.recycle();
            }
        }

        addView(view);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            Log.e("JJY", "adapter is null");
            return;
        }

        mRvList.setAdapter(adapter);
    }

    public void setStatus(HSRecyclerViewStatus status, int lastAddedItemCount) {
        mSrlList.setRefreshing(false); // because we don't use it.

        this.status = status;

        switch (status) {
            case LOADING:
                mIsDoanloading = true;
                mViewEmpty.setVisibility(View.GONE);
                mViewFailed.setVisibility(View.GONE);
                mViewLoading.setVisibility(View.VISIBLE);
                mSrlList.setVisibility(View.GONE);
                mRvList.setVisibility(View.GONE);
                break;
            case FAILED:
                mIsDoanloading = false;
                mViewEmpty.setVisibility(View.GONE);
                mViewFailed.setVisibility(View.VISIBLE);
                mViewLoading.setVisibility(View.GONE);
                mSrlList.setVisibility(View.GONE);
                mRvList.setVisibility(View.GONE);
                break;
            case SUCCEED:
                mIsDoanloading = false;
                mViewFailed.setVisibility(View.GONE);
                mViewLoading.setVisibility(View.GONE);

                mRvList.getAdapter().notifyDataSetChanged();
                int currentItemCount = mRvList.getAdapter().getItemCount();
                if (currentItemCount > 0) {
                    mViewEmpty.setVisibility(View.GONE);
                    mSrlList.setVisibility(View.VISIBLE);
                    mRvList.setVisibility(View.VISIBLE);
                } else {
                    mViewEmpty.setVisibility(View.VISIBLE);
                    mSrlList.setVisibility(View.GONE);
                    mRvList.setVisibility(View.GONE);
                }

                if (lastAddedItemCount != -1) {
                    if (lastAddedItemCount < REFRESH_COUNT) {
                        mCanLoadMore = false;
                    } else {
                        mCanLoadMore = true;
                    }
                }

                Log.d("JJY", HSRecyclerView.class.getSimpleName() + "::setStatus() status=" + status.name() + ", currentItemCount=" + currentItemCount + ", mCanLoadMore=" + mCanLoadMore);
                break;
        }
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

}