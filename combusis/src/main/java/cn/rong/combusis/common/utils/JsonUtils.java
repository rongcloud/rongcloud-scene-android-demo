/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.utils;

import com.google.gson.Gson;

/**
 * @author gusd
 * @Date 2021/06/04
 */
public class JsonUtils {
    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}
