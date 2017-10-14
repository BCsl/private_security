package com.tools.security.settings;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.tools.security.R;
import com.tools.security.bean.SettingInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.settings.adapter.SettingAdapter;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;

import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/8.
 */

public class SettingsActivity extends BaseActivity implements SettingAdapter.OnRecyclerViewItemClickListener, SettingAdapter.OnRecycleViewSwitchClickListener {

    private ArrayList<SettingInfo> mList;
    private RecyclerView settingRecyclerView;
    private SettingAdapter settingAdapter;
    //设置中Sharereference的所有的key数组
    private String[] keyArray = {
            AppConstants.DATABASE_UPDATE,
            AppConstants.REAl_TIME_PROTECTION,
            AppConstants.DOWNLOAD_PRODUCTION,
            AppConstants.WEBSITE_PROTECTION,
            AppConstants.STRANGE_WIFI_ALERT,
            AppConstants.RISK_WIFI_ALERT};

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void init() {
        initData();
        settingRecyclerView = (RecyclerView) findViewById(R.id.setting_recycler_view);
        settingAdapter = new SettingAdapter(this, mList);
        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingRecyclerView.setAdapter(settingAdapter);
        settingAdapter.setOnItemClickListener(this);
        settingAdapter.setmOnSwitchClickListener(this);
    }

    private void initData() {
        mList = new ArrayList<>();
//      第一组数据
        String[] titleArray1 = getResources().getStringArray(R.array.anti_irus);
        boolean isDatabaseUpdate = SpUtil.getInstance().getBoolean(AppConstants.DATABASE_UPDATE, true);
        boolean isRealTimeProtection = SpUtil.getInstance().getBoolean(AppConstants.REAl_TIME_PROTECTION, false);
        boolean isDownloadProtection = SpUtil.getInstance().getBoolean(AppConstants.DOWNLOAD_PRODUCTION, true);
        boolean isWebsiteProtection = SpUtil.getInstance().getBoolean(AppConstants.WEBSITE_PROTECTION, true);
        Boolean[] openArray1 = {false, isDatabaseUpdate, isRealTimeProtection, isDownloadProtection, isWebsiteProtection, true};
        for (int i = 0; i < titleArray1.length; i++) {
            SettingInfo settingBean = new SettingInfo(titleArray1[i], openArray1[i]);
            mList.add(settingBean);
        }
//      第二组数据
        String[] titleArray2 = getResources().getStringArray(R.array.wifi_security);
        boolean isStrangeWifiAlert = SpUtil.getInstance().getBoolean(AppConstants.STRANGE_WIFI_ALERT, true);//getPreferenceData(AppConstants.STRANGE_WIFI_ALERT);
        boolean isRiskWifiAlert = SpUtil.getInstance().getBoolean(AppConstants.RISK_WIFI_ALERT, true);//getPreferenceData(AppConstants.RISK_WIFI_ALERT);
        Boolean[] openArray2 = {false, isStrangeWifiAlert, isRiskWifiAlert};
        for (int i = 0; i < titleArray2.length; i++) {
            SettingInfo settingBean = new SettingInfo(titleArray2[i], openArray2[i]);
            mList.add(settingBean);
        }
//      第三组数据
//        String[] titleArray3 = getResources().getStringArray(R.array.privacy_cleaner);
//        Boolean[] openArray3 = {false,false,false,false};
//        for (int i=0;i<titleArray3.length;i++){
//            SettingInfo settingBean = new SettingInfo(titleArray3[i],openArray3[i]);
//            mList.add(settingBean);
//        }
//      第四组数据
        String[] titleArray4 = getResources().getStringArray(R.array.general);
        Boolean[] openArray4 = {false, false, false, false};
        for (int i = 0; i < titleArray4.length; i++) {
            SettingInfo settingBean = new SettingInfo(titleArray4[i], openArray4[i]);
            mList.add(settingBean);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == 5) { //忽略列表
            startActivity(new Intent(this, IgnoreActivity.class));
        }

//        else if (position == mList.size() - 4) { //语言item
//            ToastUtil.showShort("Language");
//        }

        else if (position == mList.size() - 3) { //反馈item
//            feedback();

            startActivity(new Intent(this, FeedbackActivity.class));
        } else if (position == mList.size() - 2) { //评分item
            AppUtils.gotoGoogleMarket(this, AppConstants.GOOGLE_PLAY_URL, true);
        } else if (position == mList.size() - 1) { //关于item
            startActivity(new Intent(this, AboutUsActivity.class));
        }
    }

    @Override
    public void onSwitchCLick(CompoundButton buttonView, boolean isSelected, int position) {
        if (position <= 4) {
            SpUtil.getInstance().putBoolean(keyArray[position - 1], isSelected);
        } else {
            SpUtil.getInstance().putBoolean(keyArray[position - 3], isSelected);
        }
    }

    private void feedback() {
        String subject = "[Security " + SecurityApplication.getInstance().getVersionName() + " android feedback]";
        StringBuilder body = new StringBuilder("Device Brand:" + SecurityApplication.getInstance().getOperatorName());
        body.append("\r\nOs Version:").append(SecurityApplication.getInstance().getOsVersion());
        body.append("\r\n\r\n\r\nSceen Density:").append(SecurityApplication.getInstance().getScreenSize());
        body.append("\r\nVersion:").append(SecurityApplication.getInstance().getVersionName());
        gotoEmail(subject, body.toString(), AppConstants.ULTRA_EMAIL);
    }

    private void gotoEmail(String subject, String body, String... receivers) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receivers);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (!TextUtils.isEmpty(body)) {
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body.toString());
        }
        emailIntent.setType("plain/text");
        startActivity(emailIntent);
    }

}
