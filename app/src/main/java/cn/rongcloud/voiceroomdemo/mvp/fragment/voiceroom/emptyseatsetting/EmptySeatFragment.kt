/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.emptyseatsetting

import androidx.core.view.isVisible
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import com.rongcloud.common.extension.ui
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_empty_seat_setting.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/28
 */
@HiltBinding(value = IEmptySeatView::class)
@AndroidEntryPoint
class EmptySeatFragment(
    val view: IEmptySeatView,
    var seatInfo: UiSeatModel,
    val roomId: String
) :
    BaseBottomSheetDialogFragment(R.layout.fragment_empty_seat_setting),
    IEmptySeatView by view {

    override fun getEmptyUiSeatModel(): UiSeatModel {
        return seatInfo
    }

    @Inject
    lateinit var presenter: EmptySeatPresenter

    override fun initView() {

    }

    override fun initListener() {
        super.initListener()
        ll_close_seat.setOnClickListener {
            presenter.closeOrOpenSeat()
        }
        ll_mute_seat.setOnClickListener {
            presenter.muteOrUnMuteSeat()
        }

        btn_invite_user_into_seat.setOnClickListener {
            dismiss()
            view.showInviteUserView()
        }

    }

    override fun refreshView(it: UiSeatModel) {
        super.refreshView(it)
        seatInfo = it
        ui {
            if (it.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                dismiss()
                return@ui
            }
            if (it.isMute) {
                iv_mute_seat.setImageResource(R.drawable.ic_room_setting_unmute_all)
                tv_mute_seat.text = "取消禁麦"
                iv_is_mute.isVisible = true
            } else {
                iv_mute_seat.setImageResource(R.drawable.ic_member_setting_mute_seat)
                tv_mute_seat.text = "座位禁麦"
                iv_is_mute.isVisible = false
            }

            if (it.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
                iv_seat_status.setImageResource(R.drawable.ic_seat_status_locked)
                iv_user_portrait.setImageResource(R.drawable.bg_seat_status)
                iv_close_seat.setImageResource(R.drawable.ic_room_setting_unlock_all)
                tv_close_seat.text = "开启座位"

            } else if (it.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
                iv_seat_status.setImageResource(R.drawable.ic_seat_status_enter)
                iv_user_portrait.setImageResource(R.drawable.bg_seat_status)

                iv_close_seat.setImageResource(R.drawable.ic_room_setting_lock_all)
                tv_close_seat.text = "关闭座位"
            }
            tv_member_name.text = "${it.index} 号麦位"
        }
    }
}