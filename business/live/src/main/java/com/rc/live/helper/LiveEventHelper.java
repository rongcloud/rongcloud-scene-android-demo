package com.rc.live.helper;


import static com.rc.live.constant.CurrentStatusType.STATUS_NOT_ON_SEAT;
import static com.rc.live.constant.CurrentStatusType.STATUS_ON_SEAT;
import static com.rc.live.constant.CurrentStatusType.STATUS_WAIT_FOR_SEAT;
import static com.rc.live.constant.InviteStatusType.STATUS_CONNECTTING;
import static com.rc.live.constant.InviteStatusType.STATUS_NOT_INVITRED;
import static com.rc.live.constant.InviteStatusType.STATUS_UNDER_INVITATION;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.UIStack;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;
import com.basis.wapper.IRoomCallBack;
import com.basis.widget.dialog.VRCenterDialog;
import com.meihu.beauty.utils.MhDataManager;
import com.rc.live.constant.CurrentStatusType;
import com.rc.live.constant.InviteStatusType;
import com.rc.live.room.LiveRoomKvKey;

import java.util.ArrayList;
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
import cn.rongcloud.liveroom.api.RCLiveSeatViewProvider;
import cn.rongcloud.liveroom.api.RCParamter;
import cn.rongcloud.liveroom.api.callback.RCLiveCallback;
import cn.rongcloud.liveroom.api.callback.RCLiveResultCallback;
import cn.rongcloud.liveroom.api.error.RCLiveError;
import cn.rongcloud.liveroom.api.interfaces.RCLiveEventListener;
import cn.rongcloud.liveroom.api.interfaces.RCLiveLinkListener;
import cn.rongcloud.liveroom.api.interfaces.RCLivePKListener;
import cn.rongcloud.liveroom.api.interfaces.RCLiveSeatListener;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;
import cn.rongcloud.liveroom.api.model.RCLiveVideoPK;
import cn.rongcloud.liveroom.api.model.RCLivevideoFinishReason;
import cn.rongcloud.liveroom.manager.RCDataManager;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.pk.bean.PKInviteInfo;
import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.message.RCChatroomAdmin;
import cn.rongcloud.roomkit.message.RCChatroomBarrage;
import cn.rongcloud.roomkit.message.RCChatroomEnter;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.message.RCChatroomKickOut;
import cn.rongcloud.roomkit.message.RCChatroomLocationMessage;
import cn.rongcloud.roomkit.message.RCChatroomSeats;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import cn.rongcloud.roomkit.ui.miniroom.OnCloseMiniRoomListener;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCMixConfig;
import cn.rongcloud.rtc.api.stream.RCRTCCameraOutputStream;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCMicOutputStream;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCVideoFrame;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 5:30 下午
 * 用来直播房的各种监听事件  发送消息 麦位操作等
 * 维护一定的集合来返回事件
 */
public class LiveEventHelper implements ILiveEventHelper, RCLiveEventListener, RCLiveLinkListener, RCLiveSeatListener, RCLivePKListener, OnCloseMiniRoomListener {

    private String TAG = "LiveEventHelper";

    private List<MessageContent> messageList = new ArrayList<>();
    private String roomId;//直播房的房间ID
    private String createUserId;//房间创建人ID
    private VoiceRoomBean voiceRoomBean;//当前房间信息
    private CurrentStatusType currentStatus = STATUS_NOT_ON_SEAT;
    private CurrentStatusType lastStatus = STATUS_NOT_ON_SEAT;
    private InviteStatusType inviteStatusType = STATUS_NOT_INVITRED;
    private List<LiveRoomListener> liveRoomListeners = new ArrayList<>();
    private VRCenterDialog pickReceivedDialog;
    //麦克风是否被关闭
    private boolean isMute = false;
    private SparseArray<RCHolder> holder = new SparseArray<>(16);

    public boolean isMute() {
        return isMute;
    }

    public static LiveEventHelper getInstance() {
        return helper.INSTANCE;
    }

    @Override
    public CurrentStatusType getCurrentStatus() {
        return currentStatus;
    }

    @Override
    public void setCurrentStatus(CurrentStatusType currentStatus) {
        this.lastStatus = this.currentStatus;
        this.currentStatus = currentStatus;
    }

    public InviteStatusType getInviteStatusType() {
        return inviteStatusType;
    }

    public void setInviteStatusType(InviteStatusType inviteStatusType) {
        Logger.e(TAG, "setInviteStatusType: inviteStatusType = " + inviteStatusType);
        this.inviteStatusType = inviteStatusType;
    }

    @Override
    public void register(String roomId) {
        this.roomId = roomId;
        RCLiveEngine.getInstance().setLiveEventListener(this);
        RCLiveEngine.getInstance().getLinkManager().setLiveLinkListener(this);
        RCLiveEngine.getInstance().getSeatManager().setLiveSeatListener(this);
        RCLiveEngine.getInstance().setLivePKEventListener(this);
        setSeatViewProvider();
    }

    @Override
    public RCHolder getHold(int index) {
        if (holder.size() > index) {
            return holder.get(index);
        }
        return null;
    }

    @Override
    public void onSeatSpeak(RCLiveSeatInfo seatInfo, int audioLevel) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onSeatSpeak(seatInfo, audioLevel);
        }
    }

    @Override
    public void unRegister() {
        this.roomId = null;
        this.createUserId = null;
        voiceRoomBean = null;
        setCurrentStatus(STATUS_NOT_ON_SEAT);
        setInviteStatusType(STATUS_NOT_INVITRED);
        messageList.clear();
        RCLiveEngine.getInstance().unPrepare(null);
        isMute = false;
        holder.clear();
        removeSeatViewProvider();
        PKManager.get().unInit();
        MusicControlManager.getInstance().release();
    }

    /**
     * 设置provider
     */
    public void setSeatViewProvider() {
        RCLiveEngine.getInstance().setSeatViewProvider(new RCLiveSeatViewProvider() {

            @Override
            public void convert(RCHolder holder, RCLiveSeatInfo seat, RCParamter parameter) {
                for (LiveRoomListener liveRoomListener : liveRoomListeners) {
                    liveRoomListener.onBindView(holder, seat, parameter);
                }
            }

            @Override
            public View inflate(RCLiveSeatInfo seatInfo, RCParamter rcParamter) {
                for (LiveRoomListener liveRoomListener : liveRoomListeners) {
                    return liveRoomListener.inflaterSeatView(seatInfo, rcParamter);
                }
                return null;
            }

            @Override
            public void onListenerHolds(SparseArray<RCHolder> rcHolderSparseArray) {
                holder = rcHolderSparseArray;
            }
        });
    }

    /**
     * 移除provider
     */
    public void removeSeatViewProvider() {
        RCLiveEngine.getInstance().setSeatViewProvider(null);
    }


    public void setRoomBean(VoiceRoomBean voiceRoomBean) {
        this.voiceRoomBean = voiceRoomBean;
        this.createUserId = voiceRoomBean.getCreateUserId();
    }

    private String getRoomName() {
        if (voiceRoomBean != null) return voiceRoomBean.getRoomName();
        else return "";
    }

    @Override
    public void leaveRoom(IRoomCallBack callback) {
        if (voiceRoomBean != null) {
            RCRTCMicOutputStream defaultAudioStream = RCRTCEngine.getInstance().getDefaultAudioStream();
            RCRTCCameraOutputStream defaultVideoStream = RCRTCEngine.getInstance().getDefaultVideoStream();
            SensorsUtil.instance().leaveRoom(roomId, getRoomName(), voiceRoomBean.isPrivate(),
                    defaultAudioStream == null ? false : defaultAudioStream.isMicrophoneDisable(),
                    defaultVideoStream == null ? false : true, RoomType.LIVE_ROOM.convertToRcEvent(), "");

        }
        RCLiveEngine.getInstance().leaveRoom(new RCLiveCallback() {
            @Override
            public void onSuccess() {
                MusicControlManager.getInstance().stopPlayMusic();
                unRegister();
                changeUserRoom("");
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onError(code, error.getMessage());
            }
        });
    }

    @Override
    public void joinRoom(String roomId, ClickCallback<Boolean> callback) {
        register(roomId);
        RCLiveEngine.getInstance().joinCDNRoom(roomId, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                changeUserRoom(roomId);
                if (callback != null)
                    callback.onResult(true, "加入房间成功");
                Log.e(TAG, "onError: joinRoom Success:");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onResult(false, error.getMessage());
                Log.e(TAG, "onError: joinRoom Fail:" + error.getMessage());
            }
        });
    }

    /**
     * 如果麦位为-1，会自动查询第一个麦位
     *
     * @param userId
     * @param index
     * @param callback
     */
    @Override
    public void pickUserToSeat(String userId, int index, ClickCallback<Boolean> callback) {
        // 所有邀请都是自动查询第一个可用麦位
        RCLiveEngine.getInstance().getLinkManager().inviteLiveVideo(userId, -1, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                //如果为默认模式
                if (RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
                    setInviteStatusType(STATUS_UNDER_INVITATION);
                }
                if (callback != null) callback.onResult(true, "已邀请视频连线，等待对方接受");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, "邀请用户已失败");
            }
        });
    }

    /**
     * 撤销麦位邀请
     */
    @Override
    public void cancelInvitation(String userId, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getLinkManager().cancelInvitation(userId, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                setInviteStatusType(STATUS_NOT_INVITRED);
                if (callback != null) callback.onResult(true, "撤销视频连线邀请成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(true, "撤销麦位邀请失败");
            }
        });
    }

    /**
     * 接受上麦请求。如果上麦的麦位已被占用，SDK 会自动查询第一个空麦位
     *
     * @param userId   目标用户id
     * @param callback 结果回调
     */
    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getLinkManager().acceptRequest(userId, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null)
                    callback.onResult(true, "接受请求连麦成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onResult(false, "没有空闲麦位");
            }
        });
    }

    /**
     * 拒绝用户的上麦申请
     *
     * @param userId
     * @param callback
     */
    @Override
    public void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getLinkManager().rejectRequest(userId, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null)
                    callback.onResult(true, "拒绝请求连麦成功");
                MemberCache.getInstance().refreshMemberData(roomId);
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onResult(false, "拒绝请求连麦申请失败:" + error.getMessage());
            }
        });
    }

    @Override
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {
        SensorsUtil.instance().recallConnect(roomId, getRoomName(), RcEvent.LiveRoom);
        RCLiveEngine.getInstance().getLinkManager().cancelRequest(new RCLiveCallback() {
            @Override
            public void onSuccess() {
                Logger.e(TAG, "cancelRequestSeat");
                if (callback != null) {
                    setCurrentStatus(STATUS_NOT_ON_SEAT);
                    callback.onResult(true, "取消请求连麦成功");
                }

            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onResult(false, "取消请求连麦失败:" + error.getMessage());
            }
        });
    }

    @Override
    public void lockSeat(int index, boolean isClose, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getSeatManager().lock(index, isClose, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                //锁座位成功
                if (callback != null)
                    callback.onResult(true, isClose ? "座位已关闭" : "座位已开启");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                //锁座位失败
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().switchTo(seatIndex, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "切换麦位成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void muteSeat(int index, boolean isMute, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getSeatManager().mute(index, isMute, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                //座位禁麦成功
                if (callback != null) callback.onResult(true, "");
                if (isMute) {
                    KToast.show("此麦位已闭麦");
                } else {
                    KToast.show("已取消闭麦");
                }
            }

            @Override
            public void onError(int code, RCLiveError error) {
                //座位禁麦失败
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void switchVideoOrAudio(int index, boolean isVideo, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().getSeatManager().enableVideo(index, isVideo, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "");
                if (isVideo) {
                    KToast.show("切换为视频连线模式");
                } else {
                    KToast.show("切换为语音连线模式");
                }
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void MuteSelf(int index, boolean isMute, ClickCallback<Boolean> callback) {
        this.isMute = isMute;
        RCLiveEngine.getInstance().getSeatManager().enableAudio(index, !isMute, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "关闭麦克风成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(true, "关闭麦克风失败");
            }
        });
    }

    @Override
    public void kickUserFromRoom(User user, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().kickOutRoom(user.getUserId(), new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "踢出成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }


    /**
     * 抱下麦位
     *
     * @param user
     * @param callback
     */
    @Override
    public void kickUserFromSeat(User user, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().kickOutSeat(user.getUserId(), new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "抱下麦位成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void changeUserRoom(String roomId) {
        HashMap<String, Object> params = new OkParams()
                .add("roomId", roomId)
                .build();
        OkApi.get(VRApi.USER_ROOM_CHANGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    Log.e(TAG, "onResult: " + result.getMessage());
                }
            }
        });
    }

    /**
     * 因为SDK不具备真正意义的关闭房间，该方法设置为过时方法，可以不调用，而直接调用API关闭房间
     *
     * @param callback
     */
    @Override
    public void finishRoom(ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().finish(new RCLiveCallback() {
            @Override
            public void onSuccess() {
                unRegister();
                changeUserRoom("");
                if (callback != null)
                    callback.onResult(true, "关闭成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null)
                    callback.onResult(false, "关闭失败");
            }
        });
    }

    @Override
    public void begin(String roomId, ClickCallback<Boolean> callback) {
        register(roomId);
        RCLiveEngine.getInstance().begin(roomId, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                //开启直播并且加入房间成功
                Log.e(TAG, "onSuccess: ");
                changeUserRoom(roomId);
                if (callback != null)
                    callback.onResult(true, "开启直播成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                Log.e("TAG", "onError: " + code);
                if (callback != null)
                    callback.onResult(false, "开启直播失败" + ":" + code);
            }
        });
    }

    @Override
    public void prepare(ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().prepare(new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null)
                    callback.onResult(true, "准备直播成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                KToast.show(error.getMessage());
            }
        });
    }

    @Override
    public void requestLiveVideo(int index, ClickCallback<Boolean> callback) {
        SensorsUtil.instance().connectRequest(roomId, getRoomName(), RcEvent.LiveRoom);
        RCLiveEngine.getInstance().getLinkManager().requestLiveVideo(index, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                setCurrentStatus(STATUS_WAIT_FOR_SEAT);
                if (callback != null) {
                    callback.onResult(true, "");
                }
                KToast.show("已申请连线，等待房主接受");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) {
                    callback.onResult(false, error.getMessage());
                }
                KToast.show("请求连麦失败");
            }
        });
    }

    @Override
    public void enterSeat(int index, ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().enterSeat(index, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "连麦成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(true, error.getMessage());
            }
        });
    }

    @Override
    public void leaveSeat(ClickCallback<Boolean> callback) {
        RCLiveEngine.getInstance().leaveSeat(new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onResult(true, "断开链接成功");
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) callback.onResult(false, error.getMessage());
            }
        });
    }

    @Override
    public void updateRoomInfoKv(String key, String vaule, ClickCallback<Boolean> callback) {
        Map<String, String> kv = new HashMap<>();
        kv.put(key, vaule);
        RCLiveEngine.getInstance().setRoomInfo(kv, new RCLiveCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onResult(true, "更新" + key + "成功");
                }
            }

            @Override
            public void onError(int code, RCLiveError error) {
                if (callback != null) {
                    callback.onResult(false, "更新" + key + "失败:" + error.getMessage());
                }
            }
        });
    }

    /**
     * 获取KV消息
     *
     * @param key
     * @param callback
     */
    @Override
    public void getRoomInfoByKey(String key, ClickCallback<Boolean> callback) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                RCLiveEngine.getInstance().getRoomInfo(key, new RCLiveResultCallback<String>() {
                    @Override
                    public void onResult(String vaule) {
                        if (TextUtils.equals(LiveRoomKvKey.LIVE_ROOM_ENTER_SEAT_MODE, key)) {
                            //如果是查询上麦模式
                            if (TextUtils.isEmpty(vaule)) {
                                //默认为申请上麦
                                emitter.onNext("0");
                                return;
                            }
                        }
                        emitter.onNext(vaule);
                    }

                    @Override
                    public void onError(int code, RCLiveError error) {
                        emitter.onError(new Throwable(error.getMessage()));
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String vaule) throws Throwable {
                        if (callback != null) {
                            callback.onResult(true, vaule);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        if (callback != null) {
                            callback.onResult(false, throwable.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getRoomInfoByKey(List<String> keys, ClickCallback<Map<String, String>> callback) {
        Observable.create(new ObservableOnSubscribe<Map<String, String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Map<String, String>> emitter) throws Throwable {
                RCLiveEngine.getInstance().getRoomInfos(keys, new RCLiveResultCallback<Map<String, String>>() {

                    @Override
                    public void onResult(Map<String, String> result) {
                        emitter.onNext(result);
                    }


                    @Override
                    public void onError(int code, RCLiveError error) {
                        emitter.onError(new Throwable(error.getMessage()));
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, String>>() {
                    @Override
                    public void accept(Map<String, String> stringStringMap) throws Throwable {
                        if (callback != null) {
                            callback.onResult(stringStringMap, "查询成功");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        if (callback != null) {
                            callback.onResult(null, throwable.getMessage());
                        }
                    }
                });
    }

    /**
     * 获取正在申请的人
     *
     * @param callback
     */
    @Override
    public void getRequestLiveVideoIds(ClickCallback<List<String>> callback) {
        RCLiveEngine.getInstance().getLinkManager().getRequestLiveVideoIds(new RCLiveResultCallback<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                for (LiveRoomListener liveRoomListener : liveRoomListeners) {
                    liveRoomListener.onRequestLiveVideoIds(result);
                }
                if (callback != null) {
                    callback.onResult(result, "");
                }
            }

            @Override
            public void onError(int code, RCLiveError error) {

            }
        });
    }

    public List<MessageContent> getMessageList() {
        return messageList;
    }

    public String getRoomId() {
        return roomId;
    }

    /**
     * 监听直播房的一些事件
     *
     * @param liveRoomListener
     */
    public void addLiveRoomListeners(LiveRoomListener liveRoomListener) {
        Log.e(TAG, "addLiveRoomListeners: ");
        liveRoomListeners.add(liveRoomListener);
    }

    /**
     * 清除直播房fragment监听
     */
    public void removeLiveRoomListeners() {
        Log.e(TAG, "removeLiveRoomListeners: ");
        liveRoomListeners.clear();
    }

    /**
     * 发送消息
     *
     * @param messageContent 消息体
     * @param isShowLocation 是否在本地显示
     */
    @Override
    public void sendMessage(MessageContent messageContent, boolean isShowLocation) {
        if (!TextUtils.isEmpty(roomId))
            if (messageContent instanceof RCChatroomLocationMessage) {
                RCChatRoomMessageManager.sendLocationMessage(roomId, messageContent);
                if (isShowingMessage(messageContent)) {
                    messageList.add(messageContent);
                }
            } else {
                RCChatRoomMessageManager.sendChatMessage(roomId, messageContent, isShowLocation
                        , new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer integer) {
                                if (isShowLocation) {
                                    messageList.add(messageContent);
                                }
                                return null;
                            }
                        }, new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
                            @Override
                            public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                                KToast.show("发送失败");
                                return null;
                            }
                        });
            }

    }

    @Override
    public void onRoomInfoReady() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onRoomInfoReady();
        }
        Log.e(TAG, "onRoomInfoReady: ");
    }

    /**
     * @param key   直播间信息的存kv的key
     * @param value 直播间信息的存kv的value
     */
    @Override
    public void onRoomInfoUpdate(String key, String value) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onRoomInfoUpdate(key, value);
        }
        Log.e(TAG, "onRoomInfoUpdate: ");
    }


    @Override
    public void onUserEnter(String userId, int onlineCount) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onUserEnter(userId, onlineCount);
        }
        UserProvider.provider().getAsyn(userId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                RCChatroomEnter enter = new RCChatroomEnter();
                enter.setUserId(userId);
                enter.setUserName(userInfo.getName());
                Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, enter);
                onReceiveMessage(message);
            }
        });
        Log.e(TAG, "onUserEnter: " + userId);
    }

    @Override
    public void onUserExit(String userId, int onlineCount) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onUserExit(userId, onlineCount);
        }
        Log.e(TAG, "onUserExit: " + userId);
    }


    /**
     * 用户被踢出房间
     *
     * @param userId     被踢用户唯一标识
     * @param operatorId 踢人操作的执行用户的唯一标识
     */
    @Override
    public void onUserKickOut(String userId, String operatorId) {
        //被踢出房间，调用离开房间接口和反注册
        if (TextUtils.equals(userId, UserManager.get().getUserId())) {
            KToast.show(TextUtils.equals(operatorId, createUserId) ? "您被房主踢出房间" : "您被管理员踢出房间");
            MiniRoomManager.getInstance().close();
            leaveRoom(null);
        }
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onUserKickOut(userId, operatorId);
        }
        Log.e(TAG, "onUserKitOut: ");
    }

    /**
     * 连麦用户集合
     *
     * @param lineMicUserIds 连麦的用户集合
     */
    @Override
    public void onLiveVideoUpdate(List<String> lineMicUserIds) {
        if (lineMicUserIds.size() == 2 && RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue()) {
            //如果是默认连麦模式，并且已经是连麦中
            setInviteStatusType(STATUS_CONNECTTING);
        } else {
            setInviteStatusType(STATUS_NOT_INVITRED);
        }
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoUpdate(lineMicUserIds);
        }
        Log.e(TAG, "onLiveVideoUpdate: " + lineMicUserIds);
    }


    /**
     * 申请列表发生变化
     */
    @Override
    public void onLiveVideoRequestChange() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoRequestChange();
        }
        Log.e(TAG, "onLiveVideoRequestChanage: ");
    }

    /**
     * 申请上麦被同意：只有申请者收到回调
     */
    @Override
    public void onLiveVideoRequestAccepted() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoRequestAccepted();
        }
        Log.e(TAG, "onLiveVideoRequestAccepted: ");
    }

    /**
     * 申请被拒绝了
     */
    @Override
    public void onLiveVideoRequestRejected() {
        cancelRequestSeat(null);
        setCurrentStatus(STATUS_NOT_ON_SEAT);
        KToast.show("房主拒绝了您的上麦申请");
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoRequestRejected();
        }
        Log.e(TAG, "onLiveVideoRequestRejected: ");
    }


    /**
     * 申请上麦被取消：只有房主收到回调
     */
    @Override
    public void onLiveVideoRequestCanceled() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoRequestCanceled();
        }
        Log.e(TAG, "onLiveVideoRequestCanceled: ");
    }

    /**
     * 收到连线邀请
     */
    @Override
    public void onLiveVideoInvitationReceived(String userId, int index) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoInvitationReceived(userId, index);
        }
        showPickReceivedDialog(userId, index);
        Log.e(TAG, "onLiveVideoInvitationReceived: ");
    }


    /**
     * 弹窗收到上麦邀请弹窗
     *
     * @param userId
     * @param index
     */
    public void showPickReceivedDialog(String userId, int index) {
        String pickName = TextUtils.equals(userId, createUserId) ? "主播" : "管理员";
        new VRCenterDialog(UIStack.getInstance().getTopActivity(), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        VRCenterDialog pickReceivedDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
        pickReceivedDialog.replaceContent(pickName + "邀请您连线，是否同意? 10S", "拒绝", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝邀请
                RCLiveEngine.getInstance().getLinkManager().rejectInvitation(userId, null);

            }
        }, "同意", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意邀请
                RCLiveEngine.getInstance().getLinkManager().acceptInvitation(userId, index, new RCLiveCallback() {
                    @Override
                    public void onSuccess() {
                        Logger.e(TAG, "acceptInvitation:currentStatus = " + currentStatus);
                        if (currentStatus == STATUS_WAIT_FOR_SEAT || lastStatus == STATUS_WAIT_FOR_SEAT) {
                            //被邀请上麦了，并且同意了，如果该用户已经申请了上麦，那么主动撤销掉申请
                            cancelRequestSeat(null);
                        }
                    }

                    @Override
                    public void onError(int code, RCLiveError error) {
                        if (error.getCode() == 80502) {
                            KToast.show("没有空余麦位");
                        }
                    }
                });
            }
        }, null);
        Disposable subscribe = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(11)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        pickReceivedDialog.updateTitle(pickName + "邀请您连线，是否同意? " + (10 - aLong) + "s");
                        if (10 == aLong) {
                            //超时自动拒绝
                            RCLiveEngine.getInstance().getLinkManager().rejectInvitation(userId, null);
                            pickReceivedDialog.dismiss();
                        }
                    }
                });
        pickReceivedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (subscribe != null) {
                    subscribe.dispose();
                }
            }
        });
        pickReceivedDialog.show();
    }

    /**
     * 邀请被取消
     */
    @Override
    public void onLiveVideoInvitationCanceled() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoInvitationCanceled();
        }
        if (pickReceivedDialog != null) pickReceivedDialog.dismiss();
        Log.e(TAG, "onliveVideoInvitationCanceled: ");
    }

    /**
     * 邀请被同意
     *
     * @param userId
     */
    @Override
    public void onLiveVideoInvitationAccepted(String userId) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoInvitationAccepted(userId);
        }
        if (TextUtils.equals(userId, UserManager.get().getUserId())) {
            KToast.show("用户连线成功");
        }
        Log.e(TAG, "onLiveVideoInvitationAccepted: ");
    }

    /**
     * 邀请被拒绝
     *
     * @param userId 用户唯一标识
     */
    @Override
    public void onLiveVideoInvitationRejected(String userId) {
        setInviteStatusType(STATUS_NOT_INVITRED);
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoInvitationRejected(userId);
        }
        User member = MemberCache.getInstance().getMember(userId);
        if (member != null) {
            KToast.show("用户 " + member.getUserName() + " 已拒绝上麦");
        } else {
            KToast.show("用户 " + userId + " 已拒绝上麦");
        }
        Log.e(TAG, "onLiveVideoInvitationRejected: ");
    }

    /**
     * 连麦开始
     */
    @Override
    public void onLiveVideoStarted() {
        setCurrentStatus(STATUS_ON_SEAT);
        KToast.show("连线成功");
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoStarted();
        }
        Log.e(TAG, "onLiveVideoStarted: ");
    }

    /**
     * 连麦结束
     */
    @Override
    public void onLiveVideoStopped(RCLivevideoFinishReason reason) {
        setCurrentStatus(STATUS_NOT_ON_SEAT);
        if (reason == RCLivevideoFinishReason.RCLivevideoFinishReasonKick) {
            KToast.show("您被抱下麦位");
        } else if (reason == RCLivevideoFinishReason.RCLivevideoFinishReasonMix) {
            KToast.show("麦位切换模式，请重新上麦");
        }
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onLiveVideoStopped(reason);
        }
        Log.e(TAG, "onLiveVideoStopped: ");
    }

    /**
     * 收到消息
     *
     * @param message
     */
    @Override
    public void onReceiveMessage(Message message) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onReceiveMessage(message);
        }
        PKManager.get().onMessageReceived(message);
        //统一处理
        if (!TextUtils.isEmpty(roomId) && message.getConversationType() == Conversation.ConversationType.CHATROOM) {
            RCChatRoomMessageManager.onReceiveMessage(roomId, message.getContent());
            if (isShowingMessage(message.getContent())) {
                messageList.add(message.getContent());
            }
        }
        Log.e(TAG, "onReceiveMessage: " + message);
    }

    @Override
    public void onNetworkStatus(long delayMs) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onNetworkStatus(delayMs);
        }
    }

    /**
     * 是否显示在消息列表中的消息
     *
     * @param content
     * @return
     */
    public boolean isShowingMessage(MessageContent content) {
        if (content instanceof RCChatroomBarrage || content instanceof RCChatroomEnter
                || content instanceof RCChatroomKickOut || content instanceof RCChatroomGiftAll
                || content instanceof RCChatroomGift || content instanceof RCChatroomAdmin
                || content instanceof RCChatroomLocationMessage || content instanceof RCFollowMsg
                || content instanceof RCChatroomVoice || content instanceof TextMessage
                || content instanceof RCChatroomSeats) {
            return true;
        }
        return false;
    }

    /**
     * 处理美颜
     *
     * @param frame 视频流采样数据
     */
    @Override
    public void onOutputSampleBuffer(RCRTCVideoFrame frame) {
        int render = MhDataManager.getInstance().render(frame.getTextureId(), frame.getWidth(), frame.getWidth());
        frame.setTextureId(render);
    }

    @Override
    public void onFirstRemoteVideoFrame(String userId, String tag) {

    }

    @Override
    public void onReportFirstFrame(RCRTCInputStream stream, RCRTCMediaType mediaType) {

    }

    /**
     * RTC 初始化参数，用户可自定义
     *
     * @param builder
     * @return 返回null，那么用默认的
     */
    @Override
    public RCRTCConfig.Builder onInitRCRTCConfig(RCRTCConfig.Builder builder) {
        return null;
    }

    /**
     * RTC 合流参数，用户可自定义
     *
     * @param rcrtcMixConfig 合流参数
     * @return 如果返回null，那么用默认的
     */
    @Override
    public RCRTCMixConfig onInitMixConfig(RCRTCMixConfig rcrtcMixConfig) {
        return null;
    }

    @Override
    public void onRoomMixTypeChange(RCLiveMixType mixType, int customerType) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onRoomMixTypeChange(mixType, customerType);
        }
        Log.e(TAG, "onRoomMixTypeChange: " + mixType);
    }


    @Override
    public void onRoomDestroy() {
        //如果是房主的话，那么直接退出就可以了
        MiniRoomManager.getInstance().close();
        unRegister();
        if (TextUtils.equals(createUserId, RongCoreClient.getInstance().getCurrentUserId())) {
            onLiveRoomFinish();
            return;
        }
        VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
        confirmDialog.replaceContent("当前直播已结束", "", null, "确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLiveRoomFinish();
            }
        }, null);
        confirmDialog.show();
        Log.e(TAG, "onRoomDestroy: ");
    }

    /**
     * 当界面被销毁的时候
     */
    private void onLiveRoomFinish() {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onRoomDestroy();
        }
    }


    @Override
    public void onSeatLocked(RCLiveSeatInfo seatInfo, boolean locked) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onSeatLocked(seatInfo, locked);
        }
    }

    @Override
    public void onSeatMute(RCLiveSeatInfo seatInfo, boolean mute) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onSeatMute(seatInfo, mute);
        }
    }

    @Override
    public void onSeatAudioEnable(RCLiveSeatInfo seatInfo, boolean enable) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onSeatAudioEnable(seatInfo, enable);
        }
    }

    @Override
    public void onSeatVideoEnable(RCLiveSeatInfo seatInfo, boolean enable) {
        for (LiveRoomListener liveRoomListener : liveRoomListeners) {
            liveRoomListener.onSeatVideoEnable(seatInfo, enable);
        }
    }

    @Override
    public void onCloseMiniRoom(CloseResult closeResult) {
        leaveRoom(new IRoomCallBack() {
            @Override
            public void onSuccess() {
                if (closeResult != null) {
                    closeResult.onClose();
                }
            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    @Override
    public void onPKBegin(RCLiveVideoPK rcLiveVideoPK) {
        PKInviteInfo pkInviteInfo = new PKInviteInfo(rcLiveVideoPK.getInviterUserId(), rcLiveVideoPK.getInviterRoomId(), rcLiveVideoPK.getInviteeUserId(), rcLiveVideoPK.getInviteeRoomId());
        PKManager.get().onPKBegin(pkInviteInfo);
    }

    @Override
    public void onPKFinish() {
        PKManager.get().onPKFinish();
    }

    @Override
    public void onReceivePKInvitation(String inviterRoomId, String inviterUserId) {
        // 只有在连麦布局下，且没邀请别人的时候才能弹出pk请求
        if (RCDataManager.get().getMixType() == RCLiveMixType.RCMixTypeOneToOne.getValue() && getInviteStatusType() == STATUS_NOT_INVITRED) {
            UserProvider.provider().getAsyn(inviterUserId, new IResultBack<UserInfo>() {
                @Override
                public void onResult(UserInfo userInfo) {
                    PKManager.get().onReceivePKInvitation(inviterRoomId, inviterUserId);
                }
            });
        } else {
            // 房主在多麦位或连麦时，直接拒绝pk邀请，提示对方正忙
            RCLiveEngine.getInstance().rejectPKInvitation(inviterRoomId, inviterUserId, PKResponse.busy.name(), null);
        }
    }

    @Override
    public void onPKInvitationCanceled(String inviterRoomId, String inviterUserId) {
        Logger.d(TAG, "onPKInvitationCanceled");
        PKManager.get().onPKInvitationCanceled(inviterRoomId, inviterUserId);
    }

    @Override
    public void onAcceptPKInvitationFromRoom(String inviteeRoomId, String inviteeUserId) {

    }

    @Override
    public void onRejectPKInvitationFromRoom(String inviteeRoomId, String inviteeUserId, String reason) {
        PKResponse pkResponse = PKResponse.valueOf(reason);
        PKManager.get().onPKInvitationRejected(inviteeRoomId, inviteeUserId, pkResponse);
    }

    private static class helper {
        static final LiveEventHelper INSTANCE = new LiveEventHelper();
    }

}
