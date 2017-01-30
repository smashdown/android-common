package com.smashdown.android.common.mvp;

public interface BaseView {
    void showProgressDialog(int stringResId);

    void showProgressDialog(String str);

    void hideProgressDialog();

    void toast(int stringResId);

    void toast(String str);

    void hideKeyboard();
}
