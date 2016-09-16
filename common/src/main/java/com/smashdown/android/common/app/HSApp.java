package com.smashdown.android.common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.smashdown.android.common.event.HSEventAppGoToBackground;
import com.smashdown.android.common.event.HSEventAppGoToForeground;
import com.smashdown.android.common.util.Foreground;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public abstract class HSApp extends Application implements Application.ActivityLifecycleCallbacks {
    public static final String LOG_TAG = HSApp.class.getSimpleName();

    protected static Context mAppContext;

    private static boolean              isAppForeground = false;
    private static Map<String, Boolean> foregroundMap   = new HashMap<String, Boolean>();

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();

        // JodaTime
        JodaTimeAndroid.init(this);

        // Check app going to background
        Foreground.init(this);
        Foreground.get(this).addListener(mBackgroundListener);

        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        Foreground.get(this).removeListener(mBackgroundListener);

        super.onTerminate();
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    Foreground.Listener mBackgroundListener = new Foreground.Listener() {
        public void onBecameForeground() {
            LogUtils.i("App Foreground");
            EventBus.getDefault().post(new HSEventAppGoToForeground());
        }

        public void onBecameBackground() {
            LogUtils.i("App Background");
            EventBus.getDefault().post(new HSEventAppGoToBackground());
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(LOG_TAG, activity.getLocalClassName() + " WAS CREATED");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS STARTED");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS STOPPED");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS SAVED INSTANCE STATE");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS DESTROYED");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS PAUSED");

        isAppForeground = false;
        foregroundMap.put(activity.getClass().getName(), false);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(LOG_TAG, activity.getLocalClassName() + " WAS RESUMED");

        isAppForeground = true;
        foregroundMap.put(activity.getClass().getName(), true);
    }

    public static boolean isAppForeground() {
        return isAppForeground;
    }

    public static boolean isActivityForeground(String className) {
        if (foregroundMap.get(className) != null)
            return foregroundMap.get(className);
        else
            return false;
    }
}
