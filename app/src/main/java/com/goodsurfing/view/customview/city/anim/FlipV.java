package com.goodsurfing.view.customview.city.anim;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.View;

/**
 * Created by lee on 2014/7/31.
 */
@SuppressLint("NewApi")
public class FlipV extends BaseEffects{

	@SuppressLint("NewApi")
	@Override
    protected void setupAnimation(View view) {
        getAnimatorSet().playTogether(
                ObjectAnimator.ofFloat(view, "rotationX", -90, 0).setDuration(mDuration)

        );
    }
}
