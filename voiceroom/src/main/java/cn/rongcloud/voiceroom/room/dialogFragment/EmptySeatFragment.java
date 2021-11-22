package cn.rongcloud.voiceroom.room.dialogFragment;


import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rongcloud.voiceroom.R;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import cn.rongcloud.voiceroom.room.VoiceRoomModel;
import cn.rongcloud.voiceroom.ui.uimodel.UiSeatModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 房主点击空座位的时候弹窗的fragment
 */
public class EmptySeatFragment extends BaseBottomSheetDialogFragment {


    private Guideline glBg;
    private CircleImageView ivUserPortrait;
    private AppCompatImageView ivIsMute;
    private AppCompatImageView ivSeatStatus;
    private AppCompatTextView tvMemberName;
    private AppCompatTextView btnInviteUserIntoSeat;
    private ConstraintLayout clMemberSetting;
    private LinearLayout llCloseSeat;
    private AppCompatImageView ivCloseSeat;
    private AppCompatTextView tvCloseSeat;
    private LinearLayout llMuteSeat;
    private AppCompatImageView ivMuteSeat;
    private AppCompatTextView tvMuteSeat;

    private UiSeatModel uiSeatModel;
    private String roomId;
    private VoiceRoomModel voiceRoomModel;

    public EmptySeatFragment() {
        super(R.layout.fragment_new_empty_seat_setting);
    }

    public void setData(String roomId, UiSeatModel uiSeatModel, VoiceRoomModel voiceRoomModel) {
        this.uiSeatModel = uiSeatModel;
        this.roomId = roomId;
        this.voiceRoomModel = voiceRoomModel;
    }

    @Override
    public void initView() {
        glBg = (Guideline) getView().findViewById(R.id.gl_bg);
        ivUserPortrait = (CircleImageView) getView().findViewById(R.id.iv_user_portrait);
        ivIsMute = (AppCompatImageView) getView().findViewById(R.id.iv_is_mute);
        ivSeatStatus = (AppCompatImageView) getView().findViewById(R.id.iv_seat_status);
        tvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        btnInviteUserIntoSeat = (AppCompatTextView) getView().findViewById(R.id.btn_invite_user_into_seat);
        clMemberSetting = (ConstraintLayout) getView().findViewById(R.id.cl_member_setting);
        llCloseSeat = (LinearLayout) getView().findViewById(R.id.ll_close_seat);
        ivCloseSeat = (AppCompatImageView) getView().findViewById(R.id.iv_close_seat);
        tvCloseSeat = (AppCompatTextView) getView().findViewById(R.id.tv_close_seat);
        llMuteSeat = (LinearLayout) getView().findViewById(R.id.ll_mute_seat);
        ivMuteSeat = (AppCompatImageView) getView().findViewById(R.id.iv_mute_seat);
        tvMuteSeat = (AppCompatTextView) getView().findViewById(R.id.tv_mute_seat);
        refreshView(uiSeatModel);
    }

    @Override
    public void initListener() {
        super.initListener();
        //可以监听一下麦位的状态，避免出现 点击的时候是空的，后来又出现了
        llCloseSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭座位 打开座位
                closeOrOpenSeat();
            }
        });
        llMuteSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //座位禁麦
                muteOrUnMuteSeat();
            }
        });
        btnInviteUserIntoSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示邀请的fragment
                voiceRoomModel.present.showSeatOperationViewPagerFragment(1);
                dismiss();
            }
        });
    }

    /**
     * 禁麦或者开麦ß
     */
    private void muteOrUnMuteSeat() {
        voiceRoomModel.clickMuteSeat(uiSeatModel.getIndex(), !uiSeatModel.isMute(), new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    dismiss();
                } else {
                    EToast.showToast(msg);
                }
            }
        });
    }

    //座位的关闭和打开
    private void closeOrOpenSeat() {
        voiceRoomModel.clickCloseSeatByIndex(uiSeatModel.getIndex(),
                uiSeatModel.getSeatStatus() != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking,
                new ClickCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean result, String msg) {
                        if (result) {
                            if (uiSeatModel.getSeatStatus() != RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
                                EToast.showToast("座位已开启");
                            } else {
                                EToast.showToast("座位已关闭");
                            }
                            dismiss();
                        } else {
                            EToast.showToast(msg);
                        }
                    }
                });
    }

    /**
     * 根据当前麦位的信息去刷新Ui
     */
    public void refreshView(UiSeatModel uiSeatModel) {
        if (uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            dismiss();
            return;
        }
        if (uiSeatModel.isMute()) {
            ivMuteSeat.setImageResource(R.drawable.ic_room_setting_unmute_all);
            tvMuteSeat.setText("取消禁麦");
            ivIsMute.setVisibility(View.VISIBLE);
        } else {
            ivMuteSeat.setImageResource(R.drawable.ic_member_setting_mute_seat);
            tvMuteSeat.setText("座位禁麦");
            ivIsMute.setVisibility(View.GONE);
        }

        if (uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            ivSeatStatus.setImageResource(R.drawable.ic_seat_status_locked);
            ivUserPortrait.setImageResource(R.drawable.bg_seat_status);
            ivCloseSeat.setImageResource(R.drawable.ic_room_setting_unlock_all);
            tvCloseSeat.setText("开启座位");

        } else if (uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
            ivSeatStatus.setImageResource(R.drawable.ic_seat_status_enter);
            ivUserPortrait.setImageResource(R.drawable.bg_seat_status);
            ivCloseSeat.setImageResource(R.drawable.ic_room_setting_lock_all);
            tvCloseSeat.setText("关闭座位");
        }
        tvMemberName.setText(uiSeatModel.getIndex() + "号麦位");
    }
}
