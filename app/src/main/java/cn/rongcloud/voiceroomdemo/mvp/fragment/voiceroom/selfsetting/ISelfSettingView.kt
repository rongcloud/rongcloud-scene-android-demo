/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.selfsetting

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiSeatModel

/**
 * @author gusd
 * @Date 2021/06/28
 */
interface ISelfSettingView:IBaseView {
    fun refreshView(uiSeatModel: UiSeatModel){}
    fun fragmentDismiss() {}
    fun onRecordStatusChange(isRecording:Boolean) {

    }
}