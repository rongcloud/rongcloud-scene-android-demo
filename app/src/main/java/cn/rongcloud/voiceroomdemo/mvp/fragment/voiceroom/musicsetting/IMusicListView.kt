/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMusicModel

/**
 * @author gusd
 * @Date 2021/07/06
 */
interface IMusicListView : IBaseView {
    fun showMusicList(musicList: List<UiMusicModel>) {

    }

    fun gotoAddMusicView() {

    }
}