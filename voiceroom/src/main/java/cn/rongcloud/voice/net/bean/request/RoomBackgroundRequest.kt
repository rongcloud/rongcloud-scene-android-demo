/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.net.bean.request

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class RoomBackgroundRequest(
    @SerializedName("backgroundUrl")
    val backgroundUrl: String = "",
    @SerializedName("roomId")
    val roomId: String
)