/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseActivity
import com.rongcloud.common.extension.showToast
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ILoginView
import cn.rongcloud.voiceroomdemo.mvp.presenter.LoginPresenter
import cn.rongcloud.voiceroomdemo.webview.ActCommentWeb
import com.rongcloud.common.extension.ui
import com.rongcloud.common.utils.UIKit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

private const val TAG = "LoginActivity"

@HiltBinding(value = ILoginView::class)
@AndroidEntryPoint
class LoginActivity : BaseActivity(), ILoginView {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenter: LoginPresenter

    private var getVerificationCodeCountDownTimer: CountDownTimer? = null

    override fun getContentView(): Int = R.layout.activity_login


    override fun initView() {
        btn_login.isEnabled = false
        btn_get_verification_code.setOnClickListener {
            if (et_phone_number.text.isNullOrBlank()) {
                showToast(R.string.please_input_phone_number)
                return@setOnClickListener
            }
            presenter.getVerificationCode(et_phone_number.text.toString())
        }
        et_verification_code.addTextChangedListener {
            btn_login.isEnabled =
                !it.isNullOrBlank() && it.length >= 6 && !et_phone_number.text.isNullOrBlank()
        }
        btn_login.setOnClickListener {
            presenter.login(et_phone_number.text.toString(), et_verification_code.text.toString())
        }
    }

    override fun initData() {
        var style = SpannableStringBuilder()
        style.append("且表示同意《注册条款》")
        style.setSpan(
            ForegroundColorSpan(Color.parseColor("#0099FF")), 5, 11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        bottom_info.text = style
        bottom_info.setOnClickListener { view ->
            //注册条款
            Log.e(TAG, "注册条款")
            ActCommentWeb.openCommentWeb(
                this@LoginActivity,
                "file:///android_asset/agreement_zh.html", "注册条款"
            )
        }
        var vs = UIKit.getVerName()
        bottom_version.text = "融云 RTC ${vs}"
    }

    override fun setNextVerificationDuring(time: Long) {
        ui {
            et_verification_code.requestFocus()
            btn_get_verification_code.isEnabled = false
            getVerificationCodeCountDownTimer = object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    btn_get_verification_code.text = String.format(
                        getString(R.string.verification_code_send_already),
                        millisUntilFinished / 1000
                    )
                }

                override fun onFinish() {
                    btn_get_verification_code.text = getString(R.string.get_verification_code_again)
                    btn_get_verification_code.isEnabled = true
                }
            }.apply {
                start()
            }
        }

    }

    override fun onLoginSuccess() {
        ui {
            HomeActivity.startActivity(this)
            finish()
        }
    }


    override fun showNormal() {

    }


    override fun onLogout() {

    }


}