/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.revokeseatrequest

import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/29
 */
class RevokeSeatPresenter(val view: IRevokeSeatView, roomId: String) :
    BaseLifeCyclePresenter<IRevokeSeatView>(view) {
    val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }

    var cancel: Boolean = false
    override fun onCreate() {
        super.onCreate()
        addDisposable(
            roomModel
                .obMemberInfoByUserId(AccountStore.getUserId()!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.seatIndex > 0) {
                        view.showMessage("您已经在麦上了哦")
                        view.fragmentDismiss()
                    }else if (cancel) {
                        view.showMessage("已撤回连线申请")
                        cancel = false
                    }
                })
    }

    fun cancelRequest() {
        cancel = true
        addDisposable(
            roomModel
                .cancelRequest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.fragmentDismiss()
                    view.showMessage("已撤回连线申请")
                    cancel = false
                }, {
                    if (roomModel.isInSeat(AccountStore.getUserId()!!) > -1) {
                        view.showMessage("您已经在麦上了哦")
                    } else {
                        view.showError(it.message)
                    }
                    cancel = false
                })
        )
    }
}