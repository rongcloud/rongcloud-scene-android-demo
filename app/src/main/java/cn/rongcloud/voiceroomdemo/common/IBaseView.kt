/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.common

/**
 * @author gusd
 * @Date 2021/06/04
 */
interface IBaseView {


    fun showWaitingDialog()

    fun hideWaitingDialog()

    fun showLoadingView()

    fun showNormal()

    fun showEmpty()

    fun showError(errorCode: Int, message: String? = null)

    fun showError(message: String?)

    fun onLogout()

    fun showMessage(message: String?)

}