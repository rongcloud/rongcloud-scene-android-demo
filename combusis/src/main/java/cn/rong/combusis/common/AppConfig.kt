/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common

import android.text.TextUtils
import com.kit.utils.Logger

/**
 * @author gusd
 * @Date 2021/08/02
 */
object AppConfig {
    private final val TAG = AppConfig.javaClass.simpleName

    public var APP_KEY: String? = null
        private set

    /**
     * 友盟统计所使用的 APP_KEY
     */
    public var UM_APP_KEY: String? = null
        private set

    /**
     * 友盟统计所使用的渠道名
     */
    public var CHANNEL_NAME: String? = null
        private set

    /**
     * 服务器地址
     */
    public var BASE_SERVER_ADDRESS: String? = null
        private set

    /**
     * 访问测试服务器的token,需要申请
     */
    public var BUSINESS_TOKEN: String? = null
        private set
        get() {
            if (TextUtils.isEmpty(field)) {
                Logger.e(TAG, "请配置businessToken，https://rcrtc-api.rongcloud.net/code")
                return ""
            }
            return field
        }

    fun initConfig(
        appKey: String,
        UMAppKey: String?,
        channelName: String?,
        baseServerAddress: String,
        businessToken: String
    ) {
        APP_KEY = appKey
        UM_APP_KEY = UMAppKey
        CHANNEL_NAME = channelName
        BASE_SERVER_ADDRESS = baseServerAddress
        BUSINESS_TOKEN = businessToken
    }


}