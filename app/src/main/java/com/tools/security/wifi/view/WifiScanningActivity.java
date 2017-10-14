package com.tools.security.wifi.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.NetworkUtil;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.CircleDotProgressBar;
import com.tools.security.widget.MyScrollView;
import com.tools.security.wifi.presenter.WifiScanContract;
import com.tools.security.wifi.presenter.WifiScanPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * description:wifi扫描进行中
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class WifiScanningActivity extends BaseActivity implements WifiScanContract.View {

    private WifiScanPresenter presenter;
    private CircleDotProgressBar progressBar;
    private int progress = 0;
    private int maxProgress = 40;
    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_UPDATE_SCANNING_ITEM = 2;
    private Runnable progressRunnable;
    private MyScrollView scrollView;
    private TextView currentWifiText;
    private ImageView securityTabImg, speedTabImg;
    private TextView securityTabText, speedTabText;
    private RotateAnimation rotateAnimation;
    private ObjectAnimator alphaAnimation;
    private int whiteColor;
    private int grayColor;
    private AlphaAnimation tabAnimation;
    private ImageView wifiImg;
    private View bgView;
    //圆形进度的速度
    private int progressSpeed = 0;
    private ArrayList<Integer> imageIds = new ArrayList<>();
    private ArrayList<Integer> layoutIds = new ArrayList<>();
    private WifiInfo wifiInfo;

    private boolean hasProblems = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    progress++;
                    if ((progress / maxProgress) % 2 == 0) {
                        if (progressBar.getDotColor() != whiteColor) {
                            progressBar.setDotColor(whiteColor);
                            progressBar.setDotBgColor(grayColor);
                        }
                    } else {
                        if (progressBar.getDotColor() != grayColor) {
                            progressBar.setDotColor(grayColor);
                            progressBar.setDotBgColor(whiteColor);
                        }
                    }
                    progressBar.setProgress(progress % maxProgress);
                    break;
                case MSG_UPDATE_SCANNING_ITEM:
                    int index = msg.arg2;
                    if (index == imageIds.size()) {
                        finishedScan(msg.arg1);
                        return;
                    }
                    startTabAnim(index);
                    updateItemAnim(index, msg.arg1, imageIds.get(index));
                    break;
            }
        }
    };

    @Override
    protected void init() {
        initView();
        initData();
        loadAd();
    }

    private void initView() {
        progressBar = (CircleDotProgressBar) findViewById(R.id.progressbar);
        progressBar.setProgressMax(maxProgress);
        wifiImg = (ImageView)findViewById(R.id.img_wifi);
        bgView = findViewById(R.id.view_bg);
        scrollView = (MyScrollView) findViewById(R.id.scrollview_scanning);
        currentWifiText = (TextView) findViewById(R.id.text_current_wifi);
        securityTabImg = (ImageView) findViewById(R.id.img_tab_security);
        securityTabText = (TextView) findViewById(R.id.text_tab_security);
        speedTabImg = (ImageView) findViewById(R.id.img_tab_speed);
        speedTabText = (TextView) findViewById(R.id.text_tab_speed);

        layoutIds.add(R.id.relative_connected);
        layoutIds.add(R.id.relative_no_captive);
        layoutIds.add(R.id.relative_arp);
        layoutIds.add(R.id.relative_devices);
        layoutIds.add(R.id.relative_mitm);
        layoutIds.add(R.id.relative_encryption);
        layoutIds.add(R.id.relative_speed);

        imageIds.add(R.id.img_connected);
        imageIds.add(R.id.img_captive);
        imageIds.add(R.id.img_arp);
        imageIds.add(R.id.img_devices);
        imageIds.add(R.id.img_mitm);
        imageIds.add(R.id.img_encryption);
        imageIds.add(R.id.img_speed);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (ScreenUtil.getPhoneWidth(this) * 3 / 5f), (int) (ScreenUtil.getPhoneWidth(this) * 3 / 5f));
        layoutParams1.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / 5f - ScreenUtil.getPhoneWidth(this) * 3 / 10f);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressBar.setLayoutParams(layoutParams1);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams2.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / 5f - ScreenUtil.getPhoneWidth(this) * 9 / 20f);
        currentWifiText.setLayoutParams(layoutParams2);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams((int) (ScreenUtil.getPhoneWidth(this) * 3 / 5f) + ScreenUtil.dip2px(this, 14f), (int) (ScreenUtil.getPhoneWidth(this) * 3 / 5f) + ScreenUtil.dip2px(this, 14f));
        layoutParams3.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / 5f - ScreenUtil.getPhoneWidth(this) * 3 / 10f) - ScreenUtil.dip2px(this, 8f);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bgView.setLayoutParams(layoutParams3);

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams((int) (ScreenUtil.getPhoneWidth(this) * 1 / 5f), (int) (ScreenUtil.getPhoneWidth(this) * 1 / 5f));
        layoutParams4.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / 5f - ScreenUtil.getPhoneWidth(this) * 1 / 10f);
        layoutParams4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        wifiImg.setLayoutParams(layoutParams4);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void initData() {
        whiteColor = getResources().getColor(R.color.white);
        grayColor = getResources().getColor(R.color.white_20);

        boolean wifiEnable = NetworkUtil.isWifiConnected(this);
        if (wifiEnable) {
            wifiInfo = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo();
            if (wifiInfo!=null){
                currentWifiText.setText(wifiInfo.getSSID().replace("\"", ""));
            }else {
                currentWifiText.setText("Current Wifi");
            }
        }

        presenter = new WifiScanPresenter(this, this, handler,wifiInfo);
        presenter.scan();

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                float speedPercent = Math.abs(progress % 40 - 20) / 20f;
                progressSpeed = (int) (speedPercent * 30);
                handler.postDelayed(this, progressSpeed);
                handler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
            }
        };
        handler.post(progressRunnable);
        alphaAnimation = ObjectAnimator.ofFloat(wifiImg, "alpha", 1.0f, 0.1f, 1.0f);
        alphaAnimation.setDuration(1340);
        alphaAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimation.setRepeatCount(1000);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.start();
    }

    private void loadAd(){
        AdStaticConstant.ads=null;
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(6);
        BatAdBuild.Builder build = new BatAdBuild.Builder(this,
                AppConstants.BATMOBI_VIRUS_RESULT_PLACEMENT_ID,
                BatAdType.NATIVE.getType(),
                new IAdListener() {
                    @Override
                    public void onAdLoadFinish(List<Object> obj) {
                        AppUtils.isFacebookAd(obj);
                        AdStaticConstant.ads=obj;
                        AdStaticConstant.normalAdSaveTime=System.currentTimeMillis();
                    }

                    @Override
                    public void onAdError(AdError error) {
                    }

                    @Override
                    public void onAdClosed() {
                    }

                    @Override
                    public void onAdShowed() {
                    }

                    @Override
                    public void onAdClicked() {
                    }
                })
                .setAdsNum(6)
                .setFacebookConfig(config);
        BatmobiLib.load(build.build());
    }

    private void startTabAnim(int index) {
        if (tabAnimation == null) {
            tabAnimation = new AlphaAnimation(1.0f, 0.3f);
            tabAnimation.setDuration(500);
            tabAnimation.setRepeatCount(1000);
            tabAnimation.setInterpolator(new LinearInterpolator());
            tabAnimation.setRepeatMode(Animation.REVERSE);
        }
        if (index == 0) {
            onTitleChanged("Checking Security", -1);
            securityTabText.startAnimation(tabAnimation);
            securityTabImg.startAnimation(tabAnimation);
            speedTabText.setAlpha(0.5f);
            speedTabImg.setAlpha(0.5f);
        } else if (index == imageIds.size() - 1) {
            onTitleChanged("Speed Testing", -1);
            securityTabText.setAlpha(0.5f);
            securityTabImg.setAlpha(0.5f);
            speedTabText.setAlpha(1.0f);
            speedTabImg.setAlpha(1.0f);
            securityTabText.clearAnimation();
            securityTabImg.clearAnimation();
            speedTabText.startAnimation(tabAnimation);
            speedTabImg.startAnimation(tabAnimation);
        }
    }

    @Override
    protected void onHomeClick() {
        handler.removeCallbacksAndMessages(null);
        super.onHomeClick();
    }

    @Override
    public void finish() {
        presenter.onDestory();
        super.finish();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    private void finishedScan(int result) {
        handler.removeCallbacksAndMessages(null);
        View imageView = findViewById(imageIds.get(imageIds.size() - 1));
        imageView.clearAnimation();
        imageView.setBackgroundResource(R.drawable.ic_scanning_success);
        alphaAnimation.cancel();
        if (hasProblems) {
//            new WifiState(wifiInfo.getSSID(),wifiInfo.getBSSID(),WifiState.TYPE_RISK).save();
            startActivity(new Intent(this, WifiProblemResultActivity.class).putExtra("wifi",wifiInfo==null?"Current":wifiInfo.getSSID().replace("\"", "")));
        } else {
//            new WifiState(wifiInfo.getSSID(),wifiInfo.getBSSID(),WifiState.TYPE_SAFE).save();
            Bundle bundle = new Bundle();
            String speedStr = (result == -1) ? "<10KB/S" : StringUtil.getFormatSize(result * 1024) + "/S";
            CommonResult commonResult = new CommonResult("Safe", speedStr, FunctionAd.WIFI, ScreenUtil.dip2px(this, 76f));
            bundle.putSerializable("result", commonResult);
            startActivity(new Intent(this, CommonResultActivity.class).putExtras(bundle));
        }
        SpUtil.getInstance().putBoolean(wifiInfo.getBSSID(),hasProblems);
        SpUtil.getInstance().putBoolean(AppConstants.USED_WIFI_SECURITY,true);

        onHomeClick();
    }

    private void updateItemAnim(final int index, int result, final Integer imageId) {
        if (index == 0) {
            if (rotateAnimation == null) {
                rotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1200);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setRepeatMode(Animation.RESTART);
                rotateAnimation.setRepeatCount(1000);
            }
            findViewById(imageId).startAnimation(rotateAnimation);
            return;
        }
        View imageView = findViewById(imageIds.get(index - 1));
        imageView.clearAnimation();
        if (result != 0) {
            Log.e("TAG", "result=" + result + ",index=" + index);
            hasProblems = true;
        }
        imageView.setBackgroundResource(result == 0 ? R.drawable.ic_scanning_success : R.drawable.ic_scanning_failed);
        findViewById(layoutIds.get(index - 1)).setAlpha(0.6f);
        if (index >= 2) {
            findViewById(layoutIds.get(index - 2)).setAlpha(0.3f);
        }
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1.0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.6f, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, scaleX, scaleY, alpha);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scrollView.smoothScrollTo(0, index * ScreenUtil.dip2px(WifiScanningActivity.this, 30f));
                if (rotateAnimation == null) {
                    rotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setRepeatMode(Animation.RESTART);
                    rotateAnimation.setRepeatCount(1000);
                }
                findViewById(imageId).startAnimation(rotateAnimation);
            }
        });
        objectAnimator.start();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_scanning;
    }
}
