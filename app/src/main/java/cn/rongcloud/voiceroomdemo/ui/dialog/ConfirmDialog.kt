/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.dialog

import android.content.Context
import cn.rongcloud.voiceroomdemo.R
import kotlinx.android.synthetic.main.layout_confirm_dialog.*

/**
 * @author gusd
 * @Date 2021/06/10
 */
class ConfirmDialog(
    context: Context,
    val message: String,
    private val needDismiss: Boolean = false,
    val confirm: String = "确定",
    val cancel: String = "取消",
    val cancelBlock: (() -> Unit)? = null,
    val confirmBlock: () -> Unit
) :
    BaseDialog(context, R.layout.layout_confirm_dialog, false) {
    override fun initListener() {
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_confirm.setOnClickListener {
            if (needDismiss) {
                dismiss()
            }
            confirmBlock()
        }
    }

    override fun initView() {
        tv_message.text = message
        btn_cancel.text = cancel
        btn_confirm.text = confirm
    }
}