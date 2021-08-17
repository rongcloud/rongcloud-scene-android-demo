/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.respond

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * @author gusd
 * @Date 2021/06/16
 */
data class VoiceRoomMemberListBean(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val `data`: List<Member>? = null,
    @SerializedName("msg")
    val msg: String? = null
)

@Parcelize
data class Member(
    @SerializedName("portrait")
    var portrait: String? = null,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("userName")
    var userName: String? = null
) : Parcelable

data class UserIdList(
    @SerializedName("userIds")
    val userIds: List<String> = arrayListOf()
)