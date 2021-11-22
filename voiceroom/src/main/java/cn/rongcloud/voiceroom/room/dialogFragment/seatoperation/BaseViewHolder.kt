/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.room.dialogFragment.seatoperation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.rong.combusis.provider.user.User
import cn.rongcloud.voiceroom.R

/**
 * @author gusd
 * @Date 2021/06/29
 */
abstract class BaseListAdapter<VH : BaseViewHolder> : RecyclerView.Adapter<VH>() {
    private val data = arrayListOf<User>()


    fun refreshData(data: List<User>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position])
    }


}

abstract class BaseViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.layout_request_seat_item, parent, false
    )
) {
    fun bind(user: User) {
        bindView(user, itemView)
    }

    abstract fun bindView(user: User, itemView: View)
}