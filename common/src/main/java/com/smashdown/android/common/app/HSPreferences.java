package com.smashdown.android.common.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class HSPreferences {
    // Network
    public static final String NETWOR_CONNECTED = "network_connected";

    // push enabled
    protected static final String PREFERENCE_PUSH_ENABLE    = "push_enabled";
    protected static final String PREFERENCE_SOUND_ENABLE   = "sound_enabled";
    protected static final String PREFERENCE_VIBRATE_ENABLE = "vibrate_enabled";

    protected SharedPreferences mSharedPreferences;

    public HSPreferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clear() {
        mSharedPreferences.edit().clear().commit();
    }

    public void dump() {
        for (String key : mSharedPreferences.getAll().keySet()) {
            Log.d("JJY", key + " = " + mSharedPreferences.getAll().get(key));
        }
    }

    /* Push */
    public boolean getPushEnable() {
        return mSharedPreferences.getBoolean(PREFERENCE_PUSH_ENABLE, true);
    }

    public void setPushEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_PUSH_ENABLE, flag).commit();
    }

    /* Sound */
    public boolean getSoundEnable() {
        return mSharedPreferences.getBoolean(PREFERENCE_SOUND_ENABLE, true);
    }

    public void setSoundEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_SOUND_ENABLE, flag).commit();
    }

    /* Vibrate */
    public boolean getVibrateEnable() {
        return mSharedPreferences.getBoolean(PREFERENCE_VIBRATE_ENABLE, false);
    }

    public void setVibrateEnable(boolean flag) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_VIBRATE_ENABLE, flag).commit();
    }

    // To check network connectivity changing. because NetworkBroadcastReceiver don't has states.
    public boolean isNetworkConnected() {
        return mSharedPreferences.getBoolean(NETWOR_CONNECTED, false);
    }

    public void setNetworkConnected(boolean value) {
        mSharedPreferences.edit().putBoolean(NETWOR_CONNECTED, value).apply();
    }
}
