package com.tools.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.view.LockMainActivity;
import com.tools.security.common.SecurityApplication;

/**
 * Created by lzx on 2017/1/9.
 * 应用信息广播
 */

public class CommLockInfoReceiver extends BroadcastReceiver {

    private CommLockInfoManager mManager;

    public CommLockInfoReceiver() {
        mManager = new CommLockInfoManager(SecurityApplication.getInstance());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String packageName = intent.getData().getSchemeSpecificPart();

        switch (action) {
            case Intent.ACTION_PACKAGE_ADDED:  //安装
                mManager.insertCommLockInfoTable(packageName);
                break;
            case Intent.ACTION_PACKAGE_REPLACED:  //替换
                mManager.updateLockPackageName(packageName);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:  //卸载
                mManager.deletCommLockInfo(packageName);
                break;
            case "start_lock_main_activity":
                context.startActivity(new Intent(context, LockMainActivity.class));
                break;
        }
    }


}
