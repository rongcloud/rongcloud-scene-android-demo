/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMusicModel

/**
 * @author gusd
 * @Date 2021/07/06
 */
interface IMusicAddView:IBaseView {
    fun showMusicList(list: List<UiMusicModel>) {}
}