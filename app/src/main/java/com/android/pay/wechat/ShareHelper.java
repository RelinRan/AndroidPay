package com.android.pay.wechat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShareHelper {

    /**
     * File 转 byte[]
     *
     * @param path 文件路径
     * @return
     */
    public static byte[] decodeFile(String path) {
        byte[] buffer = null;
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Bitmap转byte[]
     *
     * @param bitmap 文件位图
     * @return
     */
    public static byte[] decodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    /**
     * byte[]转Bitmap
     *
     * @param bytes 文件二进制
     * @return
     */
    public static Bitmap decodeByte(byte[] bytes) {
        if (bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    /**
     * 图片URL解析到Bitmap
     *
     * @param urlPath  图片地址
     * @param width    宽度
     * @param height   高度
     * @param listener 回调
     */
    public static void decodeUrl(final String urlPath, final int width, final int height, final OnUrlDecodeBitmapListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                URL url;
                try {
                    url = new URL(urlPath);
                    HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
                    httpUrl.connect();
                    InputStream inputStream = httpUrl.getInputStream();
                    Bitmap thumbImage = BitmapFactory.decodeStream(inputStream);
                    Bitmap bitmap = Bitmap.createScaledBitmap(thumbImage, width, height, true);
                    if (listener != null) {
                        listener.onUrlDecode(bitmap);
                    }
                    inputStream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * URL解析到Bitmap
     */
    public interface OnUrlDecodeBitmapListener {

        void onUrlDecode(Bitmap bitmap);

    }


    /***
     * 图片URL转为byte数组
     * @param urlPath URL回调地址
     * @param listener 解析回调
     */
    public static void decodeUrl(final String urlPath, final OnUrlDecodeByteListener listener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                byte[] data;
                URL url;
                InputStream input;
                try {
                    url = new URL(urlPath);
                    HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
                    httpUrl.connect();
                    httpUrl.getInputStream();
                    input = httpUrl.getInputStream();
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int numBytesRead;
                    while ((numBytesRead = input.read(buf)) != -1) {
                        output.write(buf, 0, numBytesRead);
                    }
                    data = output.toByteArray();
                    if (listener != null) {
                        listener.onUrlDecode(data);
                    }
                    output.close();
                    input.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * URL解析byte数据
     */
    public interface OnUrlDecodeByteListener {

        void onUrlDecode(byte[] data);

    }


}
