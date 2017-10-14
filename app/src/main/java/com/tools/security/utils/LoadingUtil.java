package com.tools.security.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.tools.security.R;

import static com.tools.security.R.style.LoadingDialog;


/**
 * description: 加载中弹窗工具类
 * author: xiaodifu
 * date: 2016/7/28.
 */
public class LoadingUtil implements DialogInterface.OnCancelListener {

    private Dialog mLoadingDialog;
    private View mLoadingParent;
    private Context mContext;
    private ICancelCallback cancelCallback;

    public LoadingUtil(Context context,ICancelCallback cancelCallback) {
        this.mContext = context;
        this.cancelCallback=cancelCallback;
        init();
    }

    private void init() {
        mLoadingParent = LayoutInflater.from(mContext).inflate(R.layout.layout_loading, null);
        mLoadingDialog = new Dialog(mContext, LoadingDialog);
        mLoadingDialog.setContentView(mLoadingParent);
        mLoadingDialog.setOnCancelListener(this);
    }

    /**
     * 默认弹窗文本Loading
     */
    public void showProgress() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new Dialog(mContext, LoadingDialog);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setContentView(mLoadingParent);
                mLoadingDialog.setOnCancelListener(this);
            }
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setCancelable(true);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideProgress() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (cancelCallback!=null) cancelCallback.onCancel();
    }

    public interface ICancelCallback{
        void onCancel();
    }
}
