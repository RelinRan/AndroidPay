package com.android.pay.net;

/**
 * Json参数反转义处理
 */
public class JsonEscape {

    /**
     * 处理转义字符
     *
     * @param stringParams
     * @return
     */
    public static String escape(String stringParams) {
        if (stringParams.contains("\"[")) {
            stringParams = stringParams.replace("\"[", "[");
        }
        if (stringParams.contains("]\"")) {
            stringParams = stringParams.replace("]\"", "]");
        }
        if (stringParams.contains("\"{")) {
            stringParams = stringParams.replace("\"{", "{");
        }
        if (stringParams.contains("}\"")) {
            stringParams = stringParams.replace("}\"", "}");
        }
        if (stringParams.contains("\n")) {
            stringParams = stringParams.replace("\n", "\\n");
        }
        if (stringParams.contains("\t")) {
            stringParams = stringParams.replace("\t", "\\t");
        }
        if (stringParams.contains("\r")) {
            stringParams = stringParams.replace("\r", "\\r");
        }
        if (stringParams.contains(">")) {
            stringParams = stringParams.replace(">", "&gt;");
        }
        if (stringParams.contains("<")) {
            stringParams = stringParams.replace(">", "&lt;");
        }
        if (stringParams.contains(" ")) {
            stringParams = stringParams.replace(" ", "&nbsp;");
        }
        if (stringParams.contains("\\")) {
            stringParams = stringParams.replace("\\", "&quot;");
        }
        if (stringParams.contains("\\'")) {
            stringParams = stringParams.replace("\\'", "&#39;");
        }
        if (stringParams.contains("\\\\")) {
            stringParams = stringParams.replace("\\\\'", "\\\\\\\\");
        }
        return stringParams;
    }

}
