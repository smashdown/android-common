package com.smashdown.android.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.smashdown.android.common.event.HSEventNetworkConnected;
import com.smashdown.android.common.event.HSEventNetworkDisconnected;
import com.smashdown.android.common.util.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

public class NetworkReceiver extends BroadcastReceiver {
    private static final String PREFERENCE_NETWORK_CONNECTED = "PREF_NETWORK_CONNECTED";

    /**
     * To use this receiver, app need to set a network status on initial step of the app.
     */
    public static void setNetworkConnected(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        boolean currentConnected = NetworkUtil.isConnected(context);
        preference.edit().putBoolean(PREFERENCE_NETWORK_CONNECTED, currentConnected).apply();
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        boolean currentConnected = NetworkUtil.isConnected(context);

        if (!preference.contains(PREFERENCE_NETWORK_CONNECTED)) {
            preference.edit().putBoolean(PREFERENCE_NETWORK_CONNECTED, currentConnected).apply();
            if (currentConnected) {
                EventBus.getDefault().post(new HSEventNetworkConnected());
            } else {
                EventBus.getDefault().post(new HSEventNetworkDisconnected());
            }
        } else {
            boolean lastConnected = preference.getBoolean(PREFERENCE_NETWORK_CONNECTED, true);

            if (currentConnected != lastConnected) {
                preference.edit().putBoolean(PREFERENCE_NETWORK_CONNECTED, currentConnected).apply();

                if (currentConnected) {
                    EventBus.getDefault().post(new HSEventNetworkConnected());
                } else {
                    EventBus.getDefault().post(new HSEventNetworkDisconnected());
                }
            }
        }
    }
}