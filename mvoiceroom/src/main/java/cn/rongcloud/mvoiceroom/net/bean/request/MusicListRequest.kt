/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.request
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class MusicListRequest(
    @SerializedName("roomId")
    val roomId: String? = null,
    @SerializedName("type")
    val type: Int? = null
)