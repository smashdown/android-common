package com.hechikasoft.common.demo;

import com.apkfuns.logutils.LogUtils;
import com.smashdown.android.common.app.HSApp;

/**
 * Created by smashdown on 2016. 6. 2..
 */
public class DemoApp extends HSApp {

    @Override
    public void onCreate() {
        super.onCreate();

        // Logging
        LogUtils.configAllowLog = true;
        LogUtils.configTagPrefix = "hsapp-demo";
    }
}
