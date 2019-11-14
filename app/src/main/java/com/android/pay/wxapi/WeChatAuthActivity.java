package com.android.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.pay.net.OnPayHttpListener;
import com.android.pay.net.PayHttp;
import com.android.pay.net.PayHttpResponse;
import com.android.pay.net.PayJson;
import com.android.pay.net.PayRequestParams;
import com.android.pay.wechat.WeChatAccessToken;
import com.android.pay.wechat.WeChatConstants;
import com.android.pay.wechat.WeChatLogin;
import com.android.pay.wechat.WeChatUserInfo;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

/**
 * 微信授权页面
 *
 * @author RelinRan
 * @Date 2019-11-15 01:50
 */
public class WeChatAuthActivity extends Activity implements IWXAPIEventHandler, OnPayHttpListener {

    private IWXAPI api;
    private String TAG = "WXEntryActivity";
    private WeChatUserInfo userInfo;
    private WeChatAccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, true);
        api.registerApp(WeChatConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i(TAG, "[onReq] -> arg0:" + arg0);
    }

    @Override
    public void onResp(BaseResp resp) {
        int type = resp.getType();
        Log.i(TAG, "-[onResp]->type:" + type);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i(TAG, "-[onResp]-> 拒绝授权微信登录");
                sendMessage(WeChatConstants.AUTH_DENIED, "已拒绝授权微信登录");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                String message = "";
                if (type == WeChatConstants.LOGIN) {
                    message = "登录取消";
                }
                if (type == WeChatConstants.SHARE) {
                    message = "分享取消";
                }
                Log.i(TAG, "-[onResp]-> " + message);
                sendMessage(WeChatConstants.USER_CANCEL, message);
                break;
            case BaseResp.ErrCode.ERR_OK:
                Log.i(TAG, "-[onResp]-> 用户开始微信授权");
                String code = ((SendAuth.Resp) resp).code;
                sendMessage(WeChatConstants.USER_LOADING, "用户开始微信授权");
                reqAccessToken(code, WeChatConstants.APP_ID, WeChatConstants.APP_SECRET, WeChatConstants.GRANT_TYPE);
                break;
        }
        finish();
    }

    /**
     * 通过code获取access_token
     *
     * @param code       填写第一步获取的 code 参数
     * @param appid      应用唯一标识，在微信开放平台提交应用审核通过后获得
     * @param secret     应用密钥 AppSecret，在微信开放平台提交应用审核通过后获得
     * @param grant_type
     */
    private void reqAccessToken(String code, String appid, String secret, String grant_type) {
        PayRequestParams params = new PayRequestParams();
        params.add("code", code);
        params.add("appid", appid);
        params.add("secret", secret);
        params.add("grant_type", grant_type);
        PayHttp.get(this, WeChatConstants.URL_ACCESS_TOKEN, params, this);
    }

    /**
     * 获取用户个人信息（UnionID 机制）
     *
     * @param access_token 调用凭证
     * @param openid       普通用户的标识，对当前开发者帐号唯一
     * @param lang         国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为 zh-CN
     */
    private void reqUserInfo(String access_token, String openid, String lang) {
        PayRequestParams params = new PayRequestParams();
        params.add("openid", openid);
        params.add("access_token", access_token);
        params.add("lang", lang);
        PayHttp.get(this, WeChatConstants.URL_USER_INFO, params, this);
    }

    @Override
    public void onHttpSucceed(PayHttpResponse result) {
        if (result.url().contains("access_token")) {
            accessToken = PayJson.parseJSONObject(WeChatAccessToken.class, result.body());
            reqUserInfo(accessToken.getAccess_token(), accessToken.getOpenid(), "zh-CN");
        }
        if (result.url().contains("userinfo")) {
            userInfo = PayJson.parseJSONObject(WeChatUserInfo.class, result.body());
            Intent intent = new Intent(WeChatConstants.ACTION);
            intent.putExtra(WeChatConstants.USER_INFO, accessToken);
            intent.putExtra(WeChatConstants.ACCESS_TOKEN_INFO, userInfo);
            intent.putExtra(WeChatConstants.CODE, WeChatConstants.SUCCEED);
            intent.putExtra(WeChatConstants.MSG, "登录微信成功");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onHttpFailure(PayHttpResponse result) {
        Log.i(TAG, "-[onHttpFailure]-> msg:" + result.exception().toString());
    }

    /**
     * 发送信息
     *
     * @param code 代码类别
     * @param msg  信息
     */
    private void sendMessage(int code, String msg) {
        Intent intent = new Intent(WeChatConstants.ACTION);
        intent.putExtra(WeChatConstants.CODE, code);
        intent.putExtra(WeChatConstants.MSG, msg);
        sendBroadcast(intent);
    }

}