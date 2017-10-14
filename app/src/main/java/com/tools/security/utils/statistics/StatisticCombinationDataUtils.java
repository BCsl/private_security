package com.tools.security.utils.statistics;

import android.content.Context;

import com.tools.security.common.AppConstants;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取上传数据的工具类
 *
 * @author Administrator
 */
public class StatisticCombinationDataUtils {

    // 统计数据分割符
    protected static final String LAYOUT_STATISTICS_DATA_SEPARATE = "|";

    /**
     * 组合公共部分的方法
     *
     * @param mContext
     * @param buffer
     */
    public static void getCommonData(Context mContext, StringBuffer buffer) {

        buffer.append(AppStatisticsUtils.getBeiJinTime(System.currentTimeMillis()));
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppConstants.BATMOBI_APPKEY);   //appkey
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        String channel = SpUtil.getInstance().getString(AppConstants.MANIFEST_CHANNEL);
        if (channel == null) {
            channel = "";
        }
        buffer.append(channel);
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getAndroidId(mContext)); //设备id
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppUtils.getGoogleAdvertisingId(mContext));
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getCountry(mContext)); //国家
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getLauguage(mContext)); //语言
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getPhoneCode());// 手机的版本号
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getPhoneName());// 手机的版本名
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getAppVersionCode(mContext));// 产品版本号
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getAppVersion(mContext));// 产品版本名
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append("");// sdk的版本号
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append("");// sdk的版本名
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(groupDynamicInfo(mContext));
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
    }

    private static String groupDynamicInfo(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put("cpu", AppStatisticsUtils.getProcessCpuRate());
            json.put("men", AppStatisticsUtils.getMemoryRate(context));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 组合其他部分的方法
     *
     * @param mContext
     * @param buffer
     */
    public static void getOtherData(Context mContext, StringBuffer buffer) {
        buffer.append(AppStatisticsUtils.getCPU()); //cpu
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getTotalMemory()); //内存
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getRomSpace(mContext)); //手机容量
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppUtils.buildNetworkState(mContext)); //网络类型
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getCarrier(mContext)); //运营商
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getTZ(mContext)); //时区
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getIMEI(mContext)); //imei
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getIMSI(mContext)); //imsi
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getScreenSize(mContext)); //手机分辨率
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getModelName()); //机型
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getLongitude(mContext) + ","
                + AppStatisticsUtils.getLatitude(mContext));  //经纬度
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.getGmail(mContext));// gmail
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(AppStatisticsUtils.isAppExist(mContext,
                AppStatisticsUtils.MARKET_PACKAGE) ? 1 : "");// GooglePlay
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
    }


    /**
     * 组装协议号和功能号
     *
     * @param buffer
     * @param protocol
     * @param funId
     */
    public static void combinationProtocolAndFunId(StringBuffer buffer,
                                                   int protocol, int funId) {
        buffer.append(protocol);
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
        buffer.append(funId);
        buffer.append(LAYOUT_STATISTICS_DATA_SEPARATE);
    }
}
