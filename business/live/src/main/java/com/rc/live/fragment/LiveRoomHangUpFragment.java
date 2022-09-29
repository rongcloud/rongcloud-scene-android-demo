package com.rc.live.fragment;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.basis.ui.BaseBottomSheetDialog;
import com.rc.live.R;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.ui.room.fragment.SeatActionClickListener;


/**
 * @author 李浩
 * @date 2021/12/17
 */
public class LiveRoomHangUpFragment extends BaseBottomSheetDialog {

    SeatActionClickListener seatActionClickListener;
    private boolean isVideo;
    private AppCompatTextView mTvSwitchLinkStatus;
    private User user;
    private AppCompatTextView btn_pickout;
    private AppCompatTextView btn_cancel;

    public LiveRoomHangUpFragment(boolean isVideo, User user, SeatActionClickListener seatActionClickListener) {
        super(R.layout.fragment_live_room_pick_out);
        this.seatActionClickListener = seatActionClickListener;
        this.isVideo = isVideo;
        this.user = user;
    }

    @Override
    public void initView() {
        mTvSwitchLinkStatus = (AppCompatTextView) getView().findViewById(R.id.tv_switch_link_status);
        btn_pickout = (AppCompatTextView) getView().findViewById(R.id.btn_pick_out_id);
        btn_cancel = (AppCompatTextView) getView().findViewById(R.id.btn_cancel);
        mTvSwitchLinkStatus.setText(isVideo ? "正在进行视频连线" : "正在进行语音连线");
    }

    @Override
    public void initListener() {
        super.initListener();
        if (seatActionClickListener == null) {
            return;
        }

        btn_pickout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seatActionClickListener.clickKickSeat(user, null);
                dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


}
