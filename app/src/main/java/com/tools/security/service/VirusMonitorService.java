package com.tools.security.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tools.security.bean.AvlAppInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.dialog.VirusMonitorDialog;

/**
 * description:因为IntentService启动不了弹窗，所以再开一个service专门用来弹警告窗
 * author: xiaodifu
 * date: 2016/12/29.
 */

public class VirusMonitorService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AvlAppInfo appInfo = SpUtil.getInstance().getBean(AppConstants.VIRUS_MONITOR_APP_INFO, AvlAppInfo.class);
        new VirusMonitorDialog(VirusMonitorService.this, 2, appInfo,intent.getStringExtra("behavior"));
        return super.onStartCommand(intent, flags, startId);
    }
}