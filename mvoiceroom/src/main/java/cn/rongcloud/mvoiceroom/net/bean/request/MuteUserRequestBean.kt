/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.request
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */

private const val MUTE = "add"
private const val UN_MUTE = "remove"

data class MuteUserRequestBean(
    @SerializedName("operation")
    val operation: String = MUTE,
    @SerializedName("roomId")
    val roomId: String,
    @SerializedName("userIds")
    val userIds: List<String> = emptyList()
)