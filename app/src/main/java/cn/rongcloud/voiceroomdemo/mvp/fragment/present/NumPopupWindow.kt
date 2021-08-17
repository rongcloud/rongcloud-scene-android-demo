/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.present

import android.content.Context
import android.view.View
import com.bcq.adapter.recycle.RcyHolder
import com.bcq.adapter.recycle.RcySAdapter
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.utils.UiUtils.dp2Px
import kotlinx.android.synthetic.main.layout_pop_present_num.view.*
import kotlinx.android.synthetic.main.layout_pop_present_num_item.view.*

/**
 * @author baicq
 * @Date 2021/07/06
 */
class NumPopupWindow(
    context: Context,
    private val listener: (num: Int) -> Unit,
    private var selected: Int = 1,
    width: Int = dp2Px(context, 160f),
    heith: Int = dp2Px(context, 30f) * 6,
    focusable: Boolean = false
) :
    cn.rongcloud.voiceroomdemo.mvp.fragment.present.pop.CustomerPopupWindow(context, R.layout.layout_pop_present_num, width, heith, focusable) {

    override fun initView(content: View) {
        with(content) {
            pop_title.text = "自定义"
            rcy_pop.adapter = object :
                RcySAdapter<Int, RcyHolder>(this.context, R.layout.layout_pop_present_num_item) {
                override fun convert(holder: RcyHolder, t: Int, position: Int) {
                    with(holder.itemView) {
                        this.pop_info.text = "$t"
                        this.isSelected = selected == t
                        this.setOnClickListener({
                            selected = t
                            dismiss()
                        })
                    }
                }
            }.apply {
                val nums = ArrayList<Int>()
                nums.add(999)
                nums.add(666)
                nums.add(99)
                nums.add(10)
                nums.add(1)
                this.setData(nums, true)
            }
        }
        // 设置消失监听
        setOnDismissListener {
            listener(selected)
        }
    }

    fun show(anchor: View) {
        isFocusable = true
        super.showAsDropUp(anchor, 2)
        isOutsideTouchable = false
    }
}