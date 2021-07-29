/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId

/**
 * @author gusd
 * @Date 2021/06/24
 */
class SeatOrderOperationPresenter(view: IViewPageListView,val roomId: String) :
    BaseLifeCyclePresenter<IViewPageListView>(view) {

    override fun onCreate() {
        super.onCreate()
        getVoiceRoomModelByRoomId(roomId).refreshAllMemberInfoList()
    }
}