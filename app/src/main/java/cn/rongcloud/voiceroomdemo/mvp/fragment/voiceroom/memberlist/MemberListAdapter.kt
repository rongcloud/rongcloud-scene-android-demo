/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.memberlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import com.rongcloud.common.extension.loadPortrait
import kotlinx.android.synthetic.main.layout_member_list_item.view.*
import java.util.*

/**
 * @author gusd
 * @Date 2021/06/21
 */
class MemberListAdapter( private val listener: (UiMemberModel) -> Unit) :
    RecyclerView.Adapter<MemberItemViewHolder>() {
    private val data = ArrayList<UiMemberModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberItemViewHolder {
        return MemberItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_member_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemberItemViewHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(list: List<UiMemberModel>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}

class MemberItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(model: UiMemberModel, listener: (UiMemberModel) -> Unit) {
        with(itemView) {
            iv_member_portrait.loadPortrait(model.portrait)
            tv_member_name.text = model.userName
            setOnClickListener {
                listener(model)
            }
        }
    }
}