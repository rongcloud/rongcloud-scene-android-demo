/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.api

import cn.rongcloud.mvoiceroom.net.bean.request.AddMusicRequest
import cn.rongcloud.mvoiceroom.net.bean.request.DeleteMusicRequest
import cn.rongcloud.mvoiceroom.net.bean.request.MusicListRequest
import cn.rongcloud.mvoiceroom.net.bean.request.MusicOrderRequest
import cn.rongcloud.mvoiceroom.net.bean.respond.AddMusicRespond
import cn.rongcloud.mvoiceroom.net.bean.respond.MusicListRespond
import com.rongcloud.common.net.bean.SimpleRespondBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author gusd
 * @Date 2021/08/06
 */
interface MusicApiService {

    @POST("/mic/room/music/add")
    fun addMusic(@Body addMusicRequest: AddMusicRequest): Single<AddMusicRespond>

    /**
     * 修改音乐顺序 operation: up 上移，down 下移
     */
    @POST("/mic/room/music/move")
    fun modifyMusicOrder(
        @Body musicOrderRequest: MusicOrderRequest
    ): Single<SimpleRespondBean>

    @POST("/mic/room/music/list")
    fun getMusicList(@Body musicListRequest: MusicListRequest): Single<MusicListRespond>

    @POST("/mic/room/music/delete")
    fun musicDelete(@Body deleteMusicRequest: DeleteMusicRequest): Single<SimpleRespondBean>

}