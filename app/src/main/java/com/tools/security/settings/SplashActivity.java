package com.tools.security.settings;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CooperationAd;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.main.MainActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SpUtil;

/**
 * Created by lzx on 2016/12/15.
 * email：386707112@qq.com
 * 功能：
 */

public class SplashActivity extends BaseActivity {

    private Handler mHandler = new Handler();
    private TextView mAppName, mAppSlogan;
    private ImageView mAppLogo, mIcCloud;
    private ObjectAnimator animator1, animator2, animator3, animator4, animator5, animator6, animator7;
    private AnimatorSet set;
    private PropertyValuesHolder pvh1, pvh2, pvh3, pvh4;

    @Override
    protected void init() {
        AppUtils.hideStatusBar(getWindow(), true);
        initData();

//        mAppName = (TextView) findViewById(R.id.app_name);
//        mAppSlogan = (TextView) findViewById(R.id.app_slogan);
//        mAppLogo = (ImageView) findViewById(R.id.app_logo);
//        mIcCloud = (ImageView) findViewById(R.id.ic_cloud);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 1500);

//        int[] location = new int[2];
//        mAppLogo.getLocationInWindow(location);
//        int offset = ScreenUtil.getPhoneHeight(this) - location[1] - (int) (ScreenUtil.getPhoneHeight(this) * 0.1f);
//
//        mAppLogo.setTranslationY(offset);
//        mAppName.setTranslationY(offset);
//        mAppSlogan.setTranslationY(offset);
//        mIcCloud.setVisibility(View.GONE);
//
//        animator1 = ObjectAnimator.ofFloat(mAppLogo, "alpha", 0f, 1);
//        animator2 = ObjectAnimator.ofFloat(mAppLogo, "translationY", offset, 0);
//        animator1.setDuration(700);
//        animator2.setDuration(800);
//        animator2.setInterpolator(new DecelerateInterpolator());
//
//        animator3 = ObjectAnimator.ofFloat(mAppName, "alpha", 0f, 1);
//        animator4 = ObjectAnimator.ofFloat(mAppName, "translationY", offset, 0);
//        animator3.setDuration(700);
//        animator4.setDuration(800);
//        animator4.setStartDelay(200);
//        animator4.setInterpolator(new DecelerateInterpolator());
//
//        animator5 = ObjectAnimator.ofFloat(mAppSlogan, "alpha", 0f, 1);
//        animator6 = ObjectAnimator.ofFloat(mAppSlogan, "translationY", offset, 0);
//        animator5.setDuration(700);
//        animator6.setDuration(600);
//        animator6.setStartDelay(400);
//        animator6.setInterpolator(new DecelerateInterpolator());
//
//        pvh1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.05f);
//        pvh2 = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.05f);
//        pvh3 = PropertyValuesHolder.ofFloat("translationY", AppUtils.dip2px(this, 10), 0);
//        pvh4 = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1);
//        animator7 = ObjectAnimator.ofPropertyValuesHolder(mIcCloud, pvh1, pvh2, pvh3, pvh4).setDuration(800);
//        animator7.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mIcCloud.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        set = new AnimatorSet();
//        set.play(animator1).with(animator2).with(animator3).with(animator4).with(animator5).with(animator6);
//        set.play(animator7).after(animator1);
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    }
//                }, 200);
//            }
//        });
//        set.start();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //新版本初次启动初始化一些数据
    private void initData() {
        boolean isFirstIn = SpUtil.getInstance().getBoolean("_is_first_start_" + SecurityApplication.getInstance().getVersionName(), true);
        if (isFirstIn) {
            //存储内推应用
            CooperationAd coolCleanerAd = new CooperationAd(CooperationAd.CLEAN,"Clean Junk Files", "Too many junk files will slow down your divice.Clean now.", "CLEAN NOW", "https://control.kochava.com/v1/cpi/click?campaign_id=kokillapp-0alvoee367de2c0332&network_id=5491&device_id=device_id&site_id=1&append_app_conv_trk_params=1","com.cool.clean");
            coolCleanerAd.save();
            CooperationAd cooperationAd = new CooperationAd(CooperationAd.POWER,"Power Usage Ranking", "Comprehensive inspection to find power consumption root reasons.", "OPTIMIZE", "https://control.kochava.com/v1/cpi/click?campaign_id=koku-battery-f12gc6ab5415940d8&network_id=5491&device_id=device_id&site_id=1&append_app_conv_trk_params=1","com.khome.kubattery");
            cooperationAd.save();
            SpUtil.getInstance().putBoolean("_is_first_start_" + SecurityApplication.getInstance().getVersionName(), false);
            //存放时间时，做个初始排序
            new FunctionAd(FunctionAd.APP_LOCK,  "Some apps with privacy issues", "Tap here to protect private messages and photos.", "PROTECT", 1).save();
            new FunctionAd(FunctionAd.WIFI,  "Analyze Your Security and Speed", "Unsecured connection could leak user fingerprints!", "TEST", 2).save();
            new FunctionAd(FunctionAd.PERMISSION,  "Apps with usage access", "Protect app privacy and Wi-Fi security.", "ENABLE", 3).save();
//            new FunctionAd(FunctionAd.SCANLE_FILE, R.drawable.img_ad_deep_scan, "Scan files", "One tap to protect your phone from virus and malware", "Fix Now", 4).save();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
    }
}
