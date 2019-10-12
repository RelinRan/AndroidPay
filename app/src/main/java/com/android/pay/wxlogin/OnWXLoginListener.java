package com.android.pay.wxlogin;

/**
 * 微信登录回调
 */
public interface OnWXLoginListener {


    /**
     * 微信登录回调信息
     *
     * @param code
     * @param msg
     * @param user
     */
    void onWXLogin(int code, String msg, WXUser user);

}
