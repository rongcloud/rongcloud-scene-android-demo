/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.net.bean.request
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class RoomPasswordRequest(
    /**
     * 0：去锁，1：上锁
     */
    @SerializedName("isPrivate")
    val isPrivate: Int = 0,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("roomId")
    val roomId: String
)