/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.*
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.IHomeView
import cn.rongcloud.voiceroomdemo.mvp.presenter.HomePresenter
import cn.rongcloud.voiceroomdemo.ui.dialog.UserInfoDialog
import cn.rongcloud.voiceroomdemo.utils.AudioEffectManager
import cn.rongcloud.voiceroomdemo.utils.LocalUserInfoManager
import de.hdodenhof.circleimageview.CircleImageView
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCoreListener
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_action_right_button_message.view.*
import kotlinx.android.synthetic.main.layout_portrait.*

private const val PICTURE_SELECTED_RESULT_CODE = 10001

private const val TAG = "HomeActivity"


class HomeActivity : BaseActivity<HomePresenter, IHomeView>(), IHomeView,
    IRongCoreListener.OnReceiveMessageListener {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }


    private var userInfoDialog: UserInfoDialog? = null

    override fun initPresenter(): HomePresenter = HomePresenter(this, this)

    override fun getContentView(): Int = R.layout.activity_home

    override fun initView() {
        iv_voice_room.setOnClickListener {
            VoiceRoomListActivity.startActivity(this)
        }

        iv_video_call.setOnClickListener {
            showToast("暂未开放")
        }
        iv_audio_call.setOnClickListener {
            showToast("暂未开放")
        }
        bg2.setColorFilter(Color.GRAY)
        bg2.alpha = 0.5f
        bg3.setColorFilter(Color.GRAY)
        bg3.alpha = 0.5f
    }

    override fun getActionTitle(): CharSequence? {
        return null
    }

    override fun getLeftActionButton(): View? {
        val portrait =
            LayoutInflater.from(this).inflate(R.layout.layout_portrait, null) as CircleImageView
        portrait.setOnClickListener {
            userInfoDialog = UserInfoDialog(this, {
                // 退出登录
                presenter.logout()
            }, { userName, selectedPicPath ->
                // 修改用户名和头像
                presenter.modifyUserInfo(userName, selectedPicPath)
            }, {
                // 进入头像选择界面
                startPicSelectActivity()
            })
            userInfoDialog?.show()
        }
        portrait.loadPortrait(AccountStore.getUserPortrait() ?: "")
        return portrait
    }

    override fun onLogout() {
        RCVoiceRoomEngine.getInstance().leaveRoom(object : RCVoiceRoomCallback {
            override fun onError(code: Int, message: String?) {
                Log.e(TAG, "onError: $code $message")
                logout()
            }

            override fun onSuccess() {
                Log.e(TAG, "onSuccess:")
                logout()
            }
        })
    }

    fun logout() {
        RCVoiceRoomEngine.getInstance().disConnect()
        super.onLogout()
    }

    override fun onDestroy() {
        super.onDestroy()
        userInfoDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        RCVoiceRoomEngine.getInstance().removeMessageReceiveListener(this)
    }

    override fun getRightActionButton(): View? {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.layout_action_right_button_message, null)
        with(view) {
            iv_right_btn.setImageResource(R.drawable.ic_message)
            setOnClickListener {
                RouteUtils.routeToSubConversationListActivity(
                    this@HomeActivity,
                    Conversation.ConversationType.PRIVATE,
                    "消息"
                )
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshUnreadMessageCount()
    }

    private fun refreshUnreadMessageCount() {
        RongIMClient.getInstance().getUnreadCount(object : RongIMClient.ResultCallback<Int>() {
            override fun onSuccess(number: Int) {
                ui {
                    findViewById<View>(R.id.tv_seat_order_operation_number)?.isVisible = number > 0
                }
            }

            override fun onError(p0: RongIMClient.ErrorCode?) {
            }

        }, Conversation.ConversationType.PRIVATE)
    }

    override fun initData() {
        LocalUserInfoManager.getMemberByUserId("")
        RCVoiceRoomEngine.getInstance().addMessageReceiveListener(this)
    }

    override fun modifyInfoSuccess() {
        ui {
            userInfoDialog?.dismiss()
            iv_portrait.loadPortrait(AccountStore.getUserPortrait() ?: "")
        }
    }

    override fun showNormal() {
    }


    private fun startPicSelectActivity() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICTURE_SELECTED_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_SELECTED_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            val selectImageUrl = data?.data
            selectImageUrl?.let {
                userInfoDialog?.setUserPortrait(it)
            }
        }
    }

    override fun onReceived(message: Message?, p1: Int): Boolean {
        refreshUnreadMessageCount()
        return true
    }


}