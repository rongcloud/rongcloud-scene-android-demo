/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.model.query

/**
 * @author gusd
 * @Date 2021/07/22
 */
data class CallRecordModel(
    val id: Long,
    val callerNumber: String?,
    val callerId: String,
    val peerId: String?,
    val peerNumber: String?,
    val date: Long,
    val during: Long,
    val callType: Int,
    val callName: String?,
    val callPortrait: String?,
    val callNumberFromInfo: String?,
    val peerName: String?,
    val peerNumberFromInfo: String?,
    val peerPortrait: String?,
    val direction:Int,
    val recentTime:Long
)
