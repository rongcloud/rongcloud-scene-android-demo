/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.respond

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/15
 */
data class CreateRoomRespondBean(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val `data`: VoiceRoomBean? = null,
    @SerializedName("msg")
    val msg: String? = null
)
