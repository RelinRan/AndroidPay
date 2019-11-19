package com.android.pay.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

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
     * 消息标题
     */
    public final String title;

    /**
     * 消息描述
     */
    public final String description;

    /**
     * 缩略图的二进制数据,内容大小不超过 10MB
     */
    public final byte[] thumbData;

    /**
     * 缩略图位图数据,内容大小不超过 10MB
     */
    public final Bitmap thumbImage;

    /**
     * 缩略图大小
     */
    public final int thumbSize;

    /**
     * 文本
     */
    public final String text;

    /**
     * 图片的二进制数据,内容大小不超过 10MB
     */
    public final byte[] imageData;

    /**
     * 图片的本地路径  对应图片内容大小不超过 10MB
     */
    public final String imagePath;


    /**
     * 分享构造函数
     *
     * @param builder 分享构建者
     */
    public WeChatShare(Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.scene = builder.scene;
        this.title = builder.title;
        this.description = builder.description;
        this.thumbData = builder.thumbData;
        this.thumbImage = builder.thumbImage;
        this.thumbSize = builder.thumbSize;
        this.text = builder.text;
        this.imageData = builder.imageData;
        this.imagePath = builder.imagePath;
        api = WXAPIFactory.createWXAPI(context, WeChatConstants.APP_ID, true);
        api.registerApp(appId);
        shareText(text);
        shareImage(imagePath);
        shareImage(imageData);
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
         * 消息标题
         */
        private String title;

        /**
         * 消息描述
         */
        private String description;

        /**
         * 缩略图的二进制数据,内容大小不超过 10MB
         */
        private byte[] thumbData;

        /**
         * 缩略图位图数据,内容大小不超过 10MB
         */
        private Bitmap thumbImage;

        /**
         * 缩略图大小
         */
        private int thumbSize = 120;

        /**
         * 文本
         */
        private String text;

        /**
         * 图片的二进制数据,内容大小不超过 10MB
         */
        private byte[] imageData;

        /**
         * 图片的本地路径  对应图片内容大小不超过 10MB
         */
        private String imagePath;

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
         * 消息标题
         *
         * @return
         */
        public String title() {
            return title;
        }

        /**
         * 设置消息标题
         *
         * @param title
         */
        public void title(String title) {
            this.title = title;
        }

        /**
         * 描述信息
         *
         * @return
         */
        public String description() {
            return description;
        }

        /**
         * 描述信息
         *
         * @param description
         */
        public void description(String description) {
            this.description = description;
        }

        /**
         * 缩略图的二进制数据,内容大小不超过 10MB
         *
         * @return
         */
        public byte[] thumbData() {
            return thumbData;
        }

        /**
         * 缩略图的二进制数据,内容大小不超过 10MB
         *
         * @param thumbData
         */
        public void thumbData(byte[] thumbData) {
            this.thumbData = thumbData;
        }

        public int getThumbSize() {
            return thumbSize;
        }

        public void setThumbSize(int thumbSize) {
            this.thumbSize = thumbSize;
        }

        /**
         * 分享图片的缩略图，内容大小不超过 10MB
         *
         * @return
         */
        public Bitmap thumbImage() {
            return thumbImage;
        }

        /**
         * 分享图片的缩略图，内容大小不超过 10MB
         *
         * @return
         */
        public void thumbImage(Bitmap thumbImage) {
            this.thumbImage = thumbImage;
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
         * 图片的二进制数据
         *
         * @return
         */
        public byte[] imageData() {
            return imageData;
        }

        /**
         * 图片的二进制数据,内容大小不超过 10MB
         *
         * @param imageData
         */
        public void imageData(byte[] imageData) {
            this.imageData = imageData;
        }

        /**
         * 图片的本地路径,对应图片内容大小不超过 10MB
         *
         * @return
         */
        public String imagePath() {
            return imagePath;
        }

        /**
         * 图片的本地路径,对应图片内容大小不超过 10MB
         *
         * @param imagePath
         */
        public void imagePath(String imagePath) {
            this.imagePath = imagePath;
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
        if (TextUtils.isEmpty(text)) {
            return;
        }
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        sendReq("text" + System.currentTimeMillis(), msg, scene, "");
    }

    /**
     * 分享本地图片
     */
    public void shareImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.imagePath = imagePath;
        WXMediaMessage msg = new WXMediaMessage();
        if (!TextUtils.isEmpty(title)) {
            msg.title = title;
        }
        if (!TextUtils.isEmpty(description)) {
            msg.description = description;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, thumbSize, thumbSize, true);
        msg.thumbData = ShareHelper.decodeBitmap(thumbBmp);
        msg.mediaObject = imgObj;
        sendReq("image" + System.currentTimeMillis(), msg, scene, "");
    }

    /**
     * 分享二进制图片
     *
     * @param imageData
     */
    public void shareImage(byte[] imageData) {
        if (imageData == null && imageData.length == 0) {
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.imagePath = imagePath;
        imgObj.imageData = imageData;
        WXMediaMessage msg = new WXMediaMessage();
        if (!TextUtils.isEmpty(title)) {
            msg.title = title;
        }
        if (!TextUtils.isEmpty(description)) {
            msg.description = description;
        }
        msg.thumbData = imageData;
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
//      req.openId = openId;
        api.sendReq(req);
    }


}
