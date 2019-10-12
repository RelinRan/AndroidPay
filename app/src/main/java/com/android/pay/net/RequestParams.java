package com.android.pay.net;

import android.graphics.Bitmap;
import android.text.TextUtils;
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
     * 连接超时
     */
    public static final int CONNECT_TIME_OUT = 0x0001;

    /**
     * 读取超时
     */
    public static final int READ_TIME_OUT = 0x0002;

    /**
     * 写入超时
     */
    public static final int WRITE_TIME_OUT = 0x0003;

    /**
     * 请求内容类型
     */
    public static final int REQUEST_CONTENT_TYPE = 0x0006;

    /**
     * 最大连接数
     */
    public static final int MAX_IDLE_CONNECTIONS = 0x0007;

    /**
     * 最大连接数
     */
    public static final int KEEP_ALIVE_DURATION = 0x0008;

    /**
     * 请求标识
     */
    public static final int REQUEST_TAG = 0x0009;

    /**
     * 用户代理
     */
    public static final int USER_AGENT = 0x0010;

    /**
     * Cookie默认过期时间 - 如果服务器后台没返回就按照这个过滤
     */
    public static final int COOKIE_EXPIRES_SECONDS = 0x0011;

    /**
     * 默认最大连接数
     */
    public static final int DEFAULT_MAX_IDLE_CONNECTIONS = 20;

    /**
     * 默认连接存活时间
     */
    public static final int DEFAULT_KEEP_ALIVE_DURATION = 1;

    /**
     * 默认超时30秒
     */
    public static final long DEFAULT_TIME_OUT = 30;

    /**
     * 默认超时60 *3 分钟
     */
    public static final long DEFAULT_COOKIE_EXPIRES = 3 * 60 * 60;

    /**
     * 请求内容 - JSON
     */
    public static final String REQUEST_CONTENT_JSON = "json";

    /**
     * 请求内容 - 表单
     */
    public static final String REQUEST_CONTENT_FORM = "form";

    /**
     * 请求内容 - 字符串
     */
    public static final String REQUEST_CONTENT_STRING = "string";

    /**
     * 文件参数
     */
    private Map<String, File> fileParams;

    /**
     * 文字参数
     */
    private Map<String, String> stringParams;

    /**
     * Header参数
     */
    private Map<String, String> headerParams;

    /**
     * 工具参数
     */
    private Map<Integer, String> optionParams;

    /**
     * 字符串参数
     */
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
     * @param compressedSize 压缩大小
     */
    public void add(String key, File value, int compressedSize) {
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
        if (!TextUtils.isEmpty(path)) {
            path = path.toUpperCase();
        }
        Log.e(this.getClass().getSimpleName(), "addParams value:" + path);
        if (path.contains(".JPG") || path.contains(".JPEG")) {
            value = IOUtils.compress(value, Bitmap.CompressFormat.JPEG, 200);
        }
        if (path.contains(".PNG")) {
            value = IOUtils.compress(value, Bitmap.CompressFormat.PNG, 200);
        }
        fileParams.put(key, value);
    }

    /**
     * 添加文件参数
     * Add File Params
     *
     * @param key
     * @param value
     */
    public void add(String key, File value) {
        add(key, value, 200);
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
        //设置默认为表单类型
        if (optionParams == null) {
            optionParams = new HashMap<>();
            optionParams.put(REQUEST_CONTENT_TYPE, REQUEST_CONTENT_FORM);
            optionParams.put(COOKIE_EXPIRES_SECONDS, DEFAULT_COOKIE_EXPIRES + "");
        }
        return optionParams;
    }

}
