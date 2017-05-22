package com.smashdown.android.common.ui;

/**
 * 만약, onBackPressed를 Fragment에서 처리하고 싶다면, 해당 Fragment 는 이 Interface를 Override해야 함.
 */
public interface HSOnBackPressedListener {
    void onBackPressed();
}