package com.tools.security.common;

import java.util.List;

/**
 * description:广告全局常量
 * author: xiaodifu
 * date: 2017/1/24.
 */

public class AdStaticConstant {
    //普通广告
    public static List<Object> ads=null;
    //普通广告最后一次加载时间
    public static long normalAdSaveTime=0;
    //试试手气广告
    public static List<Object> luckyAds=null;
    //试试手气最后一次加载时间
    public static long luckyAdSaveTime=0;
}
