package com.android.pay.alipay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.OpenAuthTask;

import java.util.HashMap;
import java.util.Map;

public class AliLogin implements OpenAuthTask.Callback {

    private final int AUTH_V2 = 0x002;

    /**
     * 调用成功
     */
    public static final int OK = OpenAuthTask.OK;

    /**
     * 3s 内快速发起了多次支付 / 授权调用。稍后重试即可。
     */
    public static final int DUPLEX = OpenAuthTask.Duplex;

    /**
     * 用户未安装支付宝 App
     */
    public static final int NOT_INSTALLED = OpenAuthTask.NOT_INSTALLED;

    /**
     * 其它错误，如参数传递错误
     */
    public static final int SYS_ERR = OpenAuthTask.SYS_ERR;

    /**
     * 用户取消
     */
    public static final int CANCEL = 6001;

    /**
     * 网络连接出错
     */
    public static final int NET_ERROR = 6002;

    /**
     * 登录页面
     */
    public final Activity activity;

    /**
     * 支付宝平台APP_ID
     */
    public final String appId;

    /**
     * 支付宝回跳到你的应用时使用的 Intent Scheme。请设置为不和其它应用冲突的值，需要在你的 App 的 AndroidManifest.xml 中添加这一项
     */
    public final String scheme;

    /**
     * 授权信息
     */
    private final String authInfo;

    /**
     * 是否显示loading
     */
    private final boolean isShowLoading;

    /**
     * 登录回调监听
     */
    public final OnAliLoginListener listener;

    /**
     * 登录构造函数
     *
     * @param builder
     */
    public AliLogin(Builder builder) {
        this.activity = builder.activity;
        this.scheme = builder.scheme;
        this.appId = builder.appId;
        this.authInfo = builder.authInfo;
        this.isShowLoading = builder.isShowLoading;
        this.listener = builder.listener;
        auth();
        authV2(authInfo, isShowLoading);
    }


    public static class Builder {

        /**
         * 登录构建者
         *
         * @param activity 登录页面
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 登录页面
         */
        private Activity activity;

        /**
         * 支付宝平台ID
         */
        private String appId;

        /**
         * 支付宝回跳到你的应用时使用的 Intent Scheme。请设置为不和其它应用冲突的值。
         */
        private String scheme;

        /**
         * 授权信息
         */
        private String authInfo;

        /**
         * 是否显示Loading
         */
        private boolean isShowLoading;

        /**
         * 登录监听
         */
        private OnAliLoginListener listener;

        /**
         * 获取当前登录页面
         *
         * @return
         */
        public Activity activity() {
            return activity;
        }

        /**
         * 平台ID
         *
         * @return
         */
        public String appId() {
            return appId;
        }

        /**
         * 设置平台ID
         *
         * @param appId
         * @return
         */
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 支付宝回跳到你的应用时使用的 Intent Scheme
         *
         * @return
         */
        public String scheme() {
            return appId;
        }

        /**
         * 设置支付宝回跳到你的应用时使用的 Intent Scheme，需要在你的 App 的 AndroidManifest.xml 中添加这一项
         *
         * @param scheme
         * @return
         */
        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        /**
         * 设置授权信息(完成版使用)
         *
         * @param authInfo
         * @return
         */
        public Builder authInfo(String authInfo) {
            this.authInfo = authInfo;
            return this;
        }

        /**
         * 是否显示Loading(完成版使用)
         *
         * @param isShowLoading
         * @return
         */
        public Builder showLoading(boolean isShowLoading) {
            this.isShowLoading = isShowLoading;
            return this;
        }

        /**
         * 获取登录监听
         *
         * @return
         */
        public OnAliLoginListener listener() {
            return listener;
        }

        /**
         * 设置登录监听
         *
         * @param listener
         * @return
         */
        public Builder listener(OnAliLoginListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 构建登录对象
         *
         * @return
         */
        public AliLogin build() {
            return new AliLogin(this);
        }
    }

    /**
     * 授权版本一（极简版 ）
     */
    private void auth() {
        if (appId == null || scheme == null) {
            return;
        }
        // 传递给支付宝应用的业务参数
        Map<String, String> bizParams = new HashMap<>();
        bizParams.put("url", "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=" + appId + "&scope=auth_user&state=init");
        // 唤起授权业务
        OpenAuthTask task = new OpenAuthTask(activity);
        task.execute(scheme, OpenAuthTask.BizType.AccountAuth, bizParams, this, true);
    }

    /**
     * 授权版本二（完整版）
     */
    private void authV2(final String authInfo, final boolean isShowLoading) {
        if (authInfo == null) {
            return;
        }
        Log.i(this.getClass().getSimpleName(), "->authV2 authInfo:" + authInfo);
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(activity);
                // 获取授权结果。
                Map<String, String> result = authTask.authV2(authInfo, isShowLoading);
                Log.i(this.getClass().getSimpleName(), "->authV2 result:" + result.toString());
                Message message = handler.obtainMessage();
                message.what = AUTH_V2;
                message.obj = result;
                handler.sendMessage(message);
            }
        };
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == AUTH_V2) {
                Map<String, String> info = (Map<String, String>) msg.obj;
                String result = info.get("result");
                AliUser user = new AliUser();
                if (result.contains("&")) {
                    String results[] = result.split("&");
                    for (int i = 0; i < results.length; i++) {
                        String item = results[i];
                        String keyValues[] = item.split("=");
                        info.put(keyValues[0], keyValues[1]);
                    }
                }
                user.setResultStatus(info.get("resultStatus"));
                user.setAuthCode(info.get("auth_code"));
                user.setResultCode(info.get("result_code"));
                user.setAliPayOpenId(info.get("alipay_open_id"));
                user.setUserId(info.get("user_id"));
                Log.i(this.getClass().getSimpleName(), "-> auth v2 result:" + user.toString());
                if (listener != null) {
                    int code = user.getResultCode().equals("200") && user.getResultStatus().equals(OK + "") ? OK : Integer.parseInt(user.getResultCode());
                    listener.onAliLogin(code, info.get("memo"), user);
                }
            }
        }
    };

    /**
     * 登录结果
     *
     * @param resultCode
     * @param memo
     * @param bundle
     */
    @Override
    public void onResult(int resultCode, String memo, Bundle bundle) {
        if (listener != null) {
            AliUser user = new AliUser();
            user.setAppId(bundle.getString("app_id"));
            user.setResultCode(bundle.getString("result_code"));
            user.setScope(bundle.getString("scope"));
            user.setState(bundle.getString("state"));
            user.setAuthCode(bundle.getString("auth_code"));
            Log.i(this.getClass().getSimpleName(), "-> auth v1 result:" + user.toString());
            listener.onAliLogin(resultCode, memo, user);
        }
    }

}
