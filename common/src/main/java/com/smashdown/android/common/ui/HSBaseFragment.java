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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init modules
        EventBus.getDefault().register(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        // init data
        setupData(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateData();
    }

    /**
     * All the fragments who extends this class, should call this method in onCreateView
     *
     * @param inflater
     * @param container
     * @param layoutID
     * @param frg
     * @return
     */
    protected View setContentView(LayoutInflater inflater, ViewGroup container, int layoutID, Fragment frg) {
        View rootView = inflater.inflate(layoutID, container, false);
        ButterKnife.bind(frg, rootView);

        setupUI();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * In this method, you have to initiate data for this module that has no dependancy
     *
     * @param savedInstanceState
     * @return
     */
    protected abstract boolean setupData(Bundle savedInstanceState);

    /**
     * Implements code to init views, without data.
     *
     * @return
     */
    protected abstract boolean setupUI();

    protected abstract boolean updateData();

    protected abstract boolean updateUI();

    // if this fragment used in viewPager it will be used for the tab strip.
    protected abstract String getTitle();

    @Subscribe
    public void onEvent(HSEventEmpty event) {
    }
}