package com.android.pay.net;

/**
 * Created by Ice
 * OkHttp回调函数
 * on 2017/3/20.
 */

public interface OnHttpListener {

    /**
     * get data from http failure method callback
     *
     * @param result response succeed information
     */
    void onHttpFailure(HttpResponse result);

    /**
     * get data from http succeed method callback
     *
     * @param result response succeed information
     */
    void onHttpSucceed(HttpResponse result);

}
