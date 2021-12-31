/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import cn.rong.combusis.R

/**
 * @author gusd
 * @Date 2021/06/08
 */
abstract class BaseDialog(
    context: Context,
    private val layoutId: Int,
    private val touchOutSideDismiss: Boolean
) : Dialog(context, R.style.CustomDialog) {

    protected lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(touchOutSideDismiss)
        rootView = LayoutInflater.from(context).inflate(layoutId, null, false)
        setContentView(rootView)
        initView()
        initListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val window: Window? = window
        if (hasFocus && window != null) {
            val decorView: View = window.decorView
            if (decorView.height == 0 || decorView.width == 0) {
                decorView.requestLayout()
                Log.d("TAG", "布局异常，重新布局")
            }
        }
    }


    abstract fun initListener()

    abstract fun initView()

}