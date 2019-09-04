package com.android.pay.net;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 请求参数
 * on 2017/3/13.
 */

public class RequestParams {
    /**
     * 超时
     */
    public static final int CONNECT_TIME_OUT = 0x0001;
    public static final int READ_TIME_OUT = 0x0002;
    public static final int WRITE_TIME_OUT = 0x0003;
    /**
     * 是否持久化session
     */
    public static final int SESSION_IS_PERSISTENT = 0x0005;
    /**
     * 默认超时15秒
     */
    public static final long DEFAULT_TIME_OUT = 10;

    /**
     * 持久化session
     */
    public static final String SESSION_PERSISTENT = "session_persistent";
    /**
     * 不持久化session
     */
    public static final String SESSION_ID = "session_id";


    //文件参数
    private Map<String, File> fileParams;
    //文字参数
    private Map<String, String> stringParams;
    //Header参数
    private Map<String, String> headerParams;
    //工具参数
    private Map<Integer, String> optionParams;
    //字符串参数
    private String body;


    public RequestParams() {
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
     * 添加文件参数
     * Add File Params
     *
     * @param key
     * @param value
     */
    public void add(String key, File value) {
        if (value == null) {
            return;
        }
        if (fileParams == null) {
            fileParams = new HashMap<>();
        }
        if (!value.exists()) {
            Log.e(this.getClass().getSimpleName(), "addParams file is not exist!" + value.getAbsolutePath());
        }
        //压缩图片
        String path = value.getAbsolutePath();
        Log.e(this.getClass().getSimpleName(), "addParams value:" + path);
        fileParams.put(key, value);
    }


    /**
     * 添加头文件参数
     * Add Header Params
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<>();
        }
        if (value == null) {
            return;
        }
        headerParams.put(key, value);
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
     * 获取文件参数
     *
     * @return
     */
    public Map<String, File> getFileParams() {
        return fileParams;
    }

    /**
     * 获取头文件参数
     *
     * @return
     */
    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    /**
     * 返回字符串的Body实例
     *
     * @return
     */
    public String getStringBody() {
        return body;
    }

    /**
     * 添加工具参数
     *
     * @param key
     * @param value
     */
    public void add(int key, String value) {
        if (optionParams == null) {
            optionParams = new HashMap<>();
        }
        if (value == null) {
            return;
        }
        optionParams.put(key, value);
    }

    /**
     * 获取工具参数
     *
     * @return
     */
    public Map<Integer, String> getOptionParams() {
        return optionParams;
    }

}
