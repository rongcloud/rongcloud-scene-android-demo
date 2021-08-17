/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting

import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel.*
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/22
 */

class RoomSettingPresenter @Inject constructor(
    view: IRoomSettingView,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    private var functionList: ArrayList<BaseFunctionModel> = arrayListOf()

    init {
        functionList.apply {
            add(LockRoomFunction(roomModel, view))
            add(RoomNameFunction(roomModel, view))
            add(BackgroundFunction(view))
            add(EnterSeatModelFunction(roomModel, view))
            add(MuteAllSeatFunction(roomModel, view))
            add(LockAllSeatFunction(roomModel, view))
            add(MuteAllRemoteFunction(roomModel, view))
            add(ChangeSeatFunction(roomModel, view))
            add(MusicFunction(roomModel, view))
        }
    }


    fun getButtons(): List<BaseFunctionModel> {
        return functionList
    }

    override fun onCreate() {
        super.onCreate()
        functionList.forEach {
            it.onCreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        functionList.forEach {
            it.onDestroy()
        }
    }
}