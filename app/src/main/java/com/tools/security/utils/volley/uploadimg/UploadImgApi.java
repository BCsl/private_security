package com.tools.security.utils.volley.uploadimg;


import com.android.volley.Request;
import com.tools.security.utils.volley.request.RequestManager;

import java.io.File;
import java.util.Map;


/**
 * Created by xdf on 2015/9/23.
 */
public class UploadImgApi {
    /**
     * 上传图片接口
     */
    public static void uploadImg(String url, Map<String, String> strParams, Map<String, File> fileParams, ResponseListener listener) {
        Request request = new PostUploadRequest(url, strParams, fileParams, listener);
        RequestManager.addRequest(request, url);
    }
}