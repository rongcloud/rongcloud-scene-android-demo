/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.createroom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.LocalDataStore
import cn.rongcloud.voiceroomdemo.common.loadImageView
import cn.rongcloud.voiceroomdemo.common.showToast
import cn.rongcloud.voiceroomdemo.mvp.fragment.BaseBottomSheetDialogFragment
import cn.rongcloud.voiceroomdemo.ui.dialog.InputPasswordDialog
import cn.rongcloud.voiceroomdemo.utils.MaxLengthWithEmojiFilter
import kotlinx.android.synthetic.main.layout_create_room.*

/**
 * @author gusd
 * @Date 2021/06/09
 */
private const val TAG = "CreateVoiceRoomDialogFr"

private const val PICTURE_SELECTED_RESULT_CODE = 10001

class CreateVoiceRoomDialogFragment(view: ICreateVoiceRoomView) :
    BaseBottomSheetDialogFragment<CreateVoiceRoomPresenter, ICreateVoiceRoomView>(R.layout.layout_create_room),
    ICreateVoiceRoomView by view {

    private lateinit var backgroundImages: List<ImageView>
    private lateinit var radioButtons: List<AppCompatRadioButton>
    private lateinit var gifMarks: List<AppCompatTextView>

    private var roomCover: Uri? = null
    private var roomBackground: String? = null
    private var roomPassword: String? = null
    private var inputPasswordDialog: InputPasswordDialog? = null


    override fun initPresenter(): CreateVoiceRoomPresenter {
        return CreateVoiceRoomPresenter(this, requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }


    override fun initView() {
        backgroundImages = arrayListOf(
            iv_voice_room_bg_0,
            iv_voice_room_bg_1,
            iv_voice_room_bg_2,
            iv_voice_room_bg_3
        )
        radioButtons =
            arrayListOf(rb_background_0, rb_background_1, rb_background_2, rb_background_3)

        gifMarks = arrayListOf(tv_is_gif_0, tv_is_gif_1, tv_is_gif_2, tv_is_gif_3)


        backgroundImages.forEachIndexed { index, imageview ->
            LocalDataStore.getBackgroundByIndex(index)?.let {
                imageview.loadImageView(it)
                gifMarks[index].isVisible = it.endsWith("gif", true)
            }
        }
        iv_background.loadImageView(
            LocalDataStore.getBackgroundByIndex(0) ?: "",
            R.drawable.test_background
        )

        et_room_name.filters = arrayOf(MaxLengthWithEmojiFilter(10, et_room_name))
    }

    override fun initListener() {
        iv_fold.setOnClickListener {
            dismiss()
        }

        iv_room_cover.setOnClickListener {
            startPicSelectActivity()
        }
        roomBackground = LocalDataStore.getBackgroundByIndex(0) ?: ""
        backgroundImages.forEachIndexed { index, image ->
            image.setOnClickListener { it ->
                radioButtons.forEach { rb ->
                    rb.isChecked = false
                }
                radioButtons[index].isChecked = true
                roomBackground = LocalDataStore.getBackgroundByIndex(index) ?: ""
                iv_background.loadImageView(
                    roomBackground!!,
                    R.drawable.test_background
                )

            }
        }

        btn_create_room.setOnClickListener {
            if (et_room_name.text.isNullOrBlank()) {
                context?.showToast("请输入房间名")
                return@setOnClickListener
            }
            if (rb_private.isChecked && roomPassword.isNullOrBlank()) {
                inputPasswordDialog = InputPasswordDialog(requireContext(), false) { password ->
                    if (password.isNullOrBlank()) {
                        return@InputPasswordDialog
                    }
                    if (password.length < 4) {
                        requireContext().showToast("请输入 4 位密码")
                        return@InputPasswordDialog
                    }
                    roomPassword = password
                    inputPasswordDialog?.dismiss()
                }
                inputPasswordDialog?.show()
                return@setOnClickListener
            }

            presenter.createVoiceRoom(
                roomCover,
                et_room_name.text.toString(),
                roomBackground ?: "",
                rb_private.isChecked,
                roomPassword
            )

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        inputPasswordDialog?.dismiss()
    }

    private fun startPicSelectActivity() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICTURE_SELECTED_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_SELECTED_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            val selectImageUrl = data?.data
//            val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
            // 查询我们需要的数据
//            selectImageUrl?.let {
//                val cursor: Cursor? = activity?.contentResolver?.query(
//                    selectImageUrl,
//                    filePathColumn, null, null, null
//                )
//                cursor?.moveToFirst()
//
//                val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0])!!
//                val picturePath: String = cursor.getString(columnIndex)
//                cursor.close()
//                roomCover = picturePath
//                iv_room_cover.loadImageView(picturePath)
//            }

            selectImageUrl?.let {
                iv_room_cover.loadImageView(it)
                roomCover = it
            }
        }
    }

}


