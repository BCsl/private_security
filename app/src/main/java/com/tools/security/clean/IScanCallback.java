package com.tools.security.clean;


import com.tools.security.bean.FileCacheBean;
import com.tools.security.bean.FileCacheGroup;

import java.util.List;

/**
 * Author：wushuangshuang on 16/10/27 14:53
 * Function：扫描回调接口
 */
public interface IScanCallback {
    public void onBegin(); // 开始扫描
    public void onProgress(FileCacheBean bean); // 进行中
    public void onFinish(List<FileCacheBean> bean); // 扫描结束
    public void onFinish(FileCacheGroup fileCacheGroup); // 扫描结束
}
