package com.rc.live.room;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.mvp.BasePresenter;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ScreenUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.wapper.IRoomCallBack;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.jakewharton.rxbinding4.view.RxView;
import com.rc.live.R;
import com.rc.live.constant.CurrentStatusType;
import com.rc.live.constant.InviteStatusType;
import com.rc.live.fragment.LiveRoomCreatorSettingFragment;
import com.rc.live.fragment.LiveRoomLayoutSettingFragment;
import com.rc.live.fragment.LiveRoomVideoSettingFragment;
import com.rc.live.helper.LiveEventHelper;
import com.rc.live.helper.LiveRoomListener;
import com.rc.live.inter.LiveLayoutSettingCallBack;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.liveroom.api.RCHolder;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.liveroom.api.RCLiveMixType;
import cn.rongcloud.liveroom.api.RCParamter;
import cn.rongcloud.liveroom.api.callback.RCLiveCallback;
import cn.rongcloud.liveroom.api.error.RCLiveError;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;
import cn.rongcloud.liveroom.api.model.RCLivevideoFinishReason;
import cn.rongcloud.liveroom.manager.RCDataManager;
import cn.rongcloud.liveroom.manager.SeatManager;
import cn.rongcloud.liveroom.weight.RCLiveView;
import cn.rongcloud.music.MusicApi;
import cn.rongcloud.music.MusicBean;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.manager.AllBroadcastManager;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatroomAdmin;
import cn.rongcloud.roomkit.message.RCChatroomBarrage;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.message.RCChatroomLocationMessage;
import cn.rongcloud.roomkit.message.RCChatroomSeats;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.provider.VoiceRoomProvider;
import cn.rongcloud.roomkit.ui.RoomListIdsCache;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomBeautyFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomBeautyMakeUpFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomOverTurnFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSeatModeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSpecialEffectsFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomTagsFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomVideoSetFun;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.EmptySeatFragment;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.ICommonDialog;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.RevokeSeatRequestFragment;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.SeatOperationViewPagerFragment;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.roomkit.ui.room.widget.RoomBottomView;
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar;
import cn.rongcloud.roomkit.ui.room.widget.WaveView;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCMixConfig;
import cn.rongcloud.rtc.api.stream.RCRTCCameraOutputStream;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import cn.rongcloud.rtc.base.RCRTCVideoFrame;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.TextMessage;
import jp.wasabeef.glide.transformations.BlurTransformation;
import kotlin.Unit;

/**
 * 直播房
 */
public class LiveRoomPresenter extends BasePresenter<LiveRoomView> implements
        ILiveRoomPresent, RoomTitleBar.OnFollowClickListener,
        MemberSettingFragment.OnMemberSettingClickListener,
        ICommonDialog, LiveRoomListener, GiftFragment.OnSendGiftListener
        , RoomBottomView.OnBottomOptionClickListener, LiveLayoutSettingCallBack
        , LiveRoomCreatorSettingFragment.OnCreatorSettingClickListener
        , LiveRoomVideoSettingFragment.OnVideoConfigSetting {

    private String TAG = "LiveRoomPresenter";

    private VoiceRoomBean mVoiceRoomBean;//房间信息
    private RoomOwnerType roomOwnerType;//房间用户身份
    private List<String> shields = new ArrayList<>();//当前屏蔽词
    private List<Disposable> disposablesManager = new ArrayList<>();//监听管理器

    private ArrayList<User> requestSeats = new ArrayList<>();//申请连麦的集合
    private ArrayList<User> inviteSeats = new ArrayList<>();//可以被邀请的集合
    private boolean isInRoom;
    private SeatOperationViewPagerFragment seatOperationViewPagerFragment;
    private EmptySeatFragment emptySeatFragment;
    private Map<String, String> giftMap;
    private InputPasswordDialog inputPasswordDialog;

    public LiveRoomPresenter(LiveRoomView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
    }


    /**
     * 初始化
     *
     * @param roomId
     * @param isCreate
     */
    public void init(String roomId, boolean isCreate) {
        LiveEventHelper.getInstance().addLiveRoomListeners(this);
        isInRoom = TextUtils.equals(LiveEventHelper.getInstance().getRoomId(), roomId);
        getRoomInfo(roomId, isCreate);
    }

    /**
     * 获取房间数据
     *
     * @param roomId
     * @param isCreate
     */
    public void getRoomInfo(String roomId, boolean isCreate) {
        mView.showLoading("");
        OkApi.get(VRApi.getRoomInfo(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        mVoiceRoomBean = roomBean;
                        if (isInRoom) {
                            //如果已经在房间里面了,那么需要重新设置监听
                            LiveEventHelper.getInstance().setSeatViewProvider();
                            setCurrentRoom(mVoiceRoomBean, isCreate);
                            RCLiveEngine.getInstance().preview().updateLayout();
                            if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                                refreshMusicView(true);
                            }
                            mView.dismissLoading();
                        } else {
                            leaveRoom(roomId, isCreate, true);
                        }

                    }
                } else {
                    mView.dismissLoading();
                    if (result.getCode() == 30001) {
                        //房间不存在了
                        mView.showFinishView();
                        leaveRoom(roomId, isCreate, false);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
            }
        });
    }

    /**
     * 先退出上次房间，再加入房间
     *
     * @param roomId
     * @param isCreate
     * @param isExit
     */
    private void leaveRoom(String roomId, boolean isCreate, boolean isExit) {
        LiveEventHelper.getInstance().leaveRoom(new IRoomCallBack() {
            @Override
            public void onSuccess() {
                if (isExit) {
                    UIKit.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinRoom(roomId, isCreate);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onError(int code, String message) {
                if (isExit) {
                    joinRoom(roomId, isCreate);
                }
            }
        });
    }

    /**
     * 加入房间
     *
     * @param roomId
     * @param isCreate
     */
    private void joinRoom(String roomId, boolean isCreate) {
        if (mVoiceRoomBean.getCreateUserId().equals(UserManager.get().getUserId())) {
            prepare(roomId, isCreate);
        } else {
            //如果是观众就直接加入房间
            LiveEventHelper.getInstance().joinRoom(roomId, new ClickCallback<Boolean>() {
                @Override
                public void onResult(Boolean result, String msg) {
                    mView.dismissLoading();
                    if (result) {
                        setCurrentRoom(mVoiceRoomBean, isCreate);
                        if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                            refreshMusicView(true);
                        }
                    } else {
                        //房间不存在了
                        mView.showFinishView();
                        leaveRoom(roomId, isCreate, false);
                    }
                }
            });
        }
    }

    /**
     * 获取屏蔽词
     */
    private void getShield() {
        LiveEventHelper.getInstance().getRoomInfoByKey(LiveRoomKvKey.LIVE_ROOM_SHIELDS, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    shields.clear();
                    try {
                        JSONArray jsonArray = new JSONArray(msg);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String shile = (String) jsonArray.get(i);
                            shields.add(shile);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 申请上麦的逻辑
     *
     * @param position
     */
    @Override
    public void requestSeat(int position) {
        CurrentStatusType currentStatus = LiveEventHelper.getInstance().getCurrentStatus();
        //获取上麦模式
        LiveEventHelper.getInstance().getRoomInfoByKey(LiveRoomKvKey.LIVE_ROOM_ENTER_SEAT_MODE, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String mode) {
                //&& TextUtils.equals(mode, LiveRoomKvKey.EnterSeatMode.LIVE_ROOM_RequestEnterSeat)
                if (currentStatus == CurrentStatusType.STATUS_WAIT_FOR_SEAT) {
                    showRevokeSeatRequestFragment();
                    return;
                }
                //如果是自由上麦
                if (TextUtils.equals(mode, LiveRoomKvKey.EnterSeatMode.LIVE_ROOM_FreeEnterSeat)) {
                    int index = position;
                    if (index == -1) {
                        for (RCLiveSeatInfo seatInfo : RCLiveEngine.getInstance().getSeatManager().getSeatInfos()) {
                            if (TextUtils.isEmpty(seatInfo.getUserId()) && !seatInfo.isLock()) {
                                //麦位空闲并且没有被锁定
                                index = seatInfo.getIndex();
                                break;
                            }
                        }
                        if (index == -1) {
                            KToast.show("没有空闲的麦位!");
                            return;
                        }
                    }
                    LiveEventHelper.getInstance().enterSeat(index, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            KToast.show(msg);
                            if (result) {
                                //上麦成功
                                mView.changeStatus();
                            }
                        }
                    });
                } else {
                    //如果是申请上麦
                    LiveEventHelper.getInstance().requestLiveVideo(position, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            if (result) {
                                mView.changeStatus();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void setRoomPassword(boolean isPrivate, String password, MutableLiveData<IFun.BaseFun> item, String roomId) {
        int p = isPrivate ? 1 : 0;
        OkApi.put(VRApi.ROOM_PASSWORD,
                new OkParams()
                        .add("roomId", roomId)
                        .add("isPrivate", p)
                        .add("password", password).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            mView.showToast(isPrivate ? "设置成功" : "取消成功");
                            mVoiceRoomBean.setIsPrivate(isPrivate ? 1 : 0);
                            mVoiceRoomBean.setPassword(password);
                            IFun.BaseFun fun = item.getValue();
                            fun.setStatus(p);
                            item.setValue(fun);
                        } else {
                            mView.showToast(isPrivate ? "设置失败" : "取消失败");
                        }
                    }
                });
    }


    @Override
    public void setRoomName(String name, String roomId) {
        OkApi.put(VRApi.ROOM_NAME,
                new OkParams()
                        .add("roomId", roomId)
                        .add("name", name)
                        .build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            KToast.show("修改成功");
                            mVoiceRoomBean.setRoomName(name);
                            LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NAME, name, null);
                        } else {
                            String message = result.getMessage();
                            mView.showToast(!TextUtils.isEmpty(message) ? message : "修改失败");
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mView.showToast(!TextUtils.isEmpty(msg) ? msg : "修改失败");
                    }
                });
    }

    /**
     * 设置上麦的模式
     * true 自由上麦
     * false 申请上麦
     *
     * @param isFreeEnterSeat
     */
    @Override
    public void setSeatMode(boolean isFreeEnterSeat) {
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_ENTER_SEAT_MODE, isFreeEnterSeat ? "1" : "0", new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    if (isFreeEnterSeat) {
                        KToast.show("当前观众可自由上麦");
                    } else {
                        KToast.show("当前观众上麦要申请");
                    }
                } else {
                    KToast.show(msg);
                }

            }
        });
    }

    @Override
    public RoomOwnerType getRoomOwnerType() {
        return roomOwnerType;
    }

    /**
     * 设置当前房间
     *
     * @param mVoiceRoomBean
     * @param isCreate
     */
    @Override
    public void setCurrentRoom(VoiceRoomBean mVoiceRoomBean, boolean isCreate) {
        initLiveRoomListener(mVoiceRoomBean.getRoomId());
        roomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(mVoiceRoomBean);
        if (isInRoom) {
            //恢复一下当前信息就可以了
            List<MessageContent> messageList = LiveEventHelper.getInstance().getMessageList();
            mView.addMessageList(messageList, true);
        } else {
            // 发送默认消息
            sendDefaultMessage();
        }
        //初次进入的时候，获取房间内的人数信息
        MemberCache.getInstance().fetchData(mVoiceRoomBean.getRoomId());
        //设置创建者id
        LiveEventHelper.getInstance().setCreateUserId(mVoiceRoomBean.getCreateUserId());
        //显示直播布局
        mView.showRCLiveVideoView(RCLiveEngine.getInstance().preview());
        getShield();
        getGiftCount(mVoiceRoomBean.getRoomId());
        mView.setRoomData(mVoiceRoomBean);

    }

    /**
     * 设置直播房的各种监听
     *
     * @param roomId
     */
    @Override
    public void initLiveRoomListener(String roomId) {
        setObMessageListener(roomId);
//        setObShieldListener();
        //监听房间里面的人
        MemberCache.getInstance().getMemberList()
                .observe(((LiveRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        mView.setOnlineCount(users.size());
                        LiveEventHelper.getInstance().getRequestLiveVideoIds(null);
                        onInviteLiveVideoIds(users);
                    }
                });
        //监听管理员变化
        MemberCache.getInstance().getAdminList()
                .observe(((LiveRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        mView.refreshMessageList();
                    }
                });
    }

    /**
     * 取消房间界面的各种监听
     */
    @Override
    public void unInitLiveRoomListener() {
        for (Disposable disposable : disposablesManager) {
            disposable.dispose();
        }
        disposablesManager.clear();
        LiveEventHelper.getInstance().removeLiveRoomListeners();
//        EventBus.get().off(UPDATE_SHIELD, null);
    }

    @Override
    public void getGiftCount(String roomId) {
        OkApi.get(VRApi.getGiftList(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    giftMap = result.getMap();
                    Logger.e("================" + giftMap.toString());
                    for (String userId : giftMap.keySet()) {
                        RCLiveSeatInfo seat = SeatManager.get().getSeatByUserId(userId);
                        if (seat != null) {
                            //在麦位上的人,更新麦位上人的礼物
                            RCHolder hold = LiveEventHelper.getInstance().getHold(seat.getIndex());
                            if (hold != null) {
                                View view = hold.rootView();
                                if (view != null) {
                                    TextView tv_gift_count = view.findViewById(R.id.tv_gift_count);
                                    String giftCount = giftMap.get(userId);
                                    tv_gift_count.setText(TextUtils.isEmpty(giftCount) ? "0" : giftCount);
                                }
                            }
//                            SeatManager.get().update(seat.getIndex(), seat, true);
                        }
                        if (TextUtils.equals(userId, mVoiceRoomBean.getCreateUserId())) {
                            //创建者礼物数量
                            mView.setCreateUserGift(giftMap.get(userId));
                        }
                    }
                }
            }
        });
    }

    /**
     * 更改公告信息
     *
     * @param notice 公告内容
     */
    @Override
    public void modifyNotice(String notice) {
        Logger.e(TAG, "notice = " + notice);
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NOTICE, notice, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    TextMessage noticeMsg = TextMessage.obtain("房间公告已更新!");
                    sendMessage(noticeMsg);
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

    /**
     * 送给麦位和房主
     */
    @Override
    public void sendGift() {
        ArrayList<Member> memberArrayList = new ArrayList<>();

        if (PKManager.get().getPkState().isInPk()) {
            memberArrayList.add(Member.fromUser(mVoiceRoomBean.getCreateUser()));
            mView.showSendGiftDialog(mVoiceRoomBean, getCreateUserId(), memberArrayList);
            return;
        }
        //房间内所有人
        List<RCLiveSeatInfo> rcLiveSeatInfos = RCLiveEngine.getInstance().getSeatManager().getSeatInfos();
        for (RCLiveSeatInfo rcLiveSeatInfo : rcLiveSeatInfos) {
            String userId = rcLiveSeatInfo.getUserId();
            if (TextUtils.isEmpty(userId)) {
                //当前麦位没有用户
                continue;
            }
            if (!TextUtils.equals(userId, getCreateUserId())) {
                //当前用户在麦位上,并且不是房主的情况
                User user = MemberCache.getInstance().getMember(userId);
                Member member = null;
                if (user == null) {
                    member = new Member();
                    member.setUserId(userId);
                } else {
                    member = Member.fromUser(user);
                }
                member.setSeatIndex(rcLiveSeatInfo.getIndex());
                memberArrayList.add(member);
            }
        }
        //按照麦位从小到大排序
        Collections.sort(memberArrayList, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getSeatIndex() - o2.getSeatIndex();
            }
        });

        //如果是房主麦位，不用管房主是否存在
        Member member = Member.fromUser(mVoiceRoomBean.getCreateUser());
        memberArrayList.add(0, member);
        mView.showSendGiftDialog(mVoiceRoomBean, getCreateUserId(), memberArrayList);
    }

    /**
     * 准备直播
     *
     * @param roomId
     * @param isCreate
     */
    @Override
    public void prepare(String roomId, boolean isCreate) {
        if (isCreate) {
            //如果是创建房间
            begin(roomId, isCreate);
        } else {
            //如果不是创建房间，而是再次进入房间
            LiveEventHelper.getInstance().prepare(new ClickCallback<Boolean>() {
                @Override
                public void onResult(Boolean result, String msg) {
                    if (result) {
                        begin(roomId, isCreate);
                    }
                }
            });
        }
    }

    /**
     * 开始直播并且加入房间
     *
     * @param roomId
     * @param isCreate
     */
    @Override
    public void begin(String roomId, boolean isCreate) {
        LiveEventHelper.getInstance().begin(roomId, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                mView.dismissLoading();
                if (result) {
                    setCurrentRoom(mVoiceRoomBean, isCreate);
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

    @Override
    public void finishLiveRoom() {
        mView.showLoading("正在关闭房间");
        MusicControlManager.getInstance().release();
        //房主关闭房间，调用删除房间接口
        OkApi.get(VRApi.deleteRoom(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                mView.dismissLoading();
                mView.finish();
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                mView.dismissLoading();
                mView.showToast(msg);
            }
        });
    }

    @Override
    public void leaveLiveRoom(ClickCallback callBack) {
        mView.showLoading("正在离开当前房间");
        LiveEventHelper.getInstance().leaveRoom(new IRoomCallBack() {
            @Override
            public void onSuccess() {
                mView.dismissLoading();
                mView.finish();
                if (callBack != null)
                    callBack.onResult(true, "成功");
            }

            @Override
            public void onError(int code, String message) {
                mView.dismissLoading();
                KToast.show(message);
            }
        });
    }

    /**
     * 监听自己删除或者添加屏蔽词
     *
     * @param shieldArrayList
     */
    public void updateShield(List<Shield> shieldArrayList) {
        this.shields.clear();
        for (Shield shield : shieldArrayList) {
            if (!shield.isDefault()) {
                //说明是正常的屏蔽词
                this.shields.add(shield.getName());
            }
        }
        JsonArray jsonElements = new JsonArray();
        for (String shield : this.shields) {
            jsonElements.add(shield);
        }
        //发送KV消息
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_SHIELDS, jsonElements.toString(), null);
    }


    /**
     * 监听接收房间的所有信息
     *
     * @param roomId
     */
    private void setObMessageListener(String roomId) {
        disposablesManager.add(RCChatRoomMessageManager.
                obMessageReceiveByRoomId(roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageContent>() {
                    @Override
                    public void accept(MessageContent messageContent) throws Throwable {
                        //将消息显示到列表上
                        if (LiveEventHelper.getInstance().isShowingMessage(messageContent)) {
                            if (null != mView)
                                mView.addMessageContent(messageContent, false);
                        }
                        if (messageContent instanceof RCChatroomGift || messageContent instanceof RCChatroomGiftAll) {
                            getGiftCount(roomId);
                        } else if (messageContent instanceof RCChatroomLike) {
                            if (null != mView) mView.showLikeAnimation();
                        } else if (messageContent instanceof RCAllBroadcastMessage) {
                            AllBroadcastManager.getInstance().addMessage((RCAllBroadcastMessage) messageContent);
                        } else if (messageContent instanceof RCChatroomSeats) {
//                            refreshRoomMember();
                        } else if (messageContent instanceof RCChatroomAdmin) {
                            MemberCache.getInstance().refreshAdminData(mVoiceRoomBean.getRoomId());
                        } else if (messageContent instanceof CommandMessage) {
                            boolean show = !TextUtils.isEmpty(((CommandMessage) messageContent).getData());
                            refreshMusicView(show);
                        }
                    }
                }));
    }

    /**
     * 发送消息
     * 默认显示在本地
     *
     * @param messageContent
     */
    @Override
    public void sendMessage(MessageContent messageContent) {
        sendMessage(messageContent, true);
    }

    /**
     * 发送消息
     *
     * @param messageContent 消息体
     * @param isShowLocation 是否显示在本地
     */
    @Override
    public void sendMessage(MessageContent messageContent, boolean isShowLocation) {
        if (!isContainsShield(messageContent))
            LiveEventHelper.getInstance().sendMessage(messageContent, isShowLocation);
    }

    /**
     * 是否包含屏蔽词
     *
     * @return
     */
    private boolean isContainsShield(MessageContent messageContent) {
        boolean isContains = false;
        if (shields != null) {
            for (String shield : shields) {
                if (messageContent instanceof RCChatroomBarrage) {
                    if (((RCChatroomBarrage) messageContent).getContent().contains(shield)) {
                        isContains = true;
                        break;
                    }
                }
            }
            if (isContains) {
                //如果是包含了敏感词'
                mView.addMessageContent(messageContent, false);
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新音乐播放的小窗UI
     *
     * @param show 是否显示
     */
    private void refreshMusicView(boolean show) {
        if (show) {
            MusicApi.getPlayingMusic(getRoomId(), new IResultBack<MusicBean>() {
                @Override
                public void onResult(MusicBean musicBean) {
                    if (musicBean != null) {
                        mView.refreshMusicView(true, musicBean.getNameAndAuthor(), musicBean.getBackgroundUrl());
                    } else {
                        mView.refreshMusicView(false, "", "");
                    }
                }
            });
        } else {
            mView.refreshMusicView(false, "", "");
        }
    }

    /**
     * 界面销毁，取消房间的各种监听,但是不代表取消房间的事件监听
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置管理员
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickSettingAdmin(User user, ClickCallback<Boolean> callback) {
        if (mVoiceRoomBean == null) {
            return;
        }
        boolean isAdmin = !MemberCache.getInstance().isAdmin(user.getUserId());
        HashMap<String, Object> params = new OkParams()
                .add("roomId", mVoiceRoomBean.getRoomId())
                .add("userId", user.getUserId())
                .add("isManage", isAdmin)
                .build();
        // 先请求 设置/取消 管理员
        OkApi.put(VRApi.ADMIN_MANAGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    RCChatroomAdmin admin = new RCChatroomAdmin();
                    admin.setAdmin(isAdmin);
                    admin.setUserId(user.getUserId());
                    admin.setUserName(user.getUserName());
                    // 成功后发送管理变更的消息
                    sendMessage(admin);
                    callback.onResult(true, "");
                } else {
                    KToast.show(result.getMessage());
                    callback.onResult(true, result.getMessage());
                }
            }
        });
    }

    /**
     * 踢出房间
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().kickUserFromRoom(user, callback);
    }

    /**
     * 点击发送礼物
     *
     * @param user
     */
    @Override
    public void clickSendGift(User user) {
        Member member = Member.fromUser(user);
        int index = SeatManager.get().getIndexByUserId(user.getUserId());
        member.setSeatIndex(index);
        mView.showSendGiftDialog(mVoiceRoomBean, user.getUserId(), Arrays.asList(member));
    }

    /**
     * 关注
     *
     * @param isFollow
     * @param followMsg
     */
    @Override
    public void clickFollow(boolean isFollow, RCFollowMsg followMsg) {
        if (isFollow) {
            sendMessage(followMsg);
        }
        mView.setTitleFollow(isFollow);
    }

    /**
     * 进入房间后发送默认的消息
     */
    private void sendDefaultMessage() {
        if (mVoiceRoomBean != null) {
            mView.addMessageContent(null, true);
            // 默认消息
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("欢迎来到 %s", mVoiceRoomBean.getRoomName()));
            sendMessage(welcome);
            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("感谢使用融云 RTC 视频直播，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。");
            sendMessage(tips);
        }
    }

    /**
     * 邀请上麦
     *
     * @param seatIndex
     * @param user
     * @param callback
     */
    @Override
    public void clickInviteSeat(int seatIndex, User user, ClickCallback<Boolean> callback) {
        if (!PKManager.get().getPkState().enableAction()) {
            return;
        }
        LiveEventHelper.getInstance().pickUserToSeat(user.getUserId(), seatIndex, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                callback.onResult(result, msg);
                if (TextUtils.equals(getCreateUserId(), UserManager.get().getUserId())) {
                    mView.changeSeatOrder();
                }
            }
        });
    }

    /**
     * 同意上麦
     *
     * @param userId
     * @param callback
     */
    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().acceptRequestSeat(userId, callback);
    }

    /**
     * 拒绝上麦申请
     *
     * @param userId
     * @param callback
     */
    @Override
    public void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().rejectRequestSeat(userId, callback);
    }

    /**
     * 撤销申请
     *
     * @param callback
     */
    @Override
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().cancelRequestSeat(new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (callback != null) callback.onResult(result, msg);
                if (result) {
                    mView.changeStatus();
                    KToast.show("已撤回连线申请");
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

    @Override
    public void cancelInvitation(String userId, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().cancelInvitation(userId, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (callback != null) {
                    callback.onResult(result, msg);
                }
                mView.changeSeatOrder();
            }
        });
    }

    /**
     * 踢下麦
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().kickUserFromSeat(user, callback);
    }

    /**
     * 开麦或禁麦
     *
     * @param seatIndex
     * @param isMute
     * @param callback
     */
    @Override
    public void clickMuteSeat(int seatIndex, boolean isMute, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().muteSeat(seatIndex, isMute, callback);
    }

    /**
     * 关闭座位或者打开座位
     *
     * @param seatIndex
     * @param isLock
     * @param callback
     */
    @Override
    public void clickCloseSeat(int seatIndex, boolean isLock, ClickCallback<Boolean> callback) {
        if (RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
            KToast.show("默认模式不支持关闭座位");
            return;
        }
        LiveEventHelper.getInstance().lockSeat(seatIndex, isLock, callback);
    }

    /**
     * 切换麦位
     *
     * @param seatIndex
     * @param callback
     */
    @Override
    public void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {
        RCLiveSeatInfo rcLiveSeatInfo = RCLiveEngine.getInstance().getSeatManager().getSeatInfos().get(seatIndex);
        if (rcLiveSeatInfo != null) {
            if (rcLiveSeatInfo.isLock()) {
                KToast.show("麦位已上锁！");
                return;
            }
        }
        LiveEventHelper.getInstance().switchToSeat(seatIndex, callback);
    }

    /**
     * 邀请弹窗
     *
     * @param index
     */
    @Override
    public void showSeatOperationViewPagerFragment(int index, int seatIndex) {
        LiveRoomLayoutSettingFragment liveRoomLayoutSettingFragment = new LiveRoomLayoutSettingFragment();
        liveRoomLayoutSettingFragment.setLiveLayoutSettingCallBack(this);
        seatOperationViewPagerFragment = new SeatOperationViewPagerFragment(getRoomOwnerType(), liveRoomLayoutSettingFragment);
        seatOperationViewPagerFragment.setIndex(index);
        seatOperationViewPagerFragment.setRequestSeats(requestSeats);
        seatOperationViewPagerFragment.setInviteSeats(inviteSeats);
        seatOperationViewPagerFragment.setInviteSeatIndex(seatIndex);
        seatOperationViewPagerFragment.setSeatActionClickListener(LiveRoomPresenter.this);
        seatOperationViewPagerFragment.show(mView.getLiveFragmentManager());
        MemberCache.getInstance().fetchData(mVoiceRoomBean.getRoomId());
    }

    /**
     * 撤销弹窗
     */
    @Override
    public void showRevokeSeatRequestFragment() {
        RevokeSeatRequestFragment revokeSeatRequestFragment = new RevokeSeatRequestFragment();
        revokeSeatRequestFragment.setSeatActionClickListener(this);
        revokeSeatRequestFragment.show(mView.getLiveFragmentManager());
    }

    @Override
    public void onRoomInfoReady() {

    }

    @Override
    public void onRoomInfoUpdate(String key, String value) {
        switch (key) {
            case LiveRoomKvKey.LIVE_ROOM_NAME://房间名
                mVoiceRoomBean.setRoomName(value);
                break;
            case LiveRoomKvKey.LIVE_ROOM_NOTICE://房间公告
                mView.setNotice(value);
                break;
            case LiveRoomKvKey.LIVE_ROOM_SHIELDS://房间屏蔽词
                getShield();
                break;
        }
    }

    /**
     * 有人加入房间
     *
     * @param userId 用户在融云服务的唯一标识，注意：和自己的业务数据userId可能不是同一个字段
     */
    @Override
    public void onUserEnter(String userId, int onlineCount) {
        mView.setOnlineCount(onlineCount);
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * 有人退出房间
     *
     * @param userId 用户在融云服务的唯一标识，注意：和自己的业务数据userId可能不是同一个字段
     */
    @Override
    public void onUserExit(String userId, int onlineCount) {
//        mView.setOnlineCount(onlineCount);
//        MemberCache.getInstance().refreshMemberData(getRoomId());
        UIKit.postDelayed(new Runnable() {
            @Override
            public void run() {
                MemberCache.getInstance().refreshMemberData(getRoomId(), new ClickCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean result, String msg) {
                        if (result) {
                            MutableLiveData<List<User>> list = MemberCache.getInstance().getMemberList();
                            mView.setOnlineCount(list.getValue().size());
                        }
                    }
                });
            }
        }, 500);
    }

    /**
     * 被踢出去
     *
     * @param userId     被踢用户唯一标识
     * @param operatorId 踢人操作的执行用户的唯一标识
     */
    @Override
    public void onUserKickOut(String userId, String operatorId) {
        if (TextUtils.equals(userId, UserManager.get().getUserId())) {
            mView.finish();
            unInitLiveRoomListener();
        }
    }

    @Override
    public void onLiveVideoUpdate(List<String> lineMicUserIds) {
        if (TextUtils.equals(getCreateUserId(), UserManager.get().getUserId())) {
            //如果是房主，那么更新
            mView.changeSeatOrder();
        }
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * 有人申请了麦位，这个时候需要去请求房间里面人得信息，不然拿不到
     */
    @Override
    public void onLiveVideoRequestChange() {
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * 请求上麦被允许
     */
    @Override
    public void onLiveVideoRequestAccepted() {
        mView.changeStatus();
    }

    /**
     * 请求上麦被拒绝
     */
    @Override
    public void onLiveVideoRequestRejected() {
        mView.changeStatus();
    }


    @Override
    public void onLiveVideoRequestCanceled() {

    }

    @Override
    public void onLiveVideoInvitationReceived(String userId, int index) {

    }

    @Override
    public void onLiveVideoInvitationCanceled() {

    }

    @Override
    public void onLiveVideoInvitationAccepted(String userId) {

    }

    @Override
    public void onLiveVideoInvitationRejected(String userId) {
        mView.changeSeatOrder();
    }

    @Override
    public void onLiveVideoStarted() {
        mView.changeStatus();
    }


    @Override
    public void onLiveVideoStopped(RCLivevideoFinishReason reason) {
        mView.changeStatus();
    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    @Override
    public void onNetworkStatus(long delayMs) {
        mView.showNetWorkStatus(delayMs);
    }

    @Override
    public void onOutputSampleBuffer(RCRTCVideoFrame frame) {

    }

    @Override
    public void onFirstRemoteVideoFrame(String userId, String tag) {

    }

    @Override
    public void onReportFirstFrame(RCRTCInputStream stream, RCRTCMediaType mediaType) {

    }

    @Override
    public RCRTCConfig.Builder onInitRCRTCConfig(RCRTCConfig.Builder builder) {
        return null;
    }

    @Override
    public RCRTCMixConfig onInitMixConfig(RCRTCMixConfig rcrtcMixConfig) {
        return null;
    }

    /**
     * 当布局发生了改变的时候
     *
     * @param mixType      合流类型
     * @param customerType 自定义布局
     */
    @Override
    public void onRoomMixTypeChange(RCLiveMixType mixType, int customerType) {
        RCLiveView videoView = RCLiveEngine.getInstance().preview();
        if (mixType == RCLiveMixType.RCMixTypeOneToOne) {
            //如果是1V1的时候
            videoView.setDevTop(0);
        } else {
            videoView.setDevTop(mView.getMarginTop());
        }
        mView.changeMessageContainerHeight();
    }


    /**
     * 房间销毁
     */
    @Override
    public void onRoomDestroy() {
        refreshMusicView(false);
        mView.finish();
    }


    @Override
    public void onSendGiftSuccess(List<MessageContent> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (MessageContent message : messages) {
                sendMessage(message);
            }
            getGiftCount(mVoiceRoomBean.getRoomId());
        }
    }

    public String getRoomId() {
        if (mVoiceRoomBean != null) {
            return mVoiceRoomBean.getRoomId();
        }
        return "";
    }

    public String getCreateUserId() {
        if (mVoiceRoomBean != null) {
            return mVoiceRoomBean.getCreateUserId();
        }
        return "";
    }

    public User getCreateUser() {
        if (mVoiceRoomBean != null) {
            return mVoiceRoomBean.getCreateUser();
        }
        return null;
    }

    public String getRoomName() {
        if (mVoiceRoomBean != null) {
            return mVoiceRoomBean.getRoomName();
        }
        return "";
    }

    @Override
    public void clickSendMessage(String message) {
        //发送文字消息
        RCChatroomBarrage barrage = new RCChatroomBarrage();
        barrage.setContent(message);
        barrage.setUserId(UserManager.get().getUserId());
        barrage.setUserName(UserManager.get().getUserName());
        sendMessage(barrage);
    }

    @Override
    public void clickPrivateMessage() {
        RouteUtils.routeToSubConversationListActivity(
                mView.getLiveActivity(),
                Conversation.ConversationType.PRIVATE,
                "消息"
        );
    }

    @Override
    public void clickSeatOrder() {
        if (!PKManager.get().getPkState().enableAction()) {
            return;
        }
        if (LiveEventHelper.getInstance().getInviteStatusType() == InviteStatusType.STATUS_CONNECTTING) {
            for (String inSeatUserId : SeatManager.get().getInSeatUserIds()) {
                if (!TextUtils.equals(inSeatUserId, getCreateUserId())) {
                    mView.showHangUpFragment(inSeatUserId);
                    continue;
                }
            }
        } else if (LiveEventHelper.getInstance().getInviteStatusType() == InviteStatusType.STATUS_UNDER_INVITATION) {
            for (String inSeatUserId : RCDataManager.get().getInvitateIds()) {
                if (!TextUtils.equals(inSeatUserId, getCreateUserId())) {
                    mView.showUninviteVideoFragment(inSeatUserId);
                    continue;
                }
            }
        } else {
            showSeatOperationViewPagerFragment(0, -1);
        }
    }

    @Override
    public void clickSettings() {
        LiveEventHelper.getInstance().getRoomInfoByKey(LiveRoomKvKey.LIVE_ROOM_ENTER_SEAT_MODE, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String mode) {
                if (result) {
                    List<MutableLiveData<IFun.BaseFun>> funList = Arrays.asList(
                            new MutableLiveData<>(new RoomLockFun(mVoiceRoomBean.isPrivate() ? 1 : 0)),
                            new MutableLiveData<>(new RoomNameFun(0)),
                            new MutableLiveData<>(new RoomNoticeFun(0)),
                            new MutableLiveData<>(new RoomShieldFun(0)),
                            new MutableLiveData<>(new RoomOverTurnFun(0)),
                            new MutableLiveData<>(new RoomTagsFun(0)),
                            new MutableLiveData<>(new RoomBeautyFun(0)),
                            new MutableLiveData<>(new RoomBeautyMakeUpFun(0)),
                            new MutableLiveData<>(new RoomSpecialEffectsFun(0)),
                            new MutableLiveData<>(new RoomSeatModeFun
                                    (TextUtils.equals(mode, LiveRoomKvKey.EnterSeatMode.LIVE_ROOM_FreeEnterSeat) ? 1 : 0)),
                            new MutableLiveData<>(new RoomMusicFun(0)),
                            new MutableLiveData<>(new RoomVideoSetFun(0))
                    );
                    mView.showRoomSettingFragment(funList);
                }
            }
        });
    }

    @Override
    public void clickPk() {
        PKState pkState = PKManager.get().getPkState();
        int mixType = RCDataManager.get().getMixType();
        // 非默认或2分屏 且 不是邀请状态 ，可点击
        if ((mixType == RCLiveMixType.RCMixTypeOneToOne.getValue()
                || mixType == RCLiveMixType.RCMixTypeGridTwo.getValue())
                && LiveEventHelper.getInstance().getInviteStatusType() == InviteStatusType.STATUS_NOT_INVITRED) {
            if (pkState.isNotInPk()) {
                // 没有在pk 只有在默认情况下能点击
                if (mixType == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                    PKManager.get().showPkInvitation((Activity) mView.getLiveActivity());
                } else {
                    KToast.show("连麦时不能发起PK");
                }
            } else if (pkState.isInInviting()) {
                PKManager.get().showCancelPkInvitation((Activity) mView.getLiveActivity());
            } else if (pkState.isInPk()) {// pk中
                PKManager.get().showQuitPK((Activity) mView.getLiveActivity());
            }
        } else {
            KToast.show("连麦时不能发起PK");
        }
    }

    @Override
    public void clickRequestSeat() {
        if (!PKManager.get().getPkState().enableAction()) {
            return;
        }
        if (LiveEventHelper.getInstance().getCurrentStatus() == CurrentStatusType.STATUS_ON_SEAT) {
            //如果当前在麦位上，那么弹出可以断开链接的弹窗
            for (RCLiveSeatInfo seatInfo : RCLiveEngine.getInstance().getSeatManager().getSeatInfos()) {
                if (TextUtils.equals(seatInfo.getUserId(), RongCoreClient.getInstance().getCurrentUserId())) {
                    mView.showCreatorSettingFragment(seatInfo);
                    return;
                }
            }
        }
        requestSeat(-1);
    }

    @Override
    public void onSendGift() {
        sendGift();
    }

    @Override
    public void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice) {
        sendMessage(rcChatroomVoice);
    }

    /**
     * TODO 麦克风被占用的前提下
     * 1、如果不在麦位上，不可以发送
     * 2、如果在麦位上,但是没有被禁麦，也不可以发送
     *
     * @return
     */
    @Override
    public boolean canSend() {
        //不在麦位上，或者在麦位上但是没有被禁麦
        RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(UserManager.get().getUserId());
        if (rcLiveSeatInfo == null || (rcLiveSeatInfo != null && !rcLiveSeatInfo.isMute())) {
            return false;
        }
        return true;
    }


    /**
     * 正在申请上麦的用户
     * 不在房间的用户和已经在麦位上的用户  不显示
     *
     * @param requestLives
     */
    @Override
    public void onRequestLiveVideoIds(List<String> requestLives) {
        requestSeats.clear();
        for (String userId : requestLives) {
            RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(userId);
            if (rcLiveSeatInfo == null) {
                User user = MemberCache.getInstance().getMember(userId);
                requestSeats.add(user);
            }
        }
        if (mView != null)
            mView.showUnReadRequestNumber(requestSeats.size());
        if (seatOperationViewPagerFragment != null) {
            seatOperationViewPagerFragment.setRequestSeats(requestSeats);
        }
        if (getRoomOwnerType() == RoomOwnerType.LIVE_VIEWER) {
            if (CurrentStatusType.STATUS_ON_SEAT != LiveEventHelper.getInstance().getCurrentStatus()) {
                for (String userId : requestLives) {
                    if (TextUtils.equals(userId, UserManager.get().getUserId())) {

                        LiveEventHelper.getInstance().setCurrentStatus(CurrentStatusType.STATUS_WAIT_FOR_SEAT);
                        break;
                    }
                }
            }
            if (mView != null) mView.changeStatus();
        }
    }

    /**
     * 可以被邀请的用户
     * 在房间里面  并且不在麦位上
     *
     * @param roomUsers
     */
    @Override
    public void onInviteLiveVideoIds(List<User> roomUsers) {
        inviteSeats.clear();
        for (User roomUser : roomUsers) {
            RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(roomUser.getUserId());
            if (rcLiveSeatInfo == null) inviteSeats.add(roomUser);
        }
        if (seatOperationViewPagerFragment != null) {
            seatOperationViewPagerFragment.setInviteSeats(inviteSeats);
        }
    }

    /**
     * 布局修改
     *
     * @param rcLiveMixType
     */
    @Override
    public void setupMixType(RCLiveMixType rcLiveMixType) {
        Log.e(TAG, "onRCMixLayoutChange: " + rcLiveMixType);
        RCLiveView videoView = RCLiveEngine.getInstance().preview();
        if (rcLiveMixType == RCLiveMixType.RCMixTypeOneToOne) {
            //如果是1V1的时候
            videoView.setDevTop(0);
        } else {
            videoView.setDevTop(mView.getMarginTop());
        }
        RCLiveEngine.getInstance().setMixType(rcLiveMixType, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                TextMessage textMessage = TextMessage.obtain("麦位布局已修改，请重新上麦");
                textMessage.setExtra("mixTypeChange");
                sendMessage(textMessage);
            }

            @Override
            public void onError(int code, RCLiveError error) {
                Log.e(TAG, "onError: " + error.getMessage());
            }
        });
        mView.changeMessageContainerHeight();
    }


    /**
     * 构建布局
     *
     * @param rcParamter
     * @return
     */
    private View createEmptyMicLayout(RCParamter rcParamter) {
        int seatHeight = rcParamter.getHeight();
        int seatWidth = rcParamter.getWidth();
        View view = LayoutInflater.from(mView.getLiveActivity()).inflate(R.layout.item_live_seat_offline_layout, null);
        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(seatWidth, seatHeight);
        view.setLayoutParams(layoutParams);
        return view;
    }

    /**
     * 构建连麦布局
     *
     * @param rcParamter
     */
    private View createMicLayout(RCParamter rcParamter) {
        int seatHeight = rcParamter.getHeight();
        int seatWidth = rcParamter.getWidth();
        View view = LayoutInflater.from(mView.getLiveActivity()).inflate(R.layout.item_live_seat_online_layout, null);
        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(seatWidth, seatHeight);
        view.setLayoutParams(layoutParams);
        ImageView imageView = view.findViewById(R.id.iv_room_creator_portrait);
        WaveView waveView = view.findViewById(R.id.wv_creator_background);
        int width = seatWidth * 2 / 5 > ScreenUtil.getScreenWidth() / 6 ? ScreenUtil.getScreenWidth() / 6 : seatWidth * 2 / 5;
        setViewLayoutParams(imageView, width, width);
        setViewLayoutParams(waveView, seatWidth, seatHeight);
        return view;
    }


    /**
     * 重设 view 的宽高
     */
    public static void setViewLayoutParams(View view, int nWidth, int nHeight) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != nHeight || lp.width != nWidth) {
            lp.width = nWidth;
            lp.height = nHeight;
            view.setLayoutParams(lp);
        }
    }

    @Override
    public void onSeatLocked(RCLiveSeatInfo seatInfo, boolean locked) {

    }

    @Override
    public void onSeatMute(RCLiveSeatInfo seatInfo, boolean mute) {

    }

    @Override
    public void onSeatAudioEnable(RCLiveSeatInfo seatInfo, boolean enable) {

    }

    @Override
    public void onSeatVideoEnable(RCLiveSeatInfo seatInfo, boolean enable) {

    }

    /**
     * 切换音视频
     *
     * @param index
     * @param isVideo
     */
    @Override
    public void clickSwitchLinkStatus(int index, boolean isVideo) {
        LiveEventHelper.getInstance().switchVideoOrAudio(index, isVideo, null);
    }

    /**
     * 当获取了焦点的时候，如果是房主，并且是视频模式，重复打开一下相机，避免出现相机被占用的情况
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getRoomOwnerType() == RoomOwnerType.LIVE_OWNER) {
            RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(UserManager.get().getUserId());
            if (rcLiveSeatInfo != null && rcLiveSeatInfo.isEnableVideo()) {
                RCRTCCameraOutputStream defaultVideoStream = RCRTCEngine.getInstance().getDefaultVideoStream();
                if (defaultVideoStream != null) defaultVideoStream.startCamera(null);
            }
        }
    }

    /**
     * 开关本地麦克风
     *
     * @param index
     * @param isMute
     */
    @Override
    public void clickMuteSelf(int index, boolean isMute) {
        LiveEventHelper.getInstance().MuteSelf(index, isMute, null);
    }

    /**
     * 断开链接
     *
     * @param index
     */
    @Override
    public void disConnect(int index) {
        LiveEventHelper.getInstance().leaveSeat(new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                KToast.show(msg);
            }
        });
    }

    /**
     * 生成布局
     *
     * @param seatInfo
     * @param rcParameter
     * @return
     */
    @Override
    public View inflaterSeatView(RCLiveSeatInfo seatInfo, RCParamter rcParameter) {
        String userId = seatInfo.getUserId();
        Log.e(TAG, "setSeatViewProvider: width" + rcParameter.getWidth());
        Log.e(TAG, "setSeatViewProvider: heigth" + rcParameter.getHeight());
        View view;
        //麦位为默认的1V1的时候
        if (!TextUtils.isEmpty(userId)) {
            if (TextUtils.equals(userId, getCreateUserId()) && rcParameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //如果是1V1，而且当前是房主，也不应该显示视图
                view = null;
            } else {
                view = createMicLayout(rcParameter);
                if (PKManager.get().getPkState().isInPk()) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        } else {
            // 麦位没人的时候
            if (rcParameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //如果当前是1V1，那么小视图应该不显示覆盖
                view = null;
            } else {
                view = createEmptyMicLayout(rcParameter);
            }
        }
        return view;
    }

    /**
     * 刷新视图的信息
     *
     * @param holder    麦位布局
     * @param seatInfo  麦位信息
     * @param parameter 当前麦位布局的参数信息
     */
    @Override
    public void onBindView(RCHolder holder, RCLiveSeatInfo seatInfo, RCParamter parameter) {
        if (holder != null) {
            View view = holder.rootView();
            if (TextUtils.isEmpty(seatInfo.getUserId()) && view != null) {
                //如果麦位为空的话，那么应该是空布局
                TextView name = view.findViewById(R.id.tv_member_name);
                name.setText(seatInfo.getIndex() + "号麦位");
                ImageView isMuteView = view.findViewById(R.id.iv_is_mute);
                if (seatInfo.isMute() && PKManager.get().getPkState().isNotInPk()) {
                    isMuteView.setVisibility(View.VISIBLE);
                } else {
                    isMuteView.setVisibility(View.GONE);
                }
                ImageView seatStatusView = view.findViewById(R.id.iv_seat_status);
                if (seatInfo.isLock()) {
                    name.setVisibility(View.GONE);
                    seatStatusView.setImageResource(R.drawable.ic_seat_status_locked);
                } else {
                    name.setVisibility(View.VISIBLE);
                    seatStatusView.setImageResource(R.drawable.ic_seat_status_enter);
                }
            } else if (!TextUtils.isEmpty(seatInfo.getUserId()) && view != null) {
                //有人在麦位上的布局
                TextView name = view.findViewById(R.id.tv_member_name);
                view.setBackground(parameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue() ? null : mView.getLiveActivity().getDrawable(R.drawable.shape_live_seat_online_bg));
                RelativeLayout rl_mic_audio_value = view.findViewById(R.id.rl_mic_audio_value);
                ImageView imageView = view.findViewById(R.id.iv_room_creator_portrait);
                //根据麦位信息来判断是否开启还是关闭动画效果
                ImageView ivBackGroup = view.findViewById(R.id.iv_background);
                TextView tv_gift_count = view.findViewById(R.id.tv_gift_count);

                if (TextUtils.equals(seatInfo.getUserId(), getCreateUserId())) {
                    tv_gift_count.setVisibility(View.GONE);
                    name.setVisibility(parameter.getMixType() == RCLiveMixType.RCMixTypeOneToSix.getValue() ? View.GONE : View.VISIBLE);
                } else {
                    tv_gift_count.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    if (giftMap != null) {
                        String giftCount = giftMap.get(seatInfo.getUserId());
                        tv_gift_count.setText(TextUtils.isEmpty(giftCount) ? "0" : giftCount);
                    }
                }
                ImageView isMuteView = view.findViewById(R.id.iv_is_mute);
                if (seatInfo.isMute() && PKManager.get().getPkState().isNotInPk()) {
                    isMuteView.setVisibility(View.VISIBLE);
                } else {
                    isMuteView.setVisibility(View.GONE);
                }
                if (PKManager.get().getPkState().isInPk() && !TextUtils.equals(mVoiceRoomBean.getCreateUserId(), seatInfo.getUserId())) {
                    PKManager.get().mutePkView(seatInfo.isMute());
                }
                UserProvider.provider().getAsyn(seatInfo.getUserId(), new IResultBack<UserInfo>() {
                    @Override
                    public void onResult(UserInfo userInfo) {
                        name.setText(userInfo.getName());
                        ImageLoader.loadUrl(imageView, userInfo.getPortraitUri().toString(), R.drawable.default_portrait);
                        if (seatInfo.isEnableVideo()) {
                            //视频连线开启的时候
                            rl_mic_audio_value.setVisibility(View.GONE);
                            ivBackGroup.setVisibility(View.GONE);
                        } else {
                            //当前位音频连线
                            rl_mic_audio_value.setVisibility(View.VISIBLE);
                            ivBackGroup.setVisibility(View.VISIBLE);
                            Glide.with(mView.getLiveActivity()).load(userInfo.getPortraitUri())
                                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(1, 25)))
                                    .into(ivBackGroup);
                        }
                    }
                });
            }

            if (view != null) {
                //设置点击事件
                setClickSeatListener(view, seatInfo);
            }
        }
    }

    /**
     * 点击麦位
     *
     * @param view
     * @param seatInfo
     */
    private void setClickSeatListener(View view, RCLiveSeatInfo seatInfo) {
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        onClickLiveRoomSeats(seatInfo);
                    }
                });
    }

    /**
     * 麦位被点击的情况
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeats(RCLiveSeatInfo rcLiveSeatInfo) {
        switch (getRoomOwnerType()) {
            case LIVE_OWNER://房主
                onClickLiveRoomSeatsByOwner(rcLiveSeatInfo);
                break;
            case LIVE_VIEWER://观众
                onClickLiveRoomSeatsByViewer(rcLiveSeatInfo);
                break;
        }

    }

    /**
     * 观众点击麦位的时候
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeatsByViewer(RCLiveSeatInfo rcLiveSeatInfo) {
        //点击的是空麦位
        if (TextUtils.isEmpty(rcLiveSeatInfo.getUserId())) {
            if (rcLiveSeatInfo.isLock()) {
                //麦位被锁定
                KToast.show("该座位已锁定");
                return;
            }
            if (LiveEventHelper.getInstance().getCurrentStatus() != CurrentStatusType.STATUS_ON_SEAT) {
                //如果当前用户不在麦位上,那么去申请麦位
                requestSeat(rcLiveSeatInfo.getIndex());
            } else {
                //切换麦位
                LiveEventHelper.getInstance().switchToSeat(rcLiveSeatInfo.getIndex(), null);
            }
        } else {
            //麦位有人
            if (TextUtils.equals(rcLiveSeatInfo.getUserId(), RongCoreClient.getInstance().getCurrentUserId())) {
                //观众端-点击自己的麦位
                mView.showCreatorSettingFragment(rcLiveSeatInfo);
            } else {
                if (PKManager.get().getPkState().isInPk()) {
                    return;
                }
                //观众端-点击的他人的麦位
                mView.showMemberSettingFragment(rcLiveSeatInfo.getUserId());
            }
        }
    }

    /**
     * 房主点击麦位的时候
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeatsByOwner(RCLiveSeatInfo rcLiveSeatInfo) {
        //如果当前麦位是空的或者被锁的时候
        if (TextUtils.isEmpty(rcLiveSeatInfo.getUserId())) {
            if (emptySeatFragment == null) {
                emptySeatFragment = new EmptySeatFragment();
            }
            int seatStatus = rcLiveSeatInfo.isLock() ? 1 : 0;
            emptySeatFragment.setData(rcLiveSeatInfo.getIndex(), seatStatus, rcLiveSeatInfo.isMute(), this);
            emptySeatFragment.setShowSwitchSeatBtn(RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeGridNine.getValue());
            emptySeatFragment.setSeatActionClickListener(this);
            emptySeatFragment.show(mView.getLiveFragmentManager());
        } else {
            //麦位有人的时候
            if (TextUtils.equals(rcLiveSeatInfo.getUserId(), getCreateUserId())) {
                //主播端-点击自己的麦位
                mView.showCreatorSettingFragment(rcLiveSeatInfo);
            } else {
                if (PKManager.get().getPkState().isInPk()) {
                    return;
                }
                //主播端-点击的他人的麦位
                mView.showMemberSettingFragment(rcLiveSeatInfo.getUserId());
            }
        }
    }


    /**
     * 音量
     *
     * @param seatInfo
     * @param audioLevel 音量
     */
    @Override
    public void onSeatSpeak(RCLiveSeatInfo seatInfo, int audioLevel) {
//        Log.e(TAG, "onSeatSpeak: seatIndex :" + seatInfo.getIndex() + "seatAudio:" + audioLevel);
        RCHolder hold = LiveEventHelper.getInstance().getHold(seatInfo.getIndex());
        if (hold != null) {
            WaveView waveView = hold.getView(R.id.wv_creator_background);
            if (waveView != null) {
                if (audioLevel > 0 && !seatInfo.isMute()) {
                    waveView.start();
                } else {
                    waveView.stop();
                }
            }
        }
    }

    /**
     * 更新分辨率KV
     *
     * @param resolution 分辨率
     */
    @Override
    public void updateVideoResolution(RCRTCParamsType.RCRTCVideoResolution resolution) {
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_VIDEO_RESOLUTION, resolution.getLabel(), null);
    }

    /**
     * 更新帧率KV
     *
     * @param fps 帧率
     */
    @Override
    public void updateVideoFps(RCRTCParamsType.RCRTCVideoFps fps) {
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_VIDEO_FPS, fps.getFps() + "", null);
    }


    /**
     * 点击全局广播后跳转到相应的房间
     *
     * @param message
     */
    public void jumpRoom(RCAllBroadcastMessage message) {
        // 当前房间不跳转
        if (message == null || TextUtils.isEmpty(message.getRoomId()) || TextUtils.equals(message.getRoomId(), getRoomId())
                || LiveEventHelper.getInstance().getCurrentStatus() == CurrentStatusType.STATUS_ON_SEAT
                || TextUtils.equals(UserManager.get().getUserId(), getCreateUserId()))
            return;
        OkApi.get(VRApi.getRoomInfo(message.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        // 房间有密码需要弹框验证密码
                        if (roomBean.isPrivate()) {
                            inputPasswordDialog = new InputPasswordDialog(mView.getLiveActivity(), false, new InputPasswordDialog.OnClickListener() {
                                @Override
                                public void clickCancel() {
                                    inputPasswordDialog.dismiss();
                                }

                                @Override
                                public void clickConfirm(String password) {

                                }
                            });
                            inputPasswordDialog = new InputPasswordDialog(mView.getLiveActivity(), false, new InputPasswordDialog.OnClickListener() {
                                @Override
                                public void clickCancel() {
                                    inputPasswordDialog.dismiss();
                                }

                                @Override
                                public void clickConfirm(String password) {
                                    if (TextUtils.isEmpty(password)) {
                                        return;
                                    }
                                    if (password.length() < 4) {
                                        mView.showToast("请输入四位密码");
                                        return;
                                    }
                                    if (TextUtils.equals(password, roomBean.getPassword())) {
                                        inputPasswordDialog.dismiss();
                                        exitRoom(roomBean.getRoomType(), roomBean.getRoomId());
                                    } else {
                                        mView.showToast("密码错误");
                                    }
                                }
                            });
                            inputPasswordDialog.show();
                        } else {
                            exitRoom(roomBean.getRoomType(), roomBean.getRoomId());
                        }
                    }
                } else {
                    mView.dismissLoading();
                    if (result.getCode() == 30001) {
                        //房间不存在了
                        mView.showToast("房间不存在了");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
            }
        });
    }

    private void exitRoom(int roomType, final String roomId) {
        // 房间类表包含roomId，则直接切换，否则跳转
        if (RoomListIdsCache.get().contains(roomId)) {
            mView.switchOtherRoom(roomId);
        } else {
            mView.showLoading("");
            LiveEventHelper.getInstance().leaveRoom(new IRoomCallBack() {
                @Override
                public void onSuccess() {
                    mView.finish();
                    mView.dismissLoading();
                    IntentWrap.launchRoom(mView.getLiveActivity(), roomType, roomId);
                }

                @Override
                public void onError(int code, String message) {

                }
            });
        }
    }
}
