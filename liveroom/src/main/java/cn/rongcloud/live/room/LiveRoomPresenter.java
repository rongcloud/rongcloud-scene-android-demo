package cn.rongcloud.live.room;


import static cn.rong.combusis.EventBus.TAG.UPDATE_SHIELD;
import static cn.rong.combusis.sdk.event.wrapper.EToast.showToast;

import android.annotation.SuppressLint;
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

import com.basis.mvp.BasePresenter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.jakewharton.rxbinding4.view.RxView;
import com.kit.UIKit;
import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.utils.ScreenUtil;
import com.kit.wapper.IResultBack;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.ImageLoaderUtil;

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

import cn.rong.combusis.EventBus;
import cn.rong.combusis.R;
import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
import cn.rong.combusis.common.ui.widget.WaveView;
import cn.rong.combusis.intent.IntentWrap;
import cn.rong.combusis.manager.AllBroadcastManager;
import cn.rong.combusis.manager.RCChatRoomMessageManager;
import cn.rong.combusis.message.RCAllBroadcastMessage;
import cn.rong.combusis.message.RCChatroomAdmin;
import cn.rong.combusis.message.RCChatroomBarrage;
import cn.rong.combusis.message.RCChatroomEnter;
import cn.rong.combusis.message.RCChatroomGift;
import cn.rong.combusis.message.RCChatroomGiftAll;
import cn.rong.combusis.message.RCChatroomKickOut;
import cn.rong.combusis.message.RCChatroomLike;
import cn.rong.combusis.message.RCChatroomLocationMessage;
import cn.rong.combusis.message.RCChatroomSeats;
import cn.rong.combusis.message.RCChatroomVoice;
import cn.rong.combusis.message.RCFollowMsg;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.user.UserProvider;
import cn.rong.combusis.provider.voiceroom.CurrentStatusType;
import cn.rong.combusis.provider.voiceroom.InviteStatusType;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.sdk.event.listener.LeaveRoomCallBack;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rong.combusis.ui.room.dialog.shield.Shield;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.LiveLayoutSettingCallBack;
import cn.rong.combusis.ui.room.fragment.MemberSettingFragment;
import cn.rong.combusis.ui.room.fragment.RoomVideoSettingFragment;
import cn.rong.combusis.ui.room.fragment.gift.GiftFragment;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomBeautyFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomBeautyMakeUpFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomOverTurnFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSeatModeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSpecialEffectsFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomTagsFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomVideoSetFun;
import cn.rong.combusis.ui.room.fragment.seatsetting.EmptySeatFragment;
import cn.rong.combusis.ui.room.fragment.seatsetting.ICommonDialog;
import cn.rong.combusis.ui.room.fragment.seatsetting.RevokeSeatRequestFragment;
import cn.rong.combusis.ui.room.fragment.seatsetting.SeatOperationViewPagerFragment;
import cn.rong.combusis.ui.room.model.Member;
import cn.rong.combusis.ui.room.model.MemberCache;
import cn.rong.combusis.ui.room.widget.RoomBottomView;
import cn.rong.combusis.ui.room.widget.RoomTitleBar;
import cn.rongcloud.live.fragment.LiveRoomCreatorSettingFragment;
import cn.rongcloud.live.helper.LiveEventHelper;
import cn.rongcloud.live.helper.LiveRoomListener;
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
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.stream.RCRTCCameraOutputStream;
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
        , RoomVideoSettingFragment.OnVideoConfigSetting {

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

    public LiveRoomPresenter(LiveRoomView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
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
        LiveEventHelper.getInstance().leaveRoom(new LeaveRoomCallBack() {
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
        if (mVoiceRoomBean.getCreateUserId().equals(AccountStore.INSTANCE.getUserId())) {
            prepare(roomId, isCreate);
        } else {
            //如果是观众就直接加入房间
            LiveEventHelper.getInstance().joinRoom(roomId, new ClickCallback<Boolean>() {
                @Override
                public void onResult(Boolean result, String msg) {
                    mView.dismissLoading();
                    if (result) {
                        setCurrentRoom(mVoiceRoomBean, isCreate);
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
                            EToast.showToast("没有空闲的麦位!");
                            return;
                        }
                    }
                    LiveEventHelper.getInstance().enterSeat(index, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            EToast.showToast(msg);
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
                            EToast.showToast("修改成功");
                            mVoiceRoomBean.setRoomName(name);
                            LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NAME, name, null);
                        } else {
                            mView.showToast("修改失败");
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mView.showToast("修改失败");
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
                        EToast.showToast("当前观众可自由上麦");
                    } else {
                        EToast.showToast("当前观众上麦要申请");
                    }
                } else {
                    EToast.showToast(msg);
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
        setObShieldListener();
        //监听房间里面的人
        MemberCache.getInstance().getMemberList()
                .observe(((LiveRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        mView.setOnlineCount(users.size());
                        LiveEventHelper.getInstance().getRequestLiveVideoIds(null);
                        onInvitateLiveVideoIds(users);
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
        EventBus.get().off(UPDATE_SHIELD, null);
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
        LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_NOTICE, notice, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    TextMessage noticeMsg = TextMessage.obtain("房间公告已更新!");
                    sendMessage(noticeMsg);
                } else {
                    EToast.showToast(msg);
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
                    member = new Member().toMember(user);
                }
                member.setSeatIndex(rcLiveSeatInfo.getIndex());
                memberArrayList.add(member);
            }
        }
        //按照麦位从小到大拍讯
        Collections.sort(memberArrayList, new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getSeatIndex() - o2.getSeatIndex();
            }
        });

        //如果是房主麦位，不用管房主是否存在
        User user = MemberCache.getInstance().getMember(mVoiceRoomBean.getCreateUserId());
        Member member = null;
        if (user == null) {
            member = new Member();
            member.setUserName(mVoiceRoomBean.getCreateUserName());
            member.setUserId(mVoiceRoomBean.getCreateUserId());
        } else {
            member = new Member().toMember(user);
        }
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
        LiveEventHelper.getInstance().finishRoom(new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                //房主关闭房间，调用删除房间接口
                OkApi.get(VRApi.deleteRoom(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        mView.dismissLoading();
                        if (result.ok()) {
                            mView.finish();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mView.dismissLoading();
                        mView.showToast(msg);
                    }
                });
            }
        });
    }

    @Override
    public void leaveLiveRoom(ClickCallback callBack) {
        mView.showLoading("正在离开当前房间");
        LiveEventHelper.getInstance().leaveRoom(new LeaveRoomCallBack() {
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
                EToast.showToast(message);
            }
        });
    }

    /**
     * 监听自己删除或者添加屏蔽词
     */
    private void setObShieldListener() {
        EventBus.get().on(UPDATE_SHIELD, new EventBus.EventCallback() {
            @SuppressLint("NewApi")
            @Override
            public void onEvent(String tag, Object... args) {
                shields.clear();
                ArrayList<Shield> shieldArrayList = (ArrayList<Shield>) args[0];
                for (Shield shield : shieldArrayList) {
                    if (!shield.isDefault()) {
                        //说明是正常的屏蔽词
                        shields.add(shield.getName());
                    }
                }
                JsonArray jsonElements = new JsonArray();
                for (String shield : shields) {
                    jsonElements.add(shield);
                }
                //发送KV消息
                LiveEventHelper.getInstance().updateRoomInfoKv(LiveRoomKvKey.LIVE_ROOM_SHIELDS, jsonElements.toString(), null);
            }
        });
    }

    /**
     * 监听接收房间的所有信息
     *
     * @param roomId
     */
    private void setObMessageListener(String roomId) {
        disposablesManager.add(RCChatRoomMessageManager.INSTANCE.
                obMessageReceiveByRoomId(roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageContent>() {
                    @Override
                    public void accept(MessageContent messageContent) throws Throwable {
                        //将消息显示到列表上
                        Class<? extends MessageContent> aClass = messageContent.getClass();
                        if (RCChatroomLocationMessage.class.equals(aClass) || RCChatroomVoice.class.equals(aClass)
                                || RCChatroomBarrage.class.equals(aClass) || RCChatroomEnter.class.equals(aClass)
                                || RCChatroomKickOut.class.equals(aClass) || RCChatroomGift.class.equals(aClass)
                                || RCChatroomAdmin.class.equals(aClass) || RCChatroomSeats.class.equals(aClass)
                                || RCChatroomGiftAll.class.equals(aClass) || RCFollowMsg.class.equals(aClass)
                                || TextMessage.class.equals(aClass)) {
                            if (null != mView) mView.addMessageContent(messageContent, false);
                        }
                        if (RCChatroomGift.class.equals(aClass) || RCChatroomGiftAll.class.equals(aClass)) {
                            getGiftCount(roomId);
                        } else if (aClass.equals(RCChatroomLike.class)) {
                            if (null != mView) mView.showLikeAnimation();
                        } else if (aClass.equals(RCAllBroadcastMessage.class)) {
                            AllBroadcastManager.getInstance().addMessage((RCAllBroadcastMessage) messageContent);
                        } else if (aClass.equals(RCChatroomSeats.class)) {
//                            refreshRoomMember();
                        } else if (aClass.equals(RCChatroomLocationMessage.class)) {

                        } else if (aClass.equals(RCChatroomAdmin.class)) {
                            MemberCache.getInstance().refreshAdminData(mVoiceRoomBean.getRoomId());
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
                    showToast(result.getMessage());
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
        Member member = new Member().toMember(user);
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
            // 广播消息
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(AccountStore.INSTANCE.getUserId());
            enter.setUserName(AccountStore.INSTANCE.getUserName());
            sendMessage(enter, false);
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
        LiveEventHelper.getInstance().pickUserToSeat(user.getUserId(), seatIndex, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                callback.onResult(result, msg);
                if (TextUtils.equals(getCreateUserId(), AccountStore.INSTANCE.getUserId())) {
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
                    EToast.showToast("已撤回连线申请");
                } else {
                    EToast.showToast(msg);
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
            EToast.showToast("默认模式不支持关闭座位");
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
                EToast.showToast("麦位已上锁！");
                return;
            }
        }
        LiveEventHelper.getInstance().swichToSeat(seatIndex, callback);
    }

    /**
     * 邀请弹窗
     *
     * @param index
     */
    @Override
    public void showSeatOperationViewPagerFragment(int index, int seatIndex) {
        seatOperationViewPagerFragment = new SeatOperationViewPagerFragment(getRoomOwnerType());
        seatOperationViewPagerFragment.setIndex(index);
        seatOperationViewPagerFragment.setRequestSeats(requestSeats);
        seatOperationViewPagerFragment.setInviteSeats(inviteSeats);
        seatOperationViewPagerFragment.setInviteSeatIndex(seatIndex);
        seatOperationViewPagerFragment.setSeatActionClickListener(LiveRoomPresenter.this);
        seatOperationViewPagerFragment.setLiveLayoutSettingCallBack(this::setupMixType);
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
        mView.setOnlineCount(onlineCount);
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * 被踢出去
     *
     * @param userId     被踢用户唯一标识
     * @param operatorId 踢人操作的执行用户的唯一标识
     */
    @Override
    public void onUserKitOut(String userId, String operatorId) {
        if (TextUtils.equals(userId, AccountStore.INSTANCE.getUserId())) {
            mView.finish();
            unInitLiveRoomListener();
        }
    }

    @Override
    public void onLiveVideoUpdate(List<String> lineMicUserIds) {
        if (TextUtils.equals(getCreateUserId(), AccountStore.INSTANCE.getUserId())) {
            //如果是房主，那么更新
            mView.changeSeatOrder();
        }
        MemberCache.getInstance().refreshMemberData(getRoomId());
    }

    /**
     * 有人申请了麦位，这个时候需要去请求房间里面人得信息，不然拿不到
     */
    @Override
    public void onLiveVideoRequestChanage() {
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
    public void onliveVideoInvitationReceived(String userId, int index) {

    }

    @Override
    public void onliveVideoInvitationCanceled() {

    }

    @Override
    public void onliveVideoInvitationAccepted(String userId) {

    }

    @Override
    public void onliveVideoInvitationRejected(String userId) {
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
        barrage.setUserId(AccountStore.INSTANCE.getUserId());
        barrage.setUserName(AccountStore.INSTANCE.getUserName());
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

    }

    @Override
    public void clickRequestSeat() {
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
            for (String userId : requestLives) {
                if (TextUtils.equals(userId, AccountStore.INSTANCE.getUserId())) {
                    LiveEventHelper.getInstance().setCurrentStatus(CurrentStatusType.STATUS_WAIT_FOR_SEAT);
                    break;
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
    public void onInvitateLiveVideoIds(List<User> roomUsers) {
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
        int width = seatWidth * 2 / 5 > ScreenUtil.getScreemWidth() / 6 ? ScreenUtil.getScreemWidth() / 6 : seatWidth * 2 / 5;
        setViewLayoutParams(imageView, width, width);
        setViewLayoutParams(waveView, seatWidth, seatHeight);
        return view;
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
            RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByUserId(AccountStore.INSTANCE.getUserId());
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
                EToast.showToast(msg);
            }
        });
    }

    /**
     * 生成布局
     *
     * @param seatInfo
     * @param rcParamter
     * @return
     */
    @Override
    public View inflaterSeatView(RCLiveSeatInfo seatInfo, RCParamter rcParamter) {
        String userId = seatInfo.getUserId();
        Log.e(TAG, "setSeatViewProvider: width" + rcParamter.getWidth());
        Log.e(TAG, "setSeatViewProvider: heigth" + rcParamter.getHeight());
        View view = null;
        //麦位为默认的1V1的时候
        if (!TextUtils.isEmpty(userId)) {
            if (TextUtils.equals(userId, getCreateUserId()) && rcParamter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //如果是1V1，而且当前是房主，也不应该显示视图
                view = null;
            } else {
                view = createMicLayout(rcParamter);
            }
        } else {
            // 麦位没人的时候
            if (rcParamter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                //如果当前是1V1，那么小视图应该不显示覆盖
                view = null;
            } else {
                view = createEmptyMicLayout(rcParamter);
            }
        }
        return view;
    }

    /**
     * 刷新视图的信息
     *
     * @param holder   麦位布局
     * @param seatInfo 麦位信息
     * @param paramter 当前麦位布局的参数信息
     */
    @Override
    public void onBindView(RCHolder holder, RCLiveSeatInfo seatInfo, RCParamter paramter) {
        if (holder != null) {
            View view = holder.rootView();
            if (TextUtils.isEmpty(seatInfo.getUserId()) && view != null) {
                //如果麦位为空的话，那么应该是空布局
                TextView name = view.findViewById(R.id.tv_member_name);
                name.setText(seatInfo.getIndex() + "号麦位");
                ImageView isMuteView = view.findViewById(R.id.iv_is_mute);
                if (seatInfo.isMute()) {
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
                view.setBackground(paramter.getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue() ? null : mView.getLiveActivity().getDrawable(R.drawable.shape_live_seat_online_bg));
                RelativeLayout rl_mic_audio_value = view.findViewById(R.id.rl_mic_audio_value);
                ImageView imageView = view.findViewById(R.id.iv_room_creator_portrait);
                //根据麦位信息来判断是否开启还是关闭动画效果
                ImageView ivBackGroup = view.findViewById(R.id.iv_background);
                TextView tv_gift_count = view.findViewById(R.id.tv_gift_count);

                if (TextUtils.equals(seatInfo.getUserId(), getCreateUserId())) {
                    tv_gift_count.setVisibility(View.GONE);
                    name.setVisibility(paramter.getMixType() == RCLiveMixType.RCMixTypeOneToSix.getValue() ? View.GONE : View.VISIBLE);
                } else {
                    tv_gift_count.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    if (giftMap != null) {
                        String giftCount = giftMap.get(seatInfo.getUserId());
                        tv_gift_count.setText(TextUtils.isEmpty(giftCount) ? "0" : giftCount);
                    }
                }
                ImageView isMuteView = view.findViewById(R.id.iv_is_mute);
                if (seatInfo.isMute()) {
                    isMuteView.setVisibility(View.VISIBLE);
                } else {
                    isMuteView.setVisibility(View.GONE);
                }
                UserProvider.provider().getAsyn(seatInfo.getUserId(), new IResultBack<UserInfo>() {
                    @Override
                    public void onResult(UserInfo userInfo) {
                        name.setText(userInfo.getName());
                        ImageLoaderUtil.INSTANCE.loadImage(mView.getLiveActivity(), imageView, userInfo.getPortraitUri().toString(), R.drawable.default_portrait);
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
                EToast.showToast("该座位已锁定");
                return;
            }
            if (LiveEventHelper.getInstance().getCurrentStatus() != CurrentStatusType.STATUS_ON_SEAT) {
                //如果当前用户不在麦位上,那么去申请麦位
                requestSeat(rcLiveSeatInfo.getIndex());
            } else {
                //切换麦位
                LiveEventHelper.getInstance().swichToSeat(rcLiveSeatInfo.getIndex(), null);
            }
        } else {
            //麦位有人
            if (TextUtils.equals(rcLiveSeatInfo.getUserId(), RongCoreClient.getInstance().getCurrentUserId())) {
                //观众端-点击自己的麦位
                mView.showCreatorSettingFragment(rcLiveSeatInfo);
            } else {
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
        Log.e(TAG, "onSeatSpeak: seatIndex :" + seatInfo.getIndex() + "seatAudio:" + audioLevel);
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
                || TextUtils.equals(AccountStore.INSTANCE.getUserId(), getCreateUserId()))
            return;
        OkApi.get(VRApi.getRoomInfo(message.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        // 房间有密码需要弹框验证密码
                        if (roomBean.isPrivate()) {
                            new InputPasswordDialog(mView.getLiveActivity(), false, () -> null, s -> {
                                if (TextUtils.isEmpty(s)) {
                                    return null;
                                }
                                if (s.length() < 4) {
                                    mView.showToast("请输入四位密码");
                                    return null;
                                }
                                if (TextUtils.equals(s, roomBean.getPassword())) {
                                    exitRoom(roomBean.getRoomType(), roomBean.getRoomId());
                                } else {
                                    mView.showToast("密码错误");
                                }
                                return null;
                            }).show();
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
        if (VoiceRoomProvider.provider().contains(roomId)) {
            mView.switchOtherRoom(roomId);
        } else {
            mView.showLoading("");
            LiveEventHelper.getInstance().leaveRoom(new LeaveRoomCallBack() {
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
