package com.tools.security.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.tools.security.R;
import com.tools.security.utils.AppUtils;

/**
 * Created by lzx on 2016/12/15.
 * email：386707112@qq.com
 * 功能：Sd卡扫描控件
 */

public class ScanSdcardView extends View {

    //控件默认宽高
    private int defaultHeight = AppUtils.dip2px(getContext(), 290);
    private int defaultWidth = AppUtils.dip2px(getContext(), 290);
    private int viewHeight, viewWidth;
    private Paint mProPaint; //圈的画笔
    private Paint mBigPaint, mSmallPain; //文字画笔
    private Paint mArcPaint; //弧形进度条画笔
    private Paint mBallPaint; //进度条上的小球画笔
    private Paint mRoundPaint; //里面圆形画笔
    private int mStrokeWidth = AppUtils.dip2px(getContext(), 6); //进度条宽度
    private float bigTextSize = AppUtils.dip2px(getContext(), 45); //百分数数字大小
    private float smallTextSize = AppUtils.dip2px(getContext(), 20); //百分号大小

    private String bigText = "0";
    private String smallText = "%";
    //进度条当前角度
    private float sweepAngle = 0;
    private int ballWidth = AppUtils.dip2px(getContext(), 9); //小球的宽度
    private int offset = 0; //小球加阴影效果宽度造成的偏移量

    //进度条所占用的角度
    private static final int arc_full_angle = 359;

    //进度条最大值和当前进度值
    private float max = 100, progress;

    //里面的进度条相关
    private Bitmap bgRoundPro; //背景进度条
    private Bitmap forRoundPro; //前景进度条
    private int roundProSize;
    private int roundProLeft, roundProTop;
    private PorterDuffXfermode mMode;
    private Paint mXferPaint;
    private RectF mOval;


    public ScanSdcardView(Context context) {
        this(context, null, 0);
    }

    public ScanSdcardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanSdcardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);  //关闭硬件加速

        mProPaint = new Paint();
        mProPaint.setAntiAlias(true);
        mProPaint.setStrokeWidth(mStrokeWidth);
        mProPaint.setStyle(Paint.Style.STROKE);
        mProPaint.setStrokeCap(Paint.Cap.ROUND);
        mProPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_30));

        mBigPaint = new TextPaint();
        mBigPaint.setAntiAlias(true);
        mBigPaint.setColor(Color.WHITE);
        mBigPaint.setTextSize(bigTextSize);

        mSmallPain = new TextPaint();
        mSmallPain.setAntiAlias(true);
        mSmallPain.setColor(Color.WHITE);
        mSmallPain.setTextSize(smallTextSize);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_90));
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeWidth(mStrokeWidth);

        mBallPaint = new Paint();
        mBallPaint.setAntiAlias(true);
        mBallPaint.setColor(Color.WHITE);
        mBallPaint.setStrokeWidth(mStrokeWidth);
        mBallPaint.setShadowLayer(30, 5, 2, Color.WHITE);

        mRoundPaint = new Paint();
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_30));

        //初始化进度条
        bgRoundPro = BitmapFactory.decodeResource(getResources(), R.drawable.scan_kedu_grey);
        forRoundPro = BitmapFactory.decodeResource(getResources(), R.drawable.scan_kedu_white);
        roundProSize = bgRoundPro.getWidth();
        mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mXferPaint = new Paint();
        mXferPaint.setAntiAlias(true);
        mXferPaint.setXfermode(mMode);
        mOval = new RectF();
        mOval.left = 0;
        mOval.top = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, defaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
        offset = (ballWidth + 37) / 2 + mStrokeWidth / 2;

        roundProLeft = viewWidth / 2 - roundProSize / 2;
        roundProTop = viewHeight / 2 - roundProSize / 2;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = viewWidth - paddingLeft - paddingRight;
        int height = viewHeight - paddingTop - paddingBottom;

        //画最外面的圆
        float radius = Math.max(width / 2, height / 2) - mStrokeWidth - offset / 2;
        canvas.drawCircle(width / 2, height / 2, radius, mProPaint);

        //画里面的圆
        float solidRoundRadius = (float) (Math.max(width / 2, height / 2) * 0.6 - mStrokeWidth / 2 - offset);
        canvas.drawCircle(width / 2, height / 2, solidRoundRadius, mRoundPaint);

        //画里面的进度条
        mXferPaint.setXfermode(null);
        canvas.drawBitmap(bgRoundPro, roundProLeft, roundProTop, mXferPaint);
        int saveCount = canvas.saveLayer(roundProLeft, roundProTop, roundProSize + roundProLeft, roundProSize + roundProTop, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        mOval.left = roundProLeft;
        mOval.top = roundProTop;
        mOval.right = roundProSize + roundProLeft;
        mOval.bottom = roundProSize + roundProTop;
        mXferPaint.setXfermode(null);
        canvas.drawArc(mOval, -90, 360 * progress / max, true, mXferPaint);
        mXferPaint.setXfermode(mMode);
        canvas.drawBitmap(forRoundPro, roundProLeft, roundProTop, mXferPaint);
        canvas.restoreToCount(saveCount);

        //画字
        canvas.translate(width / 2, height / 2);
        float textHeight = mBigPaint.ascent() + mBigPaint.descent();
        int smallTextWidth = (int) mSmallPain.measureText(smallText);
        int bigTextWidth = (int) mBigPaint.measureText(bigText);
        int realWidth = bigTextWidth + smallTextWidth;
        float bigTextX = -realWidth / 2;
        canvas.drawText(bigText, bigTextX, -textHeight / 2, mBigPaint);
        canvas.drawText(smallText, bigTextX + bigTextWidth, -textHeight / 2, mSmallPain);

        //画圆弧进度条
        @SuppressLint("DrawAllocation") RectF mOval = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(mOval, 270, sweepAngle, false, mArcPaint);

        //画进度条上的圆
        float progressRadians = (float) ((180 + sweepAngle) / 180 * Math.PI);
        float thumbX = -radius * (float) Math.sin(progressRadians);
        float thumbY = radius * (float) Math.cos(progressRadians);
        canvas.drawCircle(thumbX, thumbY, ballWidth, mBallPaint);
    }

    public void setData(String bigText, float sweepAngle) {
        this.bigText = bigText;
        this.sweepAngle = sweepAngle;
        this.progress = Integer.parseInt(bigText) + 1;
        postInvalidate();
    }

    public void onDestory() {
        if (bgRoundPro != null && !bgRoundPro.isRecycled()) {
            bgRoundPro.recycle();
            bgRoundPro = null;
        }
        if (forRoundPro != null && !forRoundPro.isRecycled()) {
            forRoundPro.recycle();
            forRoundPro = null;
        }
    }
}
