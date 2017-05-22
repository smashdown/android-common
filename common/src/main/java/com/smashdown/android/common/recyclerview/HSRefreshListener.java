package com.smashdown.android.common.recyclerview;

import android.support.v4.widget.SwipeRefreshLayout;

public interface HSRefreshListener extends SwipeRefreshLayout.OnRefreshListener {

    void onLoadMore(int offset);

}