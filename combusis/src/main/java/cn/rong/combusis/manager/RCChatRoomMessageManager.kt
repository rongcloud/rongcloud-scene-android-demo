/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.manager

import cn.rong.combusis.message.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.rong.imlib.IRongCoreCallback
import io.rong.imlib.IRongCoreEnum
import io.rong.imlib.RongCoreClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent

/**
 * @author gusd
 * @Date 2021/06/17
 */

private const val TAG = "RCChatRoomMessageManage"

object RCChatRoomMessageManager {

    private val messageSubject: PublishSubject<MessageWrapperModel> = PublishSubject.create()

    fun onReceiveMessage(roomId: String, messageContent: MessageContent) {
        messageSubject.onNext(MessageWrapperModel(roomId, messageContent))
    }

    fun sendChatMessages(
        roomId: String,
        messageContents: List<MessageContent>,
        showLocation: Boolean = true,
        onSuccess: ((messageId: Int) -> Unit)? = null,
        onError: ((errorCode: IRongCoreEnum.CoreErrorCode?, messageId: Int) -> Unit)? = null
    ) {
        messageContents.forEach {
            sendChatMessage(roomId, it, showLocation, onSuccess, onError)
        }
    }

    fun sendChatMessage(
        roomId: String,
        messageContent: MessageContent,
        showLocation: Boolean = true,
        onSuccess: ((messageId: Int) -> Unit)? = null,
        onError: ((errorCode: IRongCoreEnum.CoreErrorCode?, messageId: Int) -> Unit)? = null
    ) {
        RongCoreClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM,
            roomId,
            messageContent,
            null,
            null,
            object : IRongCoreCallback.ISendMessageCallback {
                override fun onAttached(message: Message?) {
                }

                override fun onSuccess(message: Message?) {
                    message?.let {
                        onSuccess?.invoke(it.messageId)
                        if (showLocation) {
                            messageSubject.onNext(MessageWrapperModel(roomId, messageContent))
                        }
                    }
                }

                override fun onError(
                    message: Message?,
                    coreErrorCode: IRongCoreEnum.CoreErrorCode?
                ) {
                    message?.let {
                        onError?.invoke(coreErrorCode, message.messageId)
                    }
                }
            }
        )
    }

    fun sendLocationMessage(
        roomId: String,
        message: String
    ) {
        messageSubject.onNext(
            MessageWrapperModel(roomId, RCChatroomLocationMessage()
                .apply {
                    content = message
                })
        )
    }

    fun sendLocationMessage(
        roomId: String,
        messageContent: MessageContent,
    ) {
        messageSubject.onNext(MessageWrapperModel(roomId, messageContent))
    }

    fun obMessageReceiveByRoomId(roomId: String): Observable<MessageContent> {
        return messageSubject.observeOn(AndroidSchedulers.mainThread()).filter {
            it.roomId == roomId
        }.map {
            return@map it.messageContent
        }
    }

    internal class MessageWrapperModel(
        val roomId: String,
        val messageContent: MessageContent
    )
}