/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.IRoomSettingView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/22
 */
class LockRoomFunction(roomId: String, val view: IRoomSettingView) :
    BaseRoomSettingFunctionModel(roomId) {


    override fun onCreate() {
        addDisposable(roomModel
            .obRoomInfoChange()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.roomBean?.isPrivate == 1) {
                    onDataChange(R.drawable.ic_room_setting_unlock, "房间解锁") {
                        addDisposable(roomModel
                            .setRoomLock(false, null)
                            .subscribe { result ->
                                if (result) {
                                    view.showMessage("取消成功")
                                } else {
                                    view.showError("取消失败")
                                }
                            })
                    }
                } else {
                    onDataChange(R.drawable.ic_room_setting_lock, "房间上锁") {
                        view.showPasswordDialog()
                            ?.subscribe { password ->
                                addDisposable(
                                    roomModel
                                        .setRoomLock(true, password)
                                        .subscribe { result ->
                                            if (result) {
                                                view.showMessage("设置成功")
                                            } else {
                                                view.showError("设置失败")
                                            }
                                        })
                            }?.apply {
                                addDisposable(this)
                            }
                    }
                }
            })
    }
}