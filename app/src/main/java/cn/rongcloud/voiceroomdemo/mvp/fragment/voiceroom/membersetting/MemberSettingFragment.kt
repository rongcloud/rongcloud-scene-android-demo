/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.membersetting

import androidx.core.view.isVisible
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import com.rongcloud.common.extension.loadPortrait
import com.rongcloud.common.extension.ui
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.AndroidEntryPoint
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.layout_member_setting.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/21
 */
@HiltBinding(value = IMemberSettingView::class)
@AndroidEntryPoint
class MemberSettingFragment(
    val view: IMemberSettingView,
    private val roomInfoBean: VoiceRoomBean,
    val member: UiMemberModel,
) :
    BaseBottomSheetDialogFragment(R.layout.layout_member_setting),
    IMemberSettingView by view {

    @Inject
    lateinit var presenter: MemberSettingPresenter


    override fun getMemberInfo(): UiMemberModel? {
        return member
    }

    override fun initView() {
        if (roomInfoBean.createUser?.userId == AccountStore.getUserId()) {
            rl_setting_admin.isVisible = true
            cl_member_setting.isVisible = true
        }
        iv_member_portrait.loadPortrait(member.portrait)
        tv_member_name.text = member.userName

        rl_setting_admin.setOnClickListener {
            presenter.toggleAdmin()
        }

        ll_invited_seat.setOnClickListener {
            presenter.inviteEnterSeat()
        }
        ll_kick_room.setOnClickListener {
            presenter.kickRoom()
        }
        ll_kick_seat.setOnClickListener {
            presenter.kickSeat()
        }
        ll_mute_seat.setOnClickListener {
            presenter.muteSeat()
        }
        ll_close_seat.setOnClickListener {
            presenter.closeSeat()
        }
    }

    override fun initListener() {
        super.initListener()
        btn_send_gift.setOnClickListener {
            view.sendGift(member.userId)
        }
        btn_send_message.setOnClickListener {
            RouteUtils.routeToConversationActivity(
                requireContext(),
                Conversation.ConversationType.PRIVATE,
                member.userId
            )
        }
    }


    override fun loginUserIsCreator(isCreatorUser: Boolean, isAdmin: Boolean) {
        ui {
            when {
                isCreatorUser -> {
                    rl_setting_admin.isVisible = true
                    cl_member_setting.isVisible = true
                    //处理管理员可能因此
                    ll_mute_seat.isVisible = true
                    ll_close_seat.isVisible = true
                }
                isAdmin -> {
                    rl_setting_admin.isVisible = false
                    if (member.isAdmin || roomInfoBean.createUser?.userId == member.userId) {
                        cl_member_setting.isVisible = false//对方是管理员 不能操作
                    } else {
                        cl_member_setting.isVisible = true
                        // 管理员没有权限处理座位相关权限
                        ll_mute_seat.isVisible = false
                        ll_close_seat.isVisible = false
//                        if(!isFromSeat) {
//                            ll_mute_seat.isVisible = false
//                            ll_close_seat.isVisible = false
//                        }else{
//                            ll_mute_seat.isVisible = true
//                            ll_close_seat.isVisible = true
//                        }
                    }
                }
                else -> {
                    rl_setting_admin.isVisible = false
                    cl_member_setting.isVisible = false
                }
            }
        }
    }

    override fun thisUserIsOnSeat(seatIndex: Int, isAdmin: Boolean) {
        ui {
            if (seatIndex > -1) {
                ll_kick_seat.isVisible = true
                // 修改麦位和权限的冲突
                if (roomInfoBean.createUser?.userId == AccountStore.getUserId()) {
                    ll_mute_seat.isVisible = true
                    ll_close_seat.isVisible = true
                } else if (isAdmin && (!member.isAdmin && roomInfoBean.createUser?.userId != member.userId)) {
                    // 管理员没有权限处理座位相关权限
                    ll_mute_seat.isVisible = false
                    ll_close_seat.isVisible = false
//                    if(!isFromSeat) {
//                        ll_mute_seat.isVisible = false
//                        ll_close_seat.isVisible = false
//                    }else{
//                        ll_mute_seat.isVisible = true
//                        ll_close_seat.isVisible = true
//                    }
                }
                ll_kick_room.isVisible = true
                ll_invited_seat.isVisible = false
                tv_seat_position.isVisible = true
                tv_seat_position.text = "$seatIndex 号麦位"

            } else {
                ll_kick_seat.isVisible = false
                ll_close_seat.isVisible = false
                ll_mute_seat.isVisible = false
                ll_kick_room.isVisible = true
                ll_invited_seat.isVisible = true
                tv_seat_position.isVisible = false
            }
        }
    }

    override fun thisUserIsAdmin(isAdmin: Boolean) {
        if (isAdmin) {
            tv_setting_admin.text = "撤回管理"
            tv_setting_admin.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_member_setting_is_admin,
                0,
                0,
                0
            )
        } else {
            tv_setting_admin.text = "设为管理"
            tv_setting_admin.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_member_setting_not_admin,
                0,
                0,
                0
            )
        }
    }

    override fun thisUserIsMute(isMute: Boolean) {
        if (isMute) {
            iv_mute_seat.setImageResource(R.drawable.ic_room_setting_unmute_all)
            tv_mute_seat.text = "座位开麦"

        } else {
            iv_mute_seat.setImageResource(R.drawable.ic_member_setting_mute_seat)
            tv_mute_seat.text = "座位闭麦"
        }
    }

    override fun fragmentDismiss() {
        dismiss()
    }
}