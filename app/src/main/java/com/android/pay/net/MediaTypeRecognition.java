package com.android.pay.net;

import android.text.TextUtils;

import java.io.File;

public class MediaTypeRecognition {

    /**
     * 识别
     *
     * @param file
     * @return
     */
    public static String identifyMediaType(File file) {
        if (file == null) {
            return "*/*";
        }
        return identifyMediaType(file.getName());
    }

    /**
     * 识别类型
     *
     * @param name
     * @return
     */
    public static String identifyMediaType(String name) {
        if (TextUtils.isEmpty(name)) {
            return "*/*";
        }
        if (name.toUpperCase().endsWith(".JPG") || name.toUpperCase().endsWith(".JPEG")) {
            return "image/jpeg";
        }
        if (name.toUpperCase().endsWith(".PNG")) {
            return "image/png";
        }
        if (name.toUpperCase().endsWith(".GIF")) {
            return "image/gif";
        }
        if (name.toUpperCase().endsWith(".BMP")) {
            return "image/bmp";
        }
        if (name.toUpperCase().endsWith(".ZIP")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }

}
