/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.IRoomSettingView
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel

/**
 * @author gusd
 * @Date 2021/07/05
 */
class MusicFunction(val roomModel: VoiceRoomModel, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel() {
    override fun onCreate() {
        onDataChange(R.drawable.ic_room_setting_music, "音乐") {
            view.showMusicSettingFragment()
        }
    }
}