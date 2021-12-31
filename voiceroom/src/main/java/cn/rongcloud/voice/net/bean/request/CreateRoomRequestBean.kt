/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.net.bean.request

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/15
 */
data class CreateRoomRequestBean(
    @SerializedName("isPrivate")
    val isPrivate: Int = 0,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("themePictureUrl")
    val themePictureUrl: String? = "",
    @SerializedName("backgroundUrl")
    val backgroundUrl: String? = "",
    @SerializedName("kv")
    val kv: List<Kv>? = null,
    @SerializedName("roomType")
    val roomType: Int = 1,
)

data class Kv(
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("value")
    val value: String? = null
)