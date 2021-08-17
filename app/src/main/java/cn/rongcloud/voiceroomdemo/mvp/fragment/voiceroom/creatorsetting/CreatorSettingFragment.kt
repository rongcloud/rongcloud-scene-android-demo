/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.creatorsetting

import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import com.rongcloud.common.extension.loadPortrait
import com.rongcloud.common.extension.ui
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.AndroidEntryPoint
import com.rongcloud.common.ui.dialog.ConfirmDialog
import kotlinx.android.synthetic.main.fragmeng_creator_setting.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/28
 */
@HiltBinding(value = ICreatorView::class)
@AndroidEntryPoint
class CreatorSettingFragment(view: ICreatorView, private val roomInfoBean: VoiceRoomBean) :
    BaseBottomSheetDialogFragment(R.layout.fragmeng_creator_setting),
    ICreatorView by view {

    @Inject
    lateinit var presenter: CreatorSettingPresenter


    fun showMusicPauseTip(confirmBlock: () -> Unit) {
        ConfirmDialog(
            requireContext(),
            "播放音乐中下麦会导致音乐中断，是否确定下麦?",
            true,
        ) {
            confirmBlock()
        }.apply {
            show()
        }
    }

    override fun initView() {
        btn_out_of_seat.setOnClickListener {
            if (presenter.isPlayingMusic()) {
                showMusicPauseTip {
                    presenter.stopPlayMusic()
                    presenter.leaveSeat()
                }
            } else {
                presenter.leaveSeat()
            }
        }
        btn_mute_self.setOnClickListener {
            presenter.muteMic()
        }
        tv_member_name.text = roomInfoBean.createUser?.userName

        iv_member_portrait.loadPortrait(AccountStore.getUserPortrait())
    }

    override fun fragmentDismiss() {
        super.fragmentDismiss()
        dismiss()
    }

    override fun onMuteChange(isMute: Boolean) {
        super.onMuteChange(isMute)
        ui {
            if (isMute) {
                btn_mute_self.text = "打开麦克风"
            } else {
                btn_mute_self.text = "关闭麦克风"
            }
        }
    }
}