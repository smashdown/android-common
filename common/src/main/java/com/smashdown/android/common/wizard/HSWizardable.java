package com.smashdown.android.common.wizard;

/**
 * Created by Jongyoung on 2016. 4. 2..
 */
public interface HSWizardable {

    // returns true when all the input validation was succeed
    public boolean isReady(boolean skipWarnning);

    // save current values
    public boolean save();
}
