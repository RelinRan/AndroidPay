package com.android.pay.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatLogin {

    /**
     * 微信appId
     */
    public final String appId;

    /**
     * 上下文对象
     */
    public final Context context;

    /**
     * 微信Secret
     */
    public final String appSecret;

    /**
     * 登录接收器
     */
    private WeChatReceiver receiver;

    /**
     * 登录监听
     */
    public final OnWeChatLoginListener listener;

    /**
     * 微信登录构造函数
     *
     * @param builder 构造器
     */
    public WeChatLogin(Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.appSecret = builder.appSecret;
        this.listener = builder.listener;
        WeChatConstants.APP_ID = appId;
        WeChatConstants.APP_SECRET = appSecret;
        if (listener != null && context != null && receiver == null) {
            receiver = new WeChatReceiver();
            IntentFilter filter = new IntentFilter(WeChatConstants.ACTION);
            context.registerReceiver(receiver, filter);
        }
        login(appId);
    }

    public static class Builder {
        /**
         * 上下文对象
         */
        private Context context;

        /**
         * 微信Secret
         */
        private String appSecret;

        /**
         * 微信appId
         */
        private String appId;

        private OnWeChatLoginListener listener;

        public Builder(Context context) {
            this.context = context;

        }

        /**
         * 获取上下文对象
         *
         * @return Context
         */
        public Context context() {
            return context;
        }


        /**
         * 获取微信appSecret
         *
         * @return String
         */
        public String appSecret() {
            return appSecret;
        }

        /**
         * 设置微信appSecret
         *
         * @return Builder
         */
        public Builder appSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        /**
         * 获取微信AppId
         *
         * @return
         */
        public String appId() {
            return appId;
        }

        /**
         * 设置微信AppId
         *
         * @param appId
         * @return
         */
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 登录监听
         *
         * @return
         */
        public OnWeChatLoginListener listener() {
            return listener;
        }

        /**
         * 设置登录监听
         *
         * @param listener
         * @return
         */
        public Builder listener(OnWeChatLoginListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 构建登录对象
         *
         * @return WxLogin
         */
        public WeChatLogin build() {
            return new WeChatLogin(this);
        }
    }

    /**
     * 登录
     *
     * @param appId
     */
    private void login(String appId) {
        WeChatConstants.APP_ID = appId;
        IWXAPI wxAPI = WXAPIFactory.createWXAPI(context, appId, true);
        wxAPI.registerApp(appId);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());
        wxAPI.sendReq(req);
    }

    /**
     * 登录接收器
     */
    private class WeChatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WeChatConstants.ACTION)) {
                int code = intent.getIntExtra(WeChatConstants.CODE, -200);
                String msg = intent.getStringExtra(WeChatConstants.MSG);
                WeChatUser user = null;
                if (intent.getSerializableExtra(WeChatConstants.USER_INFO) != null) {
                    user = (WeChatUser) intent.getSerializableExtra(WeChatConstants.USER_INFO);
                }
                if (listener != null) {
                    listener.onWeChatLogin(code, msg, user);
                }
                if (context != null && receiver != null && (code == WeChatConstants.SUCCEED || code == WeChatConstants.USER_CANCEL || code == WeChatConstants.AUTH_DENIED)) {
                    context.unregisterReceiver(receiver);
                }
            }
        }
    }

}
