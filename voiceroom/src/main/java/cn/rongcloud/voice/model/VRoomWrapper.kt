/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.model

import android.app.Activity
import cn.rong.combusis.message.*
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider
import cn.rong.combusis.sdk.event.EventHelper
import cn.rong.combusis.sdk.event.listener.RoomListener
import cn.rong.combusis.sdk.event.listener.StatusListener
import cn.rongcloud.voice.R
import cn.rongcloud.voice.net.bean.request.*
import cn.rongcloud.voice.ui.uimodel.*
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import io.reactivex.rxjava3.core.*
import kotlinx.coroutines.*

private const val TAG = "VRoomWrapper"


class VRoomWrapper(activity: Activity?, val roomId: String) : RoomListener,
    StatusListener {
    // service room info
    lateinit var netRoomInfo: VoiceRoomBean

    // SDK room info
    var rcRoomInfo: RCVoiceRoomInfo? = null

    //member list
    val members = arrayListOf<Member>()

    // 房间麦序
    val seatInfos = arrayListOf<UiMusicModel>()

    val gifts = mapOf<String, Int>()

    // 礼物数据
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

    init {
        VoiceRoomProvider.provider().observeSingle(roomId) { roomBean ->
            netRoomInfo = roomBean
        }
        EventHelper.helper().regeister(
            roomId
        )
        EventHelper.helper().addRoomListener(this)
        EventHelper.helper().addStatusListener(this)
    }

    fun release() {
        EventHelper.helper().unregeister()
    }

    override fun onRoomInfo(roomInfo: RCVoiceRoomInfo) {
        this.rcRoomInfo = roomInfo
    }

    override fun onSeatList(seatInfos: MutableList<RCVoiceSeatInfo>) {

    }

    override fun onNotify(code: String?, content: String?) {
    }

    override fun onOnLineUserIds(userIds: MutableList<String>?) {
    }

    override fun onStatus(delay: Int) {
    }

    override fun onReceive(unReadCount: Int) {
    }

    override fun onSpeaking(index: Int, speaking: Boolean) {
    }
}