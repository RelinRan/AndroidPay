package com.android.pay.alipay;

public class AliUser {

    /**
     * 支付宝分配给开发者的应用 ID
     */
    private String appId;
    /**
     * 结果码
     */
    private String resultCode;
    /**
     * 极简版 SDK 固定参数，传其他参数无效。auth_base 为用户基础授权，仅用于静默获取用户支付宝 UID；auth_user 获取用户信息，网站支付宝登录。
     */
    private String scope;
    /**
     * OAuth 2 协议参数，可设置为随机字符串的 base64 编码（100位以内）
     */
    private String state;
    /**
     * 授权码
     */
    private String authCode;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
