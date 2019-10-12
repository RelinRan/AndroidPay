package com.android.pay.net;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Relin on 2018/5/10.
 * 日期工具类
 */

public class DateUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_24H_MM_SS = "HH:mm:ss";
    public static final String DATE_FORMAT_12H_MM_SS = "hh:mm:ss";
    public static final String DATE_FORMAT_24H_MM = "24H:mm";
    public static final String DATE_FORMAT_12H_MM = "12H:mm";
    public static final String DATE_FORMAT_MM_DD = "MM-DD";
    public static final String DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYY_MM_DD_BLANK_12H_MM = "yyyy-MM-dd hh:mm";
    public static final String DATE_FORMAT_YYYY_MM_DD_BLANK_12H_MM_SS = "yyyy-MM-dd hh:mm:ss";

    /**
     * 现在的日期时间
     *
     * @return
     */
    public static String now() {
        return buildFormat(DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS).format(new Date());
    }

    /**
     * 现在的日期时间
     *
     * @param dateFormat 日期格式
     * @return
     */
    public static String now(String dateFormat) {
        return buildFormat(dateFormat).format(new Date());
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp 时间戳单位秒
     * @return
     */
    public static String parseFromTimestamp(String timestamp) {
        if (TextUtils.isEmpty(timestamp)) {
            return "";
        }
        return buildFormat(DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS).format(new Date(Long.parseLong(timestamp + "000")));
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp  时间戳单位秒
     * @param dateFormat 时间转换格式
     * @return
     */
    public static String parseFromTimestamp(String timestamp, String dateFormat) {
        if (TextUtils.isEmpty(timestamp)) {
            return "";
        }
        return buildFormat(dateFormat).format(new Date(Long.parseLong(timestamp + "000")));
    }

    /**
     * 时间转时间戳
     *
     * @param time 时间字符串
     * @return
     */
    public static long parseToTimestamp(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        return parseToTimestamp(time, DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS);
    }

    /**
     * 时间转时间戳
     *
     * @param time       时间字符串
     * @param dateFormat 时间字符格式
     * @return
     */
    public static long parseToTimestamp(String time, String dateFormat) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        return parse(time, dateFormat).getTime() / 1000;
    }

    /**
     * 字符串转时间
     *
     * @param time 时间字符串
     * @return
     */
    public static Date parse(String time) {
        Date date = null;
        try {
            date = buildFormat(DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转时间
     *
     * @param time       时间字符串
     * @param dateFormat 时间字符格式
     * @return
     */
    public static Date parse(String time, String dateFormat) {
        Date date = null;
        try {
            date = buildFormat(dateFormat).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期时间差
     *
     * @param start      开始时间
     * @param end        结束时间
     * @param dateFormat 时间格式
     * @return
     */
    public static long timeDiff(String start, String end, String dateFormat) {
        return parse(end, dateFormat).getTime() - parse(start, dateFormat).getTime();
    }

    /**
     * 创建时间日期格式对象
     *
     * @param format
     * @return
     */
    public static SimpleDateFormat buildFormat(String format) {
        return new SimpleDateFormat(format);
    }


    /**
     * 通过年份和月份获取对应的月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int dayOfMonth(int year, int month) {
        if (year % 100 == 0 && year % 400 == 0 && month == 2) return 29;
        else {
            switch (month) {
                case 2:
                    return 28;
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
            }
        }
        return 0;
    }

}
