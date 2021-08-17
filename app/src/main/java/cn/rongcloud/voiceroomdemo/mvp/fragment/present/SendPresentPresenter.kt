/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.present

import android.util.Log
import androidx.fragment.app.Fragment
import com.rongcloud.common.base.BaseLifeCyclePresenter
import cn.rongcloud.voiceroomdemo.mvp.bean.Present
import cn.rongcloud.voiceroomdemo.mvp.model.VoiceRoomModel
import cn.rongcloud.mvoiceroom.ui.uimodel.UiMemberModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Named

/**
 * @author gusd
 * @Date 2021/06/28
 */

class SendPresentPresenter @Inject constructor(
    val view: ISendPresentView,
    private val roomModel: VoiceRoomModel,
    fragment: Fragment
) :
    BaseLifeCyclePresenter(fragment) {


    fun initeialObserve(selectedIds: List<String>) {
        view.onPresentInited(roomModel.presents)
        // 礼物信息：图标 名称和 价值
        currentPresent = roomModel.presents.first()
        addDisposable(roomModel
            .obMemberListChange()
            .map {
                return@map it.filter {
                    (it.seatIndex >= 0) || selectedIds.contains(it.userId)
                }
                return@map it
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                //移出已下麦
//                var filts = selects.filter { mem ->
//                    !it.contains(mem)
//                }
//                selects.clear()
//                selects.addAll(filts)
//                // 处理默认选中
//                it.forEach {
//                    if (selectedIds.contains(it.userId)) {
//                        selects.add(it)
//                    }
//                }
                var filts: List<UiMemberModel>
                if (!selectedIds.isEmpty()) {
                    // 个人设置过来 只过滤选中的人
                    filts = it.filter { mem ->
                        selectedIds.contains(mem.userId)
                    }
                    selects.clear()
                    selects.addAll(filts)
                    // 只显示 选中人
                    view.onMemberModify(filts)
                } else {
                    filts = selects.filter { mem ->
                        !it.contains(mem)
                    }
                    selects.clear()
                    selects.addAll(filts)
                    view.onMemberModify(it)
                }
                checkEnableSend()
            })
    }

    // 存储选中发生对象
    val selects: ArrayList<UiMemberModel> by lazy {
        return@lazy ArrayList<UiMemberModel>(16)
    }

    var currentPresent: Present? = null
        set(value) {
            if (field != value) {
                field = value
                presentNum = 1
            }
            checkEnableSend()
        }

    // 注意：新曾选中时 会将num重置
    var presentNum: Int = 1

    /**
     * 跟新则发送礼物的成员的选择状态
     * @return selected 选中状态 true： 当前选中  false：当前未选中
     */
    fun updateSelected(member: UiMemberModel): Boolean {
        selects.let {
            if (selects.contains(member)) {
                selects.remove(member)
                checkEnableSend()
                return false
            } else {
                selects.add(member)
                presentNum = 1
                checkEnableSend()
                return true
            }
        }
        return false
    }

    fun isSelected(member: UiMemberModel): Boolean {
        selects.let {
            return it.contains(member)
        }
    }

    fun selectAll(members: List<UiMemberModel>?) {
        selects.let {
            selects.clear()
            members?.let {
                selects.addAll(it)
            }
        }
        presentNum = 1
        checkEnableSend()
    }

    /**
     * 检查赠送状态
     */
    fun checkEnableSend() {
        val enable = null != currentPresent && selects.isNotEmpty()
        view.onEnableSend(enable)
    }

    fun sendPresent(isAll: Boolean) {
        Log.e(
            "sendPresent",
            "赠送礼物：数量: $presentNum 人数:${selects.size} 礼物索引:${currentPresent?.index ?: 0} 价值：${currentPresent?.price} isAll = $isAll"
        )
        currentPresent?.let { present ->
            addDisposable(
                roomModel
                    .sendGift(selects, present, presentNum)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { members ->
                        roomModel.sendGiftMsg(members, present, presentNum, isAll)
                        if (members.size == selects.size) {
                            view.showMessage("赠送成功")
                        } else {
                            view.showMessage("赠送异常")
                        }
                        view.fragmentDismiss()
                    }
            )
        }

    }

    override fun onResume() {
        super.onResume()
        roomModel.refreshAllMemberInfoList()
    }
}

