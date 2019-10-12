package com.android.pay.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Relin
 * on 2018-09-25.
 */
public class HttpCookie {

    private static Context context;

    public HttpCookie(Context context){
        this.context = context;
    }

    /**
     * 保存Cookie
     *
     * @param url     请求
     * @param conn    连接
     * @param expires 过期时间[单位秒]
     */
    public void saveCookie(URL url, HttpURLConnection conn, int expires) {
        String cookie = conn.getHeaderField("Set-Cookie");
        if (cookie == null) {
            return;
        }
        List<Map<String, String>> cookieList = getCookie(url.getHost());
        for (int i = 0; i < ListUtils.getSize(cookieList); i++) {
            String expiresAt = cookieList.get(i).get("expiresAt");
            if (Long.parseLong(expiresAt) < new Date().getTime()) {
                cookieList.remove(i);
            }
        }
        Map<String, String> cookieMap = decodeCookie(cookie, expires);
        cookieMap.put("host", url.getHost());
        cookieList.add(cookieMap);
        persistenceCookie(url.getHost(), cookieList);
    }

    /**
     * 加载Cookie
     *
     * @param url 请求
     * @return Cookie字符
     */
    public String loadCookie(URL url) {
        List<Map<String, String>> cookieList = getCookie(url.getHost());
        String cookie = "";
        for (int i = 0; i < ListUtils.getSize(cookieList); i++) {
            Map<String, String> cookieMap = cookieList.get(i);
            String host = cookieMap.get("host");
            if (host.equals(url.getHost())) {
                cookie = cookieList.get(i).get("cookie");
            }
        }
        return cookie;
    }

    /**
     * 解析Cookie
     *
     * @param cookie  cookie字符串
     * @param expires 过期时间
     * @return HucCookie Map对象
     */
    public Map<String, String> decodeCookie(String cookie, int expires) {
        Map<String, String> map = new HashMap<>();
        map.put("cookie", cookie);
        if (!TextUtils.isEmpty(cookie)) {
            String start = cookie.substring(0, cookie.lastIndexOf(";"));
            String end = cookie.substring(cookie.lastIndexOf(";") + 1);
            String[] cookies = start.split(";");
            map.put("end", end);
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].contains("=")) {
                    String keyValue[] = cookies[i].split("=");
                    map.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            map.put("expiresAt", new Date().getTime() + expires + "");
        }
        return map;
    }

    /**
     * Cookie日期转换
     *
     * @param expires
     * @return
     */
    public long parseExpires(String expires) {
        if (expires == null || expires.length() == 0) {
            return 0;
        }
        String monthName[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String time = expires.split(", ")[1].replace(" GMT", "");
        String timeSplit[] = time.split(" ");
        String start[] = timeSplit[0].split("-");
        String year = start[2];
        String month = start[1];
        String day = start[0];
        for (int i = 0; i < monthName.length; i++) {
            if (monthName[i].equals(month)) {
                month = (i + 1) + "";
            }
        }
        return DateUtils.parse(year + "-" + month + "-" + day + " " + timeSplit[1]).getTime();
    }

    /**
     * 获取Cookie
     *
     * @return
     */
    public static List<Map<String, String>> getCookie(String host) {
        String cookieJson = DataStorage.with(context).getString(host, "[]");
        List<Map<String, String>> cookieList = JsonParser.parseJSONArray(cookieJson);
        return cookieList;
    }

    /**
     * 持久化Cookie
     *
     * @param cookieList cookie列表
     */
    private void persistenceCookie(String host, List<Map<String, String>> cookieList) {
        String cookieJson;
        if (cookieList == null || cookieList.size() == 0) {
            cookieJson = "[]";
        } else {
            cookieJson = JsonParser.parseMapList(cookieList);
        }
        Log.i(this.getClass().getSimpleName(), cookieJson);
        DataStorage.with(context).put(host, cookieJson);
    }

    /**
     * 删除过期Cookie
     *
     * @param cookieList cookie列表
     */
    private void removeExpires(List<Map<String, String>> cookieList) {
        for (int i = 0; i < ListUtils.getSize(cookieList); i++) {
            Map<String, String> cookieMap = cookieList.get(i);
            long expires = System.currentTimeMillis();
            if (cookieMap.get("expires") != null) {
                expires = parseExpires(cookieMap.get("expires"));
            }
            if (cookieMap.get("Expires") != null) {
                expires = parseExpires(cookieMap.get("Expires"));
            }
            if (expires < System.currentTimeMillis()) {
                cookieList.remove(i);
            }
        }
    }

    /**
     * 删除cookie
     */
    public static void remove(String host) {
        DataStorage.with(context).put(host, "[]");
    }

}
