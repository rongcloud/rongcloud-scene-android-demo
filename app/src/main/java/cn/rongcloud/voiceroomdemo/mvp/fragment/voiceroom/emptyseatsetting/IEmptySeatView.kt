/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.emptyseatsetting

import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel

/**
 * @author gusd
 * @Date 2021/06/28
 */
interface IEmptySeatView: IBaseView {
    fun refreshView(uiSeatModel: UiSeatModel){}
    fun showInviteUserView(){}
    fun getEmptyUiSeatModel():UiSeatModel?{
        return null
    }
}