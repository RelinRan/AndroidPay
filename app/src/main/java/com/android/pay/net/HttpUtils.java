package com.android.pay.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Relin
 * on 2018-09-10.
 */
public class HttpUtils {

    //编码格式
    private static final String CHARSET = "utf-8";
    //前缀
    private static final String PREFIX = "--";
    //边界标识 随机生成
    private static final String BOUNDARY = UUID.randomUUID().toString();
    //内容类型
    private static final String CONTENT_TYPE = "multipart/form-data";
    //换行
    private static final String LINE_FEED = "\r\n";

    private HttpHandler httpHandler;

    public HttpUtils() {
        httpHandler = new HttpHandler();
    }

    public void get(final String url, final RequestParams params, final OnHttpListener listener) {
        new Thread() {
            public void run() {
                HttpURLConnection conn = null;
                try {
                    String getUrl = "";
                    //根据传递的参数重新拼接
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
                        getUrl = requestUrl.toString().substring(0, requestUrl.lastIndexOf("&"));
                        Log.e(this.getClass().getSimpleName(), "get url : " + getUrl);
                    }
                    conn = (HttpURLConnection) new URL(getUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
                    conn.setConnectTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    //设置请求头参数
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("User-Agent", "Android");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

                    //=================================添加头部=================================
                    if (params != null && params.getHeaderParams() != null) {
                        for (Map.Entry<String, String> entry : params.getHeaderParams().entrySet()) {
                            conn.setRequestProperty("\"" + entry.getKey() + "\"", "\"" + entry.getValue() + "\"");
                        }
                    }
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        httpHandler.sendSuccessfulMsg(params, url, sb.toString(), listener);
                    } else {
                        httpHandler.sendExceptionMsg(params, url, new IOException(HttpHandler.HTTP_NO_RESPONSE), listener);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    httpHandler.sendExceptionMsg(params, url, e, listener);
                } catch (IOException e) {
                    e.printStackTrace();
                    httpHandler.sendExceptionMsg(params, url, e, listener);
                }
            }
        }.start();
    }

    public void post(final String url, final RequestParams params, final OnHttpListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setReadTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
                    conn.setConnectTimeout((int) (RequestParams.DEFAULT_TIME_OUT * 1000));
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    //设置请求头参数
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("User-Agent", "Android");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    //=================================添加头部=================================
                    if (params != null && params.getHeaderParams() != null) {
                        for (Map.Entry<String, String> entry : params.getHeaderParams().entrySet()) {
                            conn.setRequestProperty("\"" + entry.getKey() + "\"", "\"" + entry.getValue() + "\"");
                        }
                    }
                    //=================================文字参数==================================
                    //上传参数
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    if (params != null) {
                        //单个数据字符 --- 一般支付上面需要
                        if (params.getStringBody() != null) {
                            dos.writeBytes(params.getStringBody());
                        }
                        //一般文字参数
                        if (params.getStringParams() != null) {
                            Map<String, String> map = params.getStringParams();
                            StringBuilder sb = new StringBuilder();
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                sb.append(buildPostStringParams(entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                    dos.flush();
                    //=====================================文件上传===================================
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
                    //读取服务器返回信息
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line = null;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        httpHandler.sendSuccessfulMsg(params, url, response.toString(), listener);
                    } else {
                        httpHandler.sendExceptionMsg(params, url, new IOException(HttpHandler.HTTP_NO_RESPONSE), listener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    httpHandler.sendExceptionMsg(params, url, e, listener);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 创建Post请求文字参数
     *
     * @param key
     * @param value
     * @return
     */
    private StringBuilder buildPostStringParams(String key, String value) {
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
    private StringBuilder buildPostFileParams(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(BOUNDARY);
        sb.append(LINE_FEED);
        sb.append("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + value + "\"" + LINE_FEED);
        sb.append("Content-Type: application/octet-stream" + LINE_FEED); //此处的ContentType不同于 请求头 中Content-Type
        sb.append("Content-Transfer-Encoding: 8bit" + LINE_FEED);
        sb.append(LINE_FEED);// 参数头设置完以后需要两个换行，然后才是参数内容
        return sb;
    }


}
