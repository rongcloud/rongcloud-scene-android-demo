/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common

import android.app.Application
import android.content.Context
import android.util.Log
import cn.rongcloud.annotation.AutoInit
import com.rongcloud.common.dao.database.DatabaseManager
import com.rongcloud.common.init.ModuleInit
import com.rongcloud.common.utils.AudioManagerUtil
import com.rongcloud.common.utils.CrashCollectHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/07/29
 */
private const val TAG = "CommonInit"

@AutoInit
class CommonInit @Inject constructor() : ModuleInit {

    /**
     * 可通过该种方式获取 ApplicationContext
     */
    @Inject
    @ApplicationContext
    lateinit var context: Context

    /**
     * 异常捕获
     */
    @Inject
    lateinit var crashCollectHandler: CrashCollectHandler

    override fun getPriority(): Int {
        // CommonInit 模块具有最高优先初始化权利
        return 0
    }

    override fun getName(context: Context): String {
        return TAG
    }

    override fun onInit(application: Application) {
        Log.d(TAG, "onInit: ")

        // rxjava 的全局 onError 回调
        RxJavaPlugins.setErrorHandler {
            Log.e(TAG, "RxJavaOnError: ", it)
        }

        // 初始化数据库
        DatabaseManager.init(application)

        // 耳机焦点的自动化管理
        AudioManagerUtil.init(application)
    }
}