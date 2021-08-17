/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.IRoomSettingView
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel

/**
 * @author gusd
 * @Date 2021/06/24
 */
class MuteAllRemoteFunction(val roomModel: VoiceRoomModel, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel() {
    override fun onCreate() {
        addDisposable(roomModel.obRoomInfoChange().subscribe { roomInfo ->
            if (roomInfo.isMute) {
                onDataChange(R.drawable.ic_room_setting_unmute, "取消静音") {
                    addDisposable(roomModel.muteAllRemoteStreams(false).subscribe({
                        view.showMessage("设置成功")
                    }, {
                        view.showError("设置失败")
                    }))
                }
            } else {
                onDataChange(R.drawable.ic_room_setting_mute, "静音") {
                    addDisposable(roomModel.muteAllRemoteStreams(true).subscribe({
                        view.showMessage("设置成功")
                    }, {
                        view.showError("设置失败")
                    }))
                }
            }
        })
    }
}