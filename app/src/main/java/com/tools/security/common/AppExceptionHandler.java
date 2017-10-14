package com.tools.security.common;

import android.content.Context;

import com.orhanobut.logger.Logger;

/**
 * description: 系统异常处理
 * author: xiaodifu
 * date: 2016/7/8.
 */
public class AppExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static AppExceptionHandler mInstance = new AppExceptionHandler();

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Context mContext;

    private AppExceptionHandler() {

    }

    public static AppExceptionHandler getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.e(ex,"uncatchexception");
        SecurityApplication.getInstance().saveLog(ex);
        SecurityApplication.getInstance().restartApp();
    }
}
