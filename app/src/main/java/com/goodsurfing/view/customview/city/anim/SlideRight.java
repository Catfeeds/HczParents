package com.goodsurfing.view.customview.city.anim;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.View;

/**
 * Created by lee on 2014/7/31.
 */
@SuppressLint("NewApi")
public class SlideRight extends BaseEffects{

    @Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "translationX",300,0).setDuration(mDuration),
                ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(mDuration*3/2)

        );
    }
}
