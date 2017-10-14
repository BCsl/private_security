package com.tools.security.utils.statistics;


import android.content.Context;
import android.os.Process;

import com.giftbox.statistic.StatisticsManager;

/**
 * Created by wushuangshuang on 16/6/16.
 * 75协议
 */
public class Operation75Statistic {
    // 布局统计数据分割符
    protected static final String LAYOUT_STATISTICS_DATA_SEPARATE = "|";
    public static final int OPERATION_PROTOCOL_75 = 75; // 基本信息协议号

    /**
     * @param context
     * @param funId  功能点
     */
    public static void uploadSqe75StatisticData(final Context context,final int funId) {
        Thread upload75Thread = new Thread() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                StringBuffer buffer = new StringBuffer();
                StatisticCombinationDataUtils.combinationProtocolAndFunId(buffer, OPERATION_PROTOCOL_75, funId);  // 协议号
                StatisticCombinationDataUtils.getCommonData(context, buffer);  // 公共部分
                StatisticCombinationDataUtils.getOtherData(context, buffer);// 其它
                uploadStatisticData(context, buffer); // 上传统计数据
            }
        };
        upload75Thread.start();
    }



    /**
     * 调用统计SDK上传统计数据
     *
     * @param context
     * @param
     * @param data    不保护基础数据(在SDK中做了处理)的统计数据
     */
    protected static void uploadStatisticData(final Context context, final StringBuffer data, Object... immediately) {
        // 上传统计数据
        StatisticsManager.getInstance(context).upLoadStaticData(toString(data));
    }

    /**
     * 将Object类型转换为String,且去除空格
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim();
    }
}
