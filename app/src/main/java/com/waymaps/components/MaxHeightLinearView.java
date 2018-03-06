package com.waymaps.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.waymaps.R;

/**
 * Created by Admin on 03.03.2018.
 */

public class MaxHeightLinearView extends LinearLayout {

    private int maxHeightPx;

    public MaxHeightLinearView(Context context) {
        super(context);
    }

    public MaxHeightLinearView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaxHeightLinearView, 0, 0);
        try {
            maxHeightPx = a.getInteger(R.styleable.MaxHeightLinearView_maxHeightPx, 0);
        } finally {
            a.recycle();
        }
    }

    public MaxHeightLinearView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeightDp(int maxHeightPx) {
        this.maxHeightPx = maxHeightPx;
        invalidate();
    }
}