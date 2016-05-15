package com.smashdown.android.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by smashdown on 2016. 5. 10..
 */
public class SquireRelativeLayout extends RelativeLayout {
    public SquireRelativeLayout(Context context) {
        super(context);
    }

    public SquireRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquireRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
