/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.memberlist

import android.content.Context
import android.util.Log
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/06/21
 */
private const val TAG = "MemberListPresenter"

class MemberListPresenter(
    private val view: IMemberListView,
    private val context: Context,
    private val roomInfoBean: VoiceRoomBean
) : BaseLifeCyclePresenter<IMemberListView>(view) {
    private val roomModel: VoiceRoomModel by lazy {
        getVoiceRoomModelByRoomId(roomInfoBean.roomId)
    }

    fun getMemberList() {
        addDisposable(roomModel
            .obMemberListChange()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ bean ->
                Log.d(TAG, "getMemberList: ${bean.size}")
                view.showMemberList(bean)
            }, { t ->
                view.showError(-1, t.message)
            }))


    }

    override fun onResume() {
        super.onResume()
        roomModel.refreshAllMemberInfoList()
    }
}