package com.tools.security.utils.statistics;

/**
 * Created by lzx on 2017/1/10.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SpUtil;

/**
 * 3分钟8小时上传统计
 * Created by wushuangshuang on 16/7/4.
 */
public class ScheduleTaskHandler extends BroadcastReceiver {
    private final static long AUTO_CHECK_DELAY = 3 * 60 * 1000; // 启动后3min检查一次更新
    private final static long UPDATE_INTERVAL = 8 * 60 * 60 * 1000; // 相隔8小时触发
    private Context mContext;
    private AlarmManager mAlarmManager;
    private static final String ACTION_SECURITY_INFO = "com.zz.ultra.security.action";
    private static final String KEY_CHECK_TIME = "check_time_key";
    private static ScheduleTaskHandler sInstance;

    ScheduleTaskHandler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ACTION_SECURITY_INFO);
        mContext.registerReceiver(this, filter);
    }

    public static ScheduleTaskHandler getScheduleTaskHandler(Context context) {
        if (sInstance == null) {
            sInstance = new ScheduleTaskHandler(context);
        }

        return sInstance;
    }

    /**
     * 开始启动
     */
    public void startTask() {
        startBasicInfoStaticTask();
    }

    private void startBasicInfoStaticTask() {
        try {
            long now = System.currentTimeMillis();
            long nextCheckTime = UPDATE_INTERVAL; // 下一次上传间隔时间
            long lastCheckUpdate = getLastCheckedTime(KEY_CHECK_TIME); // 上一次的检查时间
            if (lastCheckUpdate == 0L) {
                setLastCheckedTime(KEY_CHECK_TIME, 1L);
                nextCheckTime = AUTO_CHECK_DELAY;
            } else if (lastCheckUpdate == 1L) {
                //3分钟
                SecurityBaseInfoStaticOperator.uploadBaseInfo(mContext);
                setLastCheckedTime(KEY_CHECK_TIME, System.currentTimeMillis());
                nextCheckTime = UPDATE_INTERVAL;
            } else if ((now - lastCheckUpdate >= UPDATE_INTERVAL) || (now - lastCheckUpdate <= 0L)) {
                // 八小时
                SecurityBaseInfoStaticOperator.uploadBaseInfo(mContext);
                // 保存本次检查的时长
                setLastCheckedTime(KEY_CHECK_TIME, now);
            } else {
                // 动态调整下一次的间隔时间
                nextCheckTime = UPDATE_INTERVAL - (now - lastCheckUpdate);
            }

            final long tiggertTime = System.currentTimeMillis() + nextCheckTime;
            Intent updateIntent = new Intent(ACTION_SECURITY_INFO);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, updateIntent, 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
            updateIntent = null;
            pendingIntent = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getLastCheckedTime(String key) {
        return SpUtil.getInstance().getLong(key);
    }

    private void setLastCheckedTime(String key, long checkedTime) {
        SpUtil.getInstance().putLong(key, checkedTime);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (AppUtils.isConnected(context)) {
                startBasicInfoStaticTask();
            }
        } else if (action.equals(ACTION_SECURITY_INFO)) {
            startBasicInfoStaticTask();
        }
    }
}
