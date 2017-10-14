package com.tools.security.mainscan.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.SafeLevel;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.main.MainActivity;
import com.tools.security.mainscan.presenter.MainScanContract;
import com.tools.security.mainscan.presenter.MainScanPresenter;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.MyParticleSystem;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Random;

/**
 * description:云查杀Activity
 * author: xiaodifu
 * date: 2016/12/12.
 */

public class MainScanActivity extends BaseActivity implements MainScanContract.View {

    private RelativeLayout progressRelative;
    private MainScanPresenter presenter;
    private View progressView;
    private TextView virusText, privacyText, junkText, countText,scanningStatusText, progressText;
    private LinearLayout virusLinear,privacyLinear,junkLinear;

    private RelativeLayout contentView;
    //粒子效果
    private MyParticleSystem pointPs,alertPs,virusPs;

    private boolean virusScanning = false;
    private boolean privacyScanning = false;
    private boolean junkScanning = false;

    private SafeLevel currentSafeLevel;

    private int[] countTextLocation;

    //扫描时状态动画
    private AnimatorSet statusAnimatorSet;

    private int left;
    private int top;

    private boolean hasProblems=false;

    //是否需要等待颜色转换动画完成
    private boolean needWait = false;

    private static final int MSG_FINISH = 1000;

    private Handler handler;

    private ObjectAnimator rotationAnimator;

    @Override
    protected void init() {
        saveUseTime();
        initHandler();
        initView();
        initCloudScan();
    }

    //更新功能最后使用时间
    private void saveUseTime() {
        List<FunctionAd> functionAds = DataSupport.where(" type = ?", "" + FunctionAd.VIRUS).find(FunctionAd.class);
        if (functionAds != null && functionAds.size() > 0) {
            FunctionAd functionAd = functionAds.get(0);
            functionAd.setLast_user_time(System.currentTimeMillis());
            functionAd.update(functionAd.getId());
        }
    }

    private void initHandler() {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
            }
        };
    }

    private void initView() {
        contentView = (RelativeLayout) findViewById(R.id.relative_cloud_scan);
        scanningStatusText = (TextView) findViewById(R.id.text_scanning_status);
        progressView=findViewById(R.id.view_progress);
        progressText = (TextView) findViewById(R.id.text_progress);
        progressRelative = (RelativeLayout) findViewById(R.id.relative_progress);
        virusText = (TextView) findViewById(R.id.text_virus);
        privacyText = (TextView) findViewById(R.id.text_privacy);
        junkText = (TextView) findViewById(R.id.text_junk);
        countText = (TextView) findViewById(R.id.count_text);
        virusLinear= (LinearLayout) findViewById(R.id.linear_virus);
        privacyLinear= (LinearLayout) findViewById(R.id.linear_privacy);
        junkLinear= (LinearLayout) findViewById(R.id.linear_junk);

        scanningStatusText.setMovementMethod(new ScrollingMovementMethod());

        //粒子动画距离左边和上边的距离
        left = (int) ((ScreenUtil.getPhoneWidth(this) - ScreenUtil.getPhoneWidth(this) * 4 / (float) 7) / 2);
        top = (int) Math.abs(ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - ScreenUtil.getPhoneWidth(this) * 4 / (float) 14);

        //让Scan圆的直径为手机宽度的6/7，水平居中，距离顶部的距离（topMargin）为手机屏幕高度的2/5-圆的半径
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (ScreenUtil.getPhoneWidth(this) * 6 / (float) 7), (int) (ScreenUtil.getPhoneWidth(this) * 6 / (float) 7));
        layoutParams.topMargin = (int) Math.abs(ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - ScreenUtil.getPhoneWidth(this) * 6 / (float) 14);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressView.setLayoutParams(layoutParams);
        progressRelative.setLayoutParams(layoutParams);

        //设置问题计数文本的位置
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int countTextTopMargin = (int) Math.abs(ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - ScreenUtil.getPhoneWidth(this) * 6 / (float) 14) + (int) (ScreenUtil.getPhoneWidth(this) * 6 / (float) 7) - (int) (ScreenUtil.getPhoneHeight(this) * 0.05f);
        layoutParams2.topMargin = countTextTopMargin;
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        countText.setLayoutParams(layoutParams2);

        //设置扫描状态文本的位置
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams3.topMargin = countTextTopMargin + ScreenUtil.dip2px(this, 40);
        scanningStatusText.setLayoutParams(layoutParams3);

        AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        currentSafeLevel = appConfig.getSafeLevel();
        switch (currentSafeLevel) {
            case SAFE:
                contentView.setBackgroundColor(getResources().getColor(R.color.primary));
                break;
            case SUSPICIOUS:
                contentView.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case DANGER:
                contentView.setBackgroundColor(getResources().getColor(R.color.red));
                break;
        }
    }

    //开启旋转动画
    private void startRotationAnim(){
        rotationAnimator=ObjectAnimator.ofFloat(progressView,"rotation",0f,359f);
        rotationAnimator.setDuration(1500);
        rotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimator.setRepeatCount(Integer.MAX_VALUE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.start();
    }

    //开启粒子动画
    private void startParticleAnim() {
        if (pointPs != null) return;
        pointPs = new MyParticleSystem(this, 4, R.drawable.ic_point, 3000);
        pointPs.setScaleRange(1.0f, 2.0f);
        pointPs.setSpeedRange(0.0001f, 0.001f);
        pointPs.setFadeIn(1000);
        pointPs.setFadeOut(1000);
        pointPs.emitWithGravity(contentView, Gravity.NO_GRAVITY, 0.4f);
    }

    //扫描时状态字体加动画
    private void scanningStatusAnim(final View view) {
        if (statusAnimatorSet != null && statusAnimatorSet.isRunning()) statusAnimatorSet.cancel();

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.1f);
        objectAnimator1.setDuration(400);
        objectAnimator1.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator1.setRepeatCount(1000);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "scale", 1.0f, 0.90f);
        objectAnimator2.setDuration(400);
        objectAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator2.setRepeatCount(1000);

        statusAnimatorSet = new AnimatorSet();
        statusAnimatorSet.playTogether(objectAnimator1, objectAnimator2);
        statusAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setAlpha(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        statusAnimatorSet.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        startParticleAnim();
    }

    private void initCloudScan() {
        presenter = new MainScanPresenter(this, this);
        presenter.start();
        startRotationAnim();
    }

    //更新粒子位置
    private void updateParticleAnim(int status) {
        int[] location = getRandXY();
        switch (status) {
            case 1:
                if (virusPs == null) {
                    virusPs = new MyParticleSystem(this, 3, R.drawable.ic_virus_3, 2000);
                    virusPs.setScaleRange(0.8f, 1.2f);
                    virusPs.setSpeedRange(0.0001f, 0.001f);
                    virusPs.setFadeOut(1000);
                    virusPs.emit(location[0] + left, location[1] + top, 0.3f);
                } else {
                    virusPs.updateEmitPoint(location[0] + left, location[1] + top);
                }
                break;
            case 2:
            case 3:
                if (virusPs != null) virusPs.stopEmitting();
                if (alertPs != null) alertPs.stopEmitting();
                if (alertPs == null) {
                    alertPs = new MyParticleSystem(this, 3, R.drawable.ic_alert, 2000);
                    alertPs.setScaleRange(0.8f, 1.2f);
                    virusPs.setSpeedRange(0.0001f, 0.001f);
                    virusPs.setFadeOut(1000);
                    alertPs.emit(location[0] + left, location[1] + top, 0.3f);
                } else {
                    alertPs.updateEmitPoint(location[0] + left, location[1] + top);
                }
                break;
            default:
                break;
        }
    }

    //获取随机XY
    private int[] getRandXY() {
        float screenW = (ScreenUtil.getPhoneWidth(this) * 4 / (float) 7);
        int x = new Random().nextInt((int) screenW);
        int y = new Random().nextInt((int) screenW);
        //XY不能在圆的区域内,（将圆半径加大20，为了不让放大后的图标落到园内）
        while ((x > (int) screenW * 248 / 836 && x < (int) screenW * 588 / 836) || (x < (int) screenW * 153 / 836) || (x > (int) screenW * 683 / 836)) {
            x = new Random().nextInt((int) screenW);
        }
        while ((y > (int) screenW * 248 / 836 && y < (int) screenW * 588 / 836) || (y < (int) screenW * 153 / 836) || (y > (int) screenW * 683 / 836)) {
            y = new Random().nextInt((int) screenW);
        }
        return new int[]{x, y};
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_cloud_scan;
    }

    @Override
    protected void onDestroy() {
        if (pointPs != null) {
            pointPs.stopEmitting();
            pointPs = null;
        }
        if (alertPs != null) {
            alertPs.stopEmitting();
            alertPs = null;
        }
        if (virusPs != null) {
            virusPs.stopEmitting();
            virusPs = null;
        }
        if (statusAnimatorSet != null) {
            statusAnimatorSet.cancel();
            statusAnimatorSet = null;
        }
        if (statusAnimatorSet != null) {
            statusAnimatorSet.cancel();
            statusAnimatorSet = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (rotationAnimator!=null){
            if (rotationAnimator.isRunning()){
                rotationAnimator.cancel();
            }
            rotationAnimator=null;
        }
        currentSafeLevel = null;
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onScanningProgress(int progress, String countStr, int status) {
        progressText.setText("" + progress);
        switch (status) {
            case 1:
                if (!virusScanning) {
                    scanningStatusText.setText("Scanning for virus");
                    virusScanning = true;
                    scanningStatusAnim(virusText);
                }
                if (!TextUtils.isEmpty(countStr)&&!countStr.equals("0")) {
                    startColorTranslation(currentSafeLevel, SafeLevel.DANGER);
                    currentSafeLevel = SafeLevel.DANGER;
                    countText.setText(countStr);
                }
                if (progress % 7 == 0) updateParticleAnim(1);
                break;
            case 2:
                if (!privacyScanning) {
                    scanningStatusText.setText("Scanning for privacy risks");
                    privacyScanning = true;
                    scanningStatusAnim(privacyText);
                }
                if (progress % 3 == 0) updateParticleAnim(2);
                break;
            case 3:
                if (!junkScanning) {
                    scanningStatusText.setText("Scanning for junk files");
                    junkScanning = true;
                    scanningStatusAnim(junkText);
                }
                if (progress % 3 == 0) updateParticleAnim(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onScanningEnd(String result, int status, boolean haveProblem) {
        if (Double.parseDouble(result)>0d){
            hasProblems=true;
        }
        switch (status) {
            case 1:
                //结果数字动画
                switchResultAnim(result, status, virusText);
                break;
            case 2:
                if (haveProblem && currentSafeLevel == SafeLevel.SAFE) {
                    startColorTranslation(currentSafeLevel, SafeLevel.SUSPICIOUS);
                    currentSafeLevel = SafeLevel.SUSPICIOUS;
                }
                switchResultAnim(result, status, privacyText);
                break;
            case 3:
                if (haveProblem && currentSafeLevel == SafeLevel.SAFE) {
                    needWait = true;
                    startColorTranslation(currentSafeLevel, SafeLevel.SUSPICIOUS);
                    currentSafeLevel = SafeLevel.SUSPICIOUS;
                }
                switchResultAnim(result, status, junkText);
                break;
        }
    }

    //每一项扫描完时的动画
    private void switchResultAnim(final String result, final int status, final TextView view) {
        countText.setVisibility(View.VISIBLE);
        //值改变动画
        if (status == 1 || status == 2) {
            int count = Integer.parseInt(result);
            ValueAnimator valueAnimator = ValueAnimator.ofInt(count);
            valueAnimator.setDuration(100);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    countText.setText("" + value);
                }
            });
            valueAnimator.start();

        } else {
            float count = Float.parseFloat(result);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(count);
            valueAnimator.setDuration(100);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    countText.setText(StringUtil.getFormatSize(value));
                }
            });
            valueAnimator.start();
        }

        //获取起点坐标
        if (countTextLocation == null) {
            countTextLocation = new int[2];
            countText.getLocationInWindow(countTextLocation);
        }

        //获取终点坐标
        int[] end = new int[2];
        view.getLocationInWindow(end);

        //数字从小到大动画
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(countText, "scale", 0.0f, 1.0f);
        objectAnimator.setDuration(100);
        objectAnimator.start();

        //抛物线动画
        ObjectAnimator transX = ObjectAnimator.ofFloat(countText, "translationX", 0, end[0] - countTextLocation[0]/* + viewWidth / (float) 2*/);
        transX.setDuration(500);
        transX.setInterpolator(new LinearInterpolator());

        ObjectAnimator transYup = ObjectAnimator.ofFloat(countText, "translationY", 0, -ScreenUtil.dip2px(this, 10f));
        transYup.setDuration(100);
        transYup.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator transYdown = ObjectAnimator.ofFloat(countText, "translationY", -ScreenUtil.dip2px(this, 10f), end[1] - countTextLocation[1]/* + viewHeight / (float) 2*/);
        transYdown.setDuration(400);
        transYdown.setInterpolator(new AccelerateInterpolator());

        //item缩小动画
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.0f);
        scaleXAnimator.setDuration(400);
        scaleXAnimator.setInterpolator(new LinearInterpolator());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.0f);
        scaleYAnimator.setDuration(400);
        scaleYAnimator.setInterpolator(new LinearInterpolator());


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(transX).with(transYup);
        animatorSet.play(transYdown).with(scaleXAnimator).with(scaleYAnimator).after(transYup);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                countText.setVisibility(View.GONE);
                countText.setX(countTextLocation[0]);
                countText.setY(countTextLocation[1]);
                if (status == 3) {
                    view.setText(StringUtil.getFormatSize(Float.parseFloat(result)));
                } else {
                    view.setText(result);
                }
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                view.setBackgroundResource(R.color.transparent);
                if (status==1){
                    virusLinear.setAlpha(0.7f);
                    privacyLinear.setAlpha(1.0f);
                }else if (status==2){
                    privacyLinear.setAlpha(0.7f);
                    junkLinear.setAlpha(1.0f);
                }else {
                    junkLinear.setAlpha(0.7f);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.setStartDelay(100);
        animatorSet.start();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    //颜色渐变动画
    private void startColorTranslation(final SafeLevel current, final SafeLevel target) {
        int animResId = -1;
        switch (current) {
            case SAFE:
                if (target == SafeLevel.SUSPICIOUS) {
                    animResId = R.animator.bg_color_blue_orange;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_blue_red;
                }
                break;
            default:
            case SUSPICIOUS:
                if (target == SafeLevel.SAFE) {
                    animResId = R.animator.bg_color_orange_blue;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_orange_red;
                }
                break;
            case DANGER:
                if (target == SafeLevel.SAFE) {
                    animResId = R.animator.bg_color_red_blue;
                } else if (target == SafeLevel.SUSPICIOUS) {
                    animResId = R.animator.bg_color_red_orange;
                }
                break;
        }
        if (animResId == -1) return;
        ObjectAnimator objectAnimator1 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainScanActivity.this, animResId);
        objectAnimator1.setEvaluator(new ArgbEvaluator());
        objectAnimator1.setTarget(contentView);
        objectAnimator1.start();
    }

    @Override
    public void onScanningFinished() {
        AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        if (!appConfig.isScanned()) {
            appConfig.setScanned(true);
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        }
        rotationAnimator.cancel();
        if (needWait) {
            handler.sendEmptyMessage(MSG_FINISH);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!hasProblems){
                        Bundle bundle = new Bundle();
                        CommonResult commonResult = new CommonResult("Safe", getString(R.string.all_threat_resolved), FunctionAd.VIRUS, ScreenUtil.dip2px(MainScanActivity.this, 76f));
                        bundle.putSerializable("result",commonResult);
                        startActivity(new Intent(MainScanActivity.this, CommonResultActivity.class).putExtras(bundle));
                        MainScanActivity.this.finish();
                    }else {
                        startActivity(new Intent(MainScanActivity.this, MainScanResultActivity.class));
                        MainScanActivity.this.finish();
                    }
                }
            }, 100);
        } else {
            if (!hasProblems){
                Bundle bundle = new Bundle();
                CommonResult commonResult = new CommonResult("Safe", getString(R.string.all_threat_resolved), FunctionAd.VIRUS, ScreenUtil.dip2px(MainScanActivity.this, 76f));
                bundle.putSerializable("result",commonResult);
                startActivity(new Intent(MainScanActivity.this, CommonResultActivity.class).putExtras(bundle));
                MainScanActivity.this.finish();
            }else {
                startActivity(new Intent(MainScanActivity.this, MainScanResultActivity.class));
                MainScanActivity.this.finish();
            }
        }
    }

    @Override
    protected void onHomeClick() {
        AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        if (!appConfig.isScanned()) {
            appConfig.setScanned(true);
            appConfig.setSafeLevel(SafeLevel.SAFE);
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        }
        super.onHomeClick();
        overridePendingTransition(R.anim.fast_alpha_in,R.anim.fast_alpha_out);
    }

    @Override
    public void finish() {
        presenter.onDestory();
        super.finish();
    }
}
