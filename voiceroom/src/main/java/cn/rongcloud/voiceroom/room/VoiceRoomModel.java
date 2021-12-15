package cn.rongcloud.voiceroom.room;


import static cn.rong.combusis.sdk.Api.EVENT_AGREE_MANAGE_PICK;
import static cn.rong.combusis.sdk.Api.EVENT_KICKED_OUT_OF_ROOM;
import static cn.rong.combusis.sdk.Api.EVENT_KICK_OUT_OF_SEAT;
import static cn.rong.combusis.sdk.Api.EVENT_REJECT_MANAGE_PICK;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_AGREE;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_CANCEL;
import static cn.rong.combusis.sdk.Api.EVENT_REQUEST_SEAT_REFUSE;
import static cn.rong.combusis.sdk.event.wrapper.EToast.showToast;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.basis.mvp.BaseModel;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.AudioManagerUtil;

import java.util.ArrayList;
import java.util.List;

import cn.rong.combusis.common.utils.SharedPreferUtil;
import cn.rong.combusis.manager.RCChatRoomMessageManager;
import cn.rong.combusis.message.RCChatroomKickOut;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.model.MemberCache;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import cn.rongcloud.voiceroom.net.VoiceRoomNetManager;
import cn.rongcloud.voiceroom.net.bean.respond.VoiceRoomInfoBean;
import cn.rongcloud.voiceroom.ui.uimodel.UiRoomModel;
import cn.rongcloud.voiceroom.ui.uimodel.UiSeatModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * 语聊房的逻辑处理
 */
public class VoiceRoomModel extends BaseModel<VoiceRoomPresenter> implements RCVoiceRoomEventListener {

    //线程调度器
    Scheduler dataModifyScheduler = Schedulers.computation();
    private String TAG = "NewVoiceRoomModel";
    //麦位信息变化监听器
    private BehaviorSubject<UiSeatModel> seatInfoChangeSubject = BehaviorSubject.create();

    //座位数量订阅器,为了让所有订阅的地方都能回调回去
    private BehaviorSubject<List<UiSeatModel>> seatListChangeSubject = BehaviorSubject.create();

    //房间信息发生改变订阅，比如房间被解散，上锁之类的
    private BehaviorSubject<UiRoomModel> roomInfoSubject = BehaviorSubject.create();
    public UiRoomModel currentUIRoomInfo = new UiRoomModel(roomInfoSubject);
    //房间事件监听（麦位 进入 踢出等等）
    private BehaviorSubject<Pair<String, ArrayList<String>>> roomEventSubject = BehaviorSubject.create();
    /**
     * 申请和撤销上麦下麦的监听
     */
    private BehaviorSubject<List<User>> obRequestSeatListChangeSuject = BehaviorSubject.create();
    /**
     * 可以被邀请的人员监听
     */
    private BehaviorSubject<List<User>> obInviteSeatListChangeSuject = BehaviorSubject.create();
    //本地麦克风的状态，默认是开启的
    private boolean recordingStatus = true;
    //麦位集合
    private volatile ArrayList<UiSeatModel> uiSeatModels = new ArrayList<>();

    //申请连麦的集合
    private ArrayList<User> requestSeats = new ArrayList<>();
    //可以被邀请的集合
    private ArrayList<User> inviteSeats = new ArrayList<>();

    public VoiceRoomModel(VoiceRoomPresenter present, Lifecycle lifecycle) {
        super(present, lifecycle);
    }

    public ArrayList<User> getInviteSeats() {
        return inviteSeats;
    }

    public ArrayList<User> getRequestSeats() {
        return requestSeats;
    }

    public ArrayList<UiSeatModel> getUiSeatModels() {
        return uiSeatModels;
    }

    public boolean isRecordingStatus() {
        return recordingStatus;
    }

    public void setRecordingStatus(boolean recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    /**
     * 监听麦位数量变化
     */
    public Observable<List<UiSeatModel>> obSeatListChange() {
        return seatListChangeSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取指定位置的麦位
     */
    public Observable<UiSeatModel> obSeatInfoByIndex(int index) {
        return seatListChangeSubject.map(new Function<List<UiSeatModel>, UiSeatModel>() {
            @Override
            public UiSeatModel apply(List<UiSeatModel> uiSeatModels) throws Throwable {
                return uiSeatModels.get(index);
            }
        }).subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 麦位信息发生了变化
     */
    public Observable<UiSeatModel> obSeatInfoChange() {
        return seatInfoChangeSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 监听房间的事件
     */
    public Observable<Pair<String, ArrayList<String>>> obRoomEventChange() {
        return roomEventSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(dataModifyScheduler);
    }

    /**
     * 监听房间的信息改变，比如上锁，解散之类的信息
     */
    public Observable<UiRoomModel> obRoomInfoChange() {
        return roomInfoSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 监听申请上麦和撤销申请的监听
     */
    public Observable<List<User>> obRequestSeatListChange() {
        return obRequestSeatListChangeSuject.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(dataModifyScheduler);
    }

    /**
     * 监听可以被邀请的人员
     */
    public Observable<List<User>> obInviteSeatListChange() {
        return obInviteSeatListChangeSuject.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(dataModifyScheduler);
    }

    @Override
    public void onRoomKVReady() {

    }

    @Override
    public void onRoomInfoUpdate(RCVoiceRoomInfo rcVoiceRoomInfo) {
        Log.e(TAG, "onRoomInfoUpdate: ");
        currentUIRoomInfo.setRcRoomInfo(rcVoiceRoomInfo);
    }

    /**
     * 麦位信息发生了变化
     * 这里用同步锁，避免多线程操作的时候，影响麦位的显示
     *
     * @param list
     */
    @Override
    public void onSeatInfoUpdate(List<RCVoiceSeatInfo> list) {
        synchronized (this) {
            int size = null == list ? 0 : list.size();
            uiSeatModels.clear();
            for (int i = 0; i < size; i++) {
                //构建一个集合返回去
                RCVoiceSeatInfo rcVoiceSeatInfo = list.get(i);
                if (!TextUtils.isEmpty(rcVoiceSeatInfo.getUserId())
                        && rcVoiceSeatInfo.getStatus().equals(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty)) {
                    rcVoiceSeatInfo.setStatus(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing);
                }
                UiSeatModel uiSeatModel = new UiSeatModel(i, rcVoiceSeatInfo, seatInfoChangeSubject);
                uiSeatModels.add(uiSeatModel);
            }
            seatListChangeSubject.onNext(uiSeatModels);
        }
    }


    /**
     * 用户加入麦位
     *
     * @param i
     * @param s
     */
    @Override
    public void onUserEnterSeat(int i, String s) {
        Log.e(TAG, "onUserEnterSeat: ");
        present.refreshRoomMember();
    }

    /**
     * 用户离开麦位,并且保证该用户在房间里面 那么该用户应该能够被邀请才对
     *
     * @param i
     * @param s
     */
    @Override
    public void onUserLeaveSeat(int i, String s) {
        Log.e(TAG, "onUserLeaveSeat: ");
        //如果是房主的话，那么去更新房主的信息
        present.refreshRoomMember();
    }

    /**
     * 麦位被禁止
     *
     * @param i
     * @param b
     */
    @Override
    public void onSeatMute(int i, boolean b) {
        UiSeatModel uiSeatModel = uiSeatModels.get(i);
        uiSeatModel.setMute(b);
        Log.e(TAG, "onSeatMute: ");
    }

    /**
     * 锁住当前座位
     *
     * @param i
     * @param b
     */
    @Override
    public void onSeatLock(int i, boolean b) {
        //锁住的位置，和状态
        present.refreshRoomMember();
        Log.e(TAG, "onSeatLock: ");
    }

    /**
     * 观众加入
     *
     * @param s
     */
    @Override
    public void onAudienceEnter(String s) {
        Log.e(TAG, "onAudienceEnter: ");
        present.refreshRoomMember();
    }

    /**
     * 观众退出
     *
     * @param s
     */
    @Override
    public void onAudienceExit(String s) {
        Log.e(TAG, "onAudienceExit: ");
        present.refreshRoomMember();
    }

    /**
     * 麦位的信息变化监听
     *
     * @param i
     * @param b
     */
    @Override
    public void onSpeakingStateChanged(int i, boolean b) {
        if (uiSeatModels.size() > i) {
            UiSeatModel uiSeatModel = uiSeatModels.get(i);
            uiSeatModel.setSpeaking(b);
        }
    }

    /**
     * 消息接收
     *
     * @param message 收到的消息
     */
    @Override
    public void onMessageReceived(Message message) {
        if (!TextUtils.isEmpty(present.getRoomId()) && message.getConversationType() == Conversation.ConversationType.CHATROOM) {
            RCChatRoomMessageManager.INSTANCE.onReceiveMessage(present.getRoomId(), message.getContent());
        }
    }

    @Override
    public void onRoomNotificationReceived(String name, String content) {
        ArrayList<String> contents = new ArrayList<>();
        contents.add(content);
        roomEventSubject.onNext(new Pair<>(name, contents));
    }

    /**
     * 收到上麦邀请
     *
     * @param userId
     */
    @Override
    public void onPickSeatReceivedFrom(String userId) {

        if (userId.equals(present.getCreateUserId())) {
            //当前是房主邀请的
            present.showPickReceivedDialog(true, userId);
        } else {
            //管理员邀请
            present.showPickReceivedDialog(false, userId);
        }
    }

    /**
     * 被抱下麦
     *
     * @param i
     */
    @Override
    public void onKickSeatReceived(int i) {
        roomEventSubject.onNext(new Pair(EVENT_KICK_OUT_OF_SEAT, new ArrayList<>()));
        AudioManagerUtil.INSTANCE.choiceAudioModel();
    }

    /**
     * 请求加入麦位被允许
     */
    @Override
    public void onRequestSeatAccepted() {
        roomEventSubject.onNext(new Pair(EVENT_REQUEST_SEAT_AGREE, new ArrayList<>()));
    }

    /**
     * 请求加入麦位被拒绝
     */
    @Override
    public void onRequestSeatRejected() {
        roomEventSubject.onNext(new Pair(EVENT_REQUEST_SEAT_REFUSE, new ArrayList<>()));
    }

    /**
     * 接收请求 撤销麦位
     */
    @Override
    public void onRequestSeatListChanged() {
        getRequestSeatUserIds();
    }

    /**
     * 获取到正在申请麦位的用户的信息
     */
    public void getRequestSeatUserIds() {
        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(new RCVoiceRoomResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> requestUserIds) {
                //获取到当前房间所有用户,申请人需要在房间，并且不在麦位上
                List<User> users = MemberCache.getInstance().getMemberList().getValue();
                requestSeats.clear();
                for (String requestUserId : requestUserIds) {
                    for (User user : users) {
                        if (user.getUserId().equals(requestUserId) && getSeatInfoByUserId(user.getUserId()) == null) {
                            requestSeats.add(user);
                            break;
                        }
                    }
                }
                obRequestSeatListChangeSuject.onNext(requestSeats);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 收到邀请
     *
     * @param invitationId
     * @param userId
     * @param content
     */
    @Override
    public void onInvitationReceived(String invitationId, String userId, String content) {
        Log.e(TAG, "onInvitationReceived: ");
    }

    /**
     * 同意邀请
     *
     * @param invitationId
     */
    @Override
    public void onInvitationAccepted(String invitationId) {
        Log.e(TAG, "onInvitationAccepted: ");
    }

    /**
     * 拒绝邀请
     *
     * @param invitationId
     */
    @Override
    public void onInvitationRejected(String invitationId) {
        Log.e(TAG, "onInvitationRejected: ");
    }

    /**
     * 取消邀请
     *
     * @param invitationId
     */
    @Override
    public void onInvitationCancelled(String invitationId) {
        Log.e(TAG, "onInvitationCancelled: ");
    }

    /**
     * 用户收到被踢出房间 然后弹窗告知，然后退出房间等操作
     *
     * @param targetId 被踢用户的标识
     * @param userId   发起踢人用户的标识
     */
    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        Log.e(TAG, "onUserReceiveKickOutRoom: ");
        ArrayList<String> strings = new ArrayList<>();
        strings.add(userId);
        strings.add(targetId);
        roomEventSubject.onNext(new Pair(EVENT_KICKED_OUT_OF_ROOM, strings));
    }

    /**
     * 网络信号监听
     *
     * @param i
     */
    @Override
    public void onNetworkStatus(int i) {
        if (present != null)
            present.onNetworkStatus(i);
    }

    @Override
    public void onPKGoing(@NonNull RCPKInfo rcpkInfo) {

    }

    @Override
    public void onPKFinish() {

    }

    @Override
    public void onReveivePKInvitation(String s, String s1) {

    }

    @Override
    public void onPKInvitationCanceled(String s, String s1) {

    }

    @Override
    public void onPKInvitationRejected(String s, String s1) {

    }

    @Override
    public void onPKInvitationIgnored(String s, String s1) {

    }


    /**
     * 获取房间信息
     */
    public Single<VoiceRoomBean> getRoomInfo(String roomId) {

        return Single.create(new SingleOnSubscribe<VoiceRoomBean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<VoiceRoomBean> emitter) throws Throwable {
                cn.rongcloud.voiceroom.net.bean.respond.VoiceRoomBean roomBean = currentUIRoomInfo.getRoomBean();
                if (roomBean != null) {
//                    currentUIRoomInfo.setRoomBean(voiceRoomBean);
                } else {
                    //通过网络去获取
                    queryRoomInfoFromServer(roomId).subscribe();
                }
            }
        });
    }

    /**
     * 通过网络去获取最新的房间信息
     *
     * @param roomId
     * @return
     */
    public Single<VoiceRoomInfoBean> queryRoomInfoFromServer(String roomId) {
        return VoiceRoomNetManager.INSTANCE.getARoomApi()
                .getVoiceRoomInfo(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<VoiceRoomInfoBean>() {
                    @Override
                    public void accept(VoiceRoomInfoBean voiceRoomInfoBean) throws Throwable {
                        //房间信息
                        currentUIRoomInfo.setRoomBean(voiceRoomInfoBean.getRoom());
                    }
                });
    }


    /**
     * 音乐的所有操作
     * TODO =================================================================
     */

    /**
     * 音乐是否正在播放
     *
     * @return
     */
    public boolean isPlayingMusic() {
        return MusicManager.get().isPlaying();
    }


    /**
     * 当房间人员变化的时候监听，当有人上麦或者下麦的时候也要监听
     *
     * @param users
     */
    public void onMemberListener(List<User> users) {
        //房间观众发生变化
        getRequestSeatUserIds();

        //只要不在麦位的人都可以被邀请
        inviteSeats.clear();
        for (User user : users) {
            //是否在麦位上标识
            boolean isInSeat = false;
            //当前用户在麦位上或者当前用户是房间创建者，那么不可以被邀请
            for (UiSeatModel uiSeatModel : uiSeatModels) {
                if ((!TextUtils.isEmpty(uiSeatModel.getUserId()) && uiSeatModel.getUserId().equals(user.getUserId()))
                        || user.getUserId().equals(AccountStore.INSTANCE.getUserId())) {
                    isInSeat = true;
                    break;
                }
            }
            if (!isInSeat) {
                inviteSeats.add(user);
            }
        }
        obInviteSeatListChangeSuject.onNext(inviteSeats);
    }

    /**
     * 关于到麦位的所有的操作
     * TODO =====================================================================
     */

    /**
     * 根据ID获取当前的麦位信息
     *
     * @param userId
     * @return
     */
    public UiSeatModel getSeatInfoByUserId(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        for (UiSeatModel uiSeatModel : uiSeatModels) {
            if (!TextUtils.isEmpty(uiSeatModel.getUserId()) && uiSeatModel.getUserId().equals(userId)) {
                return uiSeatModel;
            }
        }
        return null;
    }


    /**
     * 麦位断开链接
     *
     * @return
     */
    public Completable leaveSeat() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().leaveSeat(new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        AudioManagerUtil.INSTANCE.choiceAudioModel();
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new Throwable(s));
                    }
                });
            }
        }).subscribeOn(dataModifyScheduler);
    }

    /**
     * 取消上麦
     */
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().cancelRequestSeat(new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //取消成功
                        callback.onResult(true, "");
                        //发送通知，当前为取消状态，去刷新Ui
                        roomEventSubject.onNext(new Pair<>(EVENT_REQUEST_SEAT_CANCEL, new ArrayList<>()));
                    }

                    @Override
                    public void onError(int i, String s) {
                        //取消失败
                        callback.onResult(false, s);
                    }
                });
            }
        }).subscribe());
    }


    /**
     * 同意上麦
     */
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                int availableIndex = getAvailableIndex();
                if (availableIndex < 0) {
                    showToast("房间麦位已满");
                    return;
                }
                RCVoiceRoomEngine.getInstance()
                        .acceptRequestSeat(userId, new RCVoiceRoomCallback() {
                            @Override
                            public void onSuccess() {
                                emitter.onComplete();
                                callback.onResult(true, "");
                            }

                            @Override
                            public void onError(int i, String s) {
                                emitter.onError(new Throwable(s));
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }


    /**
     * 邀请上麦
     *
     * @param userId
     * @param callback
     */
    public void clickInviteSeat(String userId, ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                if (getAvailableIndex() < 0) {
                    emitter.onError(new Throwable("麦位已满"));
                    return;
                }
                RCVoiceRoomEngine.getInstance().pickUserToSeat(userId, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //邀请成功,集合会跟着变化
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new Throwable(s));
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        callback.onResult(true, "邀请成功");
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.onResult(false, throwable.getMessage());
                    }
                }).subscribe());
    }

    /**
     * 踢出去房间
     *
     * @param user
     * @param callback
     */
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().kickUserFromRoom(user.getUserId(), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //踢出房间成功以后，要发送消息给被踢出的人
                        RCChatroomKickOut kickOut = new RCChatroomKickOut();
                        kickOut.setUserId(AccountStore.INSTANCE.getUserId());
                        kickOut.setUserName(AccountStore.INSTANCE.getUserName());
                        kickOut.setTargetId(user.getUserId());
                        kickOut.setTargetName(user.getUserName());
                        RCChatRoomMessageManager.INSTANCE.sendChatMessage(present.getRoomId(),
                                kickOut,
                                true,
                                new Function1<Integer, Unit>() {
                                    @Override
                                    public Unit invoke(Integer integer) {
                                        //成功
                                        emitter.onComplete();
                                        return null;
                                    }
                                }
                                , new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
                                    @Override
                                    public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                                        //失败
                                        emitter.onError(new Throwable(coreErrorCode + ""));
                                        return null;
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new Throwable(s));
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        callback.onResult(true, "");
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.onResult(false, throwable.getMessage());
                    }
                }).subscribe());
    }

    /**
     * 抱下麦
     *
     * @param user
     * @param callback
     */
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().kickUserFromSeat(user.getUserId(), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new Throwable(s));
                    }
                });
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Throwable {
                callback.onResult(true, "");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                callback.onResult(false, throwable.getMessage());
            }
        }).subscribe());
    }


    /**
     * 座位禁麦，根据点击的位置来禁止
     */
    public void clickMuteSeat(int index, boolean isMute, ClickCallback<Boolean> callback) {
        Log.e(TAG, "clickMuteSeat: " + isMute);
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance()
                        .muteSeat(index, isMute, new RCVoiceRoomCallback() {
                            @Override
                            public void onSuccess() {
                                //座位禁麦成功
                                emitter.onComplete();
                                if (isMute) {
                                    EToast.showToast("此麦位已闭麦");
                                } else {
                                    EToast.showToast("已取消闭麦");
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                //座位禁麦失败
                                emitter.onError(new Throwable(s));
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.onResult(false, throwable.getMessage());
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        callback.onResult(true, "");
                    }
                }).subscribe());
    }

    /**
     * 根据麦位的位置去关闭座位
     */
    public void clickCloseSeatByIndex(int index, boolean isClose, ClickCallback<Boolean> callback) {
        present.addDisposable(Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().lockSeat(index, isClose, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //锁座位成功
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int i, String s) {
                        //锁座位失败
                        emitter.onError(new Throwable(s));
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        callback.onResult(false, throwable.getMessage());
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        callback.onResult(true, "");
                    }
                }).subscribe());
    }

    /**
     * 上麦
     */
    public void enterSeatIfAvailable() {
        RCVoiceRoomEngine.getInstance()
                .notifyVoiceRoom(EVENT_AGREE_MANAGE_PICK, AccountStore.INSTANCE.getUserId(), null);
        int availableIndex = getAvailableIndex();
        if (availableIndex > 0) {
            RCVoiceRoomEngine
                    .getInstance()
                    .enterSeat(availableIndex, new RCVoiceRoomCallback() {
                        @Override
                        public void onSuccess() {
                            EToast.showToast("上麦成功");
                            AudioManagerUtil.INSTANCE.choiceAudioModel();
                        }

                        @Override
                        public void onError(int code, String message) {
                            EToast.showToast(message);
                        }
                    });
        } else {
            EToast.showToast("当前没有空余的麦位");
        }
    }

    /**
     * 自己是否已经在麦位上了
     *
     * @return
     */
    public boolean userInSeat() {
        for (UiSeatModel currentSeat : uiSeatModels) {
            if (currentSeat.getUserId() != null && currentSeat.getUserId().equals(AccountStore.INSTANCE.getUserId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 位置是否有效
     *
     * @return
     */
    public int getAvailableIndex() {
        for (int i = 0; i < uiSeatModels.size(); i++) {
            UiSeatModel uiSeatModel = uiSeatModels.get(i);
            if (uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty && i != 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 拒绝邀请
     *
     * @param userId 邀请人ID
     */
    public void refuseInvite(String userId) {
        RCVoiceRoomEngine.getInstance()
                .notifyVoiceRoom(EVENT_REJECT_MANAGE_PICK, userId, null);
    }

    /**
     * 房主控制自己上麦和下麦
     */
    public Completable creatorMuteSelf() {
        UiSeatModel seatInfoByUserId = getSeatInfoByUserId(AccountStore.INSTANCE.getUserId());
        boolean isMute = !seatInfoByUserId.isMute();
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().disableAudioRecording(isMute);
                RCVoiceRoomEngine.getInstance().muteSeat(0, isMute, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        emitter.onComplete();
                        if (isMute) {
                            EToast.showToast("此麦位已闭麦");
                        } else {
                            EToast.showToast("已取消闭麦");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        emitter.onError(new Throwable(s));
                    }
                });
                //主播禁麦自己
                if (isMute) {//关闭耳返
                    RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(false);
                } else {//根据缓存状态恢复耳返
                    boolean enable = SharedPreferUtil.getBoolean("key_earMonitoring_" + present.getRoomId());
                    RCRTCEngine.getInstance().getDefaultAudioStream().enableEarMonitoring(enable);
                }
            }
        }).subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
