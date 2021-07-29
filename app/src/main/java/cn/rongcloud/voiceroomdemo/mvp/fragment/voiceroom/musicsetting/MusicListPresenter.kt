/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMusicModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * @author gusd
 * @Date 2021/07/06
 */
class MusicListPresenter(val view: IMusicListView, roomId: String) :
    BaseLifeCyclePresenter<IMusicListView>(view) {
    private val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }

    override fun onStart() {
        super.onStart()
        addDisposable(
            roomModel.obUserMusicListChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showMusicList(it)
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun deleteMusic(model: UiMusicModel) {
        model.id?.let {
            addDisposable(
                roomModel.deleteMusic(model.url ?: "", it)
                    .subscribe({

                    }, {
                        view.showError(it.message)
                    })
            )
        }

    }

    fun playOrPauseMusic(model: UiMusicModel) {
        roomModel.playOrPauseMusic(model)
    }

    fun moveMusicTop(model: UiMusicModel) {
        addDisposable(roomModel.moveMusicToTop(model).subscribe({

        }, {
            view.showError(it.message)
        }))
    }
}