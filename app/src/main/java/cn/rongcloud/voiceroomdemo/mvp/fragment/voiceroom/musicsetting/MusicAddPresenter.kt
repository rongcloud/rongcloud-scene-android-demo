/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.musicsetting

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import cn.rongcloud.voiceroomdemo.common.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.model.FileModel
import cn.rongcloud.voiceroomdemo.mvp.model.getVoiceRoomModelByRoomId
import cn.rongcloud.voiceroomdemo.ui.uimodel.MUSIC_FROM_TYPE_LOCAL
import cn.rongcloud.voiceroomdemo.ui.uimodel.UiMusicModel
import cn.rongcloud.voiceroomdemo.utils.RealPathFromUriUtils
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

/**
 * @author gusd
 * @Date 2021/07/06
 */
private const val TAG = "MusicAddPresenter"

class MusicAddPresenter(val view: IMusicAddView, roomId: String) :
    BaseLifeCyclePresenter<IMusicAddView>(view) {
    private val roomModel by lazy {
        getVoiceRoomModelByRoomId(roomId)
    }

    private val fileModel by lazy {
        FileModel
    }

    override fun onStart() {
        super.onStart()
        addDisposable(
            roomModel
                .obSystemMusicListChange()
                .subscribe({
                    view.showMusicList(arrayListOf<UiMusicModel>().apply {
                        addAll(it)
                        add(UiMusicModel.createLocalAddMusicModel())
                    })
                }, {
                    view.showError(it.message)
                })
        )
    }

    fun addMusic(name: String?, author: String? = "", type: Int = 0, url: String) {
        view.showWaitingDialog()
        addDisposable(
            roomModel
                .addMusic(name ?: "", author, type, url)
                .subscribe({
                    view.hideWaitingDialog()
                }, {
                    view.showError(it.message)
                    view.hideWaitingDialog()
                })
        )
    }

    fun getSupportFileTypeMime(): Array<String> {
        return arrayOf(
            "audio/x-mpeg",
            "audio/aac",
            "audio/mp4a-latm",
            "audio/x-wav",
            "audio/ogg",
            "audio/3gpp"
        )
    }

    fun addMusicFromLocal(context: Context, uri: Uri) {
        val realPath = RealPathFromUriUtils.getRealPathFromUri(context, uri)
        if (!realPath.endsWith("mp3", true)
            && !realPath.endsWith("aac")
            && !realPath.endsWith("m4a", true)
            && !realPath.endsWith("wav", true)
            && !realPath.endsWith("ogg", true)
            && !realPath.endsWith("amr", true)
        ) {
            view.showError("仅支持 MP3、AAC、M4A、WAV、OGG、AMR 格式文件")
            return
        }
        view.showWaitingDialog()
        addDisposable(
            FileModel
                .musicUpload(context, realPath)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    FileModel.moveMusicToCache(it, realPath)
                }
                .flatMapCompletable {
                    var author: String? = null
                    if (!it.isNullOrEmpty()) {
                        var cursor: Cursor? = null

                        author = try {
                            cursor = context.contentResolver.query(
                                uri,
                                null,
                                null,
                                null,
                                MediaStore.Audio.AudioColumns.IS_MUSIC
                            )
                            with(MediaMetadataRetriever()) {
                                setDataSource(realPath)
                                extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "addMusicFromLocal: ", e)
                            "无"
                        } finally {
                            cursor?.close()
                        }
                    }
                    Log.d(TAG, "addMusicFromLocal: author = $author")
                    val file = File(realPath)
                    return@flatMapCompletable roomModel.addMusic(
                        file.nameWithoutExtension,
                        author,
                        MUSIC_FROM_TYPE_LOCAL,
                        it,
                        file.length() / 1024
                    )
                }
                .subscribe({
                    view.hideWaitingDialog()
                }, {
                    view.hideWaitingDialog()
                })
        )
    }


}
