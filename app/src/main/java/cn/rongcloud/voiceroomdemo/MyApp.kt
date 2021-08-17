/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import cn.rongcloud.voiceroomdemo.mvp.activity.LauncherActivity
import cn.rongcloud.voiceroomdemo.mvp.activity.LoginActivity
import com.rongcloud.common.ActivityManager
import com.rongcloud.common.AppConfig
import com.rongcloud.common.ModuleManager
import com.rongcloud.common.base.IBaseView
import com.rongcloud.common.utils.AccountStore
import com.rongcloud.common.utils.UIKit
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * @author gusd
 * @Date 2021/06/07
 */
private const val TAG = "MyApp"

@HiltAndroidApp
class MyApp : MultiDexApplication() {

    @Inject
    lateinit var moduleManager: ModuleManager

    @Inject
    lateinit var activityManager: ActivityManager

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        // 设置基本初始化参数
        AppConfig.initConfig(
            BuildConfig.APP_KEY,
            BuildConfig.UM_APP_KEY,
            "rcrtc",
            BuildConfig.BASE_SERVER_ADDRES
        )

        var process = UIKit.getCurrentProcessName()
        Log.d(TAG, "process : $process")
        if (applicationContext.packageName != process) {
            // 避免过度初始化
            return
        }
        // 初始化所有模块，通话参数可修改初始化优先级，获取初始化总的模块数和初始进度
        moduleManager.init(this, { name, priority ->
            Log.d(TAG, "initModule: $name")
            priority
        })
        // 退出或者账号被挤下线的流程
        AccountStore.obLogoutSubject().subscribe {
            try {
                activityManager.activityList.lastOrNull()?.run {
                    if (this !is LoginActivity && this !is LauncherActivity) {
                        LoginActivity.startActivity(this)
                    }
                    activityManager.activityList.forEach { activity ->
                        if (activity !is LoginActivity && activity !is LauncherActivity && !activity.isFinishing) {
                            if (activity is IBaseView) {
                                Log.d(TAG, "obLogoutSubject: ")
                                activity.onLogout()
                            } else if (activity is LauncherActivity) {
                                // do nothing
                            } else {
                                activity.finish()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "obLogoutSubject: ", e)
            }
        }
    }


    companion object {
        var context: Context by Delegates.notNull()
            private set
        val APP_KEY by lazy {
            AppConfig.APP_KEY
        }

        var instance: MyApp by Delegates.notNull()
    }

}