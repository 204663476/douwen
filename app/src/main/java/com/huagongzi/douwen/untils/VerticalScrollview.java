package com.huagongzi.douwen.untils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.huagongzi.douwen.MainActivity;
import com.huagongzi.douwen.cardswipelayout.CardItemTouchHelperCallback;

/**
 * Created by Chao  2018/8/6 on 14:55
 * description
 */

public class VerticalScrollview extends ScrollView {


    private int mTouchSlop;
    private float currentX = 0;
    private float currentY = 0;


    public VerticalScrollview(Context context) {
        this(context, null);
    }

    public VerticalScrollview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
    }


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
        }
        return super.onTouchEvent(ev);
    }
}