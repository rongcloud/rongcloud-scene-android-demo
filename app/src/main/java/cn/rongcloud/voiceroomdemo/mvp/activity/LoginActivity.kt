/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.core.widget.addTextChangedListener
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.BaseActivity
import cn.rongcloud.voiceroomdemo.common.showToast
import cn.rongcloud.voiceroomdemo.common.ui
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ILoginView
import cn.rongcloud.voiceroomdemo.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"

class LoginActivity : BaseActivity<LoginPresenter, ILoginView>(), ILoginView {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

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

    override fun initPresenter(): LoginPresenter {
        return LoginPresenter(this, this)
    }

    override fun onLogout() {

    }


}