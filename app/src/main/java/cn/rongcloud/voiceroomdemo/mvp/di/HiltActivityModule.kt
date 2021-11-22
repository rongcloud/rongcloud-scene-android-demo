/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * @author gusd
 * @Date 2021/07/20
 */
@Module
@InstallIn(ActivityComponent::class)
public class HiltActivityModule {

//    @Named("roomId")
//    @Provides
//    fun provideVoiceRoomId(activity: Activity): String {
//        if (activity is VoiceRoomActivity) {
//            return activity.getRoomId()
//        }
//        return ""
//    }

//    @Named("isCreate")
//    @Provides
//    fun provideVoiceRoomIsCreate(activity: Activity): Boolean {
//        if (activity is VoiceRoomActivity) {
//            return activity.isCreate()
//        }
//        return false
//    }
}