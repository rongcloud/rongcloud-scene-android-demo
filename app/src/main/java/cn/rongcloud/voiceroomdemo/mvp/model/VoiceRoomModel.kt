/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import android.util.Log
import cn.rongcloud.rtc.api.RCRTCAudioMixer
import cn.rongcloud.rtc.api.callback.RCRTCAudioMixingStateChangeListener
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import cn.rongcloud.voiceroomdemo.MyApp
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.showToast
import cn.rongcloud.voiceroomdemo.mvp.model.message.*
import cn.rongcloud.voiceroomdemo.net.RetrofitManager
import cn.rongcloud.voiceroomdemo.net.api.bean.request.*
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import cn.rongcloud.voiceroomdemo.ui.uimodel.*
import cn.rongcloud.voiceroomdemo.utils.AudioManagerUtil
import cn.rongcloud.voiceroomdemo.utils.LocalUserInfoManager
import cn.rongcloud.voiceroomdemo.utils.RCChatRoomMessageManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.ChatRoomInfo
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * @author gusd
 * @Date 2021/06/18
 */

private val map = HashMap<String, VoiceRoomModel>()
fun getVoiceRoomModelByRoomId(roomId: String): VoiceRoomModel {
    return map[roomId] ?: VoiceRoomModel(roomId).apply {
        map[roomId] = this
    }
}

val EMPTY_ROOM_INFO: VoiceRoomBean = VoiceRoomBean(roomId = "")

private const val TAG = "VoiceRoomModel"

const val EVENT_ROOM_CLOSE = "VoiceRoomClosed"
const val EVENT_BACKGROUND_CHANGE = "VoiceRoomBackgroundChanged"
const val EVENT_MANAGER_LIST_CHANGE = "VoiceRoomNeedRefreshManagerList"
const val EVENT_REJECT_MANAGE_PICK = "VoiceRoomRejectManagePick" // 拒绝上麦
const val EVENT_AGREE_MANAGE_PICK = "VoiceRoomAgreeManagePick" // 同意上麦

const val EVENT_KICK_OUT_OF_SEAT = "EVENT_KICK_OUT_OF_SEAT"
const val EVENT_REQUEST_SEAT_REFUSE = "EVENT_REQUEST_SEAT_REFUSE"
const val EVENT_REQUEST_SEAT_AGREE = "EVENT_REQUEST_SEAT_AGREE"

const val EVENT_REQUEST_SEAT_CANCEL = "EVENT_REQUEST_SEAT_CANCEL"


const val EVENT_KICKED_OUT_OF_ROOM = "EVENT_KICKED_OUT_OF_ROOM"


class VoiceRoomModel(val roomId: String) : RCVoiceRoomEventListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val roomMemberInfoList = arrayListOf<UiMemberModel>()

    private val roomMemberInfoMap = HashMap<String, UiMemberModel>()

    private val userMusicList = arrayListOf<UiMusicModel>()

    private val systemMusicList = arrayListOf<UiMusicModel>()

    private var isInitRoomSetting = false

    private val voiceRoomListModel by lazy {
        VoiceRoomListModel
    }

    private val dataModifyScheduler by lazy {
        Schedulers.computation()
    }
    val presents by lazy {
        return@lazy ArrayList<Present>(16).apply {
            add(Present(1, R.drawable.ic_present_0, "小心心", 1))
            add(Present(2, R.drawable.ic_present_1, "话筒", 2))
            add(Present(3, R.drawable.ic_present_2, "麦克风", 5))
            add(Present(4, R.drawable.ic_present_3, "萌小鸡", 10))
            add(Present(5, R.drawable.ic_present_4, "手柄", 20))
            add(Present(6, R.drawable.ic_present_5, "奖杯", 50))
            add(Present(7, R.drawable.ic_present_6, "火箭", 100))
            add(Present(8, R.drawable.ic_present_7, "礼花", 200))
            add(Present(9, R.drawable.ic_present_8, "玫瑰花", 10))
            add(Present(10, R.drawable.ic_present_9, "吉他", 20))
        }
    }

    private var dataModifyWorker: Scheduler.Worker = dataModifyScheduler.createWorker()

    val currentUISeatInfoList: ArrayList<UiSeatModel> = arrayListOf()

    /**
     * 房间信息发生改变的额订阅
     */
    private val roomInfoSubject: BehaviorSubject<UiRoomModel> = BehaviorSubject.create()

    /**
     * 座位数量发生改变的订阅
     */
    private val seatListChangeSubject: BehaviorSubject<List<UiSeatModel>> = BehaviorSubject.create()

    private val seatInfoChangeSubject: BehaviorSubject<UiSeatModel> = BehaviorSubject.create()

    private val onlineUserCountSubject: BehaviorSubject<Int> = BehaviorSubject.create()


    private val memberListChangeSubject = BehaviorSubject.create<List<UiMemberModel>>()

    private val memberInfoChangeSubject = BehaviorSubject.create<UiMemberModel>()


    private val pickSeatReceivedSubject = BehaviorSubject.create<String>()

    private val recordingStatusSubject = BehaviorSubject.create<Boolean>()

    private val privateMessageSubject = BehaviorSubject.create<Int>()

    private val userMusicListSubject = BehaviorSubject.create<List<UiMusicModel>>()

    private val systemMusicListSubject = BehaviorSubject.create<List<UiMusicModel>>()

    private val roomEventSubject: BehaviorSubject<Pair<String, ArrayList<String>>> =
        BehaviorSubject.create()

    private val refreshAllMemberList = PublishSubject.create<Unit>()

    val currentUIRoomInfo = UiRoomModel(roomInfoSubject)

    var recordingStatus = true
        private set(value) {
            field = value
            recordingStatusSubject.onNext(value)
        }

    fun obOnPickSeatReceived(): Observable<String> {
        return pickSeatReceivedSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obMemberListChange(): Observable<List<UiMemberModel>> {
        return memberListChangeSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obMemberInfoChange(): Observable<UiMemberModel> {
        return memberInfoChangeSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obRoomInfoChange(): Observable<UiRoomModel> {
        return roomInfoSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obSeatListChange(): Observable<List<UiSeatModel>> {
        return seatListChangeSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obSeatInfoChange(): Observable<UiSeatModel> {
        return seatInfoChangeSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obOnlineUserCount(): Observable<Int> {
        return onlineUserCountSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obRoomEventChange(): Observable<Pair<String, ArrayList<String>>> {
        return roomEventSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    fun obRequestSeatListChange(): Observable<List<UiMemberModel>> {
        return memberListChangeSubject.map { list ->
            return@map list.filter { model -> model.isRequestSeat && model.seatIndex == -1 }
        }.observeOn(AndroidSchedulers.mainThread())
    }

    fun obInviteSeatListChange(): Observable<List<UiMemberModel>> {
        return memberListChangeSubject.map { list ->
            return@map list.filter { model -> model.seatIndex == -1 }
        }.observeOn(AndroidSchedulers.mainThread())
    }

    fun obMemberInfoByUserId(userId: String): Observable<UiMemberModel> {
        return memberInfoChangeSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
            .filter { model -> model.userId == userId }
    }

    fun obRecordingStatusChange(): Observable<Boolean> {
        return recordingStatusSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun obUnreadMessageNumberChange(): Observable<Int> {
        return privateMessageSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun obUserMusicListChange(): Observable<List<UiMusicModel>> {
        return userMusicListSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun obSystemMusicListChange(): Observable<List<UiMusicModel>> {
        return systemMusicListSubject.observeOn(AndroidSchedulers.mainThread())
    }


    init {
        voiceRoomListModel
            .getRoomInfo(roomId)
            .subscribe({
                currentUIRoomInfo.roomBean = it
            }, {
                Log.e(TAG, "getRoomInfoError: ")
                roomInfoSubject.onError(it)
            })

        addDisposable(obSeatListChange()
            .observeOn(dataModifyScheduler)
            .subscribe { seatList ->
                val tempList = arrayListOf<UiMemberModel>()
                seatList.forEach { seat ->
                    seat.userId?.let {
                        roomMemberInfoMap[it]?.let { member ->
                            tempList.add(member)
                            member.seatIndex = seat.index
                        }
                    }
                }
                roomMemberInfoList
                    .filter { !tempList.contains(it) }
                    .forEach {
                        it.seatIndex = -1
                    }
                memberListChangeSubject.onNext(roomMemberInfoList)
            })

        addDisposable(obSeatInfoChange()
            .subscribe
            { seatInfo ->
                seatInfo.userId?.let {
                    roomMemberInfoMap[it]?.let { member ->
                        member.seatIndex = seatInfo.index
                        member.isInvitedInfoSeat = false
                    }
                }
            })

        RCRTCAudioMixer.getInstance().setAudioMixingStateChangeListener(object :
            RCRTCAudioMixingStateChangeListener() {
            override fun onMixEnd() {
                Log.d(TAG, "onMixEnd: ")
                playNextMusic()
            }

            override fun onStateChanged(
                p0: RCRTCAudioMixer.MixingState,
                p1: RCRTCAudioMixer.MixingStateReason?
            ) {
                Log.d(TAG, "onStateChanged: $p0")
                currentMusicState = p0
                when (p0) {
                    RCRTCAudioMixer.MixingState.PLAY -> {
                        userMusicList.forEach {
                            it.isPlaying = currentPlayMusic == it.url
                        }
                    }
                    RCRTCAudioMixer.MixingState.PAUSED -> {
                        userMusicList.forEach {
                            it.isPlaying = false
                        }
                    }
                    RCRTCAudioMixer.MixingState.STOPPED -> {
                        currentPlayMusic = null
                        userMusicList.forEach {
                            it.isPlaying = false
                        }
                    }
                }
                userMusicListSubject.onNext(userMusicList)
            }

            override fun onReportPlayingProgress(p0: Float) {
            }

        })

        addDisposable(refreshAllMemberList
            .debounce(30L, TimeUnit.MILLISECONDS)
            .subscribe {
                queryAllUserInfo()
            })

    }


    fun setAdmin(userId: String, isAdmin: Boolean): Single<Boolean> {
        return RetrofitManager
            .commonService
            .setAdmin(SettingAdminRequest(roomId, userId, isAdmin))
            .observeOn(dataModifyScheduler)
            .map {
                if (it.code == 10000) {
                    RCChatRoomMessageManager.sendChatMessage(roomId, RCChatroomAdmin()
                        .apply {
                            this.userId = userId
                            this.userName = getMemberInfoByUserIdOnlyLocal(userId)?.userName
                            this.isAdmin = isAdmin
                        })
                    roomMemberInfoList.firstOrNull { model ->
                        model.userId == userId
                    }?.let { model ->
                        model.isAdmin = isAdmin
                    }
                    RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_MANAGER_LIST_CHANGE, "")
                }
                return@map it.code == 10000
            }
    }

    fun setRoomLock(lock: Boolean, password: String?): Single<Boolean> {
        return RetrofitManager
            .commonService
            .setRoomPasswordRequest(
                RoomPasswordRequest(if (lock) 1 else 0, password, roomId)
            )
            .doOnSuccess {
                queryRoomInfoFromServer()
            }.map {
                return@map it.code == 10000
            }
    }

    fun setRoomBackground(backgroundUrl: String): Single<Boolean> {
        return RetrofitManager
            .commonService
            .setRoomBackgroundRequest(
                RoomBackgroundRequest(
                    backgroundUrl,
                    roomId
                )
            ).doOnSuccess {
                refreshRoomInfo()
                RCVoiceRoomEngine.getInstance()
                    .notifyVoiceRoom(EVENT_BACKGROUND_CHANGE, backgroundUrl)
            }.map {
                return@map it.code == 10000
            }
    }

    fun setRoomName(newName: String): Single<Boolean> {
        return RetrofitManager
            .commonService
            .setRoomName(RoomNameRequest(newName, roomId))
            .doOnSuccess {
                refreshRoomInfo()
                currentUIRoomInfo.rcRoomInfo?.let {
                    it.roomName = newName
                    RCVoiceRoomEngine.getInstance().setRoomInfo(it, object : RCVoiceRoomCallback {
                        override fun onError(code: Int, message: String?) {
                            Log.d(TAG, "setRoomName:onError: ")
                        }

                        override fun onSuccess() {
                            Log.d(TAG, "setRoomName:onSuccess: ")
                        }
                    })
                }
            }.map {
                return@map it.code == 10000
            }
    }

    fun refreshRoomInfo() {
        queryRoomInfoFromServer()
    }

    fun getSeatInfoByUserId(userId: String?): UiSeatModel? {
        if (userId.isNullOrEmpty()) {
            return null
        }
        return currentUISeatInfoList.firstOrNull { it.userId == userId }
    }


    fun isInSeat(userId: String): Int {
        return currentUISeatInfoList.indexOfFirst { uiSeatModel -> uiSeatModel.userId == userId }
    }

    private fun doOnDataScheduler(block: () -> Unit) {
        dataModifyWorker.schedule{
            try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, "doOnDataScheduler: ", e)
            }
        }
    }

    fun getMemberInfoByUserIdOnlyLocal(userId: String?): UiMemberModel? {
        if (userId.isNullOrEmpty()) {
            return null
        }
        return roomMemberInfoList.find { member -> member.userId == userId }
            ?: LocalUserInfoManager.getMemberByUserId(userId)?.run {
                UiMemberModel(memberInfoChangeSubject).apply {
                    this.member = member
                }
            }
    }


    fun addDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }


    fun onDestroy() {
        dataModifyWorker.dispose()
        map.remove(roomId)
        compositeDisposable.dispose()
    }

    fun kickSeat(
        userId: String
    ): Completable {
        return Completable.create {
            RCVoiceRoomEngine.getInstance().kickUserFromSeat(userId, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    it.onError(Throwable(message))
                }

                override fun onSuccess() {
                    it.onComplete()
                }
            })
        }
    }

    fun setSeatMode(isFreeEnterSeatModel: Boolean): Single<Boolean> {
        return Single.create { emitter ->
            currentUIRoomInfo.rcRoomInfo?.let {
                it.isFreeEnterSeat = isFreeEnterSeatModel
                RCVoiceRoomEngine.getInstance().setRoomInfo(it, object : RCVoiceRoomCallback {
                    override fun onError(code: Int, message: String?) {
                        Log.e(TAG, "onError: $message")
                        emitter.onError(Throwable(message))
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "setRoomName:onSuccess: ")
                        emitter.onSuccess(true)
                        pushRoomSettingToServer(applyOnMic = !isFreeEnterSeatModel)
                    }
                })
            } ?: emitter.onError(Throwable("state error"))
        }
    }

    fun setAllSeatMute(isMuteAll: Boolean): Completable {
        return Completable.create { emitter ->
            currentUIRoomInfo.rcRoomInfo?.let {
                RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll)
                pushRoomSettingToServer(applyAllLockMic = isMuteAll)
                emitter.onComplete()
            } ?: emitter.onError(Throwable("state error"))
        }

    }

    fun setAllSeatLock(isLockAll: Boolean): Completable {
        return Completable.create { emitter ->
            currentUIRoomInfo.rcRoomInfo?.let {
                RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll)
                pushRoomSettingToServer(applyAllLockSeat = isLockAll)
                emitter.onComplete()
            } ?: emitter.onError(Throwable("state error"))
        }
    }

    fun muteAllRemoteStreams(isMute: Boolean): Completable {
        return Completable.create { emitter ->
            currentUIRoomInfo.rcRoomInfo?.let {
                RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute)
                currentUIRoomInfo.isMute = isMute
                pushRoomSettingToServer(setMute = isMute)
                emitter.onComplete()
            } ?: emitter.onError(Throwable("state error"))
        }
    }

    fun setSeatCount(count: Int) {
        currentUIRoomInfo.rcRoomInfo?.let {
            it.seatCount = count
            RCVoiceRoomEngine.getInstance().setRoomInfo(it, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    Log.e(TAG, "onError: ")
                }

                override fun onSuccess() {
                    pushRoomSettingToServer(setSeatNumber = count)
                    RCChatRoomMessageManager.sendChatMessage(roomId, RCChatroomSeats()
                        .apply {
                            this.count = count - 1
                        })
                }
            })
        }
    }


    override fun onRoomKVReady() {
        Log.d(TAG, "onRoomKVReady: ")
    }

    override fun onRoomInfoUpdate(rcRoomInfo: RCVoiceRoomInfo) {
        Log.d(TAG, "onRoomInfoChanged: $rcRoomInfo")
        currentUIRoomInfo.rcRoomInfo = rcRoomInfo
        if (!isInitRoomSetting) {
            isInitRoomSetting = true
            refreshRoomInfo()
//            refreshRoomSetting()
        }
    }

    override fun onSeatInfoUpdate(seatInfoList: MutableList<RCVoiceSeatInfo>) {
        doOnDataScheduler {
            Log.d(TAG, "onSeatInfoUpdate: $seatInfoList")

            val list = seatInfoList.mapIndexed { index, rcVoiceSeatInfo ->
                val uiSeatModel = UiSeatModel(
                    index,
                    rcVoiceSeatInfo,
                    seatInfoChangeSubject
                )
                uiSeatModel.member = getMemberInfoByUserIdOnlyLocal(uiSeatModel.userId)
                return@mapIndexed uiSeatModel
            }
            currentUISeatInfoList.clear()
            currentUISeatInfoList.addAll(list)
            seatListChangeSubject.onNext(currentUISeatInfoList)
        }
    }

    override fun onUserEnterSeat(seatIndex: Int, userId: String) {
        doOnDataScheduler {
            Log.d(TAG, "onUserEnterSeat: index = $seatIndex,userId = $userId")
            currentUISeatInfoList.elementAtOrNull(seatIndex)?.let {
                it.userId = userId
            }
        }

    }

    override fun onUserLeaveSeat(seatIndex: Int, userId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onUserLeaveSeat: seatIndex = $seatIndex,userId = $userId")
            currentUISeatInfoList
                .elementAtOrNull(seatIndex)?.let { model ->
                    Log.d(TAG, "onUserLeaveSeat: $model")
//                    model.userId = null
                    model.member = null
                }
        }

    }

    override fun onSeatMute(index: Int, isMute: Boolean) {
        doOnDataScheduler {
            Log.d(TAG, "onSeatMute: index = $index,isMute = $isMute")
            currentUISeatInfoList.elementAtOrNull(index)?.let { model ->
                model.isMute = isMute

            }
        }

    }

    override fun onSeatLock(index: Int, isLock: Boolean) {
        doOnDataScheduler {
            Log.d(TAG, "onSeatLock: index = $index , isLock = $isLock")
            currentUISeatInfoList.elementAtOrNull(index)?.let {
                it.seatStatus =
                    if (isLock) RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking else RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
            }
        }
    }

    override fun onAudienceEnter(userId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onAudienceEnter: userId = $userId")
//            refreshAllMemberInfoList()

            getOnLineUsersCount()
        }
    }

    override fun onAudienceExit(userId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onAudienceExit: userId = $userId")
            getOnLineUsersCount()
            roomMemberInfoMap[userId]?.let {
                roomMemberInfoList.remove(it)
                roomMemberInfoMap.remove(userId)
            }
        }
    }

    override fun onSpeakingStateChanged(seatIndex: Int, isSpeaking: Boolean) {
        doOnDataScheduler {
            val uiSeatModel = currentUISeatInfoList[seatIndex]
            uiSeatModel.isSpeaking = isSpeaking
        }
    }

    override fun onMessageReceived(message: Message) {
        doOnDataScheduler {
            if (message.conversationType == Conversation.ConversationType.PRIVATE) {
                noticeRefreshUnreadMessageCount()
            }
        }
    }

    fun noticeRefreshUnreadMessageCount() {
        RongIMClient.getInstance().getUnreadCount(
            object :
                RongIMClient.ResultCallback<Int>() {
                override fun onSuccess(p0: Int) {
                    privateMessageSubject.onNext(p0)
                }

                override fun onError(p0: RongIMClient.ErrorCode) {

                }
            },
            Conversation.ConversationType.PRIVATE
        )
    }

    fun obSeatInfoByIndex(index: Int): Observable<UiSeatModel> {
        return seatListChangeSubject.map { seatList ->
            seatList.elementAtOrNull(index) ?: UiSeatModel(
                -1,
                RCVoiceSeatInfo(),
                seatInfoChangeSubject
            )
        }.observeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(dataModifyScheduler)
    }

    override fun onRoomNotificationReceived(name: String?, content: String?) {
        roomEventSubject.onNext(Pair(name ?: "", arrayListOf((content ?: ""))))
    }

    override fun onPickSeatReceivedFrom(userId: String) {
        doOnDataScheduler {
            Log.d(TAG, "onPickSeatReceivedFrom: userId = $userId")
            val seatInfoByUserId = getSeatInfoByUserId(AccountStore.getUserId()!!)
            if (seatInfoByUserId != null) {
                // 当前用户在座位上
                return@doOnDataScheduler
            }
            pickSeatReceivedSubject.onNext(userId)
        }

    }

    override fun onKickSeatReceived(index: Int) {
        doOnDataScheduler {
            Log.d(TAG, "onKickSeatReceived: index = $index")
            roomEventSubject.onNext(Pair(EVENT_KICK_OUT_OF_SEAT, arrayListOf()))
            AudioManagerUtil.choiceAudioModel()
        }

    }

    override fun onRequestSeatAccepted() {
        doOnDataScheduler {
            Log.d(TAG, "onRequestSeatAccepted: ")
            roomEventSubject.onNext(Pair(EVENT_REQUEST_SEAT_AGREE, arrayListOf()))
        }
    }

    override fun onRequestSeatRejected() {
        doOnDataScheduler {
            Log.d(TAG, "onRequestSeatRejected: ")
            roomEventSubject.onNext(Pair(EVENT_REQUEST_SEAT_REFUSE, arrayListOf()))
        }
    }

    override fun onRequestSeatListChanged() {
        doOnDataScheduler {
            Log.d(TAG, "onRequestSeatListChanged: ")
            refreshRequestSeatUserList()
        }

    }

    override fun onInvitationReceived(invitationId: String?, userId: String?, content: String?) {
        doOnDataScheduler {
            Log.d(
                TAG,
                "onInvitationReceived: invitationId = $invitationId,userId = $userId,content = $content"
            )
        }

    }

    override fun onInvitationAccepted(invitationId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onInvitationAccepted: invitationId = $invitationId")
        }

    }

    override fun onInvitationRejected(invitationId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onInvitationRejected: invitationId = $invitationId")
        }

    }

    override fun onInvitationCancelled(invitationId: String?) {
        doOnDataScheduler {
            Log.d(TAG, "onInvitationCancelled: invitationId = $invitationId")
        }

    }

    override fun onUserReceiveKickOutRoom(targetId: String, userId: String) {
        doOnDataScheduler {
            Log.d(TAG, "onUserReceiveKickOutRoom: targetId = $targetId,userId = $userId")
            roomEventSubject.onNext(Pair(EVENT_KICKED_OUT_OF_ROOM, arrayListOf(userId, targetId)))
        }

    }


    fun getOnLineUsersCount() {
        RongIMClient
            .getInstance()
            .getChatRoomInfo(roomId,
                0,
                ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC,
                object : RongIMClient.ResultCallback<ChatRoomInfo>() {
                    override fun onSuccess(chatRoomInfo: ChatRoomInfo?) {
                        chatRoomInfo?.let {
                            onlineUserCountSubject.onNext(it.totalMemberCount)
                        }
                    }

                    override fun onError(e: RongIMClient.ErrorCode?) {
                        Log.d(TAG, "onError: getOnLineUsersCount : ${e?.message}")
                    }
                })
    }

    private fun queryRoomInfoFromServer() {
        addDisposable(RetrofitManager
            .commonService
            .getVoiceRoomInfo(roomId)
            .subscribe { bean ->
                currentUIRoomInfo.roomBean = bean.room
            })
    }

    private fun pushRoomSettingToServer(
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
        addDisposable(
            RetrofitManager
                .commonService
                .setRoomSetting(setting).subscribe()
        )
    }

    fun refreshAdminList() {
        addDisposable(RetrofitManager
            .commonService
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
                noticeMemberListUpdate()
            })
    }

    fun refreshGift() {
        addDisposable(RetrofitManager
            .commonService
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
                        }
                    }
                }
            })
    }

    fun removeMemberFromList(userId: String) {
        roomMemberInfoMap[userId]?.let {
            roomMemberInfoList.remove(it)
            roomMemberInfoMap.remove(userId)
        }
    }


    fun refreshAllMemberInfoList() {
        refreshAllMemberList.onNext(Unit)
    }

    fun queryAllUserInfo(
        needRefreshRequestSeatUserList: Boolean = false,
        onComplete: (() -> Unit)? = null
    ) {
        addDisposable(RetrofitManager
            .commonService
            .getMembersList(roomId)
            .observeOn(dataModifyScheduler)
            .subscribeOn(Schedulers.io())
            .map { membersBean ->
                // 更新数据时避免数据刷新丢失，尽可能的复用旧对象
                membersBean.data?.forEach { member ->
                    val uiMemberModel = roomMemberInfoMap[member.userId]
                    if (uiMemberModel != null) {
                        // 更新已有数据
                        uiMemberModel.member = member
                    } else {
                        // 添加新数据
                        roomMemberInfoMap[member.userId] =
                            UiMemberModel(memberInfoChangeSubject).apply {
                                this.member = member
                                roomMemberInfoList.add(this)
                            }
                    }
                    LocalUserInfoManager.addUserInfoToCache(member)
                }
                // 移除过期数据
                membersBean.data?.let { members ->
                    val invalidData =
                        roomMemberInfoList.filter { uiModel -> members.firstOrNull { member -> member.userId == uiModel.userId } == null }
                    if (invalidData.isNotEmpty()) {
                        roomMemberInfoList.removeAll(invalidData)
                        invalidData.forEach {
                            roomMemberInfoMap.remove(it.userId)
                        }
                    }
                }
                memberListChangeSubject.onNext(roomMemberInfoList)
                return@map roomId
            }.flatMap {
                return@flatMap RetrofitManager.commonService.getAdminList(it)
            }.map {
                it.data?.let { adminList ->
                    roomMemberInfoList.forEach { member ->
                        member.isAdmin =
                            adminList.firstOrNull { admin -> admin.userId == member.userId } != null
                    }
                }
                return@map roomId
            }.flatMap {
                return@flatMap RetrofitManager.commonService.getGiftList(it)
            }.map {
                it.data?.let { listMap ->
                    listMap.forEach { map ->
                        map.forEach { entry ->
                            roomMemberInfoList.firstOrNull { member ->
                                member.userId == entry.key
                            }?.giftCount = entry.value
                        }
                    }
                }
            }.doOnSuccess {
                if (needRefreshRequestSeatUserList) {
                    refreshRequestSeatUserList()
                }
            }
            .subscribe({
                onComplete?.invoke()
            }, {
                onComplete?.invoke()
            })
        )
    }

    fun queryUserInfoFromLocalAndServer(userId: String, onComplete: ((UiMemberModel?) -> Unit)?) {
        var memberModel = getMemberInfoByUserIdOnlyLocal(userId)
        if (memberModel?.member == null) {
            queryAllUserInfo {
                memberModel = getMemberInfoByUserIdOnlyLocal(userId)
                onComplete?.invoke(memberModel)
            }
        } else {
            onComplete?.invoke(memberModel)
        }
    }

    fun userInSeat(userId: String): Boolean {
        return currentUISeatInfoList.find { it.userId == userId } != null
    }

    fun requestSeat(callback: (success: Boolean) -> Unit) {
        RCVoiceRoomEngine.getInstance().requestSeat(object : RCVoiceRoomCallback {
            override fun onError(code: Int, message: String?) {
                Log.d(TAG, "onError: requestSeat = $message")
                callback(false)
            }

            override fun onSuccess() {
                Log.d(TAG, "onError: requestSeat = success")
                callback(true)
            }
        })
    }

    private fun refreshRequestSeatUserList() {
        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(object :
            RCVoiceRoomResultCallback<List<String>> {
            override fun onError(code: Int, message: String?) {
                Log.e(TAG, "onError: code:$code ,message = $message")
            }

            override fun onSuccess(data: List<String>) {
                doOnDataScheduler {
                    var allUserInList = true
                    data.forEach { userId ->
                        if (roomMemberInfoMap[userId] == null) {
                            allUserInList = false
                        }
                    }
                    if (allUserInList) {
                        roomMemberInfoList.forEach {
                            it.isRequestSeat = data.contains(it.userId)
                        }
                        memberListChangeSubject.onNext(roomMemberInfoList)
                    } else {
                        queryAllUserInfo(false)
                    }
                }
            }
        })
    }

    fun invitedIntoSeat(userId: String): Completable {
        return Completable.create {
            if (getAvailableIndex() < 0) {
                it.onError(Throwable("麦位已满"))
                return@create
            }

            RCVoiceRoomEngine.getInstance().pickUserToSeat(userId, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    Log.e(TAG, "invitedIntoSeat:onError: code = $code,message = $message")
                    it.onError(Throwable(message))
                }

                override fun onSuccess() {
                    doOnDataScheduler {
                        getMemberInfoByUserIdOnlyLocal(userId)?.run {
                            isInvitedInfoSeat = true
                            memberListChangeSubject.onNext(roomMemberInfoList)
                        }
                        it.onComplete()
                    }
                }
            })
        }.subscribeOn(dataModifyScheduler)
    }

    fun kickRoom(userId: String): Completable {
        Log.d(TAG, "kickRoom: ")
        return Completable.create {
            queryUserInfoFromLocalAndServer(userId) { member ->
                RCVoiceRoomEngine.getInstance()
                    .kickUserFromRoom(userId, object : RCVoiceRoomCallback {
                        override fun onError(code: Int, message: String?) {
                            it.onError(Throwable(message))
                        }

                        override fun onSuccess() {
                            doOnDataScheduler {
                                val uiMemberModel = roomMemberInfoMap[userId]
                                uiMemberModel?.let { model ->
                                    roomMemberInfoMap.remove(model.userId)
                                    roomMemberInfoList.remove(model)
                                    memberListChangeSubject.onNext(roomMemberInfoList)
                                }

                                member?.let {
                                    RCChatRoomMessageManager.sendChatMessage(
                                        roomId,
                                        RCChatroomKickOut()
                                            .apply {
                                                this.userId = AccountStore.getUserId()
                                                this.userName = AccountStore.getUserName()
                                                this.targetId = it.userId
                                                this.targetName = it.userName
                                            }, true
                                    )
                                }

                                it.onComplete()
                            }
                        }
                    })
            }
        }.subscribeOn(dataModifyScheduler)

    }

    fun setSeatLockByUserId(userId: String): Completable {
        return Completable.create { emitter ->
            getSeatInfoByUserId(userId)?.let { seatModel ->
                kickSeat(userId).subscribe({
                    RCVoiceRoomEngine.getInstance().lockSeat(
                        seatModel.index,
                        true,
                        object : RCVoiceRoomCallback {
                            override fun onError(code: Int, message: String?) {
                                emitter.onError(Throwable(message))
                            }

                            override fun onSuccess() {
                                emitter.onComplete()
                            }
                        })
                }, {
                    emitter.onError(Throwable(it.message))
                })
            } ?: emitter.onError(Throwable("user not in seat"))
        }.subscribeOn(dataModifyScheduler)
    }

    fun setSeatMuteByUserId(userId: String, isMute: Boolean): Completable {
        return Completable.create { emitter ->
            getSeatInfoByUserId(userId)?.let { seatModel ->
                RCVoiceRoomEngine.getInstance()
                    .muteSeat(seatModel.index, isMute, object : RCVoiceRoomCallback {
                        override fun onError(code: Int, message: String?) {
                            emitter.onError(Throwable(message))
                        }

                        override fun onSuccess() {
                            emitter.onComplete()
                        }
                    })
            } ?: emitter.onError(Throwable("user not in seat"))
        }.subscribeOn(dataModifyScheduler)
    }

    fun getAvailableIndex(): Int {
        currentUISeatInfoList.forEachIndexed { index, seatModel ->
            if (seatModel.seatStatus == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty && index != 0) {
                return index
            }
        }
        return -1
    }

    fun leaveSeat(userId: String): Completable {
        return Completable.create { emitter ->
            val seatInfo = getSeatInfoByUserId(userId)
            seatInfo?.let {
                RCVoiceRoomEngine.getInstance().leaveSeat(object : RCVoiceRoomCallback {
                    override fun onError(code: Int, message: String?) {
                        emitter.onError(Throwable(message))
                    }

                    override fun onSuccess() {
                        if (currentMusicState == RCRTCAudioMixer.MixingState.PLAY
                            || currentMusicState == RCRTCAudioMixer.MixingState.PAUSED
                        ) {
                            stopPlayMusic()
                        }
                        emitter.onComplete()
                        AudioManagerUtil.choiceAudioModel()
                    }
                })
            } ?: emitter.onError(Throwable("user not in seat"))

        }.subscribeOn(dataModifyScheduler)
    }

    fun setSeatLock(index: Int, isLock: Boolean): Completable {
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().lockSeat(index, isLock, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    emitter.onError(Throwable(message))
                }

                override fun onSuccess() {
                    emitter.onComplete()
                }
            })
        }
    }

    fun setSeatMute(index: Int, isMute: Boolean): Completable {
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().muteSeat(index, isMute, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    emitter.onError(Throwable(message))
                }

                override fun onSuccess() {
                    emitter.onComplete()
                }
            })
        }
    }

    fun creatorMuteSelf(isMute: Boolean): Completable {
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().disableAudioRecording(isMute)
            RCVoiceRoomEngine.getInstance().muteSeat(0, isMute, object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    emitter.onError(Throwable(message))
                }

                override fun onSuccess() {
                    emitter.onComplete()
                }
            })
        }
    }


    fun isAdmin(userId: String): Boolean {
        return (roomMemberInfoList.firstOrNull { model ->
            model.userId == userId
        }?.isAdmin) ?: false
    }


    fun cancelRequest(): Completable {
        return Completable.create {
            RCVoiceRoomEngine.getInstance().cancelRequestSeat(object : RCVoiceRoomCallback {
                override fun onError(code: Int, message: String?) {
                    it.onError(Throwable(message))
                }

                override fun onSuccess() {
                    roomEventSubject.onNext(Pair(EVENT_REQUEST_SEAT_CANCEL, arrayListOf()))
                    it.onComplete()
                }
            })
        }
    }

    fun acceptRequest(userId: String): Completable {
        return Completable.create {
            var availableIndex = getAvailableIndex()
            if (availableIndex < 0) {
                it.onError(Throwable("房间麦位已满"))
            } else {
                RCVoiceRoomEngine.getInstance()
                    .acceptRequestSeat(userId, object : RCVoiceRoomCallback {
                        override fun onError(code: Int, message: String?) {
                            Log.d(TAG, "onError: code = $code,message = $message")
                            it.onError(Throwable(message))
                        }

                        override fun onSuccess() {
                            it.onComplete()
                        }
                    })
            }
        }
    }

    fun sendGift(
        members: List<UiMemberModel>,
        present: Present,
        num: Int
    ): Observable<List<UiMemberModel>> {

        val result = arrayListOf<UiMemberModel>()

        val publisherList = members.map { model ->
            RetrofitManager.commonService.sendGifts(
                SendGiftsRequest(
                    present.index,
                    num,
                    roomId,
                    model.userId
                )
            ).doOnSuccess {
                if (it.code == 10000) {
                    result.add(model)
                }
            }.toFlowable()
        }.toTypedArray()


        return Observable.create<List<UiMemberModel>> { emitter ->
            addDisposable(
                Flowable
                    .concatArray(*publisherList)
                    .subscribe({
                    }, {
                        emitter.onError(it)
                    }, {
                        emitter.onNext(result)
                    })
            )
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


    fun setRecordingEnable(enable: Boolean): Completable {
        return Completable.create { emitter ->
            RCVoiceRoomEngine.getInstance().disableAudioRecording(!enable)
            recordingStatus = enable
            emitter.onComplete()
        }
    }

    fun noticeMemberListUpdate() {
        memberListChangeSubject.onNext(roomMemberInfoList)
    }

    private fun querySystemMusicList(): Single<List<UiMusicModel>> {
        return queryMusicListByType(MUSIC_TYPE_SYSTEM)
    }

    private fun queryCustomizeMusicList(): Single<List<UiMusicModel>> {
        return queryMusicListByType(MUSIC_FROM_TYPE_LOCAL)
    }

    private fun queryMusicListByType(type: Int): Single<List<UiMusicModel>> {
        return RetrofitManager
            .commonService
            .getMusicList(MusicListRequest(roomId, type))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                return@map it.data?.map { bean -> UiMusicModel.create(bean) } ?: emptyList()
            }
    }


    fun refreshMusicList(onComplete: (() -> Unit)? = null) {
        addDisposable(
            Observable.combineLatest(
                querySystemMusicList().toObservable(),
                queryCustomizeMusicList().toObservable(),
                BiFunction { systemList, customList ->
                    systemList.forEach { sysModel ->
                        sysModel.addAlready = customList.firstOrNull {
                            sysModel.url == it.url
                        } != null
                    }
                    userMusicList.clear()
                    userMusicList.addAll(customList)
                    if (currentMusicState == RCRTCAudioMixer.MixingState.PLAY) {
                        userMusicList.forEach {
                            it.isPlaying = currentPlayMusic == it.url
                        }
                    }

                    systemMusicList.clear()
                    systemMusicList.addAll(systemList)
                    userMusicListSubject.onNext(customList)
                    systemMusicListSubject.onNext(systemList)
                    return@BiFunction emptyList<UiMusicModel>()
                }).subscribe {
                onComplete?.invoke()
            }
        )
    }

    fun addMusic(
        name: String,
        author: String? = "",
        type: Int = 0,
        url: String,
        size: Long? = null
    ): Completable {
        Log.d(TAG, "addMusic: name = $name,author = $author,type = $type,url = $url")
        return Completable.create { emitter ->
            addDisposable(
                RetrofitManager
                    .commonService
                    .addMusic(
                        AddMusicRequest(
                            name = name,
                            author = author,
                            roomId = roomId,
                            type = type,
                            url = url,
                            size = size
                        )
                    ).subscribe({ result ->
                        if (result.code == 10000) {
                            refreshMusicList {
                                if (userMusicList.size == 1) {
                                    Log.d(TAG, "addMusic: list only one music,start play")
                                    userMusicList.elementAtOrNull(0)?.url?.let {
                                        playMusic(name, it)
                                    }
                                }
                            }
                            emitter.onComplete()
                        } else {
                            emitter.onError(Throwable(result.msg))
                        }
                    }, {
                        emitter.onError(it)
                    })
            )
        }

    }

    fun deleteMusic(url: String, id: Int): Completable {
        return Completable.create { emitter ->
            RetrofitManager
                .commonService
                .musicDelete(DeleteMusicRequest(id, roomId))
                .subscribe({
                    if (it.code == 10000) {
                        if (url == currentPlayMusic) {

                            try {
                                stopPlayMusic()
                            } catch (e: Exception) {
                                Log.e(TAG, "deleteMusic: ", e)
                            }
                        }
                        refreshMusicList()
                        emitter.onComplete()
                    } else {
                        emitter.onError(Throwable(it.msg))
                    }
                }, {
                    emitter.onError(it)
                })
        }
    }

    private var currentPlayMusic: String? = null
    private var currentMusicState = RCRTCAudioMixer.MixingState.STOPPED


    fun playOrPauseMusic(model: UiMusicModel) {
        if (getSeatInfoByUserId(AccountStore.getUserId()) == null) {
            MyApp.context.showToast("请先上麦之后再播放音乐")
            return
        }
        playNextMusicJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }

        if (currentPlayMusic.isNullOrEmpty()) {
            // 当前没在播放，直接播放
            model.url?.let { playMusic(model.name, it) }
        } else if (currentPlayMusic != model.url) {
            // 当前在播放的和选择的不同,停止播放旧的，直接播放新的
            stopPlayMusic()
            model.url?.let { playMusic(model.name, it) }
        } else {
            // 暂停
            if (currentMusicState == RCRTCAudioMixer.MixingState.PAUSED) {
                RCRTCAudioMixer.getInstance().resume()
            } else if (currentMusicState == RCRTCAudioMixer.MixingState.PLAY) {
                RCRTCAudioMixer.getInstance().pause()
            }
        }
    }

    private fun playMusic(name: String? = "", url: String) {
        musicStopFlag = false
        GlobalScope.launch(Dispatchers.IO) {
            addDisposable(
                FileModel
                    .checkOrDownLoadMusic(name ?: "", url)
                    .subscribe({
                        if (musicStopFlag) {
                            return@subscribe
                        }
                        currentPlayMusic = url
                        val path = FileModel.getCompleteMusicPathByName(
                            FileModel.getNameFromUrl(url) ?: ""
                        )
                        Log.d(TAG, "playMusic: path = $path")
                        RCRTCAudioMixer.getInstance()
                            .startMix(path, RCRTCAudioMixer.Mode.MIX, true, 1)
                    }, {
                        MyApp.context.showToast(it.message)
                    })
            )
        }
    }

    fun moveMusicToTop(model: UiMusicModel): Completable {
        return Completable.create { emitter ->

            if (currentPlayMusic.isNullOrEmpty() || currentMusicState == RCRTCAudioMixer.MixingState.PAUSED) {
                model.url?.let {
                    stopPlayMusic()
                    playMusic(model.name, it)
                    emitter.onComplete()
                }
            } else {
                val currentMusic = userMusicList.lastOrNull {
                    it.url == currentPlayMusic
                }
                RetrofitManager.commonService.modifyMusicOrder(
                    MusicOrderRequest(roomId, model.id, currentMusic?.id ?: 0)
                ).subscribe({
                    if (it.code == 10000) {
                        refreshMusicList()
                        emitter.onComplete()
                    } else {
                        emitter.onError(Throwable(it.msg))
                    }
                }, {
                    emitter.onError(it)
                })
            }
        }
    }

    private var playNextMusicJob: Job? = null
    private fun playNextMusic() {
        Log.d(TAG, "playNextMusic: ")
        if (playNextMusicJob?.isActive == true) {
            playNextMusicJob?.cancel()
        }
        playNextMusicJob = GlobalScope.launch(Dispatchers.IO) {
            // FIXME: 2021/7/12 sdk 混音存在问题，添加延迟临时处理
            val index = userMusicList.indexOfLast {
                it.url == currentPlayMusic
            }
            Log.d(TAG, "playNextMusic: index = $index")
            currentPlayMusic = null
            // 及时通知上层音乐播放完成
            userMusicListSubject.onNext(userMusicList)
            userMusicList
                .elementAtOrNull((index + 1) % userMusicList.size)?.let {
                    Log.d(TAG, "playNextMusic: $it")
                    delay(1000)
                    if (musicStopFlag) {
                        return@launch
                    }
                    it.url?.let { it1 -> playMusic(it.name, it1) }
                } ?: run {
                Log.d(TAG, "playNextMusic: not find next music,try play first index music")
                if (userMusicList.size > 0) {
                    userMusicList[0].let {
                        delay(1000)
                        if (musicStopFlag) {
                            return@launch
                        }
                        it.url?.let { it1 -> playMusic(it.name, it1) }
                    }
                }
            }
        }
    }

    fun onLeaveRoom() {
        if (currentMusicState == RCRTCAudioMixer.MixingState.PAUSED
            || currentMusicState == RCRTCAudioMixer.MixingState.PLAY
        ) {
            stopPlayMusic()
        }
    }

    fun stopPlayMusic() {
        try {
            musicStopFlag = true
            if (playNextMusicJob?.isActive == true) {
                playNextMusicJob?.cancel()
            }
            RCRTCAudioMixer.getInstance().stop()
        } catch (e: Exception) {
            Log.e(TAG, "stopPlayMusic: ", e)
        }
    }

    fun isPlayingMusic(): Boolean {
        // 暂停状态下不视为音乐正在播放
        return (currentPlayMusic != null && currentMusicState == RCRTCAudioMixer.MixingState.PLAY)
                || (playNextMusicJob?.isCompleted == false)
    }

    /**
     * 用于记录音乐停止的状态
     */
    @Volatile
    private var musicStopFlag = true

}