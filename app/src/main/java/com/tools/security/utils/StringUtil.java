package com.tools.security.utils;

import android.content.Context;

import com.tools.security.wifi.core.devicescan.IP_MAC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/22.
 */

public class StringUtil {

    /**
     * 将Null转为" "
     *
     * @param s
     * @return
     */
    public static String nullToString(String s) {
        if (s == null) return "";
        return s;
    }


    //传入Byte，返回处理后的想对应大小
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + " B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB";
    }

    public static String getFormatTime(long millisTime) {
        //小于一分钟，返回秒
        if (millisTime < 60 * 1000l) {
            long time = millisTime / 1000l;
            return time > 1 ? time + " seconds" : time + " second";
        }

        //小于一小时，返回分
        if (millisTime < 60 * 60 * 1000l) {
            long time = millisTime / (60 * 1000l);
            return time > 1 ? time + " minutes" : time + " minute";
        }

        //小于一天，显示小时
        if (millisTime < 24 * 60 * 60 * 1000l) {
            long time = millisTime / (60 * 60 * 1000l);
            return time > 1 ? time + " hours" : time + " hour";
        }

        //大于24小时，全部显示天
        long time = millisTime / (24 * 60 * 60 * 1000l);
        return time > 1 ? time + " days" : time + " day";
    }

    public static String[] getFormatSize2(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return new String[]{"" + size, "B"};
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return new String[]{"" + result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), "KB"};
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return new String[]{"" + result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), "MB"};
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return new String[]{"" + result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), "GB"};
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return new String[]{"" + result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString(), "TB"};
    }

    //解决TextVIEW排版自动换行不整齐问题
    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 获取本地(assets目录)JSON文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getLocalJsonFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    context.getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 获取强制停止文案
     * @param context
     * @return
     */
    public static String getForceStopStr(Context context) {
        String str = getLocalJsonFile(context, "lang.json");
        try {
            JSONArray jsonArray = new JSONArray(str);
            JSONObject jsonObject = jsonArray.getJSONObject(1);
            return jsonObject.getString(Locale.getDefault().getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
            return "FORCE STOP";
        }
    }

    /**
     * 获取强制停止OK文案
     * @param context
     * @return
     */
    public static String getForceStopOkStr(Context context) {
        String str = getLocalJsonFile(context, "lang.json");
        try {
            JSONArray jsonArray = new JSONArray(str);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject.getString(Locale.getDefault().getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
            return "OK";
        }
    }


    /**
     * 格式化历史记录列表时间
     */
    public static String getFormatBrowserHistoryTime(long milliseconds) {
        String dayString = "";
        String monthString = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == Calendar.MONDAY) {
            dayString = "Monday,";
        }
        if (day == Calendar.TUESDAY) {
            dayString = "Tuesday,";
        }
        if (day == Calendar.WEDNESDAY) {
            dayString = "Wednesday,";
        }
        if (day == Calendar.THURSDAY) {
            dayString = "Thursday,";
        }
        if (day == Calendar.FRIDAY) {
            dayString = "Friday,";
        }
        if (day == Calendar.SATURDAY) {
            dayString = "Saturday,";
        }
        if (day == Calendar.SUNDAY) {
            dayString = "Sunday,";
        }

        if (month == Calendar.JANUARY) {
            monthString = "January" + dayofMonth;
        }
        if (month == Calendar.FEBRUARY) {
            monthString = "February" + dayofMonth;
        }
        if (month == Calendar.MARCH) {
            monthString = "March" + dayofMonth;
        }
        if (month == Calendar.APRIL) {
            monthString = "April" + dayofMonth;
        }
        if (month == Calendar.MAY) {
            monthString = "May" + dayofMonth;
        }
        if (month == Calendar.JUNE) {
            monthString = "June" + dayofMonth;
        }
        if (month == Calendar.JULY) {
            monthString = "July" + dayofMonth;
        }
        if (month == Calendar.AUGUST) {
            monthString = "August" + dayofMonth;
        }
        if (month == Calendar.SEPTEMBER) {
            monthString = "September" + dayofMonth;
        }
        if (month == Calendar.OCTOBER) {
            monthString = "October" + dayofMonth;
        }
        if (month == Calendar.NOVEMBER) {
            monthString = "November" + dayofMonth;
        }
        if (month == Calendar.DECEMBER) {
            monthString = "December" + dayofMonth;
        }
        return dayString + monthString;
    }


    /**
     * 将字符串三位一分隔
     *
     * @param str
     * @return
     */
    public static String addComma(String str) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(Double.parseDouble(str));
    }

    public static String toMD5(String source) {
        if (null == source || "".equals(source))
            return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            return toHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }


    /**
     * 对IP地址进行排序
     *
     * @param list
     * @return
     */
    public static List<String> sortIpAddress(List<String> list) {

        Map<Double, String> treeMap = new TreeMap<Double, String>();
        for (String ip : list) {
            String[] str = ip.split("\\.");

            double key = Double.parseDouble(str[0]) * 1000000 + Double.parseDouble(str[1]) * 1000
                    + Double.parseDouble(str[2]) + Double.parseDouble(str[3]) * 0.001;
            treeMap.put(key, ip);
        }
        List<String> ret = new ArrayList<String>();
        for (Iterator<Double> it = treeMap.keySet().iterator(); it.hasNext(); ) {
            double key = it.next().doubleValue();
            String value = treeMap.get(key);
            ret.add(value);
        }
        return ret;
    }

    /**
     * 对IP地址进行排序
     * 根据业务需求定规则
     *
     * @param list
     * @return
     */
    public static List<IP_MAC> sortIpMac(List<IP_MAC> list, String localIp, String gateIp) {
        Map<Double, IP_MAC> treeMap = new TreeMap<Double, IP_MAC>();
        String[] locals = localIp.split("\\.");
        double localKey = Double.parseDouble(locals[0]) * 1000000 + Double.parseDouble(locals[1]) * 1000
                + Double.parseDouble(locals[2]) + Double.parseDouble(locals[3]) * 0.001;
        String[] gates = gateIp.split("\\.");

        IP_MAC localIpMac = null;

        double gateKey = Double.parseDouble(gates[0]) * 1000000 + Double.parseDouble(gates[1]) * 1000
                + Double.parseDouble(gates[2]) + Double.parseDouble(gates[3]) * 0.001;
        for (IP_MAC ip_mac : list) {
            String[] str = ip_mac.mIp.split("\\.");

            double key = Double.parseDouble(str[0]) * 1000000 + Double.parseDouble(str[1]) * 1000
                    + Double.parseDouble(str[2]) + Double.parseDouble(str[3]) * 0.001;
            treeMap.put(key, ip_mac);
        }
        List<IP_MAC> ret = new ArrayList<IP_MAC>();
        for (Iterator<Double> it = treeMap.keySet().iterator(); it.hasNext(); ) {
            double key = it.next().doubleValue();
            IP_MAC value = treeMap.get(key);
            if (key == localKey) {
                localIpMac = value;
            }
            ret.add(value);
        }

        if (treeMap.containsKey(localKey) && treeMap.containsKey(gateKey)) {
            int localPosition = ret.indexOf(localIpMac);
            ret.remove(localPosition);
            ret.add(1, localIpMac);
        }
        return ret;
    }


}
