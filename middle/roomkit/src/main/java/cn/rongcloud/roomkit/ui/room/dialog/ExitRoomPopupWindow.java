package cn.rongcloud.roomkit.ui.room.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.RoomOwnerType;


/**
 * @author gyn
 * @date 2021/9/24
 */
public class ExitRoomPopupWindow extends PopupWindow {
    private View mRootView;

    public ExitRoomPopupWindow(Context context, RoomOwnerType roomOwnerType, OnOptionClick onOptionClick) {
        super(context);
        mRootView = LayoutInflater.from(context).inflate(R.layout.popup_exit_room, null, false);
        setContentView(mRootView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        LinearLayout packUpRoomView = mRootView.findViewById(R.id.ll_pack_up_room);
        LinearLayout leaveRoomView = mRootView.findViewById(R.id.ll_leave_room);
        LinearLayout closeRoomView = mRootView.findViewById(R.id.ll_close_room);
        switch (roomOwnerType) {
            case RADIO_VIEWER:
            case VOICE_VIEWER:
            case LIVE_VIEWER:
                closeRoomView.setVisibility(View.GONE);
                break;
        }
        if (onOptionClick != null) {
            packUpRoomView.setOnClickListener(v -> {
                dismiss();
                onOptionClick.clickPackRoom();
            });
            leaveRoomView.setOnClickListener(v -> {
                dismiss();
                onOptionClick.clickLeaveRoom();
            });
            closeRoomView.setOnClickListener(v -> {
                dismiss();
                onOptionClick.clickCloseRoom();
            });
        }
    }

    public ExitRoomPopupWindow(View contentView, int width, int height, View mRootView) {
        super(contentView, width, height);
        this.mRootView = mRootView;
    }

    public interface OnOptionClick {
        void clickPackRoom();

        void clickLeaveRoom();

        void clickCloseRoom();
    }
}
