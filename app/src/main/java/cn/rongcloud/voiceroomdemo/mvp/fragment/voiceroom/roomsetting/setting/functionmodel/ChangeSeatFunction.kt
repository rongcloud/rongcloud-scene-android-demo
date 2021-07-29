/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.IRoomSettingView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/24
 */
class ChangeSeatFunction(roomId: String, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel(roomId) {
    override fun onCreate() {
        roomModel
            .obSeatListChange()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                if (list.size == 9) {
                    onDataChange(R.drawable.ic_room_setting_4_seat,"设置 4 个\n座位"){
                        roomModel.setSeatCount(5)
                        view.hideSettingView()
                    }
                } else {
                    onDataChange(R.drawable.ic_room_setting_8_seat,"设置 8 个\n座位"){
                        roomModel.setSeatCount(9)
                        view.hideSettingView()
                    }
                }
            }
    }
}