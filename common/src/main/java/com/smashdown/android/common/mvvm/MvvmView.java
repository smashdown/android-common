package com.smashdown.android.common.mvvm;

import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog.InputCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

public interface MvvmView {
    void hideKeyboard();

    void toast(@StringRes int stringResId);

    void toast(String message);

    void showProgressBar(String msg);

    void showProgressBar(@StringRes int msgResId);

    void hideProgressBar();

    void showConfirmDialog(String title,
                           String message,
                           String positiveButtonText,
                           String negativeButtonText,
                           SingleButtonCallback positiveCallback,
                           SingleButtonCallback negativeCallback,
                           boolean cancelable);

    void showInputTextDialog(String title,
                             String message,
                             int inputType,
                             boolean autoDismiss,
                             String hint,
                             String preFill,
                             String positiveButtonText,
                             String negativeButtonText,
                             InputCallback inputCallback,
                             SingleButtonCallback negativeCallback,
                             boolean cancelable);

    void onApiFailed(Throwable throwable, boolean needToFinish);

    boolean checkAndRequestPermissions(int requestCode, String[] neededPermission);
}