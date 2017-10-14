package com.tools.security.utils;

import android.content.pm.ResolveInfo;

import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.CommLockInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/29.
 */

public class DataUtil {

    public static ArrayList<AvlAppInfo> clearRepeatAppInfo(List<AvlAppInfo> appInfos) {
        HashMap<String, AvlAppInfo> hashMap = new HashMap<>();
        for (AvlAppInfo appInfo : appInfos) {
            if (hashMap.containsKey(appInfo.getPackageName())) continue;
            hashMap.put(appInfo.getPackageName(), appInfo);
        }
        ArrayList<AvlAppInfo> appInfos1 = new ArrayList<>();
        for (HashMap.Entry<String, AvlAppInfo> entry : hashMap.entrySet()) {
            appInfos1.add(entry.getValue());
        }
        return appInfos1;
    }


    public static ArrayList<AppWhitePaper> clearRepeatWhitePaper(List<AppWhitePaper> whitePapers) {
        HashMap<String, AppWhitePaper> hashMap = new HashMap<>();
        for (AppWhitePaper appInfo : whitePapers) {
            if (hashMap.containsKey(appInfo.getPkgName())) continue;
            hashMap.put(appInfo.getPkgName(), appInfo);
        }
        ArrayList<AppWhitePaper> appInfos1 = new ArrayList<>();
        for (HashMap.Entry<String, AppWhitePaper> entry : hashMap.entrySet()) {
            appInfos1.add(entry.getValue());
        }
        return appInfos1;
    }

    public static List<CommLockInfo> clearRepeatCommLockInfo(List<CommLockInfo> lockInfos) {
        HashMap<String, CommLockInfo> hashMap = new HashMap<>();
        for (CommLockInfo lockInfo : lockInfos) {
            if (!hashMap.containsKey(lockInfo.getPackageName())) {
                hashMap.put(lockInfo.getPackageName(), lockInfo);
            }
        }
        List<CommLockInfo> commLockInfos = new ArrayList<>();
        for (HashMap.Entry<String, CommLockInfo> entry : hashMap.entrySet()) {
            commLockInfos.add(entry.getValue());
        }
        return commLockInfos;
    }

    public static List<ResolveInfo> clearRepeatResolveInfo(List<ResolveInfo> resolveInfos) {
        HashMap<String, ResolveInfo> hashMap = new HashMap<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (!hashMap.containsKey(resolveInfo.activityInfo.packageName)) {
                hashMap.put(resolveInfo.activityInfo.packageName, resolveInfo);
            }
        }
        List<ResolveInfo> resolveInfoList = new ArrayList<>();
        for (HashMap.Entry<String, ResolveInfo> entry : hashMap.entrySet()) {
            resolveInfoList.add(entry.getValue());
        }
        return resolveInfoList;
    }

}
