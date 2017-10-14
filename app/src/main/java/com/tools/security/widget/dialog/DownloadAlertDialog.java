package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.dialog.base.BaseDialog;

import java.io.File;

import static com.tools.security.common.AppConstants.VIRUS_MONITOR_APP_INFO;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class DownloadAlertDialog extends BaseDialog implements View.OnClickListener {

    private AvlAppInfo appInfo;
    private PackageManager packageManager;
    private ImageView iconImg;
    private ImageView closeImg;
    private TextView nameText;
    private TextView virusNameText;
    private TextView deleteText;
    private String absPath;

    private boolean isDeleted = false;
    private Context context;
    private boolean isFromService = false;

    public DownloadAlertDialog(Context context, String path, boolean from) {
        super(context);
        this.context = context;
        this.absPath = path;
        this.isFromService = from;
    }

    @Override
    protected float setWidthScale() {
        return 0.83f;
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
        if (isFromService) getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        setCanceledOnTouchOutside(true);
        initView();
        initData();
    }

    private void initData() {
        this.appInfo = SpUtil.getInstance().getBean(VIRUS_MONITOR_APP_INFO, AvlAppInfo.class);
        packageManager = context.getPackageManager();

        if (appInfo == null) return;

        virusNameText.setText(TextUtils.isEmpty(appInfo.getVirusName()) ? context.getString(R.string.default_virus_name) : appInfo.getVirusName());
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(appInfo.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        iconImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
        nameText.setText(applicationInfo.loadLabel(packageManager));
    }

    private void initView() {
        iconImg = (ImageView) findViewById(R.id.img_logo);
        closeImg = (ImageView) findViewById(R.id.img_close);
        nameText = (TextView) findViewById(R.id.text_name);
        virusNameText = (TextView) findViewById(R.id.text_virus_name);
        deleteText = (TextView) findViewById(R.id.text_del);

        closeImg.setOnClickListener(this);
        deleteText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_del:
                File file = new File(absPath);
                if (file != null && file.isFile()) {
                    file.delete();
                }
                isDeleted = true;
                dismiss();
                break;
            case R.id.img_close:
                dismiss();
                break;
        }
    }

    @Override
    public void superDismiss() {
        if (!isDeleted) {
            AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
            if (appConfig.getSafeLevel() != SafeLevel.DANGER) {
                appConfig.setSafeLevel(SafeLevel.DANGER);
            }
            appConfig.setProblemCount(appConfig.getProblemCount() + 1);
            appInfo.save();
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        }
        super.superDismiss();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_download_apk_alert;
    }
}
