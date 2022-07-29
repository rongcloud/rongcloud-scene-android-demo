package com.rc.live.room;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.utils.UiUtils;
import com.basis.widget.dialog.VRCenterDialog;
import com.meihu.beauty.fragment.BeautyDialogFragment;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.rc.live.R;
import com.rc.live.constant.CurrentStatusType;
import com.rc.live.fragment.LiveRoomCreatorSettingFragment;
import com.rc.live.fragment.LiveRoomHangUpFragment;
import com.rc.live.fragment.LiveRoomUnIninviteVideoFragment;
import com.rc.live.fragment.LiveRoomVideoSettingFragment;
import com.rc.live.helper.LiveEventHelper;
import com.rc.live.helper.SimpleLivePkListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.liveroom.api.RCLiveMixType;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;
import cn.rongcloud.liveroom.manager.RCDataManager;
import cn.rongcloud.liveroom.manager.SeatManager;
import cn.rongcloud.liveroom.weight.RCLiveView;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.music.MusicMiniView;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.ui.OnItemClickListener;
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
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.fragment.MemberListFragment;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomBeautyFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomBeautyMakeUpFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomFunIdUitls;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomOverTurnFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSeatModeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSpecialEffectsFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomTagsFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomVideoSetFun;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.widget.AllBroadcastView;
import cn.rongcloud.roomkit.ui.room.widget.GiftAnimationView;
import cn.rongcloud.roomkit.ui.room.widget.RecyclerViewAtVP2;
import cn.rongcloud.roomkit.ui.room.widget.RoomBottomView;
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar;
import cn.rongcloud.roomkit.widget.EditDialog;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import cn.rongcloud.roomkit.widget.decoration.DefaultItemDecoration;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.utils.StatusBarUtil;
import io.rong.imlib.model.MessageContent;

/**
 * 直播房界面
 */
public class LiveRoomFragment extends AbsRoomFragment<LiveRoomPresenter>
        implements LiveRoomView, CreateLiveRoomFragment.CreateRoomCallBack, MemberListFragment.OnClickUserListener
        , View.OnClickListener, AllBroadcastView.OnClickBroadcast,
        OnItemClickListener<MutableLiveData<IFun.BaseFun>>, RoomMessageAdapter.OnClickMessageUserListener {


    private FrameLayout flLiveView;
    private GiftAnimationView giftView;
    private ConstraintLayout clLiveRoomView;
    private RoomTitleBar roomTitleBar;
    private TextView tvNotice;
    private RecyclerViewAtVP2 rvMessage;
    private AllBroadcastView viewAllBroadcast;
    private RoomBottomView roomBottomView;
    private RelativeLayout rlRoomFinishedId;
    private Button btnGoBackList;
    private FrameLayout flCreateLiveRoom;
    private MusicMiniView mMusicMiniView;
    private LivePKView pkView;
    private boolean isCreate;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private CreateLiveRoomFragment createLiveRoomFragment;
    private RoomSettingFragment mRoomSettingFragment;
    private RoomNoticeDialog mNoticeDialog;
    private InputPasswordDialog mInputPasswordDialog;
    private EditDialog setRoomNameDialog;
    private String mRoomId;
    private ShieldDialog mShieldDialog;
    private RoomMessageAdapter mRoomMessageAdapter;
    private BeautyDialogFragment tiezhiDilog;
    private BeautyDialogFragment meiyanDialog;
    private BeautyDialogFragment meizhuangDialog;
    private BeautyDialogFragment texiaoDialog;
    private MemberSettingFragment mMemberSettingFragment;
    private LiveRoomVideoSettingFragment roomVideoSettingFragment;
    private VRCenterDialog finishDiolog;
    private ExitRoomPopupWindow mExitRoomPopupWindow;
    private GiftFragment mGiftFragment;
    private MemberListFragment mMemberListFragment;
    private TextView tvGiftCount;
    private int marginTop;
    private FrameLayout messageContainerView;

    public static Fragment getInstance(String roomId, boolean isCreate) {
        Bundle bundle = new Bundle();
        bundle.putString(ROOM_ID, roomId);
        bundle.putBoolean(IntentWrap.KEY_IS_CREATE, isCreate);
        LiveRoomFragment fragment = new LiveRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public LiveRoomPresenter createPresent() {
        return new LiveRoomPresenter(this, this.getLifecycle());
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_live_room;
    }

    @Override
    public int getMarginTop() {
        return marginTop;
    }

    @Override
    public void init() {
        initView();
        initData();
        initDialog();
        fragmentManager = getActivity().getSupportFragmentManager();
        if (isCreate) {
            fragmentTransaction = fragmentManager.beginTransaction();
            clLiveRoomView.setVisibility(View.INVISIBLE);
            createLiveRoomFragment = CreateLiveRoomFragment.getInstance();
            createLiveRoomFragment.setCreateRoomCallBack(this);
            fragmentTransaction.replace(R.id.fl_create_live_room, createLiveRoomFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void initData() {
        isCreate = getArguments().getBoolean(IntentWrap.KEY_IS_CREATE);
        mRoomId = getArguments().getString(ROOM_ID);
    }

    /**
     * 构建部分dialog
     */
    private void initDialog() {
        if (null == mNoticeDialog) {
            mNoticeDialog = new RoomNoticeDialog(activity);
        }
    }

    /**
     * 加入房间，初次进入和切换房间的时候
     */
    @Override
    public void joinRoom() {
        //如果当前不是创建房间，就继续执行，如果是创建房间,那么就创建成功了以后去执行
        if (!isCreate) {
            present.init(mRoomId, isCreate);
            viewAllBroadcast.setBroadcastListener();
        }
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.equals(mRoomId, "-1")) {
            //说明房间没有生成的情况
            LiveEventHelper.getInstance().unRegister();
            finish();
            return;
        }
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
    public void preJoinRoom() {
        super.preJoinRoom();
    }

    /**
     * 滑动切换房间，房间销毁的方法，取消掉对于当前界面的监听就可以了
     */
    @Override
    public void destroyRoom() {
        super.destroyRoom();
        present.unInitLiveRoomListener();
//        mRoomMessageAdapter.release();
        PKManager.get().unInit();
    }

    @Override
    public void initListener() {
        super.initListener();
        btnGoBackList.setOnClickListener(this::onClick);
        tvNotice.setOnClickListener(this::onClick);
    }

    private void initView() {
        flLiveView = (FrameLayout) getView().findViewById(R.id.fl_live_view);
        messageContainerView = getView().findViewById(R.id.rl_message_id);
        giftView = (GiftAnimationView) getView().findViewById(R.id.gift_view);
        giftView.setOnBottomOptionClickListener(new GiftAnimationView.OnClickBackgroundListener() {
            @Override
            public void onSendLikeMessage(RCChatroomLike rcChatroomLike) {
                if (!TextUtils.equals(mRoomId, "-1")) {
                    present.sendMessage(rcChatroomLike, false);
                }
            }
        });
        clLiveRoomView = (ConstraintLayout) getView().findViewById(R.id.cl_live_room_view);
        roomTitleBar = (RoomTitleBar) getView().findViewById(R.id.room_title_bar);
        tvNotice = (TextView) getView().findViewById(R.id.tv_notice);
        pkView = getView().findViewById(R.id.pk_view);
        tvNotice.post(new Runnable() {
            @Override
            public void run() {
                // 设置pk的位置，和双人布局完全重合
                marginTop = (int) (tvNotice.getY() + tvNotice.getHeight() + UiUtils.dp2px(8));
            }
        });
        tvGiftCount = (TextView) getView().findViewById(R.id.tv_gift_count);

        viewAllBroadcast = (AllBroadcastView) getView().findViewById(R.id.view_all_broadcast);
        viewAllBroadcast.setOnClickBroadcast(this::clickBroadcast);

        roomBottomView = (RoomBottomView) getView().findViewById(R.id.room_bottom_view);
        rlRoomFinishedId = (RelativeLayout) getView().findViewById(R.id.rl_room_finished_id);
        btnGoBackList = (Button) getView().findViewById(R.id.btn_go_back_list);
        clLiveRoomView.setPadding(0, StatusBarUtil.getStatusBarHeight(requireContext()), 0, 0);

        //弹幕消息列表
        rvMessage = (RecyclerViewAtVP2) getView().findViewById(R.id.rv_message);
        rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessage.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT, 0, UiUtils.dp2px(5)));
        mRoomMessageAdapter = new RoomMessageAdapter(getContext(), rvMessage, this, RoomType.LIVE_ROOM);
        rvMessage.setAdapter(mRoomMessageAdapter);

        roomTitleBar.setOnMenuClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                clickMenu();
            }
        });
        roomTitleBar.setOnMemberClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
//                if (present.getRoomOwnerType() == RoomOwnerType.LIVE_VIEWER) {
//                    showMemberSettingFragment(present.getCreateUserId());
//                }
                showMemberSettingFragment(present.getCreateUserId());
            }
        });
        roomTitleBar.setOnLineMemberClickListener().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Throwable {
                mMemberListFragment = new MemberListFragment(present.getRoomId(), LiveRoomFragment.this);
                mMemberListFragment.show(getChildFragmentManager());
            }
        });
        // 音乐小窗口
        mMusicMiniView = getView(R.id.mmv_view);
        mMusicMiniView.setOnMusicClickListener(v -> {
            if (present.getRoomOwnerType() == RoomOwnerType.LIVE_OWNER) {
                showMusicDialog();
            }
        });
    }

    /**
     * 显示是否关闭房间弹窗
     */
    private void showFinishDiolog() {
        if (finishDiolog == null) {
            finishDiolog = new VRCenterDialog(requireActivity(), null);
            finishDiolog.replaceContent("是否结束当前直播?", "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDiolog.dismiss();
                }
            }, "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    present.finishLiveRoom();
                }
            }, null);
        }
        finishDiolog.show();
    }

    @Override
    public void changeMessageContainerHeight() {
        messageContainerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int mixType = RCDataManager.get().getMixType();
                ViewGroup.LayoutParams layoutParams = messageContainerView.getLayoutParams();
                if (mixType == RCLiveMixType.RCMixTypeOneToOne.getValue() || mixType == RCLiveMixType.RCMixTypeOneToSix.getValue()) {
                    //如果是默认和1V6的时候，高度默认为260
                    layoutParams.height = UiUtils.dp2px(260);
                } else {
                    RCLiveView preview = RCLiveEngine.getInstance().preview();
                    if (preview != null) {
                        int realHeight = preview.getRealHeight();
                        if (realHeight > 0) {
                            layoutParams.height = clLiveRoomView.getHeight() - marginTop - preview.getRealHeight()
                                    - roomBottomView.getHeight() - UiUtils.dp2px(12);
                        }
                    }
                }
                messageContainerView.setLayoutParams(layoutParams);
                Log.e(TAG, "changeMessageContainerHeight: " + messageContainerView.getHeight());
            }
        }, 500);
    }

    @Override
    public void refreshMessageList() {
        mRoomMessageAdapter.notifyDataSetChanged();
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
        if (TextUtils.equals(UserManager.get().getUserId(), present.getCreateUserId()) && !PKManager.get().getPkState().enableAction()) {
            return;
        }
        //如果是房主
        if (present.getRoomOwnerType() == RoomOwnerType.LIVE_OWNER) {
            showFinishDiolog();
            return;
        }
        mExitRoomPopupWindow = new ExitRoomPopupWindow(getContext(), present.getRoomOwnerType(), new ExitRoomPopupWindow.OnOptionClick() {
            @Override
            public void clickPackRoom() {
                //如果是观众，但是观众在麦位上
                if (present.getRoomOwnerType() == RoomOwnerType.LIVE_VIEWER
                        && LiveEventHelper.getInstance().getCurrentStatus() == CurrentStatusType.STATUS_ON_SEAT) {
                    KToast.show("连麦中禁止此操作");
                    return;
                }
                //最小化窗口,判断是否有权限
                if (checkDrawOverlaysPermission(false)) {
                    finish();
                    //缩放动画,并且显示悬浮窗，在这里要做悬浮窗判断
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    LiveEventHelper.getInstance().removeSeatViewProvider();
                    PKManager.get().unInit();
                    MiniRoomManager.getInstance().show(requireActivity(), mRoomId, requireActivity().getIntent()
                            , createMiniWindow(), LiveEventHelper.getInstance());
                } else {
                    showOpenOverlaysPermissionDialog();
                }
            }

            @Override
            public void clickLeaveRoom() {
                // 观众离开房间
                present.leaveLiveRoom(null);
            }

            @Override
            public void clickCloseRoom() {

            }
        });
        mExitRoomPopupWindow.show(clLiveRoomView);
    }

    private View createMiniWindow() {
        View miniWindows = LayoutInflater.from(UIKit.getContext()).inflate(R.layout.view_live_room_mini, null);
        RelativeLayout relativeLayout = miniWindows.findViewById(R.id.fl_content_id);
        View view_close = miniWindows.findViewById(R.id.iv_close_id);
        view_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiniRoomManager.getInstance().finish("", null);
            }
        });
        // 这里改成DP值
        RCLiveView rcLiveView = RCLiveEngine.getInstance().preview();
        ViewParent parent = rcLiveView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeAllViews();
        }
        rcLiveView.setDevTop(0);
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                rcLiveView.attachParent(relativeLayout, null);
            }
        });
        return miniWindows;
    }

    /**
     * 创建房间成功
     *
     * @param voiceRoomBean
     */
    @Override
    public void onCreateSuccess(VoiceRoomBean voiceRoomBean) {
        if (createLiveRoomFragment != null && fragmentManager != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(createLiveRoomFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        mRoomId = voiceRoomBean.getRoomId();
        clLiveRoomView.setVisibility(View.VISIBLE);
        present.init(mRoomId, isCreate);
        viewAllBroadcast.setBroadcastListener();
    }

    /**
     * 房间已经存在
     *
     * @param voiceRoomBean
     */
    @Override
    public void onCreateExist(VoiceRoomBean voiceRoomBean) {

    }

    @Override
    public void prepareSuccess(RCLiveView rcLiveVideoView) {
        showRCLiveVideoView(rcLiveVideoView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_go_back_list) {
            finish();
            LiveEventHelper.getInstance().unRegister();
        } else if (v.getId() == R.id.tv_notice) {
            showNoticeDialog(false);
        }
    }

    /**
     * 点击用户
     *
     * @param user
     */
    @Override
    public void clickUser(User user) {
        //如果点击的是本人的名称，那么无效
        if (TextUtils.equals(user.getUserId(), UserManager.get().getUserId())) {
            return;
        }
        showMemberSettingFragment(user.getUserId());
    }

    @Override
    public void showMemberSettingFragment(String userId) {
        OkApi.post(VRApi.GET_USER, new OkParams().add("userIds", new String[]{userId}).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Member> members = result.getList(Member.class);
                    if (members != null && members.size() > 0) {
                        if (mMemberSettingFragment == null) {
                            mMemberSettingFragment = new MemberSettingFragment(present.getRoomOwnerType(), present);
                        }
                        RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(userId);
                        if (rcLiveSeatInfo != null) {
                            //说明当前用户在麦位上
                            mMemberSettingFragment.setMemberIsOnSeat(rcLiveSeatInfo.getIndex() > -1);
                            mMemberSettingFragment.setSeatPosition(rcLiveSeatInfo.getIndex());
                            mMemberSettingFragment.setMute(rcLiveSeatInfo.isMute());
                        } else {
                            mMemberSettingFragment.setMemberIsOnSeat(false);
                        }
                        mMemberSettingFragment.show(getChildFragmentManager(), members.get(0), present.getCreateUserId());
                    }
                }
            }
        });
    }

    @Override
    public void showCreatorSettingFragment(RCLiveSeatInfo rcLiveSeatInfo) {
        if (PKManager.get().getPkState().isInPk()) {
            return;
        }
        boolean isOwner = false;
        if (present.getCreateUserId().equals(rcLiveSeatInfo.getUserId())) {
            //创建者
            isOwner = true;
        }
        LiveRoomCreatorSettingFragment liveRoomCreatorSettingFragment = new LiveRoomCreatorSettingFragment(rcLiveSeatInfo.getIndex(),
                LiveEventHelper.getInstance().isMute(), rcLiveSeatInfo.isEnableVideo()
                , present.getCreateUser(), present, isOwner);
        liveRoomCreatorSettingFragment.show(getLiveFragmentManager());
    }

    @Override
    public void showHangUpFragment(String userId) {
        User user = new User();
        user.setUserId(userId);
        RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(userId);
        LiveRoomHangUpFragment liveRoomPickOutFragment = new LiveRoomHangUpFragment(rcLiveSeatInfo.isEnableVideo(), user, present);
        liveRoomPickOutFragment.show(getLiveFragmentManager());
    }

    @Override
    public void showUninviteVideoFragment(String userId) {
        LiveRoomUnIninviteVideoFragment liveRoomUnIninviteVideoFragment = new LiveRoomUnIninviteVideoFragment(userId, present);
        liveRoomUnIninviteVideoFragment.show(getLiveFragmentManager());
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
     * 发送全服广播
     *
     * @param message
     */
    @Override
    public void clickBroadcast(RCAllBroadcastMessage message) {
        viewAllBroadcast.showMessage(null);
        present.jumpRoom(message);
    }

    @Override
    public void switchOtherRoom(String roomId) {
        ((AbsRoomActivity) requireActivity()).switchOtherRoom(roomId);
    }


    @Override
    public void clickItem(MutableLiveData<IFun.BaseFun> item, int position) {
        IFun.BaseFun fun = item.getValue();
        if (fun instanceof RoomNoticeFun) {
            showNoticeDialog(true);
        } else if (fun instanceof RoomLockFun) {
            if (fun.getStatus() == 1) {
                present.setRoomPassword(false, "", item, mRoomId);
            } else {
                showSetPasswordDialog(item);
            }
        } else if (fun instanceof RoomNameFun) {
            showSetRoomNameDialog();
        } else if (fun instanceof RoomShieldFun) {
            showShieldDialog();
        } else if (fun instanceof RoomSeatModeFun) {
            if (fun.getStatus() == 1) {
                //申请上麦
                present.setSeatMode(false);
            } else {
                //自由上麦
                present.setSeatMode(true);
            }
        } else if (fun instanceof RoomMusicFun) {
            showMusicDialog();
        } else if (fun instanceof RoomOverTurnFun) {
            RCLiveEngine.getInstance().switchCamera(null);
        } else if (fun instanceof RoomBeautyFun) {
            if (meiyanDialog == null)
                meiyanDialog = new BeautyDialogFragment(requireActivity(), MHConfigConstants.MEI_YAN);
            meiyanDialog.show();
        } else if (fun instanceof RoomBeautyMakeUpFun) {
            if (meizhuangDialog == null)
                meizhuangDialog = new BeautyDialogFragment(requireActivity(), MHConfigConstants.MEI_ZHUANG);
            meizhuangDialog.show();
        } else if (fun instanceof RoomTagsFun) {
            if (tiezhiDilog == null)
                tiezhiDilog = new BeautyDialogFragment(requireActivity(), MHConfigConstants.TIE_ZHI);
            tiezhiDilog.show();
        } else if (fun instanceof RoomSpecialEffectsFun) {
            if (texiaoDialog == null)
                texiaoDialog = new BeautyDialogFragment(requireActivity(), MHConfigConstants.TE_XIAO);
            texiaoDialog.show();
        } else if (fun instanceof RoomVideoSetFun) {
            List<String> keys = Arrays.asList(LiveRoomKvKey.LIVE_ROOM_VIDEO_RESOLUTION, LiveRoomKvKey.LIVE_ROOM_VIDEO_FPS);
            LiveEventHelper.getInstance().getRoomInfoByKey(keys, new ClickCallback<Map<String, String>>() {
                @Override
                public void onResult(Map<String, String> result, String msg) {
                    //传进去最新的分辨率和帧率
                    roomVideoSettingFragment = new LiveRoomVideoSettingFragment(result.get(LiveRoomKvKey.LIVE_ROOM_VIDEO_RESOLUTION), result.get(LiveRoomKvKey.LIVE_ROOM_VIDEO_FPS), present);
                    roomVideoSettingFragment.show(getLiveFragmentManager());
                }
            });
        }
        SensorsUtil.instance().settingClick(mRoomId, present.getRoomName(), fun.getText(), RoomFunIdUitls.convert(fun), RcEvent.LiveRoom);
    }

    /**
     * 显示公告弹窗
     *
     * @param isEdit 是否可编辑
     */
    public void showNoticeDialog(boolean isEdit) {
        LiveEventHelper.getInstance().getRoomInfoByKey(LiveRoomKvKey.LIVE_ROOM_NOTICE, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String notice) {
                if ((result && TextUtils.isEmpty(notice)) || !result) {
                    notice = String.format("欢迎来到%s。", present.getRoomName());
                }
                mNoticeDialog.show(notice, isEdit, new RoomNoticeDialog.OnSaveNoticeListener() {
                    @Override
                    public void saveNotice(String notice) {
                        //修改公告信息
                        present.modifyNotice(notice);
                    }
                });
            }
        });
    }

    @Override
    public void setNotice(String notice) {
        Logger.e(TAG, "notice = " + notice);
        if (null != mNoticeDialog) {
            mNoticeDialog.setNotice(notice);
        }
    }

    @Override
    public void showUnReadRequestNumber(int requestNumber) {
        //如果不是房主，不设置
        if (present.getRoomOwnerType() == RoomOwnerType.LIVE_OWNER) {
            roomBottomView.setmSeatOrderNumber(requestNumber);
        }
    }

    /**
     * 显示密码设置弹窗
     *
     * @param item
     */
    public void showSetPasswordDialog(MutableLiveData<IFun.BaseFun> item) {
        if (mInputPasswordDialog == null)
            mInputPasswordDialog = new InputPasswordDialog(getContext(), false, new InputPasswordDialog.OnClickListener() {
                @Override
                public void clickCancel() {
                    mInputPasswordDialog.dismiss();
                    mInputPasswordDialog = null;
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
                    mInputPasswordDialog = null;
                    present.setRoomPassword(true, password, item, mRoomId);
                }
            });
        mInputPasswordDialog.show();
    }

    /**
     * 弹出房间名称弹窗
     */
    public void showSetRoomNameDialog() {
        setRoomNameDialog = new EditDialog(requireContext(),
                "修改房间标题",
                "请输入房间名",
                present.getRoomName(),
                10,
                false, new EditDialog.OnClickEditDialog() {
            @Override
            public void clickCancel() {
                setRoomNameDialog.dismiss();
            }

            @Override
            public void clickConfirm(String newName) {
                if (TextUtils.isEmpty(newName)) {
                    KToast.show("房间名称不能为空");
                    return;
                }
                present.setRoomName(newName, mRoomId);
                setRoomNameDialog.dismiss();
            }
        });
        setRoomNameDialog.show();
    }

    /**
     * 显示屏蔽词弹窗
     */
    public void showShieldDialog() {
        if (mShieldDialog == null)
            mShieldDialog = new ShieldDialog(requireActivity(), mRoomId, 10, new ShieldDialog.OnShieldDialogListener() {
                @Override
                public void onAddShield(String s, List<Shield> shields) {
                    onAfter(shields);
                }

                @Override
                public void onDeleteShield(Shield shield, List<Shield> shields) {
                    onAfter(shields);
                }

                public void onAfter(List<Shield> shields) {
                    present.updateShield(shields);
                }
            });
        mShieldDialog.show();
    }


    /**
     * 显示音乐弹窗
     */
    public void showMusicDialog() {
        MusicControlManager.getInstance().showDialog(getChildFragmentManager(), present.getRoomId());
    }

    @Override
    public void showRoomSettingFragment(List<MutableLiveData<IFun.BaseFun>> funList) {
        if (mRoomSettingFragment == null) {
            mRoomSettingFragment = new RoomSettingFragment(this);
        }
        mRoomSettingFragment.show(getLiveFragmentManager(), funList);
    }

    @Override
    public Context getLiveActivity() {
        return requireActivity();
    }

    /**
     * 当前房间已结束
     */
    @Override
    public void showFinishView() {
        flLiveView.setVisibility(View.INVISIBLE);
        clLiveRoomView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.VISIBLE);
    }

    /**
     * 设置房间的数据
     *
     * @param voiceRoomBean
     */
    @Override
    public void setRoomData(VoiceRoomBean voiceRoomBean) {
        clLiveRoomView.setVisibility(View.VISIBLE);
        flLiveView.setVisibility(View.INVISIBLE);
        rlRoomFinishedId.setVisibility(View.GONE);
        // 设置title数据
        User createUser = voiceRoomBean.getCreateUser();
        roomTitleBar.setData(present.getRoomOwnerType(), createUser.getUserName(), voiceRoomBean.getId(), voiceRoomBean.getUserId(), present);
        roomTitleBar.setCreatorPortrait(createUser.getPortraitUrl());
        // 设置底部按钮
        roomBottomView.setData(present.getRoomOwnerType(), present, voiceRoomBean.getRoomId());
        // 设置消息列表数据
        mRoomMessageAdapter.setRoomCreateId(voiceRoomBean.getCreateUserId());
        PKManager.get().init(mRoomId, RoomType.LIVE_ROOM.getType(), pkView, new SimpleLivePkListener() {
            @Override
            public void onPkStart() {
                pkView.setVisibility(View.VISIBLE);
                RCLiveView videoView = RCLiveEngine.getInstance().preview();
                videoView.setDevTop(getMarginTop());
            }

            @Override
            public void onPkStop() {
                pkView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPkStateChanged(PKState pkState) {
                switch (pkState) {
                    case PK_NONE:
                    case PK_FINISH:
                    case PK_STOP:
                        roomBottomView.refreshPkState(RoomBottomView.PKViewState.pk);
                        pkView.setVisibility(View.INVISIBLE);
                        break;

                    case PK_INVITE:
                        roomBottomView.refreshPkState(RoomBottomView.PKViewState.wait);
                        break;
                    case PK_GOING:
                    case PK_PUNISH:
                    case PK_START:
                        roomBottomView.refreshPkState(RoomBottomView.PKViewState.stop);
                        break;
                }
            }
        });
    }


    @Override
    public void setTitleFollow(boolean isFollow) {
        roomTitleBar.setFollow(isFollow);
    }


    /**
     * 添加消息到公屏上
     *
     * @param messageContent 消息
     * @param isReset        是否重置消息 false 代表不重置，仅仅添加 true 代表清空之前消息，重置消息
     */
    @Override
    public void addMessageContent(MessageContent messageContent, boolean isReset) {
        List<MessageContent> list = new ArrayList<>(1);
//        if (messageContent != null) {
//            list.add(messageContent);
//        }
//        mRoomMessageAdapter.setMessages(list, isReset);
        if (isReset) {
            if (messageContent != null) {
                list.add(messageContent);
            }
            mRoomMessageAdapter.setMessages(list, isReset);
        } else {
            mRoomMessageAdapter.interMessage(messageContent);
        }
    }

    @Override
    public void addMessageList(List<MessageContent> messageContentList, boolean isReset) {
        mRoomMessageAdapter.setMessages(messageContentList, isReset);
    }

    @Override
    public void showLikeAnimation() {
        giftView.showFov(roomBottomView.getGiftViewPoint());
    }

    @Override
    public void changeStatus() {
        //修改底部的状态
        switch (LiveEventHelper.getInstance().getCurrentStatus()) {
            case STATUS_NOT_ON_SEAT:
                //申请中
                roomBottomView.setRequestSeatImage(R.drawable.ic_request_enter_seat);
                ((AbsRoomActivity) requireActivity()).setCanSwitch(true);
                break;
            case STATUS_WAIT_FOR_SEAT:
                //等待中
                roomBottomView.setRequestSeatImage(R.drawable.ic_wait_enter_seat);
                ((AbsRoomActivity) requireActivity()).setCanSwitch(true);
                break;
            case STATUS_ON_SEAT:
                //已经在麦上
                roomBottomView.setRequestSeatImage(R.drawable.ic_on_seat);
                ((AbsRoomActivity) requireActivity()).setCanSwitch(false);
                break;
        }
    }

    @Override
    public void changeSeatOrder() {
        switch (LiveEventHelper.getInstance().getInviteStatusType()) {
            case STATUS_NOT_INVITRED:
                roomBottomView.setSeatOrderImage(R.drawable.ic_seat_order);
                break;
            case STATUS_UNDER_INVITATION:
                roomBottomView.setSeatOrderImage(R.drawable.ic_wait_enter_seat);
                break;
            case STATUS_CONNECTTING:
                roomBottomView.setSeatOrderImage(R.drawable.ic_on_seat);
                break;
        }
    }

    @Override
    public FragmentManager getLiveFragmentManager() {
        return getChildFragmentManager();
    }

    /**
     * 销毁整个界面，取消掉对于整个界面的监听
     */
    @Override
    public void finish() {
        present.unInitLiveRoomListener();
        if (null != mRoomMessageAdapter) mRoomMessageAdapter.release();
        requireActivity().finish();
    }

    @Override
    public void onDestroy() {
        flLiveView.removeAllViews();
        super.onDestroy();
    }

    @Override
    public void showRCLiveVideoView(RCLiveView videoView) {
        flLiveView.removeAllViews();
        if (RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
            //如果是1V1的时候
            videoView.setDevTop(0);
        } else {
            videoView.setDevTop(marginTop);
        }
        flLiveView.setVisibility(View.INVISIBLE);
        videoView.attachParent(flLiveView, null);
        flLiveView.postDelayed(() -> {
            flLiveView.setVisibility(View.VISIBLE);
        }, 400);
        changeMessageContainerHeight();
    }

    @Override
    public void showNetWorkStatus(long delayMs) {
        roomTitleBar.post(new Runnable() {
            @Override
            public void run() {
                roomTitleBar.setDelay((int) delayMs, true);
            }
        });
    }

    @Override
    public void setOnlineCount(int onLineCount) {
        roomTitleBar.setOnlineNum(onLineCount);
    }

    @Override
    public void setCreateUserGift(String giftCount) {
        tvGiftCount.setText(giftCount);
    }

    @Override
    public void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members) {
        mGiftFragment = new GiftFragment(voiceRoomBean, selectUserId, present);
        mGiftFragment.refreshMember(members);
        mGiftFragment.show(getChildFragmentManager());
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
}
