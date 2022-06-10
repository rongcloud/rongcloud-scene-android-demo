package cn.rongcloud.roomkit.ui.room.fragment;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.widget.dialog.VRCenterDialog;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gyn
 * @date 2021/10/12
 */
public class CreatorSettingFragment extends BaseBottomSheetDialog {

    OnCreatorSettingClickListener onCreatorSettingClickListener;
    private boolean isMute;
    private boolean isPlayMusic;
    private User user;
    private CircleImageView mIvMemberPortrait;
    private AppCompatTextView mTvMemberName;
    private AppCompatTextView mBtnOutOfSeat;
    private AppCompatTextView mBtnMuteSelf;
    private VRCenterDialog confirmDialog;

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
            ImageLoader.loadUrl(mIvMemberPortrait, user.getPortraitUrl(), R.drawable.default_portrait);
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
        confirmDialog = new VRCenterDialog(getActivity(), null);
        confirmDialog.replaceContent(getString(R.string.text_pause_music_tips), getString(R.string.cancel), null,
                getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确定
                        if (onCreatorSettingClickListener != null) {
                            onCreatorSettingClickListener.clickLeaveSeat();
                        }
                    }
                }, null);
        confirmDialog.show();
    }

    public interface OnCreatorSettingClickListener {
        void clickLeaveSeat();

        void clickMuteSelf(boolean isMute);
    }
}
