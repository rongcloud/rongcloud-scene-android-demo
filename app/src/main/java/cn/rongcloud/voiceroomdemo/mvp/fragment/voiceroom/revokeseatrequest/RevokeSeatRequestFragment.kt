/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.revokeseatrequest

import cn.rongcloud.annotation.HiltBinding
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.base.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cancel_request_seat.*
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/29
 */
@HiltBinding(value = IRevokeSeatView::class)
@AndroidEntryPoint
class RevokeSeatRequestFragment(view: IRevokeSeatView) :
    BaseBottomSheetDialogFragment(
        R.layout.fragment_cancel_request_seat
    ), IRevokeSeatView by view {

    @Inject
    lateinit var presenter:RevokeSeatPresenter


    override fun initView() {
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_cancel_request.setOnClickListener {
            presenter.cancelRequest()
        }
    }

    override fun fragmentDismiss() {
        dismiss()
    }
}