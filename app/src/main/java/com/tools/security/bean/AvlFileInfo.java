package com.tools.security.bean;

import com.avl.engine.AVLAppInfo;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description: 查杀文件结果类
 * author: xiaodifu
 * date: 2017/1/12.
 */

public class AvlFileInfo extends DataSupport implements AVLAppInfo {

    private int dangerLevel;//0安全 1恶意 2风险
    private String virusName;
    private String packageName;
    private String appName;
    @Column(unique = true)
    private String path;

    public AvlFileInfo() {
    }

    public AvlFileInfo(int dangerLevel, String virusName, String packageName, String appName, String path) {
        this.dangerLevel = dangerLevel;
        this.virusName = virusName;
        this.packageName = packageName;
        this.appName = appName;
        this.path = path;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public void setVirusName(String virusName) {
        this.virusName = virusName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getDangerLevel() {
        return dangerLevel;
    }

    @Override
    public String getVirusName() {
        return virusName;
    }
}
