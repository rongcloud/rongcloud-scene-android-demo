/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel

/**
 * @author gusd
 * @Date 2021/06/24
 */
interface IRequestSeatListView : IBaseView {
    fun refreshData(list: List<UiMemberModel>){}
}