package com.tools.security.bean;

/**
 * description:本地图片略缩图简要信息
 * author: xiaodifu
 * date: 2017/1/10.
 */

public class ThumbnailsData {
    int id;
    int imageId;
    String data;

    public ThumbnailsData(int id, int imageId, String data) {
        this.id = id;
        this.imageId = imageId;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
