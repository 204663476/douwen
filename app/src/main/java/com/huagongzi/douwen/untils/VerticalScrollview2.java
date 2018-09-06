package com.huagongzi.douwen.untils;

/**
 * Created by 许鑫源 on 2018/8/6.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.huagongzi.douwen.MainActivity;
import com.huagongzi.douwen.cardswipelayout.CardConfig;

/**
 * Created by Chao  2018/8/6 on 14:55
 * description
 */

public class VerticalScrollview2 extends ScrollView {


    private int mTouchSlop;
    private float currentX = 0;
    private float currentY = 0;


    public VerticalScrollview2(Context context) {
        this(context, null);
    }

    public VerticalScrollview2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScrollview2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }

    int huadong = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = ev.getX();
                currentY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = ev.getX() - currentX;
                float moveY = ev.getY() - currentY;
                if (Math.abs(moveX) > Math.abs(moveY)) {
                    //Log.e("TAG", "手指横向滑动，事件不处理");

                    return false;
                }
            case MotionEvent.ACTION_UP:
                if (ev.getX() - currentX < 0){
                    huadong = 1;
                }else {
                    huadong = 2;
                }
                if (huadong == 1&&Math.abs(ev.getX() - currentX) > 100){
                    MainActivity.cardCallback.handleCardSwipe(CardConfig.SWIPING_LEFT, 300L);
                }else if (huadong == 2&&Math.abs(ev.getX() - currentX) > 100){
                    MainActivity.cardCallback.handleCardSwipe(CardConfig.SWIPING_RIGHT, 300L);

                }
                break;
        }
        return super.onTouchEvent(ev);
    }



    private float getThreshold(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return recyclerView.getWidth() * getSwipeThreshold(viewHolder);
    }

    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        //滑动的比例达到多少之后, 视为滑动
        return 0.3f;
    }
}