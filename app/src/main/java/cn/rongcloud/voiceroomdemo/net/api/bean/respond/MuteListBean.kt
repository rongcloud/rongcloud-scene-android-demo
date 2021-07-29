/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api.bean.respond
import com.google.gson.annotations.SerializedName


/**
 * @author gusd
 * @Date 2021/06/17
 */
data class MuteListBean(
    @SerializedName("code")
    val code: Int = -1,
    @SerializedName("data")
    val `data`: List<MuteMember>? = null,
    @SerializedName("msg")
    val msg: String? = null
)

data class MuteMember(
    @SerializedName("portrait")
    val portrait: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("userName")
    val userName: String? = null
)