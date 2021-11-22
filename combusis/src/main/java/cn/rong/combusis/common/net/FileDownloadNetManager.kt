/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.net

import com.rongcloud.common.net.api.DownloadFileApiService

/**
 * @author gusd
 * @Date 2021/08/06
 */
object FileDownloadNetManager {
    val downloadService: DownloadFileApiService by lazy {
        RetrofitManager.getDownloadRetrofit().create(DownloadFileApiService::class.java)
    }
}