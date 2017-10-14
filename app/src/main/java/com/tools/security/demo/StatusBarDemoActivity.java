package com.tools.security.demo;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import com.tools.security.R;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.statusbar.StatusBarUtil;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/24.
 */

public class StatusBarDemoActivity extends BaseActivity {

    private int mAlpha = StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA;
    private DrawerLayout drawerLayout;

    @Override
    protected void init() {
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void setStatusBar() {
        int mStatusBarColor = getResources().getColor(R.color.primary);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout), mStatusBarColor, mAlpha);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_statusbar_demo;
    }
}
