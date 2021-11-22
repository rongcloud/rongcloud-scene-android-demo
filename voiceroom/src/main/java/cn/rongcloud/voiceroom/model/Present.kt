/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.model

import androidx.annotation.DrawableRes

data class Present(
    val index: Int,
    @DrawableRes val icon: Int,
    val name: String,
    val price: Int,
)