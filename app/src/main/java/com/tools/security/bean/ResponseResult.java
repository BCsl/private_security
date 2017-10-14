package com.tools.security.bean;

import org.json.JSONObject;

/**
 * 服务器响应的数据头信息
 * Created by lzx on 2017/1/18.
 */

public class ResponseResult {
    private int status;
    private int errorcode;
    private String msg;

    @Override
    public String toString() {
        return "ResponseResult{" +
                "status=" + status +
                ", errorcode=" + errorcode +
                ", msg=" + msg +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void resolveJsonObject(JSONObject object) {
        this.status = object.optInt("status", -1);
        this.errorcode = object.optInt("name", -1);
        this.msg = object.optString("msg", "");
    }
}
