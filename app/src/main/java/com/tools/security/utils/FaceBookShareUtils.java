package com.tools.security.utils;

import android.app.Activity;
import android.net.Uri;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by Zhizhen on 2017/1/12.
 */

public class FaceBookShareUtils {


    private Activity mActivity ;
    private ShareDialog shareDialog;
    private ShareLinkContent linkContent;

    /**
     * 构造器
     */
    public FaceBookShareUtils(Activity activity, CallbackManager callbackManager, FacebookCallback facebookCallback){

        this.mActivity = activity ;
        shareDialog = new ShareDialog(mActivity);
        //注册分享状态监听回调接口
        shareDialog.registerCallback(callbackManager, facebookCallback);
    }


    /**
     * 分享
     */
    public  void share(String contentTitle,String desc, String contentUrl) {

        if (shareDialog.canShow(ShareLinkContent.class)) {

             linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(contentTitle)
                        .setContentDescription(desc)
                        .setContentUrl(Uri.parse(contentUrl))
                        .build();
            shareDialog.show(mActivity, linkContent);
        }
    }
}
