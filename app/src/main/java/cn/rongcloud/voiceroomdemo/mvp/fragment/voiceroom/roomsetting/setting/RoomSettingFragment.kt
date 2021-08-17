/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting

import androidx.recyclerview.widget.GridLayoutManager
import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import com.rongcloud.common.ui.dialog.EditDialog
import com.rongcloud.common.ui.dialog.InputPasswordDialog
import com.rongcloud.common.ui.widget.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Maybe
import kotlinx.android.synthetic.main.fragment_background_setting.*
import kotlinx.android.synthetic.main.fragment_room_setting.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/22
 */
@HiltBinding(value = IRoomSettingView::class)
@AndroidEntryPoint
class RoomSettingFragment(
    view: IRoomSettingView
) :
    BaseBottomSheetDialogFragment(R.layout.fragment_room_setting),
    IRoomSettingView by view {

    private var passwordDialog: InputPasswordDialog? = null
    private var modifyNameDialog: EditDialog? = null


    @Inject
    lateinit var presenter: RoomSettingPresenter


    override fun onDestroy() {
        super.onDestroy()
        passwordDialog?.dismiss()
        modifyNameDialog?.dismiss()
    }

    override fun initView() {
        iv_close.setOnClickListener {
            dismiss()
        }
        val itemDecoration = GridSpacingItemDecoration(
            (rv_function_list.layoutManager as GridLayoutManager).spanCount,
            resources.getDimensionPixelSize(R.dimen.background_setting_decoration), true
        )
        rv_function_list.addItemDecoration(itemDecoration)
        rv_function_list.adapter = RoomSettingAdapter().apply {
            refreshData(presenter.getButtons())
        }

    }

    override fun showPasswordDialog(): Maybe<String> {

        return Maybe.create<String> { emitter ->
            passwordDialog =
                InputPasswordDialog(requireContext(), true, cancelListener = {
                    emitter.onComplete()
                }) { password ->
                    if (password.length < 4) {
                        requireActivity().showToast("请输入 4 位密码")
                        return@InputPasswordDialog
                    }
                    passwordDialog?.dismiss()
                    emitter.onSuccess(password)
                }.apply {
                    show()
                }
        }
    }

    override fun showModifyRoomNameDialog(roomName: String?): Maybe<String>? {
        return Maybe.create { emitter ->
            modifyNameDialog = EditDialog(
                requireActivity(),
                "修改房间标题",
                "请输入房间名",
                roomName ?: "",
                10,
                cancelListener = {
                    emitter.onComplete()
                }) { newName ->
                if (newName.isNullOrEmpty()) {
                    requireActivity().showToast("房间名不能为空")
                    return@EditDialog
                }
                modifyNameDialog?.dismiss()
                emitter.onSuccess(newName)
            }.apply {
                show()
            }
        }
    }

}