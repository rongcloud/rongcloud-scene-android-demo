package cn.rong.combusis.ui.room.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.rong.combusis.R;
import cn.rong.combusis.common.ui.widget.RealtimeBlurView;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;

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
        RealtimeBlurView blurView = mRootView.findViewById(R.id.blur_view);
        switch (roomOwnerType) {
            case RADIO_VIEWER:
            case VOICE_VIEWER:
                closeRoomView.setVisibility(View.GONE);
                blurView.setOverlayColor(Color.parseColor("#29FFFFFF"));
                break;
            case LIVE_VIEWER:
                closeRoomView.setVisibility(View.GONE);
                blurView.setOverlayColor(Color.parseColor("#5C5095"));
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
