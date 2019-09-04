package com.android.pay.uupay;

import android.app.Activity;
import android.util.Log;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

/**
 * Created by Ice on 2016/2/23.
 * 银联支付
 */
public class UUPay {

    private Activity activity;

    public UUPay(Activity activity) {
        this.activity = activity;
    }

    /**
     * 银联支付
     *
     * @param tn      流水号
     * @param payMode 支付模式 FORM：正式  TEST：测试
     */
    public void pay(String tn, Enum payMode) {
        if (tn != null) {
            UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null, tn, payMode.equals(PayMode.TEST) ? "01" : "00");
        }else{
            Log.e(this.getClass().getSimpleName(),"tn is null please check you tn");
        }
    }

    public enum PayMode {
        TEST, FORM;
    }
}
