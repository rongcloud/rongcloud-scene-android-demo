/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net

import cn.rongcloud.voiceroom.net.api.ARoomApi
import cn.rongcloud.voiceroom.net.api.GiftApiService
import cn.rongcloud.voiceroom.net.api.MusicApiService
import com.rongcloud.common.net.RetrofitManager

/**
 * @author gusd
 * @Date 2021/06/07
 */
private const val TAG = "RetrofitManager"

object VoiceRoomNetManager {

    val aRoomApi: ARoomApi by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit().create(ARoomApi::class.java)
    }

    val musicService: MusicApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit().create(MusicApiService::class.java)
    }

    val giftService: GiftApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit().create(GiftApiService::class.java)
    }
}