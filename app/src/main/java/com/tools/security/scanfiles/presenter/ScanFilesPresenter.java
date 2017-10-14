package com.tools.security.scanfiles.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.avl.engine.AVLScanListener;
import com.tools.security.bean.AvlFileInfo;
import com.tools.security.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2016/12/19.
 * email：386707112@qq.com
 * 功能：
 */

public class ScanFilesPresenter implements ScanFilesContract.Presenter {

    private ScanFilesContract.View mView;
    private SdCardScanAsyncTask sdCardScanAsyncTask;
    private TravelPathAsyncTask pathAsyncTask;
    private int fileCount = 0;
    private int virusCount = 0;
    private Context context;
    private ArrayList<String> filePathList = new ArrayList<>();
    private boolean scanFinished = false;
    //是否用户主动取消了
    private boolean isCanceld = false;
    //最大扫描时间20s
    private int maxScanMillsTime = 20 * 1000;
    //开始扫描系统时间
    private long startMillsTime;
    //当前百分比计数,最大一百
    private int currentPercentIndex = 0;

    private Runnable updateUIRunnable;

    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_UPDATE_VIRUS_COUNT = 2;

    private List<String> sdPathList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    mView.onUpdateProgress("" + currentPercentIndex, currentPercentIndex * 360f / 100);
                    int index=(int) (fileCount * (currentPercentIndex / 100f)) - 1;
                    if (filePathList.size()==0) return;
                    mView.onUpdateText(filePathList.get(index<0?0:index), "" + (int) (fileCount * (currentPercentIndex / 100f)));
                    break;
                case MSG_UPDATE_VIRUS_COUNT:
                    mView.onUpdateVirusCount(msg.arg1);
                    break;
            }
        }
    };

    public ScanFilesPresenter(Context context, ScanFilesContract.View view) {
        this.context = context;
        mView = view;
        sdCardScanAsyncTask = new SdCardScanAsyncTask();
        pathAsyncTask = new TravelPathAsyncTask();

        updateUIRunnable = new Runnable() {
            @Override
            public void run() {
                currentPercentIndex++;
                if (scanFinished) {
                    //逻辑：最大扫描时间20s，如果查杀已经走完了，且查杀时间大于5s，则再走5s结束；如果查杀已经走完，且查杀
                    //时间小于等于5s，则再走10s
                    if ((System.currentTimeMillis() - startMillsTime) > 5 * 1000) {
                        maxScanMillsTime = 5 * 1000;
                    } else {
                        maxScanMillsTime = 10 * 1000;
                    }
                }
                handler.postDelayed(this, maxScanMillsTime / 100);
                handler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
            }
        };
        sdPathList = new ArrayList<String>();
        String outPath = FileUtil.getStoragePath(context, true);
        if (outPath != null) {
            sdPathList.add(outPath);
        }
        String inPath = FileUtil.getStoragePath(context, false);
        if (inPath != null) {
            sdPathList.add(inPath);
        }
    }

    @Override
    public void startScanSdcard() {
        handler.post(updateUIRunnable);
        startMillsTime = System.currentTimeMillis();
        AvlFileInfo.deleteAll(AvlFileInfo.class);
        sdCardScanAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        pathAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        isCanceld = true;
        if (sdCardScanAsyncTask != null && sdCardScanAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            sdCardScanAsyncTask.cancel(true);
            sdCardScanAsyncTask = null;
        }
        if (pathAsyncTask != null && pathAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            pathAsyncTask.cancel(true);
            pathAsyncTask = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        updateUIRunnable = null;
    }

    //扫描SD卡安装包
    private class SdCardScanAsyncTask extends AsyncTask<Context, Integer, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            if (sdPathList.size() == 0) return null;
            AVLEngine.scanDir(params[0], new AVLScanListener() {
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
                            if (isCancelled() || isCanceld) {
                                AVLEngine.stopScan(context);
                                return;
                            }
                            if (avlAppInfo.getDangerLevel() > 0) {
                                new AvlFileInfo(avlAppInfo.getDangerLevel(), avlAppInfo.getVirusName(), avlAppInfo.getPackageName(), avlAppInfo.getAppName(), avlAppInfo.getPath()).save();
                                virusCount++;
                                Message message = new Message();
                                message.what = MSG_UPDATE_VIRUS_COUNT;
                                message.arg1 = virusCount;
                                handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void scanStop() {
                            scanFinished = true;
                            if (sdCardScanAsyncTask!=null)sdCardScanAsyncTask.cancel(true);
                            if (pathAsyncTask!=null)pathAsyncTask.cancel(true);
                        }

                        @Override
                        public void scanFinished() {
                            scanFinished = true;
                            if (pathAsyncTask!=null)pathAsyncTask.cancel(true);
                            mView.refreshResult(virusCount != 0);
                        }

                        @Override
                        public void onCrash() {
                            scanFinished = true;
                            if (sdCardScanAsyncTask!=null)sdCardScanAsyncTask.cancel(true);
                            if (pathAsyncTask!=null)pathAsyncTask.cancel(true);
                        }
                    }
                    , sdPathList);
            return null;
        }
    }

    class TravelPathAsyncTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (sdPathList.size()==1){
                travelPath(new File(sdPathList.get(0)));
            }else if (sdPathList.size()==2){
                travelPath(new File(sdPathList.get(0)));
                travelPath(new File(sdPathList.get(1)));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mView.onUpdateText(values[1], values[0]);
        }

        private void travelPath(File root) {
            if (root == null || !root.exists()) {
                return;
            }
            File[] lists = root.listFiles();
            if (lists == null || lists.length == 0) {
                return;
            }
            for (File file : lists) {
                if (isCancelled()){
                    return;
                }
                if (file.isFile()) {
                    fileCount++;
                    filePathList.add(file.getAbsolutePath() + "/" + file.getName());
                } else {
                    travelPath(file);
                }
            }
        }
    }
}
