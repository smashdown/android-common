package com.smashdown.android.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class UiUtil {

    public static void toast(Context context, @StringRes int stringResId) {
        try {
            Toast.makeText(context, stringResId, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MaterialDialog showProgressDialog(Context context, MaterialDialog dialog, String msg) {
        if (dialog == null) {
            dialog = new MaterialDialog.Builder(context)
                    .content("Loading...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        dialog.dismiss();
        if (!TextUtils.isEmpty(msg)) {
            dialog.setContent(msg);
        }
        dialog.show();

        return dialog;
    }

    public static void hideProgressDialog(MaterialDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void showConfirmDialog(Context context,
                                         String title,
                                         String message,
                                         String positiveButtonText,
                                         String negativeButtonText,
                                         MaterialDialog.SingleButtonCallback positiveCallback,
                                         MaterialDialog.SingleButtonCallback negativeCallback,
                                         boolean cancelable) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        builder.content(message);
        builder.cancelable(cancelable);
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.positiveText(positiveButtonText);
        }
        if (positiveCallback != null) {
            builder.onPositive(positiveCallback);
        }
        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.negativeText(negativeButtonText);
        }
        if (negativeCallback != null) {
            builder.onNegative(negativeCallback);
        }
        builder.show();
    }

    public static void showInputTextDialog(Context context,
                                           String title,
                                           String message,
                                           int inputType,
                                           boolean autoDismiss,
                                           String hint,
                                           String preFill,
                                           String positiveButtonText,
                                           String negativeButtonText,
                                           MaterialDialog.InputCallback inputCallback,
                                           MaterialDialog.SingleButtonCallback negativeCallback,
                                           boolean cancelable) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        if (!TextUtils.isEmpty(message)) {
            builder.content(message);
        }
        builder.inputType(inputType);
        builder.autoDismiss(autoDismiss);
        builder.cancelable(cancelable);

        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.positiveText(positiveButtonText);
        }
        builder.input(hint, preFill, inputCallback);

        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.negativeText(negativeButtonText);
        }
        if (negativeCallback != null) {
            builder.onNegative(negativeCallback);
        }
        builder.show();
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            android.view.View view = activity.getCurrentFocus();
            if (view != null)
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Dialog dialog) {
        if (dialog != null) {
            InputMethodManager inputManager = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = dialog.getCurrentFocus();
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static float spToPx(Context ctx, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, ctx.getResources().getDisplayMetrics());
    }

    public static int dpToPx(Context ctx, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public static float pxToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


    public static void makeTextViewStrikeLine(TextView tv) {
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void makeTextViewUnderline(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

}
