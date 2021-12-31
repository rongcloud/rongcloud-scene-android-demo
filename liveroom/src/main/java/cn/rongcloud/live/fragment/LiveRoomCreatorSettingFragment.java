package cn.rongcloud.live.fragment;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.provider.user.User;
import cn.rongcloud.live.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author 李浩
 * @date 2021/12/12
 */
public class LiveRoomCreatorSettingFragment extends BaseBottomSheetDialogFragment {

    OnCreatorSettingClickListener onCreatorSettingClickListener;
    private boolean isMute;
    private boolean isVideo;
    private User user;
    private CircleImageView mIvMemberPortrait;
    private AppCompatTextView mTvMemberName;
    private AppCompatTextView mTvSwitchLinkStatus;
    private AppCompatTextView mBtnMuteSelf;
    private int index;
    private boolean isOwner;
    private AppCompatTextView btn_disconnect;
    private View iv_member_portrait_bg;

    public LiveRoomCreatorSettingFragment(int index, boolean isMute, boolean isVideo, User user, OnCreatorSettingClickListener onCreatorSettingClickListener, boolean isOwner) {
        super(R.layout.fragment_live_room_creator_setting);
        this.isMute = isMute;
        this.onCreatorSettingClickListener = onCreatorSettingClickListener;
        this.user = user;
        this.isVideo = isVideo;
        this.index = index;
        this.isOwner = isOwner;
    }

    @Override
    public void initView() {
        mIvMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        mTvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        mTvSwitchLinkStatus = (AppCompatTextView) getView().findViewById(R.id.tv_switch_link_status);
        mBtnMuteSelf = (AppCompatTextView) getView().findViewById(R.id.btn_mute_self);

        iv_member_portrait_bg = (View) getView().findViewById(R.id.iv_member_portrait_bg);
        btn_disconnect = (AppCompatTextView) getView().findViewById(R.id.btn_disconnect);
        if (!isOwner) {
            btn_disconnect.setVisibility(View.VISIBLE);
            iv_member_portrait_bg.setVisibility(View.INVISIBLE);
            mIvMemberPortrait.setVisibility(View.INVISIBLE);
        } else {
            btn_disconnect.setVisibility(View.GONE);
        }
        refreshMuteBtn();
        refreshLinkStatus();
        if (user != null) {
            if (isOwner) {
                mTvMemberName.setText(user.getUserName());
            } else {
                mTvMemberName.setText(isVideo ? "正在进行视频连线" : "正在进行语音连线");
            }
            ImageLoaderUtil.INSTANCE.loadImage(getContext(), mIvMemberPortrait, user.getPortraitUrl(), cn.rong.combusis.R.drawable.default_portrait);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        if (onCreatorSettingClickListener == null) {
            return;
        }
        mTvSwitchLinkStatus.setOnClickListener(v -> {
            isVideo = !isVideo;
            onCreatorSettingClickListener.clickSwitchLinkStatus(index, isVideo);
            refreshLinkStatus();
            dismiss();
        });
        mBtnMuteSelf.setOnClickListener(v -> {
            isMute = !isMute;
            onCreatorSettingClickListener.clickMuteSelf(index, isMute);
            refreshMuteBtn();
            dismiss();
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreatorSettingClickListener.disConnect(index);
                dismiss();
            }
        });
    }

    private void refreshMuteBtn() {
        mBtnMuteSelf.setText(isMute ? "打开麦克风" : "关闭麦克风");
    }

    private void refreshLinkStatus() {
        mTvSwitchLinkStatus.setText(isVideo ? "切换到语音连线" : "切换到视频连线");
    }


    public interface OnCreatorSettingClickListener {
        void clickSwitchLinkStatus(int index, boolean isVideo);

        void clickMuteSelf(int index, boolean isMute);

        void disConnect(int index);
    }
}
