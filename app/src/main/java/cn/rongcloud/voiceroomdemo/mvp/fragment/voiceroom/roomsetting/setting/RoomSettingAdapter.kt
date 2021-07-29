/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel.BaseFunctionModel
import kotlinx.android.synthetic.main.layout_room_setting_item.view.*

/**
 * @author gusd
 * @Date 2021/06/22
 */
class RoomSettingAdapter :
    RecyclerView.Adapter<RoomSettingViewHolder>() {
    private val functionList: ArrayList<BaseFunctionModel> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomSettingViewHolder {
        return RoomSettingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_room_setting_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RoomSettingViewHolder, position: Int) {
        holder.bind(functionList[position])
    }

    override fun getItemCount(): Int {
        return functionList.size
    }

    fun refreshData(list: List<BaseFunctionModel>) {
        functionList.clear()
        functionList.addAll(list)
        notifyDataSetChanged()
    }
}

class RoomSettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(functionModel: BaseFunctionModel) {
        with(itemView) {
            functionModel.setChangeListener { image, text, clickListener ->
                iv_icon.setImageResource(image)
                tv_text.text = text
                iv_icon.setOnClickListener {
                    clickListener()
                }
            }
        }

    }
}