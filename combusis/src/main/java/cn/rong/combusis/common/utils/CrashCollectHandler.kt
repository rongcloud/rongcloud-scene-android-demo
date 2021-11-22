/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.rongcloud.common.ActivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

/**
 * @author gusd
 * @Date 2021/06/30
 */
private const val TAG = "CrashCollectHandler"

@Singleton
class CrashCollectHandler @Inject constructor(
    @ApplicationContext val context: Context,
    private val activityManager: ActivityManager
) :
    Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    init {
        // 获取系统默认的UncaughtException处理器
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)

    }


    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (!handleException(e) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(t, e)
        } else {
            try {
                //给Toast留出时间
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            //退出程序
            activityManager.finishAllActivity()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)

        }

    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        Thread {
            Looper.prepare()
            Toast.makeText(context, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT).show()
            Looper.loop()
        }.start()

        return true
    }
}