/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.uimodel

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * @author gusd
 * @Date 2021/06/18
 */
class UiSeatModel(
    val index: Int = -1,
    private val seatModel: RCVoiceSeatInfo,
    private val seatInfoChangeSubject: BehaviorSubject<UiSeatModel>
) {

    var member: UiMemberModel? = null
        set(value) {
            if (value != field) {
                field = value
                seatInfoChangeSubject.onNext(this)
            }
        }

    var userId: String?
        get() {
            return seatModel.userId
        }
        set(value) {
            seatModel.userId = value
            seatInfoChangeSubject.onNext(this)
        }

    var seatStatus: RCVoiceSeatInfo.RCSeatStatus
        get() {
            return seatModel.status ?: RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
        }
        set(value) {
            seatModel.status = value
            seatInfoChangeSubject.onNext(this)
        }


    var isMute: Boolean
        get() {
            return seatModel.isMute
        }
        set(value) {
            seatModel.isMute = value
            seatInfoChangeSubject.onNext(this)
        }

    var isSpeaking: Boolean
        get() {
            return seatModel.isSpeaking
        }
        set(value) {
            seatModel.isSpeaking = value
            seatInfoChangeSubject.onNext(this)
        }

    var extra: String?
        get() {
            return seatModel.extra
        }
        set(value) {
            seatModel.extra = value
            seatInfoChangeSubject.onNext(this)
        }

    var portrait: String?
        get() {
            return member?.portrait
        }
        private set(value) {
        }

    var userName: String?
        get() = member?.userName
        set(value) {}

    var isAdmin: Boolean = false
        get() = member?.isAdmin ?: false

    var giftCount: Int
        get() = member?.giftCount ?: 0
        private set(value) {}


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiSeatModel

        if (index != other.index) return false
        if (userId != other.userId) return false
        if (seatStatus != other.seatStatus) return false
        if (isMute != other.isMute) return false
        if (isSpeaking != other.isSpeaking) return false
        if (extra != other.extra) return false
        if (portrait != other.portrait) return false
        if (userName != other.userName) return false
        if (isAdmin != other.isAdmin) return false
        if (giftCount != other.giftCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + seatStatus.hashCode()
        result = 31 * result + isMute.hashCode()
        result = 31 * result + isSpeaking.hashCode()
        result = 31 * result + (extra?.hashCode() ?: 0)
        result = 31 * result + (portrait?.hashCode() ?: 0)
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + isAdmin.hashCode()
        result = 31 * result + giftCount
        return result
    }

    override fun toString(): String {
        return "UiSeatModel(index=$index, userId=$userId, seatStatus=$seatStatus, mute=$isMute, isSpeaking=$isSpeaking, extra=$extra, portrait=$portrait, userName=$userName, isAdmin=$isAdmin, giftCount=$giftCount)"
    }


}