/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.present

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcq.adapter.recycle.RcyHolder
import com.bcq.adapter.recycle.RcySAdapter
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.loadPortrait
import cn.rongcloud.voiceroomdemo.common.ui
import cn.rongcloud.voiceroomdemo.mvp.fragment.BaseBottomSheetDialogFragment
import cn.rongcloud.voiceroomdemo.mvp.fragment.present.page.CustomerPageLayoutManager
import cn.rongcloud.voiceroomdemo.mvp.fragment.present.page.CustomerPageLayoutManager.HORIZONTAL
import cn.rongcloud.voiceroomdemo.mvp.fragment.present.page.PagerSnapHelper
import cn.rongcloud.voiceroomdemo.mvp.model.Present
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMemberModel
import cn.rongcloud.voiceroomdemo.utils.UiUtils
import kotlinx.android.synthetic.main.fragmeng_send_present.*
import kotlinx.android.synthetic.main.layout_present_item.view.*
import kotlinx.android.synthetic.main.layout_present_member_item.view.*

/**
 * @author baicq
 * @Date 2021/07/05
 */
class SendPresentFragment(
    view: ISendPresentView,
    private val roomId: String,
    private val selectedIds: List<String> = emptyList()
) :
    BaseBottomSheetDialogFragment<SendPresentPresenter, ISendPresentView>(R.layout.fragmeng_send_present),
    ISendPresentView by view {
    override fun initPresenter(): SendPresentPresenter {
        return SendPresentPresenter(this, roomId, selectedIds)
    }

    val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }
    var members: List<UiMemberModel> = ArrayList()

    override fun initListener() {
        btn_selectall.setOnClickListener {
            members.let {
                /**
                 * 已全选 -> selected = false 显示文字：'全选' 非全选
                 * 非全选 -> selected = true 显示文字：'取消' 已全选
                 */
                if (btn_selectall.isSelected) {//已全选
                    presenter.selectAll(members)
                    btn_selectall.isSelected = false
                    btn_selectall.text = "取消"
                } else {//取消
                    presenter.selectAll(null)
                    btn_selectall.isSelected = true
                    btn_selectall.text = "全选"
                }
                rcy_member.adapter?.notifyDataSetChanged()
            }
        }
        btn_send.setOnClickListener {
            presenter.sendPresent(presenter.selects.size == members.size && presenter.selects.size != 1)
        }
        btn_num.setOnClickListener {
            NumPopupWindow(
                requireContext(), {
                    presenter.presentNum = it
                    // pop dismiss
                    updateBtnNum(false)
                },
                presenter.presentNum
            ).show(btn_num)
            // show pop
            updateBtnNum(true)
        }
    }


    override fun initData() {
        presenter.initeialObserve()
    }

    @SuppressLint("SetTextI18n")
    fun updateBtnNum(showPop: Boolean) {
        btn_num.text = "${presenter.presentNum} x"
        var res = R.drawable.ic_up
        if (showPop) res = R.drawable.ic_down
        btn_num.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
        btn_num.compoundDrawablePadding = UiUtils.dp2Px(requireContext(), 1f)
    }

    /**
     * 已全选：文案 取消 isSelected = false
     * 非全选：文案 全选 isSelected = true
     */
    fun updateBtnAll() {
        var isAll = presenter.selects.size == members.size
        btn_selectall.isSelected = !isAll
        btn_selectall.text = if (isAll) "取消" else "全选"
    }

    override fun initView() {
        updateBtnNum(false)
        updateBtnAll()
        rcy_member.layoutManager.let {
            (it as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        }
        rcy_member.adapter = object : RcySAdapter<UiMemberModel, RcyHolder>(
            requireContext(),
            R.layout.layout_present_member_item
        ) {
            override fun convert(holder: RcyHolder, t: UiMemberModel, position: Int) {
                with(holder.itemView) {
                    this.isSelected = presenter.isSelected(t)
                    this.setOnClickListener {
                        presenter.updateSelected(t)
                        updateBtnAll()
                        notifyDataSetChanged()
                    }
                    this.iv_member_head.loadPortrait(t.portrait)
                    this.tv_member_name.text =
                        if (t.userId == roomModel.currentUIRoomInfo.roomBean?.createUser?.userId) "房主" else if (t.seatIndex < 0) "观众" else "${t.seatIndex}"
                }
            }
        }
        vp_present.setHasFixedSize(true)
        val pageLayoutManager =
            CustomerPageLayoutManager(
                2,
                4,
                HORIZONTAL
            )
        pageLayoutManager.isAllowContinuousScroll = false
        pageLayoutManager.setPageListener(object : CustomerPageLayoutManager.PageListener {
            override fun onPageSizeChanged(pageSize: Int) {}
            override fun onItemVisible(fromItem: Int, toItem: Int) {}
            override fun onPageSelect(pageIndex: Int) {
                onPersentPageSelect(pageIndex)
            }
        })
        vp_present.layoutManager = pageLayoutManager
        val pagerSnapHelper = cn.rongcloud.voiceroomdemo.mvp.fragment.present.page.PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(vp_present)
        vp_present.adapter = object :
            RcySAdapter<Present, RcyHolder>(requireContext(), R.layout.layout_present_item) {
            override fun convert(holder: RcyHolder, t: Present, position: Int) {
                with(holder.itemView) {
                    this.isSelected = t == presenter.currentPresent
                    this.iv_present.setImageResource(t.icon)
                    this.tv_present_name.text = t.name
                    this.tv_present_price.text = "${t.price}"
                    this.setOnClickListener {
                        if (presenter.currentPresent == t) {
                            presenter.currentPresent = null
                        } else {
                            presenter.currentPresent = t
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun onPersentPageSelect(page: Int) {
        if (0 == page) {
            index_first.setBackgroundResource(R.drawable.bg_index_selected)
            index_second.setBackgroundResource(R.drawable.bg_index_nomal)
        } else {
            index_first.setBackgroundResource(R.drawable.bg_index_nomal)
            index_second.setBackgroundResource(R.drawable.bg_index_selected)
        }
    }


    override fun fragmentDismiss() {
        super.fragmentDismiss()
        dismiss()
    }

    override fun onMemberModify(members: List<UiMemberModel>) {
        ui {
            this.members = members
            btn_selectall.visibility = if (members.size > 1) View.VISIBLE else View.GONE
            updateBtnAll()
            (rcy_member.adapter as RcySAdapter<UiMemberModel, RcyHolder>).setData(
                this.members,
                true
            )
        }
    }

    override fun onPresentInited(members: List<Present>) {
        ui {
            (vp_present.adapter as RcySAdapter<Present, RcyHolder>).setData(members, true)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onEnableSend(enable: Boolean) {
        ui {
            btn_send?.isEnabled = enable
            btn_num?.isEnabled = enable
            btn_num.text = "${presenter.presentNum} x"
        }
    }
}