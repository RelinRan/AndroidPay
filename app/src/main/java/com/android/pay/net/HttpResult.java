package com.android.pay.net;

import java.io.Serializable;

/**
 * Created by Ice on 2017/8/6.
 */

public class HttpResult implements Serializable {

    //返回的数据
    private String body;
    //请求地址
    private String url;
    //请求参数
    private RequestParams requestParams;
    //文件流异常
    private Exception exception;
    //回调接口
    private OnHttpListener httpListener;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public OnHttpListener getHttpListener() {
        return httpListener;
    }

    public void setHttpListener(OnHttpListener httpListener) {
        this.httpListener = httpListener;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

}
