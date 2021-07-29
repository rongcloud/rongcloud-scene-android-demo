/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.presenter

import android.app.Application
import android.content.Context
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroomdemo.MyApp
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ILoginView
import cn.rongcloud.voiceroomdemo.mvp.model.LoginModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author gusd
 * @Date 2021/06/04
 */
class LoginPresenter(val view: ILoginView, private val context: Context) :
    BaseLifeCyclePresenter<ILoginView>(view) {

    private val loginModel: LoginModel by lazy {
        LoginModel()
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    fun getVerificationCode(phoneNumber: String) {
        view.showWaitingDialog()
        GlobalScope.launch(Dispatchers.IO) {
            addDisposable(
                loginModel
                    .getVerificationCode(phoneNumber)
                    .doFinally {
                        view.hideWaitingDialog()
                    }
                    .subscribe({ bean ->
                        view.apply {

                            if (bean.code == 10000) {
                                setNextVerificationDuring(60 * 1000L)
                            } else {
                                view.showError(bean.code, bean.msg)
                            }
                        }

                    }, { throwable ->
                        view.showError(-1, throwable.message)
                    })
            )
        }

    }

    fun login(phoneNumber: String, verifyCode: String) {
        view.showWaitingDialog()
        GlobalScope.launch(Dispatchers.IO) {
            addDisposable(
                loginModel
                    .login(phoneNumber, verifyCode)
                    .doFinally {
                        view.hideWaitingDialog()
                    }
                    .subscribe({ bean ->
                        if (bean.code == 10000) {
                            AccountStore.saveAccountInfo(bean.data)
                            if (!AccountStore.getImToken().isNullOrBlank()) {
                                RCVoiceRoomEngine
                                    .getInstance()
                                    .connectWithToken(
                                        MyApp.context as Application,
                                        AccountStore.getImToken(),
                                        object : RCVoiceRoomCallback {
                                            override fun onError(code: Int, message: String?) {
                                                view.hideWaitingDialog()
                                                view.showError(code, message)
                                            }

                                            override fun onSuccess() {
                                                view.hideWaitingDialog()
                                                view.onLoginSuccess()
                                            }
                                        })
                            }
                        } else {
                            view.showError(bean.code, bean.msg)
                        }
                    }, { throwable ->
                        view.showError(-1, throwable.message)
                    })
            )
        }
    }
}