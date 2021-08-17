/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel

/**
 * @author gusd
 * @Date 2021/06/24
 */
interface IInviteSeatListView: IBaseView {
    fun refreshData(data: List<UiMemberModel>) {

    }
}