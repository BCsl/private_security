package com.tools.security.scanfiles.presenter;

import com.tools.security.bean.AvlFileInfo;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/12.
 */

public interface ScanFileDangerContract {
    interface View extends BaseView {
        void refreshList(ArrayList<AvlFileInfo> avlFileInfos);
    }

    interface Presenter extends BasePresenter {
        void loadFile();
    }
}
