package com.smashdown.android.common.adapter.listener;

import android.support.v7.widget.RecyclerView;


/**
 * Created by smashdown on 2016. 11. 10..
 */

public class HSOnScrollLoadMoreListener extends RecyclerView.OnScrollListener {
    private HSBaseRefreshInterface mOnLoadMoreListener;
    private HSRefreshListener      mRefreshListener;
    private boolean                mIsReverse;

    public HSOnScrollLoadMoreListener(HSBaseRefreshInterface adapter, HSRefreshListener refreshListener, boolean isReverse) {
        this.mOnLoadMoreListener = adapter;
        this.mRefreshListener = refreshListener;
        this.mIsReverse = isReverse;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!recyclerView.canScrollVertically(mIsReverse ? 1 : -1)) {
            // Scrolled On Top
        } else if (!recyclerView.canScrollVertically(mIsReverse ? -1 : 1)) {
            if (mOnLoadMoreListener != null && mOnLoadMoreListener.canLoadMore()) {
                mRefreshListener.onLoadMore(mOnLoadMoreListener.getPureItemCount());
            }
        } else if (dy < 0) {
            // Scrolled Up
        } else if (dy > 0) {
            // Scrolled Down
        }
    }
}
