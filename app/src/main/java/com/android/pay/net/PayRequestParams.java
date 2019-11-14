package com.android.pay.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Relin
 * 请求参数
 * on 2017/3/13.
 */

public class PayRequestParams {

    /**
     * 文字参数
     */
    private Map<String, String> stringParams;


    /**
     * 字符串参数
     */
    private String body;


    public PayRequestParams() {

    }

    /**
     * 添加文字参数
     * Add String Params
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        if (stringParams == null) {
            stringParams = new HashMap<>();
        }
        stringParams.put(key, value == null ? "" : value);
    }


    /**
     * 添加字符串实例
     *
     * @param body
     */
    public void addStringBody(String body) {
        this.body = body;
    }

    /**
     * 获取文字参数
     *
     * @return
     */
    public Map<String, String> getStringParams() {
        return stringParams;
    }

    /**
     * 返回字符串的Body实例
     *
     * @return
     */
    public String getStringBody() {
        return body;
    }


}
