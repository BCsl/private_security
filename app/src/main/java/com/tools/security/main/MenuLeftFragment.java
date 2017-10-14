package com.tools.security.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avl.engine.AVLCheckUpdate;
import com.avl.engine.AVLEngine;
import com.avl.engine.AVLUpdateCheckCallBack;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseFragment;
import com.tools.security.scanfiles.view.ScanFilesActivity;
import com.tools.security.settings.FeedbackActivity;
import com.tools.security.settings.SettingsActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.NetworkUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.dialog.CheckingVirusLibDialog;
import com.tools.security.widget.dialog.NormalDialog;
import com.tools.security.widget.dialog.UpdateVirusLibDialog;
import com.tools.security.widget.dialog.VirusLibCheckDialog;
import com.tools.security.wifi.view.WifiMainActivity;

/**
 * description:侧滑左边lan
 * author: xiaodifu
 * date: 2017/1/12.
 */

public class MenuLeftFragment extends BaseFragment implements View.OnClickListener, CheckingVirusLibDialog.ICancelCheckCallback, VirusLibCheckDialog.ICheckCallback {

    //弹窗
    private CheckingVirusLibDialog checkingVirusLibDialog;
    private VirusLibCheckDialog virusLibCheckDialog;
    private NormalDialog networkErrorDialog;

    private AVLCheckUpdate avlCheckUpdate;
    //更新是否取消了
    private boolean checkCanceled = false;

    private TextView securityLevelText, wifiText, applockText, scanFileText, phoneBoostText,
            cleanJunkText, batterySaverText, downloadText, checkVirusLibText, feedbackText,
            rateText, settingsText;

    private RelativeLayout headerLayout;
    private BroadcastReceiver broadcastReceiver;

    private Drawable drawableWarning;
    private Drawable drawableNormal;
    private boolean isHasUsageAccess = false; //手机是否有UsageAccess权限
    private boolean isHasNotification = false;

    private boolean clicked = false;
    private View clickedView;

    @Override
    protected void init(View rootView) {
        initView(rootView);
        registerRecevier();
    }

    private void initView(View rootView) {
        securityLevelText = (TextView) rootView.findViewById(R.id.security_level_text);
        wifiText = (TextView) rootView.findViewById(R.id.text_wifi);
        applockText = (TextView) rootView.findViewById(R.id.text_applock);

        scanFileText = (TextView) rootView.findViewById(R.id.text_scan_file);
        checkVirusLibText = (TextView) rootView.findViewById(R.id.text_check_virus_lib);
        feedbackText = (TextView) rootView.findViewById(R.id.text_feedback);
        rateText = (TextView) rootView.findViewById(R.id.text_rate);
        settingsText = (TextView) rootView.findViewById(R.id.text_settings);
        headerLayout = (RelativeLayout) rootView.findViewById(R.id.layout_header);

        wifiText.setOnClickListener(this);
        applockText.setOnClickListener(this);
        scanFileText.setOnClickListener(this);
        checkVirusLibText.setOnClickListener(this);
        feedbackText.setOnClickListener(this);
        rateText.setOnClickListener(this);
        settingsText.setOnClickListener(this);
        headerLayout.setOnClickListener(this);

        drawableWarning = getResources().getDrawable(R.drawable.security_level_insecure);
        drawableNormal = getResources().getDrawable(R.drawable.security_level_secure);
        drawableWarning.setBounds(0, 0, drawableWarning.getMinimumWidth(), drawableWarning.getMinimumHeight());
        drawableNormal.setBounds(0, 0, drawableNormal.getMinimumWidth(), drawableNormal.getMinimumHeight());
    }

    @Override
    public void onResume() {
        super.onResume();
        int safeLevel = SpUtil.getInstance().getInt(AppConstants.APP_SAFE_LEVEL);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            isHasNotification = true;
        }
        if (LockUtil.isNoOption(getActivity())) {
            isHasUsageAccess = true;
        }
        if (safeLevel == 0) {
            securityLevelText.setText(R.string.security_level_insecure);
            securityLevelText.setCompoundDrawables(drawableWarning, null, null, null);
        } else if (safeLevel == 1) {
            if (!isHasNotification && !isHasUsageAccess) {
                securityLevelText.setText(R.string.security_level_full_protection);
                securityLevelText.setCompoundDrawables(drawableNormal, null, null, null);
            } else if ((isHasNotification && !isHasUsageAccess) || (!isHasNotification && isHasUsageAccess)) {
                securityLevelText.setText(R.string.security_level_basic);
                securityLevelText.setCompoundDrawables(drawableWarning, null, null, null);
            } else {
                securityLevelText.setText(R.string.security_level_low);
                securityLevelText.setCompoundDrawables(drawableWarning, null, null, null);
            }
        } else if (safeLevel == 2) {
            if ((isHasNotification && !isHasUsageAccess) || (!isHasNotification && isHasUsageAccess)) {
                securityLevelText.setText(R.string.security_level_full_protection);
                securityLevelText.setCompoundDrawables(drawableNormal, null, null, null);
            } else {
                securityLevelText.setText(R.string.security_level_basic);
                securityLevelText.setCompoundDrawables(drawableWarning, null, null, null);
            }
        } else if (safeLevel == 3) {
            securityLevelText.setText(R.string.security_level_full_protection);
            securityLevelText.setCompoundDrawables(drawableNormal, null, null, null);
        }
    }

    private void registerRecevier() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConstants.ACTION_FILTER_CHANGE_COLOR)) {
                    switch (intent.getIntExtra("safe_level", 1)) {
                        case 1:
                            headerLayout.setBackgroundResource(R.drawable.bg_blue);
                            break;
                        case 2:
                            headerLayout.setBackgroundResource(R.drawable.bg_orange);
                            break;
                        case 3:
                            headerLayout.setBackgroundResource(R.drawable.bg_red);
                            break;
                    }
                } else if (intent.getAction().equals(AppConstants.ACTION_FILTER_MAIN_DRAWER_CLOSED)) {
                    if (clicked){
                        performClickEvent(clickedView);
                        clicked=false;
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACTION_FILTER_CHANGE_COLOR);
        intentFilter.addAction(AppConstants.ACTION_FILTER_MAIN_DRAWER_CLOSED);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_left_menu;
    }

    @Override
    public void onClick(View v) {
        clicked = true;
        clickedView = v;
        ((MainActivity) getActivity()).closeDrawerLayout();
    }

    private void performClickEvent(View v) {
        switch (v.getId()) {
            case R.id.text_wifi:
                startActivity(new Intent(MenuLeftFragment.this.getActivity(), WifiMainActivity.class));
                break;
            case R.id.text_applock:
                AppUtils.gotoAppLockActivity(getActivity());
                break;

            case R.id.text_scan_file:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_SD_CARD);
                startActivity(new Intent(MenuLeftFragment.this.getActivity(), ScanFilesActivity.class));
                break;
            case R.id.text_feedback:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_FEEDBACK);
                MenuLeftFragment.this.startActivity(new Intent(MenuLeftFragment.this.getActivity(), FeedbackActivity.class));
                break;
            case R.id.text_rate:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_RATE);
                AppUtils.gotoGoogleMarket(MenuLeftFragment.this.getActivity(), AppConstants.GOOGLE_PLAY_URL, true);
                break;
            case R.id.text_check_virus_lib:
                if (NetworkUtil.isConnected(MenuLeftFragment.this.getContext())) {
                    checkVirusLibVersion();
                } else {
                    networkErrorDialog = new NormalDialog(MenuLeftFragment.this.getContext(), new NormalDialog.IOnClickListener() {
                        @Override
                        public void onLeftClick() {
                            networkErrorDialog.superDismiss();
                        }

                        @Override
                        public void onRightClick() {
                            networkErrorDialog.superDismiss();
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    });
                    networkErrorDialog.title(getString(R.string.network_error))
                            .content(getString(R.string.network_error_desc))
                            .left(getString(R.string.cancel_cap)).right(getString(R.string.set))
                            .show();
                }
                break;
            case R.id.text_settings:
                startActivity(new Intent(MenuLeftFragment.this.getActivity(), SettingsActivity.class));
                break;
            case R.id.layout_header:
                startActivity(new Intent(this.getContext(), SecurityLevelActivity.class));
            default:
                break;
        }
    }

    private boolean appExist(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getApplicationInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void checkVirusLibVersion() {
        int checkReuslt = AVLEngine.checkUpdate(new AVLUpdateCheckCallBack() {
            @Override
            public void updateCheckStart() {
                checkingVirusLibDialog = new CheckingVirusLibDialog(MenuLeftFragment.this.getContext(), MenuLeftFragment.this);
                checkingVirusLibDialog.show();
            }

            @Override
            public void updateCheckEnd(AVLCheckUpdate avlCheckUpdate) {

                if (checkCanceled) return;
                MenuLeftFragment.this.avlCheckUpdate = avlCheckUpdate;
                if (checkingVirusLibDialog != null) checkingVirusLibDialog.dismiss();
                if (avlCheckUpdate != null) {
                    virusLibCheckDialog = new VirusLibCheckDialog(avlCheckUpdate, MenuLeftFragment.this, MenuLeftFragment.this.getContext());
                    virusLibCheckDialog.show();
                }
            }
        });
        Log.e("TAG", "checkReuslt=" + checkReuslt);
    }

    @Override
    public void cancelCheck() {
        checkCanceled = true;
    }

    @Override
    public void failedTryAgain() {
        checkVirusLibVersion();
    }

    @Override
    public void update() {
        new UpdateVirusLibDialog(getActivity(), avlCheckUpdate.virusLibSize).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

}
