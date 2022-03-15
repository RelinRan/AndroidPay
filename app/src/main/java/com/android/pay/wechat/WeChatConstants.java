package com.android.pay.wechat;

import com.tencent.mm.opensdk.modelbase.BaseResp;

/**
 * 微信常量</br>
 */
public class WeChatConstants {

    /**
     * 微信appId
     */
    public static String APP_ID;
    /**
     * 微信secret
     */
    public static String APP_SECRET;
    /**
     * 微信Token地址
     */
    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 微信用户信息地址
     */
    public static final String URL_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 微信刷新Token
     */
    public static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    /**
     * 微信授权类型
     */
    public static final String GRANT_TYPE = "authorization_code";
    /**
     * 微信Action
     */
    public static final String ACTION = "ACTION_COM_ANDROID_PAY_WX_ACTION";
    /**
     * 微信授权失败
     */
    public static final int AUTH_DENIED = BaseResp.ErrCode.ERR_AUTH_DENIED;
    /**
     * 微信用户取消登录
     */
    public static final int CANCEL = BaseResp.ErrCode.ERR_USER_CANCEL;
    /**
     * 平台参数不一致
     */
    public static final int ERR_BAN = BaseResp.ErrCode.ERR_BAN;
    /**
     * 微信用户正在登录
     */
    public static final int LOADING = BaseResp.ErrCode.ERR_OK;
    /**
     * 微信登录
     */
    public static final int LOGIN = 1;
    /**
     * 微信分享
     */
    public static final int SHARE = 2;
    /**
     * 成功
     */
    public static final int SUCCEED = 1;
    /**
     * 失败
     */
    public static final int FAILED = -1;
    /**
     * 代码
     */
    public static final String CODE = "code";
    /**
     * 消息
     */
    public static final String MSG = "msg";
    /**
     * 用户信息
     */
    public static final String USER_INFO = "user_info";
    /**
     * 授权信息
     */
    public static final String ACCESS_TOKEN_INFO = "access_token_info";

}
