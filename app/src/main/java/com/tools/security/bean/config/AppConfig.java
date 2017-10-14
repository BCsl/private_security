package com.tools.security.bean.config;

import com.tools.security.bean.SafeLevel;

import org.litepal.crud.DataSupport;

/**
 * description:应用配置
 * author: xiaodifu
 * date: 2016/12/15.
 */

public class AppConfig extends DataSupport{
    //是否扫描过
    private boolean isScanned;
    //安全级别
    private SafeLevel safeLevel;
    //病毒数量
    private int problemCount;

    public AppConfig() {
    }

    public AppConfig(boolean isScanned, SafeLevel safeLevel, int problemCount) {
        this.isScanned = isScanned;
        this.safeLevel = safeLevel;
        this.problemCount = problemCount;
    }

    public boolean isScanned() {
        return isScanned;
    }

    public void setScanned(boolean scanned) {
        isScanned = scanned;
    }

    public SafeLevel getSafeLevel() {
        return safeLevel;
    }

    public void setSafeLevel(SafeLevel safeLevel) {
        this.safeLevel = safeLevel;
    }

    public int getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(int problemCount) {
        this.problemCount = problemCount;
    }
}
