package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.DownloadFile;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.FileUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.widget.dialog.base.BaseDialog;

import java.io.File;

/**
 * description:下载文件列表menu点击弹窗
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class DownloadItemDialog extends BaseDialog implements View.OnClickListener {

    private TextView openText, setAsText, shareText, delText;
    private Context context;
    private DownloadFile downloadFile;

    public DownloadItemDialog(Context context, DownloadFile downloadFile) {
        super(context);
        this.context = context;
        this.downloadFile = downloadFile;
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
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
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        openText = (TextView) findViewById(R.id.text_open);
        setAsText = (TextView) findViewById(R.id.text_set_as);
        shareText = (TextView) findViewById(R.id.text_share);
        delText = (TextView) findViewById(R.id.text_del);

        openText.setOnClickListener(this);
        setAsText.setOnClickListener(this);
        shareText.setOnClickListener(this);
        delText.setOnClickListener(this);

       /* if (downloadFile.getFileType() == DownloadFile.FileType.APK) {
            openText.setText(R.string.install);
            setAsText.setVisibility(View.GONE);
        } else {
            openText.setText(R.string.open);
            setAsText.setVisibility(View.VISIBLE);
        }*/
    }

    @Override

    protected int getContentViewId() {
        return R.layout.dialog_download_item;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.text_open:
                if (downloadFile.getFileType() == DownloadFile.FileType.APK) {
                    SystemUtil.installFromFile(context, new File(downloadFile.getAbsPath()), false);
                } else {
                    Intent intent = FileUtil.openFile(downloadFile.getAbsPath());
                    context.startActivity(intent);
                }
                break;
            case R.id.text_set_as:
                break;
            case R.id.text_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(downloadFile.getAbsPath()));
                context.startActivity(Intent.createChooser(shareIntent, "Share to..."));
                break;
            case R.id.text_del:
                File file = new File(downloadFile.getAbsPath());
                if (file != null && file.isFile()) {
                    file.delete();
                    context.sendBroadcast(new Intent(AppConstants.UPDATE_DOWNLOAD_SECURITY_LIST));
                }
                break;
        }
    }
}
