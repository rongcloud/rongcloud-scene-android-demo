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
class EnterSeatModelFunction(roomId: String, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel(roomId) {
    override fun onCreate() {
        addDisposable(roomModel
            .obRoomInfoChange()
            .subscribe { info ->
                if (info.isFreeEnterSeat) {
                    onDataChange(R.drawable.ic_room_setting_free_enter_model, "申请上麦") {
                        addDisposable(
                            roomModel
                                .setSeatMode(false)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    if (result) {
                                        view.showMessage("设置成功")
                                    } else {
                                        view.showError("设置失败")
                                    }
                                }, {
                                    view.showError("设置失败")
                                })
                        )
                    }
                } else {
                    onDataChange(R.drawable.ic_room_setting_request_seat_model, "自由上麦") {
                        addDisposable(
                            roomModel
                                .setSeatMode(true)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    if (result) {
                                        view.showMessage("设置成功")
                                    } else {
                                        view.showError("设置失败")
                                    }
                                }, {
                                    view.showError("设置失败")
                                })
                        )
                    }
                }
            })
    }
}