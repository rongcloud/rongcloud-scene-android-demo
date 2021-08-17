/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatsetting

import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment

/**
 * @author gusd
 * @Date 2021/06/22
 */
class SeatSettingFragment(view: ISeatSettingView) :
    BaseBottomSheetDialogFragment(R.layout.fragment_seat_setting),
    ISeatSettingView by view {

    override fun initView() {

    }
}