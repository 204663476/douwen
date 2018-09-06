package com.huagongzi.douwen.cardswipelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * @author yuqirong
 */

public class CardRecyclerView extends RecyclerView {

    float mDownPosX ;
    float mDownPosY ;
    private Canvas canvas;

    public CardRecyclerView(Context context) {
        super(context);
    }

    public CardRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas c) {
        this.canvas = c;
        super.onDraw(c);
    }

    public Canvas getCanvas() {
        return canvas;
    }


/*
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownPosX = x;
                mDownPosY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);
                final float deltaY = Math.abs(y - mDownPosY);
                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (deltaX < deltaY) {
                    Toast.makeText(getContext(),"拦截",Toast.LENGTH_SHORT).show();
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
*/
}
