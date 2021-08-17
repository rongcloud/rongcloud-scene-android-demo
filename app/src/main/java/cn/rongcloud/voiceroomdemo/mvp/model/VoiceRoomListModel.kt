/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import android.util.Log
import cn.rongcloud.mvoiceroom.net.VoiceRoomNetManager
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomBean
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomInfoBean
import cn.rongcloud.mvoiceroom.net.bean.respond.VoiceRoomListBean
import cn.rongcloud.voiceroomdemo.throwable.RoomNotExistException
import com.rongcloud.common.base.BaseModel
import com.rongcloud.common.net.ApiConstant
import com.rongcloud.common.utils.LocalDataStore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author gusd
 * @Date 2021/06/09
 */
private const val TAG = "VoiceRoomListModel"

@Singleton
class VoiceRoomListModel @Inject constructor() : BaseModel {

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
        VoiceRoomNetManager
            .aRoomApi
            .getRoomList(page, pageSize)
            .observeOn(loadSchedulers)
            .subscribeOn(loadSchedulers)
            .subscribe({ bean ->
                onRoomListChange(bean)
                bean.data?.images?.let {
                    LocalDataStore.saveBackGroundUrl(it)
                }
            }, { t ->
                roomListErrorSubject.onNext(t)
            })
    }

    fun obRoomInfoChangeByRoomId(roomId: String): Observable<VoiceRoomBean> {
        return roomInfoChangeSubject.filter { it.roomId == roomId }
    }


    private fun onRoomListChange(bean: VoiceRoomListBean) {
        if (bean.code == ApiConstant.REQUEST_SUCCESS_CODE) {
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
        return VoiceRoomNetManager
            .aRoomApi
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
                            it.room?.let { room ->
                                currentRoomList[indexOf] = room
                                currentRoomMap[roomId] = room
                                roomInfoChangeSubject.onNext(room)
                            }
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

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    fun addRoomInfo(roomInfo: VoiceRoomBean) {
        currentRoomList.add(0, roomInfo)
        currentRoomMap.put(roomInfo.roomId, roomInfo)
        roomListSubject.onNext(currentRoomList)
    }


}