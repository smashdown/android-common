package com.smashdown.android.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidUtils {

    public static void toast(Context context, final String msg) {
        try {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toast(Context context, final int msgId) {
        try {
            Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void sendEmail(Activity context, int chooserTitleResId, String from, String to, String title) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(context);
        builder.setType("message/rfc822");
        builder.addEmailTo(to);

        StringBuilder content = new StringBuilder(title);
        if (!TextUtils.isEmpty(from))
            content.append("(").append(from).append(")");
        builder.setSubject(content.toString());
        builder.setChooserTitle(chooserTitleResId);
        builder.startChooser();
    }

    public static void call(Activity context, String phoneNumber, boolean immediately) {
        Intent myIntent = null;

        if (immediately)
            myIntent = new Intent(Intent.ACTION_CALL);
        else
            myIntent = new Intent(Intent.ACTION_DIAL);
        String phNum = "tel:" + phoneNumber;
        myIntent.setData(Uri.parse(phNum));
        context.startActivity(myIntent);
    }

    public static void sendMessage(Context context, String phone) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        // it.putExtra("sms_body", "The SMS text");
        context.startActivity(it);
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

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static void hideKeyboard(Dialog dialog) {
        if (dialog != null) {
            InputMethodManager inputManager = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = dialog.getCurrentFocus();
            if (view != null)
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //    public static void hideKeyboard(Activity context) {
    //        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    //        View v = context.getCurrentFocus();
    //        if (v != null)
    //            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    //    }
    //
    //    public static void showKeyboard(Activity activity) {
    //        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    //        View v = activity.getCurrentFocus();
    //        if (v != null)
    //            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
    //    }


    public static String getCountryCode(Context context) {
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String code = telManager.getNetworkCountryIso().toUpperCase();

            if (!TextUtils.isEmpty(code))
                return code;
            else {
                code = telManager.getSimCountryIso().toUpperCase();

                if (!TextUtils.isEmpty(code))
                    return code;
                else
                    return "AUS"; // TODO: 하드코딩
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static void goToMarket(Activity activity, String appPackageName) {
        // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
