/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net

import cn.rongcloud.voiceroomdemo.net.api.CommonApiService
import com.rongcloud.common.net.RetrofitManager

/**
 * @author gusd
 * @Date 2021/08/06
 */
object CommonNetManager {
    val commonService: CommonApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        RetrofitManager.getRetrofit().create(CommonApiService::class.java)
    }
}