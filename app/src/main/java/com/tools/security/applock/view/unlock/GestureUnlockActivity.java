package com.tools.security.applock.view.unlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.batmobi.Ad;
import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatNativeAd;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.facebook.ads.NativeAd;
import com.tools.security.R;
import com.tools.security.applock.LockPatternViewPattern;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.view.LockMainActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.service.LockService;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.LockPatternUtils;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.utils.volley.image.ImageUtils;
import com.tools.security.widget.LockPatternView;
import com.tools.security.widget.popupwindow.UnLockMenuPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/1/6.
 * 图形解锁
 */

public class GestureUnlockActivity extends BaseActivity implements View.OnClickListener {

    private TextView mAdBgView;
    private ImageView mIconMore;
    private LockPatternView mLockPatternView;
    private ImageView mUnLockIcon, mBgLayout, mAppLogo;
    private TextView mUnLockText, mUnlockFailTip, mLockAdDesc, mLockAdBtn, mAppLabel;
    private RelativeLayout mUnLockLayout, mAdLayout;
    private NetworkImageView mLockAdContent;

    private PackageManager packageManager;
    private String pkgName; //解锁应用的包名
    private String actionFrom;//按返回键的操作
    private LockPatternUtils mLockPatternUtils;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CommLockInfoManager mLockInfoManager;
    private UnLockMenuPopWindow mPopWindow;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureUnlockReceiver mGestureUnlockReceiver;
    private BatNativeAd batNativeAd;
    private List<Ad> batmobiNativeAds = new ArrayList<>();
    private List<Object> facebookNativeAds = new ArrayList<>();
    private String iconUrl;
    private String contentUrl;
    private List<String> contentUrls;
    private ApplicationInfo appInfo;
    public static final String FINISH_UNLOCK_THIS_APP = "finish_unlock_this_app";

    private Drawable iconDrawable;
    private String appLabel;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_gesture_unlock;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this); //状态栏透明
    }

    @Override
    protected void init() {
        mUnLockLayout = (RelativeLayout) findViewById(R.id.unlock_layout);
        mIconMore = (ImageView) findViewById(R.id.btn_more);
        mLockPatternView = (LockPatternView) findViewById(R.id.unlock_lock_view);
        mUnLockIcon = (ImageView) findViewById(R.id.unlock_icon);
        mBgLayout = (ImageView) findViewById(R.id.bg_layout);
        mUnLockText = (TextView) findViewById(R.id.unlock_text);
        mUnlockFailTip = (TextView) findViewById(R.id.unlock_fail_tip);
        mAdLayout = (RelativeLayout) findViewById(R.id.ad_layout);
        mLockAdContent = (NetworkImageView) findViewById(R.id.lock_ad_content);
        mLockAdDesc = (TextView) findViewById(R.id.lock_ad_desc);
        mLockAdBtn = (TextView) findViewById(R.id.lock_ad_btn);
        mAppLogo = (ImageView) findViewById(R.id.app_logo);
        mAppLabel = (TextView) findViewById(R.id.app_label);
        mAdBgView = (TextView) findViewById(R.id.ad_bg_view);
        mAdBgView.getPaint().setAntiAlias(true);

        //获取解锁应用的包名
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        //获取按返回键的操作
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        //初始化
        packageManager = getPackageManager();

        mLockInfoManager = new CommLockInfoManager(this);
        mPopWindow = new UnLockMenuPopWindow(this, pkgName, true);

        initLayoutBackground();
        initLockPatternView();
        mIconMore.setOnClickListener(this);

        mGestureUnlockReceiver = new GestureUnlockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UnLockMenuPopWindow.UPDATE_LOCK_VIEW);
        filter.addAction(FINISH_UNLOCK_THIS_APP);
        registerReceiver(mGestureUnlockReceiver, filter);

        loadAdvertisement();
    }

    /**
     * 给应用Icon和背景赋值
     */
    private void initLayoutBackground() {
        try {
            appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (appInfo != null) {
                iconDrawable = packageManager.getApplicationIcon(appInfo);
                appLabel = packageManager.getApplicationLabel(appInfo).toString();
                mUnLockIcon.setImageDrawable(iconDrawable);
                mUnLockText.setText(appLabel);
                mUnlockFailTip.setText(getString(R.string.password_gestrue_tips));
                final Drawable icon = packageManager.getApplicationIcon(appInfo);
                mUnLockLayout.setBackgroundDrawable(icon);
                mUnLockLayout.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mUnLockLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                                mUnLockLayout.buildDrawingCache();
                                Bitmap bmp = LockUtil.drawableToBitmap(icon, mUnLockLayout);
                                LockUtil.blur(GestureUnlockActivity.this, LockUtil.big(bmp), mUnLockLayout);  //高斯模糊
                                return true;
                            }
                        });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化解锁控件
     */
    private void initLockPatternView() {
        mLockPatternView.setLineColorRight(0x80ffffff);
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                if (mLockPatternUtils.checkPattern(pattern)) { //解锁成功,更改数据库状态
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
                        startActivity(new Intent(GestureUnlockActivity.this, LockMainActivity.class));
                        finish();
                    } else {
                        SpUtil.getInstance().putLong(AppConstants.LOCK_CURR_MILLISENCONS, System.currentTimeMillis()); //记录解锁时间
                        SpUtil.getInstance().putString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, pkgName);//记录解锁包名

                        //发送最后解锁的时间给应用锁服务
                        Intent intent = new Intent(LockService.UNLOCK_ACTION);
                        intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis());
                        intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, pkgName);
                        sendBroadcast(intent);

                        mLockInfoManager.unlockCommApplication(pkgName);
                        LockUtil.getInstance().setLastUnlockPkg(pkgName);
                        finish();
                    }
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                            String format = getResources().getString(R.string.password_error_count);
//                            String str = String.format(format, retry);
                            // mUnlockFailTip.setText(str);
//                            ToastUtil.showShort(str);
                        }
                    } else {
//                        ToastUtil.showShort(getString(R.string.password_short));
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= 3) { //失败次数大于3次
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) { //失败次数大于阻止用户前的最大错误尝试次数
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    } else {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                }
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }


    /**
     * 加载广告
     */
    private void loadAdvertisement() {
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(1);
        BatAdBuild.Builder build = new BatAdBuild.Builder(this, AppConstants.BATMOBI_APP_LOCK_PLACEMENT_ID, BatAdType.NATIVE.getType(), new IAdListener() {
            @Override
            public void onAdLoadFinish(List<Object> list) {
                initAdvertisement(list);
                AppUtils.isFacebookAd(list);
            }

            @Override
            public void onAdError(AdError adError) {
                mAdBgView.setVisibility(View.GONE);
                mAdLayout.setVisibility(View.GONE);
                mUnLockIcon.setVisibility(View.VISIBLE);
                mUnLockText.setVisibility(View.VISIBLE);
                mUnlockFailTip.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdShowed() {

            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdClicked() {

            }
        })
                .setAdsNum(1).setFacebookConfig(config);
        BatmobiLib.load(build.build());
    }

    /**
     * 初始化广告
     */
    private void initAdvertisement(List<Object> list) {
        if (list != null && list.size() > 0) {
            mUnLockIcon.setVisibility(View.GONE);
            mUnLockText.setVisibility(View.GONE);
            mUnlockFailTip.setVisibility(View.GONE);
            mAdBgView.setVisibility(View.VISIBLE);
            mAdLayout.setVisibility(View.VISIBLE);
            if (iconDrawable != null) {
                mAppLogo.setImageDrawable(iconDrawable);
            }
            if (!TextUtils.isEmpty(appLabel)) {
                mAppLabel.setText(appLabel);
            }
            Object object = null;
            if (list.get(0) instanceof BatNativeAd) {
                batNativeAd = (BatNativeAd) list.get(0);
                batmobiNativeAds = batNativeAd.getAds();
                object = batmobiNativeAds.get(0);
            } else if (list.get(0) instanceof NativeAd) {
                facebookNativeAds = list;
                object = facebookNativeAds.get(0);
            }
            if (object == null) return;
            if (object instanceof NativeAd) {
                NativeAd nativeAd = (NativeAd) object;
                NativeAd.Image iconImg = nativeAd.getAdIcon();
                NativeAd.Image contentImg = nativeAd.getAdCoverImage();
                if (iconImg != null) iconUrl = iconImg.getUrl();
                if (contentImg != null) contentUrl = contentImg.getUrl();
                if (!TextUtils.isEmpty(iconUrl)) {
                }
                if (!TextUtils.isEmpty(contentUrl)) {
                    ImageUtils.displayNet2(contentUrl, mLockAdContent, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                }
                mLockAdDesc.setText(nativeAd.getAdTitle());
                //nativeAd.getAdBody();
                nativeAd.registerViewForInteraction(mLockAdBtn);
            } else if (object instanceof Ad) {
                Ad ad = (Ad) object;
                String iconUrl = ad.getIcon();
                if (!TextUtils.isEmpty(iconUrl)) {
                    //ImageUtils.display2(iconUrl, mImgIcon, this, R.drawable.ic_default_90, R.drawable.ic_default_90);
                }
                contentUrls = ad.getCreatives(Ad.AD_CREATIVE_SIZE_1200x627);
                if (contentUrls != null && contentUrls.size() > 0) {
                    if (!TextUtils.isEmpty(contentUrls.get(0))) {
                        ImageUtils.displayNet2(contentUrls.get(0), mLockAdContent, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                    }
                }
                mLockAdDesc.setText(ad.getName());
                //ad.getDescription();
                batNativeAd.registerView(mLockAdBtn, ad);
            }
        } else {
            mAdBgView.setVisibility(View.GONE);
            mAdLayout.setVisibility(View.GONE);
            mUnLockIcon.setVisibility(View.VISIBLE);
            mUnLockText.setVisibility(View.VISIBLE);
            mUnlockFailTip.setVisibility(View.VISIBLE);
        }
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public void onBackPressed() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
            LockUtil.goHome(this);
 //           SecurityApplication.getInstance().clearAllActivity();
        } else if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
            finish();
        } else {
            startActivity(new Intent(this, LockMainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more:
                mPopWindow.showAsDropDown(mIconMore);
                break;
        }
    }

    private class GestureUnlockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UnLockMenuPopWindow.UPDATE_LOCK_VIEW)) {
                mLockPatternView.initRes();
            } else if (action.equals(FINISH_UNLOCK_THIS_APP)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGestureUnlockReceiver);
    }
}
