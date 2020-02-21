package com.android.pay.net;

import android.os.Message;
import android.util.Log;

/**
 * Created by Relin
 * on 2018-09-10.
 * Http异步处理类
 */
public class Handler extends android.os.Handler {

    /**
     * 日志标识
     */
    public static final String TAG = "Http";

    /**
     * 网络请求失败的what
     */
    public static final int WHAT_ON_FAILURE = 0xa01;

    /**
     * 网络请求成功的what
     */
    public static final int WHAT_ON_SUCCEED = 0xb02;

    /**
     * 请求数据无返回的异常
     */
    public static final String HTTP_NO_RESPONSE = "The server request is unresponsive code = ";

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Response response = (Response) msg.obj;
        OnHttpListener listener = response.listener();
        printLog(response);
        switch (msg.what) {
            case WHAT_ON_FAILURE:
                if (listener != null && response != null && response.body() != null) {
                    listener.onHttpFailure(response);
                }
                break;
            case WHAT_ON_SUCCEED:
                if (listener != null && response != null && response.body() != null) {
                    listener.onHttpSucceed(response);
                }
                break;
        }
    }

    /**
     * 发送异常消息
     *
     * @param requestParams 请求参数
     * @param url           请求地址
     * @param code          请求结果代码
     * @param e             异常
     * @param listener      网络请求监听
     */
    public void sendExceptionMsg(RequestParams requestParams, String url, int code, Exception e, OnHttpListener listener) {
        Message msg = obtainMessage();
        msg.what = Handler.WHAT_ON_FAILURE;
        Response response = new Response();
        response.requestParams(requestParams);
        response.url(url);
        response.exception(e);
        response.code(code);
        response.listener(listener);
        msg.obj = response;
        sendMessage(msg);
    }

    /**
     * 发送成功信息
     *
     * @param requestParams 请求参数
     * @param url           请求地址
     * @param code          请求结果代码
     * @param result        请求结果
     * @param listener      网络请求监听
     */
    public void sendSuccessfulMsg(RequestParams requestParams, String url, int code, String result, OnHttpListener listener) {
        Response response = new Response();
        response.body(result);
        response.url(url);
        response.requestParams(requestParams);
        response.code(code);
        response.listener(listener);
        Message msg = this.obtainMessage();
        msg.what = Handler.WHAT_ON_SUCCEED;
        msg.obj = response;
        sendMessage(msg);
    }

    /**
     * 打印调试日志
     *
     * @param httpResult
     */
    private void printLog(Response httpResult) {
        StringBuffer logBuffer = new StringBuffer("Program interface debug mode");
        logBuffer.append("\n");
        logBuffer.append("┌──────────────────────────────────────");
        logBuffer.append("\n");
        logBuffer.append("│" + httpResult.url());
        logBuffer.append("\n");
        logBuffer.append("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
        StringBuffer paramsBuffer = new StringBuffer("");
        if (httpResult.requestParams().getStringParams() != null) {
            for (String key : httpResult.requestParams().getStringParams().keySet()) {
                paramsBuffer.append("│\"" + key + "\":" + "\"" + httpResult.requestParams().getStringParams().get(key) + "\"");
                paramsBuffer.append("\n");
            }
        }
        if (httpResult.requestParams().getStringBody() != null) {
            paramsBuffer.append(httpResult.requestParams().getStringBody());
            paramsBuffer.append(",");
            paramsBuffer.append("│\"" + httpResult.requestParams().getStringBody() + "\"");
            paramsBuffer.append("\n");
        }
        if (paramsBuffer.toString().length() != 0) {
            logBuffer.append("\n");
            logBuffer.append(paramsBuffer);
            logBuffer.append("├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄");
        }
        if (httpResult != null) {
            logBuffer.append("\n");
            logBuffer.append("│\"" + "code:" + "\"" + httpResult.code() + "\"");
            logBuffer.append("\n");
            if (httpResult.exception() != null) {
                logBuffer.append("│  \"" + "exception:" + "\"" + httpResult.exception() + "\"");
                logBuffer.append("\n");
            }
        }
        logBuffer.append("│\"" + "body:" + httpResult.body());
        logBuffer.append("\n");
        logBuffer.append("└──────────────────────────────────────");
        Log.i(TAG, logBuffer.toString());
    }

}
