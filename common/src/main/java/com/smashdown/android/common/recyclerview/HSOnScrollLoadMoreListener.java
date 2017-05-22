package com.smashdown.android.common.recyclerview;

import android.support.v7.widget.RecyclerView;

public class HSOnScrollLoadMoreListener extends RecyclerView.OnScrollListener {

    private HSAdapter         adapter;
    private HSRefreshListener refreshListener;
    private boolean           isReverse;

    public HSOnScrollLoadMoreListener(HSAdapter adapter, HSRefreshListener refreshListener, boolean isReverse) {
        this.adapter = adapter;
        this.refreshListener = refreshListener;
        this.isReverse = isReverse;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!recyclerView.canScrollVertically(isReverse ? -1 : 1)) {
            if (adapter != null && adapter.canLoadMore()) {
                refreshListener.onLoadMore(adapter.getPureItemCount());
            }
        } else if (dy < 0) {
            // Scrolled Up
        } else if (dy > 0) {
            // Scrolled Down
        }
    }
}
