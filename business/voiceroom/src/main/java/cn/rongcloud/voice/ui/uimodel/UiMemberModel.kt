/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.ui.uimodel

import cn.rongcloud.roomkit.ui.room.model.Member
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * @author gusd
 * @Date 2021/06/24
 */
class UiMemberModel(
    private val infoChangeSubject: BehaviorSubject<UiMemberModel>
) {

    var member: Member? = null
        set(value) {
            if (member != value) {
                field = value
                infoChangeSubject.onNext(this)
            }
        }

    var portrait: String?
        get() = member?.portrait
        set(value) {
            member?.portrait = value
        }

    var userId: String
        get() = member?.userId ?: ""
        set(value) {
            member?.userId = value
        }

    var userName: String?
        get() = member?.userName
        set(value) {
            member?.userName = value
        }

    var isAdmin: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                infoChangeSubject.onNext(this)
            }
        }
    var giftCount: Int = 0
        set(value) {
            if (value != field) {
                field = value
                infoChangeSubject.onNext(this)
            }
        }

    /**
     * 正在请求麦位
     */
    var isRequestSeat: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                infoChangeSubject.onNext(this)
            }
        }

    /**
     * 正在被邀请上麦
     */
    var isInvitedInfoSeat: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                infoChangeSubject.onNext(this)
            }
        }

    var seatIndex: Int = -1
        set(value) {
            if (value != field) {
                if (value != -1) {
                    isInvitedInfoSeat = false
                    isRequestSeat = false
                }
                field = value
                infoChangeSubject.onNext(this)
            }
        }

    var selected: Boolean = false

    override fun toString(): String {
        return "UiMemberModel(portrait=$portrait, userId='$userId', userName=$userName, isAdmin=$isAdmin, giftCount=$giftCount, isRequestSeat=$isRequestSeat, isInvitedInfoSeat=$isInvitedInfoSeat, seatIndex=$seatIndex)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiMemberModel

        if (portrait != other.portrait) return false
        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (isAdmin != other.isAdmin) return false
        if (giftCount != other.giftCount) return false
        if (isRequestSeat != other.isRequestSeat) return false
        if (isInvitedInfoSeat != other.isInvitedInfoSeat) return false
        if (seatIndex != other.seatIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = portrait?.hashCode() ?: 0
        result = 31 * result + userId.hashCode()
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + isAdmin.hashCode()
        result = 31 * result + giftCount
        result = 31 * result + isRequestSeat.hashCode()
        result = 31 * result + isInvitedInfoSeat.hashCode()
        result = 31 * result + seatIndex
        return result
    }


}