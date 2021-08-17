/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.respond
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class MusicListRespond(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val `data`: List<MusicBean>? = null,
    @SerializedName("msg")
    val msg: String? = null
)
