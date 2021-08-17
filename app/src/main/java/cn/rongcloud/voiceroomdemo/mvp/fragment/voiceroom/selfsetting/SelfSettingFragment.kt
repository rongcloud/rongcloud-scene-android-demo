/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.selfsetting

import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import com.rongcloud.common.extension.loadPortrait
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_self_setting.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/28
 */
@HiltBinding(value = ISelfSettingView::class)
@AndroidEntryPoint
class SelfSettingFragment(view: ISelfSettingView, var seatInfo: UiSeatModel, val roomId: String) :
    BaseBottomSheetDialogFragment(
        R.layout.fragment_self_setting
    ), ISelfSettingView by view {

    @Inject
    lateinit var presenter: SelfSettingPresenter

    override fun getUiSeatModel(): UiSeatModel {
        return seatInfo
    }

    override fun initView() {
        iv_member_portrait.loadPortrait(seatInfo.portrait)
        tv_member_name.text = seatInfo.userName
        btn_mute_self.setOnClickListener {
            presenter.muteSelf()
        }
        btn_out_of_seat.setOnClickListener {
            presenter.leaveSeat()
        }
    }

    override fun refreshView(uiSeatModel: UiSeatModel) {
//        if(uiSeatModel.isMute){
//            btn_mute_self.text = "打开麦克风"
//        }else{
//            btn_mute_self.text = "关闭麦克风"
//        }
    }

    override fun fragmentDismiss() {
        dismiss()
    }

    override fun onRecordStatusChange(isRecording: Boolean) {
        if (!isRecording) {
            btn_mute_self.text = "打开麦克风"
        } else {
            btn_mute_self.text = "关闭麦克风"
        }
    }
}