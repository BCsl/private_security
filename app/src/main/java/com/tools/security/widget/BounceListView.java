package com.tools.security.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * Created by wushuangshuang on 16/7/13.
 */
public class BounceListView extends ListView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;
    private Context mContext;
    private int mMaxYOverscrollDistance;

    public BounceListView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public BounceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public BounceListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX,
                                   int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//        LogUtils.e("xha","scrollY="+scrollY+",clampedY="+clampedY+",y="+DrawUtils.dip2px(MAX_Y_OVERSCROLL_DISTANCE));
        /*if (scrollY == -DrawUtils.dip2px(MAX_Y_OVERSCROLL_DISTANCE) && clampedY) {
            if (mPoint && isNeedRefresh) {
                mContext.sendBroadcast(new Intent(mType));
            }
            mPoint = false;
        } else {
            mPoint = true;
        }*/
    }
}
