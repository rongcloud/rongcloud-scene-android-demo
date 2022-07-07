package cn.rongcloud.roomkit.ui.room.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.basis.utils.UiUtils;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.RoomOwnerType;


/**
 * @author gyn
 * @date 2021/9/24
 */
public class ExitRoomPopupWindow extends PopupWindow {
    private View mRootView;
    private Animation inAnimation;
    private Animation outAnimation;
    private FrameLayout flPop;

    public ExitRoomPopupWindow(Context context, RoomOwnerType roomOwnerType, OnOptionClick onOptionClick) {
        super(context);
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.pop_enter_anim);
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.pop_exit_anim);
        mRootView = LayoutInflater.from(context).inflate(R.layout.popup_exit_room, null, false);
        setContentView(mRootView);
        flPop = mRootView.findViewById(R.id.fl_pop);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.bg_exit_pop)));
        setClippingEnabled(false);
        setHeight(UiUtils.getFullScreenHeight(context));
        LinearLayout packUpRoomView = mRootView.findViewById(R.id.ll_pack_up_room);
        LinearLayout leaveRoomView = mRootView.findViewById(R.id.ll_leave_room);
        LinearLayout closeRoomView = mRootView.findViewById(R.id.ll_close_room);
        switch (roomOwnerType) {
            case RADIO_VIEWER:
            case VOICE_VIEWER:
            case LIVE_VIEWER:
                closeRoomView.setVisibility(View.GONE);
                break;
            case GAME_OWNER:
                packUpRoomView.setVisibility(View.GONE);
                break;
            case GAME_VIEWER:
                packUpRoomView.setVisibility(View.GONE);
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
        ImageView ivClose = mRootView.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(v -> {
            dismiss();
        });
        mRootView.setOnClickListener(v -> dismiss());
    }

    public ExitRoomPopupWindow(View contentView, int width, int height, View mRootView) {
        super(contentView, width, height);
        this.mRootView = mRootView;
    }

    public void show(View anchor) {
        if (!isShowing()) {
            super.showAtLocation(anchor, Gravity.TOP, 0, 0);
            flPop.startAnimation(inAnimation);
        }
    }


    @Override
    public void dismiss() {
        flPop.startAnimation(outAnimation);
        flPop.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExitRoomPopupWindow.super.dismiss();
            }
        }, 200);
    }


    public interface OnOptionClick {
        void clickPackRoom();

        void clickLeaveRoom();

        void clickCloseRoom();
    }
}
