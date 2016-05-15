package com.smashdown.android.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by smashdown on 2016. 5. 10..
 */
public class SquireButton extends Button {
    public SquireButton(Context context) {
        super(context);
    }

    public SquireButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquireButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
