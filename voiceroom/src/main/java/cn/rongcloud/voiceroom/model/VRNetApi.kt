/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.model

import android.util.Log
import cn.rong.combusis.manager.RCChatRoomMessageManager
import cn.rong.combusis.message.*
import cn.rong.combusis.provider.user.UserProvider
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider
import cn.rong.combusis.sdk.Api
import cn.rong.combusis.sdk.VoiceRoomApi
import cn.rong.combusis.sdk.event.EventHelper
import cn.rongcloud.voiceroom.net.VoiceRoomNetManager
import cn.rongcloud.voiceroom.net.bean.request.*
import cn.rongcloud.voiceroom.ui.uimodel.*
import com.kit.wapper.IResultBack
import com.rongcloud.common.net.ApiConstant
import com.rongcloud.common.utils.AccountStore
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*

/**
 * @author gusd
 * @Date 2021/06/18
 */

private const val TAG = "VoiceRoomModel"


class VRNetApi(val vRoomWrapper: VRoomWrapper) {
    private val dataModifyScheduler by lazy {
        return@lazy Schedulers.computation()
    }


    private val roomId by lazy {
        return@lazy vRoomWrapper.roomId
    }

    private val roomMemberInfoList by lazy {
        return@lazy vRoomWrapper.members
    }

    /**
     * 设置管理员
     */
    fun setAdmin(userId: String, isAdmin: Boolean): Single<Boolean> {
        return VoiceRoomNetManager
            .aRoomApi
            .setAdmin(SettingAdminRequest(roomId, userId, isAdmin))
            .observeOn(dataModifyScheduler)
            .map {
                if (it.code == ApiConstant.REQUEST_SUCCESS_CODE) {
                    UserProvider.provider().getAsyn(userId) { user ->
                        RCChatRoomMessageManager.sendChatMessage(roomId, RCChatroomAdmin()
                            .apply {
                                this.userId = userId
                                this.userName = user.name
                                this.isAdmin = isAdmin
                            })
                    }
                    roomMemberInfoList.firstOrNull { model ->
                        model.userId == userId
                    }?.let { model ->
                        model.isAdmin = isAdmin
                    }
                    // 通知管理员变更
                    VoiceRoomApi.getApi().notifyRoom(Api.EVENT_MANAGER_LIST_CHANGE, "")
                }
                return@map it.code == ApiConstant.REQUEST_SUCCESS_CODE
            }
    }

    /**
     * 锁定房间：设置房间密码
     */
    fun setRoomLock(lock: Boolean, password: String?): Single<Boolean> {
        return VoiceRoomNetManager
            .aRoomApi
            .setRoomPasswordRequest(
                RoomPasswordRequest(if (lock) 1 else 0, password, roomId)
            )
            .doOnSuccess {
                VoiceRoomProvider.provider().provideFromService(arrayListOf(roomId), null)
            }.map {
                return@map it.code == ApiConstant.REQUEST_SUCCESS_CODE
            }
    }

    /**
     * 设置背景
     */
    fun setRoomBackground(backgroundUrl: String): Single<Boolean> {
        return VoiceRoomNetManager
            .aRoomApi
            .setRoomBackgroundRequest(
                RoomBackgroundRequest(
                    backgroundUrl,
                    roomId
                )
            ).doOnSuccess {
                //通知背景便跟
                VoiceRoomApi.getApi().notifyRoom(Api.EVENT_BACKGROUND_CHANGE, backgroundUrl)
            }.map {
                return@map it.code == ApiConstant.REQUEST_SUCCESS_CODE
            }
    }

    /**
     * 修改房间名称
     * 1.调api修改名称
     * 2.调sdk api 跟新kv
     */
    fun setRoomName(newName: String): Single<Boolean> {
        return VoiceRoomNetManager
            .aRoomApi
            .setRoomName(RoomNameRequest(newName, roomId))
            .doOnSuccess {
                vRoomWrapper.netRoomInfo.roomName = newName;
                VoiceRoomApi.getApi().updateRoomName(newName) {
                    if (it) {
                        vRoomWrapper.netRoomInfo.roomName = newName
                    }
                }
            }.map {
                return@map it.code == ApiConstant.REQUEST_SUCCESS_CODE
            }
    }


    fun pushRoomSettingToServer(
        applyOnMic: Boolean? = null,
        applyAllLockMic: Boolean? = null,
        applyAllLockSeat: Boolean? = null,
        setMute: Boolean? = null,
        setSeatNumber: Int? = null
    ) {
        val setting = RoomSettingRequest(
            roomId,
            applyAllLockMic,
            applyAllLockSeat,
            applyOnMic,
            setMute,
            setSeatNumber
        )
        VoiceRoomNetManager
            .aRoomApi
            .setRoomSetting(setting).subscribe()
    }

    /**
     * 获取管理员列表
     */
    fun getAdminList() {
        VoiceRoomNetManager
            .aRoomApi
            .getAdminList(roomId)
            .observeOn(dataModifyScheduler)
            .subscribeOn(Schedulers.io())
            .subscribe { bean ->
                bean.data?.let { adminList ->
                    roomMemberInfoList.forEach { member ->
                        member.isAdmin =
                            adminList.firstOrNull { admin -> admin.userId == member.userId } != null
                    }
                }
            }
    }

    /**
     * 获取礼物count
     */
    fun getGift(callback: IResultBack<Boolean>?) {
        Log.e(TAG, "getGift")
        VoiceRoomNetManager
            .giftService
            .getGiftList(roomId)
            .observeOn(dataModifyScheduler)
            .subscribeOn(Schedulers.io())
            .subscribe { bean ->
                bean.data?.let { listMap ->
                    listMap.forEach { map ->
                        map.forEach { entry ->
                            roomMemberInfoList.firstOrNull { member ->
                                member.userId == entry.key
                            }?.giftCount = entry.value
                            callback?.onResult(true)
                        }
                    }
                }
            }
    }

    private fun removeMemberById(userId: String): Boolean {
        var del = false
        roomMemberInfoList.forEach {
            if (it.userId == userId) {
                del = true
                roomMemberInfoList.remove(it)
                return@forEach
            }
        }
        return del
    }

    private fun toUserIds(): List<String> {
        var userIds = arrayListOf<String>()
        roomMemberInfoList.forEach {
            userIds.add(it.userId)
        }
        return userIds;
    }

    fun getMemeberFromRoom(
        callback: IResultBack<Boolean>?
    ) {
        Log.d(TAG, "queryAllUserInfo: ")
        VoiceRoomNetManager
            .aRoomApi
            .getMembersList(roomId)
            .observeOn(dataModifyScheduler)
            .subscribeOn(Schedulers.io())
            .map { membersBean ->
                membersBean.data?.forEach { memb ->
                    removeMemberById(memb.userId)
                    roomMemberInfoList.add(Member(memb.userId, memb.userName, memb.portrait))
                }
                return@map roomId
            }.flatMap {
                return@flatMap VoiceRoomNetManager.aRoomApi.getAdminList(it)//管理员信息
            }.map {
                it.data?.let { adminList ->
                    roomMemberInfoList.forEach { member ->
                        member.isAdmin =
                            adminList.firstOrNull { admin -> admin.userId == member.userId } != null
                    }
                }
                return@map roomId
            }.flatMap {
                return@flatMap VoiceRoomNetManager.giftService.getGiftList(it)//礼物信息
            }.map {
                it.data?.let { listMap ->
                    listMap.forEach { map ->
                        map.forEach { entry ->
                            roomMemberInfoList.firstOrNull { member ->
                                member.userId == entry.key
                            }?.giftCount = entry.value
                            // 刷新gifcount
                            roomMemberInfoList.firstOrNull {
                                it.userId == entry.key
                            }
                        }
                    }
                }
            }.doOnSuccess {
                EventHelper.helper().getRequestSeatUserIds { requests ->
                    var needGet = arrayListOf<String>()
                    var list = toUserIds();
                    requests?.forEach {
                        if (!list.contains(it)) {
                            needGet.add(it)
                        }
                    }
                    UserProvider.provider().provideFromService(requests) { users ->
                        //修改
                    }
                }
            }
            .subscribe({
                callback?.onResult(true)
            }, {
                callback?.onResult(false)
            })
    }


    fun sendGift(
        members: List<UiMemberModel>,
        present: Present,
        num: Int
    ): Observable<List<UiMemberModel>> {
        val result = arrayListOf<UiMemberModel>()
        val publisherList = members.map { model ->
            VoiceRoomNetManager
                .giftService
                .sendGifts(
                    SendGiftsRequest(
                        present.index,
                        num,
                        roomId,
                        model.userId
                    )
                ).doOnSuccess {
                    if (it.code == ApiConstant.REQUEST_SUCCESS_CODE) {
                        result.add(model)
                    }
                }.toFlowable()
        }.toTypedArray()
        return Observable.create<List<UiMemberModel>> { emitter ->
            Flowable
                .concatArray(*publisherList)
                .subscribe({
                }, {
                    emitter.onError(it)
                }, {
                    emitter.onNext(result)
                })

        }.subscribeOn(Schedulers.io())
    }

    fun sendGiftMsg(
        members: List<UiMemberModel>,
        present: Present,
        nums: Int,
        isAll: Boolean
    ) {
        if (isAll) {
            RCChatRoomMessageManager.sendChatMessage(roomId, RCChatroomGiftAll()
                .apply {
                    userId = AccountStore.getUserId()
                    userName = AccountStore.getUserName()
                    giftId = "${present.index}"
                    giftName = present.name
                    number = nums
                    price = present.price
                })
        } else {
            RCChatRoomMessageManager.sendChatMessages(roomId,
                members.map { member ->
                    return@map RCChatroomGift().apply {
                        userId = AccountStore.getUserId()
                        userName = AccountStore.getUserName()
                        targetId = member.userId
                        targetName = member.userName
                        giftId = "${present.index}"
                        giftName = present.name
                        number = "$nums"
                        price = present.price
                    }
                })
        }

    }
}