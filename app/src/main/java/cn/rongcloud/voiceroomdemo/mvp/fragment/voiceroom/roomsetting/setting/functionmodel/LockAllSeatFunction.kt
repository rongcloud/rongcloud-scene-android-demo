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
class LockAllSeatFunction(val roomModel: VoiceRoomModel, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel() {
    override fun onCreate() {
        addDisposable(roomModel
            .obRoomInfoChange()
            .subscribe { roomInfo ->
                if (roomInfo.isLockAll) {
                    onDataChange(R.drawable.ic_room_setting_unlock_all, "解锁全座") {
                        addDisposable(roomModel.setAllSeatLock(false).subscribe({
                            view.showMessage("设置成功")
                        }, {
                            view.showError("设置失败")
                        }))
                    }
                } else {
                    onDataChange(R.drawable.ic_room_setting_lock_all, "全麦锁座") {
                        addDisposable(roomModel.setAllSeatLock(true).subscribe({
                            view.showMessage("设置成功")
                        }, {
                            view.showError("设置失败")
                        }))
                    }
                }
            })
    }
}