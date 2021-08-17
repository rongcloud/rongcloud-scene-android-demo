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
 * @Date 2021/06/23
 */
class RoomNameFunction(val roomModel: VoiceRoomModel, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel() {

    override fun onCreate() {
        onDataChange(R.drawable.ic_room_setting_name, "房间标题") {
            view.showModifyRoomNameDialog(roomModel.currentUIRoomInfo.roomBean?.roomName)
                ?.subscribe { newName ->
                    addDisposable(roomModel
                        .setRoomName(newName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            if (result) {
                                view.showMessage("修改成功")
                            } else {
                                view.showError("修改失败")
                            }
                        })
                }
        }
    }

}