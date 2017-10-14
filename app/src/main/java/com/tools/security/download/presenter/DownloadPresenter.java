package com.tools.security.download.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.tools.security.bean.DownloadFile;
import com.tools.security.bean.SafeLevel;
import com.tools.security.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class DownloadPresenter implements DownloadContract.Presenter {

    private DownloadContract.View view;
    private Context context;
    private AsyncTask asyncTask;
    private ArrayList<DownloadFile> pathList;

    public DownloadPresenter(DownloadContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void loadData() {
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                //外置SD卡
                String outPath = FileUtil.getStoragePath(context, true);
                //内置SD卡
                String inPath = FileUtil.getStoragePath(context, false);

                pathList = new ArrayList<>();
                pathList = new ArrayList<>();
                pathList.addAll(addData(outPath));
                pathList.addAll(addData(inPath));
                return pathList;

                /*if (!TextUtils.isEmpty(outPath)) {
                    pathList.addAll(addData(outPath + "/Download", "Download"));//系统浏览器及常见下载位置
                    pathList.addAll(addData(outPath + "/Pictures/Facebook", "Facebook"));//Facebook
                    pathList.addAll(addData(outPath + "/DCIM/Facebook", "Facebook"));//Facebook
                    pathList.addAll(addData(outPath + "/Movies/Instagram", "Instagram"));//Instagram
                    pathList.addAll(addData(outPath + "/Pictures/Instagram", "Instagram"));//Instagram
                    pathList.addAll(addData(outPath + "/DCIM/100PINT/Pins", "Pinterest"));//Pinterest
                }
                if (!TextUtils.isEmpty(inPath)) {
                    pathList.addAll(addData(outPath + "/Download", "Download"));//系统浏览器及常见下载位置
                    pathList.addAll(addData(outPath + "/Pictures/Facebook", "Facebook"));//Facebook
                    pathList.addAll(addData(outPath + "/DCIM/Facebook", "Facebook"));//Facebook
                    pathList.addAll(addData(outPath + "/Movies/Instagram", "Instagram"));//Instagram
                    pathList.addAll(addData(outPath + "/Pictures/Instagram", "Instagram"));//Instagram
                    pathList.addAll(addData(outPath + "/DCIM/100PINT/Pins", "Pinterest"));//Pinterest
                }*/
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                view.refreshData((ArrayList<DownloadFile>) o);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 拼装数据
     *
     * @param path 文件路径
     * @return
     *//*
    private ArrayList<DownloadFile> addData(String path, String from) {
        ArrayList<DownloadFile> list = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    DownloadFile.FileType fileType;
                    SafeLevel safeLevel = SafeLevel.SAFE;
                    if (fileName.endsWith(".apk")) {
                        fileType = DownloadFile.FileType.APK;
                        AVLAppInfo avlAppInfo = AVLEngine.Scan(file.getAbsolutePath());
                        if (avlAppInfo.getDangerLevel() == 1) {
                            safeLevel = SafeLevel.DANGER;
                        }
                    } else if (fileName.toUpperCase().endsWith(".PNG") || fileName.toUpperCase().endsWith(".JPG") || fileName.toUpperCase().endsWith(".JPEG") || fileName.toUpperCase().endsWith(".PNG")) {
                        fileType = DownloadFile.FileType.IMAGE;
                    } else if (fileName.toUpperCase().endsWith(".ZIP") || fileName.toUpperCase().endsWith(".RAR") || fileName.toUpperCase().endsWith(".7Z ") || fileName.toUpperCase().endsWith(".CAB") || fileName.toUpperCase().endsWith(".ISO")) {
                        fileType = DownloadFile.FileType.ZIP;
                    } else {
                        fileType = DownloadFile.FileType.OTHER;
                    }
                    list.add(new DownloadFile(fileType, fileName, file.getAbsolutePath(), from, safeLevel));
                }
            }
        }
        return list;
    }*/

    /**
     * 拼装数据
     *
     * @param path 文件路径
     * @return
     */
    private ArrayList<DownloadFile> addData(String path) {
        ArrayList<DownloadFile> list = new ArrayList<>();
        if (path != null) {
            File[] files = new File(path + "/Download").listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        DownloadFile.FileType fileType;
                        SafeLevel safeLevel = SafeLevel.SAFE;
                        if (fileName.endsWith(".apk")) {
                            fileType = DownloadFile.FileType.APK;
                            AVLAppInfo avlAppInfo = AVLEngine.Scan(file.getAbsolutePath());
                            if (avlAppInfo.getDangerLevel() == 1) {
                                safeLevel = SafeLevel.DANGER;
                            }
                        } else if (fileName.toUpperCase().endsWith(".PNG") || fileName.toUpperCase().endsWith(".JPG") ||fileName.toUpperCase().endsWith(".JPEG")|| fileName.toUpperCase().endsWith(".PNG")) {
                            fileType = DownloadFile.FileType.IMAGE;
                        } else if (fileName.toUpperCase().endsWith(".ZIP") || fileName.toUpperCase().endsWith(".RAR") || fileName.toUpperCase().endsWith(".7Z ") || fileName.toUpperCase().endsWith(".CAB") || fileName.toUpperCase().endsWith(".ISO")) {
                            fileType = DownloadFile.FileType.ZIP;
                        } else {
                            fileType = DownloadFile.FileType.OTHER;
                        }
                        /*FileType fileType, String name, String from, String absPath, SafeLevel safeLevel*/
                        list.add(new DownloadFile(fileType, fileName, "Download",file.getAbsolutePath(), safeLevel));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void onDetory() {
        pathList = null;
        if (asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }
}
