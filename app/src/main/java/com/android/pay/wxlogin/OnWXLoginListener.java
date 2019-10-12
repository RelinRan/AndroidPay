package com.android.pay.wxlogin;

/**
 * 微信登录回调
 */
public interface OnWXLoginListener {


    /***
     * 微信登录回调信息
     * @param user 用户信息
     */
    void onWXLogin(int code,String msg,WXUser user);

}
