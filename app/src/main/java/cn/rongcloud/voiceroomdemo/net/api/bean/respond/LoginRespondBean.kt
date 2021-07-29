/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api.bean.respond

import cn.rongcloud.voiceroomdemo.utils.JsonUtils

/**
 * @author gusd
 * @Date 2021/06/07
 */
data class LoginRespondBean(val code: Int,val msg: String?, val data: AccountInfo?)
data class AccountInfo(
    val authorization: String?=null,
    val imToken: String?=null,
    val portrait: String?=null,
    val type: Int?=null,
    val userId: String?=null,
    val userName: String?=null
){
    fun toJson() = JsonUtils.toJson(this)
}



