/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common

/**
 * @author gusd
 * @Date 2021/08/02
 */
object AppConfig {
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

    fun initConfig(
        appKey: String,
        UMAppKey: String?,
        channelName: String?,
        baseServerAddress: String
    ) {
        APP_KEY = appKey
        UM_APP_KEY = UMAppKey
        CHANNEL_NAME = channelName
        BASE_SERVER_ADDRESS = baseServerAddress
    }


}