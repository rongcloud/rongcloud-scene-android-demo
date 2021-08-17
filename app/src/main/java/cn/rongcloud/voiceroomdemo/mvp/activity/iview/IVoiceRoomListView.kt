/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity.iview

import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean

/**
 * @author gusd
 * @Date 2021/06/09
 */
interface IVoiceRoomListView : IBaseView {
    fun onDataChange(list: List<VoiceRoomBean>?)
    fun onLoadError(throwable: Throwable?)
    fun showInputPasswordDialog(bean: VoiceRoomBean)
}