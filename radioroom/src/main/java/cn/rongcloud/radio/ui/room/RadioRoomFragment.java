package cn.rongcloud.radio.ui.room;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.UIStack;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.ImageLoaderUtil;
import com.rongcloud.common.utils.UiUtils;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.common.ui.dialog.EditDialog;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
import cn.rong.combusis.common.ui.dialog.TipDialog;
import cn.rong.combusis.message.RCAllBroadcastMessage;
import cn.rong.combusis.message.RCChatroomLike;
import cn.rong.combusis.message.RCChatroomVoice;
import cn.rong.combusis.music.MusicDialog;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.user.UserProvider;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.RoomType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.ui.room.AbsRoomActivity;
import cn.rong.combusis.ui.room.AbsRoomFragment;
import cn.rong.combusis.ui.room.RoomMessageAdapter;
import cn.rong.combusis.ui.room.dialog.ExitRoomPopupWindow;
import cn.rong.combusis.ui.room.dialog.RoomNoticeDialog;
import cn.rong.combusis.ui.room.dialog.shield.ShieldDialog;
import cn.rong.combusis.ui.room.fragment.BackgroundSettingFragment;
import cn.rong.combusis.ui.room.fragment.CreatorSettingFragment;
import cn.rong.combusis.ui.room.fragment.MemberListFragment;
import cn.rong.combusis.ui.room.fragment.MemberSettingFragment;
import cn.rong.combusis.ui.room.fragment.gift.GiftFragment;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSettingFragment;
import cn.rong.combusis.ui.room.model.Member;
import cn.rong.combusis.ui.room.widget.AllBroadcastView;
import cn.rong.combusis.ui.room.widget.GiftAnimationView;
import cn.rong.combusis.ui.room.widget.RoomBottomView;
import cn.rong.combusis.ui.room.widget.RoomSeatView;
import cn.rong.combusis.ui.room.widget.RoomTitleBar;
import cn.rong.combusis.widget.miniroom.MiniRoomManager;
import cn.rongcloud.radio.R;
import cn.rongcloud.radio.helper.RadioEventHelper;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.utils.StatusBarUtil;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/9/17
 */
public class RadioRoomFragment extends AbsRoomFragment<RadioRoomPresenter> implements
        RoomMessageAdapter.OnClickMessageUserListener, RadioRoomView, RoomBottomView.OnBottomOptionClickListener,
        MemberListFragment.OnClickUserListener, View.OnClickListener, AllBroadcastView.OnClickBroadcast {
    private ImageView mBackgroundImageView;
    private GiftAnimationView mGiftAnimationView;
    private RoomTitleBar mRoomTitleBar;
    private TextView mNoticeView;
    private RoomSeatView mRoomSeatView;
    private RoomBottomView mRoomBottomView;
    private RecyclerView mMessageView;
    private View mCoverView;
    private AllBroadcastView mAllBroadcastView;
    private ConstraintLayout clVoiceRoomView;
    private RelativeLayout rlRoomFinishedId;
    private Button btnGoBackList;

    private RoomMessageAdapter mRoomMessageAdapter;
    private ExitRoomPopupWindow mExitRoomPopupWindow;
    private RoomNoticeDialog mNoticeDialog;
    private MemberListFragment mMemberListFragment;
    private MemberSettingFragment mMemberSettingFragment;
    private RoomSettingFragment mRoomSettingFragment;
    private InputPasswordDialog mInputPasswordDialog;
    private EditDialog mEditDialog;
    private BackgroundSettingFragment mBackgroundSettingFragment;
    private ShieldDialog mShieldDialog;
    private GiftFragment mGiftFragment;
    private CreatorSettingFragment mCreatorSettingFragment;
    private MusicDialog mMusicDialog;

    private String mRoomId;

    public static Fragment getInstance(String roomId) {
        Bundle bundle = new Bundle();
        bundle.putString(ROOM_ID, roomId);
        Fragment fragment = new RadioRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public RadioRoomPresenter createPresent() {
        return new RadioRoomPresenter(this, getViewLifecycleOwner());
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_radio_room;
    }

    @Override
    public void init() {
        mRoomId = getArguments().getString(ROOM_ID);
        mRoomSettingFragment = new RoomSettingFragment(present);
        // 双击点赞的view
        mGiftAnimationView = getView().findViewById(R.id.gift_view);

        clVoiceRoomView = (ConstraintLayout) getView().findViewById(R.id.cl_voice_room_view);
        rlRoomFinishedId = (RelativeLayout) getView().findViewById(R.id.rl_room_finished_id);
        btnGoBackList = (Button) getView().findViewById(R.id.btn_go_back_list);
        // 全局广播View
        mAllBroadcastView = getView(R.id.view_all_broadcast);
        mAllBroadcastView.setOnClickBroadcast(this::clickBroadcast);
        // 头部
        mRoomTitleBar = getView(R.id.room_title_bar);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mRoomTitleBar.getLayoutParams();
        params.topMargin = StatusBarUtil.getStatusBarHeight(requireContext());
        mRoomTitleBar.setLayoutParams(params);

        mNoticeView = getView(R.id.tv_notice);

        // 背景
        mBackgroundImageView = getView(R.id.iv_background);
        // 房主座位
        mRoomSeatView = getView(R.id.room_seat_view);

        // 底部操作按钮和双击送礼物
        mRoomBottomView = getView(R.id.room_bottom_view);
        // 弹幕消息列表
        mMessageView = getView(R.id.rv_message);
        mMessageView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageView.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.INSTANCE.dp2Px(getContext(), 5)));
        mRoomMessageAdapter = new RoomMessageAdapter(getContext(), this, RoomType.RADIO_ROOM);
        mMessageView.setAdapter(mRoomMessageAdapter);

        mCoverView = getView(R.id.view_cover);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnGoBackList.setOnClickListener(this::onClick);
        mGiftAnimationView.setOnBottomOptionClickListener(new GiftAnimationView.OnClickBackgroundListener() {
            @Override
            public void onSendLikeMessage(RCChatroomLike rcChatroomLike) {
                present.sendMessage(rcChatroomLike);
            }
        });
        mRoomTitleBar.setOnMenuClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                clickMenu();
            }
        });
        mRoomTitleBar.setOnMemberClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                mMemberListFragment = new MemberListFragment(present.getRoomId(), RadioRoomFragment.this);
                mMemberListFragment.show(getChildFragmentManager());
            }
        });
        mNoticeView.setOnClickListener(v -> {
            present.getNotice(false);
        });
        mRoomSeatView.setRoomOwnerHeadOnclickListener(v -> {
            present.clickRoomSeat();
        });
        mRoomSeatView.setResumeLiveClickListener(v -> {
            present.enterSeat();
        });
    }

    @Override
    public void joinRoom() {
        present.init(mRoomId);
        mAllBroadcastView.setBroadcastListener();
    }

    /**
     * 设置房间数据
     *
     * @param voiceRoomBean
     * @param roomOwnerType
     */
    @Override
    public void setRoomData(VoiceRoomBean voiceRoomBean, RoomOwnerType roomOwnerType) {
        clVoiceRoomView.setVisibility(View.VISIBLE);
        rlRoomFinishedId.setVisibility(View.GONE);
        // 加载背景
        setRoomBackground(voiceRoomBean.getBackgroundUrl());
        // 设置title数据
        mRoomTitleBar.setData(roomOwnerType, voiceRoomBean.getRoomName(), voiceRoomBean.getId(), voiceRoomBean.getUserId(), present);
        mRoomTitleBar.setDelay(0, false);
        // 设置房主麦位信息
        mRoomSeatView.setData(voiceRoomBean.getCreateUserName(), voiceRoomBean.getCreateUserPortrait());
        // 设置底部按钮
        mRoomBottomView.setData(roomOwnerType, this, voiceRoomBean.getRoomId());
        // 设置消息列表数据
        mRoomMessageAdapter.setRoomCreateId(voiceRoomBean.getCreateUserId());
    }

    @Override
    public void onBackPressed() {
        clickMenu();
    }

    @Override
    public void addSwitchRoomListener() {
        ((AbsRoomActivity) requireActivity()).addSwitchRoomListener(mRoomId, this);
    }

    @Override
    public void removeSwitchRoomListener() {
        ((AbsRoomActivity) requireActivity()).removeSwitchRoomListener(mRoomId);
    }

    @Override
    public void addToMessageList(MessageContent messageContent, boolean isRefresh) {
        List<MessageContent> list = new ArrayList<>(1);
        if (messageContent != null) {
            list.add(messageContent);
        }
        mRoomMessageAdapter.setData(list, isRefresh);
        int count = mRoomMessageAdapter.getItemCount();
        if (count > 0) {
            mMessageView.smoothScrollToPosition(count - 1);
        }
    }

    @Override
    public void addAllToMessageList(List<MessageContent> messageContents, boolean isRefresh) {
        mRoomMessageAdapter.setData(messageContents, isRefresh);
        int count = mRoomMessageAdapter.getItemCount();
        if (count > 0) {
            mMessageView.smoothScrollToPosition(count - 1);
        }
    }

    @Override
    public void finish() {
        //在销毁之前提前出栈顶
        try {
            UIStack.getInstance().remove(((RadioRoomActivity) requireActivity()));
            requireActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSpeaking(boolean speaking) {
        mRoomSeatView.setSpeaking(speaking);
    }

    @Override
    public void setRadioName(String name) {
        mRoomTitleBar.setRoomName(name);
    }

    @Override
    public void showNotice(String notice, boolean isModify) {
        mNoticeDialog = new RoomNoticeDialog(activity);
        mNoticeDialog.show(notice, isModify, newNotice -> {
            present.modifyNotice(newNotice);
        });
    }

    @Override
    public void setSeatState(RoomSeatView.SeatState seatState) {
        mRoomSeatView.refreshSeatState(seatState);
        if (seatState == RoomSeatView.SeatState.OWNER_PAUSE) {
            mCoverView.setVisibility(View.VISIBLE);
        } else {
            mCoverView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSeatMute(boolean isMute) {
        mRoomSeatView.setRoomOwnerMute(isMute);
    }

    @Override
    public void showSettingDialog(List<MutableLiveData<IFun.BaseFun>> funList) {
        mRoomSettingFragment.show(getChildFragmentManager(), funList);
    }

    @Override
    public void showSetPasswordDialog(MutableLiveData<IFun.BaseFun> item) {
        mInputPasswordDialog = new InputPasswordDialog(getContext(), false, () -> null, s -> {
            if (TextUtils.isEmpty(s)) {
                return null;
            }
            if (s.length() < 4) {
                showToast(getString(R.string.text_please_input_four_number));
                return null;
            }
            mInputPasswordDialog.dismiss();
            present.setRoomPassword(true, s, item);
            return null;
        });
        mInputPasswordDialog.show();
    }

    @Override
    public void showSetRoomNameDialog(String name) {
        mEditDialog = new EditDialog(
                requireActivity(),
                "修改房间标题",
                "请输入房间名",
                name,
                10,
                false,
                () -> null,
                s -> {
                    if (TextUtils.isEmpty(s)) {
                        showToast("房间名称不能为空");
                        return null;
                    }
                    present.setRoomName(s);
                    mEditDialog.dismiss();
                    return null;
                }
        );
        mEditDialog.show();
    }

    @Override
    public void showSelectBackgroundDialog(String url) {
        mBackgroundSettingFragment = new BackgroundSettingFragment(url, present);
        mBackgroundSettingFragment.show(getChildFragmentManager());
    }

    @Override
    public void setRoomBackground(String url) {
        ImageLoaderUtil.INSTANCE.loadImage(requireContext(), mBackgroundImageView, url, R.color.black);
    }

    @Override
    public void showShieldDialog(String roomId) {
        mShieldDialog = new ShieldDialog(requireActivity(), roomId, 10);
        mShieldDialog.show();
    }

    @Override
    public void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members) {
        mGiftFragment = new GiftFragment(voiceRoomBean, selectUserId, present);
        mGiftFragment.refreshMember(members);
        mGiftFragment.show(getChildFragmentManager());
    }

    @Override
    public void setGiftCount(Long count) {
        mRoomSeatView.setGiftCount(count);
    }

    @Override
    public void showUserSetting(Member member) {
        mMemberSettingFragment = new MemberSettingFragment(present.getRoomOwnerType(), present);
        mMemberSettingFragment.show(getChildFragmentManager(), member, present.getCreateUserId());
    }

    @Override
    public void showLikeAnimation() {
        mGiftAnimationView.showFov(mRoomBottomView.getGiftViewPoint());
    }

    @Override
    public void showCreatorSetting(boolean isMute, boolean isPlayingMusic, User user) {
        mCreatorSettingFragment = new CreatorSettingFragment(isMute, isPlayingMusic, user, present);
        mCreatorSettingFragment.show(getChildFragmentManager());
    }

    @Override
    public void showMusicDialog() {
        mMusicDialog = new MusicDialog(present.getRoomId());
        mMusicDialog.show(getChildFragmentManager());
    }

    @Override
    public void showRoomCloseDialog() {
        TipDialog closeRoomDialog = new TipDialog(
                requireContext(),
                "当前直播已结束",
                "确定", "", () -> {
            present.leaveRoom();
            return null;
        });
        closeRoomDialog.setCancelable(false);
        closeRoomDialog.show();
    }

    @Override
    public void showFinishView() {
        clVoiceRoomView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.VISIBLE);
    }

    @Override
    public void switchOtherRoom(String roomId) {
        ((AbsRoomActivity) requireActivity()).switchOtherRoom(roomId);
    }

    @Override
    public void refreshMessageList() {
        mRoomMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTitleFollow(boolean isFollow) {
        mRoomTitleBar.setFollow(isFollow);
    }

    /**
     * 点击右上角菜单按钮
     */
    private void clickMenu() {
        if (present.getRoomOwnerType() == null) {
            finish();
            return;
        }
        mExitRoomPopupWindow = new ExitRoomPopupWindow(getContext(), present.getRoomOwnerType(), new ExitRoomPopupWindow.OnOptionClick() {
            @Override
            public void clickPackRoom() {
                //最小化窗口,判断是否有权限
                if (checkDrawOverlaysPermission(false)) {
                    RadioEventHelper.getInstance().setMiniRoomListener(MiniRoomManager.getInstance());
                    MiniRoomManager.getInstance().show(requireContext(), present.getRoomId(), present.getThemePictureUrl(), requireActivity().getIntent(), RadioEventHelper.getInstance());
                    finish();
                } else {
                    showOpenOverlaysPermissionDialog();
                }
            }

            @Override
            public void clickLeaveRoom() {
                // 观众离开房间
                present.leaveRoom();
            }

            @Override
            public void clickCloseRoom() {
                new ConfirmDialog(requireContext(), "确定结束本次直播吗？", true, "确定", "取消", () -> null, () -> {
                    // 房主关闭房间
                    present.closeRoom();
                    return null;
                }).show();
            }
        });
        mExitRoomPopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        mExitRoomPopupWindow.showAtLocation(mBackgroundImageView, Gravity.TOP, 0, 0);
    }

    @Override
    public void setOnlineCount(int num) {
        mRoomTitleBar.setOnlineNum(num);
    }


    @Override
    public void destroyRoom() {
//        Logger.e("===================destroyRoom" + mRoomId);
        super.destroyRoom();
        clVoiceRoomView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.GONE);
        present.switchRoom();
    }

    @Override
    public void clickMessageUser(String userId) {
        UserProvider.provider().getAsyn(userId, userInfo -> {
            User user = new User();
            user.setUserId(userId);
            user.setUserName(userInfo.getName());
            user.setPortrait(userInfo.getPortraitUri().toString());
            clickUser(user);
        });
    }

    @Override
    public void clickSendMessage(String message) {
        present.sendMessage(message);
    }

    @Override
    public void clickPrivateMessage() {
        RouteUtils.routeToSubConversationListActivity(
                requireActivity(),
                Conversation.ConversationType.PRIVATE,
                "消息"
        );
    }

    @Override
    public void clickSeatOrder() {

    }

    @Override
    public void clickSettings() {
        present.showSettingDialog();
    }

    @Override
    public void clickPk() {

    }

    @Override
    public void clickRequestSeat() {

    }

    @Override
    public void onSendGift() {
        present.sendGift();
    }

    /**
     * 松手发送语音消息
     *
     * @param rcChatroomVoice
     */
    @Override
    public void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice) {
        present.sendMessage(rcChatroomVoice);
    }

    @Override
    public void clickUser(User user) {
        if (TextUtils.equals(user.getUserId(), AccountStore.INSTANCE.getUserId())) {
            return;
        }
        present.getUserInfo(user.getUserId());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_go_back_list) {
            //直接退出当前房间
            present.leaveRoom();
        }
    }

    @Override
    public void clickBroadcast(RCAllBroadcastMessage message) {
        mAllBroadcastView.showMessage(null);
        present.jumpRoom(message);
    }
}
