package com.tools.security.wifi.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.wifi.view.WifiReleasingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/23.
 */

public class WifiReleasingLayout extends FrameLayout implements View.OnClickListener {

    private WindowManager wManager;
    private WindowManager.LayoutParams wmParams;
    private View addView;

    private Activity context;

    private RelativeLayout mAnimLayout;
    private LinearLayout mBottomLayout;
    private ImageView mImgWifiPro, mImgLightning, mImgAppIcon, mImgCross;
    private View mSquare1, mSquare2, mSquare3, mSquare4, mSquare5, mSquare6, mRingBig, mRingSmall;
    private TextView countText;
    private TextView cancelText;
    private List<View> mSquareList = new ArrayList<>();

    private ObjectAnimator mRoundRotateAnim, mRoundBigAnim, mRoundSmallAnim, scaleLightningAnim, mAppIconAnim,
            mRingBigAnim, mRingSmallAnim, mImgCrossAnim, mLayoutAnim;

    private AnimatorSet mSquareSet, mSmallSet;

    private LinearInterpolator mLinearInterpolator;

    private List<Drawable> mAppIconList;

    private int indexReplayCount = 0;
    private int indexAppIcon = 0;
    private int rotationTime = 500;
    private final static int WHAT_START_ANIM = 0; //开始动画
    private final static int WHAT_START_APPICON_ANIM = 1; //开始图标动画
    private final static int WHAT_START_SMALL_ANIM = 3; //开始缩小动画

    private AbsoluteSizeSpan absoluteSizeSpan;
    private SpannableStringBuilder mStringBuilder;

    private IOnFinishCallback finishCallback;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_START_ANIM:
                    startRotationAnim();
                    break;
                case WHAT_START_APPICON_ANIM:
                    startAppIconAnim();
                    break;
                case WHAT_START_SMALL_ANIM:
                    mRingBigAnim = null;
                    mRingSmallAnim = null;
                    mImgCrossAnim = null;
                    mImgCross.setVisibility(View.GONE);
                    mRingBig.setVisibility(View.GONE);
                    mRingSmall.setVisibility(View.GONE);
                    mBottomLayout.setVisibility(View.GONE);
                    startScaleSmallAnim();
                    break;
            }
        }
    };

    public WifiReleasingLayout(Activity context, List<Drawable> drawables, IOnFinishCallback iOnFinishCallback) {
        super(context);
        this.context = context;
        this.mAppIconList = drawables;
        this.finishCallback = iOnFinishCallback;
        initView();
        getWindowManager(context);
    }

    public void startAnim() {
        mHandler.sendEmptyMessage(WHAT_START_ANIM);
        wManager.addView(this, wmParams);
    }

    private void getWindowManager(final Context context) {
        wManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.TRANSPARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        wmParams.gravity = 17;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    private void initView() {

        addView = LayoutInflater.from(context).inflate(R.layout.layout_wifi_releasing,
                this);

        mLinearInterpolator = new LinearInterpolator();

        mBottomLayout = (LinearLayout) addView.findViewById(R.id.bottom_layout);
        countText = (TextView) addView.findViewById(R.id.text_release_count);
        mAnimLayout = (RelativeLayout) addView.findViewById(R.id.anim_layout);
        mImgWifiPro = (ImageView) addView.findViewById(R.id.wifi_pro);
        mImgLightning = (ImageView) addView.findViewById(R.id.img_lightning);
        mImgAppIcon = (ImageView) addView.findViewById(R.id.ic_app_icon);
        mImgCross = (ImageView) addView.findViewById(R.id.img_cross);
        cancelText = (TextView) addView.findViewById(R.id.text_cancel);


        mSquare1 = addView.findViewById(R.id.square1);
        mSquare2 = addView.findViewById(R.id.square2);
        mSquare3 = addView.findViewById(R.id.square3);
        mSquare4 = addView.findViewById(R.id.square4);
        mSquare5 = addView.findViewById(R.id.square5);
        mSquare6 = addView.findViewById(R.id.square6);
        mRingBig = addView.findViewById(R.id.ring_big);
        mRingSmall = addView.findViewById(R.id.ring_small);
        mSquareList.add(mSquare1);
        mSquareList.add(mSquare2);
        mSquareList.add(mSquare3);
        mSquareList.add(mSquare4);
        mSquareList.add(mSquare5);
        mSquareList.add(mSquare6);

        mStringBuilder = new SpannableStringBuilder();
        absoluteSizeSpan = new AbsoluteSizeSpan(AppUtils.dip2px(context, 40));

        String numberString = indexAppIcon + "/" + mAppIconList.size();
        updateNumber(numberString);

        cancelText.setOnClickListener(this);
    }

    /**
     * @param numberString
     */
    private void updateNumber(String numberString) {
        mStringBuilder.clear();
        mStringBuilder.append(numberString);

        mStringBuilder.setSpan(absoluteSizeSpan, 0, numberString.indexOf("/") + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        countText.setText(mStringBuilder);
    }


    /**
     * 开始转圈动画
     */
    private void startRotationAnim() {
        //圈圈旋转
        if (mRoundRotateAnim == null) {
            mRoundRotateAnim = ObjectAnimator.ofFloat(mImgWifiPro, "rotation", 359);
            mRoundRotateAnim.setInterpolator(mLinearInterpolator);
            mRoundRotateAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    indexReplayCount++;
                    if (indexReplayCount == 2) {
                        rotationTime = 300;
                        startLightningAnim();
                    }
                    mHandler.sendEmptyMessage(WHAT_START_ANIM);
                }
            });
        }
        mRoundRotateAnim.setDuration(rotationTime);
        mRoundRotateAnim.start();

    }

    /**
     * 开始闪电动画
     */
    private void startLightningAnim() {
        //闪电
        PropertyValuesHolder pvhScaleXLightning = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f);
        PropertyValuesHolder pvhScaleYLightning = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
        scaleLightningAnim = ObjectAnimator.ofPropertyValuesHolder(mImgLightning, pvhScaleXLightning, pvhScaleYLightning);
        scaleLightningAnim.setDuration(800);
        scaleLightningAnim.start();
        //3. 闪电完后变大
        scaleLightningAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startScaleBigAnim();
            }
        });
    }

    /**
     * 开始圈圈变大动画
     */
    private void startScaleBigAnim() {
        //闪电动画释放
        scaleLightningAnim = null;

        if (mRoundBigAnim == null) {
            //圈圈变大
            PropertyValuesHolder pvhScaleXRoundBig = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f);
            PropertyValuesHolder pvhScaleYRoundBig = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f);
            mRoundBigAnim = ObjectAnimator.ofPropertyValuesHolder(mImgWifiPro, pvhScaleXRoundBig, pvhScaleYRoundBig);
            mRoundBigAnim.setDuration(700);
            mRoundBigAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mImgAppIcon.setVisibility(View.VISIBLE);
                    if (mAppIconList.size() == 0) {
                        mBottomLayout.setVisibility(View.GONE);
                        startScaleSmallAnim();
                    } else {
                        mImgAppIcon.setImageDrawable(mAppIconList.get(indexAppIcon));

                        String numberString = (indexAppIcon + 1) + "/" + mAppIconList.size();
                        updateNumber(numberString);
                        startSquareAnim();
                        startRingAnim();
                    }
                }
            });
        }
        mRoundBigAnim.start();
    }

    /**
     * 开始方块动画
     */
    private void startSquareAnim() {
        for (View view : mSquareList) {
            view.setVisibility(View.VISIBLE);
        }
        //方块动画
        ObjectAnimator[] anim = new ObjectAnimator[mSquareList.size()];
        for (int i = 0; i < mSquareList.size(); i++) {
            anim[i] = ObjectAnimator.ofFloat(mSquareList.get(i), "rotation", 359);
            anim[i].setDuration(2500);
            anim[i].setRepeatCount(1000);
            anim[i].setInterpolator(mLinearInterpolator);
        }
        mSquareSet = new AnimatorSet();
        mSquareSet.playTogether(anim);
        mSquareSet.start();
    }

    /**
     * 开始圆环动画
     */
    private void startRingAnim() {
        mRingBig.setVisibility(View.VISIBLE);
        mRingSmall.setVisibility(View.VISIBLE);
        //大环
        PropertyValuesHolder pvhScaleXRingBig = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.83f);
        PropertyValuesHolder pvhScaleYRingBig = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.83f);
        mRingBigAnim = ObjectAnimator.ofPropertyValuesHolder(mRingBig, pvhScaleXRingBig, pvhScaleYRingBig);
        mRingBigAnim.setDuration(500);
        mRingBigAnim.start();
        //小环
        PropertyValuesHolder pvhScaleXRingSmall = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f);
        PropertyValuesHolder pvhScaleYRingSmall = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f);
        mRingSmallAnim = ObjectAnimator.ofPropertyValuesHolder(mRingSmall, pvhScaleXRingSmall, pvhScaleYRingSmall);
        mRingSmallAnim.setDuration(300);
        mRingSmallAnim.start();
        //十字架
        mImgCrossAnim = ObjectAnimator.ofFloat(mImgCross, "rotation", 359);
        mImgCrossAnim.setInterpolator(mLinearInterpolator);
        mImgCrossAnim.setDuration(rotationTime);
        mImgCrossAnim.setRepeatCount(1000);

        mRingBigAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mImgCross.setVisibility(View.VISIBLE);
                mImgCrossAnim.start();
            }
        });

        mRingSmallAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHandler.sendEmptyMessage(WHAT_START_APPICON_ANIM);
            }
        });
    }

    /**
     * 开始应用图标动画
     */
    private void startAppIconAnim() {
        if (mAppIconAnim == null) {
            PropertyValuesHolder pvhAlphaAppIcon = PropertyValuesHolder.ofFloat("alpha", 0.5f, 1f, 0.5f);
            PropertyValuesHolder pvhScaleXAppIcon = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f);
            PropertyValuesHolder pvhScaleYAppIcon = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
            mAppIconAnim = ObjectAnimator.ofPropertyValuesHolder(mImgAppIcon, pvhAlphaAppIcon, pvhScaleXAppIcon, pvhScaleYAppIcon);
            mAppIconAnim.setDuration(1000);
            mAppIconAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (indexAppIcon == mAppIconList.size() - 1) {
                        mImgAppIcon.setVisibility(View.GONE);
                        mHandler.sendEmptyMessage(WHAT_START_SMALL_ANIM);
                    } else {
                        indexAppIcon++;
                        mImgAppIcon.setImageDrawable(mAppIconList.get(indexAppIcon));

                        String numberString = (indexAppIcon + 1) + "/" + mAppIconList.size();
                        updateNumber(numberString);
                        mHandler.sendEmptyMessageDelayed(WHAT_START_APPICON_ANIM, 500);
                    }
                }
            });
        }
        mAppIconAnim.start();
    }

    /**
     * 开始圈圈变小动画
     */
    private void startScaleSmallAnim() {
        //圈圈变小
        if (mRoundSmallAnim == null) {
            //改变速度
            rotationTime = 500;

            WrapperView wrapper = new WrapperView(mAnimLayout);
            PropertyValuesHolder pvhScaleXLayout = PropertyValuesHolder.ofInt("width", mAnimLayout.getWidth(), 0);
            PropertyValuesHolder pvhScaleYLayout = PropertyValuesHolder.ofInt("height", mAnimLayout.getHeight(), 0);
            mLayoutAnim = ObjectAnimator.ofPropertyValuesHolder(wrapper, pvhScaleXLayout, pvhScaleYLayout);

            PropertyValuesHolder pvhScaleXRoundSmall = PropertyValuesHolder.ofFloat("scaleX", 1.3f, 0f);
            PropertyValuesHolder pvhScaleYRoundSmall = PropertyValuesHolder.ofFloat("scaleY", 1.3f, 0f);
            mRoundSmallAnim = ObjectAnimator.ofPropertyValuesHolder(mImgWifiPro, pvhScaleXRoundSmall, pvhScaleYRoundSmall);

            mRoundSmallAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    for (View view : mSquareList) {
                        view.setVisibility(View.GONE);
                    }
                }
            });

            mSmallSet = new AnimatorSet();
            mSmallSet.setDuration(1000);
            mSmallSet.playTogether(mLayoutAnim, mRoundSmallAnim);
            mSmallSet.start();
            mSmallSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //释放圈圈动画
                    mHandler.removeCallbacksAndMessages(null);
                    mRoundSmallAnim = null;
                    mLayoutAnim = null;
                    mSmallSet = null;
                    //释放方块动画
                    for (View view : mSquareList) {
                        view.setVisibility(View.GONE);
                    }
                    if (mSquareSet != null)
                        mSquareSet = null;
                    onFinish();
                }
            });
        }
        mRoundSmallAnim.start();
    }

    private void stopAnim(ObjectAnimator objectAnimator) {
        if (objectAnimator != null) {
            if (objectAnimator.isRunning()) {
                objectAnimator.removeAllListeners();
                objectAnimator.cancel();
            }
            objectAnimator = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                onFinish();
                break;
            default:
                break;
        }
    }

    public interface IOnFinishCallback {
        void onFinish();
    }

    private void onFinish() {
        stopAnim(mRoundRotateAnim);
        stopAnim(mRoundBigAnim);
        stopAnim(mRoundSmallAnim);
        stopAnim(scaleLightningAnim);
        stopAnim(mAppIconAnim);
        stopAnim(mRingBigAnim);
        stopAnim(mRingSmallAnim);
        stopAnim(mImgCrossAnim);
        stopAnim(mLayoutAnim);
        if (mSquareSet != null) {
            if (mSquareSet.isRunning()) {
                mSquareSet.removeAllListeners();
                mSquareSet.cancel();
            }
            mSquareSet = null;
        }
        if (mSmallSet != null) {
            if (mSmallSet.isRunning()) {
                mSmallSet.removeAllListeners();
                mSmallSet.cancel();
            }
            mSmallSet = null;
        }
        removeFromWindow();
        if (finishCallback!=null) finishCallback.onFinish();
    }

    public boolean removeFromWindow() {
        if (wManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    wManager.removeViewImmediate(this);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() != null) {
                        wManager.removeViewImmediate(this);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 封装一个属性
     */
    private static class WrapperView {
        private View mTarget;

        public WrapperView(View mTarget) {
            this.mTarget = mTarget;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

}
