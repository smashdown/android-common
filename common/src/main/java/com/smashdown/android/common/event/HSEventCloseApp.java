package com.smashdown.android.common.event;

/**
 * Created by Jongyoung on 2016. 3. 26..
 */
public class HSEventCloseApp {
    // to finish all the activities except me.
    private String mCallerActivityName;

    public HSEventCloseApp(String caller) {
        mCallerActivityName = caller;
    }
}
