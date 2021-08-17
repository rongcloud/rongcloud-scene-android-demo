/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dagger

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.rongcloud.common.ModuleManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * @author gusd
 * @Date 2021/07/28
 */
@Module
@InstallIn(ActivityComponent::class)
class AppCompatActivityModule {
    @Provides
    fun provideAppCompatActivity(activity: Activity): AppCompatActivity {
        if (activity is AppCompatActivity) {
            return activity
        }
        throw Throwable("inject only support AppCompatActivity")
    }
}