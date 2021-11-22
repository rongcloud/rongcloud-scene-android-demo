/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.net

import com.rongcloud.common.AppConfig

/**
 * @author gusd
 * @Date 2021/06/07
 */
object ApiConstant {
    val BASE_URL = AppConfig.BASE_SERVER_ADDRESS ?: ""

    val FILE_URL = "${BASE_URL}file/show?path="

    val HOME_PAGE = "https://docs.rongcloud.cn/v4/5X/views/scene/voiceroom/android/intro/intro.html"

    val DEFAULT_PORTRAIT_ULR = "https://cdn.ronghub.com/demo/default/rce_default_avatar.png"

    const val REQUEST_SUCCESS_CODE = 10000

    const val CUSTOMER_PHONE = "13161856839"
}