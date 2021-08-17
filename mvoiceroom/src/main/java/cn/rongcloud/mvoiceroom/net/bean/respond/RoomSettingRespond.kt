/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.respond
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class RoomSettingRespond(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
    @SerializedName("data")
    val `data`: SettingBean? = null
)

data class SettingBean(
    @SerializedName("applyAllLockMic")
    val applyAllLockMic: Boolean? = null,
    @SerializedName("applyAllLockSeat")
    val applyAllLockSeat: Boolean? = null,
    @SerializedName("applyOnMic")
    val applyOnMic: Boolean? = null,
    @SerializedName("roomId")
    val roomId: String? = null,
    @SerializedName("setMute")
    val setMute: Boolean? = null,
    @SerializedName("setSeatNumber")
    val setSeatNumber: Int? = null
)