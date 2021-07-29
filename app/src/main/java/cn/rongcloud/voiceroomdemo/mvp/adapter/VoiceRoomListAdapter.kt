/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.loadImageView
import cn.rongcloud.voiceroomdemo.common.loadPortrait
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import kotlinx.android.synthetic.main.layout_voice_room_list_item.view.*

/**
 * @author gusd
 * @Date 2021/06/09
 */
private const val TAG = "VoiceRoomListAdapter"

class VoiceRoomListAdapter(
    private val listener: ((View, VoiceRoomBean) -> Unit)? = null
) : RecyclerView.Adapter<VoiceRoomListViewHolder>() {

    private var data: ArrayList<VoiceRoomBean> = arrayListOf()

    override fun onBindViewHolder(holder: VoiceRoomListViewHolder, position: Int) {
        data[position].let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceRoomListViewHolder {
        return VoiceRoomListViewHolder.create(parent, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(list: List<VoiceRoomBean>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

}

// TODO: 2021/6/18  稍后添加上 DiffUtil
val COMPARATOR = object : DiffUtil.ItemCallback<VoiceRoomBean>() {
    override fun areItemsTheSame(oldItem: VoiceRoomBean, newItem: VoiceRoomBean): Boolean {
        return oldItem.roomId == newItem.roomId
    }

    override fun areContentsTheSame(oldItem: VoiceRoomBean, newItem: VoiceRoomBean): Boolean {
        return oldItem == newItem
    }

}

class VoiceRoomListViewHolder(
    itemView: View,
    private val listener: ((View, VoiceRoomBean) -> Unit)? = null
) :
    RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(
            parent: ViewGroup,
            listener: ((View, VoiceRoomBean) -> Unit)? = null
        ): VoiceRoomListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_voice_room_list_item, parent, false)
            return VoiceRoomListViewHolder(view, listener)
        }
    }

    fun bind(bean: VoiceRoomBean) {
        with(itemView) {
            (iv_room_cover as ImageView).loadImageView(
                bean.themePictureUrl ?: "",
                R.drawable.default_room_cover
            )
            tv_room_name.text = bean.roomName
            tv_room_creator_name.text = bean.createUser?.userName
            (iv_room_creator as ImageView).loadPortrait(bean.createUser?.portrait)
            tv_room_people_number.text = bean.userTotal
            iv_room_locked.isVisible = bean.isPrivate == 1
            setOnClickListener {
                listener?.invoke(it, bean)
            }
        }
    }
}