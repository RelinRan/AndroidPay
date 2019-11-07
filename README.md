# AndroidPay

#### 介绍
Android支付,主要用户中国常用的微信支付、支付宝支付、银联支付

#### 软件架构
只要采用Builder模式

#### 使用说明
## 方法一  JitPack依赖

（1）在项目下的build.gradle配置如下

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

（2）在项目app文件夹下的build.gradle配置如下
```
dependencies {
	        implementation 'com.github.RelinRan:AndroidPay:1.0.5'
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
A.需要在项目新建wxapi文件夹，然后新建WXPayEntryActivity.java文件,继承WeChatPayActivity

```
public class WXPayEntryActivity extends WeChatPayActivity {

}
```
B.AndroidManifest.xml配置

```
        <activity
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/Android.Theme.Light.NoActionBar" />
```

C.支付调用

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
                if(code==WeChatPay.PAY_CODE_SUCCEED){//支付成功

                }
                if(code==WeChat.PAY_CODE_CANCEL){//用户取消

                }
                if(code==WeChat.PAY_CODE_FAILED){//支付失败

                }
            }
        });
        builder.extData(xxxxx);//支付提示文字
        builder.build();

```

#### 2. 支付宝支付

A.AndroidManifest.xml配置

```
        <!--支付宝默认页面-->
        <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind"
            android:windowSoftInputMode="adjustResize|stateHidden" />
```

B.支付调用

```
     AliPay.Builder builder = new AliPay.Builder(this);
     builder.orderInfo("xxxx");
     builder.listener(new OnAliPayListener() {
	    
    /**
     * 参数解释
     *
     * @param status      是结果码(类型为字符串)。
     *                    9000	订单支付成功
     *                    8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    4000	订单支付失败
     *                    5000	重复请求
     *                    6001	用户中途取消
     *                    6002	网络连接出错
     *                    6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    其它	其它支付错误
     * @param json        是处理结果(类型为json结构字符串)
     *                    out_trade_no	String	是	64	商户网站唯一订单号	70501111111S001111119
     *                    trade_no	String	是	64	该交易在支付宝系统中的交易流水号。最长64位。	2014112400001000340011111118
     *                    app_id	String	是	32	支付宝分配给开发者的应用Id。	2014072300007148
     *                    total_amount	Price	是	9	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。	9.00
     *                    seller_id	String	是	16	收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字	2088111111116894
     *                    msg	String	是	16	处理结果的描述，信息来自于code返回结果的描述	success
     *                    charset	String	是	16	编码格式	utf-8
     *                    timestamp	String	是	32	时间	2016-10-11 17:43:36
     *                    code	String	是	16	结果码	具体见
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
A.需要在项目新建wxapi文件夹，然后新建WXEntryActivity.java文件,继承WeChatLoginActivity
```
public class WXEntryActivity extends WeChatLoginActivity {

}
```
B.AndroidManifest.xml配置

```
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"></activity>
```
C.微信登录代码
```
WeChatLogin.Builder builder = new WeChatLogin.Builder(context);
builder.appId("xxx");
builder.appSecret("xxx");
builder.listener(new OnWXLoginListener() {
    @Override
    public void onWeChatLogin(int code, String msg, WeChatUser user) {
        if (code==WeChatLogin.CODE_USER_LOADING){//登录中

         }
        if (code==WeChatLogin.CODE_LOGIN_SUCCEED){//登录成功

         }
         if (code==WeChatLogin.CODE_USER_CANCEL){//用户取消登录

         }
         if (code==WeChatLogin.CODE_AUTH_DENIED){//授权取消

         }
    }
});
builder.build();
```
####  5.支付宝登录
A.在项目AndroidManifest.xml配置如下（注意：<data android:scheme="xxxxxxxxxx"/>这个需要自己配置，最好是自己应用包名）
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
B.支付宝登录代码
```
AliLogin.Builder builder = new AliLogin.Builder(this);
builder.appId("xxxxx");
builder.scheme("xxxxxx");//必须跟AndroidManifest.xml配置一致
builder.listener(new OnAliLoginListener() {
    @Override
    public void onAliLogin(int resultCode, String memo, AliUser aliUser) {
        if (resultCode == AliLogin.OK) {
            //处理你的逻辑，支付宝只有aliUser.getAuthCode()
        }
    }
});
builder.build();
```

