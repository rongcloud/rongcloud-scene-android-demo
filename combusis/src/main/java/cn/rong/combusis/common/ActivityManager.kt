/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author gusd
 * @Date 2021/08/03
 */
@Singleton
class ActivityManager @Inject constructor(@ApplicationContext val applicationContext: Context) {

    // 全局的 Activity 管理栈
    val activityList: ArrayList<Activity> = arrayListOf()

    @Inject
    fun init() {
        (applicationContext as Application).registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }

        })
    }


    fun finishAllActivity() {
        activityList.forEach {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }
}