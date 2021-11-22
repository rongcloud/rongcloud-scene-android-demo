/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.respond

import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/15
 */
data class FileUploadRespond(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
    @SerializedName("data")
    val data: String? = null
)