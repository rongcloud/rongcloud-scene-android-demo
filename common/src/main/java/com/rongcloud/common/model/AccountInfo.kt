/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.model

import com.rongcloud.common.utils.JsonUtils

/**
 * @author gusd
 * @Date 2021/07/28
 */
data class AccountInfo(
    val authorization: String? = null,
    val imToken: String? = null,
    var phone: String? = null,
    val portrait: String? = null,
    val type: Int? = null,
    val userId: String? = null,
    val userName: String? = null
) {
    fun toJson() = JsonUtils.toJson(this)
}