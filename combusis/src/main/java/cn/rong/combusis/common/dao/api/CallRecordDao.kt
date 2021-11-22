/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.dao.api

import androidx.room.*
import com.rongcloud.common.dao.entities.CallRecordEntity
import com.rongcloud.common.dao.entities.DIRECTION_CALL
import com.rongcloud.common.dao.entities.DIRECTION_CALLED
import com.rongcloud.common.dao.model.query.CallRecordModel
import io.reactivex.rxjava3.core.Flowable

/**
 * @author gusd
 * @Date 2021/07/21
 */

private const val CALL_RECORD_PARAMETERS =
    """cr.id,cr.callerNumber,cr.callerId,cr.peerId,cr.peerNumber,cr.date,cr.during,cr.callType,cr.direction, 
            callInfo.userName as callName,callInfo.number as callNumberFromInfo,callInfo.portrait as callPortrait,
            peerInfo.userName as peerName,peerInfo.number as peerNumberFromInfo,peerInfo.portrait as peerPortrait,
            max(cr.date) as recentTime From CallRecord AS cr LEFT JOIN UserInfo AS callInfo ON cr.callerId = callInfo.userId 
            LEFT JOIN UserInfo AS peerInfo ON cr.peerId = peerInfo.userId"""

@Dao
interface CallRecordDao {


    @Transaction
    @Query(
        """SELECT * FROM(SELECT $CALL_RECORD_PARAMETERS WHERE (cr.callerId = :userId AND cr.direction = '$DIRECTION_CALL') GROUP BY cr.peerNumber UNION 
                SELECT $CALL_RECORD_PARAMETERS WHERE (cr.peerId = :userId AND cr.direction = '$DIRECTION_CALLED') GROUP BY cr.callerNumber)
             ORDER BY date DESC """
    )
    fun queryCallRecordList(userId: String): Flowable<List<CallRecordModel>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCallRecord(vararg callRecordEntity: CallRecordEntity)


}