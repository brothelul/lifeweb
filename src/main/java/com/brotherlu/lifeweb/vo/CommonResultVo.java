package com.brotherlu.lifeweb.vo;

import java.util.HashMap;
import java.util.Map;

public class CommonResultVo {
    public  static final String SUCCESS="success";
    public  static final String FAIL="fail";

    private String success;
    private Object data;
    private String msg;
    private int statusCode=0;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
