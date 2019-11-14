package com.android.pay.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.H5PayActivity;
import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * Created by Ice on 2016/2/17.
 * 支付宝支付类
 */
public class AliPay {

    /**
     * 支付标志
     */
    private static final int SDK_PAY_FLAG = 1;


    /**
     * app支付请求参数字符串，主要包含商户的订单信息，key=value形式，以&连接。
     */
    public final String orderInfo;

    /**
     * 支付页面的Activity
     */
    public final Activity activity;

    /**
     * 支付回调函数
     */
    public final OnAliPayListener listener;

    /**
     * 用户在商户app内部点击付款，是否需要一个loading做为在钱包唤起之前的过渡，这个值设置为true，
     * 将会在调用pay接口的时候直接唤起一个loading，直到唤起H5支付页面或者唤起外部的钱包付款页面loading才消失。
     * （建议将该值设置为true，优化点击付款到支付唤起支付页面的过渡过程。）
     */
    public final boolean loading;

    /**
     * H5网页支付地址
     */
    public final String h5Url;


    public AliPay(Builder builder) {
        this.activity = builder.activity;
        this.orderInfo = builder.orderInfo;
        this.h5Url = builder.h5Url;
        this.listener = builder.listener;
        this.loading = builder.loading;
        pay();
    }


    public static class Builder {

        private String orderInfo;
        private String h5Url;
        private Activity activity;
        private OnAliPayListener listener;
        private boolean loading = true;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public String orderInfo() {
            return orderInfo;
        }

        public Builder orderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public String h5Url() {
            return h5Url;
        }

        public Builder h5Url(String h5Url) {
            this.h5Url = h5Url;
            return this;
        }

        public Activity activity() {
            return activity;
        }

        public OnAliPayListener listener() {
            return listener;
        }

        public Builder listener(OnAliPayListener listener) {
            this.listener = listener;
            return this;
        }

        public boolean isLoading() {
            return loading;
        }

        public Builder loading(boolean loading) {
            this.loading = loading;
            return this;
        }

        public AliPay build() {
            return new AliPay(this);
        }
    }

    /**
     * 支付
     */
    public void pay() {
        if (orderInfo != null) {
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask task = new PayTask(activity);
                    Map<String, String> result = task.payV2(orderInfo, loading);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }
        if (h5Url != null) {
            h5Pay(h5Url);
        }
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
     * 需要声明H5PayActivity
     *
     * @param url url可以是一号店或者美团等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
     *            url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
     *            demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
     *            商户可以根据自己的需求来实现
     */
    public void h5Pay(String url) {
        getSDKVersion();
        Intent intent = new Intent(activity, H5PayActivity.class);
        Bundle extras = new Bundle();
        extras.putString("url", h5Url);
        intent.putExtras(extras);
        activity.startActivity(intent);
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(activity);
        String version = payTask.getVersion();
        Log.i(this.getClass().getSimpleName(), "getSDKVersion is " + version);
    }


    /**
     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    if (listener != null) {
                        listener.onAliPay(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}
