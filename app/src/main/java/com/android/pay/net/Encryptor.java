package com.android.pay.net;

import java.util.Random;

/**
 * Created by Relin
 * on 2018-08-01.
 * 加密器工具
 * 支持普通加密成字母加数字、加密成文字、加密成图形
 * encryptor tool supports normal encryption into letters and Numbers,
 * encryption into text, encryption into graphics
 */

public class Encryptor {

    public enum Type {
        NORMAL,
        WORLD,
        SHAPE
    }

    public static final char DIGITAL[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final char ENCRYPTION_NORMAL[] = {'4', '0', '2', '6', '3', '9', '5', '7', '1', '8'};
    public static final char ENCRYPTION_WORLD[] = {'ㄌ', 'ㄎ', 'ㄊ', '큐', 'ㄓ', 'ㄝ', 'ㄞ', 'ㄢ', 'ㄨ', 'ㄤ'};
    public static final char ENCRYPTION_SHAPE[] = {'▪', '╦', '▓', '▣', '▩', '▭', '▄', '░', '▯', '╠'};

    public static final String SEGMENTATION_NORMAL[] = {
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "Q", "W", "E",
            "R", "T", "Y", "U", "I", "O", "P"
    };
    public static final String SEGMENTATION_WORLD[] = {
            "❤", "☼", "✠", "❏", "☢", "❖", "※", "★", "ღ", "♣", "❋", "☯", "✌", "®", "☸", "℉", "∰", "｡◕‿◕｡", "◕‿-｡",
            "☐", "☒", "☲", "卍", "◑", "⊙", "ぃ", "❦", "☪", "の", "⊕", "☄", "◎", "◈", "☈", "╬", "§", "✣", "☀", "☠",
            "ぁ", "あ", "ぃ", "い", "ぅ", "う", "ぇ", "え", "ぉ", "お", "か", "が", "き", "ぎ", "ぐ", "け", "げ", "こ", "ご",
            "す", "ず", "せ", "ぜ", "そ", "ぞ", "だ", "ち", "ぢ", "っ", "つ", "づ", "て", "で", "と", "ど", "な", "に", "ぬ",
            "Ж", "И", "ф", "ё", "Щ", "Ю", "Б", "Ы", "л", "д", "Я", "Ц", "Ё", "э", "й", "г", "Ф", "ζ", "Ψ", "σ",
            "弍", "〆", "弎", "甴", "〡", "〢", "〣", "〤", "〥", "〦", "〧", "〨", "〩", "卄", "巜", "朤", "弐", "氺", "曱", "兀"
    };
    public static final String SEGMENTATION_SHAPE[] = {
            "☑", "▲", "▼", "☷", "⊿", "◤", "◇", "▤", "▦", "▨", "◘", "◗", "◖", "◍", "●", "⊗", "✪", "☮", "✡",
            "✯", "☯", "☢", "◌", "◉", "❂", "◙"
    };


    public static String encode(String value) {
        return encode(value, Type.NORMAL);
    }


    public static String decode(String value) {
        return decode(value, Type.NORMAL);
    }

    /**
     * 加密
     *
     * @param value
     * @return
     */
    public static String encode(String value, Type type) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int intChar = (int) chars[i];
            String strChar = encodeDigital((intChar) << 1, type);
            if (i != chars.length - 1) {
                int index;
                String segmentation = "";
                switch (type) {
                    case NORMAL:
                        index = new Random().nextInt(SEGMENTATION_NORMAL.length - 1);
                        segmentation = SEGMENTATION_NORMAL[index];
                        break;
                    case WORLD:
                        index = new Random().nextInt(SEGMENTATION_WORLD.length - 1);
                        segmentation = SEGMENTATION_WORLD[index];
                        break;
                    case SHAPE:
                        index = new Random().nextInt(SEGMENTATION_SHAPE.length - 1);
                        segmentation = SEGMENTATION_SHAPE[index];
                        break;
                }
                sbu.append(strChar);
                sbu.append(segmentation);
            } else {
                sbu.append(strChar);
            }
        }
        return sbu.toString();
    }


    /**
     * 解密
     *
     * @param value
     * @return
     */
    public static String decode(String value, Type type) {
        StringBuffer sbu = new StringBuffer();
        int length = 0;
        String[] segmentation = new String[0];
        switch (type) {
            case NORMAL:
                length = SEGMENTATION_NORMAL.length;
                segmentation = SEGMENTATION_NORMAL;
                break;
            case WORLD:
                length = SEGMENTATION_WORLD.length;
                segmentation = SEGMENTATION_WORLD;
                break;
            case SHAPE:
                length = SEGMENTATION_SHAPE.length;
                segmentation = SEGMENTATION_SHAPE;
                break;
        }
        for (int j = 0; j < length; j++) {
            String letter = segmentation[j];
            if (value.contains(letter)) {
                value = value.replace(letter, ",");
            }
        }
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            String decodeDigitalStr = decodeDigital(chars[i], type);
            int charInt = Integer.parseInt(decodeDigitalStr);
            sbu.append((char) (charInt >> 1));
        }
        return sbu.toString();
    }

    /**
     * 加密数字
     *
     * @param res
     * @return
     */
    private static String encodeDigital(int res, Type type) {
        char[] encryption = new char[0];
        switch (type) {
            case NORMAL:
                encryption = ENCRYPTION_NORMAL;
                break;
            case WORLD:
                encryption = ENCRYPTION_WORLD;
                break;
            case SHAPE:
                encryption = ENCRYPTION_SHAPE;
                break;
        }
        char[] chars = String.valueOf(res).toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < DIGITAL.length; j++) {
                if (chars[i] == DIGITAL[j]) {
                    sb.append(encryption[Integer.parseInt(DIGITAL[j] + "")]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 解析数字
     *
     * @param res
     * @return
     */
    private static String decodeDigital(String res, Type type) {
        char[] encryption = new char[0];
        switch (type) {
            case NORMAL:
                encryption = ENCRYPTION_NORMAL;
                break;
            case WORLD:
                encryption = ENCRYPTION_WORLD;
                break;
            case SHAPE:
                encryption = ENCRYPTION_SHAPE;
                break;
        }
        char[] chars = res.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < encryption.length; j++) {
                if (chars[i] == (encryption[j])) {
                    sb.append(DIGITAL[Integer.parseInt(DIGITAL[j] + "")]);
                }
            }
        }
        return sb.toString();
    }
}
