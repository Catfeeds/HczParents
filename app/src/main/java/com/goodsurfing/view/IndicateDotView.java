package com.goodsurfing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class IndicateDotView extends View {

    private Paint mPaint;
    private int width;
    public IndicateDotView(Context context) {
        this(context,null);
    }

    public IndicateDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndicateDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public IndicateDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint();
        //设置画笔颜色
        //设置画笔模式
        mPaint.setStyle(Paint.Style.FILL);
        //设置画笔宽度为30px
        mPaint.setStrokeWidth(5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

    }



    //根据xml的设定获取宽度

    private int measureWidth(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);

        int specSize = MeasureSpec.getSize(measureSpec);

        //wrap_content

        if (specMode == MeasureSpec.AT_MOST){

        }

        //fill_parent或者精确值

        else if (specMode == MeasureSpec.EXACTLY){

        }


        return specSize;

    }

    //根据xml的设定获取高度

    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);

        int specSize = MeasureSpec.getSize(measureSpec);

        //wrap_content

        if (specMode == MeasureSpec.AT_MOST){

        }
        //fill_parent或者精确值

        else if (specMode == MeasureSpec.EXACTLY){

        }


        return specSize;

    }

    public void setBackGroud(int color){
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width/2, width/2, width/2, mPaint);
    }
}
