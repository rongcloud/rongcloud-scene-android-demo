/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import androidx.fragment.app.Fragment
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import com.rongcloud.common.base.BaseLifeCyclePresenter
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/24
 */
class SeatOrderOperationPresenter @Inject constructor(
    view: IViewPageListView,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    override fun onCreate() {
        super.onCreate()
        roomModel.refreshAllMemberInfoList()
    }
}