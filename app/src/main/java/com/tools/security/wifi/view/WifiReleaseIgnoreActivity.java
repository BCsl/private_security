package com.tools.security.wifi.view;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.tools.security.R;
import com.tools.security.bean.WifiIgnoreApp;
import com.tools.security.common.BaseActivity;

import java.util.List;

/**
 * description:wifi带宽释放白名单
 * author: xiaodifu
 * date: 2017/1/16.
 */

public class WifiReleaseIgnoreActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<WifiIgnoreApp> wifiIgnoreApps;
    

    @Override
    protected void init() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_ignore_list;
    }

    @Override
    protected int getOptionsMenuId() {
        return R.menu.menu_wifi_ignore_add;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.item_add){
            startActivity(new Intent(this,AppToIgnoreListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
