/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.init

import android.app.Application
import android.content.Context

/**
 * 自动化无侵入式的初始化注解，使用方式参考 CommonInit，单个模块支持包含多个初始化类
 * @author gusd
 * @Date 2021/07/28
 */
interface ModuleInit {
    /**
     * 初始化优先级，当小于 0 时不初始化该模块
     */
    fun getPriority(): Int

    /**
     * 模块名称
     */
    fun getName(context: Context): String

    /**
     * 初始化回调
     */
    fun onInit(application: Application)
}