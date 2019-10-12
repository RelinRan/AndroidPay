package com.android.pay.wxpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.android.pay.net.HttpResponse;
import com.android.pay.net.HttpUtils;
import com.android.pay.net.OnHttpListener;
import com.android.pay.net.RequestParams;
import com.android.pay.wxlogin.OnWXLoginListener;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Inflater;


/**
 * Created by Ice on 2016/2/18.
 * 微信支付
 * #WXPay
 * APP_ID
 * MCH_ID
 * API_KEY
 */
public class WxPay implements OnHttpListener {

    //微信支付的广播
    public static final String ACTION_PAY_FINISH = "ACTION_PAY_FINISH";
    //0:success,-1:fail,-2:cancel [type:int]
    public static final String PAY_RESULT = "PAY_RESULT";
    public static final String PAY_MSG = "PAY_MSG";
    public static final int PAY_CODE_SUCCEED = 0;
    public static final int PAY_CODE_FAILED = -1;
    public static final int PAY_CODE_CANCEL = -2;

    public static String APP_ID;

    private IWXAPI iwxapi;

    public final Context context;

    //旧版本参数
    public final String appId;
    public final String mchId;
    public final String apiKey;
    public final String outTradeNo;
    public final String body;
    public final String totalFree;
    public final String notifyUrl;

    //版本二的参数
    public final String partnerId;
    public final String prepayId;
    public final String nonceStr;
    public final String timeStamp;
    public final String packageValue;
    public final String sign;
    public final String extData;

    public final OnWXPayListener listener;

    private WXPayReceiver receiver;

    public static class Builder {

        private Context context;
        //旧版本参数
        private String appId;
        private String mchId;
        private String apiKey;
        private String outTradeNo;
        private String body;
        private String totalFree;
        private String notifyUrl;

        //版本二的参数
        private String partnerId;
        private String prepayId;
        private String nonceStr;
        private String timeStamp;
        private String packageValue = "Sign=WXPay";
        private String sign;
        private String extData;

        private OnWXPayListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public String appId() {
            return appId;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public String mchId() {
            return mchId;
        }

        public Builder mchId(String mchId) {
            this.mchId = mchId;
            return this;
        }

        public String apiKey() {
            return apiKey;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public String outTradeNo() {
            return outTradeNo;
        }

        public Builder outTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
            return this;
        }

        public String totalFree() {
            return totalFree;
        }

        public Builder totalFree(String totalFree) {
            this.totalFree = totalFree;
            return this;
        }

        public String body() {
            return body;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public String notifyUrl() {
            return notifyUrl;
        }

        public Builder notifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
            return this;
        }

        public String partnerId() {
            return partnerId;
        }

        public Builder partnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public String prepayId() {
            return prepayId;
        }

        public Builder prepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        public String nonceStr() {
            return nonceStr;
        }

        public Builder nonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public String timeStamp() {
            return timeStamp;
        }

        public Builder timeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public String packageValue() {
            return packageValue;
        }

        public Builder packageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        public String sign() {
            return sign;
        }

        public Builder sign(String sign) {
            this.sign = sign;
            return this;
        }

        public String extData() {
            return extData;
        }

        public Builder extData(String extData) {
            this.extData = extData;
            return this;
        }

        public OnWXPayListener listener() {
            return listener;
        }

        public Builder listener(OnWXPayListener listener) {
            this.listener = listener;
            return this;
        }

        public WxPay build() {
            return new WxPay(this);
        }

    }

    public WxPay(Builder builder) {
        this.context = builder.context;

        this.appId = builder.appId;
        this.mchId = builder.mchId;
        this.apiKey = builder.apiKey;
        this.outTradeNo = builder.outTradeNo;
        this.body = builder.body;
        this.totalFree = builder.totalFree;
        this.notifyUrl = builder.notifyUrl;

        this.partnerId = builder.partnerId;
        this.prepayId = builder.prepayId;
        this.nonceStr = builder.nonceStr;
        this.timeStamp = builder.timeStamp;
        this.packageValue = builder.packageValue;
        this.sign = builder.sign;
        this.extData = builder.extData;

        this.listener = builder.listener;
        if (listener != null && receiver != null) {
            IntentFilter filter = new IntentFilter(WxPay.ACTION_PAY_FINISH);
            receiver = new WXPayReceiver();
            context.registerReceiver(receiver, filter);
        }

        APP_ID = appId;
        iwxapi = WXAPIFactory.createWXAPI(context, appId);
        pay();
    }

    public void pay() {
        //新版本
        if (sign != null) {
            PayReq req = new PayReq();
            req.appId = appId;
            req.partnerId = partnerId;
            req.prepayId = prepayId;
            req.nonceStr = nonceStr;
            req.timeStamp = timeStamp;
            req.packageValue = packageValue;
            req.sign = sign;
            req.extData = extData;
            Log.i(this.getClass().getSimpleName(), "appId:" + appId + ",partnerId:" + partnerId + ",prepayId:" + prepayId + ",nonceStr:" + nonceStr + ",timeStamp:" + timeStamp + ",packageValue:" + packageValue + ",sign:" + sign + ",extData:" + extData);
            iwxapi.registerApp(appId);
            iwxapi.sendReq(req);
        }
        //旧版本
        if (apiKey != null) {
            try {
                getPrepayId(outTradeNo, body, totalFree, notifyUrl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 下单获取PrepayId
     */
    private void getPrepayId(String out_trade_no, String body, String total_free, String notify_url) throws UnsupportedEncodingException {
        TreeMap<String, String> params = new TreeMap<>();
        //公众账号ID
        params.put("appid", appId);
        //商品描述
        params.put("body", body);
        //商户号
        params.put("mch_id", mchId);
        //随机字符串
        params.put("nonce_str", WxUtils.getNonceStr());
        //异步通知Url
        params.put("notify_url", notify_url);
        //商户订单号
        params.put("out_trade_no", out_trade_no == null ? WxUtils.getOutTradNo() : out_trade_no);
        //终端IP
        params.put("spbill_create_ip", WxUtils.getLocalIpAddress(context));
        //总金额
        params.put("total_fee", WxUtils.parseMoneyToFen(total_free));
        //交易类型 https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=4_2
        params.put("trade_type", "APP");
        //签名
        String sign = WxUtils.getSign(context, apiKey, params);
        params.put("sign", sign);
        //参数转xml
        String xmlString = WxUtils.paramsToXml(params);
        Log.i(this.getClass().getSimpleName(), "getPrepayId 参数：" + xmlString);
        //xutils工具类不需要字符转换
        String prePayXml = new String(xmlString.toString().getBytes(), "ISO8859-1");
        RequestParams requestParameter = new RequestParams();
        requestParameter.addStringBody(prePayXml);
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HttpUtils.post(context, url, requestParameter, this);
    }

    @Override
    public void onHttpSucceed(HttpResponse result) {
        Map<String, String> map = WxUtils.decodeXml(result.body());
        Log.i(this.getClass().getSimpleName(), "onHttpSuccess result：" + result);
        PayReq payReq = new PayReq();
        payReq.appId = appId;
        payReq.partnerId = mchId;
        payReq.prepayId = map.get("prepay_id");
        payReq.packageValue = "Sign=WXPay";
        payReq.nonceStr = WxUtils.getNonceStr();
        payReq.timeStamp = String.valueOf(WxUtils.getTimeStamp());
        //签名
        TreeMap<String, String> signParams = new TreeMap<>();
        signParams.put("appid", payReq.appId);
        signParams.put("noncestr", payReq.nonceStr);
        signParams.put("package", payReq.packageValue);
        signParams.put("partnerid", payReq.partnerId);
        signParams.put("prepayid", payReq.prepayId);
        signParams.put("timestamp", payReq.timeStamp);
        payReq.sign = WxUtils.getSign(context, apiKey, signParams);
        //注册App
        iwxapi.registerApp(appId);
        //调用支付
        iwxapi.sendReq(payReq);
    }

    @Override
    public void onHttpFailure(HttpResponse result) {

    }

    private class WXPayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WxPay.ACTION_PAY_FINISH)) {
                int code = intent.getIntExtra(WxPay.PAY_RESULT, -1);
                String msg = intent.getStringExtra(WxPay.PAY_MSG);
                if (listener != null) {
                    listener.onWXPay(code, msg);
                }
                if (context != null && receiver != null) {
                    context.unregisterReceiver(receiver);
                }
            }
        }
    }


}
