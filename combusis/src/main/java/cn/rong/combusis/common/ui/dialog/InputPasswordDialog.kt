/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.ui.dialog

import android.content.Context
import cn.rong.combusis.R
import kotlinx.android.synthetic.main.layout_input_password_dialog.*

/**
 * @author gusd
 * @Date 2021/06/10
 */
private const val TAG = "InputPasswordDialog"

class InputPasswordDialog(
    context: Context,
    private val isSettingPassword: Boolean? = false,
    val cancelListener: (() -> Unit)? = null,
    val result: (String) -> Unit

) :
    BaseDialog(context, R.layout.layout_input_password_dialog, false) {
    override fun initListener() {
        btn_cancel.setOnClickListener {
            cancelListener?.invoke()
            dismiss()
        }

        btn_confirm.setOnClickListener {
            result(et_password.content)
        }
    }


    override fun initView() {
        if (isSettingPassword == true) {
            tv_label_dialog_title.setText(R.string.please_setting_four_number_password)
        } else {
            tv_label_dialog_title.setText(R.string.please_input_four_number_password)
        }
    }
}