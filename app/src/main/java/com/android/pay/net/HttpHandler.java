package com.android.pay.net;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Relin
 * on 2018-09-10.
 * Http异步处理类
 */
public class HttpHandler extends Handler {

    //网络请求失败的what
    public static final int WHAT_ON_FAILURE = 0x001;
    //网络请求成功的what
    public static final int WHAT_ON_SUCCEED = 0x002;
    //请求数据无返回的异常
    public static final String HTTP_NO_RESPONSE = "The server request is unresponsive [response code != 200]";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        HttpResult httpResult = (HttpResult) msg.obj;
        OnHttpListener listener = httpResult.getHttpListener();
        switch (msg.what) {
            case WHAT_ON_FAILURE:
                if (listener != null) {
                    listener.onHttpFailure(httpResult);
                }
                break;
            case WHAT_ON_SUCCEED:
                if (listener != null) {
                    listener.onHttpSucceed(httpResult);
                }
                break;
        }
        removeCallbacksAndMessages(null);
    }

    /**
     * 发送异常消息
     *
     * @param requestParams 请求参数
     * @param url           请求地址
     * @param e             异常
     * @param listener      网络请求监听
     */
    public void sendExceptionMsg(RequestParams requestParams, String url, Exception e, OnHttpListener listener) {
        Message msg = obtainMessage();
        msg.what = HttpHandler.WHAT_ON_FAILURE;
        HttpResult httpResult = new HttpResult();
        httpResult.setRequestParams(requestParams);
        httpResult.setUrl(url);
        httpResult.setException(e);
        httpResult.setHttpListener(listener);
        msg.obj = httpResult;
        sendMessageDelayed(msg, 0);
    }

    /**
     * 发送成功信息
     *
     * @param requestParams 请求参数
     * @param url           请求地址
     * @param result        请求结果
     * @param listener      网络请求监听
     */
    public void sendSuccessfulMsg(RequestParams requestParams, String url, String result, OnHttpListener listener) {
        HttpResult httpResult = new HttpResult();
        httpResult.setBody(result);
        httpResult.setUrl(url);
        httpResult.setRequestParams(requestParams);
        httpResult.setHttpListener(listener);
        Message msg = this.obtainMessage();
        msg.what = HttpHandler.WHAT_ON_SUCCEED;
        msg.obj = httpResult;
        sendMessageDelayed(msg, 0);
    }

}
