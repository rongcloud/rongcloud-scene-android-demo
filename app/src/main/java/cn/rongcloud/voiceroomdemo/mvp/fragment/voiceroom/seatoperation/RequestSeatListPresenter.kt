/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/24
 */
class RequestSeatListPresenter(val view: IRequestSeatListView, roomId: String) :
    BaseLifeCyclePresenter<IRequestSeatListView>(view) {
    private val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }

    override fun onResume() {
        super.onResume()
        addDisposable(
            roomModel
                .obRequestSeatListChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.refreshData(it)
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun acceptRequest(uiMemberModel: UiMemberModel, complete: () -> Unit) {
        addDisposable(
            roomModel
                .acceptRequest(uiMemberModel.userId)
                .subscribe({
                    complete()
                    uiMemberModel.isRequestSeat = false
                    roomModel.noticeMemberListUpdate()
                }, {
                    view.showError(it.message)
                    complete()
                })
        )
    }
}