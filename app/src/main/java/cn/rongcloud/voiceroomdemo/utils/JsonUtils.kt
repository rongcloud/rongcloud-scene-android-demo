/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.utils

import com.google.gson.Gson

/**
 * @author gusd
 * @Date 2021/06/08
 */
object JsonUtils {
    private val gson: Gson by lazy {
        Gson()
    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> fromJson(jsonString: String, clazz: Class<T>): T? {
        return gson.fromJson(jsonString, clazz)
    }
}