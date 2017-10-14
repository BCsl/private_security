package com.tools.security.applock.view.unlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.presenter.NumberUnLockContract;
import com.tools.security.applock.presenter.NumberUnLockPresenter;
import com.tools.security.applock.view.LockMainActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.service.LockService;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.utils.volley.image.ImageUtils;
import com.tools.security.widget.popupwindow.UnLockMenuPopWindow;

import java.util.ArrayList;
import java.util.List;

import static com.tools.security.applock.view.unlock.GestureUnlockActivity.FINISH_UNLOCK_THIS_APP;

/**
 * Created by lzx on 2017/1/6.
 * 数字解锁
 */

public class NumberUnlockActivity extends BaseActivity implements View.OnClickListener, NumberUnLockContract.View {

    private TextView mAdBgView;
    private ImageView mIconMore, mAppLogo;
    private RelativeLayout mUnLockLayout, mAdLayout;
    private TextView mLockTip, mUnLockText, mLockAdDesc, mLockAdBtn, mAppLabel;
    private ImageView mNumPoint_1, mNumPoint_2, mNumPoint_3, mNumPoint_4;
    private TextView mNumber_0, mNumber_1, mNumber_2, mNumber_3, mNumber_4, mNumber_5, mNumber_6, mNumber_7, mNumber_8, mNumber_9;
    private ImageView mNumberDel;
    private ImageView mUnLockIcon;
    private NetworkImageView mLockAdContent;

    private static final int COUNT = 4; //4个点
    private List<String> numInput; //存储输入数字的列表
    private List<ImageView> pointList;
    private String lockPwd = "";//密码
    private String pkgName; //解锁应用的包名
    private String actionFrom;//按返回键的操作
    private Handler mHandler = new Handler();
    private PackageManager packageManager;
    private CommLockInfoManager mLockInfoManager;
    private UnLockMenuPopWindow mPopWindow;
    private NumberUnLockPresenter mPresenter;
    private NumberUnlockReceiver mNumberUnlockReceiver;
    private BatNativeAd batNativeAd;
    private List<Ad> batmobiNativeAds = new ArrayList<>();
    private List<Object> facebookNativeAds = new ArrayList<>();
    private String iconUrl;
    private String contentUrl;
    private List<String> contentUrls;
    private Drawable iconDrawable;
    private String appLabel;

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this); //状态栏透明
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_num_unlock;
    }

    @Override
    protected void init() {
        mIconMore = (ImageView) findViewById(R.id.btn_more);
        mUnLockLayout = (RelativeLayout) findViewById(R.id.unlock_layout);
        mLockTip = (TextView) findViewById(R.id.tv_lock_tip);
        mUnLockText = (TextView) findViewById(R.id.unlock_text);
        mNumPoint_1 = (ImageView) findViewById(R.id.num_point_1);
        mNumPoint_2 = (ImageView) findViewById(R.id.num_point_2);
        mNumPoint_3 = (ImageView) findViewById(R.id.num_point_3);
        mNumPoint_4 = (ImageView) findViewById(R.id.num_point_4);
        mNumber_0 = (TextView) findViewById(R.id.number_0);
        mNumber_1 = (TextView) findViewById(R.id.number_1);
        mNumber_2 = (TextView) findViewById(R.id.number_2);
        mNumber_3 = (TextView) findViewById(R.id.number_3);
        mNumber_4 = (TextView) findViewById(R.id.number_4);
        mNumber_5 = (TextView) findViewById(R.id.number_5);
        mNumber_6 = (TextView) findViewById(R.id.number_6);
        mNumber_7 = (TextView) findViewById(R.id.number_7);
        mNumber_8 = (TextView) findViewById(R.id.number_8);
        mNumber_9 = (TextView) findViewById(R.id.number_9);
        mNumberDel = (ImageView) findViewById(R.id.number_del);
        mUnLockIcon = (ImageView) findViewById(R.id.unlock_icon);
        mAdLayout = (RelativeLayout) findViewById(R.id.ad_layout);
        mLockAdContent = (NetworkImageView) findViewById(R.id.lock_ad_content);
        mLockAdDesc = (TextView) findViewById(R.id.lock_ad_desc);
        mLockAdBtn = (TextView) findViewById(R.id.lock_ad_btn);
        mAppLogo = (ImageView) findViewById(R.id.app_logo);
        mAppLabel = (TextView) findViewById(R.id.app_label);
        mAdBgView = (TextView) findViewById(R.id.ad_bg_view);
        mAdBgView.getPaint().setAntiAlias(true);

        mNumber_0.setOnClickListener(this);
        mNumber_1.setOnClickListener(this);
        mNumber_2.setOnClickListener(this);
        mNumber_3.setOnClickListener(this);
        mNumber_4.setOnClickListener(this);
        mNumber_5.setOnClickListener(this);
        mNumber_6.setOnClickListener(this);
        mNumber_7.setOnClickListener(this);
        mNumber_8.setOnClickListener(this);
        mNumber_9.setOnClickListener(this);
        mNumberDel.setOnClickListener(this);
        mIconMore.setOnClickListener(this);

        initData();
        initImageRes();
        loadAdvertisement();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取解锁应用的包名
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        //获取按返回键的操作
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        //获取密码
        lockPwd = SpUtil.getInstance().getString(AppConstants.LOCK_PWD, "");
        packageManager = getPackageManager();
        mLockInfoManager = new CommLockInfoManager(this);
        mPopWindow = new UnLockMenuPopWindow(this, pkgName, false);
        mPresenter = new NumberUnLockPresenter(this);
        numInput = new ArrayList<>();
        pointList = new ArrayList<>(COUNT);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GestureUnlockActivity.FINISH_UNLOCK_THIS_APP);
        mNumberUnlockReceiver = new NumberUnlockReceiver();
        registerReceiver(mNumberUnlockReceiver, filter);

        pointList.add(mNumPoint_1);
        pointList.add(mNumPoint_2);
        pointList.add(mNumPoint_3);
        pointList.add(mNumPoint_4);
        for (ImageView iv : pointList) {
            iv.setImageResource(R.drawable.num_point);
        }
    }

    /**
     * 给应用Icon和背景赋值
     */
    private void initImageRes() {
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (appInfo != null) {
                iconDrawable = packageManager.getApplicationIcon(appInfo);
                appLabel = packageManager.getApplicationLabel(appInfo).toString();
                mUnLockIcon.setImageDrawable(iconDrawable);
                mUnLockText.setText(appLabel);
                mLockTip.setText(getString(R.string.num_create_text_01));
                final Drawable icon = packageManager.getApplicationIcon(appInfo);
                mUnLockLayout.setBackgroundDrawable(icon);
                mUnLockLayout.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {

                            @Override
                            public boolean onPreDraw() {
                                mUnLockLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                                mUnLockLayout.buildDrawingCache();
                                Bitmap bmp = LockUtil.drawableToBitmap(icon, mUnLockLayout);
                                LockUtil.blur(NumberUnlockActivity.this, LockUtil.big(bmp), mUnLockLayout);
                                return true;
                            }
                        });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
                mLockTip.setVisibility(View.VISIBLE);
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
            mLockTip.setVisibility(View.GONE);
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
            mLockTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number_0:
            case R.id.number_1:
            case R.id.number_2:
            case R.id.number_3:
            case R.id.number_4:
            case R.id.number_5:
            case R.id.number_6:
            case R.id.number_7:
            case R.id.number_8:
            case R.id.number_9:
                clickNumber((TextView) v);
                break;
            case R.id.number_del:
                deleteNumber();
                break;
            case R.id.btn_more:
                mPopWindow.showAsDropDown(mIconMore);
                break;
        }
    }

    private void clickNumber(TextView btn) {
        mPresenter.clickNumber(numInput, pointList, btn.getText().toString(), lockPwd);
    }

    private void deleteNumber() {
        if (numInput.size() == 0) {
            return;
        }
        pointList.get(numInput.size() - 1).setImageResource(R.drawable.num_point);
        numInput.remove(numInput.size() - 1);
    }

    /**
     * 解锁成功
     */
    @Override
    public void unLockSuccess() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
            startActivity(new Intent(NumberUnlockActivity.this, LockMainActivity.class));
        } else {
            SpUtil.getInstance().putString(AppConstants.LOCK_LAST_LOAD_PKG_NAME, pkgName);//记录解锁包名
            SpUtil.getInstance().putLong(AppConstants.LOCK_CURR_MILLISENCONS, System.currentTimeMillis()); //记录解锁时间

            Intent intent = new Intent(LockService.UNLOCK_ACTION);
            intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis());
            intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, pkgName);
            sendBroadcast(intent);

            mLockInfoManager.unlockCommApplication(pkgName);
            LockUtil.getInstance().setLastUnlockPkg(pkgName);
        }
        finish();
    }

    /**
     * 解锁失败
     */
    @Override
    public void unLockError(int retryNum) {
        clearPassword();
        if (retryNum == 0) {

        }
        String format = getResources().getString(R.string.password_error_count);
        String str = String.format(format, retryNum);
//        ToastUtil.showShort(str);
        //mLockTip.setText(str);
        for (ImageView iv : pointList) {
            iv.setImageResource(R.drawable.number_point_error);
        }
    }

    @Override
    public void clearPassword() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (ImageView iv : pointList) {
                    iv.setImageResource(R.drawable.num_point);
                }
            }
        }, 2000);
    }

    @Override
    public void setNumberPointImageResource(List<String> numInput) {
        int index = 0;
        for (ImageView iv : pointList) {
            if (index++ < numInput.size()) {
                iv.setImageResource(R.drawable.num_point_check);
            } else {
                iv.setImageResource(R.drawable.num_point);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
            LockUtil.goHome(this);
           // SecurityApplication.getInstance().clearAllActivity();
        } else if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
            finish();
        } else {
            startActivity(new Intent(this, LockMainActivity.class));
        }
    }

    private class NumberUnlockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FINISH_UNLOCK_THIS_APP)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNumberUnlockReceiver);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

}
