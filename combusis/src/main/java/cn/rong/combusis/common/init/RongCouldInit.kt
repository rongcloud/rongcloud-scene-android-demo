/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.init

import android.app.Application
import android.content.Context
import android.util.Log
import cn.rongcloud.annotation.AutoInit
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.utils.AccountStore
import io.rong.imkit.IMCenter
import io.rong.imlib.RongIMClient
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/08/03
 * 融云相关基础业务模块
 */
private const val TAG = "RongCouldInit"

@AutoInit
class RongCouldInit @Inject constructor() : ModuleInit {
    override fun getPriority(): Int {
        return 100
    }

    override fun getName(context: Context): String = "RongCloudInit"

    override fun onInit(application: Application) {
        Log.d(TAG, "onInit: ")
        IMCenter.getInstance().addConnectionStatusListener {
            Log.d(TAG, "onInit: ConnectionStatusListener")
            if (it == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                Log.d(TAG, "onInit: KICKED_OFFLINE_BY_OTHER_CLIENT")
                application.showToast("当前账号已在其他设备登录，请重新登录")
                AccountStore.logout()
            }
        }
    }

}