/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import android.util.Log
import cn.rongcloud.voiceroomdemo.common.LocalDataStore
import cn.rongcloud.voiceroomdemo.net.RetrofitManager
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomInfoBean
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomListBean
import cn.rongcloud.voiceroomdemo.throwable.RoomNotExistException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * @author gusd
 * @Date 2021/06/09
 */
private const val TAG = "VoiceRoomListModel"

object VoiceRoomListModel {

    private val pageSize = 10
    private var page = 0
    private val currentRoomList = ArrayList<VoiceRoomBean>()
    private val currentRoomMap = HashMap<String, VoiceRoomBean>()
    private val loadSchedulers by lazy {
        Schedulers.io()
    }
    private val roomListSubject by lazy {
        BehaviorSubject.create<List<VoiceRoomBean>>()
    }

    private val roomListErrorSubject by lazy {
        PublishSubject.create<Throwable>()
    }

    private val roomInfoChangeSubject = BehaviorSubject.create<VoiceRoomBean>()

    fun refreshDataList() {
        Log.d(TAG, "refreshDataList: ")
        page = 0
        currentRoomList.clear()
        currentRoomMap.clear()
        requestRoomList()
    }

    fun loadMoreData() {
        requestRoomList()
    }

    private fun requestRoomList() {
        RetrofitManager
            .commonService
            .getRoomList(page, pageSize)
            .observeOn(loadSchedulers)
            .subscribeOn(loadSchedulers)
            .subscribe({ bean ->
                onRoomListChange(bean)
                bean.data?.images?.let {
                    LocalDataStore.saveBackGroundUrl(bean.data.images)
                }
            }, { t ->
                roomListErrorSubject.onNext(t)
            })
    }

    fun obRoomInfoChangeByRoomId(roomId: String): Observable<VoiceRoomBean> {
        return roomInfoChangeSubject.filter { it.roomId == roomId }
    }


    private fun onRoomListChange(bean: VoiceRoomListBean) {
        if (bean.code == 10000) {
            bean.data?.rooms?.let { list ->
                list.forEach { room ->
                    val voiceRoomBean = currentRoomMap[room.roomId]
                    if (voiceRoomBean == null) {
                        currentRoomList.add(room)
                    } else {
                        val index = currentRoomList.indexOfFirst {
                            it.roomId == room.roomId
                        }
                        currentRoomList[index] = room
                    }
                    currentRoomMap[room.roomId] = room
                }
                if (list.size >= pageSize) {
                    page++
                }
                roomListSubject.onNext(currentRoomList)
            }
        } else {
            roomListErrorSubject.onNext(Throwable(bean.msg))
        }
    }

    fun obVoiceRoomList(): Observable<List<VoiceRoomBean>> {
        return roomListSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun obVoiceRoomErrorEvent(): Observable<Throwable> {
        return roomListErrorSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun queryRoomInfoFromServer(roomId: String): Single<VoiceRoomInfoBean> {
        return RetrofitManager
            .commonService
            .getVoiceRoomInfo(roomId)
            .observeOn(loadSchedulers)
            .subscribeOn(loadSchedulers)
            .doOnSuccess {
                val currentRoom = currentRoomMap[roomId]
                if (currentRoom != null) {
                    if (it.room == null) {
                        currentRoomList.remove(currentRoom)
                        currentRoomMap.remove(roomId)
                    } else {
                        val indexOf = currentRoomList.indexOf(currentRoom)
                        if (indexOf >= 0) {
                            currentRoomList[indexOf] = it.room
                            currentRoomMap[roomId] = it.room
                            roomInfoChangeSubject.onNext(it.room)
                        }
                    }
                } else {
                    it.room?.let { room ->
                        currentRoomList.add(0, room)
                        currentRoomMap[room.roomId] = room
                        roomInfoChangeSubject.onNext(room)
                    }
                }
                roomListSubject.onNext(currentRoomList)
            }
    }

    fun getRoomInfo(roomId: String): Single<VoiceRoomBean> {
        return Single.create { emitter ->
            var roomInfo = currentRoomMap[roomId]
            if (roomInfo != null) {
                emitter.onSuccess(roomInfo)
            } else {
                queryRoomInfoFromServer(roomId)
                    .subscribe({
                        if (it.room == null) {
                            emitter.onError(RoomNotExistException())
                        } else {
                            emitter.onSuccess(it.room)
                        }
                    }, {
                        emitter.onError(it)
                    })
            }
        }
    }


}