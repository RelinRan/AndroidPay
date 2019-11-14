package com.android.pay.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by Ice on 2016/2/18.
 * 微信支付
 * #WXPay
 * APP_ID
 * MCH_ID
 * API_KEY
 */
public class WeChatPay {

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
    public final String appId;
    public final String partnerId;
    public final String prepayId;
    public final String nonceStr;
    public final String timeStamp;
    public final String packageValue;
    public final String sign;
    public final String extData;

    public final OnWeChatPayListener listener;

    private WXPayReceiver receiver;

    public static class Builder {

        private Context context;
        private String appId;
        private String partnerId;
        private String prepayId;
        private String nonceStr;
        private String timeStamp;
        private String packageValue = "Sign=WXPay";
        private String sign;
        private String extData;

        private OnWeChatPayListener listener;

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

        public OnWeChatPayListener listener() {
            return listener;
        }

        public Builder listener(OnWeChatPayListener listener) {
            this.listener = listener;
            return this;
        }

        public WeChatPay build() {
            return new WeChatPay(this);
        }

    }

    public WeChatPay(Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.partnerId = builder.partnerId;
        this.prepayId = builder.prepayId;
        this.nonceStr = builder.nonceStr;
        this.timeStamp = builder.timeStamp;
        this.packageValue = builder.packageValue;
        this.sign = builder.sign;
        this.extData = builder.extData;
        this.listener = builder.listener;
        if (listener != null && receiver == null) {
            IntentFilter filter = new IntentFilter(WeChatPay.ACTION_PAY_FINISH);
            receiver = new WXPayReceiver();
            context.registerReceiver(receiver, filter);
        }
        APP_ID = appId;
        iwxapi = WXAPIFactory.createWXAPI(context, appId);
        pay();
    }

    public void pay() {
        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = packageValue;
        req.sign = sign;
        req.extData = extData;
        Log.i(this.getClass().getSimpleName(), "-[pay]->" + "appId:" + appId + ",partnerId:" + partnerId + ",prepayId:" + prepayId + ",nonceStr:" + nonceStr + ",timeStamp:" + timeStamp + ",packageValue:" + packageValue + ",sign:" + sign + ",extData:" + extData);
        iwxapi.registerApp(appId);
        iwxapi.sendReq(req);
    }


    private class WXPayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WeChatPay.ACTION_PAY_FINISH)) {
                int code = intent.getIntExtra(WeChatPay.PAY_RESULT, -1);
                String msg = intent.getStringExtra(WeChatPay.PAY_MSG);
                if (listener != null) {
                    listener.onWeChatPay(code, msg);
                }
                if (context != null && receiver != null) {
                    context.unregisterReceiver(receiver);
                }
            }
        }
    }


}
