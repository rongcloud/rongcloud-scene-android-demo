package cn.rongcloud.radio.ui.room;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.basis.ui.UIStack;
import com.basis.utils.ImageLoader;
import com.basis.utils.UiUtils;
import com.basis.widget.dialog.VRCenterDialog;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.music.MusicMiniView;
import cn.rongcloud.radio.R;
import cn.rongcloud.radio.helper.RadioEventHelper;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import cn.rongcloud.roomkit.ui.room.AbsRoomActivity;
import cn.rongcloud.roomkit.ui.room.AbsRoomFragment;
import cn.rongcloud.roomkit.ui.room.RoomMessageAdapter;
import cn.rongcloud.roomkit.ui.room.dialog.ExitRoomPopupWindow;
import cn.rongcloud.roomkit.ui.room.dialog.RoomNoticeDialog;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.dialog.shield.ShieldDialog;
import cn.rongcloud.roomkit.ui.room.fragment.BackgroundSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.CreatorSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.MemberListFragment;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSettingFragment;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.widget.AllBroadcastView;
import cn.rongcloud.roomkit.ui.room.widget.GiftAnimationView;
import cn.rongcloud.roomkit.ui.room.widget.RecyclerViewAtVP2;
import cn.rongcloud.roomkit.ui.room.widget.RoomBottomView;
import cn.rongcloud.roomkit.ui.room.widget.RoomSeatView;
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar;
import cn.rongcloud.roomkit.widget.EditDialog;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import cn.rongcloud.roomkit.widget.decoration.DefaultItemDecoration;
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
    private RecyclerViewAtVP2 mMessageView;
    private View mCoverView;
    private AllBroadcastView mAllBroadcastView;
    private ConstraintLayout clVoiceRoomView;
    private RelativeLayout rlRoomFinishedId;
    private Button btnGoBackList;
    private MusicMiniView mMusicMiniView;

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
        mMessageView.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.dp2px(5)));
        mRoomMessageAdapter = new RoomMessageAdapter(getContext(), mMessageView, this, RoomType.RADIO_ROOM);
        mMessageView.setAdapter(mRoomMessageAdapter);

        // 音乐小窗口
        mMusicMiniView = getView(R.id.mmv_view);

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
        mMusicMiniView.setOnMusicClickListener(v -> {
            if (present.getRoomOwnerType() == RoomOwnerType.RADIO_OWNER) {
                showMusicDialog();
            }
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
//        if (messageContent != null) {
//            list.add(messageContent);
//        }
//        mRoomMessageAdapter.setMessages(list, isRefresh);
        if (isRefresh) {
            if (messageContent != null) {
                list.add(messageContent);
            }
            mRoomMessageAdapter.setMessages(list, true);
        } else {
            mRoomMessageAdapter.interMessage(messageContent);
        }
    }

    @Override
    public void addAllToMessageList(List<MessageContent> messageContents, boolean isRefresh) {
        mRoomMessageAdapter.setMessages(messageContents, isRefresh);
    }

    @Override
    public void finish() {
        //在销毁之前提前出栈顶
        try {
            if (null != mRoomMessageAdapter) mRoomMessageAdapter.release();
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
        mInputPasswordDialog = new InputPasswordDialog(getContext(), false, new InputPasswordDialog.OnClickListener() {
            @Override
            public void clickCancel() {

            }

            @Override
            public void clickConfirm(String password) {
                if (TextUtils.isEmpty(password)) {
                    return;
                }
                if (password.length() < 4) {
                    showToast(getString(R.string.text_please_input_four_number));
                    return;
                }
                mInputPasswordDialog.dismiss();
                present.setRoomPassword(true, password, item);
            }
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
                new EditDialog.OnClickEditDialog() {
                    @Override
                    public void clickCancel() {

                    }

                    @Override
                    public void clickConfirm(String text) {
                        if (TextUtils.isEmpty(text)) {
                            showToast("房间名称不能为空");
                            return;
                        }
                        present.setRoomName(text);
                        mEditDialog.dismiss();
                    }
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
        ImageLoader.loadUrl(mBackgroundImageView, url, R.color.black);
    }

    @Override
    public void showShieldDialog(String roomId) {
        mShieldDialog = new ShieldDialog(requireActivity(), roomId, 10, new ShieldDialog.OnShieldDialogListener() {
            @Override
            public void onAddShield(String s, List<Shield> shields) {
            }

            @Override
            public void onDeleteShield(Shield shield, List<Shield> shields) {

            }
        });
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
        MusicControlManager.getInstance().showDialog(getChildFragmentManager(), present.getRoomId());
    }

    @Override
    public void showRoomCloseDialog() {
        VRCenterDialog closeRoomDialog = new VRCenterDialog(requireActivity(), null);
        closeRoomDialog.setCancelable(false);
        closeRoomDialog.replaceContent(getString(R.string.text_room_end), null, null, getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.leaveRoom();
            }
        }, null);
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

    @Override
    public void refreshMusicView(boolean show, String name, String url) {
        if (show) {
            mMusicMiniView.show(name, url);
        } else {
            mMusicMiniView.dismiss();
        }
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
                VRCenterDialog dialog = new VRCenterDialog(requireActivity(), null);
                dialog.replaceContent(getString(R.string.text_confirm_close_room), getString(R.string.cancel), null,
                        getString(R.string.confirm), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 房主关闭房间
                                present.closeRoom();
                            }
                        }, null);

                dialog.show();
            }
        });
        mExitRoomPopupWindow.show(mBackgroundImageView);
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
//        mRoomMessageAdapter.release();
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
        SensorsUtil.instance().textClick(mRoomId, present.getRoomName(), RcEvent.RadioRoom);
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

    /**
     * TODO 麦克风被占用的前提下
     * 1、那么只要被占用，那么永远不能发送语音
     *
     * @return
     */
    @Override
    public boolean canSend() {
        return false;
    }

    @Override
    public void clickUser(User user) {
        if (TextUtils.equals(user.getUserId(), UserManager.get().getUserId())) {
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
