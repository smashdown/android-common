package com.smashdown.android.common.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.smashdown.android.common.event.HSEventEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import icepick.Icepick;
import icepick.State;

public abstract class HSBaseView extends View {
    @State int selectedPosition; // This will be automatically saved and restored

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

    @Override
    protected Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    @Subscribe
    public void onEvent(HSEventEmpty event) {
    }
}