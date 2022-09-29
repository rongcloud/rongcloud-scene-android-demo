package cn.rongcloud.roomkit.ui.room.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.basis.utils.ImageLoader;

import cn.rongcloud.roomkit.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gyn
 * @date 2021/9/17
 */
public class RoomSeatView extends ConstraintLayout {
    private View mRootView;
    private WaveView mWaveView;
    private CircleImageView mPortraitView;
    private ImageView mMuteView;
    private TextView mRoomOwnerView;
    private TextView mGiftView;

    private boolean isMute;
    private ConstraintLayout mClSeat;
    private ConstraintLayout mClSelfPause;
    private AppCompatButton mBtnContinue;
    private ConstraintLayout mClViewerPause;
    private String roomOwnerName;
    private String roomOwnerPortrait;
    private SeatState seatState = SeatState.NORMAL;

    public RoomSeatView(@NonNull Context context) {
        this(context, null);
    }

    public RoomSeatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_room_seat, this);
        initView();
    }

    /**
     * 当点击头像的时候
     *
     * @param onClickListener
     */
    public void setRoomOwnerHeadOnclickListener(OnClickListener onClickListener) {
        mPortraitView.setOnClickListener(onClickListener);
    }

    private void initView() {
        mWaveView = mRootView.findViewById(R.id.wv_creator_background);
        mPortraitView = mRootView.findViewById(R.id.iv_room_creator_portrait);
        mMuteView = mRootView.findViewById(R.id.iv_is_mute);
        mRoomOwnerView = mRootView.findViewById(R.id.tv_room_creator_name);
        mGiftView = mRootView.findViewById(R.id.tv_gift_count);
        mClSeat = (ConstraintLayout) findViewById(R.id.cl_seat);
        mClSelfPause = (ConstraintLayout) findViewById(R.id.cl_self_pause);
        mBtnContinue = (AppCompatButton) findViewById(R.id.btn_continue);
        mClViewerPause = (ConstraintLayout) findViewById(R.id.cl_viewer_pause);
    }

    public void setData(String roomOwnerName, String roomOwnerPortrait) {
        this.roomOwnerName = roomOwnerName;
        this.roomOwnerPortrait = roomOwnerPortrait;
        refreshHead(roomOwnerName, roomOwnerPortrait);
    }

    private void refreshHead(String roomOwnerName, String roomOwnerPortrait) {
        mRoomOwnerView.setText(roomOwnerName);
        ImageLoader.loadUrl(mPortraitView, roomOwnerPortrait, R.drawable.ic_room_creator_not_in_seat);
    }

    /**
     * 设置房主静音状态
     *
     * @param isMute 是否静音
     */
    public void setRoomOwnerMute(boolean isMute) {
        this.isMute = isMute;
        if (isMute) {
            mMuteView.setVisibility(View.VISIBLE);
            mWaveView.stopImmediately();
        } else {
            mMuteView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置房主礼物数量
     *
     * @param count 礼物数量
     */
    public void setGiftCount(Long count) {
        mGiftView.setText(String.valueOf(count));
    }

    /**
     * 房主说话状态
     *
     * @param isSpeaking 是否正在讲话
     */
    public void setSpeaking(boolean isSpeaking) {
        if (seatState != SeatState.NORMAL) {
            mWaveView.stop();
            return;
        }
        if (isMute) {
            mWaveView.stop();
            return;
        }
        if (isSpeaking) {
            mWaveView.start();
        } else {
            mWaveView.stop();
        }
    }

    /**
     * 点击继续直播按钮
     *
     * @param l
     */
    public void setResumeLiveClickListener(OnClickListener l) {
        mBtnContinue.setOnClickListener(l);
    }

    /**
     * 设置房主是否暂停
     *
     * @param seatState
     */
    public void refreshSeatState(SeatState seatState) {
        this.seatState = seatState;
        mClSeat.setVisibility(INVISIBLE);
        mClSelfPause.setVisibility(GONE);
        mClViewerPause.setVisibility(GONE);
        switch (seatState) {
            case NORMAL:
                mClSeat.setVisibility(VISIBLE);
                refreshHead(roomOwnerName, roomOwnerPortrait);
                break;
            case OWNER_PAUSE:
                mClSelfPause.setVisibility(VISIBLE);
                setSpeaking(false);
                break;
            case VIEWER_PAUSE:
                mClViewerPause.setVisibility(VISIBLE);
                setSpeaking(false);
                break;
            case LEAVE_SEAT:
                mClSeat.setVisibility(VISIBLE);
                refreshHead("", "");
                setSpeaking(false);
                break;
        }
    }

    public enum SeatState {
        OWNER_PAUSE,
        VIEWER_PAUSE,
        NORMAL,
        LEAVE_SEAT
    }
}
