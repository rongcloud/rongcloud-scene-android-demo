/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.createroom

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean

/**
 * @author gusd
 * @Date 2021/06/15
 */
interface ICreateVoiceRoomView:IBaseView {
    fun onCreateRoomSuccess(data: VoiceRoomBean?)
    fun onCreateRoomExist(data: VoiceRoomBean?)

}