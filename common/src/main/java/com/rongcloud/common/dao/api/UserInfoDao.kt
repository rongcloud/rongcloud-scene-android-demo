/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.api

import androidx.room.*
import com.rongcloud.common.dao.entities.UserInfoEntity
import io.reactivex.rxjava3.core.Flowable

/**
 * @author gusd
 * @Date 2021/07/22
 */
@Dao
interface UserInfoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(vararg usersInfo: UserInfoEntity)


    @Query("SELECT * FROM UserInfo")
    fun queryUserInfoList(): Flowable<List<UserInfoEntity>>

    @Query("SELECT * FROM USERINFO WHERE userId = :userId")
    fun queryUserInfoById(userId: String): Flowable<UserInfoEntity>

}