package com.android.pay.net;

import android.content.Context;
import android.text.TextUtils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Relin
 * on 2018-09-10.
 */
public class HttpUtils {

    /**
     * 编码格式
     */
    private static final String CHARSET = "utf-8";
    /**
     * 前缀
     */
    private static final String PREFIX = "--";
    /**
     * 换行
     */
    private static final String LINE_FEED = "\r\n";
    /**
     * 边界标识 随机生成
     */
    private static final String BOUNDARY = UUID.randomUUID().toString();

    /**
     * 地址对象
     */
    private static URL httpUrl;

    /**
     * Cookie对象
     */
    private static HttpCookie cookie;

    /**
     * Http异步处理
     */
    private static HttpHandler httpHandler;

    /**
     * 服务器连接对象
     */
    private static HttpURLConnection conn;

    /**
     * 连接池
     */
    private static ExecutorService executorService;

    private HttpUtils() {

    }

    static {

        httpHandler = new HttpHandler();
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * 青内存
     */
    public static void destroy() {
        if (httpHandler != null) {
            httpHandler.removeCallbacksAndMessages(null);
            httpHandler = null;
        }
    }

    /**
     * 创建服务器连接
     *
     * @param url           地址
     * @param requestMethod 请求方式 GET / POST
     * @param params        请求参数
     * @return
     */
    protected static HttpURLConnection createHttpURLConnection(String url, String requestMethod, RequestParams params) {
        try {
            httpUrl = new URL(url);
            boolean isHttps = url.startsWith("https");
            //https
            if (isHttps) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpUrl.openConnection();
                httpsURLConnection.setSSLSocketFactory(HttpsSSLSocketFactory.factory());
                httpsURLConnection.setHostnameVerifier(new HttpsHostnameVerifier());
                conn = httpsURLConnection;
            } else {
                conn = (HttpURLConnection) httpUrl.openConnection();
            }
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod(requestMethod);
            conn.setReadTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
            conn.setConnectTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
            Map<Integer, String> options = params.getOptionParams();
            if (!TextUtils.isEmpty(options.get(RequestParams.CONNECT_TIME_OUT))) {
                conn.setReadTimeout(Integer.parseInt(options.get(RequestParams.READ_TIME_OUT)) * 1000);
            }
            if (!TextUtils.isEmpty(options.get(RequestParams.READ_TIME_OUT))) {
                conn.setConnectTimeout(Integer.parseInt(options.get(RequestParams.CONNECT_TIME_OUT)) * 1000);
            }
            conn.setDoInput(true);
            conn.setUseCaches(false);
            if (requestMethod.equals("POST")) {
                conn.setDoOutput(true);
            }
            //加载缓存的Cookie
            conn.setRequestProperty("Cookie", cookie.loadCookie(httpUrl));
            //设置请求头参数
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            String agent = params.getOptionParams().get(RequestParams.USER_AGENT);
            conn.setRequestProperty("User-Agent", TextUtils.isEmpty(agent) ? "Android" : agent);
            if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_FORM)) {
                conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
            }
            if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_JSON)) {
                conn.setRequestProperty("Content-Type", "application/json");
            }
            if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_STRING)) {
                conn.setRequestProperty("Content-Type", "application/octet-stream" + ";boundary=" + BOUNDARY);
            }
            //添加头部
            if (params != null && params.getHeaderParams() != null) {
                for (Map.Entry<String, String> entry : params.getHeaderParams().entrySet()) {
                    conn.setRequestProperty("\"" + entry.getKey() + "\"", "\"" + entry.getValue() + "\"");
                }
            }
            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 创建Get请求url地址
     *
     * @param url    地址
     * @param params 请求参数
     * @return
     */
    protected static String createGetUrl(String url, RequestParams params) {
        StringBuffer requestUrl = new StringBuffer();
        requestUrl.append(url);
        requestUrl.append("?");
        if (params != null && params.getStringParams() != null) {
            Map<String, String> stringParams = params.getStringParams();
            for (String key : stringParams.keySet()) {
                String value = stringParams.get(key);
                requestUrl.append(key);
                requestUrl.append("=");
                requestUrl.append(value);
                requestUrl.append("&");
            }
        }
        String combinationUrl = requestUrl.toString().substring(0, requestUrl.lastIndexOf("&"));
        return combinationUrl;
    }

    /**
     * 添加Post请求参数
     *
     * @param conn   服务器连接对象
     * @param params 参数
     */
    protected static void addPostParams(HttpURLConnection conn, RequestParams params) {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(conn.getOutputStream());
            //文字参数
            if (params != null) {
                //单个数据字符 --- 一般支付上面需要
                if (params.getStringParams() != null) {
                    //表单数据
                    if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_FORM)) {
                        Map<String, String> map = params.getStringParams();
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            sb.append(buildPostStringParams(entry.getKey(), entry.getValue()));
                        }
                        dos.writeBytes(sb.toString());
                    }
                    //纯字符参数
                    if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_STRING)) {
                        if (params.getStringBody() != null) {
                            dos.writeBytes(params.getStringBody());
                        }
                    }
                    //json文字参数
                    if (params.getOptionParams().get(RequestParams.REQUEST_CONTENT_TYPE).equals(RequestParams.REQUEST_CONTENT_JSON)) {
                        String stringParams = JsonEscape.escape(JsonParser.parseMap(params.getStringParams()));
                        dos.writeBytes(stringParams);
                    }
                }
            }
            //文件上传
            if (params != null && params.getFileParams() != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, File> fileEntry : params.getFileParams().entrySet()) {
                    sb.append(buildPostFileParams(fileEntry.getKey(), fileEntry.getValue().getName()));
                    dos.writeBytes(sb.toString());
                    dos.flush();
                    InputStream is = new FileInputStream(fileEntry.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    is.close();
                    dos.writeBytes(LINE_FEED);
                }
            }
            //请求结束标志
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_FEED);
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求
     *
     * @param requestMethod
     * @param url
     * @param params
     * @param listener
     */
    protected static void request(final String requestMethod, final String url, final RequestParams params, final OnHttpListener listener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (requestMethod.equals("GET")) {
                    createHttpURLConnection(createGetUrl(url, params), requestMethod, params);
                }
                if (requestMethod.equals("POST")) {
                    addPostParams(createHttpURLConnection(url, requestMethod, params), params);
                }
                int code = -1;
                try {
                    code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        //保存Cookie
                        cookie.saveCookie(httpUrl, conn, Integer.parseInt(params.getOptionParams().get(RequestParams.COOKIE_EXPIRES_SECONDS)));
                        //获取返回数据
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        httpHandler.sendSuccessfulMsg(params, url, code, sb.toString(), listener);
                    } else {
                        httpHandler.sendExceptionMsg(params, url, code, new IOException(HttpHandler.HTTP_NO_RESPONSE), listener);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    httpHandler.sendExceptionMsg(params, url, code, e, listener);
                } finally {
                    conn.disconnect();
                    conn = null;
                }
            }
        });
    }

    /**
     * Get请求参数
     *
     * @param url      请求地址
     * @param params   参数
     * @param listener 监听
     */
    public static void get(Context context, String url, RequestParams params, OnHttpListener listener) {
        cookie = new HttpCookie(context);
        if (!NetworkUtils.isAvailable(context)) {
            HttpResponse response = new HttpResponse();
            response.url(url);
            response.code(-11);
            response.requestParams(params);
            response.exception(new Exception("No network connection, unable to request data interface."));
            listener.onHttpFailure(response);
            return;
        }
        request("GET", url, params, listener);
    }

    /**
     * Post请求
     *
     * @param url      请求地址
     * @param params   参数
     * @param listener 监听
     */
    public static void post(Context context, String url, RequestParams params, OnHttpListener listener) {
        cookie = new HttpCookie(context);
        if (!NetworkUtils.isAvailable(context)) {
            HttpResponse response = new HttpResponse();
            response.url(url);
            response.code(-11);
            response.requestParams(params);
            response.exception(new Exception("No network connection, unable to request data interface."));
            listener.onHttpFailure(response);
            return;
        }
        request("POST", url, params, listener);
    }


    /**
     * 创建Post请求文字参数
     *
     * @param key
     * @param value
     * @return
     */
    protected static StringBuilder buildPostStringParams(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(BOUNDARY);
        sb.append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_FEED);
        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_FEED);
        sb.append("Content-Transfer-Encoding: 8bit" + LINE_FEED);
        sb.append(LINE_FEED);// 参数头设置完以后需要两个换行，然后才是参数内容
        sb.append(value);
        sb.append(LINE_FEED);
        return sb;
    }

    /**
     * 创建Post请求的文件参数
     *
     * @param key
     * @param value
     * @return
     */
    protected static StringBuilder buildPostFileParams(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(BOUNDARY);
        sb.append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + value + "\"" + LINE_FEED);
        sb.append("Content-Type: " + MediaTypeRecognition.identifyMediaType(value) + LINE_FEED); //此处的ContentType不同于 请求头 中Content-Type
        sb.append("Content-Transfer-Encoding: 8bit" + LINE_FEED);
        sb.append(LINE_FEED);// 参数头设置完以后需要两个换行，然后才是参数内容
        return sb;
    }


}
