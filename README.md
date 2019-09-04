# AndroidPay
Android Commonly used payment in China
# AndroidPay

#### 介绍
Android支付,主要用户中国常用的微信支付、支付宝支付、银联支付

#### 软件架构
只要采用Builder模式

#### 使用说明

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
	        implementation 'com.github.RelinRan:AndroidPay:Tag'
	}
```
#### 1. 微信支付
A.需要在项目新建wxapi文件夹，然后新建WXPayEntryActivity.java文件

```
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_aty_wx_pay);
        api = WXAPIFactory.createWXAPI(this, WxPay.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        //错误参数 https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_12&index=2
        //errorCode:[0:success,-1:fail,-2:cancel]
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (baseResp.errCode == 0) {
                Log.i(this.getClass().getSimpleName(), "[微信支付成功]==>展示成功页面。");
                finish();
            }
            if (baseResp.errCode == -1) {
                Log.i(this.getClass().getSimpleName(), "[微信支付调用失败]==>可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。");
                finish();
            }
            if (baseResp.errCode == -2) {
                Log.i(this.getClass().getSimpleName(), "[微信支付用户取消]==>无需处理。发生场景：用户不支付了，点击取消，返回APP。");
                finish();
            }
            Intent intent = new Intent(WxPay.ACTION_PAY_FINISH);
            intent.putExtra(WxPay.PAY_RESULT, baseResp.errCode);
            sendBroadcast(intent);
        } else {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }
}

B.AndroidManifest.xml配置

```
        <activity
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:screenOrientation="portrait"
            android:theme="@style/Android.Theme.Light.NoActionBar" />
```



```
C.支付调用

```
        WxPay.Builder builder = new WxPay.Builder(this);
        builder.appId(xx);
        builder.partnerId(xxx);
        builder.prepayId(xxx);
        builder.nonceStr(xxxx);
        builder.timeStamp(xxxx);
        builder.packageValue("Sign=WXPay");
        builder.sign(xxxx);
        builder.extData(xxxxx);//支付提示文字
        builder.build().pay();


        //如果需要支付支付结果，需要广播接收。但是不建议这么做，最好还是后台写一个接口来判断是否支付成功，因为有时候支付广播判断有问题。
    private class WxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WxPay.ACTION_PAY_FINISH)) {
                int code = intent.getIntExtra(WxPay.PAY_RESULT, -1);
                Log.i("RRL", this.getClass().getSimpleName() + "---->[onReceive]--->code:" + code);
                if (code == 0) {
                    showToast(ToastMode.SUCCEED, "支付成功");
                } else {
                    showToast(ToastMode.FAILURE, "支付失败");
                }
            }
        }
    }

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
        builder.orderInfo(data);
        builder.listener(new AliPayListener() {
            @Override
            public void aliPaySuccess(String s, String s1, String s2) {
                showToast(ToastMode.SUCCEED, "支付成功");
            }

            @Override
            public void aliPayFail(String s, String s1, String s2) {
                showToast(ToastMode.FAILURE, "支付失败");
            }

            @Override
            public void aliPaying(String s, String s1, String s2) {

            }
        });
        builder.loading(true);
        builder.build().pay();
```

####  3.银联支付

```
        UUPay uuPay = new UUPay(this);
        uuPay.pay(tn,UUPay.PayMode.FORM);
```
