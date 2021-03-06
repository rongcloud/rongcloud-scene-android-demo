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
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.SensorsUtil;
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
import cn.rongcloud.roomkit.ui.RoomType;
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
 * ?????????
 */
public class LiveRoomPresenter extends BasePresenter<LiveRoomView> implements
        ILiveRoomPresent, RoomTitleBar.OnFollowClickListener,
        MemberSettingFragment.OnMemberSettingClickListener,
        ICommonDialog, LiveRoomListener, GiftFragment.OnSendGiftListener
        , RoomBottomView.OnBottomOptionClickListener, LiveLayoutSettingCallBack
        , LiveRoomCreatorSettingFragment.OnCreatorSettingClickListener
        , LiveRoomVideoSettingFragment.OnVideoConfigSetting {

    private String TAG = "LiveRoomPresenter";

    private VoiceRoomBean mVoiceRoomBean;//????????????
    private RoomOwnerType roomOwnerType;//??????????????????
    private List<String> shields = new ArrayList<>();//???????????????
    private List<Disposable> disposablesManager = new ArrayList<>();//???????????????

    private ArrayList<User> requestSeats = new ArrayList<>();//?????????????????????
    private ArrayList<User> inviteSeats = new ArrayList<>();//????????????????????????
    private boolean isInRoom;
    private SeatOperationViewPagerFragment seatOperationViewPagerFragment;
    private EmptySeatFragment emptySeatFragment;
    private Map<String, String> giftMap;
    private InputPasswordDialog inputPasswordDialog;

    public LiveRoomPresenter(LiveRoomView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
    }


    /**
     * ?????????
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
     * ??????????????????
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
                            //??????????????????????????????,??????????????????????????????
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
                        //??????????????????
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
     * ???????????????????????????????????????
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
     * ????????????
     *
     * @param roomId
     * @param isCreate
     */
    private void joinRoom(String roomId, boolean isCreate) {
        SensorsUtil.instance().joinRoom(roomId, mVoiceRoomBean.getRoomName(), mVoiceRoomBean.getIsPrivate() == 1,
                false, false, RoomType.LIVE_ROOM.convertToRcEvent());
        if (mVoiceRoomBean.getCreateUserId().equals(UserManager.get().getUserId())) {
            prepare(roomId, isCreate);
        } else {
            //????????????????????????????????????
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
                        //??????????????????
                        mView.showFinishView();
                        leaveRoom(roomId, isCreate, false);
                    }
                }
            });
        }
    }

    /**
     * ???????????????
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
     * ?????????????????????
     *
     * @param position
     */
    @Override
    public void requestSeat(int position) {
        CurrentStatusType currentStatus = LiveEventHelper.getInstance().getCurrentStatus();
        //??????????????????
        LiveEventHelper.getInstance().getRoomInfoByKey(LiveRoomKvKey.LIVE_ROOM_ENTER_SEAT_MODE, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String mode) {
                //&& TextUtils.equals(mode, LiveRoomKvKey.EnterSeatMode.LIVE_ROOM_RequestEnterSeat)
                if (currentStatus == CurrentStatusType.STATUS_WAIT_FOR_SEAT) {
                    showRevokeSeatRequestFragment();
                    return;
                }
                //?????????????????????
                if (TextUtils.equals(mode, LiveRoomKvKey.EnterSeatMode.LIVE_ROOM_FreeEnterSeat)) {
                    int index = position;
                    if (index == -1) {
                        for (RCLiveSeatInfo seatInfo : RCLiveEngine.getInstance().getSeatManager().getSeatInfos()) {
                            if (TextUtils.isEmpty(seatInfo.getUserId()) && !seatInfo.isLock()) {
                                //?????????????????????????????????
                                index = seatInfo.getIndex();
                                break;
                            }
                        }
                        if (index == -1) {
                            KToast.show("?????????????????????!");
                            return;
                        }
                    }
                    LiveEventHelper.getInstance().enterSeat(index, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            KToast.show(msg);
                            if (result) {
                                //????????????
                                mView.changeStatus();
                            }
                        }
                    });
                } else {
                    //?????????????????????
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
                            mView.showToast(isPrivate ? "????????????" : "????????????");
                            mVoiceRoomBean.setIsPrivate(isPrivate ? 1 : 0);
                            mVoiceRoomBean.setPassword(password);
                            IFun.BaseFun fun = item.getValue();
                            fun.setStatus(p);
                            item.setValue(fun);
                        } else {
                            mView.showToast(isPrivate ? "????????????" : "????????????");
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
                            KToast.show("????????????");
                            mVoiceRoomBean.setRoomName(name);
                            LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NAME, name, null);
                        } else {
                            String message = result.getMessage();
                            mView.showToast(!TextUtils.isEmpty(message) ? message : "????????????");
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mView.showToast(!TextUtils.isEmpty(msg) ? msg : "????????????");
                    }
                });
    }

    /**
     * ?????????????????????
     * true ????????????
     * false ????????????
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
                        KToast.show("???????????????????????????");
                    } else {
                        KToast.show("???????????????????????????");
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
     * ??????????????????
     *
     * @param mVoiceRoomBean
     * @param isCreate
     */
    @Override
    public void setCurrentRoom(VoiceRoomBean mVoiceRoomBean, boolean isCreate) {
        initLiveRoomListener(mVoiceRoomBean.getRoomId());
        roomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(mVoiceRoomBean);
        if (isInRoom) {
            //????????????????????????????????????
            List<MessageContent> messageList = LiveEventHelper.getInstance().getMessageList();
            mView.addMessageList(messageList, true);
        } else {
            // ??????????????????
            sendDefaultMessage();
        }
        //??????????????????????????????????????????????????????
        MemberCache.getInstance().fetchData(mVoiceRoomBean.getRoomId());
        //???????????????id
        LiveEventHelper.getInstance().setRoomBean(mVoiceRoomBean);
        //??????????????????
        mView.showRCLiveVideoView(RCLiveEngine.getInstance().preview());
        getShield();
        getGiftCount(mVoiceRoomBean.getRoomId());
        mView.setRoomData(mVoiceRoomBean);

    }

    /**
     * ??????????????????????????????
     *
     * @param roomId
     */
    @Override
    public void initLiveRoomListener(String roomId) {
        setObMessageListener(roomId);
//        setObShieldListener();
        //????????????????????????
        MemberCache.getInstance().getMemberList()
                .observe(((LiveRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        mView.setOnlineCount(users.size());
                        LiveEventHelper.getInstance().getRequestLiveVideoIds(null);
                        onInviteLiveVideoIds(users);
                    }
                });
        //?????????????????????
        MemberCache.getInstance().getAdminList()
                .observe(((LiveRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<String>>() {
                    @Override
                    public void onChanged(List<String> strings) {
                        mView.refreshMessageList();
                    }
                });
    }

    /**
     * ?????????????????????????????????
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
                            //??????????????????,???????????????????????????
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
                            //?????????????????????
                            mView.setCreateUserGift(giftMap.get(userId));
                        }
                    }
                }
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param notice ????????????
     */
    @Override
    public void modifyNotice(String notice) {
        Logger.e(TAG, "notice = " + notice);
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NOTICE, notice, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    TextMessage noticeMsg = TextMessage.obtain("?????????????????????!");
                    sendMessage(noticeMsg);
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    @Override
    public void sendGift() {
        SensorsUtil.instance().giftClick(getRoomId(), getRoomName(), RcEvent.LiveRoom);
        ArrayList<Member> memberArrayList = new ArrayList<>();

        if (PKManager.get().getPkState().isInPk()) {
            memberArrayList.add(Member.fromUser(mVoiceRoomBean.getCreateUser()));
            mView.showSendGiftDialog(mVoiceRoomBean, getCreateUserId(), memberArrayList);
            return;
        }
        //??????????????????
        List<RCLiveSeatInfo> rcLiveSeatInfos = RCLiveEngine.getInstance().getSeatManager().getSeatInfos();
        for (RCLiveSeatInfo rcLiveSeatInfo : rcLiveSeatInfos) {
            String userId = rcLiveSeatInfo.getUserId();
            if (TextUtils.isEmpty(userId)) {
                //????????????????????????
                continue;
            }
            if (!TextUtils.equals(userId, getCreateUserId())) {
                //????????????????????????,???????????????????????????
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
        //??????????????????????????????
        Collections.sort(memberArrayList, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getSeatIndex() - o2.getSeatIndex();
            }
        });

        //???????????????????????????????????????????????????
        Member member = Member.fromUser(mVoiceRoomBean.getCreateUser());
        memberArrayList.add(0, member);
        mView.showSendGiftDialog(mVoiceRoomBean, getCreateUserId(), memberArrayList);
    }

    /**
     * ????????????
     *
     * @param roomId
     * @param isCreate
     */
    @Override
    public void prepare(String roomId, boolean isCreate) {
        if (isCreate) {
            //?????????????????????
            begin(roomId, isCreate);
        } else {
            //???????????????????????????????????????????????????
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
     * ??????????????????????????????
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
        mView.showLoading("??????????????????");
        MusicControlManager.getInstance().release();
        if (mVoiceRoomBean != null)
            SensorsUtil.instance().closeRoom(getRoomId(), mVoiceRoomBean.getRoomName(), "", RcEvent.LiveRoom);
        //?????????????????????????????????????????????
        OkApi.get(VRApi.deleteRoom(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                mView.dismissLoading();
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
        mView.showLoading("????????????????????????");
        LiveEventHelper.getInstance().leaveRoom(new IRoomCallBack() {
            @Override
            public void onSuccess() {
                mView.dismissLoading();
                mView.finish();
                if (callBack != null)
                    callBack.onResult(true, "??????");
            }

            @Override
            public void onError(int code, String message) {
                mView.dismissLoading();
                KToast.show(message);
            }
        });
    }

    /**
     * ???????????????????????????????????????
     *
     * @param shieldArrayList
     */
    public void updateShield(List<Shield> shieldArrayList) {
        this.shields.clear();
        for (Shield shield : shieldArrayList) {
            if (!shield.isDefault()) {
                //???????????????????????????
                this.shields.add(shield.getName());
            }
        }
        JsonArray jsonElements = new JsonArray();
        for (String shield : this.shields) {
            jsonElements.add(shield);
        }
        //??????KV??????
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_SHIELDS, jsonElements.toString(), null);
    }


    /**
     * ?????????????????????????????????
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
                        //???????????????????????????
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
     * ????????????
     * ?????????????????????
     *
     * @param messageContent
     */
    @Override
    public void sendMessage(MessageContent messageContent) {
        sendMessage(messageContent, true);
    }

    /**
     * ????????????
     *
     * @param messageContent ?????????
     * @param isShowLocation ?????????????????????
     */
    @Override
    public void sendMessage(MessageContent messageContent, boolean isShowLocation) {
        if (!isContainsShield(messageContent))
            LiveEventHelper.getInstance().sendMessage(messageContent, isShowLocation);
    }

    /**
     * ?????????????????????
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
                //???????????????????????????'
                mView.addMessageContent(messageContent, false);
                return true;
            }
        }
        return false;
    }

    /**
     * ???????????????????????????UI
     *
     * @param show ????????????
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
     * ??????????????????????????????????????????,??????????????????????????????????????????
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * ???????????????
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
        // ????????? ??????/?????? ?????????
        OkApi.put(VRApi.ADMIN_MANAGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    RCChatroomAdmin admin = new RCChatroomAdmin();
                    admin.setAdmin(isAdmin);
                    admin.setUserId(user.getUserId());
                    admin.setUserName(user.getUserName());
                    // ????????????????????????????????????
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
     * ????????????
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().kickUserFromRoom(user, callback);
    }

    /**
     * ??????????????????
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
     * ??????
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
     * ????????????????????????????????????
     */
    private void sendDefaultMessage() {
        if (mVoiceRoomBean != null) {
            mView.addMessageContent(null, true);
            // ????????????
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("???????????? %s", mVoiceRoomBean.getRoomName()));
            sendMessage(welcome);
            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("?????????????????? RTC ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            sendMessage(tips);
        }
    }

    /**
     * ????????????
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
     * ????????????
     *
     * @param userId
     * @param callback
     */
    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().acceptRequestSeat(userId, callback);
    }

    /**
     * ??????????????????
     *
     * @param userId
     * @param callback
     */
    @Override
    public void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().rejectRequestSeat(userId, callback);
    }

    /**
     * ????????????
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
                    KToast.show("?????????????????????");
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
     * ?????????
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        LiveEventHelper.getInstance().kickUserFromSeat(user, callback);
    }

    /**
     * ???????????????
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
     * ??????????????????????????????
     *
     * @param seatIndex
     * @param isLock
     * @param callback
     */
    @Override
    public void clickCloseSeat(int seatIndex, boolean isLock, ClickCallback<Boolean> callback) {
        if (RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
            KToast.show("?????????????????????????????????");
            return;
        }
        LiveEventHelper.getInstance().lockSeat(seatIndex, isLock, callback);
    }

    /**
     * ????????????
     *
     * @param seatIndex
     * @param callback
     */
    @Override
    public void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {
        RCLiveSeatInfo rcLiveSeatInfo = RCLiveEngine.getInstance().getSeatManager().getSeatInfos().get(seatIndex);
        if (rcLiveSeatInfo != null) {
            if (rcLiveSeatInfo.isLock()) {
                KToast.show("??????????????????");
                return;
            }
        }
        LiveEventHelper.getInstance().switchToSeat(seatIndex, callback);
    }

    /**
     * ????????????
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
     * ????????????
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
            case LiveRoomKvKey.LIVE_ROOM_NAME://?????????
                mVoiceRoomBean.setRoomName(value);
                break;
            case LiveRoomKvKey.LIVE_ROOM_NOTICE://????????????
                mView.setNotice(value);
                break;
            case LiveRoomKvKey.LIVE_ROOM_SHIELDS://???????????????
                getShield();
                break;
        }
    }

    /**
     * ??????????????????
     *
     * @param userId ????????????????????????????????????????????????????????????????????????userId???????????????????????????
     */
    @Override
    public void onUserEnter(String userId, int onlineCount) {
        mView.setOnlineCount(onlineCount);
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * ??????????????????
     *
     * @param userId ????????????????????????????????????????????????????????????????????????userId???????????????????????????
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
     * ????????????
     *
     * @param userId     ????????????????????????
     * @param operatorId ??????????????????????????????????????????
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
            //??????????????????????????????
            mView.changeSeatOrder();
        }
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onLiveVideoRequestChange() {
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onLiveVideoRequestAccepted() {
        mView.changeStatus();
    }

    /**
     * ?????????????????????
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
     * ?????????????????????????????????
     *
     * @param mixType      ????????????
     * @param customerType ???????????????
     */
    @Override
    public void onRoomMixTypeChange(RCLiveMixType mixType, int customerType) {
        RCLiveView videoView = RCLiveEngine.getInstance().preview();
        if (mixType == RCLiveMixType.RCMixTypeOneToOne) {
            //?????????1V1?????????
            videoView.setDevTop(0);
        } else {
            videoView.setDevTop(mView.getMarginTop());
        }
        mView.changeMessageContainerHeight();
    }


    /**
     * ????????????
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
        //??????????????????
        RCChatroomBarrage barrage = new RCChatroomBarrage();
        barrage.setContent(message);
        barrage.setUserId(UserManager.get().getUserId());
        barrage.setUserName(UserManager.get().getUserName());
        sendMessage(barrage);
    }

    @Override
    public void clickPrivateMessage() {
        SensorsUtil.instance().textClick(getRoomId(), getRoomName(), RcEvent.LiveRoom);
        RouteUtils.routeToSubConversationListActivity(
                mView.getLiveActivity(),
                Conversation.ConversationType.PRIVATE,
                "??????"
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
        SensorsUtil.instance().pkClick(getRoomId(), getRoomName(), RcEvent.LiveRoom);
        PKState pkState = PKManager.get().getPkState();
        int mixType = RCDataManager.get().getMixType();
        // ????????????2?????? ??? ?????????????????? ????????????
        if ((mixType == RCLiveMixType.RCMixTypeOneToOne.getValue()
                || mixType == RCLiveMixType.RCMixTypeGridTwo.getValue())
                && LiveEventHelper.getInstance().getInviteStatusType() == InviteStatusType.STATUS_NOT_INVITRED) {
            if (pkState.isNotInPk()) {
                // ?????????pk ?????????????????????????????????
                if (mixType == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                    PKManager.get().showPkInvitation((Activity) mView.getLiveActivity());
                } else {
                    KToast.show("?????????????????????PK");
                }
            } else if (pkState.isInInviting()) {
                PKManager.get().showCancelPkInvitation((Activity) mView.getLiveActivity());
            } else if (pkState.isInPk()) {// pk???
                PKManager.get().showQuitPK((Activity) mView.getLiveActivity());
            }
        } else {
            KToast.show("?????????????????????PK");
        }
    }

    @Override
    public void clickRequestSeat() {
        if (!PKManager.get().getPkState().enableAction()) {
            return;
        }
        if (LiveEventHelper.getInstance().getCurrentStatus() == CurrentStatusType.STATUS_ON_SEAT) {
            //??????????????????????????????????????????????????????????????????
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
     * TODO ??????????????????????????????
     * 1??????????????????????????????????????????
     * 2?????????????????????,??????????????????????????????????????????
     *
     * @return
     */
    @Override
    public boolean canSend() {
        //?????????????????????????????????????????????????????????
        RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(UserManager.get().getUserId());
        if (rcLiveSeatInfo == null || (rcLiveSeatInfo != null && !rcLiveSeatInfo.isMute())) {
            return false;
        }
        return true;
    }


    /**
     * ???????????????????????????
     * ???????????????????????????????????????????????????  ?????????
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
     * ????????????????????????
     * ???????????????  ?????????????????????
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
     * ????????????
     *
     * @param rcLiveMixType
     */
    @Override
    public void setupMixType(RCLiveMixType rcLiveMixType) {
        Log.e(TAG, "onRCMixLayoutChange: " + rcLiveMixType);
        RCLiveView videoView = RCLiveEngine.getInstance().preview();
        if (rcLiveMixType == RCLiveMixType.RCMixTypeOneToOne) {
            //?????????1V1?????????
            videoView.setDevTop(0);
        } else {
            videoView.setDevTop(mView.getMarginTop());
        }
        RCLiveEngine.getInstance().setMixType(rcLiveMixType, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                TextMessage textMessage = TextMessage.obtain("???????????????????????????????????????");
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
     * ????????????
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
     * ??????????????????
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
     * ?????? view ?????????
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
     * ???????????????
     *
     * @param index
     * @param isVideo
     */
    @Override
    public void clickSwitchLinkStatus(int index, boolean isVideo) {
        LiveEventHelper.getInstance().switchVideoOrAudio(index, isVideo, null);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ?????????????????????
     *
     * @param index
     * @param isMute
     */
    @Override
    public void clickMuteSelf(int index, boolean isMute) {
        LiveEventHelper.getInstance().MuteSelf(index, isMute, null);
    }

    /**
     * ????????????
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
     * ????????????
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
        //??????????????????1V1?????????
        if (!TextUtils.isEmpty(userId)) {
            if (TextUtils.equals(userId, getCreateUserId()) && rcParameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //?????????1V1???????????????????????????????????????????????????
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
            // ?????????????????????
            if (rcParameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //???????????????1V1???????????????????????????????????????
                view = null;
            } else {
                view = createEmptyMicLayout(rcParameter);
            }
        }
        return view;
    }

    /**
     * ?????????????????????
     *
     * @param holder    ????????????
     * @param seatInfo  ????????????
     * @param parameter ?????????????????????????????????
     */
    @Override
    public void onBindView(RCHolder holder, RCLiveSeatInfo seatInfo, RCParamter parameter) {
        if (holder != null) {
            View view = holder.rootView();
            if (TextUtils.isEmpty(seatInfo.getUserId()) && view != null) {
                //???????????????????????????????????????????????????
                TextView name = view.findViewById(R.id.tv_member_name);
                name.setText(seatInfo.getIndex() + "?????????");
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
                //???????????????????????????
                TextView name = view.findViewById(R.id.tv_member_name);
                view.setBackground(parameter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue() ? null : mView.getLiveActivity().getDrawable(R.drawable.shape_live_seat_online_bg));
                RelativeLayout rl_mic_audio_value = view.findViewById(R.id.rl_mic_audio_value);
                ImageView imageView = view.findViewById(R.id.iv_room_creator_portrait);
                //???????????????????????????????????????????????????????????????
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
                            //???????????????????????????
                            rl_mic_audio_value.setVisibility(View.GONE);
                            ivBackGroup.setVisibility(View.GONE);
                        } else {
                            //?????????????????????
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
                //??????????????????
                setClickSeatListener(view, seatInfo);
            }
        }
    }

    /**
     * ????????????
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
     * ????????????????????????
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeats(RCLiveSeatInfo rcLiveSeatInfo) {
        switch (getRoomOwnerType()) {
            case LIVE_OWNER://??????
                onClickLiveRoomSeatsByOwner(rcLiveSeatInfo);
                break;
            case LIVE_VIEWER://??????
                onClickLiveRoomSeatsByViewer(rcLiveSeatInfo);
                break;
        }

    }

    /**
     * ???????????????????????????
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeatsByViewer(RCLiveSeatInfo rcLiveSeatInfo) {
        //?????????????????????
        if (TextUtils.isEmpty(rcLiveSeatInfo.getUserId())) {
            if (rcLiveSeatInfo.isLock()) {
                //???????????????
                KToast.show("??????????????????");
                return;
            }
            if (LiveEventHelper.getInstance().getCurrentStatus() != CurrentStatusType.STATUS_ON_SEAT) {
                //?????????????????????????????????,?????????????????????
                requestSeat(rcLiveSeatInfo.getIndex());
            } else {
                //????????????
                LiveEventHelper.getInstance().switchToSeat(rcLiveSeatInfo.getIndex(), null);
            }
        } else {
            //????????????
            if (TextUtils.equals(rcLiveSeatInfo.getUserId(), RongCoreClient.getInstance().getCurrentUserId())) {
                //?????????-?????????????????????
                mView.showCreatorSettingFragment(rcLiveSeatInfo);
            } else {
                if (PKManager.get().getPkState().isInPk()) {
                    return;
                }
                //?????????-????????????????????????
                mView.showMemberSettingFragment(rcLiveSeatInfo.getUserId());
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param rcLiveSeatInfo
     */
    private void onClickLiveRoomSeatsByOwner(RCLiveSeatInfo rcLiveSeatInfo) {
        //????????????????????????????????????????????????
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
            //?????????????????????
            if (TextUtils.equals(rcLiveSeatInfo.getUserId(), getCreateUserId())) {
                //?????????-?????????????????????
                mView.showCreatorSettingFragment(rcLiveSeatInfo);
            } else {
                if (PKManager.get().getPkState().isInPk()) {
                    return;
                }
                //?????????-????????????????????????
                mView.showMemberSettingFragment(rcLiveSeatInfo.getUserId());
            }
        }
    }


    /**
     * ??????
     *
     * @param seatInfo
     * @param audioLevel ??????
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
     * ???????????????KV
     *
     * @param resolution ?????????
     */
    @Override
    public void updateVideoResolution(RCRTCParamsType.RCRTCVideoResolution resolution) {
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_VIDEO_RESOLUTION, resolution.getLabel(), null);
    }

    /**
     * ????????????KV
     *
     * @param fps ??????
     */
    @Override
    public void updateVideoFps(RCRTCParamsType.RCRTCVideoFps fps) {
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_VIDEO_FPS, fps.getFps() + "", null);
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param message
     */
    public void jumpRoom(RCAllBroadcastMessage message) {
        // ?????????????????????
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
                        // ???????????????????????????????????????
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
                                        mView.showToast("?????????????????????");
                                        return;
                                    }
                                    if (TextUtils.equals(password, roomBean.getPassword())) {
                                        inputPasswordDialog.dismiss();
                                        exitRoom(roomBean.getRoomType(), roomBean.getRoomId());
                                    } else {
                                        mView.showToast("????????????");
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
                        //??????????????????
                        mView.showToast("??????????????????");
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
        // ??????????????????roomId?????????????????????????????????
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
