/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.IRoomSettingView
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/24
 */

class MuteAllSeatFunction(val roomModel: VoiceRoomModel, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel() {

    override fun onCreate() {
        addDisposable(roomModel.obRoomInfoChange().subscribe { roomInfo ->
            if (roomInfo.isMuteAll) {
                onDataChange(R.drawable.ic_room_setting_unmute_all, "解锁全麦") {
                    addDisposable(
                        roomModel.setAllSeatMute(false).observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                view.showMessage("已解锁全麦")
                            }, {
                                view.showError("设置失败")
                            })
                    )
                }
            } else {
                onDataChange(R.drawable.ic_room_setting_mute_all, "全麦锁麦") {
                    addDisposable(
                        roomModel.setAllSeatMute(true).observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                view.showMessage("全部麦位已静音")
                            }, {
                                view.showError("设置失败")
                            })
                    )
                }
            }
        })
    }
}