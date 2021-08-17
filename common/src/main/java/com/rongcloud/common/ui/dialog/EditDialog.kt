/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.ui.dialog

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.rongcloud.common.R
import com.rongcloud.common.utils.MaxLengthWithEmojiFilter
import kotlinx.android.synthetic.main.layout_edit_dialog.*

/**
 * @author gusd
 * @Date 2021/06/22
 */
class EditDialog(
    context: Context,
    private val title: String = "",
    private val hint: String,
    private val text: String = "",
    private val maxLength: Int = Int.MAX_VALUE,
    private val cancelListener: (() -> Unit)? = null,
    private val callBack: (context: String?) -> Unit
) :
    BaseDialog(context, R.layout.layout_edit_dialog, false) {
    override fun initListener() {
        btn_cancel.setOnClickListener {
            cancelListener?.invoke()
            dismiss()
        }
        btn_confirm.setOnClickListener {
            callBack(et_content.text?.toString())
        }
    }

    override fun initView() {
        tv_title.text = title
        et_content.hint = hint
        et_content.setText(text)
        et_content.setSelection(text.length)
        et_content.filters = arrayOf(MaxLengthWithEmojiFilter(maxLength))
    }

    override fun show() {
        super.show()
        et_content.postDelayed({
            showSoftKeyBoard()
        }, 300)

    }

    override fun dismiss() {
        super.dismiss()
        hideSoftKeyBoard()
    }


    private fun showSoftKeyBoard() {
        et_content.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et_content, InputMethodManager.SHOW_IMPLICIT)

    }

    private fun hideSoftKeyBoard() {
        et_content.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_content.windowToken, 0)
    }

}