package com.tools.security.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.orhanobut.logger.Logger;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.common.AppConstants;
import com.tools.security.settings.VirusMonitorActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.SystemUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * description:应用卸载，安装，更新的监听服务
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class ServicePkgChange2 extends IntentService {

    public ServicePkgChange2() {
        super("ServicePkgChange2");
    }

    public ServicePkgChange2(String var1) {
        super(var1);
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String schemeSpecificPart;
            PackageInfo packageInfo;
            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                Logger.i("PACKAGE_ADDED");
                schemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                packageInfo = SystemUtil.getPackageInfo(this, schemeSpecificPart);
                if (packageInfo != null) {
                    String pkgname = packageInfo.packageName;
                    if (SystemUtil.isSystemPackage(this, pkgname)) return;
                    AVLAppInfo appScanRes = AVLEngine.Scan(this, pkgname);
                    if (appScanRes != null && appScanRes.getDangerLevel() == 1) {
                        AvlAppInfo avlAppInfo = new AvlAppInfo(appScanRes.getDangerLevel(), appScanRes.getVirusName(), appScanRes.getPackageName(), appScanRes.getAppName(), appScanRes.getPath(),0);
                        String[] des=AVLEngine.getDescriptionByVirusName(this,avlAppInfo.getVirusName());
                        String behavior="";
                        if (des!=null){
                            if (des.length==3){
                                behavior=des[2];
                            }
                        }
                        if (Build.VERSION.SDK_INT >= 23) {
                            SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, avlAppInfo);
                            Intent intent1 = new Intent(ServicePkgChange2.this, VirusMonitorActivity.class);
                            intent1.putExtra("from", 2);
                            intent1.putExtra("behavior",behavior);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ServicePkgChange2.this.startActivity(intent1);
                        } else {
                            SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, avlAppInfo);
                            Intent intent2 = new Intent(ServicePkgChange2.this, VirusMonitorService.class);
                            intent2.putExtra("behavior",behavior);
                            startService(intent2);
                        }
                    }
                    /*PkgInfo pkgInfo = PkgUtils.populatePkgInfo(packageInfo.packageName, packageInfo.applicationInfo.publicSourceDir);
                    if (pkgInfo != null) {
                        List<PkgInfo> pkgInfoList = new ArrayList();
                        pkgInfoList.add(pkgInfo);
                        ScanResult scanResult = this.cloudScanClient.cloudScan(pkgInfoList);
                        List<AppInfo> appInfoList = scanResult.getList();
                        if (appInfoList != null && appInfoList.size() > 0) {
                            final AvlAppInfo appInfo = appInfoList.get(0);
                            if (appInfo != null && !appInfo.isSystemApp() && appInfo.getScore() > 6) {
                                if (Build.VERSION.SDK_INT>=23){
                                    SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, appInfo);
                                    Intent intent1 = new Intent(ServicePkgChange2.this, VirusMonitorActivity.class);
                                    intent1.putExtra("from", 2);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ServicePkgChange2.this.startActivity(intent1);
                                }else {
                                    SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, appInfo);
                                    Intent intent1=new Intent(ServicePkgChange2.this,VirusMonitorService.class);
                                    startService(intent1);
                                }
                            }
                        }
                    }*/
                }
            }
            if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                String dataString = intent.getDataString();
                Logger.i("removed packagename:" + dataString);
                if (dataString != null) {
                    String packageName = dataString.replace("package:", "");
                    ArrayList<AvlAppInfo> dangerList = (ArrayList<AvlAppInfo>) DataSupport.where("ignored = ? and result = ?","0","1").find(AvlAppInfo.class);
                    AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
                    Iterator<AvlAppInfo> infoIterator = dangerList.iterator();
                    boolean hasRemoved = false;
                    while (infoIterator.hasNext()) {
                        AvlAppInfo appInfo = infoIterator.next();
                        if (appInfo.getPackageName().equals(packageName)) {
                            appInfo.delete();
                            infoIterator.remove();
                            appConfig.setProblemCount(appConfig.getProblemCount() == 0 ? 0 : appConfig.getProblemCount() - 1);
                            hasRemoved = true;
                        }
                    }

                    if (dangerList.size() == 0) {
                        if (appConfig.getProblemCount() > 0) {
                            appConfig.setSafeLevel(SafeLevel.SUSPICIOUS);
                        } else {
                            appConfig.setSafeLevel(SafeLevel.SAFE);
                        }
                    } else {
                        appConfig.setSafeLevel(SafeLevel.DANGER);
                    }

                    if (hasRemoved) {
                        int currentCleanedCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_VIRUS_COUNT);
                        SpUtil.getInstance().putInt(AppConstants.CLEANED_VIRUS_COUNT, currentCleanedCount + 1);
                    }
//                    SpUtil.getInstance().putList(AppConstants.DANGER_LIST, dangerList);
                    SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
                    sendBroadcast(new Intent(AppConstants.ACTION_FILTER_REMOVED_VIRUS_APP).putExtra("package_name", packageName));
                }
            }
            if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
//                Logger.i("service replaced");
            }
        }
    }

    //根据包名打开应用
    private void openApk(String packageName) {
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        try {
            intent = packageManager.getLaunchIntentForPackage(packageName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ServicePkgChange2.this.startActivity(intent);
    }
}
