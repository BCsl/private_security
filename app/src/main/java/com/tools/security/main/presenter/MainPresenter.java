package com.tools.security.main.presenter;

import android.content.Context;
import android.util.Log;

import com.avl.engine.AVLCheckUpdate;
import com.avl.engine.AVLEngine;
import com.avl.engine.AVLUpdateCheckCallBack;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.dialog.UpdateVirusLibDialog;
import com.tools.security.widget.dialog.VirusLibCheckDialog;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/19.
 */

public class MainPresenter implements MainContract.Presenter, VirusLibCheckDialog.ICheckCallback {

    private Context context;
    private MainContract.View view;
    private AVLCheckUpdate avlCheckUpdate;

    public MainPresenter(Context context, MainContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void checkVirusLib() {
        long lastUpdateTime = SpUtil.getInstance().getLong(AppConstants.LAST_UPDATE_VIRUS_LIB_TIME, 0);
        if (lastUpdateTime == 0 || (System.currentTimeMillis() - lastUpdateTime) > 10 * 24 * 60 * 60 * 1000) {
            AVLEngine.checkUpdate(new AVLUpdateCheckCallBack() {
                @Override
                public void updateCheckStart() {
                }

                @Override
                public void updateCheckEnd(AVLCheckUpdate avlCheckUpdate) {
                    Log.e("TAG","avlCheckUpdate="+avlCheckUpdate.engineUpdate);
                    MainPresenter.this.avlCheckUpdate = avlCheckUpdate;
                    if (avlCheckUpdate != null && avlCheckUpdate.engineUpdate == AVLCheckUpdate.ACTION_NEED_UPDATE) {
                        VirusLibCheckDialog virusLibCheckDialog = new VirusLibCheckDialog(avlCheckUpdate, MainPresenter.this, context);
                        virusLibCheckDialog.show();
                    }
                }
            });
        }
    }

    @Override
    public void failedTryAgain() {
        checkVirusLib();
    }

    @Override
    public void update() {
        new UpdateVirusLibDialog(context, avlCheckUpdate.virusLibSize).show();
    }
}
