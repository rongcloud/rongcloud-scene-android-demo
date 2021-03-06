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
 * ????????????????????????
 */
public class VoiceRoomModel extends BaseModel<VoiceRoomPresenter> implements RCVoiceRoomEventListener {

    private String TAG = "NewVoiceRoomModel";
    //???????????????
    Scheduler dataModifyScheduler = Schedulers.computation();

    //???????????????????????????
    private BehaviorSubject<UiSeatModel> seatInfoChangeSubject = BehaviorSubject.create();

    //?????????????????????,????????????????????????????????????????????????
    private BehaviorSubject<List<UiSeatModel>> seatListChangeSubject = BehaviorSubject.create();

    //????????????????????????????????????????????????????????????????????????
    private BehaviorSubject<UiRoomModel> roomInfoSubject = BehaviorSubject.create();

    //??????????????????????????? ?????? ???????????????
    private BehaviorSubject<Pair<String, ArrayList<String>>> roomEventSubject = BehaviorSubject.create();

    /**
     * ????????????????????????????????????
     */
    private BehaviorSubject<List<User>> obRequestSeatListChangeSuject = BehaviorSubject.create();
    /**
     * ??????????????????????????????
     */
    private BehaviorSubject<List<User>> obInviteSeatListChangeSuject = BehaviorSubject.create();

    public UiRoomModel currentUIRoomInfo = new UiRoomModel(roomInfoSubject);

    //?????????????????????????????????????????????
    private boolean recordingStatus = true;
    //????????????
    private volatile ArrayList<UiSeatModel> uiSeatModels = new ArrayList<>();

    //?????????????????????
    private ArrayList<User> requestSeats = new ArrayList<>();
    //????????????????????????
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
     * ????????????????????????
     */
    public Observable<List<UiSeatModel>> obSeatListChange() {
        return seatListChangeSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ???????????????????????????
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
     * ???????????????????????????
     */
    public Observable<UiSeatModel> obSeatInfoChange() {
        return seatInfoChangeSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ?????????????????????
     */
    public Observable<Pair<String, ArrayList<String>>> obRoomEventChange() {
        return roomEventSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(dataModifyScheduler);
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     */
    public Observable<UiRoomModel> obRoomInfoChange() {
        return roomInfoSubject.subscribeOn(dataModifyScheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * ??????????????????????????????????????????
     */
    public Observable<List<User>> obRequestSeatListChange() {
        return obRequestSeatListChangeSuject.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(dataModifyScheduler);
    }

    /**
     * ??????????????????????????????
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
     * ???????????????????????????
     * ???????????????????????????????????????????????????????????????????????????
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
                //???????????????????????????
                RCVoiceSeatInfo rcVoiceSeatInfo = list.get(i);
                if (!TextUtils.isEmpty(rcVoiceSeatInfo.getUserId())
                        && rcVoiceSeatInfo.getStatus().equals(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty)) {
                    rcVoiceSeatInfo.setStatus(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing);
                }
                UiSeatModel uiSeatModel = new UiSeatModel(i, rcVoiceSeatInfo, seatInfoChangeSubject);
                //??????giftcount
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
     * ??????????????????
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
     * ??????????????????,???????????????????????????????????? ??????????????????????????????????????????
     *
     * @param i
     * @param s
     */
    @Override
    public void onUserLeaveSeat(int i, String s) {
        Log.e(TAG, "onUserLeaveSeat: ");
        //??????????????????????????????????????????????????????
//        present.refreshRoomMember();
    }

    /**
     * ???????????????
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
     * ??????????????????
     *
     * @param i
     * @param b
     */
    @Override
    public void onSeatLock(int i, boolean b) {
        //???????????????????????????
//        present.refreshRoomMember();
        Log.e(TAG, "onSeatLock: ");
    }

    /**
     * ????????????
     *
     * @param s
     */
    @Override
    public void onAudienceEnter(String s) {
        Log.e(TAG, "onAudienceEnter: ");
        present.refreshRoomMember();
    }

    /**
     * ????????????
     *
     * @param s
     */
    @Override
    public void onAudienceExit(String s) {
        Log.e(TAG, "onAudienceExit: ");
        //??????SDK???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        UIKit.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (present != null) {
                    present.refreshRoomMember();
                }
            }
        }, 2000);
        //??????????????????
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
     * ???????????????????????????
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
     * ????????????
     *
     * @param message ???????????????
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
     * ??????????????????
     *
     * @param userId
     */
    @Override
    public void onPickSeatReceivedFrom(String userId) {
        if (userId.equals(present.getCreateUserId())) {
            //????????????????????????
            present.showPickReceivedDialog(true, userId);
        } else {
            //???????????????
            present.showPickReceivedDialog(false, userId);
        }
    }

    /**
     * ????????????
     *
     * @param i
     */
    @Override
    public void onKickSeatReceived(int i) {
        sendRoomEvent(new Pair(EVENT_KICK_OUT_OF_SEAT, new ArrayList<>()));
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onRequestSeatAccepted() {
        sendRoomEvent(new Pair(EVENT_REQUEST_SEAT_AGREE, new ArrayList<>()));
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onRequestSeatRejected() {
        sendRoomEvent(new Pair(EVENT_REQUEST_SEAT_REFUSE, new ArrayList<>()));
    }

    /**
     * ??????????????????
     *
     * @param pair
     */
    public void sendRoomEvent(Pair pair) {
        roomEventSubject.onNext(pair);
    }

    /**
     * ???????????? ????????????
     */
    @Override
    public void onRequestSeatListChanged() {
        Logger.d(TAG, "onRequestSeatListChanged");
        getRequestSeatUserIds();
    }

    /**
     * ?????????????????????????????????????????????
     */
    public void getRequestSeatUserIds() {
        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(new RCVoiceRoomResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> requestUserIds) {
                //?????????????????????????????????,????????????????????????????????????????????????
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
     * ????????????
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
     * ????????????
     *
     * @param invitationId
     */
    @Override
    public void onInvitationAccepted(String invitationId) {
        Log.e(TAG, "onInvitationAccepted: ");
    }

    /**
     * ????????????
     *
     * @param invitationId
     */
    @Override
    public void onInvitationRejected(String invitationId) {
        Log.e(TAG, "onInvitationRejected: ");
    }

    /**
     * ????????????
     *
     * @param invitationId
     */
    @Override
    public void onInvitationCancelled(String invitationId) {
        Log.e(TAG, "onInvitationCancelled: ");
    }

    /**
     * ??????????????????????????? ????????????????????????????????????????????????
     *
     * @param targetId ?????????????????????
     * @param userId   ???????????????????????????
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
     * ??????????????????
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


    /**
     * ??????????????????
     */
    public Single<VoiceRoomBean> getRoomInfo(String roomId) {

        return Single.create(new SingleOnSubscribe<VoiceRoomBean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<VoiceRoomBean> emitter) throws Throwable {
                VoiceRoomBean roomBean = currentUIRoomInfo.getRoomBean();
                if (roomBean != null) {
//                    currentUIRoomInfo.setRoomBean(voiceRoomBean);
                } else {
                    //?????????????????????
                    queryRoomInfoFromServer(roomId);
                }
            }
        });
    }

    /**
     * ??????????????????????????????????????????
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
     * ?????????????????????
     * TODO =================================================================
     */

    /**
     * ????????????????????????
     *
     * @return
     */
    public boolean isPlayingMusic() {
        return MusicControlManager.getInstance().isPlaying();
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param users
     */
    public void onMemberListener(List<User> users) {
        Logger.d(TAG, "onMemberListener");
        //????????????????????????
        getRequestSeatUserIds();

        //??????????????????????????????????????????
        inviteSeats.clear();
        for (User user : users) {
            //????????????????????????
            boolean isInSeat = false;
            //???????????????????????????????????????????????????????????????????????????????????????
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
     * ?????????????????????????????????
     * TODO =====================================================================
     */

    /**
     * ??????ID???????????????????????????
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
     * ??????????????????
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
     * ??????
     *
     * @param userId ?????????id
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
                            KToast.show("????????????");
                        }

                        @Override
                        public void onError(int code, String message) {
                            KToast.show(message);
                        }
                    });
        } else {
            KToast.show("???????????????????????????");
        }
    }

    /**
     * ?????????????????????????????????
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
     * ??????????????????
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
     * ????????????
     *
     * @param userId ?????????ID
     */
    public void refuseInvite(String userId) {
        RCVoiceRoomEngine.getInstance()
                .notifyVoiceRoom(EVENT_REJECT_MANAGE_PICK, userId, null);
    }

    /**
     * ?????????????????????????????????
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
