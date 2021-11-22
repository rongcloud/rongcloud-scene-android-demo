/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dagger

import android.app.Application
import android.content.Context
import com.rongcloud.common.InitService
import com.rongcloud.common.ModuleManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * @author gusd
 * @Date 2021/07/30
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideModuleManager(
        @ApplicationContext context: Context,
        initService: InitService
    ): ModuleManager {
        return ModuleManager.apply {
            this.bindInitService(context as Application, initService)
        }
    }
}