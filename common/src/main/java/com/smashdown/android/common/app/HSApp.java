package com.smashdown.android.common.app;

import android.app.Application;
import android.content.Context;

import com.apkfuns.logutils.LogUtils;
import com.smashdown.android.common.R;
import com.smashdown.android.common.event.HSEventAppGoToBackground;
import com.smashdown.android.common.event.HSEventAppGoToForeground;
import com.smashdown.android.common.util.Foreground;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public abstract class HSApp extends Application {
    public static final String LOG_TAG = HSApp.class.getSimpleName();

    protected static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();

        // JodaTime
        JodaTimeAndroid.init(this);

        // Check app going to background
        Foreground.init(this);
        Foreground.get(this).addListener(mBackgroundListener);

        // Setting font library
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoTTF/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
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
}
