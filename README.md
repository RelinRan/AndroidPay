# AndroidPay
[国外GitHub](https://github.com/RelinRan/AndroidPay)、[国内Gitee](https://gitee.com/relin/AndroidPay)
## Fix-2022.3.13.1
1.微信SDK更新到6.8.0，支持Android 11吊起微信。
2.调整微信配置说明
3.调整gradle为7.0.2
4.调整JSON解析
5.handleIntent新增异常捕捉
## 功能介绍
1.微信支付、登录、分享功能  
2.支付宝支付、授权登录（极简版+完整版本）功能  
3.银联支付功能  
## 软件架构
Builder模式
## 使用说明
### JitPack
./build.gradle | settings.gradle配置如下
```
repositories {
	 ...
	 maven { url 'https://jitpack.io' }
}
```
./app/build.gradle配置如下
```
dependencies {
	 implementation 'com.github.RelinRan:AndroidPay:2022.3.13.1'
}
```
#### ARR
下载链接：[android_pay_2022.3.13.1.aar](https://github.com/RelinRan/AndroidPay/blob/master/android_pay_2022.3.13.1.aar)
```
android {
    ....
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
dependencies {
    implementation(name: 'AndroidPay', ext: 'aar')
}

```
## 微信支付
项目包名下新建wxapi文件夹，然后新建WXPayEntryActivity.java文件,继承WeChatPayActivity
```
public class WXPayEntryActivity extends WeChatPayActivity {}
```
AndroidManifest.xml配置
```
<activity
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTop"
    android:screenOrientation="portrait"
    android:theme="@style/Android.Theme.Light.NoActionBar" />
```
manifest标签内（与权限同级）下配置
```
<queries>
    <package android:name="com.tencent.mm" />
</queries>
```
支付调用
```
WeChatPay.Builder builder = new WeChatPay.Builder(this);
builder.appId("xxxx");
builder.partnerId("xxx");
builder.prepayId("xxx");
builder.nonceStr("xxxx");
builder.timeStamp("xxxx");
builder.packageValue("Sign=WXPay");
builder.sign("xxxx");
builder.listener(new OnWeChatPayListener() {
    @Override
    public void onWeChatPay(int code,String msg) {
        if(code==WeChatConstants.SUCCEED){//支付成功

        }
        if(code==WeChatConstants.CANCEL){//用户取消

        }
        if(code==WeChatConstants.FAILED){//支付失败

        }
     }
});
builder.extData(xxxxx);//支付提示文字
builder.build();
```

## 支付宝支付
AndroidManifest.xml配置，非必要配置（项目本身或者其他arr没有配置org.apache.http.legacy的情况之下需要）
```
<uses-library
    android:name="org.apache.http.legacy"
    android:required="false" />
```
支付调用
```
AliPay.Builder builder = new AliPay.Builder(this);
builder.orderInfo("xxxx");
builder.listener(new OnAliPayListener() {
	    
    /**
* 参数解释
*
* @param status 是结果码(类型为字符串)。
*       9000	订单支付成功
*       8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
*       4000	订单支付失败
*       5000	重复请求
*       6001	用户中途取消
*       6002	网络连接出错
*       6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
*       其它	其它支付错误
* @param json        是处理结果(类型为json结构字符串)
*       out_trade_no	String	是	64	商户网站唯一订单号	70501111111S001111119
*       trade_no	String	是	64	该交易在支付宝系统中的交易流水号。最长64位。	2014112400001000340011111118
*       app_id	String	是	32	支付宝分配给开发者的应用Id。	2014072300007148
*       total_amount	Price	是	9	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。	9.00
*       seller_id	String	是	16	收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字	2088111111116894
*       msg	String	是	16	处理结果的描述，信息来自于code返回结果的描述	success
*       charset	String	是	16	编码格式	utf-8
*       timestamp	String	是	32	时间	2016-10-11 17:43:36
*       code	String	是	16	结果码	具体见
* @param description description是描述信息(类型为字符串)
*/
@Override
public void onAliPay(String status, String json, String description) {
        if(status.equals("9000")){//成功

        }
        else if(status.equals("6001")){//用户取消

        }
        else{//支付失败

        }
        }
});
builder.loading(true);
builder.build();
```
## 银联支付
```
UUPay uuPay = new UUPay(this);
uuPay.pay(tn,UUPay.PayMode.FORM);
```

## 微信登录
项目包名下新建wxapi文件夹，然后新建WXEntryActivity.java文件,继承WeChatAuthActivity
```
public class WXEntryActivity extends WeChatAuthActivity {}
```
AndroidManifest.xml配置
```
<activity
     android:name=".wxapi.WXEntryActivity"
     android:configChanges="keyboardHidden|orientation|screenSize"
     android:exported="true"
     android:taskAffinity="填写你的包名"
     android:launchMode="singleTask"
     android:theme="@android:style/Theme.Translucent.NoTitleBar">
</activity>
```
manifest标签内（与权限同级）下配置
```
<queries>
    <package android:name="com.tencent.mm" />
</queries>
```
微信登录
```
WeChatLogin.Builder builder = new WeChatLogin.Builder(context);
builder.appId("xxx");
builder.appSecret("xxx");
builder.listener(new OnWXLoginListener() {
    @Override
    public void onWeChatLogin(int code, String msg, WeChatUser user) {
        if (code==WeChatConstants.LOADING){//登录中

         }
        if (code==WeChatConstants.SUCCEED){//登录成功

         }
         if (code==WeChatConstants.CANCEL){//用户取消登录

         }
         if (code==WeChatConstants.AUTH_DENIED){//授权取消

         }
    }
});
builder.build();
```
##  支付宝登录（[官方文档](https://docs.open.alipay.com/218/)）
授权登录回调onAliLogin（int code, String memo, AliUser aliUser）回调返回code值如下：  
AliLogin.OK = 9000 (调用成功)  
AliLogin.Duplex = 5000 (3s内快速发起了多次支付 / 授权调用。稍后重试即可。)  
AliLogin.NOT_INSTALLED = 4001（用户未安装支付宝 App。）  
AliLogin.SYS_ERR = 4000（其它错误，如参数传递错误。）  
AliLogin.CANCEL = 6001（用户取消）  
AliLogin.NET_ERROR = 6002（网络连接出错）  
###  极简版授权([官方文档](https://docs.open.alipay.com/218/sxc60m/))
在项目AndroidManifest.xml配置如下（注意：android:scheme="xxxxxxxxxx"这个需要自己配置，最好是自己应用包名）
```
<activity android:name="com.alipay.sdk.app.AlipayResultActivity" tools:node="merge">
    <intent-filter tools:node="replace">
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="xxxxxxxxxx"/>
    </intent-filter>
</activity>
```
支付宝登录代码
```
AliLogin.Builder builder = new AliLogin.Builder(this);
builder.appId("xxxxx");
builder.scheme("xxxxxx");//必须跟AndroidManifest.xml配置一致
builder.listener(new OnAliLoginListener() {
    @Override
    public void onAliLogin(int code, String memo, AliUser aliUser) {
        if (code == AliLogin.OK) {
            //处理你的逻辑，极简版本只有aliUser.getAuthCode()
        }
    }
});
builder.build();
```
### 完整版授权（[官方文档](https://docs.open.alipay.com/218/105325/)）
注意：authInfo需要后端提供，为了安全性。如果后端不提供就是调用OrderInfoUtil工具类如下方法获取
```
/**
* 构建授权信息
*
* @param privateKey 私钥（https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1）
* @param pid   签约的支付宝账号对应的支付宝唯一用户号，以2088开头的16位纯数字组成
* @param app_id支付宝分配给开发者的应用ID
* @param target_id  商户标识该次用户授权请求的ID，该值在商户端应保持唯一
* @param rsa2  签名类型是否是RSA2,否：RSA
* @return
*/
public static String buildAuthInfo(String privateKey, String pid, String app_id, String target_id, boolean rsa2)
```
授权AndroidManifest.xml配置
```
<activity
    android:name="com.alipay.sdk.app.H5AuthActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="false" >
</activity>
<activity
    android:name="com.alipay.sdk.app.H5OpenAuthActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="false"
    android:screenOrientation="behind"
    android:windowSoftInputMode="adjustResize|stateHidden" >
</activity>
```
授权调用代码
```
AliLogin.Builder builder = new AliLogin.Builder(this);
builder.authInfo("xxxxx");
builder.listener(new OnAliLoginListener() {
    @Override
    public void onAliLogin(int code, String memo, AliUser aliUser) {
        if (code == AliLogin.OK) {
            //处理你的逻辑，完整版本有aliUser.getUserId()和aliUser.getAliPayOpenId()
        }
    }
});
builder.build();
```
## 微信分享
项目包名下新建wxapi文件夹，然后新建WXEntryActivity.java文件,继承WeChatAuthActivity
```
public class WXEntryActivity extends WeChatAuthActivity {}
```
因为根据官方文档集成，其中参数名字也跟官方文档一致，目前只是加了一个thumUrl和imageUrl不跟官方文档一致，为了方便缩略图和图片分享使用网络图片; 其他的参数参考[官方文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html)
### 图片分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.type(WeChatShare.TYPE_IMAGE);
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.imagePath("本地图片地址");//或者builder.imageUrl("http://xxxxxx");
builder.listener(new OnWeChatShareListener() {
    @Override
    public void onWeChatShare(int code, String msg) {
        //分享回调，官方目前取消了回调，不管是否正确分享都会进入。
    }
});
builder.build();
```

### 视频分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.type(WeChatShare.TYPE_VIDEO);
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.title("标题");
builder.description("描述信息");
builder.thumbImage(bitmap);//或 builder.thumbUrl("http://xxxxxx"); 或builder.thumbData(byte[]);
builder.videoUrl("视频网络地址");
builder.listener(new OnWeChatShareListener() {
    @Override
    public void onWeChatShare(int code, String msg) {
        //分享回调，官方目前取消了回调，不管是否正确分享都会进入。
        if (code==WeChatConstants.SUCCEED){//成功

         }
         if (code==WeChatConstants.CANCEL){//取消

         }
    }
});
builder.build();
```

### 网页分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.type(WeChatShare.TYPE_WEB);
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.title("标题");
builder.description("描述信息");
//缩略图设置
builder.thumbImage(bitmap);//或 builder.thumbUrl("http://xxxxxx"); 或builder.thumbData(byte[]);
builder.webpageUrl("网络地址");
builder.listener(new OnWeChatShareListener() {
    @Override
    public void onWeChatShare(int code, String msg) {
        //分享回调，官方目前取消了回调，不管是否正确分享都会进入。
        if (code==WeChatConstants.SUCCEED){//成功

         }
         if (code==WeChatConstants.CANCEL){//取消

         }
    }
});
builder.build();
```
### 音乐分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.type(WeChatShare.TYPE_MUSIC);
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.title("标题");
builder.description("描述信息");
//缩略图设置
builder.thumbImage(bitmap);//或 builder.thumbUrl("http://xxxxxx"); 或builder.thumbData(byte[]);
builder.musicUrl("网络地址");
builder.listener(new OnWeChatShareListener() {
    @Override
    public void onWeChatShare(int code, String msg) {
        //分享回调，官方目前取消了回调，不管是否正确分享都会进入。
        if (code==WeChatConstants.SUCCEED){//成功

         }
         if (code==WeChatConstants.CANCEL){//取消

         }
    }
});
builder.build();
```