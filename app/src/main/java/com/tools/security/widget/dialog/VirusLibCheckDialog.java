package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.avl.engine.AVLCheckUpdate;
import com.avl.engine.AVLEngine;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.mainscan.view.MainScanActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * description:病毒库更新检测
 * author: xiaodifu
 * date: 2017/1/11.
 */

public class VirusLibCheckDialog extends BaseDialog implements View.OnClickListener {
    private AVLCheckUpdate avlCheckUpdate;
    private TextView titleText;
    private TextView versionMsgText;
    private TextView leftText;
    private TextView rightText;
    private ICheckCallback checkCallback;

    public VirusLibCheckDialog(AVLCheckUpdate avlCheckUpdate, ICheckCallback checkCallback, Context context) {
        super(context);
        this.avlCheckUpdate = avlCheckUpdate;
        this.checkCallback = checkCallback;
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
        titleText = (TextView) findViewById(R.id.text_title);
        versionMsgText = (TextView) findViewById(R.id.text_version_msg);
        leftText = (TextView) findViewById(R.id.text_left);
        rightText = (TextView) findViewById(R.id.text_right);

        switch (avlCheckUpdate.virusLibUpdate) {
            case AVLCheckUpdate.ACTION_NEED_UPDATE:
                titleText.setText(R.string.virus_def_update);
                leftText.setText(context.getString(R.string.cancel_upper));
                rightText.setText(R.string.update_upper);
                long lastCheckTime = SpUtil.getInstance().getLong(AppConstants.LAST_CHECK_VIRUS_LIB_VERSION, 0);
                long intervalTime;
                SpUtil.getInstance().putLong(AppConstants.LAST_CHECK_VIRUS_LIB_VERSION, System.currentTimeMillis());
                if (lastCheckTime == 0) {
                    intervalTime = 0l;
                } else {
                    intervalTime = System.currentTimeMillis() - lastCheckTime;
                }
                versionMsgText.setText(context.getString(R.string.dialog_virus_lib_content, AVLEngine.GetVirusDatabaseVersion(), avlCheckUpdate.virusLibVersion, StringUtil.getFormatTime(intervalTime)));
                break;
            case AVLCheckUpdate.ACTION_NO_NEED_UPDATE:
                titleText.setText(R.string.virus_lib_up_to_date);
                leftText.setText(R.string.later_upper);
                rightText.setText(R.string.scan_upper);
                versionMsgText.setText(context.getString(R.string.virus_lib_version, AVLEngine.GetVirusDatabaseVersion()));
                break;
            case AVLCheckUpdate.ACTION_UPDATE_FAIL:
                titleText.setText(R.string.virus_lib_def_failed);
                leftText.setText(R.string.cancel_upper);
                rightText.setText(R.string.try_again);
                versionMsgText.setText(context.getString(R.string.virus_lib_current_version, AVLEngine.GetVirusDatabaseVersion()));
                break;
        }

        leftText.setOnClickListener(this);
        rightText.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_virus_lib_check;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_left:
                superDismiss();
                break;
            case R.id.text_right:
                superDismiss();
                switch (avlCheckUpdate.virusLibUpdate) {
                    case AVLCheckUpdate.ACTION_NEED_UPDATE:
                        checkCallback.update();
                        break;
                    case AVLCheckUpdate.ACTION_NO_NEED_UPDATE:
                        context.startActivity(new Intent(context, MainScanActivity.class));
                        break;
                    case AVLCheckUpdate.ACTION_UPDATE_FAIL:
                        if (checkCallback != null) checkCallback.failedTryAgain();
                        break;
                }
                break;
        }
    }

    public interface ICheckCallback {
        void failedTryAgain();

        void update();
    }
}
