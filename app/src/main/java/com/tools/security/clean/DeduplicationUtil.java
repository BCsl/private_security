package com.tools.security.clean;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Debug;


import com.orhanobut.logger.Logger;
import com.tools.security.bean.RunningAppInfoBean;
import com.tools.security.utils.processutil.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 对List对象元素去重
 * Created by wushuangshuang on 16/9/8.
 */
public class DeduplicationUtil {
    /**
     * 重写AndroidAppProcess中的hashCode和equals方法
     * 使用set中不能添加重新元素的特性作为判断条件
     * 将不重复的data元素依次放入临时的newlist
     * 循环完毕后，将原始list清空，addAll(newlist)
     *
     * @param list
     * @return
     */
    public static List<AndroidAppProcess> removeDuplicateProcessInOrder(Context context, List<AndroidAppProcess> list) {

        HashSet<AndroidAppProcess> hashSet = new HashSet<AndroidAppProcess>();
        List<AndroidAppProcess> newlist = new ArrayList<AndroidAppProcess>();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            AndroidAppProcess element = (AndroidAppProcess) iterator.next();
            if (hashSet.add(element)) {
                newlist.add(element);
            }
        }

        list.clear();
        list.addAll(newlist);
        return list;
    }

    /**
     * 重写AndroidAppProcess中的hashCode和equals方法
     * 使用set中不能添加重新元素的特性作为判断条件
     * 将不重复的data元素依次放入临时的newlist
     * 循环完毕后，将原始list清空，addAll(newlist)
     * 5.0以后
     *
     * @param list
     * @return
     */
    public static List<RunningAppInfoBean> removeDuplicateProcessInOrder2(Context context, List<AndroidAppProcess> list) {
        PackageManager pm = context.getPackageManager();
//        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        HashMap<String, RunningAppInfoBean> hashMap = new HashMap<String, RunningAppInfoBean>();
//        HashSet<AndroidAppProcess> hashSet = new HashSet<AndroidAppProcess>();
        List<RunningAppInfoBean> mNoDupList = new ArrayList<RunningAppInfoBean>();
//        List<RunningAppInfoBean> mDupList = new ArrayList<RunningAppInfoBean>();
        long startTime = System.currentTimeMillis();
        Logger.e("Start_Time = " + startTime);

        for (AndroidAppProcess process : list) {
            if (process == null) {
                continue;
            }

            String packageName = process.getPackageName();
            RunningAppInfoBean bean = new RunningAppInfoBean();
            bean.setPkgName(packageName);
            bean.setName(RunningAppsUtil.getAppName(context, packageName));
            try {
                bean.setIcon(pm.getApplicationInfo(packageName, 0).loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            bean.setInWhite(false);
            // 占用的内存
//            int[] pids = new int[]{process.pid};
//            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
//            int memorySize = memoryInfo[0].getTotalPss();
            try {
                int memorySize = (int) (process.statm().getResidentSetSize() / 1024);
                bean.setmSize(memorySize);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (hashMap.containsKey(bean.getPkgName())) {
                hashMap.get(bean.getPkgName()).setmSize(bean.getmSize()
                        + hashMap.get(bean.getPkgName()).getmSize());
            } else {
                hashMap.put(bean.getPkgName(), bean);
            }
        }

        if (hashMap.size() > 0) {
            mNoDupList.addAll(hashMap.values());
        }

//        for (AndroidAppProcess element : list) {
//            String packageName = element.getPackageName();
//            RunningAppInfoBean bean = new RunningAppInfoBean();
//            bean.setPkgName(packageName);
//            bean.setName(RunningAppsUtil.getAppName(context, packageName));
//            try {
//                bean.setIcon(pm.getApplicationInfo(packageName, 0).loadIcon(pm));
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            bean.setInWhite(false);
//
//            // 占用的内存
//            int[] pids = new int[]{element.pid};
////            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
//            int memorySize = memoryInfo[0].getTotalPss();
//            bean.setmSize(memorySize);
//
//            if (hashSet.add(element)) {
//                mNoDupList.add(bean);
//            } else {
//                mDupList.add(bean);
//            }
//        }
//        for (RunningAppInfoBean mNoDupBean : mNoDupList) {
//            for (RunningAppInfoBean mDupBean : mDupList) {
//                if (mNoDupBean.getPkgName().equals(mDupBean.getPkgName())) {
//                    mNoDupBean.setmSize(mNoDupBean.getmSize() + mDupBean.getmSize());
//                }
//            }
//        }

        Logger.i("End_cha_Time = " + (System.currentTimeMillis() - startTime));
        return mNoDupList;
    }

    public static List<RunningAppInfoBean> removeDuplicateRunningInOrder(List<RunningAppInfoBean> list) {
        HashSet<RunningAppInfoBean> hashSet = new HashSet<RunningAppInfoBean>();
        List<RunningAppInfoBean> newlist = new ArrayList<RunningAppInfoBean>();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            RunningAppInfoBean element = (RunningAppInfoBean) iterator.next();
            if (hashSet.add(element)) {
                newlist.add(element);
            }
        }
        list.clear();
        list.addAll(newlist);
        return list;
    }


    /**
     * 5.0以前
     *
     * @param context
     * @param list
     * @return
     */
    public static List<RunningAppInfoBean> removeDuplicateRunningInOrder2(Context context, List<ActivityManager.RunningAppProcessInfo> list) {
        HashSet<RunningAppInfoBean> hashSet = new HashSet<RunningAppInfoBean>();
        List<RunningAppInfoBean> mNoDupList = new ArrayList<RunningAppInfoBean>();
        List<RunningAppInfoBean> mDupList = new ArrayList<RunningAppInfoBean>();
        List<RunningAppInfoBean> mAllList = new ArrayList<RunningAppInfoBean>();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            ActivityManager.RunningAppProcessInfo element = (ActivityManager.RunningAppProcessInfo) iterator.next();

            String packageName = element.pkgList[0];
            /*ApplicationInfo applicationInfo = RunningAppsUtil.getAppInfo(context,
                    packageName);*/

            RunningAppInfoBean bean = new RunningAppInfoBean();
            bean.setPkgName(packageName);
            bean.setName(RunningAppsUtil.getAppName(context, packageName));
            PackageManager pm = context.getPackageManager();

            try {
                bean.setIcon(pm.getApplicationInfo(packageName, 0).loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            /*if (applicationInfo != null) {
                bean.setIcon(applicationInfo.loadIcon(context.getPackageManager()));
            }*/
            bean.setInWhite(false);

            // 占用的内存
            int[] pids = new int[]{element.pid};
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
            int memorySize = memoryInfo[0].getTotalPss();
            bean.setmSize(memorySize);

            mAllList.add(bean);

        }

        for (Iterator iterator = mAllList.iterator(); iterator.hasNext(); ) {
            RunningAppInfoBean element = (RunningAppInfoBean) iterator.next();
            if (hashSet.add(element)) {
                mNoDupList.add(element);
            } else {
                mDupList.add(element);
            }
        }

        for (RunningAppInfoBean mNoDupBean : mNoDupList) {
            for (RunningAppInfoBean mDupBean : mDupList) {
                if (mNoDupBean.getPkgName().equals(mDupBean.getPkgName())) {
                    mNoDupBean.setmSize(mNoDupBean.getmSize() + mDupBean.getmSize());
                }
            }
        }

        return mNoDupList;
    }
}
