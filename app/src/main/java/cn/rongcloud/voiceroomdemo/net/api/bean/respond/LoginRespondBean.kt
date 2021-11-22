/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api.bean.respond

import androidx.annotation.Keep
import com.rongcloud.common.model.AccountInfo

/**
 * @author gusd
 * @Date 2021/06/07
 */
@Keep
data class LoginRespondBean(val code: Int, val msg: String?, val data: AccountInfo?)




