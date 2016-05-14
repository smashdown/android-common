package com.smashdown.android.common.wizard;

/**
 * Created by Jongyoung on 2016. 4. 2..
 */
public class HSEventWizardChild {
    // Fragment position in viewpager
    protected int mPos = 0;

    // Current fragment is ready, it's int because there some fragments that want distinguish current status progress
    protected int mIsReady = 0;

    public HSEventWizardChild(int pos, int isReady) {
        this.mPos = pos;
        this.mIsReady = isReady;
    }

    public int getPos() {
        return mPos;
    }

    public void setPos(int mPos) {
        this.mPos = mPos;
    }

    public int isIsReady() {
        return mIsReady;
    }

    public void setIsReady(int mIsReady) {
        this.mIsReady = mIsReady;
    }
}
