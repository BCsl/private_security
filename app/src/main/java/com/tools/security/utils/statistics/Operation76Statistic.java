package com.tools.security.utils.statistics;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.giftbox.statistic.StatisticsManager;

/**
 * Created by lzx on 2017/1/10.
 * 76协议
 */

public class Operation76Statistic {
    // 布局统计数据分割符
    protected static final String LAYOUT_STATISTICS_DATA_SEPARATE = "|";
    public static final int OPERATION_PROTOCOL_76 = 76; // 用户行为协议号


    public static void uploadSqe76StatisticData(final Context context, final int funId, final String optionCode, final String optionResult, final String tablauncherapp) {
        Thread upload76Thread = new Thread() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                StringBuffer buffer76 = new StringBuffer();
                StatisticCombinationDataUtils.combinationProtocolAndFunId(buffer76, OPERATION_PROTOCOL_76, funId);   // 协议号
                StatisticCombinationDataUtils.getCommonData(context, buffer76);// 公共部分
                buffer76.append(optionCode); //操作码  展示
                buffer76.append(LAYOUT_STATISTICS_DATA_SEPARATE);
                buffer76.append(optionResult); //操作结果
                buffer76.append(LAYOUT_STATISTICS_DATA_SEPARATE);
                buffer76.append(tablauncherapp); //Tab任务分类传值
                buffer76.append(LAYOUT_STATISTICS_DATA_SEPARATE);

                uploadStatisticData(context, buffer76);   // 上传统计数据
                Log.e("TAG","上传统计数据");
            }
        };
        upload76Thread.start();
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
