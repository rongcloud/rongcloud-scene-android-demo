/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voice.ui.popup

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import cn.rongcloud.voice.R
import kotlinx.android.synthetic.main.layout_exit_room_popup.view.*

/**
 * @author gusd
 * @Date 2021/06/16
 */
class ExitRoomPopupWindow(
    val context: Context,
    leaveRoomBlock: () -> Unit,
    closeRoomBlock: () -> Unit,
    packUpRoomBlock: () -> Unit
) : PopupWindow(
    LayoutInflater.from(context).inflate(R.layout.layout_exit_room_popup, null, false),
    ViewGroup.LayoutParams.MATCH_PARENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
) {
    init {
        isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        with(contentView) {
            ll_close_room.setOnClickListener {
                closeRoomBlock()
            }
            ll_leave_room.setOnClickListener {
                leaveRoomBlock()
            }
            ll_pack_up_room.setOnClickListener {
                packUpRoomBlock()
            }
        }
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
    }
}