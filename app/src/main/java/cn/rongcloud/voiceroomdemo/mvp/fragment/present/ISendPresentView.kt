/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.present

import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.mvp.model.Present
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel

/**
 * @author baicq
 * @Date 2021/06/28
 */
interface ISendPresentView:IBaseView {
    fun fragmentDismiss(){}
    fun onMemberModify(members:List<UiMemberModel>) {
    }

    fun onPresentInited(members:List<Present>) {
    }

    fun onEnableSend(enable:Boolean){

    }
}