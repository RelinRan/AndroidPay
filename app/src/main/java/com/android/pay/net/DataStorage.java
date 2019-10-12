package com.android.pay.net;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ice on 2015/12/4.
 * DataStorage
 */
public class DataStorage {

    //test. MODE_APPEND: 追加方式存储
    //2. MODE_PRIVATE: 私有方式存储,其他应用无法访问
    //3. MODE_WORLD_READABLE: 表示当前文件可以被其他应用读取
    //4. MODE_WORLD_WRITEABLE: 表示当前文件可以被其他应用写入
    private final String SHARE_PREFERENCE_NAME = "_SP_DATA";
    public static final String ANDROID_KIT_USER_INFO = "ANDROID_KIT_USER_INFO";
    public static final String IS_USER_LOGIN = "IS_USER_LOGIN";

    /**
     * 上下文
     */
    private Context context;

    /**
     * 包名
     */
    private String PACKAGE_NAME;

    /**
     * 实例对象
     */
    public static DataStorage instance;

    /**
     * 数据存储构造函数
     *
     * @param context 上下文对象
     */
    private DataStorage(Context context) {
        this.context = context;
        if (context != null) {
            PACKAGE_NAME = context.getApplicationContext().getPackageName().replace(".", "_").toUpperCase();
        }
    }

    /**
     * 获取单例模式
     *
     * @param context 上下文对象
     * @return DataStorage
     */
    public static DataStorage with(Context context) {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取数据保存对象
     *
     * @return SharedPreferences
     */
    public SharedPreferences getSharedPreferences() {
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(PACKAGE_NAME + SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存字符串
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 保存int
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存long
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 保存boolean
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存float
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, float value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 保存 Set<String>
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, Set<String> value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    /**
     * 保存 Map<String, String>
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, Map<String, String> value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, JsonParser.parseMap(value));
        editor.commit();
    }

    /**
     * 保存 List<Map<String, String>>
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, List<Map<String, String>> value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, JsonParser.parseMapList(value));
        editor.commit();
    }

    /**
     * 获取字符串
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    /**
     * 获取int
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    /**
     * 获取long
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    /**
     * 获取boolean
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    /**
     * 获取Set
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public Set<String> getStringSet(String key, Set defValue) {
        return getSharedPreferences().getStringSet(key, defValue);
    }

    /**
     * 获取Map<String, String>
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public Map<String, String> getStringMap(String key, Map<String, String> defValue) {
        String mapValue = getSharedPreferences().getString(key, "{}");
        if (mapValue.equals("{}")) {
            return defValue;
        }
        return JsonParser.parseJSONObject(mapValue);
    }

    /**
     * 获取List<Map<String, String>>
     *
     * @param key      键
     * @param defValue 默认值
     * @return
     */
    public List<Map<String, String>> getStringMapList(String key, List<Map<String, String>> defValue) {
        String mapValue = getSharedPreferences().getString(key, "[]");
        if (mapValue.equals("[]")) {
            return defValue;
        }
        return JsonParser.parseJSONArray(mapValue);
    }

    /**
     * 存入对象数据
     *
     * @param obj 数据对象
     */
    public void put(Object obj) {
        if (obj == null) {
            return;
        }
        put(obj.getClass().getSimpleName(), JsonParser.parseObject(obj));
    }

    /**
     * 获取对象数据
     *
     * @param cls
     * @return
     */
    public <T> T getObject(Class<T> cls) {
        String json = getString(cls.getSimpleName(), "{}");
        if (json.equals("{}")) {
            return null;
        }
        return JsonParser.parseJSONObject(cls, json);
    }

}
