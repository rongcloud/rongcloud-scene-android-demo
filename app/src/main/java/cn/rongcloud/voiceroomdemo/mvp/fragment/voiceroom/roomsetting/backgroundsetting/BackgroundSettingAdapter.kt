/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.backgroundsetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.loadImageView
import kotlinx.android.synthetic.main.layout_background_item.view.*

/**
 * @author gusd
 * @Date 2021/06/22
 */
class BackgroundSettingAdapter(private val listener: (url: String) -> Unit) :
    RecyclerView.Adapter<BackgroundSettingViewHolder>() {

    private val data = arrayListOf<String>()
    var currentSelectedBackground: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundSettingViewHolder {
        return BackgroundSettingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_background_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BackgroundSettingViewHolder, position: Int) {
        holder.bind(data[position], currentSelectedBackground, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(list: List<String>, defaultBackground: String?) {
        data.clear()
        data.addAll(list)
        currentSelectedBackground = defaultBackground
        notifyDataSetChanged()
    }

    fun selectBackground(backgroundUrl: String){
        currentSelectedBackground = backgroundUrl
        notifyDataSetChanged()
    }


}

class BackgroundSettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(backgroundUrl: String, selectedUrl: String?, listener: (url: String) -> Unit) {
        with(itemView) {
            iv_background.loadImageView(backgroundUrl)
            cb_is_selected.isChecked = backgroundUrl == selectedUrl
            tv_is_gif.isVisible = backgroundUrl.endsWith("gif", true)
            itemView.setOnClickListener {
                listener(backgroundUrl)
            }
        }
    }
}