package com.smashdown.android.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.smashdown.android.common.event.HSEventEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class HSBaseView extends View {
    public HSBaseView(Context context) {
        super(context);
        init();
    }

    public HSBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HSBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe
    public void onEvent(HSEventEmpty event) {
    }
}