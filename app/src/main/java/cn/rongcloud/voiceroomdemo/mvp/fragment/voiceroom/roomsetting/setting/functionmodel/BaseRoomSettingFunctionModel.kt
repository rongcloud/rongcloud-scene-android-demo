/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId

/**
 * @author gusd
 * @Date 2021/06/23
 */
abstract class BaseRoomSettingFunctionModel(roomId:String):BaseFunctionModel() {
    protected val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }
}