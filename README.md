# AndroidPay

#### 功能介绍
支持微信支付、支付宝支付、银联支付;
支持微信分享;
支持微信登录;
支持微信分享;

#### 软件架构
只要采用Builder模式

#### 使用说明
## 方法一  JitPack依赖

##### （1）在项目下的build.gradle配置如下

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

##### （2）在项目app文件夹下的build.gradle配置如下
```
dependencies {
	        implementation 'com.github.RelinRan:AndroidPay:1.0.7'
	}
```
## 方法二  ARR依赖
[AndroidPay.arr](https://github.com/RelinRan/AndroidPay/blob/master/AndroidPay.aar)
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
#### 1. 微信支付
##### A.需要在项目新建wxapi文件夹，然后新建WXPayEntryActivity.java文件,继承WeChatPayActivity

```
public class WXPayEntryActivity extends WeChatPayActivity {

}
```
##### B.AndroidManifest.xml配置

```
        <activity
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/Android.Theme.Light.NoActionBar" />
```

##### C.支付调用

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

#### 2. 支付宝支付

##### A.AndroidManifest.xml配置
##### 1.AndroidManifest.xml非必要配置（项目本身或者其他arr没有配置org.apache.http.legacy的情况之下需要）：
```
<uses-library
    android:name="org.apache.http.legacy"
    android:required="false" />
```
##### 2.AndroidManifest.xml主要配置
```
<activity
    android:name="com.alipay.sdk.app.H5PayActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="false" >
</activity>
<activity
    android:name="com.alipay.sdk.app.PayResultActivity"
    android:configChanges="orientation|keyboardHidden|navigation|screenSize"
    android:exported="true"
    android:launchMode="singleInstance"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" >
    <intent-filter>
<category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
<activity
    android:name="com.alipay.sdk.app.AlipayResultActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" >
</activity>
```

##### B.支付调用

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

####  3.银联支付

```
UUPay uuPay = new UUPay(this);
uuPay.pay(tn,UUPay.PayMode.FORM);
```

####  4.微信登录
##### A.需要在项目新建wxapi文件夹，然后新建WXEntryActivity.java文件,继承WeChatAuthActivity
```
public class WXEntryActivity extends WeChatAuthActivity {

}
```
##### B.AndroidManifest.xml配置

```
<activity
     android:name=".wxapi.WXEntryActivity"
     android:configChanges="keyboardHidden|orientation|screenSize"
     android:exported="true"
     android:theme="@android:style/Theme.Translucent.NoTitleBar">
</activity>
```
##### C.微信登录代码
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
####  5.支付宝登录
##### 授权登录回调onAliLogin（int code, String memo, AliUser aliUser）回调返回code值如下：
1. AliLogin.OK = 9000 (调用成功)
2. AliLogin.Duplex = 5000 (3s内快速发起了多次支付 / 授权调用。稍后重试即可。)
3. AliLogin.NOT_INSTALLED = 4001（用户未安装支付宝 App。）
4. AliLogin.SYS_ERR = 4000（其它错误，如参数传递错误。）
4. AliLogin.CANCEL = 6001（用户取消）
4. AliLogin.NET_ERROR = 6002（网络连接出错）
#####  1.极简版授权
##### A.在项目AndroidManifest.xml配置如下（注意：<data android:scheme="xxxxxxxxxx"/>这个需要自己配置，最好是自己应用包名）
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
##### B.支付宝登录代码
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
#####  2.完整版授权
##### 1.注意：authInfo需要后端提供，为了安全性。如果后端不提供就是调用OrderInfoUtil工具类如下方法获取
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
##### 2.授权AndroidManifest.xml配置
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
##### 2.授权调用代码
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
####  6.微信分享
##### A.需要在项目新建wxapi文件夹，然后新建WXEntryActivity.java文件,继承WeChatAuthActivity
```
public class WXEntryActivity extends WeChatAuthActivity {}
```
##### B.微信分享代码
因为根据官方文档集成，其中参数名字也跟官方文档一致，目前只是加了一个thumUrl和imageUrl不跟官方文档一致，为了方便缩略图和图片分享使用网络图片;
其他的参数参考官方文档：https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Share_and_Favorites/Android.html

###### B-1.图片分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.title("标题");
builder.description("描述信息");
//缩略图设置
builder.thumbImage(bitmap);//或 builder.thumbUrl("http://xxxxxx"); 或builder.thumbData(byte[]);
builder.imagePath("本地图片地址");//或者builder.imageUrl("http://xxxxxx");
builder.listener(new OnWeChatShareListener() {
    @Override
    public void onWeChatShare(int code, String msg) {
        //分享回调，官方目前取消了回调，不管是否正确分享都会进入。
    }
});
builder.build();
```

###### B-2.视频分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
builder.appId(Constants.WE_CHAT_APP_ID);
builder.scene(WeChatShare.SCENE_SESSION);
builder.title("标题");
builder.description("描述信息");
//缩略图设置
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

###### B-3.网页分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
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
###### B-4.音乐分享代码
```
WeChatShare.Builder builder = new WeChatShare.Builder(getContext());
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