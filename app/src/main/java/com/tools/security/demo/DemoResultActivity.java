package com.tools.security.demo;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tools.security.R;
import com.tools.security.bean.ResultInfo;
import com.tools.security.bean.ResultType;
import com.tools.security.mainscan.adapter.ScanResultAdapter;
import com.tools.security.common.BaseActivity;

import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/27.
 */

public class DemoResultActivity extends BaseActivity {
    RecyclerView recyclerView;
    CollapsingToolbarLayout collapsingToolbarLayout;



    @Override
    protected void init() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScanResultAdapter scanResultAdapter=new ScanResultAdapter(this);
        recyclerView.setAdapter(scanResultAdapter);
        ArrayList<ResultInfo> list=new ArrayList<>();
        for (int i=0;i<20;i++){
            list.add(new ResultInfo(ResultType.BROWSER, null, getResources().getDrawable(R.drawable.ic_broswer), "Browser Privacy", "Personal broswer history", "" + 12));
        }
//        scanResultAdapter.setData(list);

        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.primary));
        setStatusBar();
        onTitleChanged("Safe",-1);





    }

    @Override
    protected int getContentViewId() {
        return R.layout.demo_activity_result;
    }
}
