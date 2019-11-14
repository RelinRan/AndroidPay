package com.android.pay.wechat;

import com.tencent.mm.opensdk.modelbase.BaseResp;

public class WeChatConstants {

    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";


    public static final String URL_USER_INFO= "https://api.weixin.qq.com/sns/userinfo";

    public static String APP_ID;

    public static String APP_SECRET;

    public static final String GRANT_TYPE = "authorization_code";

    /**
     * 微信登录Action
     */
    public static final String ACTION = "ACTION_COM_ANDROID_PAY_WX_LOGIN";

    /**
     * 微信授权失败
     */
    public static final int AUTH_DENIED = BaseResp.ErrCode.ERR_AUTH_DENIED;

    /**
     * 微信用户取消登录
     */
    public static final int USER_CANCEL = BaseResp.ErrCode.ERR_USER_CANCEL;

    /**
     * 微信用户正在登录
     */
    public static final int USER_LOADING = BaseResp.ErrCode.ERR_OK;


    public static final int LOGIN = 1;
    public static final int SHARE = 2;

    public static final int SUCCEED = 1;
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String USER_INFO = "user_info";
    public static final String ACCESS_TOKEN_INFO = "access_token_info";

}
