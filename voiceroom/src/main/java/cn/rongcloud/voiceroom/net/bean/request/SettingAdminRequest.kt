/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.bean.request

import com.google.gson.annotations.SerializedName

/**
 * @author gusd
 * @Date 2021/06/21
 */
data class SettingAdminRequest(
    @SerializedName("roomId")
    val roomId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("isManage")
    val isManage: Boolean
)
