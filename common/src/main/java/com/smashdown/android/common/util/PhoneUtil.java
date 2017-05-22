package com.smashdown.android.common.util;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneUtil {

    public static String convertToInternationalFormat(String countryCode, String phone) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phone)) {
                return phone;
            }
            //
            //        phone = phone.replace(" ", "");
            //        if (phone.startsWith("+"))
            //            return phone;

            Phonenumber.PhoneNumber phoneNum = phoneUtil.parse(phone, countryCode);

            // Produces "+41446681800"
            // return phoneUtil.format(phoneNum, PhoneNumberUtil.PhoneNumberFormat.E164);

            // Produces "+41 44 668 18 00"
            // System.out.println(phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));

            // Produces "044 668 18 00"
            // System.out.println(phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));

            return phoneUtil.format(phoneNum, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }

    public static String convertToLocalFormat(String countryCode, String phone) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phone)) {
                return phone;
            }

            Phonenumber.PhoneNumber phoneNum = phoneUtil.parse(phone, countryCode);
            return phoneUtil.format(phoneNum, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }

    public static String formatLocalPhoneNumber(String countryCode, String phone) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (TextUtils.isEmpty(countryCode) || TextUtils.isEmpty(phone)) {
                return phone;
            }

            Phonenumber.PhoneNumber phoneNum = phoneUtil.parse(phone, countryCode);
            return phoneUtil.format(phoneNum, PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replace(" ", "-");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }
}