/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDexApplication
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.IBaseView
import cn.rongcloud.voiceroomdemo.common.showToast
import cn.rongcloud.voiceroomdemo.mvp.activity.LauncherActivity
import cn.rongcloud.voiceroomdemo.mvp.activity.LoginActivity
import cn.rongcloud.voiceroomdemo.utils.AudioManagerUtil
import cn.rongcloud.voiceroomdemo.utils.CrashCollectHandler
import cn.rongcloud.voiceroomdemo.utils.RCChatRoomMessageManager
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiProvider
import com.vanniktech.emoji.emoji.EmojiCategory
import com.vanniktech.emoji.ios.category.*
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.rong.imlib.RongCoreClient
import io.rong.imlib.RongIMClient
import kotlin.properties.Delegates

/**
 * @author gusd
 * @Date 2021/06/07
 */
private const val TAG = "MyApp"

class MyApp : MultiDexApplication() {

    private val activityList: ArrayList<Activity> = arrayListOf()

    companion object {
        var context: Context by Delegates.notNull()
            private set

        // TODO: 2021/7/23 请将 APP_KEY 设为融云官网申请的 appkey
        const val APP_KEY: String =

        var instance: MyApp by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

        // 替换为自己申请的友盟 appkey
        UMConfigure.init(
            this,
            ,
            ,
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)

        RCVoiceRoomEngine.getInstance().initWithAppKey(this, APP_KEY)
        RCChatRoomMessageManager.registerMessageTypes()

        EmojiManager.install(MyEmojiProvider())

        RxJavaPlugins.setErrorHandler {
            Log.e(TAG, "RxJavaOnError: ", it)
        }


        CrashCollectHandler(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                MobclickAgent.onResume(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                MobclickAgent.onPause(activity)
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })

        RongIMClient.setConnectionStatusListener { status ->
            if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                showToast("当前账号已在其他设备登录，请重新登录")
                AccountStore.logout()
            }
        }

        AccountStore.obLogoutSubject().subscribe {
            try {
                activityList.lastOrNull()?.run {
                    if(this !is LoginActivity && this !is LauncherActivity) {
                        LoginActivity.startActivity(this)
                    }
                    activityList.forEach { activity ->
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

        AudioManagerUtil.init(this)

        if (!AccountStore.getImToken().isNullOrEmpty()) {
            RCVoiceRoomEngine.getInstance().connectWithToken(
                context as Application,
                AccountStore.getImToken(),
                object : RCVoiceRoomCallback {
                    override fun onError(code: Int, message: String?) {
                        context.showToast("RTC 服务器连接失败,请重新登录")
                        AccountStore.logout()
                    }

                    override fun onSuccess() {

                    }

                })
        }
    }




    fun finishAllActivity() {
        activityList.forEach {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }

    internal class MyEmojiProvider : EmojiProvider {
        override fun getCategories(): Array<EmojiCategory> {
            return arrayOf(
                SmileysAndPeopleCategory(),
                AnimalsAndNatureCategory(),
                FoodAndDrinkCategory(),
                ActivitiesCategory(),
                TravelAndPlacesCategory(),
                ObjectsCategory(),
                SymbolsCategory()
            )
        }

    }

}