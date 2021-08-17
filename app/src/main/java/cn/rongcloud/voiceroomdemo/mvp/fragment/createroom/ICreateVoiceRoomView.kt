/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.createroom

import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean

/**
 * @author gusd
 * @Date 2021/06/15
 */
interface ICreateVoiceRoomView: IBaseView {
    fun onCreateRoomSuccess(data: VoiceRoomBean?)
    fun onCreateRoomExist(data: VoiceRoomBean?)

}