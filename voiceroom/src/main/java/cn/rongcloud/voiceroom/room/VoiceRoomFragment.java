package cn.rongcloud.voiceroom.room;

import static cn.rongcloud.voiceroom.room.VoiceRoomPresenter.STATUS_NOT_ON_SEAT;
import static cn.rongcloud.voiceroom.room.VoiceRoomPresenter.STATUS_ON_SEAT;
import static cn.rongcloud.voiceroom.room.VoiceRoomPresenter.STATUS_WAIT_FOR_SEAT;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.UIStack;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.wapper.IResultBack;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.ImageLoaderUtil;
import com.rongcloud.common.utils.UiUtils;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.rong.combusis.common.ui.dialog.EditDialog;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
import cn.rong.combusis.intent.IntentWrap;
import cn.rong.combusis.manager.RCChatRoomMessageManager;
import cn.rong.combusis.message.RCAllBroadcastMessage;
import cn.rong.combusis.message.RCChatroomBarrage;
import cn.rong.combusis.message.RCChatroomLike;
import cn.rong.combusis.message.RCChatroomVoice;
import cn.rong.combusis.music.MusicDialog;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.user.UserProvider;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.sdk.event.EventHelper;
import cn.rong.combusis.ui.room.AbsRoomActivity;
import cn.rong.combusis.ui.room.AbsRoomFragment;
import cn.rong.combusis.ui.room.RoomMessageAdapter;
import cn.rong.combusis.ui.room.dialog.ExitRoomPopupWindow;
import cn.rong.combusis.ui.room.dialog.RoomNoticeDialog;
import cn.rong.combusis.ui.room.dialog.shield.ShieldDialog;
import cn.rong.combusis.ui.room.fragment.BackgroundSettingFragment;
import cn.rong.combusis.ui.room.fragment.MemberListFragment;
import cn.rong.combusis.ui.room.fragment.MemberSettingFragment;
import cn.rong.combusis.ui.room.fragment.gift.GiftFragment;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSettingFragment;
import cn.rong.combusis.ui.room.model.Member;
import cn.rong.combusis.ui.room.widget.AllBroadcastView;
import cn.rong.combusis.ui.room.widget.GiftAnimationView;
import cn.rong.combusis.ui.room.widget.RecyclerViewAtVP2;
import cn.rong.combusis.ui.room.widget.RoomBottomView;
import cn.rong.combusis.ui.room.widget.RoomSeatView;
import cn.rong.combusis.ui.room.widget.RoomTitleBar;
import cn.rong.combusis.widget.miniroom.MiniRoomManager;
import cn.rongcloud.voiceroom.R;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import cn.rongcloud.voiceroom.pk.widget.PKView;
import cn.rongcloud.voiceroom.room.adapter.VoiceRoomSeatsAdapter;
import cn.rongcloud.voiceroom.ui.uimodel.UiSeatModel;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imkit.utils.StatusBarUtil;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author 李浩
 * @date 2021/9/24
 */
public class VoiceRoomFragment extends AbsRoomFragment<VoiceRoomPresenter>
        implements IVoiceRoomFragmentView, RoomMessageAdapter.OnClickMessageUserListener,
        RoomBottomView.OnBottomOptionClickListener, MemberListFragment.OnClickUserListener
        , View.OnClickListener, AllBroadcastView.OnClickBroadcast {
    private ImageView mBackgroundImageView;
    private GiftAnimationView mGiftAnimationView;
    private RoomTitleBar mRoomTitleBar;
    private TextView mNoticeView;
    private RoomSeatView mRoomSeatView;
    private RoomBottomView mRoomBottomView;
    private RecyclerViewAtVP2 mMessageView;
    private RoomMessageAdapter mRoomMessageAdapter;
    private AllBroadcastView mAllBroadcastView;
    private RecyclerView rv_seat_list;
    private VoiceRoomSeatsAdapter voiceRoomSeatsAdapter;
    private MemberSettingFragment mMemberSettingFragment;
    private ExitRoomPopupWindow mExitRoomPopupWindow;
    private RoomNoticeDialog mNoticeDialog;
    private MemberListFragment mMemberListFragment;
    private RoomSettingFragment mRoomSettingFragment;
    private InputPasswordDialog mInputPasswordDialog;
    private EditDialog mEditDialog;
    private ShieldDialog mShieldDialog;
    private BackgroundSettingFragment mBackgroundSettingFragment;
    private GiftFragment mGiftFragment;
    private MusicDialog mMusicDialog;
    private String mRoomId;
    private boolean isCreate;

    private ConstraintLayout clVoiceRoomView;
    private RelativeLayout rlRoomFinishedId;
    private Button btnGoBackList;
    private PKView pkView;
    private View voiceRoom;

    public static Fragment getInstance(String roomId, boolean isCreate) {
        Bundle bundle = new Bundle();
        bundle.putString(ROOM_ID, roomId);
        bundle.putBoolean(IntentWrap.KEY_IS_CREATE, isCreate);
        Fragment fragment = new VoiceRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_new_voice_room;
    }

    @Override
    public void init() {
        mRoomId = getArguments().getString(ROOM_ID);
        isCreate = getArguments().getBoolean(IntentWrap.KEY_IS_CREATE);
        clVoiceRoomView = (ConstraintLayout) getView().findViewById(R.id.cl_voice_room_view);

        // 双击点赞的view
        mGiftAnimationView = getView().findViewById(R.id.gift_view);
        mGiftAnimationView.setOnBottomOptionClickListener(new GiftAnimationView.OnClickBackgroundListener() {
            @Override
            public void onSendLikeMessage(RCChatroomLike rcChatroomLike) {
                present.sendMessage(rcChatroomLike);
            }

            @Override
            public void onSingleTap() {
                mRoomBottomView.hideSoftKeyboardAndInput();
            }
        });

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) clVoiceRoomView.getLayoutParams();
        layoutParams.topMargin = StatusBarUtil.getStatusBarHeight(requireContext());
        clVoiceRoomView.setLayoutParams(layoutParams);

        rlRoomFinishedId = (RelativeLayout) getView().findViewById(R.id.rl_room_finished_id);
        btnGoBackList = (Button) getView().findViewById(R.id.btn_go_back_list);

        mRoomSettingFragment = new RoomSettingFragment(present);
        // 全局广播View
        mAllBroadcastView = getView(R.id.view_all_broadcast);
        mAllBroadcastView.setOnClickBroadcast(this::clickBroadcast);

        // 头部
        mRoomTitleBar = getView(R.id.room_title_bar);
        mRoomTitleBar.setOnMemberClickListener().subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Throwable {
                //添加防抖动
                mMemberListFragment = new MemberListFragment(present.getRoomId(), VoiceRoomFragment.this);
                mMemberListFragment.show(getChildFragmentManager());
            }
        });
        //麦位
        rv_seat_list = getView(R.id.rv_seat_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        rv_seat_list.setLayoutManager(gridLayoutManager);
        voiceRoomSeatsAdapter = new VoiceRoomSeatsAdapter(getActivity(), new VoiceRoomSeatsAdapter.OnClickVoiceRoomSeatsListener() {
            @Override
            public void clickVoiceRoomSeats(UiSeatModel uiSeatModel, int position) {
                onClickVoiceRoomSeats(uiSeatModel, position);
            }
        });
        voiceRoomSeatsAdapter.setHasStableIds(true);
        rv_seat_list.setAdapter(voiceRoomSeatsAdapter);
        mRoomTitleBar.setOnMenuClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                clickMenu();
            }
        });

        mNoticeView = getView(R.id.tv_notice);
        mNoticeView.setOnClickListener(v -> {
            showNoticeDialog(false);
        });
        // 背景
        mBackgroundImageView = getView(R.id.iv_background);
        VoiceRoomProvider.provider().getAsyn(mRoomId, new IResultBack<VoiceRoomBean>() {
            @Override
            public void onResult(VoiceRoomBean voiceRoomBean) {
                setRoomBackground(voiceRoomBean.getBackgroundUrl());
            }
        });
        // 房主座位
        mRoomSeatView = getView(R.id.room_seat_view);

        mRoomSeatView.setRoomOwnerHeadOnclickListener(v -> {
            //弹出
            if (present.getRoomOwnerType() == RoomOwnerType.VOICE_OWNER) {
                //如果是房间所有者 ,如果在麦位上,那么弹出下麦和关闭麦克风弹窗，如果不在麦位上，那么直接上麦
                present.onClickRoomOwnerView(getChildFragmentManager());
            }
        });
        // 底部操作按钮和双击送礼物
        mRoomBottomView = getView(R.id.room_bottom_view);
        // 弹幕消息列表
        mMessageView = getView(R.id.rv_message);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mMessageView.setLayoutManager(linearLayoutManager);
        mMessageView.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.INSTANCE.dp2Px(getContext(), 5)));
        mRoomMessageAdapter = new RoomMessageAdapter(getContext(), this);
        mMessageView.setAdapter(mRoomMessageAdapter);

        pkView = getView(R.id.pk_view);
        voiceRoom = getView(R.id.voice_room);

        if (null == mNoticeDialog) {
            mNoticeDialog = new RoomNoticeDialog(activity);
        }
    }

    @Override
    public void onSoftKeyboardChange(int height) {
        mRoomBottomView.setPadding(0, 0, 0, height);
    }

    /**
     * 显示公告弹窗
     *
     * @param isEdit
     */
    @Override
    public void showNoticeDialog(boolean isEdit) {

        mNoticeDialog.show(present.getNotice(), isEdit, new RoomNoticeDialog.OnSaveNoticeListener() {
            @Override
            public void saveNotice(String notice) {
                //修改公告信息
                present.modifyNotice(notice);
            }
        });
    }

    /**
     * 点击右上角菜单按钮
     */
    private void clickMenu() {
        if (present.getRoomOwnerType() == null) {
            finish();
            return;
        }
        // pk中房主才提示
//        if (StateUtil.isPking() && TextUtils.equals(AccountStore.INSTANCE.getUserId(), present.getCreateUserId())) {
//            KToast.show("当前PK中，无法进行该操作");
//            return;
//        }
        mExitRoomPopupWindow = new ExitRoomPopupWindow(getContext(), present.getRoomOwnerType(), new ExitRoomPopupWindow.OnOptionClick() {
            @Override
            public void clickPackRoom() {
                //最小化窗口,判断是否有权限
                if (checkDrawOverlaysPermission(false)) {

                    requireActivity().finish();

                    //缩放动画,并且显示悬浮窗，在这里要做悬浮窗判断
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    MiniRoomManager.getInstance().show(requireContext(), present.getRoomId(), present.getThemePictureUrl(), requireActivity().getIntent(), EventHelper.helper());
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
                // 房主关闭房间
                present.closeRoom();
            }
        });
        mExitRoomPopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
        mExitRoomPopupWindow.showAtLocation(mBackgroundImageView, Gravity.TOP, 0, 0);
    }

    /**
     * 麦位被点击的情况
     *
     * @param seatModel
     * @param position
     */
    private void onClickVoiceRoomSeats(UiSeatModel seatModel, int position) {
        switch (present.getRoomOwnerType()) {
            case VOICE_OWNER://房主
                onClickVoiceRoomSeatsByOwner(seatModel, position);
                break;
            case VOICE_VIEWER://观众
                onClickVoiceRoomSeatsByViewer(seatModel, position);
                break;
        }

    }

    /**
     * 观众点击麦位的时候
     *
     * @param seatModel
     * @param position
     */
    private void onClickVoiceRoomSeatsByViewer(UiSeatModel seatModel, int position) {
        if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            // 点击锁定座位
            showToast("该座位已锁定");
        } else if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
            //点击空座位 的时候
            present.enterSeatViewer(position);
        } else if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            if (!TextUtils.isEmpty(seatModel.getUserId()) && seatModel.getUserId().equals(AccountStore.INSTANCE.getUserId())) {
                // 点击自己头像
                present.showNewSelfSettingFragment(seatModel).show(getChildFragmentManager());
            } else {
                // 点击别人头像
                present.getUserInfo(seatModel.getUserId());
            }
        }
    }

    /**
     * 房主点击麦位的时候
     *
     * @param seatModel
     * @param position
     */
    private void onClickVoiceRoomSeatsByOwner(UiSeatModel seatModel, int position) {
        if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty
                || seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            //点击空座位或者锁定座位的时候，弹出弹窗
            present.enterSeatOwner(seatModel, position);
        } else if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            //弹窗设置弹窗
            // 点击别人头像
            present.getUserInfo(seatModel.getUserId());
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        btnGoBackList.setOnClickListener(this::onClick);
    }

    @Override
    public void joinRoom() {
        present.init(mRoomId, isCreate);
        mAllBroadcastView.setBroadcastListener();
    }

    private void initPk() {
//        PKStateManager.get().init(mRoomId, pkView, new IPKState.VRStateListener() {
//            @Override
//            public void onPkStart() {
//                PKStateManager.get().enterPkWithAnimation(voiceRoom, pkView, 200);
//            }
//
//            @Override
//            public void onPkStop() {
//                PKStateManager.get().quitPkWithAnimation(pkView, voiceRoom, 200);
//            }
//
//            @Override
//            public void onPkState() {
//                mRoomBottomView.refreshPkState();
//            }
//
//            @Override
//            public void onSendPKMessage(String content) {
//                present.sendMessage(TextMessage.obtain(content));
//            }
//        });
    }

    private void unInitPk() {
//        PKStateManager.get().unInit();
    }

    /**
     * pk禁止操作提示
     */
    private boolean checkPKState() {
//        boolean isPK = StateUtil.isPking();
//        if (isPK) {
//            KToast.show("当前PK中，无法进行该操作");
//        }
//        Logger.e(TAG, "isPk = " + isPK);
//        return isPK;
        return false;
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
    public VoiceRoomPresenter createPresent() {
        return new VoiceRoomPresenter(this, getLifecycle());
    }

    /**
     * 显示单条消息
     *
     * @param messageContent
     * @param isRefresh
     */
    @Override
    public void showMessage(MessageContent messageContent, boolean isRefresh) {
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
    public void refreshMessageList() {
        mRoomMessageAdapter.notifyDataSetChanged();
    }

    /**
     * 显示消息集合
     *
     * @param messageContentList
     * @param isRefresh
     */
    @Override
    public void showMessageList(List<MessageContent> messageContentList, boolean isRefresh) {
        mRoomMessageAdapter.setData(messageContentList, isRefresh);
        int count = mRoomMessageAdapter.getItemCount();
        if (count > 0) {
            mMessageView.smoothScrollToPosition(count - 1);
        }
    }

    @Override
    public void showSettingDialog(List<MutableLiveData<IFun.BaseFun>> funList) {
        mRoomSettingFragment.show(getChildFragmentManager(), funList);
    }

    /**
     * 设置房间数据
     *
     * @param voiceRoomBean
     */
    @Override
    public void setRoomData(VoiceRoomBean voiceRoomBean) {
        clVoiceRoomView.setVisibility(View.VISIBLE);
        rlRoomFinishedId.setVisibility(View.GONE);
        // 加载背景
        ImageLoaderUtil.INSTANCE.loadImage(requireContext(), mBackgroundImageView, voiceRoomBean.getBackgroundUrl(), R.color.black);
        // 设置title数据
        mRoomTitleBar.setData(voiceRoomBean.getRoomName(), voiceRoomBean.getId(), voiceRoomBean.getUserId(), present);

        // 设置底部按钮
        mRoomBottomView.setData(present.getRoomOwnerType(), this, voiceRoomBean.getRoomId());
        // 设置消息列表数据
        mRoomMessageAdapter.setRoomCreateId(voiceRoomBean.getCreateUserId());
        // init
        initPk();
    }


    @Override
    public void destroyRoom() {
        super.destroyRoom();
        //离开房间的时候
        clVoiceRoomView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.GONE);
        // uninit pk
        unInitPk();
        //取消对当前房间的监听
        EventHelper.helper().unregeister();
        //取消当前对于各种消息的回调监听
        present.unInitListener();
    }

    @Override
    public void onSpeakingStateChanged(boolean isSpeaking) {
        mRoomSeatView.setSpeaking(isSpeaking);
    }


    /**
     * 刷新当前房主信息Ui
     *
     * @param uiSeatModel
     */
    @Override
    public void refreshRoomOwner(UiSeatModel uiSeatModel) {
        if (uiSeatModel == null) {
            return;
        }
        if (TextUtils.isEmpty(uiSeatModel.getUserId())) {
            mRoomSeatView.setData("", null);
            mRoomSeatView.setSpeaking(false);
            mRoomSeatView.setRoomOwnerMute(false);
            mRoomSeatView.setGiftCount(0L);
        } else {
//            Log.e(TAG, "refreshRoomOwner: " + uiSeatModel.toString());
            UserProvider.provider().getAsyn(uiSeatModel.getUserId(), new IResultBack<UserInfo>() {
                @Override
                public void onResult(UserInfo userInfo) {
                    if (userInfo != null) {
                        mRoomSeatView.setData(userInfo.getName(), userInfo.getPortraitUri().toString());
                    } else {
                        mRoomSeatView.setData("", "");
                    }
                }
            });
            mRoomSeatView.setSpeaking(uiSeatModel.isSpeaking());
            mRoomSeatView.setRoomOwnerMute(uiSeatModel.isMute());
            mRoomSeatView.setGiftCount((long) uiSeatModel.getGiftCount());
        }
    }

    /**
     * 设置公告的内容
     *
     * @param notice
     */
    @Override
    public void setNotice(String notice) {
        if (null != mNoticeDialog) {
            mNoticeDialog.setNotice(notice);
        }
    }

    /**
     * 点击消息列表中的用户名称
     *
     * @param userId
     */
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
    public void enterSeatSuccess() {
        showToast("上麦成功");
    }


    @Override
    public void onSeatInfoChange(int index, @NonNull UiSeatModel uiSeatModel) {

    }

    /**
     * 座位发生了改变
     *
     * @param uiSeatModelList
     */
    @Override
    public void onSeatListChange(@NonNull List<UiSeatModel> uiSeatModelList) {
        List<UiSeatModel> uiSeatModels = uiSeatModelList.subList(1, uiSeatModelList.size());
        voiceRoomSeatsAdapter.refreshData(uiSeatModels);
        refreshRoomOwner(uiSeatModelList.get(0));
    }


    @Override
    public void changeStatus(int status) {
        EventHelper.helper().setCurrentStatus(status);
        //修改底部的状态
        switch (status) {
            case STATUS_NOT_ON_SEAT:
                //申请中
                mRoomBottomView.setRequestSeatImage(R.drawable.ic_request_enter_seat);
                ((AbsRoomActivity) requireActivity()).setCanSwitch(true);
                break;
            case STATUS_WAIT_FOR_SEAT:
                //等待中
                mRoomBottomView.setRequestSeatImage(R.drawable.ic_wait_enter_seat);
                break;
            case STATUS_ON_SEAT:
                //已经在麦上
                mRoomBottomView.setRequestSeatImage(R.drawable.ic_on_seat);
                ((AbsRoomActivity) requireActivity()).setCanSwitch(false);
                break;
        }

    }

    @Override
    public void showUnReadRequestNumber(int number) {
        //如果不是房主，不设置
        if (present.getRoomOwnerType() == RoomOwnerType.VOICE_OWNER) {
            mRoomBottomView.setmSeatOrderNumber(number);
        }
    }


    /**
     * 显示撤销麦位申请的弹窗
     */
    @Override
    public void showRevokeSeatRequest() {
        switch (present.currentStatus) {
            case STATUS_ON_SEAT:
                break;
            case STATUS_WAIT_FOR_SEAT:
                present.showNewRevokeSeatRequestFragment();
                break;
            case STATUS_NOT_ON_SEAT:
                present.enterSeatViewer(-1);
                break;
        }
    }


    @Override
    public void onNetworkStatus(int i) {
        mRoomTitleBar.post(new Runnable() {
            @Override
            public void run() {
                mRoomTitleBar.setDelay(i);
            }
        });
    }

    @Override
    public void finish() {
        //在销毁之前提前出栈顶
        try {
            UIStack.getInstance().remove(((VoiceRoomActivity) requireActivity()));
            requireActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickSendMessage(String message) {
        //发送文字消息
        RCChatroomBarrage barrage = new RCChatroomBarrage();
        barrage.setContent(message);
        barrage.setUserId(AccountStore.INSTANCE.getUserId());
        barrage.setUserName(AccountStore.INSTANCE.getUserName());
        present.getShield(new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                present.sendMessage(barrage);
            }
        });
    }

    @Override
    public void clearInput() {
        mRoomBottomView.clearInput();
    }

    @Override
    public void hideSoftKeyboardAndIntput() {
        mRoomBottomView.hideSoftKeyboardAndInput();
    }


    /**
     * 发送私信
     */
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
        if (!checkPKState()) {
            //弹窗邀请弹窗 并且将申请的集合和可以被要求的传入
            present.showSeatOperationViewPagerFragment(0);
        }
    }

    /**
     * 设置按钮
     */
    @Override
    public void clickSettings() {
        present.showSettingDialog();
    }

    /**
     * PK
     */
    @Override
    public void clickPk() {
//        IEventHelp.Type type = EventHelper.helper().getPKState();
//        if (IEventHelp.Type.PK_NONE == type
//                || IEventHelp.Type.PK_FINISH == type
//                || IEventHelp.Type.PK_STOP == type) {
//            PKStateManager.get().sendPkInvitation(activity, new IResultBack<Boolean>() {
//                @Override
//                public void onResult(Boolean aBoolean) {
//                }
//            });
//        } else if (IEventHelp.Type.PK_INVITE == type) {
//            PKStateManager.get().cancelPkInvitation(activity, new IResultBack<Boolean>() {
//                @Override
//                public void onResult(Boolean aBoolean) {
//                }
//            });
//        } else if (IEventHelp.Type.PK_GOING == type
//                || IEventHelp.Type.PK_PUNISH == type
//                || IEventHelp.Type.PK_START == type) {// pk中
//            PKStateManager.get().quitPK(activity);
//        }
    }

    /**
     * 请求上麦按钮
     */
    @Override
    public void clickRequestSeat() {
        if (!checkPKState()) {
            present.requestSeat(-1);
        }
    }

    /**
     * 发送礼物
     */
    @Override
    public void onSendGift() {
        present.sendGift();
    }

    @Override
    public void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice) {
        RCChatRoomMessageManager.INSTANCE.sendChatMessage(present.getRoomId(), rcChatroomVoice, true, new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer integer) {
                //成功
                return null;
            }
        }, new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
            @Override
            public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                //失败
                return null;
            }
        });
    }

    @Override
    public void clickUser(User user) {
        //如果点击的是本人的名称，那么无效
        if (TextUtils.equals(user.getUserId(), AccountStore.INSTANCE.getUserId())) {
            return;
        }
        present.getUserInfo(user.getUserId());
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
                newName -> {
                    if (TextUtils.isEmpty(newName)) {
                        showToast("房间名称不能为空");
                        return null;
                    }
                    present.setRoomName(newName);
                    mEditDialog.dismiss();
                    return null;
                }
        );
        mEditDialog.show();
    }

    /**
     * 屏蔽词弹窗
     *
     * @param roomId
     */
    @Override
    public void showShieldDialog(String roomId) {
        mShieldDialog = new ShieldDialog(requireActivity(), roomId, 10);
        mShieldDialog.show();
    }

    /**
     * 房间背景弹窗
     *
     * @param url
     */
    @Override
    public void showSelectBackgroundDialog(String url) {
        mBackgroundSettingFragment = new BackgroundSettingFragment(url, present);
        mBackgroundSettingFragment.show(getChildFragmentManager());
    }

    @Override
    public void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members) {
        mGiftFragment = new GiftFragment(voiceRoomBean, selectUserId, present);
        mGiftFragment.refreshMember(members);
        mGiftFragment.show(getChildFragmentManager());
    }

    @Override
    public void showUserSetting(Member member, UiSeatModel uiSeatModel) {
        if (mMemberSettingFragment == null) {
            mMemberSettingFragment = new MemberSettingFragment(present.getRoomOwnerType(), present);
        }
        if (uiSeatModel != null) {
            //说明当前用户在麦位上
            mMemberSettingFragment.setMemberIsOnSeat(uiSeatModel.getIndex() > -1);
            mMemberSettingFragment.setSeatPosition(uiSeatModel.getIndex());
            mMemberSettingFragment.setMute(uiSeatModel.isMute());
        } else {
            mMemberSettingFragment.setMemberIsOnSeat(false);
        }
        mMemberSettingFragment.show(getChildFragmentManager(), member, present.getCreateUserId());
    }

    @Override
    public void showMusicDialog() {
        mMusicDialog = new MusicDialog(present.getRoomId());
        mMusicDialog.show(getChildFragmentManager());
    }

    /**
     * 当前房间直播已经结束
     */
    @Override
    public void showFinishView() {
        clVoiceRoomView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLikeAnimation() {
        mGiftAnimationView.showFov(mRoomBottomView.getGiftViewPoint());
    }

    @Override
    public void setOnlineCount(int OnlineCount) {
        mRoomTitleBar.setOnlineNum(OnlineCount);
    }

    @Override
    public void switchOtherRoom(String roomId) {
        ((AbsRoomActivity) requireActivity()).switchOtherRoom(roomId);
    }

    @Override
    public void setTitleFollow(boolean isFollow) {
        mRoomTitleBar.setFollow(isFollow);
    }


    @Override
    public void setVoiceName(String name) {
        mRoomTitleBar.setRoomName(name);
    }

    @Override
    public void setRoomBackground(String url) {
        ImageLoaderUtil.INSTANCE.loadImage(requireContext(), mBackgroundImageView, url, R.color.black);
    }

    @Override
    public void refreshSeat() {
        voiceRoomSeatsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_go_back_list) {
            //直接退出当前房间
            finish();
        }
    }

    @Override
    public void clickBroadcast(RCAllBroadcastMessage message) {
        mAllBroadcastView.showMessage(null);
        present.jumpRoom(message);
    }
}
