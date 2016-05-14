package com.smashdown.android.common.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HSPreferences {
    private static HSPreferences instance;

    // Network
    public static final String NETWOR_CONNECTED = "network_connected";

    // push enabled
    protected static final String PREFERENCE_PUSH_ENABLE = "push_enabled";

    protected SharedPreferences mSharedPreferences;

    protected HSPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(HSApp.getAppContext());
    }

    public static HSPreferences getInstance() {
        if (instance == null) {
            instance = new HSPreferences();
        }
        return instance;
    }

    public void clear() {
        mSharedPreferences.edit().clear().commit();
    }

    /* Push */
    public boolean getPushEnable() {
        return mSharedPreferences.getBoolean(PREFERENCE_PUSH_ENABLE, true);
    }

    public void setPushEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_PUSH_ENABLE, flag).commit();
    }

    // To check network connectivity changing. because NetworkBroadcastReceiver don't has states.
    public boolean isNetworkConnected() {
        return mSharedPreferences.getBoolean(NETWOR_CONNECTED, false);
    }

    public void setNetworkConnected(boolean value) {
        mSharedPreferences.edit().putBoolean(NETWOR_CONNECTED, value).apply();
    }
}
