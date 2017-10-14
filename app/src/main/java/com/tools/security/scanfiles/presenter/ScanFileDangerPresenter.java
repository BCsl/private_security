package com.tools.security.scanfiles.presenter;

import com.tools.security.bean.AvlFileInfo;

import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/12.
 */

public class ScanFileDangerPresenter implements ScanFileDangerContract.Presenter {

    private ScanFileDangerContract.View view;

    public ScanFileDangerPresenter(ScanFileDangerContract.View view) {
        this.view = view;
    }

    @Override
    public void loadFile() {
        ArrayList<AvlFileInfo> list = (ArrayList<AvlFileInfo>) AvlFileInfo.findAll(AvlFileInfo.class);
        view.refreshList(list);
    }
}
