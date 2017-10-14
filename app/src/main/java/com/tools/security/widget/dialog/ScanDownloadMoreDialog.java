package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.clean.ApkInfo;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.utils.ToastUtil;

import java.io.File;

/**
 * description:下载保护：apk点击more，显示信息
 * author: xiaodifu
 * date: 2017/1/9.
 */

public class ScanDownloadMoreDialog extends Dialog implements View.OnClickListener {
    protected DisplayMetrics dm;
    private Context context;

    private View contentView;
    private ImageView logoImg;
    private ImageView closeImg;
    private TextView nameText;
    private TextView sizeText;
    private TextView delText;
    private TextView installText;
    private File file;

    private PackageManager packageManager;

    private PackageInfo apkInfo;
    private String path;


    public ScanDownloadMoreDialog(Context context, PackageInfo apkInfo, String path) {
        super(context, R.style.DialogTransparent);
        this.context = context;
        this.apkInfo = apkInfo;
        this.path = path;
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download_apk);
        setCancelable(false);
        initData();
        initView();
    }

    private void initView() {
        contentView = findViewById(R.id.relative_dl_apk);
        logoImg = (ImageView) findViewById(R.id.img_logo);
        closeImg = (ImageView) findViewById(R.id.img_close);
        nameText = (TextView) findViewById(R.id.text_name);
        sizeText = (TextView) findViewById(R.id.text_size);
        delText = (TextView) findViewById(R.id.text_del);
        installText = (TextView) findViewById(R.id.text_install);

        closeImg.setOnClickListener(this);
        delText.setOnClickListener(this);
        installText.setOnClickListener(this);

        file = new File(path);
        String fileSize = "";
        if (file.exists() && file.isFile()) {
            fileSize = StringUtil.getFormatSize(file.length());
        } else {
            fileSize = context.getString(R.string.unkown);
        }
        sizeText.setText(fileSize);
        ApplicationInfo applicationInfo = apkInfo.applicationInfo;
        nameText.setText(packageManager.getApplicationLabel(applicationInfo));
        logoImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
    }

    private void initData() {
        dm = context.getResources().getDisplayMetrics();
        packageManager = context.getPackageManager();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (dm.widthPixels * 0.83f);
        window.setAttributes(layoutParams);

        showEnterAnim();
    }

    private void showEnterAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(contentView, "translationY", -250 * dm.density, 0), //
                ObjectAnimator.ofFloat(contentView, "alpha", 0.2f, 1));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    public void dismiss() {
        showDismissAnim();
    }

    private void superDismiss() {
        super.dismiss();
    }

    private void showDismissAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(contentView, "translationY", 0, 250 * dm.density), //
                ObjectAnimator.ofFloat(contentView, "alpha", 1, 0));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                superDismiss();
                break;
            case R.id.text_del:
                file.delete();
                ToastUtil.showShort(context.getString(R.string.delete_success));
                superDismiss();
                break;
            case R.id.text_install:
                SystemUtil.installFromFile(context,file,true);
                superDismiss();
                break;
        }
    }
}
