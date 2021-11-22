/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.ui.dialog

import android.content.Context
import cn.rong.combusis.R
import kotlinx.android.synthetic.main.layout_confirm_dialog.*

/**
 * @author gusd
 * @Date 2021/06/22
 */
class MessageDialog(
    context: Context,
    val message: String,
    private val needDismiss: Boolean = false,
    val callback: () -> Unit
) :
    BaseDialog(context, R.layout.layout_message_dialog, false) {

    override fun initListener() {
        btn_confirm.setOnClickListener {
            if (needDismiss) {
                dismiss()
            }
            callback()
        }
    }

    override fun initView() {
        tv_message.text = message
    }
}