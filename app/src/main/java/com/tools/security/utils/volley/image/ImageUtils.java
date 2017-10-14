package com.tools.security.utils.volley.image;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.volley.request.RequestManager;


public class ImageUtils {

    /**
     * 显示图片
     *
     * @param url
     * @param view
     * @param context
     */
    public static void display(String url, ImageView view, Context context) {
        RequestManager.getImageLoader().get(StringUtil.nullToString(url), new FadeInImageListener(view, context));
    }

    /**
     * 自定义错误图和默认图的显示图片
     *
     * @param url
     * @param view
     * @param context
     * @param errorImage   错误图资源id ，0表示不传
     * @param defalutImage 默认图资源id ，0表示不传
     */
    public static void display2(String url, ImageView view, Context context, int errorImage, int defalutImage) {
        RequestManager.getImageLoader().get(StringUtil.nullToString(url), new FadeInImageListener(view, context, errorImage, defalutImage));
    }

    //用NetworkImageView的方式加载
    public static void displayNet1(String url, NetworkImageView mView) {
        mView.setImageUrl(StringUtil.nullToString(url), RequestManager.getImageLoader());
    }

    //用NetworkImageView的方式加载
    public static void displayNet2(String url, NetworkImageView mView,  int errorImage, int defalutImage) {
        mView.setErrorImageResId(errorImage);
        mView.setDefaultImageResId(defalutImage);
        mView.setImageUrl(StringUtil.nullToString(url), RequestManager.getImageLoader());
    }

}
