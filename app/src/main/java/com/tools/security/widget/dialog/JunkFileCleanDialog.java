package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.FileCacheGroup;
import com.tools.security.clean.FileUtils;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/20.
 */

public class JunkFileCleanDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView sizeText;
    private TextView cleanText;
    private LinearLayout contentView;
    private double logSize = 0d, apkSize = 0d, tempSize = 0d;
    private double adSize = 0d;
    private RelativeLayout cacheRelative, adRelative;
    private TextView cacheCountText, adCountText;
    private FileCacheGroup logFileCacheGroup, apkFileCacheGroup, systempFileCacheGroup;
    private ArrayList<FileCacheGroup> fileCacheGroupArrayList = new ArrayList<>();
    protected DisplayMetrics dm;

    public JunkFileCleanDialog(Context context) {
        super(context, R.style.DialogTransparent);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_junk_file_clean);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initData();
        initView();
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
                ObjectAnimator.ofFloat(contentView, "translationX", 250 * dm.density, 0), //
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

    private void showDismissAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(contentView, "translationX", 0, -250 * dm.density), //
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
    public void dismiss() {
        showDismissAnim();
    }

    private void superDismiss() {
        super.dismiss();
    }

    private void initView() {
        contentView = (LinearLayout) findViewById(R.id.linear_junk);
        sizeText = (TextView) findViewById(R.id.text_size);
        cleanText = (TextView) findViewById(R.id.text_clean);
        cacheRelative = (RelativeLayout) findViewById(R.id.relative_cache);
        adRelative = (RelativeLayout) findViewById(R.id.relative_ad);
        cacheCountText = (TextView) findViewById(R.id.text_count_cache);
        adCountText = (TextView) findViewById(R.id.text_count_ad);

        cacheCountText.setText(StringUtil.getFormatSize(logSize + apkSize + tempSize));
        adCountText.setText(StringUtil.getFormatSize(adSize));
        sizeText.setText(StringUtil.getFormatSize(logSize + apkSize + tempSize + adSize));

        if ((logSize + apkSize + tempSize) == 0d) {
            cacheRelative.setVisibility(View.GONE);
        }

        if (adSize == 0d) {
            adRelative.setVisibility(View.GONE);
        }
        cleanText.setOnClickListener(this);
    }

    private void initData() {
        dm = context.getResources().getDisplayMetrics();

        logFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_LOG, FileCacheGroup.class);
        apkFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_APK, FileCacheGroup.class);
        systempFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, FileCacheGroup.class);

        if (logFileCacheGroup != null) {
            fileCacheGroupArrayList.add(logFileCacheGroup);
            logSize = logFileCacheGroup.getTotalCacheSize();
        }
        if (apkFileCacheGroup != null) {
            fileCacheGroupArrayList.add(apkFileCacheGroup);
            apkSize = apkFileCacheGroup.getTotalCacheSize();
        }
        if (systempFileCacheGroup != null) {
            fileCacheGroupArrayList.add(systempFileCacheGroup);
            tempSize = systempFileCacheGroup.getTotalCacheSize();
        }

        adSize = Double.parseDouble(SpUtil.getInstance().getString(AppConstants.AD_JUNK_SIZE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_clean:
                Map<String, Object> map = new HashMap<>();
                map.put("cache", cacheCountText.getText().toString().trim());
                map.put("ad", adCountText.getText().toString().trim());
                KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_JUNK_CLEAN, map);
                new CleanFileAsyncTask().executeOnExecutor(Executors.newCachedThreadPool(), fileCacheGroupArrayList);
                startCleanAnim();
                break;
            default:
                break;
        }
    }

    private void startCleanAnim() {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(cacheRelative, "translationX", 0f, -cacheRelative.getWidth());
        objectAnimator1.setDuration(300);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(cacheRelative, "alpha", 1.0f, 0.0f);
        objectAnimator2.setDuration(300);

        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(adRelative, "translationX", 0f, -adRelative.getWidth());
        objectAnimator3.setDuration(300);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(adRelative, "alpha", 1.0f, 0.0f);
        objectAnimator4.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateInterpolator());
        if (cacheRelative.getVisibility() == View.VISIBLE) {
            animatorSet.play(objectAnimator1).with(objectAnimator2);
            if (adRelative.getVisibility() == View.VISIBLE) {
                animatorSet.play(objectAnimator3).with(objectAnimator4).after(objectAnimator1);
            }
        } else {
            animatorSet.play(objectAnimator3).with(objectAnimator4);
        }
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int currentCleanedCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_JUNK_COUNT);
                SpUtil.getInstance().putInt(AppConstants.CLEANED_JUNK_COUNT, currentCleanedCount + 1);
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_LOG, null);
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_APK, null);
                SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, null);
                context.sendBroadcast(new Intent(AppConstants.ACTION_FILTER_CLEAN_JUNK));
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    class CleanFileAsyncTask extends AsyncTask<ArrayList<FileCacheGroup>, Integer, Void> {

        @Override
        protected Void doInBackground(ArrayList<FileCacheGroup>... params) {
            Double cleanedSize = 0d;
            for (FileCacheGroup fileCacheGroup : params[0]) {
                FileUtils.freeJunkInfos(fileCacheGroup.getChilds());
                cleanedSize += fileCacheGroup.getTotalCacheSize();
            }

            Double d = Double.parseDouble(SpUtil.getInstance().getString(AppConstants.CLEANED_JUNK_FILE_SIZE, "0"));
            SpUtil.getInstance().putString(AppConstants.CLEANED_JUNK_FILE_SIZE, (d + cleanedSize) + "");
            SpUtil.getInstance().putString(AppConstants.AD_JUNK_SIZE, "0");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
