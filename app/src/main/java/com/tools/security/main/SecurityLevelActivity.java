package com.tools.security.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.SecurityLevel;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.helper.HeaderViewRecyclerAdapter;
import com.tools.security.main.adapter.SecurityLevelAdapter;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.dialog.WifiPermissionsDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhizhen on 2017/1/22.
 */

public class SecurityLevelActivity extends BaseActivity implements View.OnClickListener {

    private HeaderViewRecyclerAdapter headerViewRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEnableAll;
    private ImageView mImgLevelOne, mImgLevelTwo, mImgLevelThree;
    private TextView mSafeLevelTitle, mSafeLevelDesc;
    private List<ImageView> mImageViewList;
    private SecurityLevelAdapter securityLevelAdapter;
    private List<SecurityLevel> mSecurityLevelList;
    private int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    private int RESULT_ACTION_MANAGE_OVERLAY_PERMISSION = 2;
    private int RESULT_ACTION_ACCESSIBILITY_SETTINGS = 3;
    private int RESULT_ACTION_NOTIFICATION_LISTENER_SETTINGS = 4;
    private int safeLevel = 0;
    private boolean isHasUsageAccess = true; //手机是否有UsageAccess权限
    private boolean isHasNotification = true;
    private boolean isClickEnableAll = false;
    private String whoRequestDialog = "";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_security_level;
    }

    @Override
    protected void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEnableAll = (TextView) findViewById(R.id.btn_enable_all);

        mSecurityLevelList = getSecurityLevelList();

        securityLevelAdapter = new SecurityLevelAdapter(this, mSecurityLevelList);
        headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(securityLevelAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View header = LayoutInflater.from(this).inflate(R.layout.layout_security_level_header, mRecyclerView, false);
        mImgLevelOne = (ImageView) header.findViewById(R.id.img_level_one);
        mImgLevelTwo = (ImageView) header.findViewById(R.id.img_level_two);
        mImgLevelThree = (ImageView) header.findViewById(R.id.img_level_three);
        mSafeLevelTitle = (TextView) header.findViewById(R.id.tv_safe_level_title);
        mSafeLevelDesc = (TextView) header.findViewById(R.id.tv_safe_level_desc);

        if (isHasNotification && !isHasUsageAccess) {
            mImgLevelThree.setVisibility(View.GONE);
        }
        if (!isHasNotification && isHasUsageAccess) {
            mImgLevelThree.setVisibility(View.GONE);
        }
        if (!isHasNotification && !isHasUsageAccess) {
            mImgLevelTwo.setVisibility(View.GONE);
            mImgLevelThree.setVisibility(View.GONE);
        }

        mImageViewList = new ArrayList<>();
        mImageViewList.add(mImgLevelOne);
        mImageViewList.add(mImgLevelTwo);
        mImageViewList.add(mImgLevelThree);

        initLevel();
        safeLevel = SpUtil.getInstance().getInt(AppConstants.APP_SAFE_LEVEL);
        updateSafeLevelUI();

        headerViewRecyclerAdapter.addHeaderView(header);
        mRecyclerView.setAdapter(headerViewRecyclerAdapter);

        mEnableAll.setOnClickListener(this);
        securityLevelAdapter.setOnItemClickListener(new SecurityLevelAdapter.OnItemClickListener() {
            @Override
            public void onAccessibilityItemClick(SecurityLevel level, int position, int index) {
                gotoAccessibilitySettings();
            }

            @Override
            public void onNotificationItemClick(SecurityLevel level, int position, int index) {
                gotoNotificationSettings();
            }

            @Override
            public void onUsageAccessItemClick(SecurityLevel level, int position, int index) {
                gotoUsageAccessSettings(); //有悬浮窗权限转跳到授权界面
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isAccessibilitySettingsOn = SystemUtil.isAccessibilitySettingsOn(SecurityLevelActivity.this);
        boolean isNotificationSettingOn = SystemUtil.isNotificationSettingOn(SecurityLevelActivity.this);
        boolean isStatAccessPermissionSet = LockUtil.isStatAccessPermissionSet(SecurityLevelActivity.this);

        if (requestCode == RESULT_ACTION_MANAGE_OVERLAY_PERMISSION) { //悬浮窗
            if (isManageOverlay()) {
                if (whoRequestDialog.equals("Accessibility")) {
                    if (!isAccessibilitySettingsOn) {
                        gotoAccessibilitySettings();
                    }
                } else if (whoRequestDialog.equals("Notification")) {
                    if (!isNotificationSettingOn) {
                        gotoNotificationSettings();
                    }
                } else if (whoRequestDialog.equals("UsageAccess")) {
                    if (!isStatAccessPermissionSet) {
                        gotoUsageAccessSettings(); //有悬浮窗权限转跳到授权界面
                    }
                }
            }
        } else if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {  // usage access
            if (isStatAccessPermissionSet) {
                updateListData();
            }
        } else if (requestCode == RESULT_ACTION_ACCESSIBILITY_SETTINGS) {  //辅助功能
            if (isAccessibilitySettingsOn) {
                if (isClickEnableAll) {
                    if (!isNotificationSettingOn) {
                        gotoNotificationSettings();
                    } else {
                        if (isHasUsageAccess) {
                            if (isManageOverlay()) {
                                if (!isStatAccessPermissionSet) {
                                    gotoUsageAccessSettings();
                                }
                            } else {
                                gotoManageOverlaySettings();
                            }
                        }
                    }
                }
                updateListData();
            }
        } else if (requestCode == RESULT_ACTION_NOTIFICATION_LISTENER_SETTINGS) { //通知栏
            if (isNotificationSettingOn) {
                if (isClickEnableAll) {
                    if (isHasUsageAccess) {
                        if (isManageOverlay()) {
                            if (!isStatAccessPermissionSet) {
                                gotoUsageAccessSettings();
                            }
                        } else {
                            gotoManageOverlaySettings();
                        }
                    }
                }
                updateListData();
            }
        }
    }

    /**
     * 初始化等级
     */
    private void initLevel() {
        boolean isAccessibilitySettingsOn = SystemUtil.isAccessibilitySettingsOn(SecurityLevelActivity.this);
        boolean isNotificationSettingOn = SystemUtil.isNotificationSettingOn(SecurityLevelActivity.this);
        boolean isStatAccessPermissionSet = LockUtil.isStatAccessPermissionSet(SecurityLevelActivity.this);
        if (isAccessibilitySettingsOn) {
            safeLevel = safeLevel + 1;
            SpUtil.getInstance().putInt(AppConstants.APP_SAFE_LEVEL, safeLevel);
        }
        if (isNotificationSettingOn) {
            safeLevel = safeLevel + 1;
            SpUtil.getInstance().putInt(AppConstants.APP_SAFE_LEVEL, safeLevel);
        }
        if (isHasUsageAccess) {
            if (isStatAccessPermissionSet) {
                safeLevel = safeLevel + 1;
                SpUtil.getInstance().putInt(AppConstants.APP_SAFE_LEVEL, safeLevel);
            }
        }
    }

    /**
     * 更新所有数据
     */
    private void updateListData() {
        securityLevelAdapter.notifyDataSetChanged();
        safeLevel = safeLevel + 1;
        SpUtil.getInstance().putInt(AppConstants.APP_SAFE_LEVEL, safeLevel);
        updateSafeLevelUI();
    }

    /**
     * 更新头部状态
     */
    private void updateSafeLevelUI() {
        if (safeLevel == 0) {
            for (ImageView view : mImageViewList) {
                view.setImageResource(R.drawable.safe_level_enable_false);
            }
            mSafeLevelTitle.setText(R.string.security_level_insecure);
            mSafeLevelDesc.setText(R.string.security_level_header_tip);
            setBarColor("#F59C2F", R.drawable.bg_orange);
        } else if (safeLevel == 1) {
            updateImage(mImgLevelOne);

            if (!isHasNotification && !isHasUsageAccess) {
                mSafeLevelTitle.setText(R.string.security_level_full_protection);
                mSafeLevelDesc.setText(R.string.security_level_header_tip2);
                mEnableAll.setVisibility(View.GONE);
                setBarColor("#1F90F9", R.drawable.bg_blue);
            } else if ((isHasNotification && !isHasUsageAccess) || (!isHasNotification && isHasUsageAccess)) {
                mSafeLevelTitle.setText(R.string.security_level_basic);
                mSafeLevelDesc.setText(R.string.security_level_header_tip2);
                setBarColor("#F59C2F", R.drawable.bg_orange);
            } else {
                mSafeLevelTitle.setText(R.string.security_level_low);
                mSafeLevelDesc.setText(R.string.security_level_header_tip);
                setBarColor("#F59C2F", R.drawable.bg_orange);
            }
        } else if (safeLevel == 2) {
            updateImage(mImgLevelOne, mImgLevelTwo);
            mSafeLevelDesc.setText(R.string.security_level_header_tip2);
            if ((isHasNotification && !isHasUsageAccess) || (!isHasNotification && isHasUsageAccess)) {
                mSafeLevelTitle.setText(R.string.security_level_full_protection);
                mEnableAll.setVisibility(View.GONE);
                setBarColor("#1F90F9", R.drawable.bg_blue);
            } else {
                mSafeLevelTitle.setText(R.string.security_level_basic);
                setBarColor("#F59C2F", R.drawable.bg_orange);
            }
        } else if (safeLevel == 3) {
            updateImage(mImgLevelOne, mImgLevelTwo, mImgLevelThree);
            mSafeLevelTitle.setText(R.string.security_level_full_protection);
            mSafeLevelDesc.setText(R.string.security_level_header_tip2);
            mEnableAll.setVisibility(View.GONE);
            setBarColor("#1F90F9", R.drawable.bg_blue);
        }
    }

    private void updateImage(ImageView... currView) {
        for (ImageView view : mImageViewList) {
            view.setImageResource(R.drawable.safe_level_enable_false);
        }
        for (ImageView view : currView) {
            view.setImageResource(R.drawable.safe_level_enable_true);
        }
    }

    private void setBarColor(String color, int drawable) {
        StatusBarUtil.setColor(this, Color.parseColor(color),0);
        mToolbar.setBackgroundResource(drawable);
    }

    /**
     * 转跳到辅助功能
     */
    private void gotoAccessibilitySettings() {
        if (isManageOverlay()) {
            new WifiPermissionsDialog(SecurityLevelActivity.this, R.string.wifi_permissions_dialog_title).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_ACCESSIBILITY_SETTINGS);
        } else {
            whoRequestDialog = "Accessibility";
            gotoManageOverlaySettings();
        }
    }

    /**
     * 转跳到通知栏
     */
    private void gotoNotificationSettings() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (isManageOverlay()) {
                new WifiPermissionsDialog(SecurityLevelActivity.this, R.string.notification_permissions_dialog_title).show();
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intent, RESULT_ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                whoRequestDialog = "Notification";
                gotoManageOverlaySettings();
            }
        }
    }

    /**
     * 转跳到 usage access
     */
    private void gotoUsageAccessSettings() {
        if (isManageOverlay()) {
            new WifiPermissionsDialog(SecurityLevelActivity.this, R.string.lock_permissions_dialog_title).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
        } else {
            whoRequestDialog = "UsageAccess";
            gotoManageOverlaySettings();
        }
    }

    /**
     * 转跳到悬浮窗
     */
    private void gotoManageOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivityForResult(intent, RESULT_ACTION_MANAGE_OVERLAY_PERMISSION);
    }

    /**
     * 判断是否有悬浮窗权限
     */
    private boolean isManageOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try{
                if (!Settings.canDrawOverlays(SecurityLevelActivity.this)) { //没悬浮窗权限转跳授权界面
                    return false;
                } else {
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enable_all:
                isClickEnableAll = true;
                boolean isAccessibilitySettingsOn = SystemUtil.isAccessibilitySettingsOn(SecurityLevelActivity.this);
                boolean isNotificationSettingOn = SystemUtil.isNotificationSettingOn(SecurityLevelActivity.this);
                boolean isStatAccessPermissionSet = LockUtil.isStatAccessPermissionSet(SecurityLevelActivity.this);

                if (isAccessibilitySettingsOn && isNotificationSettingOn && isStatAccessPermissionSet) {
                    return;
                } else {
                    if (!isAccessibilitySettingsOn) {
                        gotoAccessibilitySettings();
                        return;
                    }
                    if (!isNotificationSettingOn) {
                        gotoNotificationSettings();
                        return;
                    }
                    if (isHasUsageAccess) {
                        if (!isStatAccessPermissionSet) {
                            gotoUsageAccessSettings();
                        }
                    }
                }
            default:
                break;
        }
    }

    /**
     * 获取数据
     */
    private List<SecurityLevel> getSecurityLevelList() {
        List<SecurityLevel> list = new ArrayList<>();
        String[] titleArray = getResources().getStringArray(R.array.security_level_title_array);
        String[] descArray = getResources().getStringArray(R.array.security_level_desc_array);

        SecurityLevel accessibility = new SecurityLevel(0, titleArray[0], descArray[0]);
        list.add(accessibility);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            isHasNotification = true;
            SecurityLevel notification = new SecurityLevel(1, titleArray[1], descArray[1]);
            list.add(notification);
        }else {
            isHasNotification = false;
        }
        if (LockUtil.isNoOption(SecurityLevelActivity.this)) {
            isHasUsageAccess = true;
            SecurityLevel stataccess = new SecurityLevel(2, titleArray[2], descArray[2]);
            list.add(stataccess);
        }else {
            isHasUsageAccess = false;
        }
        return list;
    }
}
