/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.base

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import cn.rong.combusis.R
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.extension.ui

/**
 * @author gusd
 * @Date 2021/06/24
 */
abstract class BaseFragment(@LayoutRes val layoutId: Int) :
    Fragment(), IBaseView {
    protected lateinit var mRootView: View

    protected lateinit var mActivity: Activity

    private var waitingDialog: AlertDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = LayoutInflater.from(context).inflate(layoutId, container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initData()
    }

    open fun initData() {

    }

    open fun initListener() {

    }


    abstract fun initView()

    open fun getTitle(): String {
        return ""
    }

    override fun showEmpty() {
        // TODO: 2021/6/11
    }

    override fun showLoadingView() {
    }

    override fun showNormal() {
        // TODO: 2021/6/11
    }

    override fun showWaitingDialog() {
        ui {
            if (waitingDialog == null) {
                waitingDialog =
                    AlertDialog.Builder(mActivity, R.style.TransparentDialog).create().apply {
                        window?.setBackgroundDrawable(ColorDrawable())
                        setCancelable(false)
                        setCanceledOnTouchOutside(false)
                    }
            }
            waitingDialog?.show()
            waitingDialog?.setContentView(R.layout.layout_waiting_dialog)
        }

    }

    override fun hideWaitingDialog() {
        ui {
            waitingDialog?.dismiss()
        }
    }

    override fun onLogout() {
    }

    override fun showError(errorCode: Int, message: String?) {
        Log.e(this::class.java.name, "showError: $message")
        mActivity.showToast(message)
    }

    override fun showError(message: String?) {
        showError(-1, message)
    }

    override fun showMessage(message: String?) {
        ui {
            message?.let {
                mActivity.showToast(it)
            }
        }
    }
}