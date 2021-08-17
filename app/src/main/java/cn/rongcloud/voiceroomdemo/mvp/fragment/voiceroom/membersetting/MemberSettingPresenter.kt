/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.membersetting

import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import com.rongcloud.common.utils.AccountStore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/21
 */
class MemberSettingPresenter @Inject constructor(
    private val view: IMemberSettingView,
    private val roomInfoBean: VoiceRoomBean,
    private var member: UiMemberModel,
    private val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    override fun onCreate() {
        super.onCreate()
        addDisposable(
            roomModel
                .obMemberInfoChange()
                .filter {
                    member.userId == it.userId
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.member = it
                    refreshView()
                }
        )
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }


    private fun refreshView() {
        view.loginUserIsCreator(
            roomInfoBean.createUser?.userId == AccountStore.getUserId(),
            roomModel.isAdmin(AccountStore.getUserId()!!)
        )
        view.thisUserIsOnSeat(member.seatIndex, roomModel.isAdmin(AccountStore.getUserId()!!))
        view.thisUserIsAdmin(member.isAdmin)
        view.thisUserIsMute(roomModel.getSeatInfoByUserId(member.userId)?.isMute ?: false)
    }

    fun kickSeat() {
        addDisposable(
            roomModel
                .kickSeat(member.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showMessage("发送下麦通知成功")
                    view.fragmentDismiss()
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun closeSeat() {
        addDisposable(
            roomModel
                .setSeatLockByUserId(member.userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.fragmentDismiss()
                    view.showMessage("座位已关闭")
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun muteSeat() {
        roomModel.getSeatInfoByUserId(member.userId)?.isMute?.let { isMute ->
            addDisposable(
                roomModel
                    .setSeatMuteByUserId(member.userId, !isMute)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view.showMessage(
                            if (isMute) {
                                "已取消闭麦"
                            } else {
                                "此麦位已闭麦"
                            }
                        )
                        view.fragmentDismiss()
                    }, {
                        view.showError(it.message)
                    })
            )
        }

    }

    fun kickRoom() {
        addDisposable(
            roomModel
                .kickRoom(member.userId).subscribe({
                    view.fragmentDismiss()
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun inviteEnterSeat() {
        addDisposable(
            roomModel
                .invitedIntoSeat(member.userId)
                .subscribe({
                    view.showMessage("发送上麦通知成功")
                    view.fragmentDismiss()
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun toggleAdmin() {
        addDisposable(
            roomModel
                .setAdmin(member.userId, !member.isAdmin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ isSuccess ->
                    if (!isSuccess) {
                        view.showError("设置失败")
                        view.fragmentDismiss()
                    }
                }, { t ->
                    view.showError(t.message)
                })
        )
    }
}