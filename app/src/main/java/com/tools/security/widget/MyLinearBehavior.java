package com.tools.security.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/26.
 */

public class MyLinearBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private int width;

    public MyLinearBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        width = display.widthPixels;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        RecyclerView recyclerView = (RecyclerView) dependency;
        int scrollY = getScollYDistance(recyclerView);
        int childHeight = child.getHeight();

        float scale = 1 - Math.abs(scrollY) / childHeight;
        if (scale >= 0.6 && scale <= 1) {
            setScale(scale, child);
        }
        return true;
    }

    private void setScale(float scale, LinearLayout child) {
        CoordinatorLayout.MarginLayoutParams layoutParams = (CoordinatorLayout.MarginLayoutParams) child.getLayoutParams();
        int height = layoutParams.height;
        layoutParams.height = (int) (height * scale);
        child.setLayoutParams(layoutParams);
        for (int i = 0; i < child.getChildCount(); i++) {
            View view = child.getChildAt(i);
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    public int getScollYDistance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

}
