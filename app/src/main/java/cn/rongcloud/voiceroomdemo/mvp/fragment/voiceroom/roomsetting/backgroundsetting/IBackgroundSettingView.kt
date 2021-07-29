/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.backgroundsetting

import cn.rongcloud.voiceroomdemo.common.IBaseView

/**
 * @author gusd
 * @Date 2021/06/22
 */
interface IBackgroundSettingView:IBaseView {
    fun onBackgroundList(backGroundUrlList: List<String>){}
}