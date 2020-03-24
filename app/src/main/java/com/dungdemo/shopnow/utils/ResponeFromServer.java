package com.dungdemo.shopnow.utils;

public class ResponeFromServer {

    int code;
    String body;

    public ResponeFromServer(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
