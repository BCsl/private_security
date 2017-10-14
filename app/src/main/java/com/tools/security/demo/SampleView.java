package com.tools.security.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.tools.security.utils.ScreenUtil;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/28.
 */

public class SampleView extends View {
    private Path mPath;
    private Paint mPaint;
    private Rect mRect;
    private GradientDrawable mDrawable;

    public SampleView(Context context) {
        super(context);
        setFocusable(true);

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRect = new Rect(0, 0, ScreenUtil.getPhoneWidth(context), ScreenUtil.getPhoneHeight(context));

        mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[] { 0xffffce16,0x00000000
                         });
        mDrawable.setGradientCenter(0.5f,0.4f);
        mDrawable.setShape(GradientDrawable.RECTANGLE);
        mDrawable.setGradientRadius(ScreenUtil.getPhoneHeight(context)/2);
    }

//    static void setCornerRadii(GradientDrawable drawable, float r0,
//                               float r1, float r2, float r3) {
//        drawable.setCornerRadii(new float[] { r0, r0, r1, r1,
//                r2, r2, r3, r3 });
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        mDrawable.setBounds(mRect);

//        float r = 16;

//        canvas.save();
//        canvas.translate(10, 10);
//        mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//        setCornerRadii(mDrawable, r, r, 0, 0);
//        mDrawable.draw(canvas);
//        canvas.restore();

        canvas.save();
//        canvas.translate(10 + mRect.width() + 10, 10);
        mDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//        setCornerRadii(mDrawable, 0, 0, r, r);
        mDrawable.draw(canvas);
        canvas.restore();

//        canvas.translate(0, mRect.height() + 10);

//        canvas.save();
//        canvas.translate(10, 10);
//        mDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
//        setCornerRadii(mDrawable, 0, r, r, 0);
//        mDrawable.draw(canvas);
//        canvas.restore();
//
//        canvas.save();
//        canvas.translate(10 + mRect.width() + 10, 10);
//        mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//        setCornerRadii(mDrawable, r, 0, 0, r);
//        mDrawable.draw(canvas);
//        canvas.restore();
//
//        canvas.translate(0, mRect.height() + 10);
//
//        canvas.save();
//        canvas.translate(10, 10);
//        mDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//        setCornerRadii(mDrawable, r, 0, r, 0);
//        mDrawable.draw(canvas);
//        canvas.restore();
//
//        canvas.save();
//        canvas.translate(10 + mRect.width() + 10, 10);
//        mDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
//        setCornerRadii(mDrawable, 0, r, 0, r);
//        mDrawable.draw(canvas);
//        canvas.restore();

    }
}
