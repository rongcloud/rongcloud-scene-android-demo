/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.net.api

import io.reactivex.rxjava3.core.Flowable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @author gusd
 * @Date 2021/07/11
 */
interface DownloadFileApiService {
    @GET
    fun downloadFile(@Url path: String): Flowable<ResponseBody>
}