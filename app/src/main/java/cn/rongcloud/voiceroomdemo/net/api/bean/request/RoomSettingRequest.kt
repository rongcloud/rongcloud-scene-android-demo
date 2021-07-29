/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api.bean.request

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/24
 */
data class RoomSettingRequest(
    @SerializedName("roomId")
    val roomId: String,
    @SerializedName("applyAllLockMic")
    val applyAllLockMic: Boolean? = null,
    @SerializedName("applyAllLockSeat")
    val applyAllLockSeat: Boolean? = null,
    @SerializedName("applyOnMic")
    val applyOnMic: Boolean? = null,
    @SerializedName("setMute")
    val setMute: Boolean? = null,
    @SerializedName("setSeatNumber")
    val setSeatNumber: Int? = null
)