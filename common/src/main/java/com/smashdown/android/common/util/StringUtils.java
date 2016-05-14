package com.smashdown.android.common.util;

import android.text.TextUtils;
import android.util.Patterns;

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

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (TextUtils.isEmpty(email))
            return false;

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNumericString(String str) {
        return TextUtils.isDigitsOnly(str);
    }
}
