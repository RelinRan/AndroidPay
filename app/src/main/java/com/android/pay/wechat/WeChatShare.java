package com.android.pay.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatShare {

    /**
     * 分享到对话
     */
    public static final int SCENE_MESSAGE = SendMessageToWX.Req.WXSceneSession;

    /**
     * 分享到朋友圈
     */
    public static final int SCENE_FRIEND_CIRCLE = SendMessageToWX.Req.WXSceneTimeline;

    /**
     * 分享到收藏
     */
    public static final int SCENE_FAVORITE = SendMessageToWX.Req.WXSceneFavorite;

    private IWXAPI api;

    /**
     * 场景
     */
    private String scene;

    private String openId;

    private String secret;

    private Context context;

    public WeChatShare(Context context, String openId, String secret) {
        this.context = context;
        this.openId = openId;
        this.secret = secret;
        api = WXAPIFactory.createWXAPI(context, WeChatLogin.APP_ID, true);
    }

    /**
     * 分享纯文本
     *
     * @param text 纯文本
     */
    public void shareText(String text) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        sendReq("text" + System.currentTimeMillis(), msg, SendMessageToWX.Req.WXSceneSession, openId);
    }

    /**
     * 分享图片
     *
     * @param id
     * @param thumbData
     */
    public void shareImage(@IdRes int id, byte[] thumbData) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        bmp.recycle();
        //设置缩略图
        msg.thumbData = thumbData;
        msg.mediaObject = imgObj;
        sendReq("image" + System.currentTimeMillis(), msg, SendMessageToWX.Req.WXSceneSession, openId);
    }

    /**
     * 分享图片
     *
     * @param bitmap
     * @param thumbData
     */
    public void shareImage(Bitmap bitmap, byte[] thumbData) {
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        bitmap.recycle();
        //设置缩略图
        msg.thumbData = thumbData;
        msg.mediaObject = imgObj;
        sendReq("image" + System.currentTimeMillis(), msg, SendMessageToWX.Req.WXSceneSession, openId);
    }

    /**
     * 分享图片
     *
     * @param image
     * @param thumbData
     */
    public void shareImage(byte[] image, byte[] thumbData) {
        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(image);
        WXMediaMessage msg = new WXMediaMessage();
        //设置缩略图
        msg.thumbData = thumbData;
        msg.mediaObject = imgObj;
        sendReq("image" + System.currentTimeMillis(), msg, SendMessageToWX.Req.WXSceneSession, openId);
    }


    /**
     * 调用api接口，发送数据到微信
     *
     * @param transaction 对应该请求的事务 ID，通常由 Req 发起，回复 Resp 时应填入对应事务 ID
     * @param message     微信消息
     * @param scene       场景
     * @param openId      授权获取的openId
     */
    public void sendReq(String transaction, WXMediaMessage message, int scene, String openId) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = message;
        req.scene = scene;
        req.openId = openId;
        api.sendReq(req);
    }

}
