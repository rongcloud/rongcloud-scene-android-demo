/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.net.bean.respond

import com.google.gson.annotations.SerializedName

/**
 * @author gusd
 * @Date 2021/06/17
 */
data class GiftListRespond(
    @SerializedName("code")
    val code:Int = -1,
    @SerializedName("msg")
    val msg:String? = null,
    @SerializedName("data")
    val `data`:List<HashMap<String,Int>>?
)
