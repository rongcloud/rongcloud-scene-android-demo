/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import cn.rongcloud.annotation.AutoInit
import com.rongcloud.common.AppConfig
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.init.ModuleInit
import com.rongcloud.common.utils.AccountStore
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/08/05
 * 初始化语聊房引擎的相关配置
 */
private const val TAG = "VoiceRoomEngineInit"

@AutoInit
class VoiceRoomEngineInit @Inject constructor() : ModuleInit {
    override fun getPriority(): Int {
        return 1000
    }

    override fun getName(context: Context): String = "VoiceRoomEngineInit"

    override fun onInit(application: Application) {
        Log.d(TAG, "onInit: ")
        try {
            RongIM.init(application, AppConfig.APP_KEY, false)
        } catch (e: Exception) {
            Log.e(TAG, "onInit: e = " + e)
        }
        if (!AccountStore.getImToken().isNullOrEmpty()) {
            Log.e(TAG, "onInit: mToken= " + AccountStore.getImToken())
            var token = AccountStore.getImToken()
            android.os.Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                override fun run() {
                    RongIM.connect(token, object : RongIMClient.ConnectCallback() {
                        override fun onSuccess(t: String?) {
                        }

                        override fun onError(e: RongIMClient.ConnectionErrorCode?) {
                            application.showToast("RTC 服务器连接失败,请重新登录")
                            AccountStore.logout()
                        }

                        override fun onDatabaseOpened(code: RongIMClient.DatabaseOpenStatus?) {
                        }
                    })
                }
            }, 200)
        }
    }
}