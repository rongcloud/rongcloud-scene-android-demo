/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.*
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.IVoiceRoomListView
import cn.rongcloud.voiceroomdemo.mvp.fragment.createroom.CreateVoiceRoomDialogFragment
import cn.rongcloud.voiceroomdemo.mvp.fragment.createroom.ICreateVoiceRoomView
import cn.rongcloud.voiceroomdemo.mvp.presenter.VoiceRoomListPresenter
import cn.rongcloud.voiceroomdemo.net.api.ApiConstant
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import cn.rongcloud.voiceroomdemo.mvp.adapter.VoiceRoomListAdapter
import cn.rongcloud.voiceroomdemo.ui.dialog.ConfirmDialog
import cn.rongcloud.voiceroomdemo.ui.dialog.InputPasswordDialog
import kotlinx.android.synthetic.main.activity_voice_room_list.*
import kotlinx.android.synthetic.main.layout_input_password_dialog.*

private const val TAG = "VoiceRoomListActivity"

class VoiceRoomListActivity : BaseActivity<VoiceRoomListPresenter, IVoiceRoomListView>(),
    IVoiceRoomListView, ICreateVoiceRoomView {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, VoiceRoomListActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var mAdapter: VoiceRoomListAdapter? = null
    private var passwordDialog: Dialog? = null
    private var createVoiceRoomDialogFragment: CreateVoiceRoomDialogFragment? = null
    private var messageDialog: ConfirmDialog? = null

    override fun initPresenter(): VoiceRoomListPresenter {
        return VoiceRoomListPresenter(this)
    }

    override fun getContentView(): Int = R.layout.activity_voice_room_list

    override fun initView() {
        mAdapter = VoiceRoomListAdapter { _, bean ->
            gotoVoiceRoomActivity(bean)
        }
        rv_voice_room.adapter = mAdapter

        showBackButton()

        iv_create_room.setOnClickListener {
            showCreateVoiceRoomDialog()
        }

        srl_refresh.setOnRefreshListener {
            presenter.refreshData()
        }

        rv_voice_room.useDefaultLoadMore()
        rv_voice_room.setLoadMoreListener {
            presenter.loadMore()
        }
    }


    private fun gotoVoiceRoomActivity(bean: VoiceRoomBean) {
        presenter.gotoVoiceRoomActivity(this, bean.roomId)
    }

    override fun showInputPasswordDialog(bean: VoiceRoomBean) {
        passwordDialog = InputPasswordDialog(this) { password ->
            if (password.length < 4) {
                showToast(R.string.please_input_complete_password)
                return@InputPasswordDialog
            }
            if (password == bean.password) {
                passwordDialog?.dismiss()
                presenter.turnToRoom(this,bean)
            } else {
                showToast(R.string.password_error)
            }
        }
        passwordDialog?.show()
    }

    private fun showCreateVoiceRoomDialog() {
        createVoiceRoomDialogFragment = CreateVoiceRoomDialogFragment(this)
        createVoiceRoomDialogFragment?.show(supportFragmentManager, "CreateRoomDialog")
    }

    override fun initData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        passwordDialog?.dismiss()
        messageDialog?.dismiss()
    }

    override fun getActionTitle(): CharSequence? {
        return null
    }


    override fun onDataChange(list: List<VoiceRoomBean>?) {
        list?.let {
            ui {
                srl_refresh.isRefreshing = false
                rv_voice_room.loadMoreFinish(false, true)
                mAdapter?.refreshData(list)
            }
        }
    }

    override fun onLoadError(throwable: Throwable?) {
        ui {
            srl_refresh.isRefreshing = false
            rv_voice_room.loadMoreError(-1, "加载失败，请下拉刷新")
            showToast(throwable?.message)
        }
    }

    override fun onCreateRoomSuccess(data: VoiceRoomBean?) {
        createVoiceRoomDialogFragment?.dismiss()
        data?.let {
            gotoVoiceRoomActivity(it)
        }
    }

    override fun onCreateRoomExist(data: VoiceRoomBean?) {
        ui {
            createVoiceRoomDialogFragment?.dismiss()
            data?.let {
                messageDialog = ConfirmDialog(this, "您已创建过语聊房，是否直接进入？") {
                    gotoVoiceRoomActivity(data)
                    messageDialog?.dismiss()
                }
                messageDialog?.show()
            }
        }
    }

    override fun getRightActionButton(): View {
        val imageView = LayoutInflater.from(this)
            .inflate(R.layout.layout_right_title_icon, null, false) as AppCompatImageView
        imageView.setImageResource(R.drawable.ic_goto_homepage)
        imageView.setOnClickListener {
            val uri = Uri.parse(ApiConstant.HOME_PAGE)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        return imageView
    }

}