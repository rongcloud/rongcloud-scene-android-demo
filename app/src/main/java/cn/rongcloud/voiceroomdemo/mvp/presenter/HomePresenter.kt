/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.presenter

import android.content.Context
import android.net.Uri
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.IHomeView
import cn.rongcloud.voiceroomdemo.mvp.model.FileModel
import cn.rongcloud.voiceroomdemo.net.RetrofitManager
import cn.rongcloud.voiceroomdemo.net.api.bean.request.UpdateUserInfoRequestBean
import cn.rongcloud.voiceroomdemo.utils.RealPathFromUriUtils

/**
 * @author gusd
 * @Date 2021/06/04
 */
private const val TAG = "HomePresenter"

class HomePresenter(val view: IHomeView, val context: Context) :
    BaseLifeCyclePresenter<IHomeView>(view) {

    override fun onDestroy() {
    }

    fun modifyUserInfo(userName: String, selectedPicPath: Uri?) {
        view.showWaitingDialog()
        if (selectedPicPath!=null) {
            addDisposable(FileModel.imageUpload(RealPathFromUriUtils.getRealPathFromUri(context,selectedPicPath), context).flatMap { url ->
                return@flatMap RetrofitManager.commonService.updateUserInfo(
                    UpdateUserInfoRequestBean(
                        userName,
                        url
                    )
                )
            }.subscribe({ respond ->
                val accountInfo = AccountStore.getAccountInfo()
                    .copy(userName = respond.data?.name, portrait = respond.data?.portrait)
                AccountStore.saveAccountInfo(accountInfo)
                view.modifyInfoSuccess()
                view.hideWaitingDialog()
            }, { t ->
                view.showError(-1, t.message)
            }))
        } else {
            addDisposable(
                RetrofitManager.commonService.updateUserInfo(
                    UpdateUserInfoRequestBean(
                        userName,
                        null
                    )
                ).subscribe({ r ->
                    var accountInfo = AccountStore.getAccountInfo()
                        .copy(userName = r.data?.name)
                    AccountStore.saveAccountInfo(accountInfo)
                    view.modifyInfoSuccess()
                    view.hideWaitingDialog()
                }, { t ->
                    view.showError(-1, t.message)
                })
            )
        }
    }

    fun logout() {
        AccountStore.logout()
    }
}