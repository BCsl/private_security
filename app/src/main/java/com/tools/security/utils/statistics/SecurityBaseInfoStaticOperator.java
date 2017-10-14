package com.tools.security.utils.statistics;

import android.content.Context;

/**
 * Created by wushuangshuang on 16/7/21.
 */
public class SecurityBaseInfoStaticOperator extends Operation75Statistic {
    public static final int FUNCTION_ID_APPCHANGED = 1; // 功能ID

    /**
     * @param context
     */
    public static void uploadBaseInfo(Context context) {
        uploadSqe75StatisticData(context, FUNCTION_ID_APPCHANGED);
    }


}