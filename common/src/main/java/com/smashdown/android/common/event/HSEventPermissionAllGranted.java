package com.smashdown.android.common.event;

import java.util.List;

/**
 * Created by Jongyoung on 2016. 3. 26..
 */
public class HSEventPermissionAllGranted {
    public List<String> permissions;

    public HSEventPermissionAllGranted(List<String> permissions) {
        this.permissions = permissions;
    }
}
