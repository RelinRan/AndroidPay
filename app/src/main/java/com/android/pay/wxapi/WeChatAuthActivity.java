package com.android.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.pay.net.Http;
import com.android.pay.net.JSON;
import com.android.pay.net.OnHttpListener;
import com.android.pay.net.RequestParams;
import com.android.pay.net.Response;
import com.android.pay.wechat.WeChatAccessToken;
import com.android.pay.wechat.WeChatConstants;
import com.android.pay.wechat.WeChatUser;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信授权页面
 */
public class WeChatAuthActivity extends Activity implements IWXAPIEventHandler, OnHttpListener {

    private IWXAPI api;
    private String TAG = "WeChatAuthActivity";
    private int type;
    private WeChatUser userInfo;
    private WeChatAccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "[onCreate]");
        api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, false);
        api.registerApp(WeChatConstants.APP_ID);
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i(TAG, "[onReq] -> arg0:" + arg0);
    }

    @Override
    public void onResp(BaseResp resp) {
        type = resp.getType();
        Bundle bundle = new Bundle();
        resp.toBundle(bundle);
        Log.i(TAG, "-[onResp]->type:" + type + ",code:" + resp.errCode + ",openId:" + resp.openId + ",bundle:" + bundle.toString());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_BAN:
                Log.i(TAG, "-[onResp]-> 运行参数与平台配置参数不一致");
                sendMessage(WeChatConstants.ERR_BAN, "运行参数与平台配置参数不一致");
                break;
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
                sendMessage(WeChatConstants.CANCEL, message);
                break;
            case BaseResp.ErrCode.ERR_OK:
                if (type == WeChatConstants.LOGIN) {
                    Log.i(TAG, "-[onResp]-> 用户开始微信授权");
                    String code = ((SendAuth.Resp) resp).code;
                    sendMessage(WeChatConstants.LOADING, "用户开始微信授权");
                    reqAccessToken(code, WeChatConstants.APP_ID, WeChatConstants.APP_SECRET, WeChatConstants.GRANT_TYPE);
                }
                if (type == WeChatConstants.SHARE) {
                    Log.i(TAG, "-[onResp]-> 用户分享结束");
                    sendMessage(WeChatConstants.SUCCEED, "分享成功");
                }
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
        RequestParams params = new RequestParams();
        params.add("code", code);
        params.add("appid", appid);
        params.add("secret", secret);
        params.add("grant_type", grant_type);
        Http.get(this, WeChatConstants.URL_ACCESS_TOKEN, params, this);
    }

    /**
     * 获取用户个人信息（UnionID 机制）
     *
     * @param access_token 调用凭证
     * @param openid       普通用户的标识，对当前开发者帐号唯一
     * @param lang         国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为 zh-CN
     */
    private void reqUserInfo(String access_token, String openid, String lang) {
        RequestParams params = new RequestParams();
        params.add("openid", openid);
        params.add("access_token", access_token);
        params.add("lang", lang);
        Http.get(this, WeChatConstants.URL_USER_INFO, params, this);
    }

    /**
     * 刷新或续期 access_token 使用
     *
     * @param appid         应用唯一标识
     * @param refresh_token 填写通过 access_token 获取到的 refresh_token 参数
     */
    private void reqRefreshToken(String appid, String refresh_token) {
        RequestParams params = new RequestParams();
        params.add("appid", appid);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refresh_token);
        Http.get(this, WeChatConstants.URL_REFRESH_TOKEN, params, this);
    }

    @Override
    public void onHttpSucceed(Response result) {
        if (result.url().contains(WeChatConstants.URL_ACCESS_TOKEN)) {
            accessToken = JSON.toObject(result.body(), WeChatAccessToken.class);
            if (accessToken.getErrcode() == BaseResp.ErrCode.ERR_OK) {
                reqRefreshToken(WeChatConstants.APP_ID, accessToken.getRefresh_token());
            } else {
                Log.i(TAG, "-[ACCESS_TOKEN]-> errorCode:" + accessToken.getErrcode()+",errorMsg:"+accessToken.getErrmsg());
                sendMessage(accessToken.getErrcode(),accessToken.getErrmsg());
            }
        }
        if (result.url().contains(WeChatConstants.URL_REFRESH_TOKEN)) {
            accessToken = JSON.toObject(result.body(), WeChatAccessToken.class);
            if (accessToken.getErrcode() == BaseResp.ErrCode.ERR_OK) {
                reqUserInfo(accessToken.getAccess_token(), accessToken.getOpenid(), "zh-CN");
            } else {
                Log.i(TAG, "-[REFRESH_TOKEN]-> errorCode:" + accessToken.getErrcode()+",errorMsg:"+accessToken.getErrmsg());
                sendMessage(accessToken.getErrcode(),accessToken.getErrmsg());
            }
        }
        if (result.url().contains(WeChatConstants.URL_USER_INFO)) {
            userInfo = JSON.toObject(result.body(), WeChatUser.class);
            Intent intent = new Intent(WeChatConstants.ACTION);
            intent.putExtra(WeChatConstants.ACCESS_TOKEN_INFO, accessToken);
            intent.putExtra(WeChatConstants.USER_INFO, userInfo);
            intent.putExtra(WeChatConstants.CODE, WeChatConstants.SUCCEED);
            intent.putExtra(WeChatConstants.MSG, "登录成功");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onHttpFailure(Response response) {
        Log.i(TAG, "-[onHttpFailure]-> msg:" + response.exception().toString());
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