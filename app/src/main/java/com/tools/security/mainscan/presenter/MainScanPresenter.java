package com.tools.security.mainscan.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.avl.engine.AVLScanListener;
import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.BrowserHistory;
import com.tools.security.bean.FileCacheBean;
import com.tools.security.bean.FileCacheGroup;
import com.tools.security.clean.AllSystemTempFileScanTask;
import com.tools.security.clean.IScanCallback;
import com.tools.security.common.AppConstants;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.SecurityApplication;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.BrowserUtils;
import com.tools.security.utils.SpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class MainScanPresenter implements MainScanContract.Presenter {

    private MainScanContract.View view;
    private ScanAllAsyncTask scanAllAsyncTask;
    private LoadBrowserHistory loadBrowserHistory;
    private AllSystemTempFileScanTask logFileScanTask;
    private AllSystemTempFileScanTask apkFileScanTask;
    private AllSystemTempFileScanTask systemTempFileScanTask;
    private Activity activity;
    private static final int UPDATE_PROGRESS_VIRUS = 1;
    private static final int UPDATE_PROGRESS_PRIVACY = 2;
    private static final int UPDATE_PROGRESS_JUNK = 3;
    private static final int UPDATE_FINISHED = 4;
    private boolean avlScanSuccess = false;
    private boolean loadPrivacySuccess = false;
    private boolean loadLogJunkSuccess = false, loadTempJunkSuccess = false, loadApkJunkSuccess = false;
    private double logJunkSize = 0d;
    private double tempJunkSize = 0d;
    private double apkJunkSize = 0d;
    private int privacyCount = 0;

    private int apkcount = 0;
    private int virusCount = 0;

    //自己应用名
    private String currentPackageName;

    private ArrayList<AppWhitePaper> ignoreList = new ArrayList<>();
    private ArrayList<AvlAppInfo> scanedList = new ArrayList<>();
    private int index = 0;
    //最大扫描时间20s
    private int maxCloudScanTime = 20;

    private Runnable updateUiRunnable;
    private Thread updateUiThread;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int progress = (int) msg.arg1;
            String countStr = (String) msg.obj;
            switch (msg.what) {
                case UPDATE_PROGRESS_VIRUS:
                    view.onScanningProgress(progress, countStr, 1);
                    break;
                case UPDATE_PROGRESS_PRIVACY:
                    view.onScanningProgress(progress, countStr, 2);
                    break;
                case UPDATE_PROGRESS_JUNK:
                    view.onScanningProgress(progress, countStr, 3);
                    break;
                case UPDATE_FINISHED:
                    handler.removeCallbacksAndMessages(null);
                    view.onScanningFinished();
                    break;
                default:
                    break;
            }
        }
    };

    public MainScanPresenter(MainScanContract.View view, Activity activity) {
        this.view = view;
        this.activity = activity;
        init();
    }


    @Override
    public void init() {
        ignoreList = (ArrayList<AppWhitePaper>) DataSupport.findAll(AppWhitePaper.class);
        currentPackageName = SecurityApplication.getInstance().getPackageName();
    }

    @Override
    public void start() {
        updateUiRunnable = new Runnable() {
            @Override
            public void run() {

                if (index < 50) {
                    maxCloudScanTime = 20;
                    index++;
                    Message message = new Message();
                    message.what = UPDATE_PROGRESS_VIRUS;
                    message.arg1 = index;
                    message.obj = "" + virusCount;
                    handler.sendMessage(message);
                    if (avlScanSuccess) {
                        maxCloudScanTime = 1;
                    }
                    if (index == 50) {
                        view.onScanningEnd("" + virusCount, 1, virusCount > 0);
                    }
                    handler.postDelayed(updateUiThread, maxCloudScanTime * 50);
                } else if (index < 75) {
                    maxCloudScanTime = 2;
                    index++;
                    Message message = new Message();
                    message.what = UPDATE_PROGRESS_PRIVACY;
                    message.arg1 = index;
                    message.obj = null;
                    handler.sendMessage(message);
                    if (loadPrivacySuccess) {
                        maxCloudScanTime = 1;
                    }
                    if (index == 75) {
                        view.onScanningEnd("" + privacyCount, 2, privacyCount > 0);
                    }
                    handler.postDelayed(updateUiThread, maxCloudScanTime * 50);
                } else if (index < 100) {
                    maxCloudScanTime = 5;
                    index++;
                    Message message = new Message();
                    message.what = UPDATE_PROGRESS_JUNK;
                    message.arg1 = index;
                    message.obj = null;
                    handler.sendMessage(message);
                    if (loadLogJunkSuccess && loadTempJunkSuccess && loadApkJunkSuccess) {
                        maxCloudScanTime = 1;
                    }
                    if (index == 100) {
                        double totalJunkSize = logJunkSize + tempJunkSize + apkJunkSize + getRandomAdSize();
                        view.onScanningEnd("" + totalJunkSize, 3, totalJunkSize > 0d);
                    }
                    handler.postDelayed(updateUiThread, maxCloudScanTime * 50);
                } else {
                    Message message = new Message();
                    message.what = UPDATE_FINISHED;
                    message.arg1 = 100;
                    handler.sendMessageDelayed(message, 600);
                }
            }
        };
        updateUiThread=new Thread(updateUiRunnable);
        updateUiThread.start();
        localScan();
        loadNormalAd();
    }

    @Override
    public void localScan() {
        scanAllAsyncTask = new ScanAllAsyncTask();
        scanAllAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), activity);
    }

    @Override
    public void loadBrowser() {
        loadBrowserHistory = new LoadBrowserHistory();
        loadBrowserHistory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, activity);
    }

    @Override
    public void localFileScan() {
        logFileScanTask = new AllSystemTempFileScanTask(new IScanCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onProgress(FileCacheBean bean) {

            }

            @Override
            public void onFinish(List<FileCacheBean> bean) {

            }

            @Override
            public void onFinish(FileCacheGroup fileCacheGroup) {
                logJunkSize = fileCacheGroup.getTotalCacheSize();
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_LOG, fileCacheGroup);
                loadLogJunkSuccess = true;
            }
        }, FileCacheBean.FileCacheType.RESIDUALLOGS);
        apkFileScanTask = new AllSystemTempFileScanTask(new IScanCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onProgress(FileCacheBean bean) {

            }

            @Override
            public void onFinish(List<FileCacheBean> bean) {
            }

            @Override
            public void onFinish(FileCacheGroup fileCacheGroup) {
                apkJunkSize = fileCacheGroup.getTotalCacheSize();
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_APK, fileCacheGroup);
                loadApkJunkSuccess = true;
            }
        }, FileCacheBean.FileCacheType.OBSOLETEAPK);
        systemTempFileScanTask = new AllSystemTempFileScanTask(new IScanCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onProgress(FileCacheBean bean) {

            }

            @Override
            public void onFinish(List<FileCacheBean> bean) {
            }

            @Override
            public void onFinish(FileCacheGroup fileCacheGroup) {
                tempJunkSize = fileCacheGroup.getTotalCacheSize();
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, fileCacheGroup);
                loadTempJunkSuccess = true;
            }
        }, FileCacheBean.FileCacheType.SYSTEMPFILES);

        //三个扫文件的任务，并行处理
        logFileScanTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        apkFileScanTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        systemTempFileScanTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //销毁
    @Override
    public void onDestory() {
        if (scanAllAsyncTask != null && scanAllAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            scanAllAsyncTask.cancel(true);
            scanAllAsyncTask = null;
        }
        if (loadBrowserHistory != null && loadBrowserHistory.getStatus() != AsyncTask.Status.FINISHED) {
            loadBrowserHistory.cancel(true);
            loadBrowserHistory = null;
        }
        if (logFileScanTask != null && logFileScanTask.getStatus() != AsyncTask.Status.FINISHED) {
            logFileScanTask.cancel(true);
            logFileScanTask = null;
        }
        if (apkFileScanTask != null && apkFileScanTask.getStatus() != AsyncTask.Status.FINISHED) {
            apkFileScanTask.cancel(true);
            apkFileScanTask = null;
        }
        if (systemTempFileScanTask != null && systemTempFileScanTask.getStatus() != AsyncTask.Status.FINISHED) {
            systemTempFileScanTask.cancel(true);
            systemTempFileScanTask = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        activity = null;
    }

    @Override
    public void loadNormalAd() {
        AdStaticConstant.ads=null;
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(6);
        BatAdBuild.Builder build = new BatAdBuild.Builder(activity,
                AppConstants.BATMOBI_VIRUS_RESULT_PLACEMENT_ID,
                BatAdType.NATIVE.getType(),
                new IAdListener() {
                    @Override
                    public void onAdLoadFinish(List<Object> obj) {
                        AppUtils.isFacebookAd(obj);
                        AdStaticConstant.ads=obj;
                        AdStaticConstant.normalAdSaveTime=System.currentTimeMillis();
                    }

                    @Override
                    public void onAdError(AdError error) {
                    }

                    @Override
                    public void onAdClosed() {
                    }

                    @Override
                    public void onAdShowed() {
                    }

                    @Override
                    public void onAdClicked() {
                    }
                })
                .setAdsNum(6)
                .setFacebookConfig(config);
        BatmobiLib.load(build.build());
    }

    //获取广告垃圾大小
    private double getRandomAdSize() {
        //随机广告垃圾大小
        double randomAdSize = 0d;
        //应用剩余广告垃圾文件大小(比如说上次扫出来了没有清除)
        double leftAdSize = Double.parseDouble(SpUtil.getInstance().getString(AppConstants.AD_JUNK_SIZE, "0"));
        long lastRandomAd = SpUtil.getInstance().getLong(AppConstants.TIME_LAST_CLEAN_AD, 0);
        if (lastRandomAd != 0) {
            //如果上次随机生成的广告ad时间，已经是两天前，则再次生成
            if (System.currentTimeMillis() > lastRandomAd + 60 * 60 * 24 * 2 * 1000) {
                //随机大小为100-500kb
                randomAdSize = (new Random().nextDouble() * 400 + 100) * 1024;
                lastRandomAd = System.currentTimeMillis();
                SpUtil.getInstance().putLong(AppConstants.TIME_LAST_CLEAN_AD, lastRandomAd);
            }
        } else {
            //从未生成过随机广告垃圾
            lastRandomAd = System.currentTimeMillis();
            SpUtil.getInstance().putLong(AppConstants.TIME_LAST_CLEAN_AD, lastRandomAd);
            randomAdSize = (new Random().nextDouble() * 400 + 100) * 1024;
        }
        SpUtil.getInstance().putString(AppConstants.AD_JUNK_SIZE, (randomAdSize + leftAdSize) + "");
        return randomAdSize + leftAdSize;
    }

    //病毒查杀(已安装列表)
    private class ScanAllAsyncTask extends AsyncTask<Context, String, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            int result=AVLEngine.scanAll(params[0], new AVLScanListener() {
                @Override
                public void scanStart() {
                }

                @Override
                public void scanCount(int i) {
                }

                @Override
                public void scanSingleIng(String s, String s1, String s2) {

                }

                @Override
                public void scanSingleEnd(AVLAppInfo avlAppInfo) {
                    AvlAppInfo appInfo = new AvlAppInfo(avlAppInfo.getDangerLevel(), avlAppInfo.getVirusName(), avlAppInfo.getPackageName(), avlAppInfo.getAppName(), avlAppInfo.getPackageName(), 0);
                    if (isCancelled()) AVLEngine.stopScan(activity);
                    boolean ignored = false;
                    if (avlAppInfo.getDangerLevel() == 1) {
                        //去除白名单
                        Iterator<AppWhitePaper> iterator = ignoreList.iterator();
                        while (iterator.hasNext()) {
                            AppWhitePaper whitePaper = iterator.next();
                            if (whitePaper.getPkgName().equals(avlAppInfo.getPackageName())) {
                                ignored = true;
                                break;
                            }
                        }
                        //添加到危险列表
                        if (ignored) {
                            appInfo.setIgnored(1);
                        } else if (!avlAppInfo.getPackageName().equals(currentPackageName)) {
                            virusCount++;
                        }
                    }

                    if (!avlAppInfo.getPackageName().equals(currentPackageName)) {
                        scanedList.add(appInfo);
                        apkcount++;
                    }
                }


                @Override
                public void scanStop() {
                }

                @Override
                public void scanFinished() {
                    avlScanSuccess = true;
                    DataSupport.deleteAll(AvlAppInfo.class);
                    DataSupport.saveAll(scanedList);
                    loadBrowser();
                }

                @Override
                public void onCrash() {
                }
            }, AVLEngine.IGNORE_FLAG_SYSTEM);
            if (result!=0){
                avlScanSuccess = true;
                loadBrowser();
            }
            return null;
        }
    }


    //加载浏览历史记录
    private class LoadBrowserHistory extends AsyncTask<Context, String, List<BrowserHistory>> {

        @Override
        protected List<BrowserHistory> doInBackground(Context... params) {
            List<BrowserHistory> browserHistories = BrowserUtils.getBrowserHistory(params[0]);
            return browserHistories;
        }

        @Override
        protected void onPostExecute(List<BrowserHistory> browserHistories) {
            super.onPostExecute(browserHistories);
            loadPrivacySuccess = true;
            privacyCount = (browserHistories == null ? 0 : browserHistories.size());
            SpUtil.getInstance().putInt(AppConstants.BROSWER_HISTORY_COUNT, privacyCount);
            localFileScan();
        }
    }

}
