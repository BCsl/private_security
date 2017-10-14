package com.tools.security.wifi.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.util.Log;

import com.tools.security.bean.WifiReleaseApp;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SystemUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/14.
 */

public class WifiReleasePresenter implements WifiReleaseContract.Presenter {

    private WifiReleaseContract.View view;
    private Context context;
    private LoadReleaseAsyncTask asyncTask;

    public WifiReleasePresenter(WifiReleaseContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void loadAppFlow() {
        asyncTask = new LoadReleaseAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    @Override
    public void onDestory() {
        if (asyncTask!=null){
            if (asyncTask.getStatus()== AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
            }
            asyncTask=null;
        }
    }


    class LoadReleaseAsyncTask extends AsyncTask<Context, Void, ArrayList<WifiReleaseApp>> {
        @Override
        protected ArrayList<WifiReleaseApp> doInBackground(Context... params) {
            ArrayList<WifiReleaseApp> wifiReleaseApps = new ArrayList<>();
            List<PackageInfo> list = SystemUtil.getLocalAppsPkgInfo(context);
            String currentAppPackagename = params[0].getPackageName();
            for (PackageInfo packageInfo : list) {
                if (isCancelled()) return null;
                if (packageInfo.packageName.equals(currentAppPackagename)) continue;
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = params[0].getPackageManager().getApplicationInfo(packageInfo.packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
                int uid = applicationInfo.uid;
                long tx = TrafficStats.getUidTxBytes(uid);
                long rx = TrafficStats.getUidRxBytes(uid);
                if (tx+rx > 0) {
                    wifiReleaseApps.add(new WifiReleaseApp(packageInfo.packageName, tx+rx, true));
                }else {
                    long total=getTotalBytesManual(uid);
                    if (total>0){
                        wifiReleaseApps.add(new WifiReleaseApp(packageInfo.packageName, total, true));
                    }
                }
            }

            Collections.sort(wifiReleaseApps);
            return wifiReleaseApps;
        }

        @Override
        protected void onPostExecute(ArrayList<WifiReleaseApp> wifiReleaseApps) {
            if (isCancelled()) return;
            if (wifiReleaseApps == null) return;
            if (wifiReleaseApps.size() > 10) {
                ArrayList<WifiReleaseApp> apps = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    apps.add(wifiReleaseApps.get(i));
                }
                view.refresh(apps);
            } else {
                view.refresh(wifiReleaseApps);
            }
        }
    }

    private Long getTotalBytesManual(int localUid) {

        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        try {
            if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
                return 0L;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }

        File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
        File uidActualFileReceived = new File(uidFileDir, "tcp_rcv");
        File uidActualFileSent = new File(uidFileDir, "tcp_snd");

        String textReceived = "0";
        String textSent = "0";

        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String receivedLine;
            String sentLine;

            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
            }
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(textReceived).longValue() + Long.valueOf(textReceived).longValue();
    }

}
