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

/**
 * 微信分享
 */
public class WeChatShare {

    /**
     * 会话场景
     */
    public static final int SCENE_SESSION = SendMessageToWX.Req.WXSceneSession;

    /**
     * 朋友圈场景
     */
    public static final int SCENE_TIME_LINE = SendMessageToWX.Req.WXSceneTimeline;

    /**
     * 收藏场景
     */
    public static final int SCENE_FAVORITE = SendMessageToWX.Req.WXSceneFavorite;


    /**
     * 微信api对象
     */
    private IWXAPI api;

    /**
     * 上下文对象
     */
    public final Context context;

    /**
     * 微信appId
     */
    public final String appId;

    /**
     * 分享场景
     */
    public final int scene;

    /**
     * 文本
     */
    public final String text;

    /**
     * 分享构造函数
     *
     * @param builder 分享构建者
     */
    public WeChatShare(Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.scene = builder.scene;
        this.text = builder.text;
        api = WXAPIFactory.createWXAPI(context, WeChatConstants.APP_ID, true);
        api.registerApp(appId);
        shareText(text);
    }

    /**
     * 构建者
     */
    public static class Builder {

        /**
         * 上下文对象
         */
        private Context context;

        /**
         * 微信appId
         */
        private String appId;

        /**
         * 分享场景
         */
        private int scene;

        /**
         * 文本
         */
        private String text;

        /**
         * 分享构建者
         *
         * @param context
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 上下文对象
         *
         * @return
         */
        public Context context() {
            return context;
        }

        /**
         * 微信appId
         *
         * @return
         */
        public String appId() {
            return appId;
        }

        /**
         * 设置微信appId
         *
         * @param appId
         * @return
         */
        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 会话场景
         *
         * @return
         */
        public int scene() {
            return scene;
        }

        /**
         * 设置会话场景
         *
         * @param scene
         * @return
         */
        public Builder scene(int scene) {
            this.scene = scene;
            return this;
        }

        /**
         * 文本内容
         *
         * @return
         */
        public String text() {
            return text;
        }

        /**
         * 设置文本内容
         *
         * @param text
         */
        public void text(String text) {
            this.text = text;
        }

        /**
         * 构建分享对象进行分享
         *
         * @return
         */
        public WeChatShare build() {
            return new WeChatShare(this);
        }

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
        sendReq("text" + System.currentTimeMillis(), msg, scene, "");
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
        sendReq("image" + System.currentTimeMillis(), msg, scene, "");
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
        sendReq("image" + System.currentTimeMillis(), msg, scene, "");
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
        sendReq("image" + System.currentTimeMillis(), msg, scene, "");
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
//        req.openId = openId;
        api.sendReq(req);
    }

}
