package com.smashdown.android.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class AndroidUtils {

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


    public static String getCountryCode(Context context) {
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String code = telManager.getNetworkCountryIso().toUpperCase();

            if (!TextUtils.isEmpty(code)) {
                return code;
            } else {
                code = telManager.getSimCountryIso().toUpperCase();

                if (!TextUtils.isEmpty(code)) {
                    return code;
                }
            }
        } catch (Exception e) {
        }

        return "KR";
    }



    public static String loadAssetText(Context context, String assetFileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(assetFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getContentType(Context context, Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        Timber.d("getMimeType() - url=" + url + ", extension=" + extension);

        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (type == null) {
                return "image/" + extension;
            } else {
                return type;
            }
        } else {
            return "image/*";
        }
    }

    //    READ_CALENDAR
    //    WRITE_CALENDAR
    //    CAMERA
    //    READ_CONTACTS
    //    WRITE_CONTACTS
    //    GET_ACCOUNTS
    //    ACCESS_FINE_LOCATION
    //    ACCESS_COARSE_LOCATION
    //    RECORD_AUDIO
    //    READ_PHONE_STATE
    //    CALL_PHONE
    //    READ_CALL_LOG
    //    WRITE_CALL_LOG
    //    ADD_VOICEMAIL
    //    USE_SIP
    //    PROCESS_OUTGOING_CALLS
    //    BODY_SENSORS
    //    SEND_SMS
    //    RECEIVE_SMS
    //    READ_SMS
    //    RECEIVE_WAP_PUSH
    //    RECEIVE_MMS
    //    READ_EXTERNAL_STORAGE
    //    WRITE_EXTERNAL_STORAGE

    public static boolean hasPermissions(Context context, String[] neededPermission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (neededPermission == null || neededPermission.length < 1) {
            return true;
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : neededPermission) {
            int permissionStatus = ContextCompat.checkSelfPermission(context, permission);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                Timber.i("hasPermissions() - need " + permission);
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            Timber.i("hasPermissions() - send request size=" + listPermissionsNeeded.size());
            return false;
        }
        return true;
    }
}
