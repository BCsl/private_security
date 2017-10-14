package com.tools.security.clean;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.tools.security.bean.RunningAppInfoBean;
import com.tools.security.utils.processutil.ProcessManager;
import com.tools.security.utils.processutil.models.AndroidAppProcess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wushuangshuang on 16/9/8.
 */
public class RecentProcessUtil {

    /**
     * 异步获取正在运行列表
     *
     * @param context
     * @param listener
     */
    public static void getRunnintApps(final Context context, final ObtainRecentAppsListener listener) {
        new AsyncTask<Void, Void, List<RunningAppInfoBean>>() {
            @Override
            protected List<RunningAppInfoBean> doInBackground(Void... params) {
                return getRunningApps2(context);
            }

            @Override
            protected void onPostExecute(List<RunningAppInfoBean> appInfoBeans) {
                if (listener != null) {
                    listener.onObtainRecentAppsFinish(appInfoBeans);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    /**
     * 获取正在运行列表信息
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static List<RunningAppInfoBean> getRunningApps2(Context context) {
        List<RunningAppInfoBean> appInfoBeans = new ArrayList<RunningAppInfoBean>();

        List<ActivityManager.RunningAppProcessInfo> runningApps = getRunningAppInfo(context);
        //5.0以上只返回自己的信息
        if (runningApps == null || runningApps.size() == 1) {
            //尝试读文件方式
            List<RunningAppInfoBean> mRunningAppList = getRunningAppProcessing2(context);
            //如果还是没有,只显示自己的
            if (mRunningAppList != null && mRunningAppList.size() <= 1) {
                String packageName = runningApps.get(0).pkgList[0];
                if (packageName.equals(context.getPackageName())) {
                    return null;
                }
                RunningAppInfoBean bean = new RunningAppInfoBean();
                bean.setPkgName(packageName);
                String appName = RunningAppsUtil.getAppName(context, packageName);
                bean.setName(appName);
                ApplicationInfo applicationInfo = RunningAppsUtil.getAppInfo(context, appName);
                if (applicationInfo != null) {
                    bean.setIcon(applicationInfo.loadIcon(context.getPackageManager()));
                }

                try {
                    String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mRunningAppList.get(0).getmSize());
                    DecimalFormat df = new DecimalFormat("#.0");
                    bean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                    //判断所使用的单位
                    bean.setmMemorySizeUnit(memorySizeWithUnit[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bean.setInWhite(false);
                appInfoBeans.add(bean);
            } else {
                if (mRunningAppList != null && mRunningAppList.size() > 0) {
                    for (RunningAppInfoBean mBean : mRunningAppList) {
                        if (mBean.getPkgName().equals(context.getPackageName())) {
                            continue;
                        }
                        String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mBean.getmSize());
                        DecimalFormat df = new DecimalFormat("#.0");
                        mBean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                        //判断所使用的单位
                        mBean.setmMemorySizeUnit(memorySizeWithUnit[1]);

                        appInfoBeans.add(mBean);
                    }
                }
            }
        } else if (runningApps != null && runningApps.size() > 1) {
            //5.0以下的可以直接拿到
            List<RunningAppInfoBean> mRunningListWithSystem = DeduplicationUtil.removeDuplicateRunningInOrder2(context, runningApps);
            List<RunningAppInfoBean> mRunningList = new ArrayList<>();
            //如果是系统文件或者是本应用则不添加
            for (RunningAppInfoBean mBean : mRunningListWithSystem) {
                ApplicationInfo applicationInfo = RunningAppsUtil.getAppInfo(context,
                        mBean.getPkgName());
                if (RunningAppsUtil.isSystemApp(applicationInfo) || mBean.getPkgName().equals(context.getPackageName())) {
                    continue;
                }
                mRunningList.add(mBean);
            }
            if (mRunningList != null && mRunningList.size() > 0) {
                for (RunningAppInfoBean mBean : mRunningList) {
                    String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mBean.getmSize());
                    DecimalFormat df = new DecimalFormat("#.0");
                    mBean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                    //判断所使用的单位
                    mBean.setmMemorySizeUnit(memorySizeWithUnit[1]);

                    appInfoBeans.add(mBean);
                }
            }
        }
        filterRunningApps(appInfoBeans); // 过滤一些带有空字段的数据
//        filterWhiteListApps(context, appInfoBeans);//过滤掉白名单

        return appInfoBeans;
    }

    /**
     * 获取正在运行列表信息
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static List<RunningAppInfoBean> getRunningApps3(Context context) {
        List<RunningAppInfoBean> appInfoBeans = new ArrayList<RunningAppInfoBean>();

        List<ActivityManager.RunningAppProcessInfo> runningApps = getRunningAppInfo(context);
        //5.0以上只返回自己的信息
        if (runningApps == null || runningApps.size() == 1) {
            //尝试读文件方式
            List<RunningAppInfoBean> mRunningAppList = getRunningAppProcessing2(context);
            //如果还是没有,只显示自己的
            if (mRunningAppList != null && mRunningAppList.size() <= 1) {
                String packageName = runningApps.get(0).pkgList[0];
                if (packageName.equals(context.getPackageName())) {
                    return null;
                }
                RunningAppInfoBean bean = new RunningAppInfoBean();
                bean.setPkgName(packageName);
                String appName = RunningAppsUtil.getAppName(context, packageName);
                bean.setName(appName);
                ApplicationInfo applicationInfo = RunningAppsUtil.getAppInfo(context, appName);
                if (applicationInfo != null) {
                    bean.setIcon(applicationInfo.loadIcon(context.getPackageManager()));
                }

                try {
                    String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mRunningAppList.get(0).getmSize());
                    DecimalFormat df = new DecimalFormat("#.0");
                    bean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                    //判断所使用的单位
                    bean.setmMemorySizeUnit(memorySizeWithUnit[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bean.setInWhite(false);
                appInfoBeans.add(bean);
            } else {
                if (mRunningAppList != null && mRunningAppList.size() > 0) {
                    for (RunningAppInfoBean mBean : mRunningAppList) {
                        if (mBean.getPkgName().equals(context.getPackageName())) {
                            continue;
                        }
                        String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mBean.getmSize());
                        DecimalFormat df = new DecimalFormat("#.0");
                        mBean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                        //判断所使用的单位
                        mBean.setmMemorySizeUnit(memorySizeWithUnit[1]);

                        appInfoBeans.add(mBean);
                    }
                }
            }
        } else if (runningApps != null && runningApps.size() > 1) {
            //5.0以下的可以直接拿到
            List<RunningAppInfoBean> mRunningListWithSystem = DeduplicationUtil.removeDuplicateRunningInOrder2(context, runningApps);
            List<RunningAppInfoBean> mRunningList = new ArrayList<>();
            //如果是系统文件或者是本应用则不添加
            for (RunningAppInfoBean mBean : mRunningListWithSystem) {
                ApplicationInfo applicationInfo = RunningAppsUtil.getAppInfo(context,
                        mBean.getPkgName());
                if (RunningAppsUtil.isSystemApp(applicationInfo) || mBean.getPkgName().equals(context.getPackageName())) {
                    continue;
                }
                mRunningList.add(mBean);
            }
            if (mRunningList != null && mRunningList.size() > 0) {
                for (RunningAppInfoBean mBean : mRunningList) {
                    String[] memorySizeWithUnit = FileUtils.getAppOccupyMemorySizeUint(mBean.getmSize());
                    DecimalFormat df = new DecimalFormat("#.0");
                    mBean.setmMemorySize(df.format(Double.valueOf(memorySizeWithUnit[0])));
                    //判断所使用的单位
                    mBean.setmMemorySizeUnit(memorySizeWithUnit[1]);

                    appInfoBeans.add(mBean);
                }
            }
        }

        return appInfoBeans;
    }


    private static void filterRunningApps(List<RunningAppInfoBean> listBeans) {
//        if (listBeans != null && listBeans.size() > 0) {
//            for (int k = 0; k < listBeans.size(); k++) { // 过滤空字段
//                if (listBeans.get(k).getName() == null ||
//                        listBeans.get(k).getPkgName().equals("")
//                        || listBeans.get(k).equals(" ")
//                        || listBeans.get(k).getPkgName() == null
//                        || listBeans.get(k).getPkgName().equals("")
//                        || listBeans.get(k).getPkgName().equals(" ")) {
//                    listBeans.remove(k);
//                    k--;
//                }
//            }
//
//            List<String> whiteLauncher = Arrays.asList(LocalWhiteConfig.LOCAL_WHITE_LAUNCHER_PACKAGES);
//            List<String> whiteThirdPart = Arrays.asList(LocalWhiteConfig.LOCAL_WHITE_THIRDPART_PACKAGES);
//            for (int m = 0; m < listBeans.size(); m++) { // 过滤本地白名单
//                if (whiteLauncher.contains(listBeans.get(m).getPkgName()) ||
//                        verifyPackageName(listBeans.get(m).getPkgName()) ||
//                        whiteThirdPart.contains(listBeans.get(m).getPkgName())) {
//                    listBeans.remove(m);
//                    m--;
//                }
//            }
//        }
    }


//    private static boolean verifyPackageName(String process) {
//        for (String prefix : LocalWhiteConfig.LOCAL_WHITE_PACKAGES_PREFIX) {
//            if (process.startsWith(prefix)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 5.1以前有效得到当前正在运行的进程
     *
     * @return
     */
    private static List<ActivityManager.RunningAppProcessInfo> getRunningAppInfo(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningInfos = null;
        try {
            runningInfos = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return runningInfos;
    }

    /**
     * 直接读取系统文件夹下proc文件夹，proc下的文件包含进程运行的信息
     *
     * @return
     */
    private static List<AndroidAppProcess> getRunningAppProcessing(Context context) {
        List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();
        DeduplicationUtil.removeDuplicateProcessInOrder(context, processes); // 过滤相同包名

        // 过滤相同的UID
        if (processes != null && processes.size() > 0) {
            for (int j = 0; j < processes.size(); j++) {
                try {
                    int uid = processes.get(j).getPackageInfo(context, 0).applicationInfo.uid;
                } catch (Exception e) {
                    processes.remove(j);
                    j--;
                }
            }
        }

        if (processes != null && processes.size() > 0) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getPackageName().toLowerCase().contains("com.android")) {
                    processes.remove(i);
                    i--;
                }
            }
        }

        return processes;
    }


    /**
     * 直接读取系统文件夹下proc文件夹，proc下的文件包含进程运行的信息
     *
     * @return
     */
    private static List<RunningAppInfoBean> getRunningAppProcessing2(Context context) {
        List<RunningAppInfoBean> mRunningListWithSystem = DeduplicationUtil.removeDuplicateProcessInOrder2(context,
                ProcessManager.getRunningAppProcesses());// 过滤相同包名
        List<RunningAppInfoBean> mRunningList = new ArrayList<>();
        //如果是系统文件或者是本应用则不添加
        PackageManager pm = context.getPackageManager();
        for (RunningAppInfoBean mBean : mRunningListWithSystem) {
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(mBean.getPkgName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(mBean.getName())
                    || mBean.getmSize() <= 0
                    || RunningAppsUtil.isSystemApp(applicationInfo)
                    || mBean.getPkgName().equals(context.getPackageName())) {
                continue;
            } else {
                mRunningList.add(mBean);
            }
        }
        return mRunningList;
    }


    /**
     *
     */


    /**
     *
     */
    public interface ObtainRecentAppsListener {
        public void onObtainRecentAppsFinish(List<RunningAppInfoBean> appInfoBeans);
    }
}
