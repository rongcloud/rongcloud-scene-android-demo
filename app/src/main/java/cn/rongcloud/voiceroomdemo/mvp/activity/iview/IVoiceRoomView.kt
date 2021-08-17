/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity.iview

import android.graphics.Point
import com.rongcloud.common.base.IBaseView
import cn.rongcloud.mvoiceroom.ui.uimodel.UiRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import io.rong.imlib.model.MessageContent

/**
 * @author gusd
 * @Date 2021/06/10
 */
interface IVoiceRoomView : IBaseView {
    fun onJoinRoomSuccess()
    fun initRoleView(roomInfo: UiRoomModel)


    fun leaveRoomSuccess()
    fun enterSeatSuccess()


    fun refreshOnlineUsersNumber(onlineUsersNumber: Int)

    /**
     * 依据 RoomInfo 刷新 UI
     */
    fun refreshRoomInfo(roomInfo: UiRoomModel)

    /**
     * 通知指定坐席信息发生了改变，刷新之
     */
    fun onSeatInfoChange(index: Int, uiSeatModel: UiSeatModel)
    fun onSeatListChange(uiSeatModelList: List<UiSeatModel>)
    fun sendTextMessageSuccess(message: String)
    fun showChatRoomMessage(messageContent: MessageContent)
    fun showPickReceived(isCreateReceive: Boolean, userId: String)
    fun switchToAdminRole(isAdmin: Boolean, roomInfo: UiRoomModel)
    fun changeStatus(status: Int)
    fun showUnReadRequestNumber(number: Int)
    fun showUnreadMessage(count: Int)
    fun showFov(from: Point?)
    fun showRevokeSeatRequest()
    fun showRoomClose()
    fun onMemberInfoChange()

}