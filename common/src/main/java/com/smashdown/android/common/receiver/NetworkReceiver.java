package com.smashdown.android.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apkfuns.logutils.LogUtils;
import com.smashdown.android.common.app.HSPreferences;
import com.smashdown.android.common.event.HSEventNetworkConnected;
import com.smashdown.android.common.event.HSEventNetworkDisconnected;
import com.smashdown.android.common.util.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        HSPreferences pref = HSPreferences.getInstance();

        boolean lastConnected = pref.isNetworkConnected();
        boolean currentConnected = NetworkUtil.isConnected(context);
        LogUtils.d("NetworkChangeReceiver::onReceive()() - oldStatus=" + lastConnected + ", newStatus=" + currentConnected);

        if (currentConnected != lastConnected) {
            pref.setNetworkConnected(currentConnected);

            if (currentConnected) {
                EventBus.getDefault().post(new HSEventNetworkConnected());
            } else {
                EventBus.getDefault().post(new HSEventNetworkDisconnected());
            }
        } else {
            LogUtils.d("NetworkChangeReceiver() - same status, so ignore it");
        }
    }
}