/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.ui.uimodel

import cn.rongcloud.config.bean.VoiceRoomBean
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * @author gusd
 * @Date 2021/06/23
 */
class UiRoomModel(private val roomInfoSubject: BehaviorSubject<UiRoomModel>) {
    var rcRoomInfo: RCVoiceRoomInfo? = null
        set(value) {
            field = value
            roomInfoSubject.onNext(this)
        }
    var roomBean: VoiceRoomBean? = null
        set(value) {
            field = value
            roomInfoSubject.onNext(this)
        }

    var isMute: Boolean = false
        set(value) {
            field = value
            roomInfoSubject.onNext(this)
        }

    var mSeatCount: Int
        get() {
            return rcRoomInfo?.seatCount ?: 0
        }
        set(value) {
            rcRoomInfo?.let {
                it.seatCount = value
                roomInfoSubject.onNext(this)
            }
        }


    var isFreeEnterSeat: Boolean
        get() {
            return rcRoomInfo?.isFreeEnterSeat ?: false
        }
        set(value) {
            rcRoomInfo?.let {
                it.isFreeEnterSeat = value
                roomInfoSubject.onNext(this)
            }
        }

    var isLockAll: Boolean
        get() {
            return rcRoomInfo?.isLockAll ?: false
        }
        set(value) {
            rcRoomInfo?.let {
                it.isLockAll = value
                roomInfoSubject.onNext(this)
            }
        }

    var isMuteAll: Boolean
        get() {
            return rcRoomInfo?.isMuteAll ?: false
        }
        set(value) {
            rcRoomInfo?.let {
                it.isMuteAll = value
                roomInfoSubject.onNext(this)
            }
        }

    override fun toString(): String {
        return "UiRoomModel(roomInfoSubject=$roomInfoSubject, rcRoomInfo=$rcRoomInfo, roomBean=$roomBean, isMute=$isMute, mSeatCount=$mSeatCount, isFreeEnterSeat=$isFreeEnterSeat, isLockAll=$isLockAll, isMuteAll=$isMuteAll)"
    }


}