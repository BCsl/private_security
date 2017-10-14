package com.tools.security.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 网络工具类
 *
 * @author hogan
 */
public class NetworkUtil {
    //LogTag
    private static final String LOG_TAG = "appcenter_network";
    //==============================网络类型==========================
    //未知网络
    public static final int NETWORK_TYPE_UNKOWN = 0x00;
    //wifi网络
    public static final int NETWORK_TYPE_WIFI = 0x01;
    //2G网络
    public static final int NETWORK_TYPE_2G = 0x02;
    //3G网络
    public static final int NETWORK_TYPE_3G = 0x03;
    //4G网络
    public static final int NETWORK_TYPE_4G = 0x04;
    //其他网络，如热点、代理、以太网等.
    public static final int NETWORK_TYPE_OTHER = 0x05;

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        int type = NETWORK_TYPE_UNKOWN;
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        try {
            info = connectMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info != null) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //0.Wi-Fi网络
                    type = NETWORK_TYPE_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //======================1.移动网络========================
                    switch (info.getSubtype()) {
                        //=======================2G网络=========================
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 1，移动和联通2G
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 2，移动和联通2G
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 4，电信2G
                        case TelephonyManager.NETWORK_TYPE_1xRTT: // 7，电信2G
                            type = NETWORK_TYPE_2G;
                            break;
                        //========================3G网络========================
                        case TelephonyManager.NETWORK_TYPE_UMTS: // 3，联通3G
                        case TelephonyManager.NETWORK_TYPE_EVDO_0: // 5，电信3G
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 6，电信3G
                        case TelephonyManager.NETWORK_TYPE_HSDPA: // 8，联通3G
                        case TelephonyManager.NETWORK_TYPE_HSUPA: // 9，HSUPA
                        case TelephonyManager.NETWORK_TYPE_HSPA: // 10，HSPA
                        case TelephonyManager.NETWORK_TYPE_IDEN: // 11，IDEN，集成数字增强型网络
                        case 14/*TelephonyManager.NETWORK_TYPE_EHRPD*/: // 14，3G网络
                        case 15/*TelephonyManager.NETWORK_TYPE_HSPAP*/: // 15，HSPAP，联通3.5G
                            type = NETWORK_TYPE_3G;
                            break;
                        //========================4G网络========================
                        case 13/*TelephonyManager.NETWORK_TYPE_LTE*/:
                            //13---4G网络
                            type = NETWORK_TYPE_4G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            //0----未知网络
                            type = NETWORK_TYPE_UNKOWN;
                            break;
                        default:
                            type = NETWORK_TYPE_UNKOWN;
                            break;
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE_MMS:
                    //2.运营商的多媒体消息服务
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    //3.平面定位特定移动数据连接
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    //4.运营商热点网络
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    //5.高优先级的移动数据连接。
                case ConnectivityManager.TYPE_WIMAX:
                    //6.WIMAX网络
                case 7/*ConnectivityManager.TYPE_BLUETOOTH*/:
                    //7.蓝牙连接
                case 8/*ConnectivityManager.TYPE_DUMMY*/:
                    //8.虚拟连接
                case 9/*ConnectivityManager.TYPE_ETHERNET*/:
                    //9.以太网
                    type = NETWORK_TYPE_OTHER;
                    break;
                default:
                    type = NETWORK_TYPE_UNKOWN;
                    break;
            }
        }
        return type;
    }

    /**
     * 获取当前网络状态::->Wi-Fi\GPRS\3G\4G
     *
     * @param context
     * @return
     */
    public static String buildNetworkState(Context context) {
        String ret = "unknown";
        //获取网络类型
        int networkType = getNetworkType(context);
        if (networkType == NETWORK_TYPE_UNKOWN) {
            //未知网络
            ret = "unknown";
        } else if (networkType == NETWORK_TYPE_WIFI) {
            //wifi网络
            ret = "wifi";
        } else if (networkType == NETWORK_TYPE_2G) {
            //2G网络
            ret = "2g";
        } else if (networkType == NETWORK_TYPE_3G) {
            //3G网络
            ret = "3g";
        } else if (networkType == NETWORK_TYPE_4G) {
            //4G网络
            ret = "4g";
        } else if (networkType == NETWORK_TYPE_OTHER) {
            //其他网络,如热点、代理、以太网等.
            ret = "other";
        }
        return ret;
    }


    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    //是否连接WIFI
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * 是否开启 wifi true：开启 false：关闭
     *
     * @param wifiManager
     * @param context
     * @param isEnable
     */
    public static void setWifi(WifiManager wifiManager, Context context, boolean isEnable) {
        if (wifiManager == null) return;
        if (isEnable) {// 开启wifi
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            // 关闭 wifi
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }


    //获取链接到当前热点的设备IP
    public static ArrayList<String> getConnectedHotIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }
}
