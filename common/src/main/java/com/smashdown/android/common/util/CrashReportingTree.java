package com.smashdown.android.common.util;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree {
    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG      = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE  = "message";

    @Override
    protected void log(int priority, String tag, String message, Throwable throwable) {
        if (priority < Log.ERROR) {
            return;
        }

        Throwable t = throwable != null ? throwable : new Exception(message);

        // Firebase Crash Reporting
        FirebaseCrash.logcat(priority, tag, message);
        FirebaseCrash.report(t);
    }
}