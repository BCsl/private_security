package com.tools.security.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/8.
 */

public class AppWhitePaper extends DataSupport{
    private long id;
    private long avlId;
    private int result;//0安全 1恶意 2风险
    private String virusName;
    @Column(unique = true)
    private String pkgName;
    private String sampleName;

    public AppWhitePaper() {
    }

    public AppWhitePaper(long avlId, int result, String virusName, String pkgName, String sampleName) {
        this.avlId = avlId;
        this.result = result;
        this.virusName = virusName;
        this.pkgName = pkgName;
        this.sampleName = sampleName;
    }

    public long getAvlId() {
        return avlId;
    }

    public void setAvlId(long avlId) {
        this.avlId = avlId;
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

    public String getVirusName() {
        return virusName;
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
}
