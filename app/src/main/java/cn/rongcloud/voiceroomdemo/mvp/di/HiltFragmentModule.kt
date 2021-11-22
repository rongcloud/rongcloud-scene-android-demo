/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.di

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Named

/**
 * @author gusd
 * @Date 2021/07/20
 */
@Module
@InstallIn(FragmentComponent::class)
class HiltFragmentModule {

    @Named("isCreate")
    @Provides
    fun provideVoiceRoomIsCreate(fragment: Fragment): Boolean {
        return false
    }

    @Named("roomId")
    @Provides
    fun provideVoiceRoomId(fragment: Fragment): String {
        return ""
    }
}