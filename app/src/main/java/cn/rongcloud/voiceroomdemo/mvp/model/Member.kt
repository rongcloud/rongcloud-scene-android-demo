/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import android.net.Uri
import io.rong.imlib.model.UserInfo

private const val TAG = "Member"

class Member(userId: String, userName: String?, protrait: String?) :
    UserInfo(userId, userName, Uri.parse(protrait)) {

    var isAdmin: Boolean = false//管理员
    var giftCount: Int = 0//礼物数
    var isRequestSeat: Boolean = false// 是否申请麦位
}