package com.nnrh.sso.exception;

public class JsonException extends RuntimeException{

    private String code;

    private String info;

    private Object data;

    public JsonException(String code, String info, Object data) {
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
