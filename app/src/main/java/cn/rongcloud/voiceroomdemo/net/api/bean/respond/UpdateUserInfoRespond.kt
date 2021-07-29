/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api.bean.respond
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/15
 */
data class UpdateUserInfoRespond(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val `data`: UserInfo? = null,
    @SerializedName("msg")
    val msg: String? = null
)

data class UserInfo(
    @SerializedName("createDt")
    val createDt: Long? = null,
    @SerializedName("deviceId")
    val deviceId: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("portrait")
    val portrait: String? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("uid")
    val uid: String? = null,
    @SerializedName("updateDt")
    val updateDt: Long? = null
)