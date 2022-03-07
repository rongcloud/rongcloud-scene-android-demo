package cn.rongcloud.roomkit.ui.room.fragment.seatsetting;


import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.KToast;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.fragment.SeatActionClickListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 房主点击空座位的时候弹窗的fragment
 */
public class EmptySeatFragment extends BaseBottomSheetDialog implements View.OnClickListener {


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
    private AppCompatTextView tvSwitchSeat;
    private SeatActionClickListener seatActionClickListener;

    private int index;
    private int seatStatus;
    private boolean isMute;
    private boolean isShowSwitchSeatBtn;
    private ICommonDialog iCommonDialog;

    public EmptySeatFragment() {
        super(R.layout.fragment_empty_seat_setting);
    }

    public void setData(int index, int seatStatus, boolean isMute, ICommonDialog iCommonDialog) {
        this.index = index;
        this.seatStatus = seatStatus;
        this.isMute = isMute;
        this.iCommonDialog = iCommonDialog;
    }

    public void setShowSwitchSeatBtn(boolean isShow) {
        this.isShowSwitchSeatBtn = isShow;
    }

    /**
     * 设置回调接口
     *
     * @param seatActionClickListener
     */
    public void setSeatActionClickListener(SeatActionClickListener seatActionClickListener) {
        this.seatActionClickListener = seatActionClickListener;
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
        tvSwitchSeat = getView().findViewById(R.id.btn_swich_seat);
        refreshView();
    }

    @Override
    public void initListener() {
        super.initListener();
        llCloseSeat.setOnClickListener(this::onClick);
        llMuteSeat.setOnClickListener(this::onClick);
        btnInviteUserIntoSeat.setOnClickListener(this::onClick);
        tvSwitchSeat.setOnClickListener(this::onClick);
    }

    /**
     * 禁麦或者开麦ß
     */
    private void muteOrUnMuteSeat() {
        if (seatActionClickListener != null)
            seatActionClickListener.clickMuteSeat(index, !isMute, new ClickCallback<Boolean>() {
                @Override
                public void onResult(Boolean result, String msg) {
                    if (result) {
                        dismiss();
                    }
                }
            });
    }

    /**
     * 座位的关闭和打开
     */
    private void closeOrOpenSeat() {
        if (seatActionClickListener != null)
            seatActionClickListener.clickCloseSeat(index, seatStatus == 0, new ClickCallback<Boolean>() {
                @Override
                public void onResult(Boolean result, String msg) {
                    KToast.show(msg);
                    if (result) {
                        dismiss();
                    }
                }
            });
    }

    /**
     * 根据当前麦位的信息去刷新Ui
     */
    public void refreshView() {
        if (isMute) {
            ivMuteSeat.setImageResource(R.drawable.ic_room_setting_unmute_all);
            tvMuteSeat.setText("取消禁麦");
            ivIsMute.setVisibility(View.VISIBLE);
        } else {
            ivMuteSeat.setImageResource(R.drawable.ic_member_setting_mute_seat);
            tvMuteSeat.setText("座位禁麦");
            ivIsMute.setVisibility(View.GONE);
        }
        if (seatStatus == 1) {
            ivSeatStatus.setImageResource(R.drawable.ic_seat_status_locked);
            ivUserPortrait.setImageResource(R.drawable.bg_seat_status);
            ivCloseSeat.setImageResource(R.drawable.ic_room_setting_unlock_all);
            tvCloseSeat.setText("开启座位");
        } else {
            ivSeatStatus.setImageResource(R.drawable.ic_seat_status_enter);
            ivUserPortrait.setImageResource(R.drawable.bg_seat_status);
            ivCloseSeat.setImageResource(R.drawable.ic_room_setting_lock_all);
            tvCloseSeat.setText("关闭座位");
        }
        tvSwitchSeat.setVisibility(isShowSwitchSeatBtn ? View.VISIBLE : View.GONE);
        tvMemberName.setText(index + "号麦位");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_close_seat) {
            closeOrOpenSeat();
        } else if (id == R.id.ll_mute_seat) {
            muteOrUnMuteSeat();
        } else if (id == R.id.btn_invite_user_into_seat) {
            if (iCommonDialog != null) {
                iCommonDialog.showSeatOperationViewPagerFragment(1, index);
            }
            dismiss();
        } else if (id == R.id.btn_swich_seat) {
            if (seatActionClickListener != null)
                seatActionClickListener.switchToSeat(index, new ClickCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean result, String msg) {
                        KToast.show(msg);
                        if (result) dismiss();
                    }
                });
        }
    }
}
