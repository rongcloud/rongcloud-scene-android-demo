/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.memberlist

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel

/**
 * @author gusd
 * @Date 2021/06/21
 */
interface IMemberListView :IBaseView{
    fun showMemberList(data: List<UiMemberModel>?){}
}