/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import com.rongcloud.common.ModuleManager
import com.rongcloud.common.extension.getValueSync
import com.rongcloud.common.extension.myStringPreferencesKey
import com.rongcloud.common.extension.putValue
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
                ModuleManager.applicationContext.getValueSync(KEY_BACKGROUND),
                List::class.java
            ) as? List<String> ?: emptyList()).apply {
                backgroundCacheList.clear()
                backgroundCacheList.addAll(this)
            }
        }
    }

    fun saveBackGroundUrl(list: List<String>) {
        ModuleManager.applicationContext.putValue(KEY_BACKGROUND, JsonUtils.toJson(list).apply {
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