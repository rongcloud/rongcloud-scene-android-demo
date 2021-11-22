package cn.rongcloud.voiceroom.room;

import static cn.rong.combusis.EventBus.TAG.UPDATE_SHIELD;
import static cn.rong.combusis.sdk.Api.EVENT_ADD_SHIELD;
import static cn.rong.combusis.sdk.Api.EVENT_AGREE_MANAGE_PICK;
import static cn.rong.combusis.sdk.Api.EVENT_BACKGROUND_CHANGE;
import static cn.rong.combusis.sdk.Api.EVENT_DELETE_SHIELD;
import static cn.rong.combusis.sdk.Api.EVENT_KICKED_OUT_OF_ROOM;
import static cn.rong.combusis.sdk.Api.EVENT_KICK_OUT_OF_SEAT;
import static cn.rong.combusis.sdk.Api.EVENT_MANAGER_LIST_CHANGE;
import static cn.rong.combusis.sdk.Api.EVENT_REJECT_MANAGE_PICK;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_AGREE;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_CANCEL;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_REFUSE;
import static cn.rong.combusis.sdk.Api.EVENT_ROOM_CLOSE;
import static cn.rong.combusis.sdk.event.wrapper.EToast.showToast;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.basis.mvp.BasePresenter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.UIKit;
import com.kit.utils.Logger;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.AudioManagerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rong.combusis.EventBus;
import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
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
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.sdk.event.EventHelper;
import cn.rong.combusis.sdk.event.listener.LeaveRoomCallBack;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rong.combusis.ui.OnItemClickListener;
import cn.rong.combusis.ui.room.dialog.shield.Shield;
import cn.rong.combusis.ui.room.fragment.BackgroundSettingFragment;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.MemberSettingFragment;
import cn.rong.combusis.ui.room.fragment.gift.GiftFragment;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomBackgroundFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomLockAllSeatFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomMuteAllFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomMuteFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSeatModeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomSeatSizeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rong.combusis.ui.room.model.Member;
import cn.rong.combusis.ui.room.model.MemberCache;
import cn.rong.combusis.ui.room.widget.RoomTitleBar;
import cn.rongcloud.rtc.core.NetworkMonitorAutoDetect;
import cn.rongcloud.voiceroom.R;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import cn.rongcloud.voiceroom.room.dialogFragment.CreatorSettingFragment;
import cn.rongcloud.voiceroom.room.dialogFragment.EmptySeatFragment;
import cn.rongcloud.voiceroom.room.dialogFragment.SelfSettingFragment;
import cn.rongcloud.voiceroom.room.dialogFragment.seatoperation.RevokeSeatRequestFragment;
import cn.rongcloud.voiceroom.room.dialogFragment.seatoperation.SeatOperationViewPagerFragment;
import cn.rongcloud.voiceroom.ui.uimodel.UiRoomModel;
import cn.rongcloud.voiceroom.ui.uimodel.UiSeatModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * 语聊房present
 */
public class VoiceRoomPresenter extends BasePresenter<IVoiceRoomFragmentView> implements OnItemClickListener<MutableLiveData<IFun.BaseFun>>,
        IVoiceRoomPresent, MemberSettingFragment.OnMemberSettingClickListener
        , BackgroundSettingFragment.OnSelectBackgroundListener, GiftFragment.OnSendGiftListener, RoomTitleBar.OnFollowClickListener {

    public static final int STATUS_ON_SEAT = 0;
    public static final int STATUS_NOT_ON_SEAT = 1;
    public static final int STATUS_WAIT_FOR_SEAT = 2;
    public int currentStatus = STATUS_NOT_ON_SEAT;
    private String TAG = "NewVoiceRoomPresenter";
    /**
     * 语聊房model
     */
    private VoiceRoomModel voiceRoomModel;
    /**
     * 房间信息
     */
    private VoiceRoomBean mVoiceRoomBean;
    private ConfirmDialog confirmDialog;
    private RoomOwnerType roomOwnerType;
    private List<Shield> shields = new ArrayList<>();

    //监听事件全部用集合管理,所有的监听事件需要在离开当前房间的时候全部取消注册
    private List<Disposable> disposableList = new ArrayList<>();
    private EmptySeatFragment emptySeatFragment;
    private NetworkMonitorAutoDetect networkMonitorAutoDetect;
    private boolean isNetWorkConnect;
    private boolean isInRoom;
    private String notice;

    public VoiceRoomPresenter(IVoiceRoomFragmentView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
        voiceRoomModel = new VoiceRoomModel(this, lifecycle);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public void addDisposable(Disposable disposable) {
        disposableList.add(disposable);
    }

    public String getNotice() {
        return notice;
    }

    /**
     * 初始化
     *
     * @param roomId
     * @param isCreate
     */
    public void init(String roomId, boolean isCreate) {
        isInRoom = TextUtils.equals(EventHelper.helper().getRoomId(), roomId);
        // TODO 请求数据
        getRoomInfo(roomId, isCreate);
    }

    /**
     * 获取房间信息
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
                            initListener(roomId);
                            currentStatus = EventHelper.helper().getCurrentStatus();
                            mView.changeStatus(currentStatus);
                            voiceRoomModel.currentUIRoomInfo.setMute(EventHelper.helper().getMuteAllRemoteStreams());
                            voiceRoomModel.onSeatInfoUpdate(EventHelper.helper().getRCVoiceSeatInfoList());
                            setCurrentRoom(mVoiceRoomBean);
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

    private void leaveRoom(String roomId, boolean isCreate, boolean isExit) {
        // 先退出上个房间
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                Logger.d("==============leaveRoom onSuccess");
                EventHelper.helper().changeUserRoom("");
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
                Logger.e("==============leaveRoom onError,code:" + code + ",message:" + message);
                if (isExit) {
                    joinRoom(roomId, isCreate);
                }
            }
        });
    }

    private void joinRoom(String roomId, boolean isCreate) {
        //设置界面监听
        EventHelper.helper().regeister(roomId);

        initListener(roomId);
        //重置底部状态
        currentStatus = STATUS_NOT_ON_SEAT;
        mView.changeStatus(currentStatus);
        if (isCreate) {
            RCVoiceRoomInfo rcVoiceRoomInfo = new RCVoiceRoomInfo();
            rcVoiceRoomInfo.setRoomName(mVoiceRoomBean.getRoomName());
            rcVoiceRoomInfo.setSeatCount(9);
            rcVoiceRoomInfo.setFreeEnterSeat(false);
            rcVoiceRoomInfo.setLockAll(false);
            rcVoiceRoomInfo.setMuteAll(false);
            RCVoiceRoomEngine.getInstance().createAndJoinRoom(roomId, rcVoiceRoomInfo, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    Logger.d("==============createAndJoinRoom onSuccess");
                    EventHelper.helper().changeUserRoom(roomId);
                    setCurrentRoom(mVoiceRoomBean);
                    mView.dismissLoading();
                }

                @Override
                public void onError(int code, String message) {
                    Logger.e("==============createAndJoinRoom onError,code:" + code + ",message:" + message);
                    mView.dismissLoading();
                }
            });
        } else {
            RCVoiceRoomEngine.getInstance().joinRoom(roomId, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    Logger.d("==============joinRoom onSuccess");
                    EventHelper.helper().changeUserRoom(roomId);
                    setCurrentRoom(mVoiceRoomBean);
                    mView.dismissLoading();
                }

                @Override
                public void onError(int code, String message) {
                    Logger.e("==============joinRoom onError,code:" + code + ",message:" + message);
                    mView.dismissLoading();
                }
            });
        }
    }

    /**
     * 进入房间后发送默认的消息
     */
    private void sendSystemMessage() {
        if (mVoiceRoomBean != null) {
            mView.showMessage(null, true);
            // 默认消息
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("欢迎来到 %s", mVoiceRoomBean.getRoomName()));
            RCChatRoomMessageManager.INSTANCE.sendLocationMessage(mVoiceRoomBean.getRoomId(), welcome);

            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("感谢使用融云 RTC 语音房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。");
            RCChatRoomMessageManager.INSTANCE.sendLocationMessage(mVoiceRoomBean.getRoomId(), tips);
            Logger.d("=================发送了默认消息");
            // 广播消息
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(AccountStore.INSTANCE.getUserId());
            enter.setUserName(AccountStore.INSTANCE.getUserName());
            RCChatRoomMessageManager.INSTANCE.sendChatMessage(mVoiceRoomBean.getRoomId(), enter, false,
                    integer -> null, (coreErrorCode, integer) -> null);
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

    public String getThemePictureUrl() {
        if (mVoiceRoomBean != null) {
            return mVoiceRoomBean.getThemePictureUrl();
        }
        return "";
    }

    public RoomOwnerType getRoomOwnerType() {
        return roomOwnerType;
    }

    /**
     * 设置当前的voiceBean
     *
     * @param mVoiceRoomBean
     */
    @Override
    public void setCurrentRoom(VoiceRoomBean mVoiceRoomBean) {
        roomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(mVoiceRoomBean);
        // 房主进入房间，如果不在麦位上那么自动上麦
        if (roomOwnerType == RoomOwnerType.VOICE_OWNER && !voiceRoomModel.userInSeat() && !isInRoom) {
            roomOwnerEnterSeat();
        }
        if (isInRoom) {
            //恢复一下当前信息就可以了
            List<MessageContent> messageList = EventHelper.helper().getMessageList();
            mView.showMessageList(messageList, true);
        } else {
            // 发送默认消息
            sendSystemMessage();
        }
        //界面初始化成功的时候，要去请求网络
        voiceRoomModel.getRoomInfo(mVoiceRoomBean.getRoomId()).subscribe();
        //刷新房间信息
        MemberCache.getInstance().fetchData(mVoiceRoomBean.getRoomId());
        //监听房间里面的人
        MemberCache.getInstance().getMemberList().observe(((VoiceRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                //人数
                mView.setOnlineCount(users.size());
                voiceRoomModel.onMemberListener(users);
            }
        });
        MemberCache.getInstance().getAdminList().observe(((VoiceRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                mView.refreshMessageList();
            }
        });
        MemberCache.getInstance().getAdminList().observe(((VoiceRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                mView.refreshSeat();
            }
        });
        //获取屏蔽词
        getShield(null);
        getGiftCount();
        mView.setRoomData(mVoiceRoomBean);
    }

    @Override
    public VoiceRoomBean getmVoiceRoomBean() {
        return mVoiceRoomBean;
    }


    @Override
    public void initListener(String roomId) {
        //注册model关于房间的监听
        EventHelper.helper().setRCVoiceRoomEventListener(voiceRoomModel);
        setObSeatListChange();
        setObRoomEventChange();
        setRequestSeatListener();
        setObSeatInfoChange();
        setObRoomInfoChange();
        setObShieldListener();
        setNetWorkChangleListener();
        setObMessageListener();
    }

    /**
     * 网络状态监听
     */
    private void setNetWorkChangleListener() {

        networkMonitorAutoDetect = new NetworkMonitorAutoDetect(new NetworkMonitorAutoDetect.Observer() {
            @Override
            public void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType newConnectionType) {
                //连接网络类型发生了改变
                Log.e(TAG, "onConnectionTypeChanged: ");
            }

            @Override
            public void onNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation networkInfo) {
                //网络连接
                if (isNetWorkConnect) {
                    //说明是断网重连状态
                    isNetWorkConnect = false;
                    Log.e(TAG, "onNetworkConnect: " + "断网重连成功");
                    UiSeatModel uiSeatModel = voiceRoomModel.getSeatInfoByUserId(AccountStore.INSTANCE.getUserId());
                    if (uiSeatModel != null && uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                        //说明当前用户在本地的状态是在麦，与RTC那边做对比，如果远程显示不在麦位上了，那么本地需要重新上麦
                        //如果远程显示也在麦位上，那么不需要做任何操作
                    }
                }
            }

            @Override
            public void onNetworkDisconnect(long networkHandle) {
                //网络断开
                isNetWorkConnect = true;
                Log.e(TAG, "onNetworkDisconnect: ");
            }
        }, ((VoiceRoomFragment) mView).requireContext());
    }

    /**
     * 监听自己删除或者添加屏蔽词
     */
    private void setObShieldListener() {
        EventBus.get().on(UPDATE_SHIELD, new EventBus.EventCallback() {
            @Override
            public void onEvent(String tag, Object... args) {
                getShield(null);
            }
        });
    }


    /**
     * 监听房间的信息
     */
    private void setObRoomInfoChange() {
        disposableList.add(voiceRoomModel.obRoomInfoChange()
                .subscribe(new Consumer<UiRoomModel>() {
                    @Override
                    public void accept(UiRoomModel uiRoomModel) throws Throwable {

                        String extra = "";
                        if (uiRoomModel.getRcRoomInfo() != null) {
                            extra = uiRoomModel.getRcRoomInfo().getExtra();
                            mView.setVoiceName(uiRoomModel.getRcRoomInfo().getRoomName());
                        }
                        if (mVoiceRoomBean != null) {
                            notice = TextUtils.isEmpty(extra) ? String.format("欢迎来到 %s", mVoiceRoomBean.getRoomName()) : extra;
                            mView.setNotice(notice);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "setObRoomInfoChange: " + throwable);
                    }
                }));
    }

    /**
     * 麦位信息改变监听
     */
    private void setObSeatInfoChange() {
        disposableList.add(voiceRoomModel.obSeatInfoChange().subscribe(new Consumer<UiSeatModel>() {
            @Override
            public void accept(UiSeatModel uiSeatModel) throws Throwable {
                //根据位置去刷新波纹
                int index = uiSeatModel.getIndex();
                if (index == 0) {
                    mView.refreshRoomOwner(uiSeatModel);
                } else {
                    //刷新别的地方的波纹
                    mView.onSeatListChange(voiceRoomModel.getUiSeatModels());
                }
            }
        }));
    }

    /**
     * 设置请求上麦监听
     */
    private void setRequestSeatListener() {
        voiceRoomModel.obRequestSeatListChange()
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        //申请的，通知底部弹窗的刷新
                        mView.showUnReadRequestNumber(users.size());
                    }
                });
    }

    /**
     * 设置接收消息的监听（包括自己发送的，以及外部发送过来的）
     * TODO 多次订阅导致了多次回调，导致消息重复
     */
    private void setObMessageListener() {
        disposableList.add(RCChatRoomMessageManager.INSTANCE.obMessageReceiveByRoomId(mVoiceRoomBean.getRoomId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageContent>() {
                    @Override
                    public void accept(MessageContent messageContent) throws Throwable {
                        //将消息显示到列表上
                        Class<? extends MessageContent> aClass = messageContent.getClass();
                        if (RCChatroomVoice.class.equals(aClass) || RCChatroomLocationMessage.class.equals(aClass)
                                || RCChatroomVoice.class.equals(aClass) || RCChatroomBarrage.class.equals(aClass)
                                || RCChatroomEnter.class.equals(aClass) || RCChatroomKickOut.class.equals(aClass)
                                || RCChatroomGift.class.equals(aClass) || RCChatroomAdmin.class.equals(aClass)
                                || RCChatroomSeats.class.equals(aClass) || RCChatroomGiftAll.class.equals(aClass)
                                || RCFollowMsg.class.equals(aClass) || TextMessage.class.equals(aClass)) {
                            // fix：悬浮框 接收pk邀请
                            if (null != mView) mView.showMessage(messageContent, false);
                        }
                        if (RCChatroomGift.class.equals(aClass) || RCChatroomGiftAll.class.equals(aClass)) {
                            getGiftCount();
                        } else if (aClass.equals(RCChatroomLike.class)) {
                            if (null != mView) mView.showLikeAnimation();
                            return;
                        } else if (aClass.equals(RCAllBroadcastMessage.class)) {
                            AllBroadcastManager.getInstance().addMessage((RCAllBroadcastMessage) messageContent);
                        } else if (aClass.equals(RCChatroomSeats.class)) {
                            refreshRoomMember();
                        } else if (aClass.equals(RCChatroomLocationMessage.class)) {
                            EventHelper.helper().addMessage(messageContent);
                        }
                    }
                }));
    }


    /**
     * 空座位被点击
     *
     * @param position 指的是麦位 Recyclerview的位置，真是的麦位应该是Position+1
     */
    @Override
    public void enterSeatViewer(int position) {
        //判断是否在麦位上
        if (voiceRoomModel.userInSeat()) {
            //在麦位上
            RCVoiceRoomEngine.getInstance().switchSeatTo(position + 1, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    AudioManagerUtil.INSTANCE.choiceAudioModel();
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast(message);
                }
            });
        } else {
            //不在麦位上
            requestSeat(position + 1);
        }
    }

    /**
     * 申请连麦
     *
     * @param position
     */
    public void requestSeat(int position) {
        if (currentStatus == STATUS_ON_SEAT) {
            //如果是麦位上
            return;
        }
        //如果当前正在等待并且不可以自有上麦的模式
        if (currentStatus == STATUS_WAIT_FOR_SEAT && !voiceRoomModel.currentUIRoomInfo.isFreeEnterSeat()) {
            mView.showRevokeSeatRequest();
            return;
        }
        //如果是自由上麦模式
        if (voiceRoomModel.currentUIRoomInfo.isFreeEnterSeat()) {
            int index = position;
            if (index == -1) {
                index = voiceRoomModel.getAvailableIndex();
            }
            if (index == -1) {
                mView.showToast("当前麦位已满");
                return;
            }
            RCVoiceRoomEngine.getInstance().enterSeat(index, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    mView.showToast("上麦成功");
                    AudioManagerUtil.INSTANCE.choiceAudioModel();
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast(message);
                }
            });
        } else {
            RCVoiceRoomEngine.getInstance().requestSeat(new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    currentStatus = STATUS_WAIT_FOR_SEAT;
                    mView.changeStatus(STATUS_WAIT_FOR_SEAT);
                    mView.showToast("已申请连线，等待房主接受");
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast("请求连麦失败");
                }
            });
        }
    }

    /**
     * 空座位被点击 房主
     *
     * @param position
     */
    public void enterSeatOwner(UiSeatModel seatModel, int position) {
        if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty || seatModel.getSeatStatus() ==
                RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            //如果当前是空座位或者是上锁的座位
            if (emptySeatFragment == null) {
                emptySeatFragment = new EmptySeatFragment();
            }
            emptySeatFragment.setData(getRoomId(), seatModel, voiceRoomModel);
            emptySeatFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
        } else if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            //如果座位正在使用中
        }
    }

    /**
     * 监听麦位改变
     */
    private void setObSeatListChange() {
        disposableList.add(voiceRoomModel.obSeatListChange()
                .subscribe(new Consumer<List<UiSeatModel>>() {
                    @Override
                    public void accept(List<UiSeatModel> uiSeatModels) throws Throwable {
                        mView.onSeatListChange(uiSeatModels);
                        refreshCurrentStatus(uiSeatModels);
                        getGiftCount();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "setObSeatListChange: " + throwable);
                    }
                }));
    }

    /**
     * 刷新当前的状态
     *
     * @param uiSeatModels
     */
    private synchronized void refreshCurrentStatus(List<UiSeatModel> uiSeatModels) {
        try {
            boolean inseat = false;
            for (UiSeatModel uiSeatModel : uiSeatModels) {
                if (!TextUtils.isEmpty(uiSeatModel.getUserId()) && !TextUtils.isEmpty(AccountStore.INSTANCE.getUserId()) &&
                        uiSeatModel.getUserId().equals(AccountStore.INSTANCE.getUserId())) {
                    //说明在麦位上
                    inseat = true;
                    break;
                }
            }
            if (inseat) {
                //当前用户已经在麦位上
                currentStatus = STATUS_ON_SEAT;
                mView.changeStatus(currentStatus);
            } else {
                //当前用户不在麦位上
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //说明已经申请了上麦,那么等着对方给判断就好，不用做特定的操作
                } else {
                    //当前用户不在麦位上
                    currentStatus = STATUS_NOT_ON_SEAT;
                }
            }
            mView.changeStatus(currentStatus);
        } catch (Exception e) {
            Log.e(TAG, "refreshCurrentStatus: " + e);
        }
    }


    /**
     * 监听房间的改变
     */
    private void setObRoomEventChange() {
        disposableList.add(voiceRoomModel.obRoomEventChange().subscribe(new Consumer<Pair<String, ArrayList<String>>>() {
            @Override
            public void accept(Pair<String, ArrayList<String>> stringArrayListPair) throws Throwable {
                switch (stringArrayListPair.first) {
                    case EVENT_ADD_SHIELD:
                        Shield shield = new Shield();
                        String name = stringArrayListPair.second.get(0);
                        shield.setName(name);
                        shields.add(shield);
                        break;
                    case EVENT_DELETE_SHIELD:
                        Iterator<Shield> iterator = shields.iterator();
                        String shile = stringArrayListPair.second.get(0);
                        while (iterator.hasNext()) {
                            Shield x = iterator.next();
                            if (x.getName().equals(shile)) {
                                iterator.remove();
                            }
                        }
                        break;
                    case EVENT_REQUEST_SEAT_AGREE://请求麦位被允许
                        currentStatus = STATUS_ON_SEAT;
                        //加入麦位
                        voiceRoomModel.enterSeatIfAvailable();
                        //去更改底部的状态显示按钮
                        mView.changeStatus(currentStatus);
                        break;
                    case EVENT_REQUEST_SEAT_REFUSE://请求麦位被拒绝
                        mView.showToast("您的上麦请求被拒绝");
                        currentStatus = STATUS_NOT_ON_SEAT;
                        //去更改底部的状态显示按钮
                        mView.changeStatus(currentStatus);
                        break;
                    case EVENT_KICK_OUT_OF_SEAT: //被抱下麦
                        mView.showToast("您已被抱下麦位");
                        break;
                    case EVENT_REQUEST_SEAT_CANCEL://撤销麦位申请
                        currentStatus = STATUS_NOT_ON_SEAT;
                        mView.changeStatus(currentStatus);
                        break;
                    case EVENT_MANAGER_LIST_CHANGE://管理员列表发生了变化
                        MemberCache.getInstance().refreshAdminData(getmVoiceRoomBean().getRoomId());
                        Log.e(TAG, "accept: " + "EVENT_MANAGER_LIST_CHANGE");
                        break;
                    case EVENT_KICKED_OUT_OF_ROOM://被踢出了房间
                        ArrayList<String> second = stringArrayListPair.second;
                        if (second.get(1).equals(AccountStore.INSTANCE.getUserId())) {
                            EToast.showToast("你已被踢出房间");
                            leaveRoom();
                        }
                        break;
                    case EVENT_ROOM_CLOSE://当前房间被关闭
                        ConfirmDialog confirmDialog = new ConfirmDialog(((VoiceRoomFragment) mView).requireContext(), "当前直播已结束", true
                                , "确定", "", null, new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                leaveRoom();
                                return null;
                            }
                        });
                        confirmDialog.setCancelable(false);
                        confirmDialog.show();
                        break;
                    case EVENT_BACKGROUND_CHANGE:
                        mView.setRoomBackground(stringArrayListPair.second.get(0));
                        break;
                    case EVENT_AGREE_MANAGE_PICK:
                        EToast.showToast("用户连线成功");
                        break;
                    case EVENT_REJECT_MANAGE_PICK:
                        if (stringArrayListPair.second.get(0).equals(AccountStore.INSTANCE.getUserId())) {
                            EToast.showToast("用户拒绝邀请");
                        }
                        break;
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                Log.e(TAG, "setObRoomEventChange: " + throwable);
            }
        }));
    }


    @Override
    public void onNetworkStatus(int i) {
        mView.onNetworkStatus(i);
    }


    /**
     * 麦位上点击自己的头像
     *
     * @param seatModel
     * @return
     */
    public SelfSettingFragment showNewSelfSettingFragment(UiSeatModel seatModel) {
        SelfSettingFragment selfSettingFragment = new SelfSettingFragment(seatModel, mVoiceRoomBean.getRoomId()
                , voiceRoomModel, AccountStore.INSTANCE.toUser());
        return selfSettingFragment;
    }

    /**
     * 房间所有者点击自己的头像
     */
    public void onClickRoomOwnerView(FragmentManager fragmentManager) {
        if (voiceRoomModel.getUiSeatModels().size() > 0) {
            UiSeatModel uiSeatModel = voiceRoomModel.getUiSeatModels().get(0);
            if (uiSeatModel != null) {
                if (!TextUtils.isEmpty(uiSeatModel.getUserId()) && uiSeatModel.getUserId().equals(AccountStore.INSTANCE.getUserId())) {
                    //如果在麦位上
                    CreatorSettingFragment creatorSettingFragment = new CreatorSettingFragment(voiceRoomModel, uiSeatModel, mVoiceRoomBean.getCreateUser());
                    creatorSettingFragment.show(fragmentManager);
                } else {
                    //如果不在麦位上，直接上麦
                    roomOwnerEnterSeat();
                }
            }
        }
    }

    /**
     * 发送消息
     *
     * @param messageContent
     */
    public void sendMessage(MessageContent messageContent) {
        //先判断是否包含了屏蔽词
        boolean isContains = false;
        if (shields != null) {
            for (Shield shield : shields) {
                if (messageContent instanceof RCChatroomBarrage) {
                    if (((RCChatroomBarrage) messageContent).getContent().contains(shield.getName())) {
                        isContains = true;
                        break;
                    }
                }
            }
            if (isContains) {
                //如果是包含了敏感词'
                mView.showMessage(messageContent, false);
                mView.clearInput();
                mView.hideSoftKeyboardAndIntput();
                return;
            }
        }
        RCChatRoomMessageManager.INSTANCE.sendChatMessage(mVoiceRoomBean.getRoomId(), messageContent, true
                , new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        if (null != mView) {// fix：悬浮框 同意pk崩溃
                            mView.clearInput();
                            mView.hideSoftKeyboardAndIntput();
                        }
                        EventHelper.helper().addMessage(messageContent);
                        if (messageContent instanceof RCChatroomAdmin) {
                            //发送成功，回调给接收的地方，统一去处理，避免多个地方处理 通知刷新管理员信息
                            RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_MANAGER_LIST_CHANGE, "", null);
                            MemberCache.getInstance().refreshAdminData(mVoiceRoomBean.getRoomId());
                        }
                        return null;
                    }
                }, new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
                    @Override
                    public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                        mView.showToast("发送失败");
                        return null;
                    }
                });
    }

    /**
     * 设置屏蔽词
     *
     * @param shields
     */
    public void setShield(List<String> shields) {
        Logger.e(shields.toString());
    }

    /**
     * 获取屏蔽词
     */
    public void getShield(WrapperCallBack callBack) {
        OkApi.get(VRApi.getShield(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Shield> list = result.getList(Shield.class);
                    shields.clear();
                    if (list != null) {
                        shields.addAll(list);
                    }
                    if (callBack != null) {
                        callBack.onResult(result);
                    }
                }
            }
        });
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

    @Override
    public void clickInviteSeat(User user, ClickCallback<Boolean> callback) {
        voiceRoomModel.clickInviteSeat(user.getUserId(), callback);
    }

    @Override
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        voiceRoomModel.clickKickRoom(user, callback);
    }

    @Override
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        voiceRoomModel.clickKickSeat(user, callback);
    }


    @Override
    public void clickMuteSeat(User user, ClickCallback<Boolean> callback) {
        clickMuteSeatByUser(user, callback);
    }

    /**
     * 座位开麦或者闭麦，通过当前麦位的位置的用户
     */
    public void clickMuteSeatByUser(User user, ClickCallback<Boolean> callback) {
        UiSeatModel uiSeatModel = voiceRoomModel.getSeatInfoByUserId(user.getUserId());
        if (uiSeatModel != null)
            voiceRoomModel.clickMuteSeat(uiSeatModel.getIndex(), !uiSeatModel.isMute(), callback);
    }


    /**
     * 关闭座位(根据用户的ID去关闭)
     *
     * @param user
     * @param callback
     */
    public void clickCloseSeatByUser(User user, ClickCallback<Boolean> callback) {
        UiSeatModel uiSeatModel = voiceRoomModel.getSeatInfoByUserId(user.getUserId());
        voiceRoomModel.clickCloseSeatByIndex(uiSeatModel.getIndex(), true, callback);
    }

    /**
     * 关闭座位(根据用户的ID去关闭)
     *
     * @param user
     * @param callback
     */
    @Override
    public void clickCloseSeat(User user, ClickCallback<Boolean> callback) {
        clickCloseSeatByUser(user, callback);
    }

    /**
     * 点击底部送礼物，礼物可以送给麦位和房主，无论房主是否在房间
     */
    public void sendGift() {
        ArrayList<Member> memberArrayList = new ArrayList<>();
        //房间内所有人
        ArrayList<UiSeatModel> uiSeatModels = voiceRoomModel.getUiSeatModels();
        for (UiSeatModel uiSeatModel : uiSeatModels) {
            if (uiSeatModel.getIndex() == 0) {
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
                member.setSeatIndex(uiSeatModel.getIndex());
                memberArrayList.add(member);
                continue;
            }
            if (uiSeatModel != null && uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                //当前用户在麦位上
                User user = MemberCache.getInstance().getMember(uiSeatModel.getUserId());
                Member member = null;
                if (user == null) {
                    member = new Member();
                    member.setUserId(uiSeatModel.getUserId());
                } else {
                    member = new Member().toMember(user);
                }
                member.setSeatIndex(uiSeatModel.getIndex());
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
        mView.showSendGiftDialog(mVoiceRoomBean, "", memberArrayList);
    }

    /**
     * 发送礼物 底部发送礼物的按钮
     *
     * @param user
     */
    @Override
    public void clickSendGift(User user) {
        mView.showSendGiftDialog(mVoiceRoomBean, user.getUserId(), Arrays.asList(new Member().toMember(user)));
    }

    /**
     * 发送关注消息
     *
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
     * 展示邀请和接受邀请fragment
     *
     * @param index
     */
    public void showSeatOperationViewPagerFragment(int index) {
        SeatOperationViewPagerFragment seatOperationViewPagerFragment = new SeatOperationViewPagerFragment(voiceRoomModel, index);
        seatOperationViewPagerFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
    }

    /**
     * 展示撤销麦位申请
     */
    public void showNewRevokeSeatRequestFragment() {
        RevokeSeatRequestFragment revokeSeatRequestFragment = new RevokeSeatRequestFragment(voiceRoomModel);
        revokeSeatRequestFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
    }

    /**
     * 弹窗收到上麦邀请弹窗
     *
     * @param isCreate 是否是房主
     * @param userId   邀请人的ID
     */
    public void showPickReceivedDialog(boolean isCreate, String userId) {
        String pickName = isCreate ? "房主" : "管理员";
        confirmDialog = new ConfirmDialog(((VoiceRoomFragment) mView).getActivity(),
                "您被" + pickName + "邀请上麦，是否同意?", true,
                "同意", "拒绝", new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //拒绝
                confirmDialog.dismiss();
                voiceRoomModel.refuseInvite(userId);
                return null;
            }
        }, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //同意
                voiceRoomModel.enterSeatIfAvailable();
                confirmDialog.dismiss();
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //被邀请上麦了，并且同意了，如果该用户已经申请了上麦，那么主动撤销掉申请
                    voiceRoomModel.cancelRequestSeat(null);
                }
                return null;
            }
        }
        );
        confirmDialog.show();
    }

    public void leaveRoom() {
        leaveRoom(null);
    }

    /**
     * 调用离开房间
     */
    public void leaveRoom(LeaveRoomCallBack callback) {
        mView.showLoading("");
        EventHelper.helper().leaveRoom(new LeaveRoomCallBack() {
            @Override
            public void onSuccess() {
                Logger.d("==============leaveRoom onSuccess");
                mView.dismissLoading();
                mView.finish();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                Logger.e("==============leaveRoom onError");
                mView.dismissLoading();
                mView.showToast(message);
            }
        });
    }

    /**
     * 房主关闭房间
     */
    public void closeRoom() {
        mView.showLoading("正在关闭房间");
        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_ROOM_CLOSE, "", null);
        EventHelper.helper().leaveRoom(new LeaveRoomCallBack() {
            @Override
            public void onSuccess() {
                //房主关闭房间，调用删除房间接口
                OkApi.get(VRApi.deleteRoom(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            mView.finish();
                            mView.dismissLoading();
                        } else {
                            mView.dismissLoading();
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

            @Override
            public void onError(int code, String message) {
                mView.dismissLoading();
                mView.showToast(message);
            }
        });
    }

    /**
     * 房主上麦
     */
    public void roomOwnerEnterSeat() {
        RCVoiceRoomEngine.getInstance().enterSeat(0, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                AudioManagerUtil.INSTANCE.choiceAudioModel();
                mView.enterSeatSuccess();
            }

            @Override
            public void onError(int i, String message) {
                mView.showToast(message);
            }
        });
    }


    /**
     * 修改房间公告
     *
     * @param notice
     */
    public void modifyNotice(String notice) {
        //判断公告是否有显示
        UiRoomModel currentUIRoomInfo = voiceRoomModel.currentUIRoomInfo;
        RCVoiceRoomInfo rcRoomInfo = currentUIRoomInfo.getRcRoomInfo();
        rcRoomInfo.setExtra(notice);
        RCVoiceRoomEngine.getInstance().setRoomInfo(rcRoomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                sendNoticeModifyMessage();
                //公告更新成功
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(int i, String s) {
                //公告更新失败
                Log.e(TAG, "onError: ");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //界面销毁，回收监听
        unInitListener();
    }

    /**
     * 回收掉对房间的各种监听
     */
    public void unInitListener() {
        for (Disposable disposable : disposableList) {
            disposable.dispose();
        }
        disposableList.clear();
        EventHelper.helper().removeRCVoiceRoomEventListener();
        EventBus.get().off(UPDATE_SHIELD, null);

        if (networkMonitorAutoDetect != null) {
            networkMonitorAutoDetect.destroy();
        }
    }

    /**
     * 发送公告更新的
     */
    private void sendNoticeModifyMessage() {
        TextMessage tips = TextMessage.obtain("房间公告已更新!");
        sendMessage(tips);
    }

    /**
     * 弹出设置弹窗
     */
    public void showSettingDialog() {
        List<MutableLiveData<IFun.BaseFun>> funList = Arrays.asList(
                new MutableLiveData<>(new RoomLockFun(mVoiceRoomBean.isPrivate() ? 1 : 0)),
                new MutableLiveData<>(new RoomNameFun(0)),
                new MutableLiveData<>(new RoomNoticeFun(0)),
                new MutableLiveData<>(new RoomBackgroundFun(0)),
                new MutableLiveData<>(new RoomSeatModeFun(voiceRoomModel.currentUIRoomInfo.isFreeEnterSeat() ? 1 : 0)),
                new MutableLiveData<>(new RoomMuteAllFun(voiceRoomModel.currentUIRoomInfo.isMuteAll() ? 1 : 0)),
                new MutableLiveData<>(new RoomLockAllSeatFun(voiceRoomModel.currentUIRoomInfo.isLockAll() ? 1 : 0)),
                new MutableLiveData<>(new RoomMuteFun(voiceRoomModel.currentUIRoomInfo.isMute() ? 1 : 0)),
                new MutableLiveData<>(new RoomSeatSizeFun(voiceRoomModel.currentUIRoomInfo.getMSeatCount() == 5 ? 1 : 0)),
                new MutableLiveData<>(new RoomShieldFun(0)),
                new MutableLiveData<>(new RoomMusicFun(0))
        );
        mView.showSettingDialog(funList);
    }

    /**
     * 点击设置的
     *
     * @param item
     * @param position
     */
    @Override
    public void clickItem(MutableLiveData<IFun.BaseFun> item, int position) {
        IFun.BaseFun fun = item.getValue();
        if (fun instanceof RoomNoticeFun) {
            mView.showNoticeDialog(true);
        } else if (fun instanceof RoomLockFun) {
            if (fun.getStatus() == 1) {
                setRoomPassword(false, "", item);
            } else {
                mView.showSetPasswordDialog(item);
            }
        } else if (fun instanceof RoomNameFun) {
            mView.showSetRoomNameDialog(mVoiceRoomBean.getRoomName());
        } else if (fun instanceof RoomBackgroundFun) {
            mView.showSelectBackgroundDialog(mVoiceRoomBean.getBackgroundUrl());
        } else if (fun instanceof RoomShieldFun) {
            mView.showShieldDialog(mVoiceRoomBean.getRoomId());
        } else if (fun instanceof RoomSeatModeFun) {
            if (fun.getStatus() == 1) {
                //申请上麦
                setSeatMode(false);
            } else {
                //自由上麦
                setSeatMode(true);
            }
        } else if (fun instanceof RoomMuteAllFun) {
            if (fun.getStatus() == 1) {
                //解锁全麦
                setAllSeatLock(false);
            } else {
                //全麦锁麦
                setAllSeatLock(true);
            }
        } else if (fun instanceof RoomLockAllSeatFun) {
            if (fun.getStatus() == 1) {
                //解锁全座
                lockOtherSeats(false);
            } else {
                //全麦锁座
                lockOtherSeats(true);
            }
        } else if (fun instanceof RoomMuteFun) {
            if (fun.getStatus() == 1) {
                //取消静音
                muteAllRemoteStreams(false);
            } else {
                //静音
                muteAllRemoteStreams(true);
            }
        } else if (fun instanceof RoomSeatSizeFun) {
            if (fun.getStatus() == 1) {
                //设置8个座位
                setSeatCount(9);
            } else {
                //设置4个座位
                setSeatCount(5);
            }
        } else if (fun instanceof RoomMusicFun) {
            //音乐 判断房主是否在麦位上
            UiSeatModel seatInfoByUserId = voiceRoomModel.getSeatInfoByUserId(AccountStore.INSTANCE.getUserId());
            if (seatInfoByUserId != null && seatInfoByUserId.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                //在座位上，可以播放音乐
                mView.showMusicDialog();
            } else {
                mView.showToast("请先上麦之后再播放音乐");
            }
        }
    }

    /**
     * 设置房间座位
     *
     * @param seatCount
     */
    private void setSeatCount(int seatCount) {
        voiceRoomModel.currentUIRoomInfo.setMSeatCount(seatCount);
        RCVoiceRoomEngine.getInstance().setRoomInfo(voiceRoomModel.currentUIRoomInfo.getRcRoomInfo(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //更换模式成功
                RCChatroomSeats rcChatroomSeats = new RCChatroomSeats();
                rcChatroomSeats.setCount(seatCount - 1);
                RCChatRoomMessageManager.INSTANCE.sendChatMessage(getmVoiceRoomBean().getRoomId(), rcChatroomSeats
                        , true, new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer integer) {
                                return null;
                            }
                        }, new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
                            @Override
                            public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                                return null;
                            }
                        });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 静音 取消静音
     *
     * @param isMute
     */
    private void muteAllRemoteStreams(boolean isMute) {
        RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute);
        voiceRoomModel.currentUIRoomInfo.setMute(isMute);
        EventHelper.helper().setMuteAllRemoteStreams(isMute);
        if (isMute) {
            EToast.showToast("扬声器已静音");
        } else {
            EToast.showToast("已取消静音");
        }
        //此时要将当前的状态同步到服务器，下次进入的时候可以同步
    }

    /**
     * 全麦锁座
     */
    private void lockOtherSeats(boolean isLockAll) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll, null);
        if (isLockAll) {
            EToast.showToast("全部座位已锁定");
        } else {
            EToast.showToast("已解锁全座");
        }
    }

    /**
     * 全麦锁麦
     *
     * @param isMuteAll
     */
    private void setAllSeatLock(boolean isMuteAll) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll, null);
        if (isMuteAll) {
            EToast.showToast("全部麦位已静音");
        } else {
            EToast.showToast("已解锁全麦");
        }
    }

    /**
     * 设置上麦的模式
     */
    public void setSeatMode(boolean isFreeEnterSeat) {
        voiceRoomModel.currentUIRoomInfo.setFreeEnterSeat(isFreeEnterSeat);
        RCVoiceRoomEngine.getInstance().setRoomInfo(voiceRoomModel.currentUIRoomInfo.getRcRoomInfo(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                if (isFreeEnterSeat) {
                    EToast.showToast("当前观众可自由上麦");
                } else {
                    EToast.showToast("当前观众上麦要申请");
                }
            }

            @Override
            public void onError(int i, String s) {
                EToast.showToast(s);
            }
        });
    }

    /**
     * 设置房间密码
     *
     * @param isPrivate
     * @param password
     * @param item
     */
    public void setRoomPassword(boolean isPrivate, String password, MutableLiveData<IFun.BaseFun> item) {
        int p = isPrivate ? 1 : 0;
        OkApi.put(VRApi.ROOM_PASSWORD,
                new OkParams()
                        .add("roomId", getmVoiceRoomBean().getRoomId())
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


    /**
     * 修改房间名称
     *
     * @param name
     */
    public void setRoomName(String name) {
        OkApi.put(VRApi.ROOM_NAME,
                new OkParams()
                        .add("roomId", getmVoiceRoomBean().getRoomId())
                        .add("name", name)
                        .build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            mView.showToast("修改成功");
                            mView.setVoiceName(name);
                            mVoiceRoomBean.setRoomName(name);
                            RCVoiceRoomInfo rcRoomInfo = voiceRoomModel.currentUIRoomInfo.getRcRoomInfo();
                            rcRoomInfo.setRoomName(name);
                            RCVoiceRoomEngine.getInstance().setRoomInfo(rcRoomInfo, new RCVoiceRoomCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG, "onSuccess: ");
                                }

                                @Override
                                public void onError(int i, String s) {
                                    Log.e(TAG, "onError: ");
                                }
                            });
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

    @Override
    public void selectBackground(String url) {
        OkApi.put(VRApi.ROOM_BACKGROUND, new OkParams()
                .add("roomId", mVoiceRoomBean.getRoomId())
                .add("backgroundUrl", url)
                .build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    mVoiceRoomBean.setBackgroundUrl(url);
                    mView.setRoomBackground(url);
                    //通知外部更改
                    RCVoiceRoomEngine.getInstance()
                            .notifyVoiceRoom(EVENT_BACKGROUND_CHANGE, url, null);
                    mView.showToast("设置成功");
                } else {
                    mView.showToast("设置失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                mView.showToast("设置失败");
            }
        });
    }

    @Override
    public void onSendGiftSuccess(List<MessageContent> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (MessageContent message : messages) {
                sendMessage(message);
            }
            getGiftCount();
        }
    }

    /**
     * 获取房间内礼物列表 ,刷新列表
     */
    public void getGiftCount() {
        if (mVoiceRoomBean != null && !TextUtils.isEmpty(mVoiceRoomBean.getRoomId()))
            OkApi.get(VRApi.getGiftList(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (result.ok()) {
                        Map<String, String> map = result.getMap();
                        Logger.e("================" + map.toString());
                        for (String userId : map.keySet()) {
                            UiSeatModel uiSeatModel = voiceRoomModel.getSeatInfoByUserId(userId);
                            String gifCount = map.get(userId);
                            if (uiSeatModel != null)
                                uiSeatModel.setGiftCount(Integer.parseInt(gifCount));
                        }
                    }
                }
            });
    }


    /**
     * 根据id获取用户信息
     */
    public void getUserInfo(String userId) {
        OkApi.post(VRApi.GET_USER, new OkParams().add("userIds", new String[]{userId}).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Member> members = result.getList(Member.class);
                    if (members != null && members.size() > 0) {
                        UiSeatModel uiSeatModel = voiceRoomModel.getSeatInfoByUserId(members.get(0).getUserId());
                        mView.showUserSetting(members.get(0), uiSeatModel);
                    }
                }
            }
        });
    }

    /**
     * 请求房间用户人数
     */
    public void refreshRoomMember() {
        MemberCache.getInstance().fetchData(getRoomId());
    }

    /**
     * 点击全局广播后跳转到相应的房间
     *
     * @param message
     */
    public void jumpRoom(RCAllBroadcastMessage message) {
        // 当前房间不跳转
        if (message == null || TextUtils.isEmpty(message.getRoomId()) || TextUtils.equals(message.getRoomId(), getRoomId())
                || voiceRoomModel.getSeatInfoByUserId(AccountStore.INSTANCE.getUserId()) != null
                || TextUtils.equals(AccountStore.INSTANCE.getUserId(), mVoiceRoomBean.getCreateUserId()))
            return;
        OkApi.get(VRApi.getRoomInfo(message.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        // 房间有密码需要弹框验证密码
                        if (roomBean.isPrivate()) {
                            new InputPasswordDialog(((VoiceRoomFragment) mView).requireContext(), false, () -> null, s -> {
                                if (TextUtils.isEmpty(s)) {
                                    return null;
                                }
                                if (s.length() < 4) {
                                    mView.showToast(UIKit.getResources().getString(R.string.text_please_input_four_number));
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
            leaveRoom(new LeaveRoomCallBack() {
                @Override
                public void onSuccess() {
                    IntentWrap.launchRoom(((VoiceRoomFragment) mView).requireContext(), roomType, roomId);
                }

                @Override
                public void onError(int code, String message) {

                }
            });
        }
    }


}
