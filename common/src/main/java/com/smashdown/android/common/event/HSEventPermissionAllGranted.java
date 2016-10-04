package com.smashdown.android.common.event;

/**
 * Created by Jongyoung on 2016. 3. 26..
 */
public class HSEventPermissionAllGranted {
    String[] permissions;

    public HSEventPermissionAllGranted(String[] permissions) {
        this.permissions = permissions;
    }
}
