/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.dialog

import android.content.Context
import android.net.Uri
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.*
import kotlinx.android.synthetic.main.layout_user_info_popup_window.*

/**
 * @author gusd
 * @Date 2021/06/08
 */
class UserInfoDialog(
    context: Context,
    private val logoutBlock: (() -> Unit)? = null,
    private val saveBlock: ((userName: String, portrait: Uri?) -> Unit)? = null,
    private val showPictureSelectBlock: (() -> Unit)? = null
) : BaseDialog(
    context, R.layout.layout_user_info_popup_window,
    false
) {

    private var selectedPicPath: Uri? = null

    override fun initListener() {

    }

    override fun initView() {
        iv_portrait.loadPortrait(AccountStore.getUserPortrait() ?: "")
        AccountStore.getUserName()?.let {
            et_user_name.setText(it)
        }
        iv_close.setOnClickListener {
            dismiss()
        }

        tv_save_user_info.setOnClickListener {
            if (et_user_name.text.isNullOrBlank()) {
                context.showToast(getString(R.string.username_can_not_be_empty))
                return@setOnClickListener
            }
            saveBlock?.invoke(et_user_name.text.toString(), selectedPicPath)
        }

        iv_portrait.setOnClickListener {
            showPictureSelectBlock?.invoke()
        }

        tv_logout.setOnClickListener {
            logoutBlock?.invoke()
        }

    }

    fun setUserPortrait(picturePath: Uri) {
        selectedPicPath = picturePath
        iv_portrait.loadLocalPortrait(picturePath)
    }
}