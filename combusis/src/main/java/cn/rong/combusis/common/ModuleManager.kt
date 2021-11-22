/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.rongcloud.common.init.ModuleInit

/**
 * @author gusd
 * @Date 2021/07/28
 */

object ModuleManager {

    @SuppressLint("StaticFieldLeak")
    private lateinit var initService: InitService

    lateinit var applicationContext: Context

    val moduleList: ArrayList<ModuleInit> = arrayListOf()

    fun bindInitService(application: Application, initService: InitService) {
        this.initService = initService
        applicationContext = application

    }

    fun init(
        application: Application,
        onBeforeInit: ((name: String, priority: Int) -> Int)? = null,
        onModuleInitFinish: ((name: String, priority: Int) -> Unit)? = null,
        onAllInitFinish: (() -> Unit)? = null
    ) {
        initService.init(application, onBeforeInit, onModuleInitFinish, onAllInitFinish)
    }


    fun registerModuleInit(moduleInit: ModuleInit) {
        moduleList.add(moduleInit)
    }


}