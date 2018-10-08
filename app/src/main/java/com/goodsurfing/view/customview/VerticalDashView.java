package com.goodsurfing.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class VerticalDashView extends View {
    private Paint mDashPaint;
    private Rect mRect;
    public VerticalDashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    @SuppressLint({ "InlinedApi", "NewApi" })
    private void init() {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
        float dashGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        float dashWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        mDashPaint = new Paint();
        mDashPaint.setColor(0xffececec);
        mDashPaint.setStyle(Style.STROKE);
        mDashPaint.setStrokeWidth(width);
        mDashPaint.setAntiAlias(true);
        //DashPathEffect是Android提供的虚线样式API，具体的使用可以参考下面的介绍
        mDashPaint.setPathEffect(new DashPathEffect(new float[] { dashWidth, dashGap }, 0));
        mRect = new Rect();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //取出线条的位置（位置的定义放在XML的layout中，具体如下xml文件所示）
        mRect.left = left;
        mRect.top = top;
        mRect.right = right;
        mRect.bottom = bottom;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        float x0 = (mRect.right - mRect.left) / 2f;
        float y0 = 0;
        float x1 = x0;
        float y1 = y0 + mRect.bottom - mRect.top;
        canvas.drawLine(x0, y0, x1, y1, mDashPaint);
    }
}
