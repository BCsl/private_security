package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avl.engine.AVLEngine;
import com.avl.engine.AVLUpdateCallback;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.mainscan.view.MainScanActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * description:更新病毒库弹窗
 * author: xiaodifu
 * date: 2017/1/11.
 */

public class UpdateVirusLibDialog extends BaseDialog implements View.OnClickListener {

    private TextView cancelText;
    private ProgressBar progressBar;
    private TextView sizeText;
    private long virusLibSize;
    private NormalDialog successDialog;

    public UpdateVirusLibDialog(Context context, long virusLibSize) {
        super(context);
        this.virusLibSize = virusLibSize;
    }

    @Override
    protected float setWidthScale() {
        return 0.85f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        cancelText = (TextView) findViewById(R.id.text_cancel);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        sizeText = (TextView) findViewById(R.id.text_size);

        cancelText.setOnClickListener(this);

        final String totalSizeStr = " / " + StringUtil.getFormatSize(virusLibSize);

        AVLEngine.update(new AVLUpdateCallback() {
            @Override
            public void updateStart() {
                progressBar.setProgress(0);
            }

            @Override
            public void updateProgress(int i) {
                progressBar.setProgress(i);
                sizeText.setText(Html.fromHtml(context.getString(R.string.update_viruslib_size, StringUtil.getFormatSize(i * virusLibSize / 100d), totalSizeStr)));
            }

            @Override
            public void updateEnd(int i) {
                superDismiss();
                if (i < 0) {
                    //失败
                    ToastUtil.showShort(context.getString(R.string.virus_lib_updated_failed));
                } else if (i == 0) {
                    //没有更新
                    ToastUtil.showShort(context.getString(R.string.virus_lib_already_up_to_date));
                } else {
                    //更新成功
                    SpUtil.getInstance().putLong(AppConstants.LAST_UPDATE_VIRUS_LIB_TIME, System.currentTimeMillis());
                    successDialog = new NormalDialog(context, new NormalDialog.IOnClickListener() {
                        @Override
                        public void onLeftClick() {
                            successDialog.superDismiss();
                        }

                        @Override
                        public void onRightClick() {
                            successDialog.superDismiss();
                            context.startActivity(new Intent(context, MainScanActivity.class));
                        }
                    });
                    successDialog.title(context.getString(R.string.virus_lib_def_up_to_date))
                            .content(context.getString(R.string.virus_lib_version, AVLEngine.GetVirusDatabaseVersion()))
                            .left(context.getString(R.string.later_upper))
                            .right(context.getString(R.string.scan_upper));
                    try {
                        successDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_update_virus_lib;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                superDismiss();
                AVLEngine.stopUpdate();
                break;
        }
    }
}
