package com.tools.security.utils.statistics;

import android.content.Context;

/**
 * Created by wushuangshuang on 16/6/16.
 */
public class SecurityStaticOperator {
    public static final int FUNCTION_ID_APPCHANGED = 1; // 功能ID
    // 操作结果---操作成功
    public final static String OPERATE_SUCCESS = "1";
    // 操作结果---操作失败
    public final static String OPERATE_FAIL = "0";

    /**
     *上传TAB，基于76协议
     *
     * @param context
     * @param optionCode 操作码
     * @param tab 具体tab值
     */
    public static void uploadTab76(Context context, String optionCode, String tab) {
        Operation76Statistic.uploadSqe76StatisticData(context, FUNCTION_ID_APPCHANGED, optionCode, OPERATE_SUCCESS, tab);
    }


}
