package com.goodsurfing.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TrajectoryRecyclerView extends RecyclerView {
    public TrajectoryRecyclerView(Context context) {
        super(context);
    }

    public TrajectoryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TrajectoryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(getChildAt(0).isShown()){
            return false;
        }

        return super.onInterceptTouchEvent(e);
    }
}
