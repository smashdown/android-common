package com.smashdown.android.common.event;

/**
 * Created by Jongyoung on 2016. 3. 26..
 */
public class HSEventPermissionDenied {
    // to finish all the activities except me.
    public String permission;

    public HSEventPermissionDenied(String permission) {
        this.permission = permission;
    }
}
