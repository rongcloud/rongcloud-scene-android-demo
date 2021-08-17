/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.emptyseatsetting

import androidx.fragment.app.Fragment
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Named

/**
 * @author gusd
 * @Date 2021/06/28
 */
@FragmentScoped
class EmptySeatPresenter @Inject constructor(
    val view: IEmptySeatView,
    @Named("EmptySeatSetting") var uiSeatModel: UiSeatModel,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {


    override fun onCreate() {
        super.onCreate()
        addDisposable(
            roomModel
                .obSeatInfoByIndex(uiSeatModel.index)
                .subscribe({
                    uiSeatModel = it
                    view.refreshView(it)
                }, {
                    view.showError(it.message)
                })
        )
        addDisposable(roomModel
            .obSeatInfoChange()
            .filter {
                it.index == uiSeatModel.index
            }.subscribe {
                uiSeatModel = it
                view.refreshView(it)
            })
    }

    fun closeOrOpenSeat() {
        addDisposable(
            roomModel.setSeatLock(
                uiSeatModel.index,
                uiSeatModel.seatStatus != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking
            ).subscribe({
                view.showMessage(
                    if (uiSeatModel.seatStatus != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
                        "座位已开启"
                    } else {
                        "座位已关闭"
                    }
                )
            }, {
                view.showError(it.message)
            })
        )
    }

    fun muteOrUnMuteSeat() {
        addDisposable(
            roomModel.setSeatMute(
                uiSeatModel.index,
                !uiSeatModel.isMute
            ).subscribe({

            }, {
                view.showError(it.message)
            })
        )
    }
}