/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.respond

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class AddMusicRespond(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val `data`: MusicBean? = null,
    @SerializedName("msg")
    val msg: String? = null
)

data class MusicBean(
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("createDt")
    val createDt: Long? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("roomId")
    val roomId: String? = null,
    @SerializedName("size")
    val size: String? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("updateDt")
    val updateDt: Long? = null,
    @SerializedName("url")
    val url: String? = null
)



