/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseFragment
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMusicModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_music_list.*
import kotlinx.android.synthetic.main.layout_music_play_item.view.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/07/06
 */
@HiltBinding(value = IMusicListView::class)
@AndroidEntryPoint
class MusicListFragment(val view: IMusicListView) :
    BaseFragment(
        R.layout.fragment_music_list
    ), IMusicListView by view {

    @Inject
    lateinit var presenter: MusicListPresenter


    override fun initView() {
        rv_list.adapter = MyAdapter()
        group_add_music.isVisible = true
        rv_list.isVisible = false

        btn_add_music.setOnClickListener {
            view.gotoAddMusicView()
        }
    }

    override fun showMusicList(musicList: List<UiMusicModel>) {
        super.showMusicList(musicList)
        if (musicList.isEmpty()) {
            group_add_music.isVisible = true
            rv_list.isVisible = false
        } else {
            group_add_music.isVisible = false
            rv_list.isVisible = true
            (rv_list.adapter as MyAdapter).refreshData(musicList)
        }

    }


    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {

        private val data = arrayListOf<UiMusicModel>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
            MyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_music_play_item, parent, false)
            )

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(position, data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun refreshData(list: List<UiMusicModel>) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, model: UiMusicModel) {
            with(itemView) {
                this.iv_music_play_icon.setImageResource(
                    if (model.isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play
                )
                this.tv_music_name.text = model.name
                this.tv_music_author.text = model.author
                this.tv_music_size.text = "${model.size}M"

                this.tv_music_name.isSelected = model.isPlaying
                this.tv_music_author.isSelected = model.isPlaying
                this.tv_music_size.isSelected = model.isPlaying

                if (model.isPlaying) {
                    iv_music_top.isVisible = false
                    iv_music_delete.isVisible = false
                    mpv_music_playing.isVisible = true
                } else {
                    iv_music_top.isVisible = true
                    iv_music_delete.isVisible = true
                    mpv_music_playing.isVisible = false
                }

                iv_music_play_icon.setOnClickListener {
                    presenter.playOrPauseMusic(model)
                }

                iv_music_delete.setOnClickListener {
                    presenter.deleteMusic(model)
                }

                iv_music_top.setOnClickListener {
                    presenter.moveMusicTop(model)
                }
            }
        }

    }
}