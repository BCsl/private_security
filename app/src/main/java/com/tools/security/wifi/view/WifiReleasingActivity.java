package com.tools.security.wifi.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.bean.WifiReleaseApp;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.service.MyAccessibilityService;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * description:正在释放带宽的界面
 * author: xiaodifu
 * date: 2017/1/14.
 */

public class WifiReleasingActivity extends BaseActivity implements View.OnClickListener, WifiReleasingLayout.IOnFinishCallback {

    private PackageManager mPackageManager;
    private List<Drawable> mAppIconList;
    private List<String> packageNameList = new ArrayList<>();
    private WifiReleasingLayout wifiReleasingLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_releasing;
    }

    @Override
    protected void init() {
        SpUtil.getInstance().putLong(AppConstants.LAST_RELEASE_BANDWIDTH_TIME, System.currentTimeMillis());
        Bundle bundle = getIntent().getExtras();
        ArrayList<WifiReleaseApp> wifiReleaseApps = bundle.getParcelableArrayList("list");
        mPackageManager = getPackageManager();
        mAppIconList = new ArrayList<>();
        try {
            for (WifiReleaseApp wifiReleaseApp : wifiReleaseApps) {
                ApplicationInfo appInfo = mPackageManager.getApplicationInfo(wifiReleaseApp.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                mAppIconList.add(mPackageManager.getApplicationIcon(appInfo));
                packageNameList.add(appInfo.packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        wifiReleasingLayout = new WifiReleasingLayout(WifiReleasingActivity.this, mAppIconList, this);
        wifiReleasingLayout.startAnim();
        startKillApp();
        loadAd();
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

    private void startKillApp() {
        for (String packageName : packageNameList) {
            if (AppUtils.isAppAlive(this, packageName)) {
                MyAccessibilityService.INVOKE_TYPE = MyAccessibilityService.TYPE_KILL_APP;
                Intent killIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri packageURI = Uri.parse("package:" + packageName);
                killIntent.setData(packageURI);
                startActivity(killIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onHomeClick() {
        wifiReleasingLayout = null;
        super.onHomeClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                onHomeClick();
                break;
        }
    }

    @Override
    public void onFinish() {
        Bundle bundle = new Bundle();
        int releasedAppNum = packageNameList.size();
        CommonResult commonResult = null;
        if (releasedAppNum == 1){
            commonResult = new CommonResult(getString(R.string.excellent), releasedAppNum+ " app cleaned", FunctionAd.RELEASING, ScreenUtil.dip2px(this, 76f));
        }else {
            commonResult = new CommonResult(getString(R.string.excellent), releasedAppNum+ " apps cleaned", FunctionAd.RELEASING, ScreenUtil.dip2px(this, 76f));
        }

        bundle.putSerializable("result", commonResult);
        startActivity(new Intent(this, CommonResultActivity.class).putExtras(bundle));
        finish();
    }
}
