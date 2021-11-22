package cn.rong.combusis.ui.room.fragment;

import androidx.appcompat.widget.AppCompatTextView;

import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.provider.user.User;
import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author gyn
 * @date 2021/10/12
 */
public class CreatorSettingFragment extends BaseBottomSheetDialogFragment {

    OnCreatorSettingClickListener onCreatorSettingClickListener;
    private boolean isMute;
    private boolean isPlayMusic;
    private User user;
    private CircleImageView mIvMemberPortrait;
    private AppCompatTextView mTvMemberName;
    private AppCompatTextView mBtnOutOfSeat;
    private AppCompatTextView mBtnMuteSelf;
    private ConfirmDialog confirmDialog;

    public CreatorSettingFragment(boolean isMute, boolean isPlayMusic, User user, OnCreatorSettingClickListener onCreatorSettingClickListener) {
        super(R.layout.fragment_creator_setting);
        this.isMute = isMute;
        this.onCreatorSettingClickListener = onCreatorSettingClickListener;
        this.isPlayMusic = isPlayMusic;
        this.user = user;
    }

    @Override
    public void initView() {
        mIvMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        mTvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        mBtnOutOfSeat = (AppCompatTextView) getView().findViewById(R.id.btn_out_of_seat);
        mBtnMuteSelf = (AppCompatTextView) getView().findViewById(R.id.btn_mute_self);
        refreshMuteBtn();
        if (user != null) {
            mTvMemberName.setText(user.getUserName());
            ImageLoaderUtil.INSTANCE.loadImage(getContext(), mIvMemberPortrait, user.getPortraitUrl(), R.drawable.default_portrait);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        if (onCreatorSettingClickListener == null) {
            return;
        }
        mBtnOutOfSeat.setOnClickListener(v -> {
            if (isPlayMusic) {
                showMusicPauseTip();
            } else {
                onCreatorSettingClickListener.clickLeaveSeat();
            }
            dismiss();
        });
        mBtnMuteSelf.setOnClickListener(v -> {
            isMute = !isMute;
            onCreatorSettingClickListener.clickMuteSelf(isMute);
            refreshMuteBtn();
        });
    }

    private void refreshMuteBtn() {
        mBtnMuteSelf.setText(isMute ? "打开麦克风" : "关闭麦克风");
    }

    private void showMusicPauseTip() {
        confirmDialog = new ConfirmDialog(requireContext(), "播放音乐中下麦会导致音乐中断，是否确定下麦?",
                true, "确定", "取消", null, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //确定
                if (onCreatorSettingClickListener != null) {
                    onCreatorSettingClickListener.clickLeaveSeat();
                }
                return null;
            }
        });
        confirmDialog.show();
    }

    public interface OnCreatorSettingClickListener {
        void clickLeaveSeat();

        void clickMuteSelf(boolean isMute);
    }
}
