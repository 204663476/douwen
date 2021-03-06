package com.huagongzi.douwen.cardswipelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.Interpolator;

import com.huagongzi.douwen.MainActivity;

import java.util.List;

import static com.huagongzi.douwen.cardswipelayout.Utils.checkIsNull;

/**
 * @author yuqirong
 */

public class CardItemTouchHelperCallback<Mydata> extends ItemTouchHelper.Callback {


    private final RecyclerView.Adapter adapter;
    private List<Mydata> dataList;
    private OnSwipeListener<Mydata> mListener;
    private CardRecyclerView recyclerView;
    ValueAnimator swipeAnimator;

    public CardItemTouchHelperCallback(@NonNull CardRecyclerView recyclerView,
                                       @NonNull RecyclerView.Adapter adapter, @NonNull List<Mydata> dataList) {
        this(recyclerView, adapter, dataList, null);
    }

    public CardItemTouchHelperCallback(@NonNull CardRecyclerView recyclerView, @NonNull RecyclerView.Adapter adapter,
                                       @NonNull List<Mydata> dataList, OnSwipeListener<Mydata> listener) {
        this.recyclerView = checkIsNull(recyclerView, "recyclerView == null");
        this.adapter = checkIsNull(adapter, "adapter == null");
        this.dataList = checkIsNull(dataList, "dataList == null");
        this.mListener = listener;
    }

    public void setOnSwipedListener(MainActivity mListener) {
        this.mListener = (OnSwipeListener<Mydata>) mListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof CardLayoutManager) {
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // 移除 onTouchListener,否则触摸滑动会乱了
        viewHolder.itemView.setOnTouchListener(null);
        int layoutPosition = viewHolder.getLayoutPosition();
        Mydata remove = dataList.remove(layoutPosition);
        adapter.notifyDataSetChanged();
        if (mListener != null) {
            mListener.onSwiped(viewHolder, remove, direction == ItemTouchHelper.LEFT ? CardConfig.SWIPED_LEFT : CardConfig.SWIPED_RIGHT);
        }
        // 当没有数据时回调 mListener
        if (adapter.getItemCount() == 0) {
            if (mListener != null) {
                mListener.onSwipedClear();
            }
        }
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float ratio = dX / getThreshold(recyclerView, viewHolder);
            // ratio 最大为 1 或 -1
            if (ratio > 1) {
                ratio = 1;
            } else if (ratio < -1) {
                ratio = -1;
            }
            itemView.setRotation(ratio * CardConfig.DEFAULT_ROTATE_DEGREE);
            int childCount = recyclerView.getChildCount();
            // 当数据源个数大于最大显示数时 position = 1
            // 当数据源个数小于或等于最大显示数时 position = 0
            for (int position = childCount > CardConfig.DEFAULT_SHOW_ITEM ? 1 : 0; position < childCount - 1; position++) {
                int index = childCount - position - 1;
                View view = recyclerView.getChildAt(position);
                view.setScaleX(1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE);
                view.setScaleY(1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE);
                view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / CardConfig.DEFAULT_TRANSLATE_Y);
            }
            if (mListener != null) {
                if (ratio != 0) {
                    mListener.onSwiping(viewHolder, ratio, ratio < 0 ? CardConfig.SWIPING_LEFT : CardConfig.SWIPING_RIGHT);
                } else {
                    mListener.onSwiping(viewHolder, ratio, CardConfig.SWIPING_NONE);
                }
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0f);
    }

    private float getThreshold(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return recyclerView.getWidth() * getSwipeThreshold(viewHolder);
    }

    public void handleCardSwipe(int flag, long duration) {
        handleCardSwipe(flag, duration, null);
    }

    public void handleCardSwipe(final int flag, long duration, Interpolator interpolator) {
        if (swipeAnimator != null && swipeAnimator.isStarted()) {
            return;
        }
        final CardRecyclerView recyclerView = checkIsNull(this.recyclerView, "recyclerView  == null");
        final Canvas canvas = checkIsNull(this.recyclerView.getCanvas(), "canvas == null");
        final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(0);
        if (viewHolder == null) {
            return;
        }
        if (flag == CardConfig.SWIPING_LEFT) {
            swipeAnimator = ValueAnimator.ofFloat(0, -recyclerView.getWidth() / 2);
        } else if (flag == CardConfig.SWIPING_RIGHT) {
            swipeAnimator = ValueAnimator.ofFloat(0, recyclerView.getWidth() / 2);
        } else {
            throw new IllegalStateException("flag must be one of SWIPING_LEFT or SWIPING_RIGHT");
        }
        swipeAnimator.setDuration(duration);
        swipeAnimator.setInterpolator(interpolator);
        swipeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();
                onChildDraw(canvas, recyclerView, viewHolder, value, 0, ItemTouchHelper.ACTION_STATE_SWIPE, true);
            }
        });
        swipeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onSwiped(viewHolder, flag == CardConfig.SWIPING_LEFT ? ItemTouchHelper.LEFT : ItemTouchHelper.RIGHT);
                clearView(recyclerView, viewHolder);
            }
        });
        swipeAnimator.start();
    }

}
