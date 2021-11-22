/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.init

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import cn.rongcloud.annotation.AutoInit
import cn.rongcloud.voiceroomdemo.BuildConfig
import com.rongcloud.common.AppConfig
import com.rongcloud.common.extension.isNotNullOrEmpty
import com.rongcloud.common.init.ModuleInit
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/08/03
 * 友盟功能的初始化,如果不需要友盟的统计功能，直接删除此类即可
 */

@AutoInit
class UMengInit @Inject constructor() : ModuleInit {
    override fun getPriority(): Int = Int.MAX_VALUE

    override fun getName(context: Context): String = "UMeng init"

    override fun onInit(application: Application) {
        if (AppConfig.UM_APP_KEY.isNotNullOrEmpty()) {
            UMConfigure.preInit(
                application,
                BuildConfig.UM_APP_KEY,
                AppConfig.CHANNEL_NAME
            )
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)
            UMConfigure.setLogEnabled(BuildConfig.DEBUG)
            UMConfigure.init(
                application,
                BuildConfig.UM_APP_KEY,
                AppConfig.CHANNEL_NAME,
                UMConfigure.DEVICE_TYPE_PHONE,
                null
            )
        }


        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                if (AppConfig.UM_APP_KEY.isNotNullOrEmpty()) {
                    MobclickAgent.onResume(activity)
                }
            }

            override fun onActivityPaused(activity: Activity) {
                if (AppConfig.UM_APP_KEY.isNotNullOrEmpty()) {
                    MobclickAgent.onPause(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })
    }
}