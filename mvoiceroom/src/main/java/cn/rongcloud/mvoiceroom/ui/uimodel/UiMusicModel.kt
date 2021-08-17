/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.mvoiceroom.ui.uimodel

import cn.rongcloud.mvoiceroom.net.bean.respond.MusicBean

/**
 * @author gusd
 * @Date 2021/07/07
 */
const val MUSIC_TYPE_SYSTEM = 0
const val MUSIC_FROM_TYPE_LOCAL = 1
const val MUSIC_FROM_TYPE_SYSTEM = 2
const val MUSIC_FUNCTION_LOCAL_ADD = -1

class UiMusicModel {
    var author: String? = null
    var createDt: Long? = null
    var id: Int? = null
    var name: String? = null
    var roomId: String? = null
    var size: String? = null
    var type: Int = 0
    var updateDt: Long? = null
    var url: String? = null
    var addAlready: Boolean = false
    var isPlaying:Boolean = false

    companion object {
        fun create(musicBean: MusicBean): UiMusicModel {
            return UiMusicModel().apply {
                author = musicBean.author
                createDt = musicBean.createDt
                id = musicBean.id
                name = musicBean.name
                roomId = musicBean.roomId
                size = musicBean.size
                type = musicBean.type ?: MUSIC_TYPE_SYSTEM
                updateDt = musicBean.updateDt
                url = musicBean.url
            }
        }

        fun createLocalAddMusicModel(): UiMusicModel {
            return UiMusicModel().apply {
                type = MUSIC_FUNCTION_LOCAL_ADD
            }
        }
    }
}