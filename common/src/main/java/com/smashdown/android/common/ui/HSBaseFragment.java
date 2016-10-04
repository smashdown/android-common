package com.smashdown.android.common.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smashdown.android.common.event.HSEventEmpty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public abstract class HSBaseFragment extends Fragment {
    @State boolean empty;

    // Data
    protected abstract boolean setupData(Bundle savedInstanceState);

    public abstract boolean updateData();

    // UI
    protected abstract int getLayoutId();

    protected abstract boolean setupUI();

    public abstract boolean updateUI();


    // if this fragment used in viewPager it will be used for the tab strip.
    public abstract String getTitle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init modules
        EventBus.getDefault().register(this);

        // IcePick
        Icepick.restoreInstanceState(this, savedInstanceState);

        // init data
        setupData(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);

        setupUI();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateData();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Subscribe
    public void onEvent(HSEventEmpty event) {
    }
}