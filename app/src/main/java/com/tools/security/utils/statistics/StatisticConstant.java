package com.tools.security.utils.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wushuangshuang on 16/6/16.
 */
public class StatisticConstant {
    //=====================操作码开始===========================
    public static final String OPERATION_SHOW = "1"; // 展示

    //=====================操作码结束===========================


    //=====================Tab分类传值======================
    public static final String TAB_LAUNCHER_APP = "0001"; // 启动应用

    //=====================Tab分类传值结束======================

    public static List<String> tabList = new ArrayList<String>();

    static {
        tabList.add(TAB_LAUNCHER_APP);
    }
}
