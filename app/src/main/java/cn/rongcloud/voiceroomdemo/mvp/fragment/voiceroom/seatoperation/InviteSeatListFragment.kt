/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import android.view.View
import android.view.ViewGroup
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseFragment
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import com.rongcloud.common.extension.loadPortrait
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_request_seat_item.view.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/24
 */
@HiltBinding(value = IInviteSeatListView::class)
@AndroidEntryPoint
class InviteSeatListFragment(view: IInviteSeatListView) :
    BaseFragment(R.layout.layout_list),
    IInviteSeatListView by view {

    @Inject
    lateinit var  presenter: InviteSeatListPresenter


    override fun initView() {
        rv_list.adapter = MyAdapter()
    }

    override fun getTitle(): String {
        return "邀请连麦"
    }

    private inner class MyAdapter : BaseListAdapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(parent)
        }

    }

    private inner class MyViewHolder(parent: ViewGroup) : BaseViewHolder(parent) {
        override fun bindView(uiMemberModel: UiMemberModel, itemView: View) {
            with(itemView) {
                iv_user_portrait.loadPortrait(uiMemberModel.portrait)
                tv_member_name.text = uiMemberModel.userName
                tv_operation.text = "邀请"
                tv_operation.isSelected = uiMemberModel.isInvitedInfoSeat
                setOnClickListener {
//                    if(tv_operation.isSelected){
//                        return@setOnClickListener
//                    }
                    presenter.inviteIntoSeat(uiMemberModel)
                }
            }
        }

    }

    override fun refreshData(data: List<UiMemberModel>) {
        super.refreshData(data)
        (rv_list.adapter as? MyAdapter)?.refreshData(data)
    }
}