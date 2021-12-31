/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.net.bean.request
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class SendGiftsRequest(
    @SerializedName("giftId")
    val giftId: Int? = null,
    @SerializedName("num")
    val num: Int? = null,
    @SerializedName("roomId")
    val roomId: String? = null,
    @SerializedName("toUid")
    val toUid: String? = null
)