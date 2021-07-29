/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.seatoperation

import android.view.View
import android.view.ViewGroup
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.loadPortrait
import cn.rongcloud.voiceroomdemo.mvp.fragment.BaseFragment
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_request_seat_item.view.*

/**
 * @author gusd
 * @Date 2021/06/24
 */
class RequestSeatListFragment(view: IRequestSeatListView, val roomId: String) :
    BaseFragment<RequestSeatListPresenter, IRequestSeatListView>(R.layout.layout_list),
    IRequestSeatListView by view {

    override fun initPresenter(): RequestSeatListPresenter {
        return RequestSeatListPresenter(this, roomId)
    }

    override fun initView() {
        rv_list.adapter = MyAdapter()

    }

    override fun getTitle(): String {
        return "申请连麦"
    }

    override fun refreshData(list: List<UiMemberModel>) {
        super.refreshData(list)
        (rv_list.adapter as? MyAdapter)?.refreshData(list)
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
                tv_operation.text = "接受"
                tv_operation.setOnClickListener {
                    tv_operation.isEnabled = false
                    presenter.acceptRequest(uiMemberModel) {
                        tv_operation.isEnabled = true
                    }
                }
            }
        }

    }
}



