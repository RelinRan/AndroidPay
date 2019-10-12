package com.android.pay.net;

import java.util.HashMap;
import java.util.Map;

public class HttpCode {

    public static Map<Integer, String> codeMap;

    public static int code[] = {
            -1,

            200,
            201,
            202,
            203,
            204,
            205,
            206,

            400,
            401,
            403,
            404,
            406,
            407,
            408,
            409,
            410,
            411,
            412,
            413,
            414,
            415,
            416,
            417,

            405,
            501,
            502,
            503,
            504,
            505,
    };

    public static String message[] = {
            "请求代码异常",//-1

            "请求成功",//200
            "服务器资源创建",//201
            "服务器已接受请求",//202
            "非授权信息",//203
            "无请求结果",//204
            "重置内容",//205
            "处理部分GET请求",//206

            "错误请求",//400
            "请求未授权",//401
            "请求被拒绝",//403
            "请求不存在",//404
            "请求方法禁用",//405
            "请求不被接受该",//406
            "请求需要代理",//407
            "请求超时",//408
            "请求冲突",//409
            "请求资源被删",//410
            "请求标头长度错误",//411
            "请求条件未满足",//412
            "请求实体过大",//413
            "请求URL过长",//414
            "请求媒体类型不支持",//415
            "请求范围不符",//416
            "请求标头不满足要求",//417
            "服务器无法识别请求方法",//501
            "错误网关",//502
            "服务不可用",//503
            "网关超时",//504
            "HTTP版本不受支持"//505
    };


    static {
        codeMap = new HashMap<>();
        for (int i = 0; i < code.length; i++) {
            codeMap.put(code[i], message[i]);
        }
    }

    /**
     * 转换代码信息
     *
     * @param code 请求代码
     * @return
     */
    public static String parseCode(int code) {
        for (int key : codeMap.keySet()) {
            if (code == key) {
                return codeMap.get(key);
            }
        }
        return "结果代码[" + code + "]";
    }

}
