package com.tools.security.main;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.orhanobut.logger.Logger;
import com.tools.security.R;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.AppConstants;
import com.tools.security.main.thread.EncourageThreadExecutorProxy;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.DrawUtils;

import java.util.List;


/**
 * Created by wushuangshuang on 16/8/16.
 */
public class LuckAnimation {
    protected static final int MSG_CREATE_LOADING = 0;
    private static final int CREATE_LOADING_INTERVAL = 150;
    public static final float ANIMATION_DURATION = 3f; // 3秒
    public static final int ANIMATION_SHOWING_DURATION = 10;


    private Context mContext;
    private ViewGroup mRootView;
    private Drawable[] mLuckLoads;
    /**
     * 动画停止的标志位
     */
    private volatile boolean mStopFlag;

    private int mScreenWidth;
    private int mScreenHeight;

    private float mMinStartX;
    private float mMaxStartX;
    private float mMinStartY;
    private float mMaxStartY;
    private float mMinVx;
    private float mMaxVx;
    private float mMinVy;
    private float mMaxVy;
    private float mMinAccelerator;
    private float mMaxAccelerator;
    private LuckAnimationListener mLuckListener;
    private int mRefreshW;
    private int mRefreshH;
    private Paint mPaint;

    private List<Object> adData = null;
    private int index = 0;
    private FacebookAdConfig config;

    private boolean isRequestSuccess = false;

    public LuckAnimation(Context context, ViewGroup viewRoot) {
        mContext = context;
        mRootView = viewRoot;
        initLuckLoads();
        initData();
        config = new FacebookAdConfig();
        config.setRequestNativeAdCount(1);
    }

    private void initLuckLoads() {
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_luck_loading_img);
        mLuckLoads = new Drawable[4];
        mLuckLoads[0] = DrawUtils.zoomDrawable(mContext, drawable,
                DrawUtils.dip2px(mContext, 54), DrawUtils.dip2px(mContext, 117));
        mLuckLoads[0].setColorFilter(0xffff007e, PorterDuff.Mode.SRC_IN); // 滤镜效果
        mLuckLoads[1] = DrawUtils.zoomDrawable(mContext, drawable, DrawUtils.dip2px(mContext, 64),
                DrawUtils.dip2px(mContext, 127));
        mLuckLoads[1].setColorFilter(0xff00e4ff, PorterDuff.Mode.SRC_IN);
        mLuckLoads[2] = DrawUtils.zoomDrawable(mContext, drawable, DrawUtils.dip2px(mContext, 72),
                DrawUtils.dip2px(mContext, 143));
        mLuckLoads[2].setColorFilter(0xff00ff9c, PorterDuff.Mode.SRC_IN);
        mLuckLoads[3] = DrawUtils.zoomDrawable(mContext, drawable, DrawUtils.dip2px(mContext, 68),
                DrawUtils.dip2px(mContext, 135));
        mLuckLoads[3].setColorFilter(0xfffff000, PorterDuff.Mode.SRC_IN);

        mRefreshW = mLuckLoads[3].getIntrinsicWidth();
        mRefreshH = mLuckLoads[3].getIntrinsicHeight();
    }

    /**
     * 功能简述: 初始化数据范围
     */
    private void initData() {
        mScreenWidth = DrawUtils.getRealWidth(mContext);
        mScreenHeight = DrawUtils.getRealHeight(mContext);

        mMinStartX = DrawUtils.dip2px(mContext, 98);
        mMaxStartX = mMinStartX;

        mMinStartY = mScreenHeight / 4f;
        mMaxStartY = mScreenHeight * 0.8f;

        mMinVx = (mScreenWidth + mMaxStartX) / ANIMATION_DURATION;
        mMaxVx = mScreenWidth * 1.2f;

        mMinVy = 0;
        mMaxVy = mScreenHeight / 3f;

        mMinAccelerator = mScreenHeight / 2;
        mMaxAccelerator = mScreenHeight / 1;
    }

    private View makeLuckLoading() {
        ImageView view = new ImageView(mContext);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(mLuckLoads[(int) getRandomValue(0, 3)]);
        } else {
            view.setBackgroundDrawable(mLuckLoads[(int) getRandomValue(0, 3)]);
        }

        return view;
    }

    public ImageView createRefreshLoading() {
        Drawable drawable = mLuckLoads[3];
        ImageView balloon = new ImageView(mContext);
        balloon.setImageBitmap(createBitmap(drawable,
                mContext.getResources().getDrawable(R.drawable.ic_luck_star)));
        return balloon;
    }

    public Bitmap createBitmap(Drawable balloon, Drawable star) {
        Bitmap dst = Bitmap.createBitmap(balloon.getIntrinsicWidth(), balloon.getIntrinsicHeight()
                + star.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
        }
        int offset = -DrawUtils.dip2px(mContext, 1);
        canvas.drawBitmap(((BitmapDrawable) balloon).getBitmap(), 0, 0, mPaint);
        canvas.drawBitmap(((BitmapDrawable) star).getBitmap(), offset, balloon.getIntrinsicHeight()
                + offset, mPaint);
        return dst;
    }

    private float getRandomValue(float min, float max) {
        return min + (float) (Math.random() * (max - min + 1));
    }

    /**
     * 启动动画
     */
    public void startAnimation() {
        index = 0;
        new Thread(mCreateBalloonRunnable).start();

    }

    public void setLuckAnimationListener(LuckAnimationListener listener) {
        mLuckListener = listener;
    }

    /**
     * 构建动画
     */
    private void compagesAnimation() {
        // 取反,从屏幕左侧进入
        float startX = -getRandomValue(mMinStartX, mMaxStartX);
        float startY = getRandomValue(mMinStartY, mMaxStartY);
        float vx = getRandomValue(mMinVx, mMaxVx);
        float vy = getRandomValue(mMinVy, mMaxVy);
        // 取反,加速度向上
        float accerator = -getRandomValue(mMinAccelerator, mMaxAccelerator);
        LuckLoadingInfo luckLoadingInfo = new LuckLoadingInfo(startX, startY, vx, vy, accerator);
        View view = makeLuckLoading();
        start(view, luckLoadingInfo);
    }

    private void start(final View view, final LuckLoadingInfo luckLoadingInfo) {
        if (mRootView != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = -mScreenWidth / 2;
            mRootView.addView(view, layoutParams);
        }

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration((long) (ANIMATION_DURATION * 1000));
        valueAnimator.setObjectValues(new PointF(luckLoadingInfo.getmStartX(), luckLoadingInfo.getmStartY()));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                PointF pointF = new PointF();
                float time = fraction * ANIMATION_DURATION;
                pointF.x = luckLoadingInfo.getmStartX() + luckLoadingInfo.getmVx() * time;
                pointF.y = luckLoadingInfo.getmStartY() + luckLoadingInfo.getmVy() * time
                        + 0.5f * luckLoadingInfo.getmAccelerator() * time * time;
                return pointF;
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                view.setX(pointF.x);
                view.setY(pointF.y);
            }
        });
    }

    /**
     * 用于生成气球
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CREATE_LOADING) {
                compagesAnimation();
                if (index == 9) {
                    loadData();
                }
                index++;
            }
        }
    };

    /**
     * 每隔0.5秒生成一个气球
     */
    public Runnable mCreateBalloonRunnable = new Runnable() {
        @Override
        public void run() {
            int count = ANIMATION_SHOWING_DURATION * 1000 / CREATE_LOADING_INTERVAL;
            for (int i = 0; i < count && !mStopFlag; i++) {
                try {
                    Thread.sleep(CREATE_LOADING_INTERVAL);
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_CREATE_LOADING;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                }
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 如果mStopFlag为false, 说明广告加载不成功
                    if (!mStopFlag) {
                        if (mLuckListener != null) {
                            mLuckListener.onLuckAdLoadError();
                        }
                    }
                }
            }, (long) (ANIMATION_DURATION * 1000 / 2));
        }
    };

    private void loadData() {
        if (AdStaticConstant.luckyAds != null && AdStaticConstant.luckyAdSaveTime > System.currentTimeMillis() - 60 * 60 * 1000l) {
            mStopFlag = true;
            if (mLuckListener != null) {
                mLuckListener.onLuckAdLoadSuccess(AdStaticConstant.luckyAds);
            }
        } else {
            AdStaticConstant.luckyAds = null;
            AdStaticConstant.luckyAdSaveTime = 0;
            BatAdBuild.Builder build = new BatAdBuild.Builder(mContext, AppConstants.BATMOBI_TRY_PLACEMENT_ID, BatAdType.NATIVE.getType(), new IAdListener() {
                @Override
                public void onAdLoadFinish(List<Object> list) {
                    AppUtils.isFacebookAd(list);
                    if (!isRequestSuccess) {
                        isRequestSuccess = true;
                        adData = list;
                        if (adData != null && adData.size() > 0) {
                            mStopFlag = true;
                            AdStaticConstant.luckyAds = list;
                            AdStaticConstant.luckyAdSaveTime = System.currentTimeMillis();
                            if (mLuckListener != null) {
                                mLuckListener.onLuckAdLoadSuccess(AdStaticConstant.luckyAds);
                            }
                        } else {
                            mStopFlag = false;
                        }
                    }
                }

                @Override
                public void onAdError(AdError adError) {

                }

                @Override
                public void onAdShowed() {

                }

                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdClicked() {
                    if (mLuckListener != null) {
                        mLuckListener.onDialogDismiss();
                    }
                }
            }).setAdsNum(1).setFacebookConfig(config);
            BatmobiLib.load(build.build());
        }
    }

    public interface LuckAnimationListener {
        void onLuckAdLoadError();

        void onLuckAdLoadSuccess(List<Object> objects);

        void onDialogDismiss();
    }

    /**
     * 设置动画停止标志位
     *
     * @param flag
     */
    public void setStopFlag(boolean flag) {
        mStopFlag = flag;
    }

    public boolean getStopFlag() {
        return mStopFlag;
    }

    public int getRefreshWidth() {
        return mRefreshW;
    }

    public int getRefreshHeight() {
        return mRefreshH;
    }

}
