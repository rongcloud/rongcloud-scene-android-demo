/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api

import cn.rongcloud.voiceroomdemo.net.api.bean.request.*
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @author gusd
 * @Date 2021/06/07
 */
interface CommonApiService {

    @POST("/user/sendCode")
    fun getVerificationCode(@Body phoneNumber: GetVerificationCode): Single<VerificationCodeRespondBean>

    @POST("/user/login")
    fun login(@Body loginRequestBean: LoginRequestBean): Single<LoginRespondBean>

    @GET("/mic/room/list")
    fun getRoomList(@Query("page") page: Int, @Query("size") size: Int): Single<VoiceRoomListBean>

    @POST("/file/upload")
    @Multipart
    fun fileUpload(@Part body: MultipartBody.Part): Single<FileUploadRespond>

    @POST("/user/update")
    fun updateUserInfo(@Body updateUserInfoRequestBean: UpdateUserInfoRequestBean): Single<UpdateUserInfoRespond>

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

    @GET("/mic/room/{roomId}/setting")
    fun getRoomSetting(@Path("roomId") roomId: String): Single<RoomSettingRespond>

    @POST("/mic/room/gift/add")
    fun sendGifts(@Body sendGiftsRequest: SendGiftsRequest): Single<SimpleRespondBean>

    @GET("/mic/room/{roomId}/gift/list")
    fun getGiftList(@Path("roomId") roomId: String): Single<GiftListRespond>

    @POST("/mic/room/message/broadcast")
    fun broadcastMessage(@Body broadcastMessage: BroadcastMessage): Single<SimpleRespondBean>

    @Streaming
    @GET("/file/show")
    fun downloadFile(@Query("path") fileName: String): Flowable<ResponseBody>

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
}