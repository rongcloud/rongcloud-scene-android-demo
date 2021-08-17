/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.createroom

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import cn.rongcloud.mvoiceroom.net.VoiceRoomNetManager
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.FileModel
import com.rongcloud.common.net.ApiConstant
import cn.rongcloud.mvoiceroom.net.bean.request.CreateRoomRequestBean
import cn.rongcloud.mvoiceroom.net.bean.request.Kv
import com.rongcloud.common.utils.RealPathFromUriUtils
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/15
 */
class CreateVoiceRoomPresenter @Inject constructor(
    val view: ICreateVoiceRoomView,
    @ActivityContext val context: Context,
    fragment:Fragment
) :
    BaseLifeCyclePresenter(fragment) {

    fun createVoiceRoom(
        roomCover: Uri? = null,
        roomName: String,
        roomBackground: String,
        isPrivate: Boolean,
        roomPassword: String?
    ) {
        view.showWaitingDialog()
        val intPrivate = if (isPrivate) 1 else 0
        val password = if (isPrivate) roomPassword else ""

        val kvList = ArrayList<Kv>()
        if (roomCover != null) {
            addDisposable(FileModel
                .imageUpload(
                    RealPathFromUriUtils.getRealPathFromUri(context, roomCover),
                    context
                )
                .flatMap {
                    return@flatMap VoiceRoomNetManager
                        .aRoomApi
                        .createVoiceRoom(
                            CreateRoomRequestBean(
                                intPrivate,
                                roomName,
                                password,
                                "${ApiConstant.FILE_URL}$it",
                                roomBackground,
                                kvList
                            )
                        )
                }.subscribe({ respond ->
                    view.hideWaitingDialog()
                    when (respond.code) {
                        10000 -> {
                            view.onCreateRoomSuccess(respond.data)
                        }
                        30016 -> {
                            view.onCreateRoomExist(respond.data)
                        }
                        else -> {
                            view.showError(respond.code ?: -1, respond.msg)
                        }
                    }
                }, { t ->
                    view.hideWaitingDialog()
                    view.showError(-1, t.message)
                })
            )
        } else {
            addDisposable(
                VoiceRoomNetManager
                    .aRoomApi
                    .createVoiceRoom(
                        CreateRoomRequestBean(
                            isPrivate = intPrivate,
                            kv = kvList,
                            name = roomName,
                            password = password,
                            backgroundUrl = roomBackground
                        )
                    ).subscribe({ respond ->
                        view.hideWaitingDialog()
                        when (respond.code) {
                            10000 -> {
                                view.onCreateRoomSuccess(respond.data)
                            }
                            30016 -> {
                                view.onCreateRoomExist(respond.data)
                            }
                            else -> {
                                view.showError(respond.code ?: -1, respond.msg)
                            }
                        }
                    }, { t ->
                        view.hideWaitingDialog()
                        view.showError(-1, t.message)
                    })
            )
        }
    }


}