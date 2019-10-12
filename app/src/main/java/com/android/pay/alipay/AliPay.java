package com.android.pay.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.H5PayActivity;
import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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
     * 旧版本
     */
    public static int VERSION_OLD = 1;

    /**
     * 新版本
     */
    public static int VERSION_NEW = 2;

    /**
     * 支付版本
     */
    public final int version;

    /**
     * 商户ID
     */
    public final String partner;


    /**
     * 商户收款账号（卖家支付宝账号）
     */
    public final String sellerId;

    /**
     * 商户私钥，pkcs8格式
     */
    public final String rsaPrivate;

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
    public final AliPayListener listener;

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

    /**
     * 商品编号
     */
    public final String orderNo;

    /**
     * 商品名称
     */
    public final String goodName;

    /**
     * 商品详情
     */
    public final String goodDetail;

    /**
     * 商品详情
     */
    public final String goodPrice;

    /**
     * 支付回调URL
     */
    public final String notifyUrl;


    public AliPay(Builder builder) {
        this.activity = builder.activity;
        this.version = builder.version;
        this.partner = builder.partner;
        this.sellerId = builder.sellerId;
        this.rsaPrivate = builder.rsaPrivate;
        this.orderInfo = builder.orderInfo;
        this.h5Url = builder.h5Url;
        this.listener = builder.listener;
        this.loading = builder.loading;
        this.orderNo = builder.orderNo;
        this.goodName = builder.goodName;
        this.goodDetail = builder.goodDetail;
        this.goodPrice = builder.goodPrice;
        this.notifyUrl = builder.notifyUrl;
        pay();
    }


    public static class Builder {

        private int version = VERSION_NEW;
        private String partner;
        private String rsaPrivate;
        private String sellerId;
        private String orderInfo;
        private String h5Url;
        private Activity activity;
        private AliPayListener listener;
        private boolean loading = true;

        private String orderNo;
        private String goodName;
        private String goodDetail;
        private String goodPrice;
        private String notifyUrl;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public int version() {
            return version;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public String partner() {
            return partner;
        }

        public Builder partner(String partner) {
            this.partner = partner;
            return this;
        }

        public String rsaPrivate() {
            return rsaPrivate;
        }

        public Builder rsaPrivate(String rsaPrivate) {
            this.rsaPrivate = rsaPrivate;
            return this;
        }

        public String orderInfo() {
            return orderInfo;
        }

        public Builder orderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public String sellerId() {
            return sellerId;
        }

        public Builder sellerId(String sellerId) {
            this.sellerId = sellerId;
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

        public AliPayListener listener() {
            return listener;
        }

        public Builder listener(AliPayListener listener) {
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

        public String orderNo() {
            return orderNo;
        }

        public void orderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String goodName() {
            return goodName;
        }

        public Builder goodName(String goodName) {
            this.goodName = goodName;
            return this;
        }

        public String goodDetail() {
            return goodDetail;
        }

        public Builder goodDetail(String goodDetail) {
            this.goodDetail = goodDetail;
            return this;
        }

        public String goodPrice() {
            return goodPrice;
        }

        public Builder goodPrice(String goodPrice) {
            this.goodPrice = goodPrice;
            return this;
        }

        public String notifyUrl() {
            return notifyUrl;
        }

        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
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
        if (version == VERSION_NEW || orderInfo != null) {
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
        if (version == VERSION_OLD) {
            getSDKVersion();
            String orderInfo = getOrderInfo(orderNo, goodName, goodDetail, goodPrice, notifyUrl);
            /**
             * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
             */
            String sign = sign(orderInfo);
            try {
                /**
                 * 仅需对sign 做URL编码
                 */
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /**
             * 完整的符合支付宝参数规范的订单信息
             */
            final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(activity);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(payInfo, loading);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            // 必须异步调用
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
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String order_no, String subject, String body, String price, String notify_url) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + sellerId + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + (order_no == null ? getOutTradeNo() : order_no) + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        Log.i(getClass().getSimpleName(), "getOrderInfo notifyurl:" + notify_url);
        orderInfo += "&notify_url=" + "\"" + (notify_url == null ? "http://notify.msp.hk/notify.htm" : notify_url) + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"test\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, rsaPrivate);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /*
    {
    "memo" : "xxxxx",
    "result" : "{
                    \"alipay_trade_app_pay_response\":{
                        \"code\":\"10000\",
                        \"msg\":\"Success\",
                        \"app_id\":\"2014072300007148\",
                        \"out_trade_no\":\"081622560194853\",
                        \"trade_no\":\"2016081621001004400236957647\",
                        \"total_amount\":\"0.01\",
                        \"seller_id\":\"2088702849871851\",
                        \"charset\":\"utf-8\",
                        \"timestamp\":\"2016-10-11 17:43:36\"
                    },
                    \"sign\":\"NGfStJf3i3ooWBuCDIQSumOpaGBcQz+aoAqyGh3W6EqA/gmyPYwLJ2REFijY9XPTApI9YglZyMw+ZMhd3kb0mh4RAXMrb6mekX4Zu8Nf6geOwIa9kLOnw0IMCjxi4abDIfXhxrXyj********\",
                    \"sign_type\":\"RSA2\"
                }",
    "resultStatus" : "9000"
    }
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    if (version == 1) {//旧版本支付
                        PayResult payResult = new PayResult((String) msg.obj);
                        /**
                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                         * docType=test) 建议商户依赖异步通知
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            if (listener != null) {
                                listener.aliPaySuccess(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                            }
                        } else {
                            // 判断resultStatus 为非"9000"则代表可能支付失败
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                if (listener != null) {
                                    listener.aliPaying(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                                }
                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                if (listener != null) {
                                    listener.aliPayFail(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                                }
                            }
                        }
                    } else if (version == 2) {//新版本支付
                        PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            if (listener != null) {
                                listener.aliPaySuccess(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                            }
                        } else if (TextUtils.equals(resultStatus, "8000")) {
                            // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                            if (listener != null) {
                                listener.aliPaying(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                            }
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            if (listener != null) {
                                listener.aliPayFail(payResult.getResultStatus(), payResult.getResult(), payResult.getMemo());
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };
}
