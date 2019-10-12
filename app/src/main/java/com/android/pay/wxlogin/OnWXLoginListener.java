package com.android.pay.wxlogin;

/**
 * 微信登录回调
 */
public interface OnWXLoginListener {

    /**
     * 登录中
     */
    void onWXLoginLoading();

    /***
     * 微信登录回调信息
     * @param user 用户信息
     */
    void onWXLoginSucceed(WXUser user);

    /**
     * 登录失败
     *
     * @param code 失败代码
     * @param msg  失败描述
     */
    void onWXLoginFailed(int code, String msg);

}
