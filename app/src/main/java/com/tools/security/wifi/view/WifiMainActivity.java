package com.tools.security.wifi.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.bean.SafeLevel;
import com.tools.security.bean.WifiSafeLevel;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.NetworkUtil;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.MyParticleSystem;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * description:WIFI主界面
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class WifiMainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private LinearLayout wifiCenterLinear;
    private TextView devicesLinear;
    private TextView releaseLinear;
    private TextView analysisText;
    private TextView statusText;
    private View wifiStatusImg;
    private TextView wifiNameText;
    private View pressCircleView, bottomShadowImage;
    private View contentView;

    private BroadcastReceiver wifiReceiver;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    //wifi是否可用
    private boolean wifiEnable = false;
    //预防多次点击scan
    private boolean isStartedScan = false;

    private WifiSafeLevel currentSafeLevel;

    private View mImgCircle;
    private ObjectAnimator animator1;
    private ObjectAnimator animator2;

    private MyParticleSystem particleSystem;

    private ParticleSystem ps;

    private RelativeLayout.LayoutParams circleParams;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected void init() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;
        saveUseTime();
        initView();
        initReceiver();
    }

    //更新功能最后使用时间
    private void saveUseTime() {
        List<FunctionAd> functionAds = DataSupport.where(" type = ?", "" + FunctionAd.WIFI).find(FunctionAd.class);
        if (functionAds != null && functionAds.size() > 0) {
            FunctionAd functionAd = functionAds.get(0);
            functionAd.setLast_user_time(System.currentTimeMillis());
            functionAd.update(functionAd.getId());
        }
    }

    private void initView() {
        wifiCenterLinear = (LinearLayout) findViewById(R.id.linear_wifi);
        devicesLinear = (TextView) findViewById(R.id.linear_devices);
        releaseLinear = (TextView) findViewById(R.id.linear_release);
        analysisText = (TextView) findViewById(R.id.text_wifi_analysis);
        statusText = (TextView) findViewById(R.id.text_wifi_status);
        wifiNameText = (TextView) findViewById(R.id.text_current_wifi);
        wifiStatusImg = findViewById(R.id.img_wifi_status);
        pressCircleView = findViewById(R.id.view_circle_press);
        mImgCircle = findViewById(R.id.img_circle);
        bottomShadowImage = findViewById(R.id.bottom_shadow_image);
        contentView = findViewById(R.id.layout_wifi);

        wifiCenterLinear.setOnClickListener(this);
        devicesLinear.setOnClickListener(this);
        releaseLinear.setOnClickListener(this);

        int layoutWidth = (int) (ScreenUtil.getPhoneWidth(this) * 7 / 10f);
        int layoutHeight = (int) (ScreenUtil.getPhoneWidth(this) * 7 / 10f);
        int topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - ScreenUtil.getPhoneWidth(this) * 7 / (float) 20);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
        layoutParams.topMargin = topMargin;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        wifiCenterLinear.setLayoutParams(layoutParams);

        circleParams = new RelativeLayout.LayoutParams(layoutWidth + AppUtils.dip2px(this, 20), layoutHeight + AppUtils.dip2px(this, 20));
        circleParams.topMargin = topMargin - AppUtils.dip2px(this, 10);
        circleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImgCircle.setLayoutParams(circleParams);

        //让释放时扩散圆的大小为按压后圆的大小
        RelativeLayout.LayoutParams pressCircleLayoutParams = new RelativeLayout.LayoutParams((int) (layoutWidth * 0.9f), (int) (layoutHeight * 0.9f));
        pressCircleLayoutParams.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / 5f - ScreenUtil.getPhoneWidth(this) * 0.9f * 7 / 20f);
        pressCircleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        pressCircleView.setLayoutParams(pressCircleLayoutParams);

        devicesLinear.setOnTouchListener(this);
        releaseLinear.setOnTouchListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in, R.anim.slide_bottom_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Create a particle system and start emiting
                ps = new ParticleSystem(this, 5000, R.drawable.ic_point_blue, 800);
                ps.setScaleRange(0.7f, 0.3f);
                ps.setSpeedRange(0.005f, 0.01f);
                ps.setRotationSpeedRange(300, 600);
                ps.setFadeOut(300, new AccelerateInterpolator());
                ps.emit((int) event.getX(), (int) event.getY(), 100);
                break;
            case MotionEvent.ACTION_MOVE:
                ps.updateEmitPoint((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ps.stopEmitting();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartedScan = false;
        playCircleAnim();
        setStatus();
    }

    //设置安全状态
    private void setStatus() {
        wifiEnable = NetworkUtil.isWifiConnected(this);
        if (wifiEnable) {
            //wifi已连接
            if (wifiManager == null) wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) {
                currentSafeLevel = WifiSafeLevel.UNKNOW;
                return;
            }
            wifiNameText.setText(wifiInfo.getSSID().replace("\"", ""));
            //获取本地存储的wifi信息，去做状态判断
            /*WifiState wifiState = getLocalWifiData(wifiInfo.getBSSID());
            if (wifiState == null) {
                //此wifi未扫描过
                currentSafeLevel = WifiSafeLevel.UNKNOW;
            } else {
                //此wifi已经扫描过
                //根据本地保存的wifi信息进行状态设置
                switch (wifiState.getType()) {
                    case WifiState.TYPE_SAFE:
                        currentSafeLevel = WifiSafeLevel.SAFE;
                        break;
                    case WifiState.TYPE_RISK:
                    case WifiState.TYPE_DANGER:
                        currentSafeLevel = WifiSafeLevel.RISK;
                        break;
                    case WifiState.TYPE_UNKNOW:
                        currentSafeLevel = WifiSafeLevel.UNKNOW;
                        break;
                }
            }*/
            if (SpUtil.getInstance().getBoolean(wifiInfo.getBSSID(),true)){
                currentSafeLevel = WifiSafeLevel.RISK;
            }else {
                currentSafeLevel = WifiSafeLevel.SAFE;
            }
        } else {
            //wifi未连接
            currentSafeLevel = WifiSafeLevel.INVALID;
        }
        //根据安全状态，设置界面
        switch (currentSafeLevel) {
            case SAFE:
                statusText.setText("Safe");
                wifiNameText.setVisibility(View.VISIBLE);
                analysisText.setVisibility(View.VISIBLE);
//                wifiStatusImg.setImageResource(R.drawable.ic_wifi_able);
                wifiStatusImg.setVisibility(View.GONE);
                bottomShadowImage.setBackgroundResource(R.drawable.wifi_security_blue);
                analysisText.setTextColor(getResources().getColor(R.color.primary));
                statusText.setTextColor(getResources().getColor(R.color.primary));
                break;
            case RISK:
            case UNKNOW:
                statusText.setText("Risk");
                wifiNameText.setVisibility(View.VISIBLE);
                analysisText.setVisibility(View.VISIBLE);
                wifiStatusImg.setVisibility(View.GONE);
                bottomShadowImage.setBackgroundResource(R.drawable.wifi_security_yellow);
                analysisText.setTextColor(getResources().getColor(R.color.orange));
                statusText.setTextColor(getResources().getColor(R.color.orange));
                break;
            case INVALID:
                statusText.setText("Turn Wi-Fi On");
                wifiNameText.setVisibility(View.GONE);
                analysisText.setVisibility(View.GONE);
                wifiStatusImg.setBackgroundResource(R.drawable.ic_wifi_invalid);
                wifiStatusImg.setVisibility(View.VISIBLE);
                bottomShadowImage.setBackgroundResource(R.drawable.wifi_security_blue);
                analysisText.setTextColor(getResources().getColor(R.color.primary));
                statusText.setTextColor(getResources().getColor(R.color.primary));
                break;
        }
        //根据wifi是否可用，设置button状态
        devicesLinear.setEnabled(wifiEnable);
        devicesLinear.setAlpha(wifiEnable ? 0.8f : 0.5f);
        releaseLinear.setEnabled(wifiEnable);
        releaseLinear.setAlpha(wifiEnable ? 0.8f : 0.5f);

        switch (currentSafeLevel) {
            case SAFE:
            case INVALID:
                contentView.setBackgroundResource(R.drawable.bg_gradient_blue);
                break;
            case RISK:
            case UNKNOW:
                contentView.setBackgroundResource(R.drawable.bg_gradient_orange);
                break;
        }
    }

    //按压压下效果
    private void pressHeartAnim(float scaleX, float scaleY,View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", scaleX, 0.9f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", scaleY, 0.9f);
        ObjectAnimator scaleAnimator=ObjectAnimator.ofPropertyValuesHolder(view,pvhScaleX,pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
    }

    //按压抬起效果
    private void releaseHeartAnim(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f);
        ObjectAnimator scaleAnimator=ObjectAnimator.ofPropertyValuesHolder(view,pvhScaleX,pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
    }


    private void initReceiver() {
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setStatus();
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.EXTRA_WIFI_STATE);
        registerReceiver(wifiReceiver, intentFilter);
    }

    /**
     * 开始动画
     */
    private void playCircleAnim() {
        if (animator1 == null) {
            PropertyValuesHolder pvhScaleX1 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.5f, 1.45f);
            PropertyValuesHolder pvhScaleY1 = PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.5f, 1.45f);
            PropertyValuesHolder pvhAlpha1 = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f, 0.0f);
            animator1 = ObjectAnimator.ofPropertyValuesHolder(mImgCircle, pvhScaleX1, pvhScaleY1, pvhAlpha1);
            animator1.setDuration(2000);
            animator1.setInterpolator(new LinearInterpolator());
            animator1.setRepeatCount(Integer.MAX_VALUE);
        }
        if (animator2 == null) {
            PropertyValuesHolder pvhScaleX2 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.9f, 1.05f, 1.0f);
            PropertyValuesHolder pvhScaleY2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.9f, 1.05f, 1.0f);
            PropertyValuesHolder pvhAlpha2 = PropertyValuesHolder.ofFloat("alpha", 0.8f, 1.0f, 0.7f, 0.8f);
            animator2 = ObjectAnimator.ofPropertyValuesHolder(wifiCenterLinear, pvhScaleX2, pvhScaleY2, pvhAlpha2);
            animator2.setDuration(2000);
            animator2.setInterpolator(new LinearInterpolator());
            animator2.setRepeatCount(Integer.MAX_VALUE);
        }

        animator1.start();
        animator2.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animator1 != null && animator1.isRunning()) animator1.cancel();
        if (animator2 != null && animator2.isRunning()) animator2.cancel();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        startParticleAnim();
    }

    //开启粒子动画
    private void startParticleAnim() {
        if (particleSystem != null) return;
        particleSystem = new MyParticleSystem(this, 4, R.drawable.ic_point, 5000);
        particleSystem.setScaleRange(1.0f, 2.0f);
        particleSystem.setSpeedRange(0.0001f, 0.001f);
        particleSystem.setFadeIn(1500);
        particleSystem.setFadeOut(1500);
        particleSystem.emitWithGravity(contentView, Gravity.NO_GRAVITY, 0.4f);
    }


    //释放扩散圆动画
    private void expandCircleAnim() {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(pressCircleView, "scaleX", 0.9f, 3.5f);
        objectAnimator1.setDuration(200);
        objectAnimator1.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(pressCircleView, "scaleY", 0.9f, 3.5f);
        objectAnimator2.setDuration(200);
        objectAnimator2.setInterpolator(new DecelerateInterpolator());

        objectAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pressCircleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pressCircleView.setScaleX(1.0f);
                pressCircleView.setScaleY(1.0f);
                pressCircleView.setVisibility(View.GONE);
                startScan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(objectAnimator1).with(objectAnimator2);
        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
        if (animator1 != null) {
            if (animator1.isRunning()) {
                animator1.cancel();
            }
            animator1 = null;
        }
        if (animator2 != null) {
            if (animator2.isRunning()) {
                animator2.cancel();
            }
            animator2 = null;
        }
        if (particleSystem != null) {
            particleSystem.cancel();
            particleSystem = null;
        }
        if (ps != null) {
            ps.cancel();
            ps = null;
        }
        wifiManager = null;
        wifiInfo = null;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_wifi:
                if (!isStartedScan) {
                    isStartedScan = true;
                    expandCircleAnim();
                }
                break;
            case R.id.linear_devices:
                startActivity(new Intent(WifiMainActivity.this, WifiDeviceScanActivity.class).putExtra("from", 1));
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
                break;
            case R.id.linear_release:
                //如果说没有释放过，或者上次释放时间已经过去4个小时以上，则跳到释放带框页面，否则跳到成功页面
                long lastReleaseTime = SpUtil.getInstance().getLong(AppConstants.LAST_RELEASE_BANDWIDTH_TIME, 0);
                if (lastReleaseTime == 0 || System.currentTimeMillis() - lastReleaseTime > 4 * 60 * 60 * 1000) {
                    startActivity(new Intent(WifiMainActivity.this, WifiReleaseActivity.class).putExtra("from", 1));
                } else {
                    Bundle bundle = new Bundle();
                    CommonResult commonResult = new CommonResult(getString(R.string.excellent), " ", FunctionAd.WIFI, ScreenUtil.dip2px(WifiMainActivity.this, 76f));
                    bundle.putSerializable("result", commonResult);
                    startActivity(new Intent(WifiMainActivity.this, CommonResultActivity.class).putExtras(bundle).putExtra("from", 1));
                }
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
                break;
        }
    }


    //颜色渐变动画
    private synchronized void startColorTranslation(final SafeLevel current, final SafeLevel target) {
        int animResId = -1;
        int gradientStartColor = -1;
        int gradientEndColor = -1;
        int primary = 0xff1f90f9;
        int orange = 0xfff59c2f;
        int red = 0xffd73358;
        switch (current) {
            case SAFE:
                gradientStartColor = primary;
                if (target == SafeLevel.SUSPICIOUS) {
                    animResId = R.animator.bg_color_blue_orange;
                    gradientEndColor = orange;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_blue_red;
                    gradientEndColor = red;
                }
                break;
            default:
            case SUSPICIOUS:
                gradientStartColor = orange;
                if (target == SafeLevel.SAFE) {
                    animResId = R.animator.bg_color_orange_blue;
                    gradientEndColor = primary;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_orange_red;
                    gradientEndColor = red;
                }
                break;
            case DANGER:
                gradientStartColor = red;
                if (target == SafeLevel.SAFE) {
                    gradientEndColor = primary;
                    animResId = R.animator.bg_color_red_blue;
                } else if (target == SafeLevel.SUSPICIOUS) {
                    gradientEndColor = orange;
                    animResId = R.animator.bg_color_red_orange;
                }
                break;
        }
        if (animResId == -1) return;
        ObjectAnimator objectAnimator1 = (ObjectAnimator) AnimatorInflater.loadAnimator(WifiMainActivity.this, animResId);
        objectAnimator1.setEvaluator(new ArgbEvaluator());
        objectAnimator1.setTarget(contentView);
        objectAnimator1.start();
        if (gradientStartColor == -1 || gradientEndColor == -1) return;
//        ColorUtil.translateStatusBarColors(this, gradientStartColor, gradientEndColor, 1500);
    }

    private void startScan() {
        if (wifiEnable) {
            startActivity(new Intent(WifiMainActivity.this, WifiScanningActivity.class));
        } else {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.linear_devices:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressHeartAnim(devicesLinear.getScaleX(), devicesLinear.getScaleY(),devicesLinear);
                        devicesLinear.setScaleX(0.9f);
                        devicesLinear.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        releaseHeartAnim(devicesLinear);
                        devicesLinear.setScaleX(1.0f);
                        devicesLinear.setScaleY(1.0f);
                        break;
                }
                break;
            case R.id.linear_release:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressHeartAnim(releaseLinear.getScaleX(), releaseLinear.getScaleY(),releaseLinear);
                        releaseLinear.setScaleX(0.9f);
                        releaseLinear.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        releaseHeartAnim(releaseLinear);
                        releaseLinear.setScaleX(1.0f);
                        releaseLinear.setScaleY(1.0f);
                        break;
                }
                break;
        }

        return false;
    }
}
