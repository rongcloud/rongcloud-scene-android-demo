/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity.iview

import cn.rongcloud.voice.net.bean.respond.VoiceRoomBean
import com.rongcloud.common.base.IBaseView

/**
 * @author gusd
 * @Date 2021/06/09
 */
interface IVoiceRoomListView : IBaseView {
    fun onDataChange(list: List<VoiceRoomBean>?)
    fun onLoadError(throwable: Throwable?)
    fun showInputPasswordDialog(bean: VoiceRoomBean)
}