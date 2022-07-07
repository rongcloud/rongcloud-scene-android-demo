package cn.rongcloud.gameroom.ui.gameroom;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.BaseActivity;
import com.basis.ui.UIStack;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.UiUtils;
import com.basis.widget.decoration.SpaceItemDecoration;
import com.basis.widget.dialog.VRCenterDialog;
import com.basis.widget.loading.LoadTag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.DataShareManager;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.gamelib.api.RCGameEngine;
import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gamelib.model.RCGameOption;
import cn.rongcloud.gamelib.model.RCGameRoomInfo;
import cn.rongcloud.gamelib.model.RCGameSafeRect;
import cn.rongcloud.gamelib.model.RCGameSound;
import cn.rongcloud.gamelib.model.RCGameUI;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.GameRoomBean;
import cn.rongcloud.gameroom.model.SeatPlayer;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.musiccontrolkit.RCMusicControlEngine;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.message.RCChatroomBarrage;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.service.RTCNotificationService;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.RoomMessageAdapter;
import cn.rongcloud.roomkit.ui.room.dialog.ExitRoomPopupWindow;
import cn.rongcloud.roomkit.ui.room.dialog.RoomNoticeDialog;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.dialog.shield.ShieldDialog;
import cn.rongcloud.roomkit.ui.room.fragment.MemberListFragment;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockAllSeatFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteAllFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.EmptySeatFragment;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.ICommonDialog;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.roomkit.ui.room.widget.AllBroadcastView;
import cn.rongcloud.roomkit.ui.room.widget.GiftAnimationView;
import cn.rongcloud.roomkit.ui.room.widget.RoomBottomView;
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar;
import cn.rongcloud.roomkit.widget.EditDialog;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import cn.rongcloud.roomkit.widget.decoration.DefaultItemDecoration;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.utils.StatusBarUtil;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2022/5/5
 */
@Route(path = RouterPath.ROUTER_GAME_ROOM)
public class GameRoomActivity extends BaseActivity implements IGameRoomListener,
        View.OnClickListener, RoomTitleBar.OnFollowClickListener,
        MemberListFragment.OnClickUserListener, RoomMessageAdapter.OnClickMessageUserListener,
        ICommonDialog, RoomBottomView.OnBottomOptionClickListener {

    private String roomId;
    private boolean isCreate;
    private boolean isFastIn;
    private String fastInGameId;
    private ImageView ivBackground;
    private FrameLayout flGame;
    private GiftAnimationView giftView;
    private RoomTitleBar titleBar;
    private RoomBottomView roomBottomView;
    private LoadTag loadingView;
    private RecyclerView rvGameSeat;
    private RecyclerView rvGameMessage;
    private RelativeLayout rlRoomFinished;
    private Button btnGoBackList;
    private ConstraintLayout clRoom;
    private ExitRoomPopupWindow mExitRoomPopupWindow;
    private GameEventHelper mGameEventHelper;
    private MemberListFragment mMemberListFragment;
    private RoomMessageAdapter mRoomMessageAdapter;
    private MemberSettingFragment mMemberSettingFragment;
    private GameSeatAdapter mGameSeatAdapter;
    private SwitchGamePopupWindow switchGamePopupWindow;
    private GiftFragment mGiftFragment;
    private VRCenterDialog inviteDialog;
    private EmptySeatFragment emptySeatFragment;
    private SelfSettingDialog selfSettingDialog;
    private InvitePlayerDialog invitePlayerDialog;
    private RoomSettingFragment mRoomSettingFragment;
    private RoomNoticeDialog mNoticeDialog;
    private String mUid;
    private InputPasswordDialog mInputPasswordDialog;
    private EditDialog mEditDialog;
    private ShieldDialog mShieldDialog;
    private ImageButton mMusicMiniView;
    private AllBroadcastView mAllBroadcastView;
    // 站位footer避免音乐弹框出来时遮挡
    private View seatFootView;
    private ConstraintLayout mClMessage;
    private ImageButton mBtnCloseMessage;
    private ConstraintLayout mClMiniMessage;
    // private android.widget.TextView mTvMessage;
    private ImageButton mBtnOpenMessage;
    private FrameLayout mFlMiniMessageContainer;

    @Override
    public int setLayoutId() {
        return R.layout.activity_game_room;
    }

    @Override
    public void init() {
        // 忽略av call
        DataShareManager.get().setIgnoreIncomingCall(true);
        initView();
        if (getIntent() != null) {
            roomId = getIntent().getStringExtra(IntentWrap.KEY_ROOM_IDS);
            isCreate = getIntent().getBooleanExtra(IntentWrap.KEY_IS_CREATE, false);
            isFastIn = getIntent().getBooleanExtra(IntentWrap.KEY_IS_FAST_IN, false);
            fastInGameId = getIntent().getStringExtra(IntentWrap.KEY_GAME_ID);
        }
        mUid = UserManager.get().getUserId();
        mGameEventHelper = GameEventHelper.getInstance();
        mGameEventHelper.setGameRoomListener(this);
        mGameEventHelper.init(roomId, isCreate, isFastIn, fastInGameId);
        // 通知栏
        Intent intent = new Intent(this, RTCNotificationService.class);
        intent.putExtra(RTCNotificationService.ACTION, IntentWrap.getGameRoomAction(activity));
        this.startService(intent);
    }

    private void initView() {
        // 状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        getWrapBar().setHide(true).work();
        ivBackground = (ImageView) findViewById(R.id.iv_background);
        flGame = (FrameLayout) findViewById(R.id.fl_game);
        giftView = (GiftAnimationView) findViewById(R.id.gift_view);
        titleBar = (RoomTitleBar) findViewById(R.id.title_bar);
        roomBottomView = (RoomBottomView) findViewById(R.id.room_bottom_view);
        rvGameSeat = (RecyclerView) findViewById(R.id.rv_game_seat);
        rvGameMessage = (RecyclerView) findViewById(R.id.rv_game_message);
        rlRoomFinished = (RelativeLayout) findViewById(R.id.rl_room_finished);
        btnGoBackList = (Button) findViewById(R.id.btn_go_back_list);
        clRoom = (ConstraintLayout) findViewById(R.id.cl_room);
        mMusicMiniView = findViewById(R.id.mmv_view);
        mClMessage = (ConstraintLayout) findViewById(R.id.cl_message);
        mBtnCloseMessage = (ImageButton) findViewById(R.id.btn_close_message);
        mClMiniMessage = (ConstraintLayout) findViewById(R.id.cl_mini_message);
        // mTvMessage = (TextView) findViewById(R.id.tv_message);
        mFlMiniMessageContainer = (FrameLayout) findViewById(R.id.fl_mini_message_container);
        mBtnOpenMessage = (ImageButton) findViewById(R.id.btn_open_message);
        // 全局广播View
        mAllBroadcastView = getView(R.id.view_all_broadcast);
        mAllBroadcastView.setOnClickBroadcast(null);
        mAllBroadcastView.setSupportJump(false);

        btnGoBackList.setOnClickListener(this);
        mRoomMessageAdapter = new RoomMessageAdapter(this, rvGameMessage, this, RoomType.GAME_ROOM);
        rvGameMessage.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.dp2px(5)));
        rvGameMessage.setAdapter(mRoomMessageAdapter);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) clRoom.getLayoutParams();
        layoutParams.topMargin = StatusBarUtil.getStatusBarHeight(this);
        clRoom.setLayoutParams(layoutParams);

        mGameSeatAdapter = new GameSeatAdapter();
        rvGameSeat.setAdapter(mGameSeatAdapter);
        rvGameSeat.getItemAnimator().setMoveDuration(0);
        rvGameSeat.setHasFixedSize(true);
        ((DefaultItemAnimator) (rvGameSeat.getItemAnimator())).setSupportsChangeAnimations(false);
        int space = UiUtils.dp2px(5);
        rvGameSeat.addItemDecoration(SpaceItemDecoration.Builder.start().setStartSpace(space).setEndSpace(space).build());
        seatFootView = new View(this);
        seatFootView.setMinimumWidth(UiUtils.dp2px(40));

        mGameSeatAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                mGameEventHelper.clickSeat(mGameSeatAdapter.getItem(position));
            }
        });

        titleBar.setOnSwitchClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                switchGamePopupWindow = new SwitchGamePopupWindow(GameRoomActivity.this, new SwitchGamePopupWindow.OnGameClickListener() {
                    @Override
                    public void switchGame(RCGameInfo gameInfo) {
                        mGameEventHelper.switchGame(gameInfo, true);
                    }
                });
                switchGamePopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
                switchGamePopupWindow.showAtLocation(ivBackground, Gravity.TOP, 0, 0);
            }
        });
        titleBar.setOnNoticeClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                showNoticeDialog(false);
            }
        });

        mMusicMiniView.setOnClickListener(v -> {
            if (mGameEventHelper.getRoomOwnerType() == RoomOwnerType.GAME_OWNER) {
                showMusicDialog();
            }
        });

        RCMusicControlEngine.getInstance().playingObserve().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshMusicView(aBoolean);
            }
        });

        mBtnOpenMessage.setOnClickListener(v -> {
            showAllMessage(true);
        });

        mBtnCloseMessage.setOnClickListener(v -> {
            showAllMessage(false);
        });
    }

    private void showAllMessage(boolean isShow) {
        if (isShow) {
            mClMessage.animate().translationY(0).start();
        } else {
            mClMessage.animate().translationY(UiUtils.dp2px(300)).start();
        }
    }

    private void showMusicDialog() {
        MusicControlManager.getInstance().showDialog(getSupportFragmentManager(), roomId);
    }

    @Override
    public void onLoadRoomDetail(GameRoomBean gameRoomBean) {
        if (gameRoomBean.getGameInfo() != null) {
            titleBar.setSwitchGameName(gameRoomBean.getGameInfo().getGameName());
            ImageLoader.loadUrl(ivBackground, gameRoomBean.getGameInfo().getLoadingPic(), R.color.black);
        }
        titleBar.setData(mGameEventHelper.getRoomOwnerType(), gameRoomBean.getRoomName(), gameRoomBean.getId(), gameRoomBean.getCreateUserId(), this::clickFollow);
        titleBar.setOnMenuClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                clickMenu();
            }
        });
        titleBar.setOnMemberClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                mMemberListFragment = new MemberListFragment(roomId, GameRoomActivity.this::clickUser);
                mMemberListFragment.show(getSupportFragmentManager());
            }
        });

        //监听房间里面的人
        MemberCache.getInstance().getMemberList().observe(this, users -> {
            // 房间人数
            titleBar.setOnlineNum(users.size());
        });
        MemberCache.getInstance().getAdminList().observe(this, strings -> {
            refreshMessageList();
            // 房主或管理可以切换游戏
            boolean canSwitchGame = mGameEventHelper.getRoomOwnerType() == RoomOwnerType.GAME_OWNER || MemberCache.getInstance().isAdmin(mUid);
            titleBar.setSwitchGameVisible(canSwitchGame);
            refreshInviteButton();
            mGameEventHelper.refreshSeatPlayers();
            mGameEventHelper.checkChangeCaptain();
        });
        roomBottomView.setData(mGameEventHelper.getRoomOwnerType(), this, roomId);

        mRoomMessageAdapter.setRoomCreateId(gameRoomBean.getCreateUserId());

    }

    private void refreshMessageList() {
        mRoomMessageAdapter.notifyDataSetChanged();
        refreshMiniMessage();
    }

    @Override
    public void onLoadGame(String roomId, String gameId, String userId, String appCode) {
        int statusHeight = UiUtils.px2dp(StatusBarUtil.getStatusBarHeight(this));
        // 配置游戏
        RCGameOption option = RCGameOption.builder()
                // 游戏内可操作按钮的四周边距，单位dp
                .setGameSafeRect(new RCGameSafeRect(0, 120 + statusHeight, 0, 150))// 空出顶部麦位和底部消息区域
                // 设置游戏内音乐开关和音量
                .setGameSound(new RCGameSound(RCGameSound.SoundControl.OPEN, 100))
                // 设置游戏内UI布局的展示
                .setGameUI(RCGameUI.builder()
                        // 隐藏版本号
                        .setVersionHide(true)
                        .setPingHide(true)
                        .setLevelHide(true)
                        .build())
                .build();
        RCGameRoomInfo gameRoomInfo = new RCGameRoomInfo();
        gameRoomInfo.setGameId(gameId);
        gameRoomInfo.setRoomId(roomId);
        gameRoomInfo.setUserId(userId);
        gameRoomInfo.setAppCode(appCode);
        // 加载游戏
        RCGameEngine.getInstance().loadGame(this, flGame, gameRoomInfo, option);
        roomBottomView.setMicSwitch(!RCVoiceRoomEngine.getInstance().isDisableAudioRecording());
    }

    @Override
    public void showLoading(String msg) {
        if (loadingView == null) {
            loadingView = new LoadTag(this);
        }
        loadingView.show(msg);
    }

    @Override
    public void dismissLoading() {
        if (loadingView != null) loadingView.dismiss();
    }

    @Override
    public void showRoomFinished() {
        rlRoomFinished.setVisibility(View.VISIBLE);
        clRoom.setVisibility(View.GONE);
    }

    @Override
    public void showMemberSetting(Member member, SeatPlayer seatPlayer, String roomCreatorId) {
        if (mMemberSettingFragment == null) {
            mMemberSettingFragment = new MemberSettingFragment(mGameEventHelper.getRoomOwnerType(), mGameEventHelper);
        }
        if (seatPlayer != null) {
            //说明当前用户在麦位上
            mMemberSettingFragment.setMemberIsOnSeat(seatPlayer.seatIndex > -1);
            mMemberSettingFragment.setSeatPosition(seatPlayer.seatIndex);
            mMemberSettingFragment.setMute(seatPlayer.isMute);
        } else {
            mMemberSettingFragment.setMemberIsOnSeat(false);
        }
        mMemberSettingFragment.setSelfAndMemberInfo(mGameEventHelper.isInSeat(mUid), mGameEventHelper.isCaptain(mUid), mGameEventHelper.isInGame(member.getUserId()), mGameEventHelper.isInGaming());
        mMemberSettingFragment.show(getSupportFragmentManager(), member, roomCreatorId);
    }

    @Override
    public void onSeatPlayerChanged(List<SeatPlayer> seatPlayerList) {
        // mGameSeatAdapter.setDiffNewData(seatPlayerList);
        mGameSeatAdapter.setNewInstance(seatPlayerList);
        refreshInviteButton();
    }

    @Override
    public void showMessage(MessageContent messageContent, boolean isRefresh) {
        List<MessageContent> list = new ArrayList<>(1);
        if (messageContent != null) {
            list.add(messageContent);
        }
        showMessageList(list, isRefresh);

    }

    @Override
    public void showMessageList(List<MessageContent> messageList, boolean isRefresh) {
        mRoomMessageAdapter.setMessages(messageList, isRefresh);
        refreshMiniMessage();
    }

    private void refreshMiniMessage() {
        View view = mRoomMessageAdapter.getLastMessageView();
        if (view != null) {
            mFlMiniMessageContainer.removeAllViews();
            mFlMiniMessageContainer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members) {
        mGiftFragment = new GiftFragment(voiceRoomBean, selectUserId, mGameEventHelper);
        mGiftFragment.setNormalSeatIndex(true);
        mGiftFragment.refreshMember(members);
        mGiftFragment.show(getSupportFragmentManager());
    }

    @Override
    public void finishRoom() {
        finish();
    }

    @Override
    public void setTitleFollow(boolean isFollow) {
        titleBar.setFollow(isFollow);
    }

    @Override
    public void showPickReceivedDialog(boolean isCreate, String userId) {
        if (inviteDialog != null && inviteDialog.isShowing()) {
            inviteDialog.dismiss();
        }
        String pickName = isCreate ? "房主" : "管理员";
        inviteDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
        inviteDialog.replaceContent("您被" + pickName + "邀请上麦，是否同意?", "拒绝", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝
                inviteDialog.dismiss();
                mGameEventHelper.responseInviteEnterSeat(false, userId);
            }
        }, "同意", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialog.dismiss();
                mGameEventHelper.responseInviteEnterSeat(true, userId);
            }
        }, null);
        inviteDialog.show();
    }

    @Override
    public void showInviteJoinGameDialog() {
        if (inviteDialog != null && inviteDialog.isShowing()) {
            inviteDialog.dismiss();
        }
        inviteDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
        inviteDialog.replaceContent("队长邀请你加入游戏，是否同意?", "拒绝", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝
                inviteDialog.dismiss();
                mGameEventHelper.responseInviteJoinGame(false);
            }
        }, "同意", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialog.dismiss();
                mGameEventHelper.responseInviteJoinGame(true);
            }
        }, null);
        inviteDialog.show();
    }

    @Override
    public void onRoomClosed() {
        //当前房间被关闭
        VRCenterDialog confirmDialog = new VRCenterDialog(this, null);
        confirmDialog.replaceContent("房主已关闭房间", "", null, "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameEventHelper.leaveRoom();
            }
        }, null);
        confirmDialog.setCancelable(false);
        confirmDialog.show();
    }

    @Override
    public void onNetworkStatus(int delay, boolean isShow) {
        titleBar.post(new Runnable() {
            @Override
            public void run() {
                titleBar.setDelay(delay, true);
            }
        });
    }

    @Override
    public void showEmptySeatDialog(SeatPlayer seatPlayer) {
        emptySeatFragment = new EmptySeatFragment();
        int seatStatus = seatPlayer.isLock ? 1 : 0;
        emptySeatFragment.setData(seatPlayer.seatIndex, seatStatus, seatPlayer.isMute, this);
        emptySeatFragment.setSeatActionClickListener(mGameEventHelper);
        emptySeatFragment.setNormalSeatIndex(true);
        emptySeatFragment.show(getSupportFragmentManager());
    }

    @Override
    public void showSelfSettingDialog(SeatPlayer seatPlayer) {
        selfSettingDialog = new SelfSettingDialog(seatPlayer, mGameEventHelper.getRoomOwnerType() == RoomOwnerType.GAME_OWNER);
        selfSettingDialog.show(getSupportFragmentManager());
    }

    @Override
    public void showInvitePlayerDialog() {
        boolean isAdmin = mGameEventHelper.getRoomOwnerType() == RoomOwnerType.GAME_OWNER || MemberCache.getInstance().isAdmin(mUid);
        invitePlayerDialog = new InvitePlayerDialog(isAdmin, mGameEventHelper.isCaptain(mUid));
        invitePlayerDialog.show(getSupportFragmentManager());
    }

    @Override
    public void setRoomName(String name) {
        titleBar.setRoomName(name);
    }

    @Override
    public void refreshMusicView(boolean show) {
        if (show) {
            mGameSeatAdapter.setFooterView(seatFootView, 0, LinearLayout.HORIZONTAL);
        } else {
            mGameSeatAdapter.removeFooterView(seatFootView);
        }
        mMusicMiniView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onGameChanged(RCGameInfo gameInfo) {
        titleBar.setSwitchGameName(gameInfo.getGameName());
        ImageLoader.loadUrl(ivBackground, gameInfo.getLoadingPic(), R.color.black);
    }

    @Override
    public void onGameStarted() {
        showAllMessage(false);
        roomBottomView.closeInput();
    }

    @Override
    public void onInSeatChanged(boolean isEnter) {
        roomBottomView.setMicSwitchVisible(isEnter);
        roomBottomView.setMicSwitch(!RCVoiceRoomEngine.getInstance().isDisableAudioRecording());
    }

    public void refreshInviteButton() {
        boolean show = mGameEventHelper.isCaptain(mUid) || MemberCache.getInstance().isAdmin(mUid) || mGameEventHelper.getRoomOwnerType() == RoomOwnerType.GAME_OWNER;
        roomBottomView.setInviteVisible(show);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_go_back_list) {
            finishRoom();
        }
    }

    @Override
    public void onBackPressed() {
        clickMenu();
    }

    @Override
    protected void onDestroy() {
        // 取消忽略av call
        DataShareManager.get().setIgnoreIncomingCall(false);
        stopService(new Intent(this, RTCNotificationService.class));
        mGameEventHelper.unInit();
        super.onDestroy();
    }

    /**
     * 点击右上角菜单按钮
     */
    private void clickMenu() {
        if (mGameEventHelper.getRoomOwnerType() == null) {
            finish();
            return;
        }

        mExitRoomPopupWindow = new ExitRoomPopupWindow(this, mGameEventHelper.getRoomOwnerType(), new ExitRoomPopupWindow.OnOptionClick() {
            @Override
            public void clickPackRoom() {

            }

            @Override
            public void clickLeaveRoom() {
                // 观众离开房间
                mGameEventHelper.leaveRoom();
            }

            @Override
            public void clickCloseRoom() {
                // 房主关闭房间
                mGameEventHelper.closeRoom();
            }
        });
        // mExitRoomPopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        mExitRoomPopupWindow.show(ivBackground);
    }

    @Override
    public void clickFollow(boolean isFollow, RCFollowMsg followMsg) {
        if (isFollow) {
            mGameEventHelper.sendMessage(followMsg);
        }
        titleBar.setFollow(isFollow);
    }

    @Override
    public void clickUser(User user) {
        //如果点击的是本人的名称，那么无效
        if (TextUtils.equals(user.getUserId(), mUid)) {
            return;
        }
        mGameEventHelper.preShowMemberSetting(user.getUserId());
    }

    @Override
    public void clickMessageUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        clickUser(user);
    }

    @Override
    public void showSeatOperationViewPagerFragment(int index, int seatIndex) {
        showInvitePlayerDialog();
    }

    @Override
    public void showRevokeSeatRequestFragment() {

    }

    @Override
    public void clickSendMessage(String message) {
        //发送文字消息
        RCChatroomBarrage barrage = new RCChatroomBarrage();
        barrage.setContent(message);
        barrage.setUserId(UserManager.get().getUserId());
        barrage.setUserName(UserManager.get().getUserName());
        mGameEventHelper.sendMessage(barrage);
    }

    @Override
    public void clickPrivateMessage() {
        RouteUtils.routeToSubConversationListActivity(
                this,
                Conversation.ConversationType.PRIVATE,
                "消息"
        );
    }

    @Override
    public void clickSeatOrder() {
        showInvitePlayerDialog();
    }

    @Override
    public void clickSettings() {
        mRoomSettingFragment = new RoomSettingFragment(new cn.rongcloud.roomkit.ui.OnItemClickListener<MutableLiveData<IFun.BaseFun>>() {
            @Override
            public void clickItem(MutableLiveData<IFun.BaseFun> item, int position) {
                clickSettingItem(item, position);
            }
        });
        mRoomSettingFragment.show(getSupportFragmentManager(), mGameEventHelper.getSettingFunction());
    }

    private void clickSettingItem(MutableLiveData<IFun.BaseFun> item, int position) {
        IFun.BaseFun fun = item.getValue();
        if (fun instanceof RoomNoticeFun) {
            showNoticeDialog(true);
        } else if (fun instanceof RoomLockFun) {
            if (fun.getStatus() == 1) {
                mGameEventHelper.setRoomPassword(false, "", item);
            } else {
                showSetPasswordDialog(item);
            }
        } else if (fun instanceof RoomNameFun) {
            showSetRoomNameDialog();
        } else if (fun instanceof RoomShieldFun) {
            showShieldDialog();
        } else if (fun instanceof RoomMuteAllFun) {
            mGameEventHelper.setAllSeatLock(fun.getStatus() != 1);
        } else if (fun instanceof RoomLockAllSeatFun) {
            mGameEventHelper.lockOtherSeats(fun.getStatus() != 1);
        } else if (fun instanceof RoomMuteFun) {
            mGameEventHelper.muteAllRemoteStreams(fun.getStatus() != 1);
        } else if (fun instanceof RoomMusicFun) {
            //音乐 判断房主是否在麦位上
            boolean isInSeat = mGameEventHelper.isInSeat(mUid);
            if (isInSeat) {
                //在座位上，可以播放音乐
                showMusicDialog();
            } else {
                KToast.show("请先上麦之后再播放音乐");
            }
        }
    }

    @Override
    public void clickPk() {

    }

    @Override
    public void clickRequestSeat() {

    }

    @Override
    public void onSendGift() {
        mGameEventHelper.sendGift();
    }

    @Override
    public void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice) {
        mGameEventHelper.sendMessage(rcChatroomVoice);
    }

    /**
     * 1、如果不在麦位上，不可以发送
     * 2、如果在麦位上,但是没有被禁麦，也不可以发送
     *
     * @return
     */
    @Override
    public boolean canSend() {
        //不在麦位上，或者在麦位上但是没有被禁麦
        SeatPlayer seatPlayer = mGameEventHelper.getSeatPlayer(mUid);
        if (seatPlayer == null || (seatPlayer != null && !seatPlayer.isMute)) {
            return false;
        }
        return true;
    }

    @Override
    public void clickMessageView() {
        showAllMessage(true);
    }

    /**
     * 公告弹框
     *
     * @param isEdit
     */
    public void showNoticeDialog(boolean isEdit) {
        mNoticeDialog = new RoomNoticeDialog(this);
        mNoticeDialog.show(mGameEventHelper.getNotice(), isEdit, new RoomNoticeDialog.OnSaveNoticeListener() {
            @Override
            public void saveNotice(String notice) {
                //修改公告信息
                mGameEventHelper.modifyNotice(notice);
            }
        });
    }

    /**
     * 设置密码弹框
     *
     * @param item
     */
    public void showSetPasswordDialog(MutableLiveData<IFun.BaseFun> item) {
        mInputPasswordDialog = new InputPasswordDialog(this, false, new InputPasswordDialog.OnClickListener() {
            @Override
            public void clickCancel() {
                mInputPasswordDialog.dismiss();
            }

            @Override
            public void clickConfirm(String password) {
                if (TextUtils.isEmpty(password)) {
                    return;
                }
                if (password.length() < 4) {
                    KToast.show(getString(R.string.text_please_input_four_number));
                    return;
                }
                mInputPasswordDialog.dismiss();
                mGameEventHelper.setRoomPassword(true, password, item);
            }
        });
        mInputPasswordDialog.show();
    }

    /**
     * 修改房间名称
     */
    public void showSetRoomNameDialog() {
        mEditDialog = new EditDialog(this, "修改房间标题",
                "请输入房间名",
                mGameEventHelper.getRoomName(),
                10,
                false,
                new EditDialog.OnClickEditDialog() {
                    @Override
                    public void clickCancel() {
                        mEditDialog.dismiss();
                    }

                    @Override
                    public void clickConfirm(String newName) {
                        if (TextUtils.isEmpty(newName)) {
                            KToast.show("房间名称不能为空");
                            return;
                        }
                        mGameEventHelper.setRoomName(newName);
                        mEditDialog.dismiss();
                    }
                });
        mEditDialog.show();
    }

    /**
     * 设置屏蔽词
     */
    public void showShieldDialog() {
        mShieldDialog = new ShieldDialog(this, roomId, 10, new ShieldDialog.OnShieldDialogListener() {
            @Override
            public void onAddShield(String shield, List<Shield> shields) {
                mGameEventHelper.getShields();
                mGameEventHelper.notifyRoom(GameConstant.EVENT_ADD_SHIELD, shield);
            }

            @Override
            public void onDeleteShield(Shield shield, List<Shield> shields) {
                mGameEventHelper.getShields();
                mGameEventHelper.notifyRoom(GameConstant.EVENT_DELETE_SHIELD, shield.getName());
            }
        });
        mShieldDialog.show();
    }

    @Override
    public void clickSwitchMic() {
        mGameEventHelper.changeRecord();
    }

    @Override
    public void onMicSwitched(boolean isOn) {
        roomBottomView.setMicSwitch(isOn);
    }

    @Override
    public void onSpeakingChanged(int seatIndex, boolean isSpeaking) {
        mGameSeatAdapter.refreshSpeaking(seatIndex, isSpeaking);
    }
}
