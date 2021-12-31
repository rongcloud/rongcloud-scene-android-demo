/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.net.api

import cn.rongcloud.voice.net.bean.request.SendGiftsRequest
import cn.rongcloud.voice.net.bean.respond.GiftListRespond
import com.rongcloud.common.net.bean.SimpleRespondBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * @author gusd
 * @Date 2021/08/06
 */
interface GiftApiService {

    @POST("/mic/room/gift/add")
    fun sendGifts(@Body sendGiftsRequest: SendGiftsRequest): Single<SimpleRespondBean>

    @GET("/mic/room/{roomId}/gift/list")
    fun getGiftList(@Path("roomId") roomId: String): Single<GiftListRespond>

}