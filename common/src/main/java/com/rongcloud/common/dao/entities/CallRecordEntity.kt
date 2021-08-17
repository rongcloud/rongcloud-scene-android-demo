/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author gusd
 * @Date 2021/07/21
 */
// 通话能力
public const val CALL_TYPE_AUDIO = 1 shl 0
public const val CALL_TYPE_VIDEO = 1 shl 1

// 通话类型
// 单呼
public const val CALL_TYPE_SINGLE_CALL = 1 shl 2

// 会议
public const val CALL_TYPE_CONF_CALL = 1 shl 3

// 语音单呼
public const val AUDIO_SINGLE_CALL = CALL_TYPE_AUDIO or CALL_TYPE_SINGLE_CALL

// 视频单呼
public const val VIDEO_SINGLE_CALL = CALL_TYPE_VIDEO or CALL_TYPE_SINGLE_CALL

// 语音会议
public const val AUDIO_CONF_CALL = CALL_TYPE_AUDIO or CALL_TYPE_CONF_CALL

// 视频会议
public const val VIDEO_CONF_CALL = CALL_TYPE_VIDEO or CALL_TYPE_CONF_CALL

// 呼叫
public const val DIRECTION_CALL = 0

// 被呼叫
public const val DIRECTION_CALLED = 1

@Entity(tableName = "CallRecord")
data class CallRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "callerId") val callerId: String,
    @ColumnInfo(name = "callerNumber") val callerNumber: String?,
    @ColumnInfo(name = "peerId") val peerId: String?,
    @ColumnInfo(name = "peerNumber") val peerNumber: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "during") val during: Long,
    @ColumnInfo(name = "callType") val callType: Int,
    @ColumnInfo(name = "direction") val direction: Int
)