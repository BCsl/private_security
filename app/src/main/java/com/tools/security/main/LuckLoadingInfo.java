package com.tools.security.main;

/**
 * Created by wushuangshuang on 16/8/16.
 */
public class LuckLoadingInfo {
    private float mStartX; // 初始的X值
    private float mStartY; // 初始的Y值
    private float mVx; // x轴速度
    private float mVy; // y轴速度
    private float mAccelerator; // 加速度

    public LuckLoadingInfo(float startX, float startY, float vx, float vy, float accelerator) {
        mStartX = startX;
        mStartY = startY;
        mVx = vx;
        mVy = vy;
        mAccelerator = accelerator;
    }

    public float getmStartX() {
        return mStartX;
    }

    public void setmStartX(float mStartX) {
        this.mStartX = mStartX;
    }

    public float getmStartY() {
        return mStartY;
    }

    public void setmStartY(float mStartY) {
        this.mStartY = mStartY;
    }

    public float getmVx() {
        return mVx;
    }

    public void setmVx(float mVx) {
        this.mVx = mVx;
    }

    public float getmVy() {
        return mVy;
    }

    public void setmVy(float mVy) {
        this.mVy = mVy;
    }

    public float getmAccelerator() {
        return mAccelerator;
    }

    public void setmAccelerator(float mAccelerator) {
        this.mAccelerator = mAccelerator;
    }
}
