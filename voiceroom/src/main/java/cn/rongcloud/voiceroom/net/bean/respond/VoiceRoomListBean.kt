/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.respond

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * @author gusd
 * @Date 2021/06/09
 */

data class VoiceRoomListBean(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("msg")
    val msg: String? = null
)

data class Data(
    @SerializedName("rooms")
    val rooms: List<VoiceRoomBean>? = null,
    @SerializedName("images")
    val images: List<String>? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class VoiceRoomInfoBean(
    @SerializedName("data")
    val room: VoiceRoomBean? = null,
    @SerializedName("code")
    val code: Int = -1,
    @SerializedName("msg")
    val msg: String? = null
)

@Parcelize
data class VoiceRoomBean(
    @SerializedName("backgroundUrl")
    val backgroundUrl: String? = null,
    @SerializedName("createUser")
    val createUser: Member? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("isPrivate")
    val isPrivate: Int? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("roomId")
    val roomId: String,
    @SerializedName("roomName")
    val roomName: String? = null,
    @SerializedName("themePictureUrl")
    val themePictureUrl: String? = null,
    @SerializedName("updateDt")
    val updateDt: Long? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("userTotal")
    val userTotal: String? = null
) : Parcelable

