package com.tools.security.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.tools.security.service.ServicePkgChange2;


/**
 * description: 应用安装卸载监听
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class PkgChangeReceiver2 extends WakefulBroadcastReceiver {
    public PkgChangeReceiver2() {
    }

    public void onReceive(Context var1, Intent var2) {
        (var2 = new Intent(var2)).setClass(var1, ServicePkgChange2.class);
        var1.startService(var2);
    }
}

