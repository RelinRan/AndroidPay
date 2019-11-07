package com.android.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.pay.net.HttpResponse;
import com.android.pay.net.HttpUtils;
import com.android.pay.net.HttpJson;
import com.android.pay.net.OnHttpListener;
import com.android.pay.net.RequestParams;
import com.android.pay.wechat.WeChatLogin;
import com.android.pay.wechat.WeChatUser;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

public class WeChatLoginActivity extends Activity implements IWXAPIEventHandler, OnHttpListener {

    private IWXAPI wxAPI;
    private String TAG = "WXEntryActivity";
    private static final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private static final int RETURN_MSG_TYPE_SHARE = 2; //分享

    private WeChatUser user;
    private String openid;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new WeChatUser();
        wxAPI = WXAPIFactory.createWXAPI(this, WeChatLogin.APP_ID, true);
        wxAPI.registerApp(WeChatLogin.APP_ID);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i(TAG, "[onReq] -> arg0:" + arg0);
    }

    @Override
    public void onResp(BaseResp resp) {
        int type = resp.getType(); //类型：分享还是登录
        Log.i(TAG, "[onResp] -> type:" + type);
        Intent intent;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                intent = new Intent(WeChatLogin.WX_LOGIN_ACTION);
                intent.putExtra(WeChatLogin.KEY_CODE, WeChatLogin.CODE_AUTH_DENIED);
                intent.putExtra(WeChatLogin.KEY_MSG, "已拒绝授权微信登录");
                sendBroadcast(intent);
                Log.i(TAG, "[onResp] -> 拒绝授权微信登录");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                String message = "";
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录";
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享";
                }
                intent = new Intent(WeChatLogin.WX_LOGIN_ACTION);
                intent.putExtra(WeChatLogin.KEY_CODE, WeChatLogin.CODE_USER_CANCEL);
                intent.putExtra(WeChatLogin.KEY_MSG, "已拒绝授权微信登录");
                sendBroadcast(intent);
                Log.i(TAG, "[onResp] -> " + message);
                break;
            case BaseResp.ErrCode.ERR_OK://用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    String code = ((SendAuth.Resp) resp).code;
                    Log.i(TAG, "[onResp] -> 用户开始登录微信...");
                    //这里拿到了这个code，去做2次网络请求获取access_token和用户个人信息
                    intent = new Intent(WeChatLogin.WX_LOGIN_ACTION);
                    intent.putExtra(WeChatLogin.KEY_CODE, WeChatLogin.CODE_USER_LOADING);
                    intent.putExtra(WeChatLogin.KEY_MSG, "用户开始登录微信");
                    sendBroadcast(intent);
                    getAccessToken(code);
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    //微信分享成功
                }
                break;
        }
        finish();
    }

    private void getAccessToken(String code) {
        RequestParams params = new RequestParams();
        params.add("appid", WeChatLogin.APP_ID);
        params.add("secret", WeChatLogin.APP_SECRET);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        HttpUtils.get(this, "https://api.weixin.qq.com/sns/oauth2/access_token", params, this);
    }

    private void getUserInfo(String access_token, String openid) {
        RequestParams params = new RequestParams();
        params.add("openid", openid);
        params.add("access_token", access_token);
        HttpUtils.get(this, "https://api.weixin.qq.com/sns/userinfo", params, this);
    }

    @Override
    public void onHttpFailure(HttpResponse result) {
        Log.i(TAG, "[onHttpFailure] -> msg:" + result.exception().toString());
    }

    @Override
    public void onHttpSucceed(HttpResponse result) {
        if (result.url().contains("access_token")) {
            Map<String, String> accessData = HttpJson.parseJSONObject(result.body());
            access_token = accessData.get("access_token");
            openid = accessData.get("openid");
            String expires_in = accessData.get("expires_in");
            String refresh_token = accessData.get("refresh_token");
            String scope = accessData.get("scope");
            String unionid = accessData.get("unionid");

            user.setAppId(WeChatLogin.APP_ID);
            user.setAppSecret(WeChatLogin.APP_SECRET);
            user.setExpiresIn(expires_in);
            user.setRefreshToken(refresh_token);
            user.setScope(scope);
            user.setUnionid(unionid);

            getUserInfo(access_token, openid);
        }
        if (result.url().contains("userinfo")) {
            Map<String, String> userData = HttpJson.parseJSONObject(result.body());
            String openid = userData.get("openid");
            String nickname = userData.get("nickname");
            String headimgurl = userData.get("headimgurl");
            String sex = userData.get("sex");//1男  2女
            String city = userData.get("city");
            String province = userData.get("province");
            String unionid = userData.get("unionid");
            String privilege = userData.get("privilege");

            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setHeadUrl(headimgurl);
            user.setUnionid(unionid);
            user.setSex(sex.equals("1") ? "男" : "女");
            user.setProvince(province);
            user.setCity(city);
            user.setPrivilege(privilege);

            Intent intent = new Intent(WeChatLogin.WX_LOGIN_ACTION);
            intent.putExtra(WeChatLogin.KEY_USER, user);
            intent.putExtra(WeChatLogin.KEY_CODE, WeChatLogin.CODE_LOGIN_SUCCEED);
            intent.putExtra(WeChatLogin.KEY_MSG, "登录微信成功");
            sendBroadcast(intent);
        }
    }
}