package com.smashdown.android.common.adapter.listener;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by smashdown on 2016. 11. 10..
 */

public interface HSRefreshListener extends SwipeRefreshLayout.OnRefreshListener {
    void onLoadMore(int offset);
}
