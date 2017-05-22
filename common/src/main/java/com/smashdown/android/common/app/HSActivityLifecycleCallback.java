package com.smashdown.android.common.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Created by smashdown on 22/05/2017.
 */

public class HSActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static HSActivityLifecycleCallback instance;

    private Activity foregroundActivity;

    private HSActivityLifecycleCallback() {
    }

    public static HSActivityLifecycleCallback getInstance() {
        if (instance == null)
            instance = new HSActivityLifecycleCallback();

        return instance;
    }

    public Activity getForegroundActivity() {
        return foregroundActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.d(activity.getLocalClassName() + " WAS CREATED");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.d(activity.getLocalClassName() + " WAS STARTED");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.d(activity.getLocalClassName() + " WAS STOPPED");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.d(activity.getLocalClassName() + " WAS SAVED INSTANCE STATE");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.d(activity.getLocalClassName() + " WAS DESTROYED");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.d(activity.getLocalClassName() + " WAS PAUSED");

        foregroundActivity = null;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.d(activity.getLocalClassName() + " WAS RESUMED");

        foregroundActivity = activity;
    }
}
