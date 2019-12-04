package com.android.pay.wechat;

/**
 * 微信分享回调
 */
public interface OnWeChatShareListener {


    /**
     * 微信分享回调
     *
     * @param code 代码
     * @param msg  信息
     */
    void onWeChatShare(int code, String msg);

}
