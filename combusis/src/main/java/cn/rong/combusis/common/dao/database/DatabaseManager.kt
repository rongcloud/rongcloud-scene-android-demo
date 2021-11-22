/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.database

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.rongcloud.common.dao.entities.CallRecordEntity
import com.rongcloud.common.dao.entities.DIRECTION_CALL
import com.rongcloud.common.dao.entities.UserInfoEntity
import com.rongcloud.common.dao.model.query.CallRecordModel
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author gusd
 * @Date 2021/07/21
 */
private const val TAG = "DatabaseManager"

object DatabaseManager {

    private lateinit var context: Application

    private val executor: ExecutorService by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val instance by lazy {
        Room
            .databaseBuilder(context, AppDatabase::class.java, "VoiceRoomDB")
            .setTransactionExecutor(executor)
            .setQueryExecutor(executor)
            .fallbackToDestructiveMigration()
            .build()
    }

//    fun obUserInfoList(): Observable<List<UserInfoEntity>> {
//        return instance.memberInfoDao().queryUserInfoList().toObservable()
//    }

    fun obCallRecordList(userId: String): Observable<List<CallRecordModel>> {
        return instance.callRecordDao().queryCallRecordList(userId).map { recordList ->
            val resultList = arrayListOf<CallRecordModel>()
            recordList.forEachIndexed { index, callRecord ->
                if (index == 0) {
                    resultList.add(callRecord)
                } else if (callRecord.callerId != recordList[index - 1].peerId
                    || callRecord.peerId != recordList[index - 1].callerId
                    || callRecord.direction == recordList[index - 1].direction
                ) {
                    resultList.add(callRecord)
                }
            }
            return@map resultList.toList()
        }.toObservable()
    }

    fun obUserInfoByUserId(userId: String): Observable<UserInfoEntity> {
        return instance.userInfoDao().queryUserInfoById(userId).toObservable()
    }


    fun init(context: Application) {
        this.context = context
    }

    fun insertCallRecordAndMemberInfo(
        callerId: String,
        callerNumber: String?,
        callerName: String?,
        callerPortrait: String?,
        peerId: String,
        peerNumber: String,
        peerName: String?,
        peerPortrait: String?,
        date: Long,
        during: Long,
        callType: Int,
        direction: Int = DIRECTION_CALL

    ) {
        doOnDataBaseScheduler {
            instance.runInTransaction {
                try {
                    CallRecordEntity(
                        id = null,
                        callerId = callerId,
                        callerNumber = callerNumber,
                        peerId = peerId,
                        peerNumber = peerNumber,
                        date = date,
                        during = during,
                        callType = callType,
                        direction = direction
                    ).apply {
                        instance.callRecordDao().insertCallRecord(this)
                    }

                    UserInfoEntity(callerId, callerName, callerPortrait, callerNumber).apply {
                        instance.userInfoDao().addOrUpdate(this)
                    }

                    UserInfoEntity(peerId, peerName, peerPortrait, peerNumber).apply {
                        instance.userInfoDao().addOrUpdate(this)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "insertCallRecordAndMemberInfo: ", e)
                }
            }
        }

    }

    fun insertCallRecord(
        callerId: String,
        callerNumber: String?,
        peerId: String,
        peerNumber: String?,
        date: Long,
        during: Long,
        callType: Int,
        direction: Int = DIRECTION_CALL

    ) {
        doOnDataBaseScheduler {
            instance.runInTransaction {
                try {
                    CallRecordEntity(
                        id = null,
                        callerId = callerId,
                        callerNumber = callerNumber,
                        peerId = peerId,
                        peerNumber = peerNumber,
                        date = date,
                        during = during,
                        callType = callType,
                        direction = direction
                    ).apply {
                        instance.callRecordDao().insertCallRecord(this)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "insertCallRecord: ", e)
                }
            }
        }

    }

    fun addOrUpdateUserInfo(userId: String, userName: String?, portrait: String?, number: String?) {
        doOnDataBaseScheduler {
            instance.runInTransaction {
                UserInfoEntity(userId, userName, portrait, number).apply {
                    instance.userInfoDao().addOrUpdate(this)
                }
            }
        }


    }

    private fun doOnDataBaseScheduler(runnable: () -> Unit) {
        executor.submit(runnable)
    }


}