package com.tools.security.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.tools.security.R;
import com.tools.security.utils.ScreenUtil;

/**
 * description: 首页渐变View
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class RadialGradientView extends View {

    private Paint mPaint = null;
    private Shader mRadialGradient = null;
    private Context context;


    public RadialGradientView(Context context) {
        super(context);
        init(context);
    }

    public RadialGradientView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadialGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mRadialGradient = new RadialGradient(getMeasuredWidth()/2, getMeasuredHeight()/0.4f, getMeasuredWidth()/2, R.color.transparent, R.color.transparent, Shader.TileMode.REPEAT);
        mPaint = new Paint();
    }


    /**
     * 重新绘制
     *
     * @param centerColor 中心色
     * @param edgeColor   边缘色
     */
    public void reDraw(int centerColor, int edgeColor) {
        try {
            mRadialGradient = new RadialGradient(getMeasuredWidth()/2, getMeasuredHeight()/0.4f, getMeasuredWidth()/2, centerColor, edgeColor, Shader.TileMode.REPEAT);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setShader(mRadialGradient);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()*0.4f,getMeasuredWidth()/2,mPaint);
    }

}
