/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.widget.addTextChangedListener
import cn.rong.combusis.common.utils.UIKit
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ILoginView
import cn.rongcloud.voiceroomdemo.mvp.presenter.LoginPresenter
import cn.rongcloud.voiceroomdemo.webview.ActCommentWeb
import com.rongcloud.common.base.BaseActivity
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.extension.ui
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

private const val TAG = "LoginActivity"

@HiltBinding(value = ILoginView::class)
@AndroidEntryPoint
class LoginActivity : BaseActivity(), ILoginView {

    var checked: Boolean = false

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
            if (!checked) {
                showToast("请勾选同意注册条款")
                return@setOnClickListener
            }
            presenter.getVerificationCode(et_phone_number.text.toString())
        }
        et_verification_code.addTextChangedListener {
            btn_login.isEnabled =
                !it.isNullOrBlank() && it.length >= 6 && !et_phone_number.text.isNullOrBlank()
        }
        btn_login.setOnClickListener {
            if (!checked) {
                showToast("请勾选同意注册条款")
                return@setOnClickListener
            }
            presenter.login(et_phone_number.text.toString(), et_verification_code.text.toString())
        }
    }

    override fun initData() {
        iv_checked.isSelected = checked;
        iv_checked.setOnClickListener { view ->
            view.isSelected = !view.isSelected
            checked = view.isSelected
        }
        var style = SpannableStringBuilder()
        style.append("同意《注册条款》和《隐私政策》并新登录即注册开通融云开发者账号")
        style.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                ActCommentWeb.openCommentWeb(
                    this@LoginActivity,
                    "file:///android_asset/agreement_zh.html", "注册条款"
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#0099FF")
            }
        }, 2, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        style.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                ActCommentWeb.openCommentWeb(
                    this@LoginActivity,
                    "file:///android_asset/privacy_zh.html", "隐私政策"
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#0099FF")
            }
        }, 9, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        bottom_info.text = style
        bottom_info.movementMethod = LinkMovementMethod.getInstance()

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