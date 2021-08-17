/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import androidx.fragment.app.Fragment
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import com.rongcloud.common.base.BaseLifeCyclePresenter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/24
 */
class RequestSeatListPresenter @Inject constructor(
    val view: IRequestSeatListView,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

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