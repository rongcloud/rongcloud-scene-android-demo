/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.di

import android.app.Activity
import androidx.fragment.app.Fragment
import cn.rongcloud.voiceroomdemo.mvp.fragment.present.SendPresentFragment
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.emptyseatsetting.IEmptySeatView
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.membersetting.IMemberSettingView
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.selfsetting.ISelfSettingView
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
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

    @Named("EmptySeatSetting")
    @Provides
    fun provideEmptySeatSettingBean(view: IEmptySeatView): UiSeatModel {
        return view.getEmptyUiSeatModel()!!
    }

    @Named("SelfSeatSetting")
    @Provides
    fun provideSelfSeatSettingBean(view: ISelfSettingView): UiSeatModel {
        return view.getUiSeatModel()!!
    }


    @Provides
    fun provideRoomInfoBean(roomModel: VoiceRoomModel): VoiceRoomBean {
        return roomModel.currentUIRoomInfo.roomBean!!
    }

    @Provides
    fun provideMemberModel(view: IMemberSettingView): UiMemberModel {
        return view.getMemberInfo()!!
    }

    @Provides
    @Named("selectedIds")
    fun provideSelectedIds(activity: Activity, fragment: Fragment): List<String> {
        if (fragment is SendPresentFragment) {
            fragment.getSelectedIds()
        }
        return emptyList()
    }
}