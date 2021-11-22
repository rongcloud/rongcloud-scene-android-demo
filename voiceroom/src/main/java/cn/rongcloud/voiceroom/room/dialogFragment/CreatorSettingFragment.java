package cn.rongcloud.voiceroom.room.dialogFragment;


import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.Guideline;

import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rongcloud.voiceroom.R;
import cn.rongcloud.voiceroom.room.VoiceRoomModel;
import cn.rongcloud.voiceroom.ui.uimodel.UiSeatModel;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * 房主点击自己的头像弹窗
 */
public class CreatorSettingFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {


    private Guideline glBg;
    private CircleImageView ivMemberPortrait;
    private AppCompatTextView tvMemberName;
    private AppCompatTextView btnOutOfSeat;
    private AppCompatTextView btnMuteSelf;
    private ConfirmDialog confirmDialog;
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
        btnMuteSelf.setText(uiSeatModel.isMute() ? "打开麦克风" : "关闭麦克风");
        tvMemberName.setText(user.getUserName());
        ImageLoaderUtil.INSTANCE.loadImage(getContext(), ivMemberPortrait, user.getPortraitUrl(), R.drawable.default_portrait);
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
                            EToast.showToast(throwable.getMessage());
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
                    public void accept(Throwable throwable) throws Throwable {
                        EToast.showToast(throwable.getMessage());
                    }
                });
    }

    private void showMusicPauseTip() {
        confirmDialog = new ConfirmDialog(requireContext(), "播放音乐中下麦会导致音乐中断，是否确定下麦?",
                true, "确定", "取消", null, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //确定
                MusicManager.get().stopPlayMusic();
                leaveSeat();
                return null;
            }
        });
        confirmDialog.show();
    }
}
