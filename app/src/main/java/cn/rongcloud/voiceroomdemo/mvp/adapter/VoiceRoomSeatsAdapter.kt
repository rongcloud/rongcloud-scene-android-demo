/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.mvoiceroom.ui.uimodel.UiSeatModel
import com.rongcloud.common.extension.loadPortrait
import kotlinx.android.synthetic.main.layout_seat_item.view.*

/**
 * @author gusd
 * @Date 2021/06/21
 */
private const val TAG = "VoiceRoomMembersAdapter"

class VoiceRoomSeatsAdapter(
    val listener: (seatModel: UiSeatModel, position: Int) -> Unit
) :
    RecyclerView.Adapter<VoiceRoomSeatsAdapter.SeatItemViewHolder>() {

    private val data: ArrayList<UiSeatModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatItemViewHolder {
        return SeatItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_seat_item, parent, false)
        )
    }

    fun refreshData(seatList: List<UiSeatModel>) {
        // TODO: 2021/6/21 后期需添加上 DiffUtil
        data.clear()
        data.addAll(seatList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SeatItemViewHolder, position: Int) {
        holder.bind(data[position], position, listener = listener)
    }

    override fun getItemId(position: Int): Long {
        return data[position].index.toLong()
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshIndex(index: Int, uiSeatModel: UiSeatModel) {
        data.elementAtOrNull(index)?.let {
            data[index] = uiSeatModel
            notifyDataSetChanged()
        }
    }

    class SeatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            mode: UiSeatModel,
            position: Int,
            listener: (seatModel: UiSeatModel, position: Int) -> Unit
        ) {
            with(itemView) {
                when (mode.seatStatus) {
                    RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing -> {
                        wv_seat_background.isVisible = true
                        iv_user_portrait.isVisible = true
                        tv_member_name.isVisible = true
                        tv_gift_count.isVisible = true
                        iv_user_portrait.setBackgroundResource(R.drawable.bg_voice_room_portrait)

                        if (mode.isSpeaking) {
                            wv_seat_background.start()
                        } else {
                            wv_seat_background.stop()
                        }
                        iv_user_portrait.loadPortrait(mode.portrait)
                        iv_user_portrait.tag = mode.portrait
                        iv_is_mute.isVisible = mode.isMute
                        iv_seat_status.isVisible = false
                        tv_member_name.text = mode.userName
                        tv_gift_count.text = "${mode.giftCount}"

                        if (mode.isAdmin) {
                            tv_member_name.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_is_admin,
                                0,
                                0,
                                0
                            )
                        } else {
                            tv_member_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }

                    }
                    RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty -> {
                        iv_user_portrait.isVisible = true
                        tv_member_name.isVisible = true
                        iv_seat_status.isVisible = true

                        wv_seat_background.isVisible = false
                        tv_gift_count.visibility = View.INVISIBLE
                        iv_is_mute.isVisible = mode.isMute
                        tv_member_name.setCompoundDrawables(null, null, null, null)

                        iv_seat_status.setImageResource(R.drawable.ic_seat_status_enter)
                        tv_member_name.text = "${position + 1} 号麦位"
                        iv_user_portrait.setImageResource(R.drawable.bg_seat_status)
                        iv_user_portrait.background = null

                    }
                    RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking -> {
                        iv_user_portrait.isVisible = true
                        tv_member_name.isVisible = true
                        iv_seat_status.isVisible = true

                        wv_seat_background.isVisible = false
                        tv_gift_count.visibility = View.INVISIBLE
                        iv_is_mute.isVisible = mode.isMute
                        tv_member_name.setCompoundDrawables(null, null, null, null)

                        iv_seat_status.setImageResource(R.drawable.ic_seat_status_locked)
                        tv_member_name.text = "${position + 1} 号麦位"
                        iv_user_portrait.setImageResource(R.drawable.bg_seat_status)
                        iv_user_portrait.background = null
                    }
                }
                setOnClickListener {
                    listener(mode, position)
                }
                itemView.tag = mode.toString()
            }
        }
    }


}

