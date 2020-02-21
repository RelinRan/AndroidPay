package com.android.pay.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class Http {

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
     * 网络接口Handler
     */
    private static Handler handler;

    /**
     * 接口线程池
     */
    private static ExecutorService executorService;

    static {
        handler = new Handler();
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * 创建服务器连接
     *
     * @param url           地址
     * @param requestMethod 请求方式 GET / POST
     * @return
     */
    protected static HttpURLConnection createHttpURLConnection(String url, String requestMethod) {
        HttpURLConnection conn = null;
        try {
            URL httpUrl = new URL(url);
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
            conn.setReadTimeout(30 * 1000);
            conn.setConnectTimeout(30 * 1000);
            conn.setReadTimeout(30 * 1000);
            conn.setConnectTimeout(30 * 1000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            if (requestMethod.equals("POST")) {
                conn.setDoOutput(true);
            }
            //设置请求头参数
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Android");
            conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + BOUNDARY);
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
                    Map<String, String> map = params.getStringParams();
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        sb.append(buildPostStringParams(entry.getKey(), entry.getValue()));
                    }
                    dos.writeBytes(sb.toString());
                }
                //纯字符参数
                if (params.getStringBody() != null) {
                    dos.writeBytes(params.getStringBody());
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
     * 创建Post请求文字参数
     *
     * @param key   名称
     * @param value 值
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
     * 请求
     *
     * @param requestMethod 请求方法
     * @param url           请求地址
     * @param params        参数
     * @param listener      回调
     */
    protected static void request(final String requestMethod, final String url, final RequestParams params, final OnHttpListener listener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                if (requestMethod.equals("GET")) {
                    Log.i("RRL", "->GET Url:" + createGetUrl(url, params));
                    conn = createHttpURLConnection(createGetUrl(url, params), requestMethod);
                }
                if (requestMethod.equals("POST")) {
                    conn = createHttpURLConnection(url, requestMethod);
                    addPostParams(conn, params);
                }
                int code = -1;
                try {
                    code = conn.getResponseCode();
                    Log.i("RRL", "->GET request code:" + code);
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        //获取返回数据
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        handler.sendSuccessfulMsg(params, url, code, sb.toString(), listener);
                    } else {
                        handler.sendExceptionMsg(params, url, code, new IOException(Handler.HTTP_NO_RESPONSE), listener);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendExceptionMsg(params, url, code, e, listener);
                } finally {
                    conn.disconnect();
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
        if (!isAvailable(context)) {
            Response response = new Response();
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
        if (!isAvailable(context)) {
            Response response = new Response();
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
     * 判断网络是否可用 [需要如下权限]
     *
     * @param context 上下文
     * @return
     */
    public static boolean isAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
