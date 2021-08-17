/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import cn.rong.combusis.umeng.RcUmEvent
import cn.rong.combusis.umeng.UmengHelper
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.IHomeView
import cn.rongcloud.voiceroomdemo.mvp.presenter.HomePresenter
import cn.rongcloud.voiceroomdemo.ui.dialog.UserInfoDialog
import com.rongcloud.common.base.BaseActivity
import com.rongcloud.common.extension.loadPortrait
import com.rongcloud.common.extension.ui
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import io.rong.callkit.DialActivity
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCoreListener
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_action_right_button_message.view.*
import kotlinx.android.synthetic.main.layout_portrait.*
import javax.inject.Inject

private const val CODE_SETTING_REQUEST = 10000

private const val TAG = "HomeActivity"

@HiltBinding(value = IHomeView::class)
@AndroidEntryPoint
class HomeActivity : BaseActivity(), IHomeView,
    IRongCoreListener.OnReceiveMessageListener {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var presenter: HomePresenter

    private var userInfoDialog: UserInfoDialog? = null


    override fun getContentView(): Int = R.layout.activity_home

    override fun initView() {
        iv_voice_room.setOnClickListener {
            UmengHelper.get().event(RcUmEvent.VoiceRoom)
            VoiceRoomListActivity.startActivity(this)
        }

        iv_video_call.setOnClickListener {
            UmengHelper.get().event(RcUmEvent.VideoCall)
            DialActivity.openDilapadPage(this, true)
        }
        iv_audio_call.setOnClickListener {
            UmengHelper.get().event(RcUmEvent.AudioCall)
            DialActivity.openDilapadPage(this, false)
        }
    }

    override fun getActionTitle(): CharSequence? {
        return null
    }

    val portrait: CircleImageView by lazy {
        return@lazy LayoutInflater.from(this)
            .inflate(R.layout.layout_portrait, null) as CircleImageView
    }

    override fun getLeftActionButton(): View? {
        portrait.setOnClickListener {
            SettingActivity.startActivity(this, CODE_SETTING_REQUEST)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_SETTING_REQUEST && resultCode == Activity.RESULT_OK) {
            portrait.loadPortrait(AccountStore.getUserPortrait() ?: "")
        }
    }

    override fun onReceived(message: Message?, p1: Int): Boolean {
        refreshUnreadMessageCount()
        return true
    }


}