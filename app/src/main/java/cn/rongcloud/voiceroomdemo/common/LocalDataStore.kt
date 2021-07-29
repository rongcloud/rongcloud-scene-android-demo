/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.common

import cn.rongcloud.voiceroom.utils.JsonUtils
import cn.rongcloud.voiceroomdemo.MyApp
import kotlin.collections.ArrayList

/**
 * @author gusd
 * @Date 2021/06/15
 */

private const val TAG = "DataLocalStore"

object LocalDataStore {

    private val KEY_BACKGROUND by myStringPreferencesKey("")

    private val backgroundCacheList = ArrayList<String>()

    fun getBackGroundUrlList(): List<String> {
        return if (!backgroundCacheList.isNullOrEmpty()) {
            backgroundCacheList
        } else {
            (JsonUtils.fromJson(
                MyApp.context.getValueSync(KEY_BACKGROUND),
                List::class.java
            ) as? List<String> ?: emptyList()).apply {
                backgroundCacheList.clear()
                backgroundCacheList.addAll(this)
            }
        }
    }

    fun saveBackGroundUrl(list: List<String>) {
        MyApp.context.putValue(KEY_BACKGROUND, JsonUtils.toJson(list).apply {
            backgroundCacheList.clear()
            backgroundCacheList.addAll(list)
        })
    }

    fun getBackgroundByIndex(index: Int): String? {
        val backGroundUrlList = getBackGroundUrlList()
        return if (index < 0 || index >= backGroundUrlList.size) {
            null
        } else {
            backGroundUrlList[index]
        }
    }
}