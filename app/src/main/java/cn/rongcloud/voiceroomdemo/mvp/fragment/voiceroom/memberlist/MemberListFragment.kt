/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.memberlist

import android.util.Log
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.mvp.fragment.BaseBottomSheetDialogFragment
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.membersetting.IMemberSettingView
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.membersetting.MemberSettingFragment
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel
import kotlinx.android.synthetic.main.layout_member_list.*

/**
 * @author gusd
 * @Date 2021/06/16
 */
private const val TAG = "MemberListFragment"

class MemberListFragment(
    view: IMemberListView,
    private val memberSettingView: IMemberSettingView,
    private val roomInfoBean: VoiceRoomBean
) :
    BaseBottomSheetDialogFragment<MemberListPresenter, IMemberListView>(R.layout.layout_member_list),
    IMemberListView by view{


    override fun initPresenter(): MemberListPresenter {
        return MemberListPresenter(this, requireContext(), roomInfoBean)
    }


    override fun initData() {
        presenter.getMemberList()
    }

    override fun initListener() {
        iv_close.setOnClickListener {
            dismiss()
        }
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun initView() {
        rv_member_list.adapter = MemberListAdapter {
            Log.d(TAG, "item onClick: $it")
            if (AccountStore.getUserId() == it.userId) {
                // 点击自己不做任何反应
                return@MemberListAdapter
            }
            MemberSettingFragment(memberSettingView, roomInfoBean, it,false).show(childFragmentManager)

        }

    }

    override fun showMemberList(data: List<UiMemberModel>?) {
        Log.d(TAG, "showMemberList: $data")
        rv_member_list.post {
            data?.let {
                (rv_member_list.adapter as? MemberListAdapter)?.refreshData(data)
            }
        }
    }

}