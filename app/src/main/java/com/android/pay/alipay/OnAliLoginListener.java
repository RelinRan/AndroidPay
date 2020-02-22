package com.android.pay.alipay;

public interface OnAliLoginListener {

    /**
     * 支付宝登录回调
     *
     * @param code 例如{@link AliLogin#OK}判断用户登录成功
     * @param memo       提示信息
     * @param user       用户信息
     */
    void onAliLogin(int code, String memo, AliUser user);

}
