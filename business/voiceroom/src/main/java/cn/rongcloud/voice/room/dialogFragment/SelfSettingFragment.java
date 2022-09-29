package cn.rongcloud.voice.room.dialogFragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.Guideline;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.rc.voice.R;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.voice.room.VoiceRoomModel;
import cn.rongcloud.voice.model.UiSeatModel;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * 麦位上点击自己的头像 弹窗
 */
public class SelfSettingFragment extends BaseBottomSheetDialog {


    private Guideline glBg;
    private CircleImageView ivMemberPortrait;
    private AppCompatTextView tvMemberName;
    private AppCompatTextView btnMuteSelf;
    private AppCompatTextView btnOutOfSeat;
    private UiSeatModel seatInfo;
    private String roomId;
    private VoiceRoomModel voiceRoomModel;
    private boolean isLeaveSeating = false;//是否正在断开连接中
    private User user;

    public SelfSettingFragment(UiSeatModel seatInfo, String roomId, VoiceRoomModel voiceRoomModel, User user) {
        super(R.layout.fragment_new_self_setting);
        this.seatInfo = seatInfo;
        this.roomId = roomId;
        this.voiceRoomModel = voiceRoomModel;
        this.user = user;
    }

    @Override
    public void initView() {
        glBg = (Guideline) getView().findViewById(R.id.gl_bg);
        ivMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        tvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        btnMuteSelf = (AppCompatTextView) getView().findViewById(R.id.btn_mute_self);
        btnOutOfSeat = (AppCompatTextView) getView().findViewById(R.id.btn_out_of_seat);

        ImageLoader.loadUrl(ivMemberPortrait, user.getPortraitUrl(), R.drawable.default_portrait);
        tvMemberName.setText(user.getUserName());
        btnMuteSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteSelf();
            }
        });
        btnOutOfSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveSeat();
            }
        });
        onRecordStatusChange(voiceRoomModel.isRecordingStatus());

        //监听一下状态
        voiceRoomModel.addSubscription(voiceRoomModel.obSeatInfoByIndex(seatInfo.getIndex())
                .subscribe(new Consumer<UiSeatModel>() {
                    @Override
                    public void accept(UiSeatModel uiSeatModel) throws Throwable {
                        if (null != seatInfo && !TextUtils.isEmpty(seatInfo.getUserId()) && !seatInfo.getUserId().equals(UserManager.get().getUserId())) {
                            if (isLeaveSeating) {
                                ToastUtils.s(getContext(), "正在断开链接中");
                            }
                            fragmentDismiss();
                        } else {
                            seatInfo = uiSeatModel;
                        }
                    }
                }));
    }


    /**
     * 断开链接
     */
    private void leaveSeat() {
        isLeaveSeating = true;
        voiceRoomModel.leaveSeat()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        isLeaveSeating = false;
                    }
                }).doOnComplete(new Action() {
            @Override
            public void run() throws Throwable {
                ToastUtils.s(getContext(), "您已断开连接");
                dismiss();
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                ToastUtils.s(getContext(), throwable.getMessage());
            }
        }).subscribe();
    }

    /**
     * 设置声音
     *
     * @return
     */
    public void muteSelf() {
        if (seatInfo.getUserId() != null) {
            if (seatInfo.isMute()) {
                ToastUtils.s(getContext(), "此座位已被管理员禁麦");
                return;
            }
            Completable.create(new CompletableOnSubscribe() {
                @Override
                public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                    //如果麦克风打开的，那么关闭方法调用
                    RCVoiceRoomEngine.getInstance().disableAudioRecording(voiceRoomModel.isRecordingStatus());
                    voiceRoomModel.setRecordingStatus(!voiceRoomModel.isRecordingStatus());
                    emitter.onComplete();
                }
            }).doOnComplete(new Action() {
                @Override
                public void run() throws Throwable {
                    ToastUtils.s(getContext(), "修改成功");
                    dismiss();
                }
            }).doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Throwable {
                    ToastUtils.s(getContext(), throwable.toString());
                }
            }).subscribe();

        } else {
            ToastUtils.s(getContext(), "您已不在该麦位上");
            fragmentDismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void fragmentDismiss() {
        dismiss();
    }

    /**
     * 设置当前状态
     *
     * @param isRecording
     */
    public void onRecordStatusChange(boolean isRecording) {
        if (!isRecording) {
            btnMuteSelf.setText("打开麦克风");
        } else {
            btnMuteSelf.setText("关闭麦克风");
        }
    }
}
