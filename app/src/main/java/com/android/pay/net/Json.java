package com.android.pay.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Relin
 * on 2018-07-10.
 * 支持Json解析的工具，工具支持Json转换JSONObject对象
 * JSONArray对象，这两种对象的转换只需要传入Json对应的字符串
 * 同时支持转换为Map对象和List<Map<String,String>对象，考虑到
 * Json数据格式太多，此处全部转换为String对象，需要什么数据
 * 类型的时候，请自己转换；在对象和Json字符串中互相转换也同时
 * 支持转换，在列表数据支持实现List接口的所有类。没有实现List
 * 接口的请不要用此类做转换。
 */

public class Json {


    /**
     * JSONObject字符对象装JSONObject
     *
     * @param jsonStr JSONObject字符
     * @return JSONObject对象类
     */
    public static JSONObject parseJSONObjectString(String jsonStr) {
        if (jsonStr == null || jsonStr.length() == 0 || jsonStr.equals("null")) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * JSONObject对象转Map对象
     *
     * @param jsonObject JSONObject对象
     * @return Map数据对象
     */
    public static Map<String, String> parseJSONObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = null;
            try {
                value = jsonObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            map.put(key, String.valueOf(value == null ? "" : value));
        }
        return map;
    }

    /**
     * Json字符串转Map对象
     *
     * @param jsonStr Json字符串
     * @return
     */
    public static Map<String, String> parseJSONObject(String jsonStr) {
        if (jsonStr == null || jsonStr.length() == 0 || jsonStr.equals("null")) {
            return null;
        }
        return parseJSONObject(parseJSONObjectString(jsonStr));
    }


    /**
     * JsonObject转换为对象
     *
     * @param cls        数据对象类
     * @param jsonObject Json对象数据
     * @param <T>        数据对象泛型
     * @return 数据对象
     */
    public static <T> T parseJSONObject(Class<T> cls, JSONObject jsonObject) {
        T bean = null;
        if (cls == null || jsonObject == null) {
            return bean;
        }
        try {
            Constructor<?>[] constructors = cls.getDeclaredConstructors();
            if (constructors.length == 0) {
                bean = cls.newInstance();
            } else {//解析内部类实例
                Constructor constructor = constructors[0];
                constructor.setAccessible(true);
                bean = (T) constructor.newInstance();
            }
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                //获取Json字段的值
                String key = iterator.next();
                if (isDeclaredField(cls, key)) {
                    Field field = null;
                    try {
                        field = cls.getDeclaredField(key);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    if (field != null) {
                        field.setAccessible(true);
                        Object value = jsonObject.get(key);
                        String valueStr = String.valueOf(value);
                        //设置字段的值
                        Class<?> fieldType = field.getType();
                        if (fieldType == String.class) {
                            field.set(bean, valueStr);
                        }
                        //字符类型
                        if (fieldType == Character.class) {
                            field.set(bean, valueStr);
                        }
                        //Int类型
                        if (fieldType == int.class) {
                            if (valueStr.contains(".")) {
                                throw new IllegalArgumentException("field " + key + " is Integer by Integer.parseInt() error,field " + key + " get value is double value.");
                            } else {
                                field.set(bean, Integer.parseInt(valueStr));
                            }
                        }
                        //Long类型
                        if (fieldType == long.class) {
                            if (valueStr.contains(".")) {
                                throw new IllegalArgumentException("field " + key + " is Integer by Long.parseLong error,field " + key + " get value is double value.");
                            } else {
                                field.set(bean, Long.parseLong(valueStr));
                            }
                        }
                        //Double类型
                        if (field.getType() == double.class) {
                            if (!valueStr.contains(".")) {
                                valueStr += ".00";
                            }
                            field.set(bean, Double.parseDouble(valueStr));
                        }
                        //Float类型
                        if (fieldType == float.class) {
                            if (!valueStr.contains(".")) {
                                valueStr += ".00";
                            }
                            field.set(bean, Float.parseFloat(valueStr));
                        }
                        //如果不是一般数据类型
                        if (!fieldType.isPrimitive()) {
                            Type type = field.getGenericType();
                            if (type instanceof ParameterizedType) {
                                //泛型转化为真实类型
                                ParameterizedType parameterizedType = (ParameterizedType) type;
                                Class genericClazz = (Class) parameterizedType.getActualTypeArguments()[0];
                                field.set(bean, parseJSONArray(field, genericClazz, valueStr));
                            } else {
                                //自定义的类
                                if (fieldType instanceof Class && fieldType != String.class && fieldType != Character.class) {
                                    field.set(bean, parseJSONObject(fieldType, parseJSONObjectString(valueStr)));
                                }
                            }
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 是否是声明的字段
     *
     * @param cls
     * @param fieldName
     * @return
     */
    public static boolean isDeclaredField(Class cls, String fieldName) {
        if (cls == null) {
            return false;
        }
        if (fieldName == null || fieldName.length() == 0) {
            return false;
        }
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            if (name == null) {
                return false;
            }
            if (fieldName.equals(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Json字符串转换成对应数据对象
     *
     * @param cls     类文件夹
     * @param jsonStr Json字符串
     * @param <T>     转换目标对象
     * @return 数据对象
     */
    public static <T> T parseJSONObject(Class<T> cls, String jsonStr) {
        if (jsonStr == null || jsonStr.length() == 0 || jsonStr.equals("null")) {
            return null;
        }
        return parseJSONObject(cls, parseJSONObjectString(jsonStr));
    }

    /**
     * Json字符串转换成JSONArray对象
     *
     * @param jsonStr Json字符串转
     * @return JSONArray对象
     */
    public static JSONArray parseJSONArrayString(String jsonStr) {
        if (jsonStr == null || jsonStr.length() == 0 || jsonStr.equals("null")) {
            return null;
        }
        if (jsonStr.contains("\"[")) {
            jsonStr = jsonStr.replace("\"[", "[");
        }
        if (jsonStr.contains("]\"")) {
            jsonStr = jsonStr.replace("]\"", "]");
        }
        if (jsonStr.contains("\"{")) {
            jsonStr = jsonStr.replace("\"{", "}");
        }
        if (jsonStr.contains("}\"")) {
            jsonStr = jsonStr.replace("}\"", "}");
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * JSONArray对象转MapList数据
     *
     * @param jsonArray JSONArray对象
     * @return Map对象的列表数据
     */
    public static List<Map<String, String>> parseJSONArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String json = jsonArray.getString(i);
                if (json != null && json.length() != 0 && !json.equals("null")) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    list.add(parseJSONObject(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * Json字符串转MapList
     *
     * @param jsonStr Json字符串
     * @return Map对象的列表数据
     */
    public static List<Map<String, String>> parseJSONArray(String jsonStr) {
        if (jsonStr == null || jsonStr.length() == 0 || jsonStr.equals("null")) {
            return null;
        }
        if (jsonStr.equals("[]")){
            return new ArrayList<>();
        }
        return parseJSONArray(parseJSONArrayString(jsonStr));
    }

    /**
     * JsonArray转换为List对象
     *
     * @param field          列表字段
     * @param fieldParamsCls 列表字段的参数对象
     * @param jsonStr        json字符串
     * @param <T>            泛型对象
     * @return
     */
    public static <T> List<T> parseJSONArray(Field field, Class<T> fieldParamsCls, String jsonStr) {
        List<T> list = null;
        try {
            //List不能直接实例化
            if (field.getType() == List.class) {
                list = new ArrayList<>();
            } else {
                list = (List<T>) field.getType().newInstance();
            }
            if (jsonStr != null && jsonStr.length() != 0 && !jsonStr.equals("[]") && !jsonStr.equals("null")) {
                JSONArray jsonArray = parseJSONArrayString(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    T t = parseJSONObject(fieldParamsCls, jsonObject);
                    list.add(t);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Json转List对象，此处只要是是实现了List接口的对象都可以
     *
     * @param cls       列表数据中的对象
     * @param jsonArray json数据
     * @param <T>       泛型对象
     * @return 列表数据
     */
    public static <T> List<T> parseJSONArray(Class<T> cls, JSONArray jsonArray) {
        List<T> list = new ArrayList<>();
        if (jsonArray == null) {
            return list;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                list.add(parseJSONObject(cls, jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * JSONArray对象字符串转换为对象
     *
     * @param cls     对象类
     * @param jsonStr json字符串
     * @param <T>     列表中数据对象
     * @return 列表数据
     */
    public static <T> List<T> parseJSONArray(Class<T> cls, String jsonStr) {
        if (jsonStr == null || jsonStr.equals("null")) {
            return null;
        }
        if (jsonStr.equals("[]")){
            return new ArrayList<>();
        }
        return parseJSONArray(cls, parseJSONArrayString(jsonStr));
    }

    /**
     * Map对象转Json字符串
     *
     * @param map
     * @return
     */
    public static String parseMap(Map<String, String> map) {
        if (map == null) {
            return "{}";
        }
        StringBuffer sb = new StringBuffer("{");
        for (String key : map.keySet()) {
            String value = map.get(key);
            value = value == null ? "" : value;
            sb.append("\"" + key + "\":");
            if (value.contains("[") && value.contains("]")) {
                sb.append(value);
            } else {
                sb.append("\"" + value + "\"");
            }
            sb.append(",");
        }
        if (sb.toString().contains(",")) {
            int lastIndex = sb.lastIndexOf(",");
            sb.replace(lastIndex, lastIndex + 1, "");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * MapList转Json字符串
     *
     * @param mapList
     * @return Json字符串
     */
    public static String parseMapList(List<Map<String, String>> mapList) {
        if (mapList == null) {
            return "[]";
        }
        if (mapList.size() == 0) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer("[");
        for (Map<String, String> map : mapList) {
            sb.append(parseMap(map));
            sb.append(",");
        }
        if (sb.toString().contains(",")) {
            int lastIndex = sb.lastIndexOf(",");
            sb.replace(lastIndex, lastIndex + 1, "");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Object对象转Json字符串
     *
     * @param obj 数据对象
     * @return Json字符串
     */
    public static String parseObject(Object obj) {
        if (obj == null) {
            return "{}";
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return "{}";
        }
        StringBuffer sb = new StringBuffer("{");
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            if (name.equals("$change") || name.equals("serialVersionUID")) {
                continue;
            }
            Class<?> fieldType = field.getType();
            String value;
            //一般数据类型、字符类型
            if (fieldType.isPrimitive() || fieldType == String.class) {
                sb.append("\"" + name + "\":");
                try {
                    value = String.valueOf(field.get(obj));
                    sb.append("\"" + value + "\"");
                    sb.append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {//内部列表数据
                Type type = field.getGenericType();
                if (type instanceof ParameterizedType && fieldType == List.class) {
                    sb.append("\"" + name + "\":");
                    List<?> list = null;
                    try {
                        list = (List<?>) field.get(obj);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    sb.append("[");
                    int size = list == null ? 0 : list.size();
                    for (int i = 0; i < size; i++) {
                        value = parseObject(list.get(i));
                        sb.append(value);
                        if (i != size - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("]");
                    sb.append(",");
                }
            }
            //内部类
            if (fieldType instanceof Class && !fieldType.isPrimitive() && fieldType != String.class && fieldType != Character.class && fieldType != List.class) {
                try {
                    sb.append("\"" + name + "\":");
                    String methodName = "get" + (name.length() > 0 ? name.substring(0, 1).toUpperCase() : "") + (name.length() > 1 ? name.substring(1).toLowerCase() : "");
                    Object innerObj = obj.getClass().getMethod(methodName).invoke(obj);
                    sb.append(parseObject(innerObj));
                    sb.append(",");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        sb.append("}");
        if (sb.length() > 1 && sb.charAt(sb.length() - 2) == ',' && sb.charAt(sb.length() - 1) == '}') {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }


}
