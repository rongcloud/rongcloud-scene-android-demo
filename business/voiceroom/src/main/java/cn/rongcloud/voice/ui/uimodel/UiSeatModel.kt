/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.ui.uimodel

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import com.basis.utils.GsonUtil
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * @author gusd
 * @Date 2021/06/18
 */
class UiSeatModel constructor(
    val index: Int = -1,
    private val seatModel: RCVoiceSeatInfo,
    private val seatInfoChangeSubject: BehaviorSubject<UiSeatModel>
) {

    /**
     * 记录上次通话状态，防止频繁触发
     */
    private var preSpeakingStatus: Boolean? = null

    init {
        //首次通话状态位
        preSpeakingStatus = seatModel.isSpeaking;
    }

    var member: UiMemberModel? = null
        set(value) {
            // giftCount处理被重置
            var temp = field?.giftCount ?: 0
            value?.let {
                if (it.giftCount < 1) {
                    it.giftCount = temp
                }
            }
            if (value != field || value == null) {
                field = value
                seatInfoChangeSubject.onNext(this)
            }
        }

    var userId: String?
        get() {
            return seatModel.userId
        }
        set(value) {
            seatInfoChangeSubject.onNext(this)
        }

    var seatStatus: RCVoiceSeatInfo.RCSeatStatus
        get() {
            return seatModel.status ?: RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
        }
        set(value) {
            seatInfoChangeSubject.onNext(this)
        }


    var isMute: Boolean
        get() {
            return seatModel.isMute
        }
        set(value) {
            seatInfoChangeSubject.onNext(this)
        }

    var isSpeaking: Boolean
        get() {
            return seatModel.isSpeaking
        }
        set(value) {
            seatModel.isSpeaking = value;
            if (preSpeakingStatus != value) {
                preSpeakingStatus = value
                seatInfoChangeSubject.onNext(this)
            }
        }

    var extra: UiSeatModelExtra? = null
        get() {
            return field ?: GsonUtil.json2Obj(seatModel.extra, UiSeatModelExtra::class.java)
            ?: UiSeatModelExtra(false)
        }
        set(value) {
            seatModel.extra = GsonUtil.obj2Json(value)
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

    var giftCount: Int = 0
        //        get() = this.giftCount ?: 0
        set(value) {
            if (value != field) {
                field = value
                seatInfoChangeSubject.onNext(this)
            }
        }


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

    data class UiSeatModelExtra(var disableRecording: Boolean = false)
}