/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis

import android.util.Log
import cn.rongcloud.rtc.api.RCRTCEngine
import com.rongcloud.common.ModuleManager
import com.rongcloud.common.extension.showToast

/**
 * @author gusd
 * @Date 2021/07/07
 */
const val MUSIC_ATMOSPHERE_ENTER = "intro_effect.mp3"
const val MUSIC_ATMOSPHERE_CLAP = "clap_effect.mp3"
const val MUSIC_ATMOSPHERE_CHEER = "cheering_effect.mp3"


val MUSIC_ATMOSPHERE_NAME_LIST = arrayListOf<String>(
    MUSIC_ATMOSPHERE_ENTER,
    MUSIC_ATMOSPHERE_CLAP,
    MUSIC_ATMOSPHERE_CHEER
)

private const val TAG = "AudioEffectManager"

object AudioEffectManager {
    fun init() {
        MUSIC_ATMOSPHERE_NAME_LIST.forEachIndexed { index, name ->
            try {
                RCRTCEngine.getInstance().audioEffectManager
                    .preloadEffect(
                        getMusicAtmospherePathByName(name), index
                    ) { result ->
                        if (result == -1) {
                            ModuleManager.applicationContext.showToast("音效文件加载失败")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "init: ", e)
            }
        }
    }

    fun getMusicAtmosphereIndexByName(name: String): Int {
        return MUSIC_ATMOSPHERE_NAME_LIST.indexOf(name)
    }

    fun getMusicAtmospherePathByName(name: String): String {
        return "file:///android_asset/AudioEffect/$name"
    }

    fun playEffect(name: String) {
        try {
            RCRTCEngine.getInstance().audioEffectManager.stopAllEffects()
            RCRTCEngine.getInstance().audioEffectManager.playEffect(
                getMusicAtmosphereIndexByName(name),
                1,
                50
            )
        } catch (e: Exception) {
            Log.e(TAG, "playEffect: ", e)
        }
    }

    fun playEffect(index: Int) {
        try {
            RCRTCEngine.getInstance().audioEffectManager.stopAllEffects()
            RCRTCEngine.getInstance().audioEffectManager.playEffect(
                getMusicAtmosphereIndexByName(MUSIC_ATMOSPHERE_NAME_LIST[index]),
                1,
                50
            )
        } catch (e: Exception) {
            Log.e(TAG, "playEffect: ", e)
        }
    }
}