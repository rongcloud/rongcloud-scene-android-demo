/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting

import cn.rongcloud.voiceroomdemo.common.IBaseView
import io.reactivex.rxjava3.core.Maybe

/**
 * @author gusd
 * @Date 2021/06/22
 */
interface IRoomSettingView :IBaseView {
    fun showPasswordDialog(): Maybe<String>?{
        return null
    }

    fun showModifyRoomNameDialog(roomName: String?):Maybe<String>?{
        return null
    }

    fun showBackgroundFragment()
    fun hideSettingView(){}
    fun showMusicSettingFragment(){}
}