package com.absket.in.model;

import com.google.gson.annotations.SerializedName;

public class ResponsePojo {

    @SerializedName("status")
    String status;
    @SerializedName("msg")
    String msg;
    @SerializedName("path")
    String path;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
