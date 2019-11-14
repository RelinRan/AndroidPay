package com.android.pay.wechat;

import java.io.Serializable;

public class WeChatAccessToken implements Serializable{

    /**
     * 接口调用凭证
     */
    private String access_token;

    /**
     * 用户刷新 access_token
     */
    private String refresh_token;

    /**
     * 当且仅当该移动应用已获得该用户的 userinfo 授权时，才会出现该字段
     */
    private String unionid;

    /**
     * 应用唯一标识，在微信开放平台提交应用审核通过后获得
     */
    private String openid;

    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    private String scope;

    /**
     * access_token 接口调用凭证超时时间，单位（秒）
     */
    private String expires_in;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getUnionid() {
        return unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public String getScope() {
        return scope;
    }

    public String getExpires_in() {
        return expires_in;
    }
}
