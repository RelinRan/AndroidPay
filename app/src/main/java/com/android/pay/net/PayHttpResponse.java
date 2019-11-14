package com.android.pay.net;

import java.io.Serializable;

/**
 * Created by Ice on 2017/8/6.
 */

public class PayHttpResponse implements Serializable {

    //返回的数据
    private String body;
    //请求地址
    private String url;
    //请求的结果code
    private int code;
    //请求参数
    private PayRequestParams requestParams;
    //文件流异常
    private Exception exception;
    //回调接口
    private OnPayHttpListener httpListener;

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }

    public String url() {
        return url;
    }

    public void url(String url) {
        this.url = url;
    }

    public Exception exception() {
        return exception;
    }

    public void exception(Exception exception) {
        this.exception = exception;
    }

    public OnPayHttpListener listener() {
        return httpListener;
    }

    public void listener(OnPayHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public PayRequestParams requestParams() {
        return requestParams;
    }

    public void requestParams(PayRequestParams requestParams) {
        this.requestParams = requestParams;
    }

    public int code() {
        return code;
    }

    public void code(int code) {
        this.code = code;
    }
}
