/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.selfsetting

import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import com.rongcloud.common.utils.AccountStore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Named

/**
 * @author gusd
 * @Date 2021/06/28
 */
class SelfSettingPresenter @Inject constructor(
    val view: ISelfSettingView,
    @Named("SelfSeatSetting") var seatInfo: UiSeatModel,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    private var isLeaveSeating = false

    override fun onCreate() {
        super.onCreate()
        addDisposable(roomModel
            .obSeatInfoByIndex(seatInfo.index)
            .subscribe {
                if (seatInfo.userId != AccountStore.getUserId()) {
                    if (isLeaveSeating) {
                        view.showMessage("您已断开连接")
                    }
                    view.fragmentDismiss()
                } else {
                    seatInfo = it
                    view.refreshView(it)
                }
            })

        addDisposable(roomModel
            .obRecordingStatusChange()
            .subscribe {
                view.onRecordStatusChange(it)
            })
    }

    fun muteSelf() {
        if (seatInfo.userId != null) {
            if (seatInfo.isMute) {
                view.showMessage("此座位已被管理员禁麦")
                return
            }
            addDisposable(
                roomModel
                    .setRecordingEnable(!roomModel.recordingStatus)
                    .subscribe({
                        view.showMessage("修改成功")
                    }, {
                        view.showError(it.message)
                    })
            )
        } else {
            view.showError("您已不在该麦位上")
            view.fragmentDismiss()
        }
    }

    fun leaveSeat() {
        isLeaveSeating = true
        addDisposable(
            roomModel
                .leaveSeat(AccountStore.getUserId()!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    isLeaveSeating = false
                }
                .subscribe({
                    view.showMessage("您已断开连接")
                    view.fragmentDismiss()
                }, {
                    view.showError(it.message)
                })
        )
    }
}