/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.mvp.model.message.*
import io.rong.imlib.model.MessageContent
import kotlinx.android.synthetic.main.layout_system_message_item.view.*

/**
 * @author gusd
 * @Date 2021/06/27
 */

private const val MESSAGE_TYPE_SYSTEM = 0
private const val MESSAGE_TYPE_NORMAL = 1

class VoiceRoomMessageAdapter(roomId: String, val listener: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<MessageContent>()

    private val roomModel: VoiceRoomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is RCChatroomLocationMessage) MESSAGE_TYPE_SYSTEM else MESSAGE_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_SYSTEM) SystemMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_system_message_item, parent, false
            )
        ) else NormalMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_normal_message_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MESSAGE_TYPE_SYSTEM) {
            (holder as SystemMessageViewHolder).bind(data[position] as RCChatroomLocationMessage)
        } else {
            (holder as NormalMessageViewHolder).bind(data[position], roomModel, listener)
        }
    }

    fun addMessage(message: MessageContent) {
        data.add(message)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class SystemMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: RCChatroomLocationMessage) {
            with(itemView) {
                tv_message_content.text = message.content
            }
        }
    }

    class NormalMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateRole(userId: String, view: TextView, roomModel: VoiceRoomModel) {
            if (roomModel.currentUIRoomInfo.roomBean?.createUser?.userId == userId) {
                view.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_creator,
                    0,
                    0,
                    0
                )
                view.compoundDrawablePadding = 2
            } else if (roomModel.getMemberInfoByUserIdOnlyLocal(userId)?.isAdmin == true
            ) {
                view.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_is_admin,
                    0,
                    0,
                    0
                )
                view.compoundDrawablePadding = 2
            }
        }

        fun bind(message: MessageContent, roomModel: VoiceRoomModel, listener: (String) -> Unit) {
            with(itemView) {
                var list = ArrayList<MsgInfo>(4)
                tv_message_content.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )
                tv_message_content.compoundDrawablePadding = 0
                when (message) {
                    is RCChatroomBarrage -> {
                        list.add(MsgInfo("${message.userName}: ", message.userId, true))
                        list.add(MsgInfo("${message.content}", "", false))
                        updateRole(message.userId, tv_message_content, roomModel)
                    }
                    is RCChatroomEnter -> {
                        list.add(MsgInfo("${message.userName} ", message.userId, true))
                        list.add(MsgInfo("进来了", "", false))
                        updateRole(message.userId, tv_message_content, roomModel)
                    }
                    is RCChatroomKickOut -> {
                        list.add(MsgInfo("${message.targetName} 被 ", "", false))
                        list.add(MsgInfo("${message.userName} ", message.userId, true))
                        list.add(MsgInfo(" 踢出去了", "", false))
                    }
                    is RCChatroomGiftAll -> {
                        list.add(MsgInfo("${message.userName} ", message.userId, true))
                        list.add(MsgInfo("全麦打赏 ${message.giftName} x${message.number}", "", false))
                        updateRole(message.userId, tv_message_content, roomModel)
                    }
                    is RCChatroomGift -> {
                        list.add(MsgInfo("${message.userName}: ", message.userId, true))
                        list.add(MsgInfo(" 送给 ", "", false))
                        list.add(MsgInfo("${message.targetName} ", message.targetId, true))
                        list.add(MsgInfo(" ${message.giftName} x${message.number}", "", false))
                        updateRole(message.userId, tv_message_content, roomModel)
                    }
                    is RCChatroomAdmin -> {
                        list.add(MsgInfo("${message.userName}: ", message.userId, true))
                        list.add(
                            MsgInfo(
                                " ${if (message.isAdmin) "成为管理员" else "被撤回管理员"}",
                                "",
                                false
                            )
                        )
                        updateRole(message.userId, tv_message_content, roomModel)
                    }
                    is RCChatroomSeats -> {
                        list.add(MsgInfo("房间更换为 ${message.count} 座模式，请重新上麦"))
                    }
                    is RCChatroomLocationMessage -> {
                        list.add(MsgInfo(message.content, "", false))
                        Log.e("LocationMessage","RCChatroomLocationMessage")
                    }
                }
                tv_message_content.text = styleBuilder(list, listener)
                tv_message_content.movementMethod = LinkMovementMethod.getInstance()
            }
        }

        fun styleBuilder(
            infos: ArrayList<MsgInfo>,
            listener: (String) -> Unit
        ): SpannableStringBuilder {
            var style = SpannableStringBuilder()
            var start = 0
            infos.forEach { info ->
                info.start = start
                start += info.content.length
                info.end = start
                style.append(info.content)
                if (info.clicked) {
                    style.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            listener(info.clickId)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText = false
                        }
                    }, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    style.setSpan(
                        ForegroundColorSpan(Color.parseColor("#78FFFFFF")),
                        info.start,
                        info.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return style
        }
    }

    data class MsgInfo(
        val content: String = "",
        val clickId: String = "",
        val clicked: Boolean = false,
        var start: Int = 0,
        var end: Int = 0
    )
}

