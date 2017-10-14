package com.tools.security.common;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.utils.LoadingUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;


/**
 * description: Activity父类
 * author: xiaodifu
 * date: 2016/7/8.
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 底部工具栏
     */
    protected Toolbar mToolbar;
    protected TextView mCustomTitleTextView;
    private LoadingUtil loadingUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getContentViewId() <= 0) {
            throw new RuntimeException("layout resId undefind");
        }
        SecurityApplication.getInstance().doForCreate(this);
        initBefore(savedInstanceState);
        setContentView(getContentViewId());
        initBundle(savedInstanceState);
        initToolbar();
        initLoading();
        setStatusBar();
        init();
    }

    private void initLoading() {
        loadingUtil = new LoadingUtil(this,new LoadingUtil.ICancelCallback(){
            @Override
            public void onCancel() {
                onHomeClick();
            }
        });
    }

    public void showProgress() {
        loadingUtil.showProgress();
    }

    public void hideProgress() {
        loadingUtil.hideProgress();
    }

    protected void setStatusBar() {
//        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.primary),0);
    }

    protected void initBundle(Bundle savedInstanceState) {

    }

    // 初始化之前
    protected void initBefore(Bundle savedInstanceState) {
    }

    // 初始化
    protected abstract void init();

    /**
     * 初始化工具栏
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHomeClick();
                }
            });

            resetToolbar();
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    public void resetToolbar() {
        if (mCustomTitleTextView == null) {
            mCustomTitleTextView = (TextView) getLayoutInflater().inflate(R.layout.layout_toolbar_title, null);
        }
        getSupportActionBar().setCustomView(mCustomTitleTextView, new ActionBar.LayoutParams(Gravity.CENTER));
        if (getTitle() != null) {
            mCustomTitleTextView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
            mCustomTitleTextView.setText(getTitle());
        }
    }

    public void hiddenActionBar() {
        getSupportActionBar().hide();
    }


    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        //super.onTitleChanged("", color);
        if (mCustomTitleTextView != null && title != null) {
            mCustomTitleTextView.setText(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getOptionsMenuId() != -1) {
            getMenuInflater().inflate(getOptionsMenuId(), menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * home返回键
     */
    protected void onHomeClick() {
        finish();
    }

    /**
     * 当前布局文件资源
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 当前页菜单资源
     *
     * @return
     */
    protected int getOptionsMenuId() {
        return -1;
    }

    @Override
    public void onBackPressed() {
        onHomeClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SecurityApplication.getInstance().doForFinish(this);
    }

    public final void clear() {
        super.finish();
    }

}
