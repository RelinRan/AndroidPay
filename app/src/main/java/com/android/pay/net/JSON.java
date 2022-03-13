package com.android.pay.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Json工具
 */
public class JSON {

    /**
     * @param json 字符
     * @return 是否为空
     */
    public static boolean isNone(String json) {
        return json == null || json.length() == 0 || json.equals("null");
    }

    /**
     * @param json 字符
     * @return JSONObject对象
     */
    public static JSONObject toJSONObject(String json) {
        if (isNone(json)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @param object 对象
     * @return Map数据对象
     */
    public static Map<String, Object> toMap(JSONObject object) {
        if (object == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = object.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object value = object.get(key);
                map.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * @param json 字符串
     * @return 字符转Map对象
     */
    public static Map<String, Object> toMap(String json) {
        if (isNone(json)) {
            return null;
        }
        if (json.startsWith("{}")) {
            return null;
        }
        return toMap(toJSONObject(json));
    }

    /**
     * @param clazz     类
     * @param fieldName 字段名称
     * @return 是否是声明的字段
     */
    public static boolean isDeclaredField(Class clazz, String fieldName) {
        if (clazz == null) {
            return false;
        }
        if (fieldName == null || fieldName.length() == 0) {
            return false;
        }
        Field[] fields = findClassDeclaredFields(clazz);
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
     * @param componentType 组件类型
     * @param jsonArray     json数组
     * @return 数组实例
     */
    public static Object newArrayInstance(Class componentType, JSONArray jsonArray) {
        Object arrayObj = Array.newInstance(componentType, jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            Object obj = null;
            try {
                obj = jsonArray.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (componentType == String.class) {
                String[] array = (String[]) arrayObj;
                array[i] = (String) obj;
            }
            if (componentType == Character.class) {
                Character[] array = (Character[]) arrayObj;
                array[i] = (Character) obj;
            }
            if (componentType == CharSequence.class) {
                CharSequence[] array = (CharSequence[]) arrayObj;
                array[i] = (CharSequence) obj;
            }
            if (componentType == int.class) {
                int[] array = (int[]) arrayObj;
                array[i] = (int) obj;
            }
            if (componentType == long.class) {
                long[] array = (long[]) arrayObj;
                array[i] = (long) obj;
            }
            if (componentType == double.class) {
                double[] array = (double[]) arrayObj;
                array[i] = (double) obj;
            }
            if (componentType == float.class) {
                float[] array = (float[]) arrayObj;
                array[i] = (float) obj;
            }
            if (componentType == short.class) {
                short[] array = (short[]) arrayObj;
                array[i] = (short) obj;
            }
            if (componentType == boolean.class) {
                boolean[] array = (boolean[]) arrayObj;
                array[i] = (boolean) obj;
            }
        }
        return arrayObj;
    }

    /**
     * 设置字段值
     *
     * @param field 字段
     * @param bean  对象
     * @param value 值
     */
    public static void setFieldValue(Field field, Object bean, String value) {
        try {
            Class fieldType = field.getType();
            //字符
            if (fieldType == String.class || fieldType == Character.class || fieldType == CharSequence.class) {
                field.set(bean, value);
            }
            //Int类型
            if (fieldType == int.class || fieldType == Integer.class) {
                if (!value.contains(".")) {
                    value = value.length() == 0 ? "0" : value;
                    field.set(bean, Integer.parseInt(value));
                }
            }
            //Short类型
            if (fieldType == short.class || fieldType == Short.class) {
                if (!value.contains(".")) {
                    value = value.length() == 0 ? "0" : value;
                    field.set(bean, Short.parseShort(value));
                }
            }
            //Long类型
            if (fieldType == long.class || fieldType == Long.class) {
                if (!value.contains(".")) {
                    value = value.length() == 0 ? "0" : value;
                    field.set(bean, Long.parseLong(value));
                }
            }
            //Double类型
            if (field.getType() == double.class || fieldType == Double.class) {
                value = value.length() == 0 ? "0.00" : value;
                value = value.contains(".") ? value : value + ".00";
                field.set(bean, Double.parseDouble(value));
            }
            //Float类型
            if (fieldType == float.class || fieldType == Float.class) {
                value = value.length() == 0 ? "0.00" : value;
                value = value.contains(".") ? value : value + ".00";
                field.set(bean, Float.parseFloat(value));
            }
            //Boolean类型
            if (fieldType == boolean.class || fieldType == Boolean.class) {
                value = value.length() == 0 ? "false" : value;
                boolean booleanValue = false;
                if (value.equals("false") || value.equals("0")) {
                    booleanValue = false;
                }
                if (value.equals("true") || value.equals("1")) {
                    booleanValue = true;
                }
                field.set(bean, booleanValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param clazz 类
     * @param name  字段名称
     * @return 本类及其父类寻找是否有此类
     */
    public static Field findClassField(Class clazz, String name) {
        for (Field field : findClassDeclaredFields(clazz)) {
            field.setAccessible(true);
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }


    /**
     * @param object 对象
     * @param clazz  数据对象类
     * @return JsonObject转对象
     */
    public static <T> T toObject(JSONObject object, Class<T> clazz) {
        T bean = null;
        if (clazz == null || object == null) {
            return null;
        }
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                bean = clazz.newInstance();
            } else {
                Constructor constructor = constructors[0];
                constructor.setAccessible(true);
                bean = (T) constructor.newInstance();
            }
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (isDeclaredField(clazz, key)) {
                    setObjectValue(clazz, bean, object, key);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 设置对象值
     *
     * @param clazz     对象类
     * @param clazzBean 对象实例化
     * @param object    json对象
     * @param fieldName 字段名称
     * @param <T>       实体
     */
    public static <T> void setObjectValue(Class<T> clazz, T clazzBean, JSONObject object, String fieldName) {
        try {
            Field field = findClassField(clazz, fieldName);
            if (field != null) {
                field.setAccessible(true);
                Object value = object.get(fieldName);
                String valueString = String.valueOf(value);
                valueString = isNone(valueString) ? "" : valueString;
                Class fieldType = field.getType();
                if (isPrimitive(fieldType)) {
                    //Primitive
                    setFieldValue(field, clazzBean, valueString);
                } else {
                    //Collection
                    if (Collection.class.isAssignableFrom(fieldType)) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            Class argumentsClazz = (Class) parameterizedType.getActualTypeArguments()[0];
                            if (isPrimitive(argumentsClazz)) {
                                field.set(clazzBean, toCollection(valueString, argumentsClazz));
                            } else {
                                field.set(clazzBean, toCollection(field, argumentsClazz, valueString));
                            }
                        }
                    } else if (fieldType.isArray()) {
                        //Array
                        JSONArray jsonArray = (JSONArray) value;
                        Class componentType = fieldType.getComponentType();
                        field.set(clazzBean, newArrayInstance(componentType, jsonArray));
                    } else if (Map.class.isAssignableFrom(fieldType)) {
                        //Map
                        JSONObject jsonObject = (JSONObject) value;
                        Map<String, Object> map = new HashMap<>();
                        Iterator it = jsonObject.keys();
                        while (it.hasNext()) {
                            String name = (String) it.next();
                            Object val = jsonObject.get(name);
                            map.put(name, val);
                        }
                        field.set(clazzBean, map);
                    } else if (JSONArray.class.isAssignableFrom(fieldType)){
                        //JSONArray
                        field.set(clazzBean, toCollection(valueString, fieldType));
                    }  else if (JSONObject.class.isAssignableFrom(fieldType)){
                        //JSONObject
                        field.set(clazzBean, toObject(valueString, fieldType));
                    }else if (Object.class.isAssignableFrom(fieldType)){
                        //Object
                        field.set(clazzBean,toObject(valueString, fieldType));
                    } else {
                        field.set(clazzBean, toObject(valueString, fieldType));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param json  字符串
     * @param clazz 类
     * @return 数据对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (isNone(json)) {
            return null;
        }
        return toObject(toJSONObject(json), clazz);
    }

    /**
     * @param json Json字符串转
     * @return JSONArray对象
     */
    public static JSONArray toJSONArray(String json) {
        if (json == null || json.length() == 0 || json.equals("null")) {
            return null;
        }
        if (!json.startsWith("[{") && !json.endsWith("}]")) {
            return null;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * @param jsonArray JSONArray对象
     * @return Map对象的列表数据
     */
    public static List<Map<String, Object>> toMapCollection(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String json = jsonArray.getString(i);
                if (json != null && json.length() != 0 && !json.equals("null")) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    list.add(toMap(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * @param json Json字符串
     * @return Map对象的列表数据
     */
    public static List<Map<String, Object>> toMapCollection(String json) {
        if (isNone(json)) {
            return null;
        }
        if (json.equals("[]")) {
            return new ArrayList<>();
        }
        JSONArray array = toJSONArray(json);
        if (array == null) {
            return null;
        }
        return toMapCollection(array);
    }

    /**
     * @param field 列表字段
     * @param clazz 列表字段的参数对象
     * @param json  json字符串
     * @return JsonArray转List对象
     */
    public static <T> List<T> toCollection(Field field, Class<T> clazz, String json) {
        List<T> list = null;
        try {
            if (field.getType() == List.class) {
                list = new ArrayList<>();
            } else {
                list = (List<T>) field.getType().newInstance();
            }
            if (isJSONArray(json)) {
                JSONArray jsonArray = toJSONArray(json);
                int size = jsonArray == null ? 0 : jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    T t = toObject(jsonObject, clazz);
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
     * @param array 数组
     * @param clazz 对象类
     * @return 列表数据
     */
    public static <T> List<T> toCollection(JSONArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) array.get(i);
                list.add(toObject(jsonObject, clazz));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * JSONArray对象字符串转换为对象
     *
     * @param json  字符
     * @param clazz 对象类
     * @return 列表数据
     */
    public static <T> List<T> toCollection(String json, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                Object obj = jsonArray.get(i);
                if (isPrimitive(obj.getClass())) {
                    list.add((T) obj);
                }
                if (obj.getClass().isAssignableFrom(JSONObject.class)) {
                    list.add(toObject((JSONObject) obj, clazz));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param type 类型
     * @return 是否基础变量
     */
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive()
                || type.isAssignableFrom(String.class)
                || type.isAssignableFrom(Boolean.class)
                || type.isAssignableFrom(Character.class)
                || type.isAssignableFrom(Byte.class)
                || type.isAssignableFrom(Short.class)
                || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(Float.class)
                || type.isAssignableFrom(Double.class)
                || type.isAssignableFrom(Void.class);
    }

    /**
     * @param field 字段
     * @return 是否预定义字段
     */
    public static boolean isPredefined(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        String name = field.getName();
        return name.equals("$change")
                || name.equals("serialVersionUID")
                || name.equals("NULL")
                || name.equals("NEGATIVE_ZERO");
    }

    /**
     * @param json 字符
     * @return 是否集合|数组
     */
    public static boolean isJSONArray(String json) {
        return json != null && json.startsWith("[") && json.endsWith("]");
    }

    /**
     * @param json 字符
     * @return 是否对象
     */
    public static boolean isJSONObject(String json) {
        return json != null && json.startsWith("{") && json.endsWith("}");
    }

    /**
     * 添加到JSONObject
     *
     * @param jsonObject json对象
     * @param key        键
     * @param value      值
     */
    public static void addJSONObjectKeyValue(JSONObject jsonObject, String key, Object value) {
        try {
            if (value != null) {
                if (isPrimitive(value.getClass())) {
                    jsonObject.put(key, value);
                } else {
                    String objValueJson = toJson(value);
                    jsonObject.put(key, isJSONObject(objValueJson) ? new JSONObject(objValueJson) : new JSONArray(objValueJson));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param clazz 类
     * @return 当前类及其父类类声明字段
     */
    public static Field[] findClassDeclaredFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * @param obj 对象
     * @return json字符
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            //Collection
            JSONArray jsonArray = new JSONArray();
            List<?> list = (List<?>) obj;
            int count = list == null ? 0 : list.size();
            //基本类型集合
            List<Object> primitiveArray = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Object item = list.get(i);
                try {
                    if (isPrimitive(item.getClass())) {
                        primitiveArray.add(item);
                    } else {
                        jsonArray.put(new JSONObject(toJson(item)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (primitiveArray.size() > 0) {
                jsonArray = new JSONArray(primitiveArray);
            }
            return jsonArray.toString();
        } else if (Map.class.isAssignableFrom(obj.getClass())) {
            //Map
            JSONObject jsonObject = new JSONObject();
            Map<String, Object> objMap = (Map<String, Object>) obj;
            for (String key : objMap.keySet()) {
                Object objValue = objMap.get(key);
                addJSONObjectKeyValue(jsonObject, key, objValue);
            }
            return jsonObject.toString();
        } else if (obj.getClass().isArray()) {
            //Array
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < Array.getLength(obj); i++) {
                jsonArray.put(Array.get(obj, i));
            }
            return jsonArray.toString();
        } else {
            //普通类
            JSONObject jsonObject = new JSONObject();
            Field[] fields = findClassDeclaredFields(obj.getClass());
            if (fields.length == 0) {
                return jsonObject.toString();
            }
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                String name = field.getName();
                if (isPredefined(field)) {
                    continue;
                }
                try {
                    //普通类型
                    Object value = field.get(obj);
                    if (isPrimitive(type)) {
                        jsonObject.put(name, value);
                    } else {
                        addJSONObjectKeyValue(jsonObject, name, value);
                    }
                } catch (IllegalAccessException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject.toString();
        }
    }

    /**
     * JSON格式化
     *
     * @param json 字符
     * @return
     */
    public static String format(String json) {
        if (isNone(json)) {
            return "";
        }
        if (isJSONObject(json)) {
            try {
                JSONObject object = new JSONObject(json);
                json = object.toString(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isJSONArray(json)) {
            try {
                JSONArray array = new JSONArray(json);
                json = array.toString(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

}
