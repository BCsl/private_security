package com.tools.security.bean;

import com.avl.engine.AVLAppInfo;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:avl扫描结果
 * author: xiaodifu
 * date: 2017/1/5.
 */

public class AvlAppInfo extends DataSupport implements AVLAppInfo {

    private long id;
    private int result;//0安全 1恶意 2风险
    private String virusName;
    @Column(unique = true)
    private String pkgName;
    private String sampleName;
    private String filePath;
    private int ignored;

    public AvlAppInfo(String pkgName, int result) {
        this.pkgName = pkgName;
        this.result = result;
    }

    public AvlAppInfo() {
    }

    public AvlAppInfo(int result, String virusName, String pkgName, String sampleName, String filePath, int ignored) {
        this.result = result;
        this.virusName = virusName;
        this.pkgName = pkgName;
        this.sampleName = sampleName;
        this.filePath = filePath;
        this.ignored = ignored;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setVirusName(String virusName) {
        this.virusName = virusName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }


    @Override
    public String getAppName() {
        return sampleName;
    }

    @Override
    public String getPackageName() {
        return pkgName;
    }

    @Override
    public String getPath() {
        return filePath;
    }

    @Override
    public int getDangerLevel() {
        return result;
    }

    @Override
    public String getVirusName() {
        return virusName;
    }

    @Override
    public String toString() {
        return "AvlAppInfo{" +
                "id=" + id +
                ", result=" + result +
                ", virusName='" + virusName + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", ignored=" + ignored +
                '}';
    }
}
