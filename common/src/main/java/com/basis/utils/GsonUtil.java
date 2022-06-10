package com.basis.utils;

import static com.basis.utils.Logger.e;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author: BaiCQ
 * @ClassName: GsonUtil
 * @Description: Gson解析相关工具类
 */
public class GsonUtil {
    public final static String TAG = "GsonUtil";
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    private final static Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();

    /**
     * 如果是jsonArr 比json2List效率高
     *
     * @param json
     * @param typeToken 如：new TypeToken<List<T>>() {}
     * @param <T>
     * @return T
     */
    public static <T> T json2Obj(String json, TypeToken<T> typeToken) {
        if (null == typeToken) {
            e(TAG, "typeToken can not null!");
            return null;
        }
        return gson.fromJson(json, typeToken.getType());
    }

    /**
     * Json解析成Obj实体
     *
     * @param json  待解析的json串
     * @param clazz 对应T的class类型
     * @param <T>   实体类型 此处不能是List
     * @return 实体Bean
     */
    public static <T> T json2Obj(String json, Class<T> clazz) {
        if (null == clazz) {
            e(TAG, "the clazz can not null!");
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    /**
     * 功能需要将说有json or JsonArr 解析成List<T>
     * 但是由于
     * gson.fromJson(json, new TypeToken<List<T>>() {}.getType()); 此处使用泛型 导致解析获取T类型失败。
     * 故有次手动解析实现 效率比 json2Obj(String json, TypeToken typeToken) 要低
     *
     * @param json  待解析的json串
     * @param clazz 字节码文件
     * @param <T>
     * @throws Exception
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        if (null == clazz) {
            e(TAG, "the clazz can not null!");
            return null;
        }
        List<T> lst = new ArrayList<T>();
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if (jsonElement instanceof JsonArray) {//兼融list
                JsonArray array = jsonElement.getAsJsonArray();
                for (JsonElement elem : array) {
                    lst.add(gson.fromJson(elem, clazz));
                }
            } else if (jsonElement instanceof JsonObject) {//obj
                lst.add(gson.fromJson(json, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lst;
    }

    public static String obj2Json(Object object) {
        if (null == object) {
            return "";
        }
        return gson.toJson(object);
    }

    public static <T> HashMap<String, T> json2Map(String json, Type type) {
        if (null == type) {
            e(TAG, "the typeToken can not null!");
            return null;
        }
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
