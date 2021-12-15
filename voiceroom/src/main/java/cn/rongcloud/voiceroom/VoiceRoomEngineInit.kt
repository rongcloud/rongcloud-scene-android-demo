/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom

import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Log
import cn.rongcloud.annotation.AutoInit
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.utils.VMLog
import com.rongcloud.common.AppConfig
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.init.ModuleInit
import com.rongcloud.common.utils.AccountStore
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
            RCVoiceRoomEngine.getInstance().initWithAppKey(application, AppConfig.APP_KEY) {
                if (!AccountStore.getImToken().isNullOrEmpty()) {
                    Log.e(TAG, "onInit: mToken= " + AccountStore.getImToken())
                    android.os.Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                        override fun run() {
                            RCVoiceRoomEngine.getInstance().connectWithToken(
                                AccountStore.getImToken(),
                                object : RCVoiceRoomCallback {
                                    override fun onError(code: Int, message: String?) {
                                        application.showToast("RTC 服务器连接失败,请重新登录")
                                        AccountStore.logout()
                                    }

                                    override fun onSuccess() {
                                        VMLog.d(TAG, "connectWithToken success")
                                    }

                                })
                        }
                    }, 200)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onInit: e = " + e)
        }

    }
}