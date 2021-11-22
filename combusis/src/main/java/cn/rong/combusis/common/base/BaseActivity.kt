/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.base

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import cn.rong.combusis.R
import cn.rong.combusis.common.base.PermissionActivity
import com.rongcloud.common.extension.setAndroidNativeLightStatusBar
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.extension.ui
import kotlinx.android.synthetic.main.layout_custom_action_bar.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author gusd
 * @Date 2021/06/04
 */
private val DEFAULT_EMPTY_VIEW = R.layout.layout_empty
private val DEFAULT_ERROR_VIEW = R.layout.layout_error
private val DEFAULT_LOADING_VIEW = R.layout.layout_loading

abstract class BaseActivity : PermissionActivity(),
    IBaseView, EasyPermissions.PermissionCallbacks {

    private var isFront = false
    protected lateinit var mRootView: View

    private var waitingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(javaClass.name, "onCreate: ")
        setAndroidNativeLightStatusBar(isLightThemeActivity())
        beforeInitView()
        super.onCreate(savedInstanceState)
        // initView initData 移动赋予权限onAccept()后,避免因权限导致的一些异常
        // setContentView(LayoutInflater.from(this).inflate(getContentView(), null, false).apply {
        //     mRootView = this
        // })
        // presenter = initPresenter()
        // afterInitPresenter()
        //  supportActionBar?.let {
        //   initActionBar(it)
        // }
        // initView()
        //initData()
    }

    override fun onSetPermissions(): Array<String>? {
        return null
    }

    override fun onAccept(accept: Boolean) {
        if (accept) {
            setContentView(LayoutInflater.from(this).inflate(getContentView(), null, false).apply {
                mRootView = this
            })
            supportActionBar?.let {
                initActionBar(it)
            }
            initView()
            initData()
        } else {
            showToast("请赋予必要权限！")
            finish()
        }
    }


    open fun isLightThemeActivity(): Boolean {
        return this.applicationContext.resources.configuration.uiMode == 0x11
    }

    open fun initActionBar(actionBar: ActionBar) {
        actionBar.elevation = 0f
        actionBar.setCustomView(R.layout.layout_custom_action_bar)
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        getLeftActionButton()?.let {
            fl_left_button.addView(
                it,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        getRightActionButton()?.let {
            fl_right_button.addView(
                it,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        tv_title.text = getActionTitle() ?: ""
    }

    open fun getLeftActionButton(): View? {
        return null
    }

    open fun getRightActionButton(): View? {
        return null
    }

    open fun getActionTitle(): CharSequence? {
        return title
    }


    abstract fun getContentView(): Int

    open fun beforeInitView() {

    }

    abstract fun initView()

    abstract fun initData()


    override fun showWaitingDialog() {
        ui {
            if (waitingDialog == null) {
                waitingDialog =
                    AlertDialog.Builder(this, R.style.TransparentDialog).create().apply {
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

    override fun showEmpty() {
        // TODO: 2021/6/11  
    }

    override fun showLoadingView() {
    }

    override fun showNormal() {
        // TODO: 2021/6/11  
    }

    override fun onResume() {
        super.onResume()
        Log.d(javaClass.name, "onResume: ")
        isFront = true
    }

    override fun onPause() {
        super.onPause()
        Log.d(javaClass.name, "onPause: ")
        isFront = false
    }

    override fun onStart() {
        super.onStart()
        Log.d(javaClass.name, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(javaClass.name, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        waitingDialog?.dismiss()
    }

    open fun needShowLoadingView(): Boolean {
        return false
    }

    open fun getEmptyLayout(): Int = DEFAULT_EMPTY_VIEW

    open fun getErrorLayout(): Int = DEFAULT_ERROR_VIEW

    open fun getLoadingLayout(): Int = DEFAULT_LOADING_VIEW

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onLogout() {
        finish()
    }

    fun showBackButton() {
        fl_left_button?.removeAllViews()
        val backButton = LayoutInflater.from(this).inflate(R.layout.layout_back_button, null, false)
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.CENTER
        fl_left_button.addView(backButton, lp)
        fl_left_button.setOnClickListener {
            finish()
        }
    }

    override fun showError(errorCode: Int, message: String?) {
        Log.e(this::class.java.name, "showError: $message")
        showToast(message)
    }

    override fun showError(message: String?) {
        showError(-1, message)
    }

    override fun showMessage(message: String?) {
        ui {
            message?.let {
                showToast(it)
            }
        }
    }
}