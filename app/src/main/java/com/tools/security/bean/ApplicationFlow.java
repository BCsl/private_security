package com.tools.security.bean;

import android.content.pm.ApplicationInfo;

/**
 * description:应用流量
 * author: xiaodifu
 * date: 2017/1/14.
 */

public class ApplicationFlow {
    private ApplicationInfo applicationInfo;
    private long flow;

    public ApplicationFlow(ApplicationInfo applicationInfo, long flow) {
        this.applicationInfo = applicationInfo;
        this.flow = flow;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }
}
