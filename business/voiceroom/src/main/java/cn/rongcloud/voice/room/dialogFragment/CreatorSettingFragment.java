package cn.rongcloud.voice.room.dialogFragment;


import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.Guideline;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.widget.dialog.VRCenterDialog;
import com.rc.voice.R;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.voice.room.VoiceRoomModel;
import cn.rongcloud.voice.model.UiSeatModel;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 房主点击自己的头像弹窗
 */
public class CreatorSettingFragment extends BaseBottomSheetDialog implements View.OnClickListener {


    private Guideline glBg;
    private CircleImageView ivMemberPortrait;
    private AppCompatTextView tvMemberName;
    private AppCompatTextView btnOutOfSeat;
    private AppCompatTextView btnMuteSelf;
    private VRCenterDialog confirmDialog;
    private VoiceRoomModel voiceRoomModel;
    private UiSeatModel uiSeatModel;
    private User user;

    public CreatorSettingFragment(VoiceRoomModel voiceRoomModel, UiSeatModel uiSeatModel, User user) {
        super(R.layout.fragmeng_new_creator_setting);
        this.voiceRoomModel = voiceRoomModel;
        this.uiSeatModel = uiSeatModel;
        this.user = user;
    }

    @Override
    public void initView() {
        glBg = (Guideline) getView().findViewById(R.id.gl_bg);
        ivMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        tvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        btnOutOfSeat = (AppCompatTextView) getView().findViewById(R.id.btn_out_of_seat);
        btnMuteSelf = (AppCompatTextView) getView().findViewById(R.id.btn_mute_self);
        if (uiSeatModel.getExtra() == null) {
            btnMuteSelf.setText("关闭麦克风");
        } else {
            btnMuteSelf.setText(uiSeatModel.getExtra().isDisableRecording() ? "打开麦克风" : "关闭麦克风");
        }
        tvMemberName.setText(user.getUserName());
        ImageLoader.loadUrl(ivMemberPortrait, user.getPortraitUrl(), R.drawable.default_portrait);
    }

    @Override
    public void initListener() {
        btnOutOfSeat.setOnClickListener(this::onClick);
        btnMuteSelf.setOnClickListener(this::onClick);
        super.initListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_out_of_seat) {//是否下麦围观
            //判断是否在播放音乐
            if (voiceRoomModel.isPlayingMusic()) {
                showMusicPauseTip();
            } else {
                leaveSeat();
            }
        } else if (v.getId() == R.id.btn_mute_self) {
            //操作关闭还是打开麦克风
            voiceRoomModel.creatorMuteSelf()
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Throwable {
                            dismiss();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            KToast.show(throwable.getMessage());
                        }
                    });
        }
    }

    /**
     * 离开麦位
     */
    private void leaveSeat() {
        voiceRoomModel.leaveSeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Throwable {
                        dismiss();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        KToast.show(throwable.getMessage());
                    }
                });
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
                        leaveSeat();
                    }
                }, null);
        confirmDialog.show();
    }
}
