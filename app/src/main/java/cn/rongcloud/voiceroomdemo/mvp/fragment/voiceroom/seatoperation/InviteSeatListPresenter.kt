/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/24
 */
class InviteSeatListPresenter @Inject constructor(
    val view: IInviteSeatListView,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    override fun onResume() {
        super.onResume()
        addDisposable(
            roomModel
                .obInviteSeatListChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.refreshData(it)
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun inviteIntoSeat(uiMemberModel: UiMemberModel) {
        addDisposable(
            roomModel
                .invitedIntoSeat(uiMemberModel.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    view.showError(it.message)
                })
        )
    }
}