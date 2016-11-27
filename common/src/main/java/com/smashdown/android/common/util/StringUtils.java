package com.smashdown.android.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.smashdown.android.common.R;

import java.text.Format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jongyoung on 2016. 1. 19..
 */
public class StringUtils {
    public static boolean hasUppercase(String str) {
        return !str.equals(str.toLowerCase());
    }

    public static boolean hasLowercase(String str) {
        return !str.equals(str.toUpperCase());
    }

    public static boolean hasNumeric(String str) {
        return str.matches(".*\\d.*");
    }

    public static boolean isNumeric(String str) {
        return TextUtils.isDigitsOnly(str);
    }

    public static boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }


    public static boolean isValidEmail(String inputStr) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputStr);
        if (!m.matches()) {
            return false;
        }
        return true;
    }


    public static boolean isNumericString(String str) {
        return TextUtils.isDigitsOnly(str);
    }

    public static String makeHiddenUserName(String email) {
        String id = email.split("@")[0];
        int halfPos = id.length() / 3;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < id.length(); i++) {
            if (i < halfPos)
                builder.append(id.charAt(i));
            else
                builder.append("*");
        }
        return builder.toString();
    }

    // replace newlines with <br>
    public static String replaceNewlinesWithBreaks(String source) {
        return source != null ? source.replaceAll("(?:\n|\r\n)","<br>") : "";
    }
    // replace newlines with <br>
    public static String replaceNewlinesWithBreaks(Context context, int stringId) {
        return context.getString(stringId) != null ? context.getString(stringId).replaceAll("(?:\n|\r\n)","<br>") : "";
    }

    private static       Format format = null;
    private static final int    WEEK   = 1000 * 60 * 60 * 24 * 7;
    private static final int    DAY    = 1000 * 60 * 60 * 24;
    private static final int    HOUR   = 1000 * 60 * 60;
    private static final int    MINUTE = 1000 * 60;

    public static String makeDateString(Context context, long date) {
        long now = System.currentTimeMillis();
        long elapse = now - date;

        if (format == null) {
            format = android.text.format.DateFormat.getDateFormat(context);
        }
        if (elapse > WEEK) {
            return format.format(date);
        } else if (elapse > DAY) {
            return String.format("%d %s", elapse / DAY, context.getString(R.string.hs_days_ago));
        } else if (elapse > HOUR) {
            return String.format("%d %s", elapse / HOUR, context.getString(R.string.hs_hours_ago));
        } else {
            return String.format("%d %s", elapse / MINUTE, context.getString(R.string.hs_minutes_ago));
        }
    }
}
