package com.android.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.pay.R;
import com.android.pay.wechat.WeChatPay;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WeChatPayActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private final String TAG = "WXPayEntryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_aty_wx_pay);
        api = WXAPIFactory.createWXAPI(this, WeChatPay.APP_ID);
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
        String msg = "";
        if (baseResp.errCode == 0) {
            msg = "支付成功";
            Log.i(TAG, "-[onResp]-> 微信支付成功,展示成功页面。");
        }
        if (baseResp.errCode == -1) {
            msg = "支付失败";
            Log.e(TAG, "-[onResp]-> 微信支付调用失败,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。");
        }
        if (baseResp.errCode == -2) {
            msg = "取消支付";
            Log.i(TAG, "-[onResp]-> 微信支付用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP。");
        }
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Intent intent = new Intent(WeChatPay.ACTION_PAY_FINISH);
            intent.putExtra(WeChatPay.PAY_RESULT, baseResp.errCode);
            intent.putExtra(WeChatPay.PAY_MSG, msg);
            sendBroadcast(intent);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

}