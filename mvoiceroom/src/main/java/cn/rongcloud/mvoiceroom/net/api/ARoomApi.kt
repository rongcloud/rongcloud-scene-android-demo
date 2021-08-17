/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.api

import cn.rongcloud.mvoiceroom.net.bean.request.*
import cn.rongcloud.mvoiceroom.net.bean.respond.*
import com.rongcloud.common.net.bean.SimpleRespondBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

/**
 * @author gusd
 * @Date 2021/08/06
 */
interface ARoomApi {
    @GET("/mic/room/list")
    fun getRoomList(@Query("page") page: Int, @Query("size") size: Int): Single<VoiceRoomListBean>

    @POST("/mic/room/create")
    fun createVoiceRoom(@Body createRoomRequestBean: CreateRoomRequestBean): Single<CreateRoomRespondBean>

    @GET("/mic/room/{roomId}/delete")
    fun deleteRoom(@Path("roomId") roomId: String): Single<SimpleRespondBean>

    @GET("/mic/room/{roomId}/members")
    fun getMembersList(@Path("roomId") roomId: String): Single<VoiceRoomMemberListBean>

    @GET("/mic/room/{id}")
    fun getVoiceRoomInfo(@Path("id") roomId: String): Single<VoiceRoomInfoBean>

    @POST("/mic/room/gag")
    fun muteUsersRequest(@Body muteUserRequestBean: MuteUserRequestBean): Single<SimpleRespondBean>

    @POST("/mic/room/{roomId}/gag/members")
    fun getMuteList(@Path("roomId") roomId: String): Single<MuteListBean>

    @PUT("/mic/room/private")
    fun setRoomPasswordRequest(@Body roomPasswordRequest: RoomPasswordRequest): Single<SimpleRespondBean>

    @PUT("/mic/room/background")
    fun setRoomBackgroundRequest(@Body roomBackgroundRequest: RoomBackgroundRequest): Single<SimpleRespondBean>

    @GET("/mic/room/{roomId}/manage/list")
    fun getAdminList(@Path("roomId") roomId: String): Single<VoiceRoomMemberListBean>

    @PUT("/mic/room/manage")
    fun setAdmin(@Body settingAdminRequest: SettingAdminRequest): Single<SimpleRespondBean>

    @PUT("/mic/room/name")
    fun setRoomName(@Body roomNameRequest: RoomNameRequest): Single<SimpleRespondBean>

    @PUT("/mic/room/setting")
    fun setRoomSetting(@Body roomSettingRequest: RoomSettingRequest): Single<SimpleRespondBean>

    @POST("/user/batch")
    fun getUserInfoList(@Body userIdList: UserIdList): Single<VoiceRoomMemberListBean>

    @GET("/mic/room/{roomId}/setting")
    fun getRoomSetting(@Path("roomId") roomId: String): Single<RoomSettingRespond>

}