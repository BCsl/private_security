package com.tools.security.download;

import android.os.FileObserver;
import android.util.Log;

import com.tools.security.bean.DownloadScanPath;

/**
 * description:监听浏览器下载目录变化
 * author: xiaodifu
 * date: 2017/1/8.
 */

public class SdCardDownloadListener extends FileObserver {
    private ICreatedCallback callback;
    private DownloadScanPath downloadScanPath;

    public SdCardDownloadListener(DownloadScanPath downloadScanPath, ICreatedCallback iCreatedCallback) {
        super(downloadScanPath.getPath(), FileObserver.CREATE | FileObserver.MODIFY);
        this.downloadScanPath = downloadScanPath;
        this.callback = iCreatedCallback;
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.CREATE:
                downloadScanPath.setName(path);
                callback.created(downloadScanPath);
                break;
        }
    }

    public interface ICreatedCallback {
        void created(DownloadScanPath downloadScanPath);
    }
}
