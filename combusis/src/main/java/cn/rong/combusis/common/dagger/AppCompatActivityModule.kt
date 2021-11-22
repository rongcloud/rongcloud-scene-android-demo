/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dagger

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

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