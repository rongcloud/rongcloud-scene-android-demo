/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rongcloud.common.dao.api.CallRecordDao
import com.rongcloud.common.dao.api.UserInfoDao
import com.rongcloud.common.dao.entities.CallRecordEntity
import com.rongcloud.common.dao.entities.UserInfoEntity

/**
 * @author gusd
 * @Date 2021/07/21
 */
@Database(entities = [CallRecordEntity::class, UserInfoEntity::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun callRecordDao(): CallRecordDao

    abstract fun userInfoDao(): UserInfoDao
}