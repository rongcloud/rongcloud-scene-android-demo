package cn.rongcloud.gameroom.ui.gameroom;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.VRCenterDialog;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.SeatPlayer;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imlib.model.UserInfo;


/**
 * @author gyn
 * @date 2022/5/18
 */
public class SelfSettingDialog extends BaseBottomSheetDialog {
    private CircleImageView mIvMemberPortrait;
    private AppCompatTextView mTvMemberName;
    private AppCompatTextView mBtnMuteSelf;
    private AppCompatTextView mBtnCloseMic;
    private AppCompatTextView mBtnOutOfSeat;

    private SeatPlayer seatPlayer;
    private boolean isOwner;
    private VRCenterDialog confirmDialog;

    public SelfSettingDialog(SeatPlayer seatPlayer, boolean isOwner) {
        super(R.layout.game_dialog_self_setting);
        this.seatPlayer = seatPlayer;
        this.isOwner = isOwner;
    }

    @Override
    public void initView() {
        mIvMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        mTvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        mBtnMuteSelf = (AppCompatTextView) getView().findViewById(R.id.btn_mute_self);
        mBtnCloseMic = (AppCompatTextView) getView().findViewById(R.id.btn_close_mic);
        mBtnOutOfSeat = (AppCompatTextView) getView().findViewById(R.id.btn_out_of_seat);

        UserProvider.provider().getAsyn(seatPlayer.userId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                ImageLoader.loadUrl(mIvMemberPortrait, userInfo.getPortraitUri().toString(), R.drawable.default_portrait);
                mTvMemberName.setText(userInfo.getName());
            }
        });

        mBtnMuteSelf.setOnClickListener(v -> {
            GameEventHelper.getInstance().muteSeat(seatPlayer.seatIndex, !seatPlayer.isMute);
            dismiss();
        });
        mBtnCloseMic.setOnClickListener(v -> {

            if (seatPlayer.isMute && !isOwner) {
                ToastUtils.s(getContext(), "此座位已被管理员禁麦");
            } else {
                GameEventHelper.getInstance().changeRecord();
            }
            dismiss();
        });

        mBtnOutOfSeat.setOnClickListener(v -> {
            //判断是否在播放音乐
            if (MusicControlManager.getInstance().isPlaying()) {
                showMusicPauseTip();
            } else {
                GameEventHelper.getInstance().leaveSeat();
            }
            dismiss();
        });

        // 房主可以禁麦
        if (isOwner) {
            mBtnMuteSelf.setVisibility(View.VISIBLE);
            onMuteChange(!seatPlayer.isMute);
        } else {
            mBtnMuteSelf.setVisibility(View.GONE);
        }
        // 其他人是自己本地静音
        onMicChange(!RCVoiceRoomEngine.getInstance().isDisableAudioRecording());
    }

    public void onMicChange(boolean isRecording) {
        if (!isRecording) {
            mBtnCloseMic.setText("打开麦克风");
        } else {
            mBtnCloseMic.setText("关闭麦克风");
        }
    }

    public void onMuteChange(boolean isRecording) {
        if (!isRecording) {
            mBtnMuteSelf.setText("取消禁麦");
        } else {
            mBtnMuteSelf.setText("座位禁麦");
        }
    }


    private void showMusicPauseTip() {
        confirmDialog = new VRCenterDialog(requireActivity(), null);
        confirmDialog.replaceContent("播放音乐中下麦会导致音乐中断，是否确定下麦?",
                "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MusicControlManager.getInstance().stopPlayMusic();
                        GameEventHelper.getInstance().leaveSeat();
                    }
                }, null);
        confirmDialog.show();
    }
}
