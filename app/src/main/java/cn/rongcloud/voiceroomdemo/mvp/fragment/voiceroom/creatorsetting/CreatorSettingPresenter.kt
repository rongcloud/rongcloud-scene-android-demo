/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.creatorsetting

import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import com.rongcloud.common.utils.AccountStore
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/28
 */
@FragmentScoped
class CreatorSettingPresenter @Inject constructor(
    val view: ICreatorView,
    val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    override fun onCreate() {
        super.onCreate()
        addDisposable(roomModel
            .obSeatInfoChange()
            .filter {
                it.index == 0
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.onMuteChange(it.isMute)
            })
    }

    fun leaveSeat() {
        addDisposable(
            roomModel
                .leaveSeat(AccountStore.getUserId()!!)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.fragmentDismiss()
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun muteMic() {
        roomModel.getSeatInfoByUserId(AccountStore.getUserId())?.let {
            addDisposable(
                roomModel
                    .creatorMuteSelf(!it.isMute)
                    .subscribe({

                    }, {
                        view.showError(it.message)
                    })
            )
        }

    }


    fun isPlayingMusic(): Boolean {
        return roomModel.isPlayingMusic()
    }

    fun stopPlayMusic() {
        roomModel.stopPlayMusic()
    }
}