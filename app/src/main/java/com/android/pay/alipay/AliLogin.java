package com.android.pay.alipay;

import android.app.Activity;
import android.os.Bundle;

import com.alipay.sdk.app.OpenAuthTask;

import java.util.HashMap;
import java.util.Map;

public class AliLogin implements OpenAuthTask.Callback {

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
        this.listener = builder.listener;
        login();
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
     * 登录
     */
    private void login() {
        // 传递给支付宝应用的业务参数
        Map<String, String> bizParams = new HashMap<>();
        bizParams.put("url", "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=" + appId + "&scope=auth_user&state=init");
        // 唤起授权业务
        OpenAuthTask task = new OpenAuthTask(activity);
        task.execute(scheme, OpenAuthTask.BizType.AccountAuth, bizParams, this, true);
    }

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
            listener.onAliLogin(resultCode, memo, user);
        }
    }
    
}
