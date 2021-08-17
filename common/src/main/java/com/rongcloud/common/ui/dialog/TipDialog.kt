/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.ui.dialog

import android.content.Context
import androidx.core.view.isVisible
import com.rongcloud.common.R
import kotlinx.android.synthetic.main.layout_confirm_dialog.*

/**
 * @author gusd
 * @Date 2021/06/10
 */
class TipDialog(
    context: Context,
    val message: String,
    val confirm: String = "确定",
    val cancel: String = "取消",
    val listener: () -> Unit
) :
    BaseDialog(context, R.layout.layout_confirm_dialog, false) {
    override fun initListener() {
        btn_confirm.setOnClickListener {
            listener()
            dismiss()
        }
    }

    override fun initView() {
        btn_cancel.isVisible = false
        b_divider.isVisible = false
        tv_message.text = message
        btn_cancel.text = cancel
        btn_confirm.text = confirm
    }
}