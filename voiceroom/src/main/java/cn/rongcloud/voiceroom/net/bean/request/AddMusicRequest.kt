/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.request

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class AddMusicRequest(
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("roomId")
    val roomId: String? = null,
    /**
     * 0:官方，1：本地添加，2：从官方添加
     */
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("size")
    val size: Long? = 0
)