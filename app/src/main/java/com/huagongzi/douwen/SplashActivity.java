package com.huagongzi.douwen;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends Activity {


    ImageView mSplashImage;

    private static final int ANIMATION_TIME = 2000;

    private static final float SCALE_END = 1.02F;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashImage = (ImageView)findViewById(R.id.iv_splash);
        Random random = new Random(SystemClock.elapsedRealtime());
        mSplashImage.setImageResource(R.drawable.splash);
        startAnim();
    }

    private void startAnim() {

        ObjectAnimator animatorX=ObjectAnimator.ofFloat(mSplashImage,"scaleX",1f,SCALE_END);
        ObjectAnimator animatorY=ObjectAnimator.ofFloat(mSplashImage,"scaleY",1f,SCALE_END);
        AnimatorSet set=new AnimatorSet();
        set.setDuration(ANIMATION_TIME).play(animatorX).with(animatorY);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                SplashActivity.this.finish();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
