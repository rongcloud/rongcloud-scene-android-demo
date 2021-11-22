/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.presenter

import androidx.appcompat.app.AppCompatActivity
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ILoginView
import cn.rongcloud.voiceroomdemo.mvp.model.LoginModel
import com.kit.cache.GsonUtil
import com.rongcloud.common.base.BaseLifeCyclePresenter
import com.rongcloud.common.net.ApiConstant
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/04
 */
@ActivityScoped
class LoginPresenter @Inject constructor(
    val view: ILoginView,
    private val loginModel: LoginModel,
    activity: AppCompatActivity
) :
    BaseLifeCyclePresenter(activity) {

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

                            if (bean.code == ApiConstant.REQUEST_SUCCESS_CODE) {
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
                        com.kit.utils.Logger.e(TAG, GsonUtil.obj2Json(bean))
                        if (null == bean) {
                        } else {
                            if (bean.code == ApiConstant.REQUEST_SUCCESS_CODE) {
                                AccountStore.saveAccountInfo(bean.data?.apply {
                                    this.phone = phoneNumber
                                })
                                if (!AccountStore.getImToken().isNullOrBlank()) {
                                    RCVoiceRoomEngine
                                        .getInstance()
                                        .connectWithToken(
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
                        }
                    }, { throwable ->
                        view.showError(-1, throwable.message)
                    })
            )
        }
    }
}