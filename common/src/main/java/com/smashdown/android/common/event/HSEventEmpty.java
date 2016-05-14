package com.smashdown.android.common.event;

/**
 * Created by Jongyoung on 2016. 3. 26..
 */
public class HSEventEmpty {
    // to finish all the activities except me.
    private String mCallerActivityName;

    public HSEventEmpty(String caller) {
        mCallerActivityName = caller;
    }
}
