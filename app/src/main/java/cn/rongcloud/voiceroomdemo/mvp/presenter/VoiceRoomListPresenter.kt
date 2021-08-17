/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.presenter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import cn.rongcloud.voiceroomdemo.mvp.activity.VoiceRoomActivity
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.IVoiceRoomListView
import cn.rongcloud.voiceroomdemo.mvp.model.EMPTY_ROOM_INFO
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomListModel
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import com.rongcloud.common.base.BaseLifeCyclePresenter
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/09
 */
@ActivityScoped
class VoiceRoomListPresenter @Inject constructor(
    val view: IVoiceRoomListView,
    private val voiceRoomListMode: VoiceRoomListModel,
    activity: AppCompatActivity
) :
    BaseLifeCyclePresenter(activity) {


    override fun onCreate() {
        super.onCreate()
        initObDataChange()
    }

    private fun initObDataChange() {
        addDisposable(
            voiceRoomListMode
                .obVoiceRoomList()
                .subscribe { bean ->
                    view.onDataChange(bean)
                }
        )

        addDisposable(voiceRoomListMode
            .obVoiceRoomErrorEvent()
            .subscribe {
                view.onLoadError(it)
            })
    }

    override fun onResume() {
        super.onResume()
        voiceRoomListMode.refreshDataList()
    }

    fun refreshData() {
        voiceRoomListMode.refreshDataList()
    }

    fun loadMore() {
        voiceRoomListMode.loadMoreData()
    }

    fun gotoVoiceRoomActivity(context: Context, roomId: String, isCreate: Boolean = false) {
        if (isCreate) {
            VoiceRoomActivity.startActivity(context, roomId, AccountStore.getUserId()!!, isCreate)
            return
        }
        view.showWaitingDialog()
        voiceRoomListMode
            .queryRoomInfoFromServer(roomId)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { view.hideWaitingDialog() }
            .doFinally {
                view.hideWaitingDialog()
            }
            .subscribe({ info ->
                if (info.room == null || info.room == EMPTY_ROOM_INFO) {
                    view.showError("房间不存在")
                } else {
                    if (info.room?.isPrivate == 1 && info.room?.createUser?.userId != AccountStore.getUserId()) {
                        view.showInputPasswordDialog(info.room!!)
                    } else {
                        turnToRoom(context, info.room)
                    }
                }
            }, { t ->
                view.showError(t.message)
            })

    }

    fun turnToRoom(context: Context, info: VoiceRoomBean?) {
        info?.createUser?.let {
            VoiceRoomActivity.startActivity(context, info.roomId, it.userId)
        } ?: view.showError("房间数据错误")
    }

    fun addRoomInfo(roomInfo: VoiceRoomBean) {
        voiceRoomListMode.addRoomInfo(roomInfo)
    }
}