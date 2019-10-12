package com.android.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.pay.net.HttpResponse;
import com.android.pay.net.HttpUtils;
import com.android.pay.net.JsonParser;
import com.android.pay.net.OnHttpListener;
import com.android.pay.net.RequestParams;
import com.android.pay.wxlogin.WXLogin;
import com.android.pay.wxlogin.WXUser;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

public class AndroidWXEntryActivity extends Activity implements IWXAPIEventHandler, OnHttpListener {

    private IWXAPI wxAPI;
    private static final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private static final int RETURN_MSG_TYPE_SHARE = 2; //分享

    private String openid;
    private String access_token;

    private WXUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new WXUser();
        wxAPI = WXAPIFactory.createWXAPI(this, WXLogin.APP_ID, true);
        wxAPI.registerApp(WXLogin.APP_ID);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxAPI.handleIntent(getIntent(), this);
        Log.i("WXEntryActivity", "WXEntryActivity onNewIntent");
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i("WXEntryActivity", "WXEntryActivity onReq:" + arg0);
    }

    @Override
    public void onResp(BaseResp resp) {
        int type = resp.getType(); //类型：分享还是登录
        Log.i("WXEntryActivity", "onResp type:------>" + type);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, "已拒绝授权微信登录", Toast.LENGTH_SHORT).show();
                //拒绝授权微信登录
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                String message = "";
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录";
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_OK://用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    String code = ((SendAuth.Resp) resp).code;
                    Log.i("WXEntryActivity", "code:------>" + code);
                    //这里拿到了这个code，去做2次网络请求获取access_token和用户个人信息
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
        params.add("appid", WXLogin.APP_ID);
        params.add("secret", WXLogin.APP_SECRET);
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

    }

    @Override
    public void onHttpSucceed(HttpResponse result) {
        if (result.url().contains("access_token")) {
            Map<String, String> accessData = JsonParser.parseJSONObject(result.body());
            access_token = accessData.get("access_token");
            openid = accessData.get("openid");
            String expires_in = accessData.get("expires_in");
            String refresh_token = accessData.get("refresh_token");
            String scope = accessData.get("scope");
            String unionid = accessData.get("unionid");

            user.setAppId(WXLogin.APP_ID);
            user.setAppSecret(WXLogin.APP_SECRET);
            user.setExpiresIn(expires_in);
            user.setRefreshToken(refresh_token);
            user.setScope(scope);
            user.setUnionid(unionid);

            getUserInfo(access_token, openid);
        }
        if (result.url().contains("userinfo")) {
            Map<String, String> userData = JsonParser.parseJSONObject(result.body());
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
            user.setSex(sex.equals("1")?"男":"女");
            user.setProvince(province);
            user.setCity(city);
            user.setPrivilege(privilege);

            Intent intent = new Intent(WXLogin.WX_LOGIN_ACTION);
            intent.putExtra("WxUser",user);
            sendBroadcast(intent);
        }
    }
}