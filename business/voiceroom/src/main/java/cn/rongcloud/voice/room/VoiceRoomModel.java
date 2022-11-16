package cn.rongcloud.voice.room;


import static cn.rongcloud.voice.Constant.EVENT_AGREE_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_KICKED_OUT_OF_ROOM;
import static cn.rongcloud.voice.Constant.EVENT_KICK_OUT_OF_SEAT;
import static cn.rongcloud.voice.Constant.EVENT_REJECT_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_REQUEST_SEAT_AGREE;
import static cn.rongcloud.voice.Constant.EVENT_REQUEST_SEAT_REFUSE;
import static cn.rongcloud.voice.Constant.EVENT_ROOM_CLOSE;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.mvp.BaseModel;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.voice.model.UiRoomModel;
import cn.rongcloud.voice.model.UiSeatModel;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 语聊房的逻辑处理
 */
public class VoiceRoomModel extends BaseModel<VoiceRoomPresenter> implements RCVoiceRoomEventListener {

    private String TAG = "NewVoiceRoomModel";
    //线程调度器
    Scheduler dataModifyScheduler = Schedulers.computation();

    //麦位信息变化监听器
    private BehaviorSubject<UiSeatModel> seatInfoChangeSubject = BehaviorSubject.create();

    //座位数量订阅器,为了让所有订阅的地方都能回调回去
    private BehaviorSubject<List<UiSeatModel>> seatListChangeSubject = BehaviorSubject.create();

    //房间信息发生改变订阅，比如房间被解散，上锁之类的
    private BehaviorSubject<UiRoomModel> roomInfoSubject = BehaviorSubject.create();

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

    public UiRoomModel currentUIRoomInfo = new UiRoomModel(roomInfoSubject);

    //本地麦克风的状态，默认是开启的
    private boolean recordingStatus = true;
    //麦位集合
    private volatile ArrayList<UiSeatModel> uiSeatModels = new ArrayList<>();

    //申请连麦的集合
    private ArrayList<User> requestSeats = new ArrayList<>();
    //可以被邀请的集合
    private ArrayList<User> inviteSeats = new ArrayList<>();

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


    public VoiceRoomModel(VoiceRoomPresenter present, Lifecycle lifecycle) {
        super(present, lifecycle);
    }

    @Override
    public void onRoomKVReady() {

    }

    @Override
    public void onRoomDestroy() {
        roomEventSubject.onNext(new Pair(EVENT_ROOM_CLOSE, new ArrayList<>()));
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
            List<UiSeatModel> olds = new ArrayList<>();
            olds.addAll(uiSeatModels);
            int oldCount = olds.size();
            uiSeatModels.clear();
            for (int i = 0; i < size; i++) {
                //构建一个集合返回去
                RCVoiceSeatInfo rcVoiceSeatInfo = list.get(i);
                if (!TextUtils.isEmpty(rcVoiceSeatInfo.getUserId())
                        && rcVoiceSeatInfo.getStatus().equals(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty)) {
                    rcVoiceSeatInfo.setStatus(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing);
                }
                UiSeatModel uiSeatModel = new UiSeatModel(i, rcVoiceSeatInfo, seatInfoChangeSubject);
                //缓存giftcount
                if (i < oldCount) {
                    uiSeatModel.setGiftCount(olds.get(i).getGiftCount());
                }
                uiSeatModels.add(uiSeatModel);
            }
            seatListChangeSubject.onNext(uiSeatModels);
        }
        present.refreshRoomMember();
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
//        present.refreshRoomMember();
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
//        present.refreshRoomMember();
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
//        Log.e(TAG, "onSeatMute: ");
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
//        present.refreshRoomMember();
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
        //由于SDK内实现的是先发消息，才退的房间，导致此处立即刷新房间成员列表，偶像依然会拉取退房间前的列表
        UIKit.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (present != null) {
                    present.refreshRoomMember();
                }
            }
        }, 2000);
        //缓存立即刷新
        if (null != inviteSeats && !inviteSeats.isEmpty()) {
            int count = inviteSeats.size();
            User exit = null;
            for (int i = 0; i < count; i++) {
                User user = inviteSeats.get(i);
                if (TextUtils.equals(user.getUserId(), s)) {
                    exit = user;
                    break;
                }
            }
            if (null != exit) {
                inviteSeats.remove(exit);
                obInviteSeatListChangeSuject.onNext(inviteSeats);
            }
        }
    }

    /**
     * 麦位的信息变化监听
     *
     * @param i
     * @param audioLevel
     */
    @Override
    public void onSpeakingStateChanged(int i, int audioLevel) {
        if (uiSeatModels.size() > i) {
            UiSeatModel uiSeatModel = uiSeatModels.get(i);
            uiSeatModel.setSpeaking(audioLevel > 0);
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
            RCChatRoomMessageManager.onReceiveMessage(present.getRoomId(), message.getContent());
        }
    }

    @Override
    public void onRoomNotificationReceived(String name, String content) {
        ArrayList<String> contents = new ArrayList<>();
        contents.add(content);
        sendRoomEvent(new Pair<>(name, contents));
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
        sendRoomEvent(new Pair(EVENT_KICK_OUT_OF_SEAT, new ArrayList<>()));
    }

    /**
     * 请求加入麦位被允许
     */
    @Override
    public void onRequestSeatAccepted() {
        sendRoomEvent(new Pair(EVENT_REQUEST_SEAT_AGREE, new ArrayList<>()));
    }

    /**
     * 请求加入麦位被拒绝
     */
    @Override
    public void onRequestSeatRejected() {
        sendRoomEvent(new Pair(EVENT_REQUEST_SEAT_REFUSE, new ArrayList<>()));
    }

    /**
     * 发送房间事件
     *
     * @param pair
     */
    public void sendRoomEvent(Pair pair) {
        Logger.d(TAG, "sendRoomEvent:pair = " + GsonUtil.obj2Json(pair));
        if (null != roomEventSubject) {
            Logger.d(TAG, "sendRoomEvent:hasObservers = " +  roomEventSubject.hasObservers());
            roomEventSubject.onNext(pair);
        } else {
            Logger.d(TAG, "sendRoomEvent: else");
        }
    }

    /**
     * 接收请求 撤销麦位
     */
    @Override
    public void onRequestSeatListChanged() {
        Logger.d(TAG, "onRequestSeatListChanged");
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
                Logger.e(TAG, "requestUserIds = " + GsonUtil.obj2Json(requestUserIds));
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
        sendRoomEvent(new Pair(EVENT_KICKED_OUT_OF_ROOM, strings));
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
    public void onReceivePKInvitation(String s, String s1) {

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

    @Override
    public void onUserAudioRecordingDisable(String roomId, String userId, boolean disable) {

    }


    /**
     * 获取房间信息
     */
    public Single<VoiceRoomBean> getRoomInfo(String roomId) {

        return Single.create(new SingleOnSubscribe<VoiceRoomBean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<VoiceRoomBean> emitter) throws Throwable {
                VoiceRoomBean roomBean = currentUIRoomInfo.getRoomBean();
                if (roomBean != null) {
//                    currentUIRoomInfo.setRoomBean(voiceRoomBean);
                } else {
                    //通过网络去获取
                    queryRoomInfoFromServer(roomId);
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
    public void queryRoomInfoFromServer(String roomId) {
        OkApi.get(VRApi.getRoomInfo(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        currentUIRoomInfo.setRoomBean(roomBean);
                    }
                }

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
        return MusicControlManager.getInstance().isPlaying();
    }


    /**
     * 当房间人员变化的时候监听，当有人上麦或者下麦的时候也要监听
     *
     * @param users
     */
    public void onMemberListener(List<User> users) {
        Logger.d(TAG, "onMemberListener");
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
                        || user.getUserId().equals(UserManager.get().getUserId())) {
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
     * 上麦
     *
     * @param userId 邀请人id
     */
    public void enterSeatIfAvailable(String userId) {
        RCVoiceRoomEngine.getInstance()
                .notifyVoiceRoom(EVENT_AGREE_MANAGE_PICK, userId, null);
        int availableIndex = getAvailableIndex();
        if (availableIndex > 0) {
            RCVoiceRoomEngine
                    .getInstance()
                    .enterSeat(availableIndex, new RCVoiceRoomCallback() {
                        @Override
                        public void onSuccess() {
                            KToast.show("上麦成功");
                        }

                        @Override
                        public void onError(int code, String message) {
                            KToast.show(message);
                        }
                    });
        } else {
            KToast.show("当前没有空余的麦位");
        }
    }

    /**
     * 自己是否已经在麦位上了
     *
     * @return
     */
    public boolean userInSeat() {
        for (UiSeatModel currentSeat : uiSeatModels) {
            if (currentSeat.getUserId() != null && currentSeat.getUserId().equals(UserManager.get().getUserId())) {
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
        UiSeatModel seatInfoByUserId = getSeatInfoByUserId(UserManager.get().getUserId());
        UiSeatModel.UiSeatModelExtra extra = seatInfoByUserId.getExtra();
        if (extra == null) {
            extra = new UiSeatModel.UiSeatModelExtra();
        }
        extra.setDisableRecording(!extra.isDisableRecording());
        seatInfoByUserId.setExtra(extra);
        UiSeatModel.UiSeatModelExtra finalExtra = extra;
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull CompletableEmitter emitter) throws Throwable {
                RCVoiceRoomEngine.getInstance().disableAudioRecording(finalExtra.isDisableRecording());
                RCVoiceRoomEngine.getInstance().updateSeatInfo(0, GsonUtil.obj2Json(finalExtra), new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(int code, String message) {
                        emitter.onError(new Throwable(message));
                    }
                });
            }
        }).subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void creatorMuteSelf(boolean disable) {
        Logger.e(TAG, "creatorMuteSelf disable = " + disable);
        RCVoiceRoomEngine.getInstance().disableAudioRecording(disable);
        UiSeatModel.UiSeatModelExtra extra = new UiSeatModel.UiSeatModelExtra();
        extra.setDisableRecording(disable);
        RCVoiceRoomEngine.getInstance().updateSeatInfo(0, GsonUtil.obj2Json(extra), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                Logger.e(TAG, "creatorMuteSelf:updateSeatInfo success");
            }

            @Override
            public void onError(int code, String message) {
                Logger.e(TAG, "creatorMuteSelf:updateSeatInfo code =" + code + ": " + message);
            }
        });
    }

    public void muteSelf(int index, boolean disable) {
        Logger.e(TAG, "muteSelf disable = " + disable + " index = " + index);
        RCVoiceRoomEngine.getInstance().disableAudioRecording(disable);
        UiSeatModel.UiSeatModelExtra extra = new UiSeatModel.UiSeatModelExtra();
        extra.setDisableRecording(disable);
        RCVoiceRoomEngine.getInstance().updateSeatInfo(index, GsonUtil.obj2Json(extra), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                Logger.e(TAG, "creatorMuteSelf:updateSeatInfo success");
            }

            @Override
            public void onError(int code, String message) {
                Logger.e(TAG, "creatorMuteSelf:updateSeatInfo code =" + code + ": " + message);
            }
        });
    }
}
