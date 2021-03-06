package cn.rongcloud.voice.room;


import static cn.rongcloud.voice.Constant.EVENT_ADD_SHIELD;
import static cn.rongcloud.voice.Constant.EVENT_AGREE_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_BACKGROUND_CHANGE;
import static cn.rongcloud.voice.Constant.EVENT_DELETE_SHIELD;
import static cn.rongcloud.voice.Constant.EVENT_KICKED_OUT_OF_ROOM;
import static cn.rongcloud.voice.Constant.EVENT_KICK_OUT_OF_SEAT;
import static cn.rongcloud.voice.Constant.EVENT_MANAGER_LIST_CHANGE;
import static cn.rongcloud.voice.Constant.EVENT_REJECT_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_REQUEST_SEAT_AGREE;
import static cn.rongcloud.voice.Constant.EVENT_REQUEST_SEAT_CANCEL;
import static cn.rongcloud.voice.Constant.EVENT_REQUEST_SEAT_REFUSE;
import static cn.rongcloud.voice.Constant.EVENT_ROOM_CLOSE;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.mvp.BasePresenter;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.wapper.IRoomCallBack;
import com.basis.widget.dialog.VRCenterDialog;
import com.rc.voice.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.music.MusicApi;
import cn.rongcloud.music.MusicBean;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.manager.AllBroadcastManager;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatroomAdmin;
import cn.rongcloud.roomkit.message.RCChatroomEnter;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.message.RCChatroomLocationMessage;
import cn.rongcloud.roomkit.message.RCChatroomSeats;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.provider.VoiceRoomProvider;
import cn.rongcloud.roomkit.ui.OnItemClickListener;
import cn.rongcloud.roomkit.ui.RoomListIdsCache;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.fragment.BackgroundSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomBackgroundFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomFunIdUitls;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockAllSeatFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteAllFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSeatModeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomSeatSizeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.EmptySeatFragment;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.ICommonDialog;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.RevokeSeatRequestFragment;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.SeatOperationViewPagerFragment;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.roomkit.ui.room.widget.RoomTitleBar;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import cn.rongcloud.voice.model.UiRoomModel;
import cn.rongcloud.voice.model.UiSeatModel;
import cn.rongcloud.voice.room.dialogFragment.CreatorSettingFragment;
import cn.rongcloud.voice.room.dialogFragment.SelfSettingFragment;
import cn.rongcloud.voice.room.helper.VoiceEventHelper;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandMessage;
import io.rong.message.TextMessage;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * ?????????present
 */
public class VoiceRoomPresenter extends BasePresenter<IVoiceRoomFragmentView> implements
        OnItemClickListener<MutableLiveData<IFun.BaseFun>>,
        IVoiceRoomPresent, MemberSettingFragment.OnMemberSettingClickListener
        , BackgroundSettingFragment.OnSelectBackgroundListener,
        GiftFragment.OnSendGiftListener,
        RoomTitleBar.OnFollowClickListener,
        ICommonDialog {

    private String TAG = "NewVoiceRoomPresenter";

    public static final int STATUS_ON_SEAT = 0;
    public static final int STATUS_NOT_ON_SEAT = 1;
    public static final int STATUS_WAIT_FOR_SEAT = 2;
    /**
     * ?????????model
     */
    private VoiceRoomModel voiceRoomModel;
    /**
     * ????????????
     */
    private VoiceRoomBean mVoiceRoomBean;

    private VRCenterDialog confirmDialog;

    private RoomOwnerType roomOwnerType;
    private InputPasswordDialog inputPasswordDialog;


    public int currentStatus = STATUS_NOT_ON_SEAT;
//    private List<Shield> shields = new ArrayList<>();

    //?????????????????????????????????,???????????????????????????????????????????????????????????????????????????
    private List<Disposable> disposableList = new ArrayList<>();
    private EmptySeatFragment emptySeatFragment;
    private boolean isInRoom;
    private String notice;

    public VoiceRoomPresenter(IVoiceRoomFragmentView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
        voiceRoomModel = new VoiceRoomModel(this, lifecycle);
    }

    public String getNotice() {
        return notice;
    }

    /**
     * ?????????
     *
     * @param roomId
     * @param isCreate
     */
    public void init(String roomId, boolean isCreate) {
        isInRoom = TextUtils.equals(VoiceEventHelper.helper().getRoomId(), roomId);
        // TODO ????????????
        getRoomInfo(roomId, isCreate);
    }

    /**
     * ??????????????????
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
                            initListener(roomId);
                            currentStatus = VoiceEventHelper.helper().getCurrentStatus();
                            mView.changeStatus(currentStatus);
                            voiceRoomModel.currentUIRoomInfo.setMute(VoiceEventHelper.helper().getMuteAllRemoteStreams());
                            voiceRoomModel.onSeatInfoUpdate(VoiceEventHelper.helper().getRCVoiceSeatInfoList());
                            setCurrentRoom(mVoiceRoomBean);
                            mView.dismissLoading();
                            if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                                refreshMusicView(true);
                            }
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

    private void leaveRoom(String roomId, boolean isCreate, boolean isExit) {
        // ?????????????????????
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                Logger.d("==============leaveRoom onSuccess");
                VoiceEventHelper.helper().changeUserRoom("");
                if (isExit) {
                    UIKit.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            joinRoom(roomId, isCreate);
                        }
                    }, 0);
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
        SensorsUtil.instance().joinRoom(roomId, mVoiceRoomBean.getRoomName(), mVoiceRoomBean.getIsPrivate() == 1,
                false, false, RoomType.VOICE_ROOM.convertToRcEvent());
        //??????????????????
        VoiceEventHelper.helper().register(roomId);
        VoiceEventHelper.helper().setRoomBean(mVoiceRoomBean);

        initListener(roomId);
        //??????????????????
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
                    VoiceEventHelper.helper().changeUserRoom(roomId);
                    setCurrentRoom(mVoiceRoomBean);
                    if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true);
                    }
                    mView.dismissLoading();
                }

                @Override
                public void onError(int code, String message) {
                    Logger.e("==============createAndJoinRoom onError,code:" + code + ",message:" + message);
                    mView.dismissLoading();
                    KToast.show("??????????????????");
                    closeRoom();
                }
            });
        } else {
            RCVoiceRoomEngine.getInstance().joinRoom(roomId, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    Logger.d("==============joinRoom onSuccess");
                    VoiceEventHelper.helper().changeUserRoom(roomId);
                    setCurrentRoom(mVoiceRoomBean);
                    if (roomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true);
                    }
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
     * ????????????????????????????????????
     */
    private void sendSystemMessage() {
        if (mVoiceRoomBean != null) {
            mView.showMessage(null, true);
            // ????????????
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("???????????? %s", mVoiceRoomBean.getRoomName()));
            RCChatRoomMessageManager.sendLocationMessage(mVoiceRoomBean.getRoomId(), welcome);

            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("?????????????????? RTC ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            RCChatRoomMessageManager.sendLocationMessage(mVoiceRoomBean.getRoomId(), tips);
            Logger.d("=================?????????????????????");
            // ????????????
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(UserManager.get().getUserId());
            enter.setUserName(UserManager.get().getUserName());
            RCChatRoomMessageManager.sendChatMessage(mVoiceRoomBean.getRoomId(), enter, false,
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
     * ???????????????voiceBean
     *
     * @param mVoiceRoomBean
     */
    @Override
    public void setCurrentRoom(VoiceRoomBean mVoiceRoomBean) {
        Logger.e(TAG, "setCurrentRoom");
        roomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(mVoiceRoomBean);
        // ????????????????????????????????????????????????????????????
        if (roomOwnerType == RoomOwnerType.VOICE_OWNER && !voiceRoomModel.userInSeat() && !isInRoom) {
            roomOwnerEnterSeat(true);
        } else if (voiceRoomModel.userInSeat()) {
            // ?????????????????? ?????????disableRecord????????????false
            if (null == voiceRoomModel) return;
            RCVoiceRoomEngine.getInstance().disableAudioRecording(!voiceRoomModel.isRecordingStatus());
//            UiSeatModel seatModel = voiceRoomModel.getUiSeatModels().get(0);
//            if (null != seatModel && null != seatModel.getExtra()) {
//                boolean disable = seatModel.getExtra().isDisableRecording();
//                voiceRoomModel.creatorMuteSelf(disable);
//            }
        }
        if (isInRoom) {
            //????????????????????????????????????
            List<MessageContent> messageList = VoiceEventHelper.helper().getMessageList();
            mView.showMessageList(messageList, true);
        } else {
            // ??????????????????
            sendSystemMessage();
        }
        //???????????????????????????????????????????????????
        voiceRoomModel.getRoomInfo(mVoiceRoomBean.getRoomId()).subscribe();
        //??????????????????
        MemberCache.getInstance().fetchData(mVoiceRoomBean.getRoomId());
        //????????????????????????
        MemberCache.getInstance().getMemberList().observe(((VoiceRoomFragment) mView).getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                //??????
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
        //???????????????
        getShield();
        getGiftCount();
        mView.setRoomData(mVoiceRoomBean);
    }

    @Override
    public VoiceRoomBean getmVoiceRoomBean() {
        return mVoiceRoomBean;
    }


    @Override
    public void initListener(String roomId) {
        //??????model?????????????????????
        VoiceEventHelper.helper().setRCVoiceRoomEventListener(voiceRoomModel);
        setObSeatListChange();
        setObRoomEventChange();
        setRequestSeatListener();
        setObSeatInfoChange();
        setObRoomInfoChange();
        setObShieldListener();
        setObMessageListener();
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
     * ???????????????????????????????????????
     */
    private void setObShieldListener() {
//        EventBus.get().on(UPDATE_SHIELD, new EventBus.EventCallback() {
//            @Override
//            public void onEvent(String tag, Object... args) {
//                getShield();
//            }
//        });
    }


    /**
     * ?????????????????????
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
                            notice = TextUtils.isEmpty(extra) ? String.format("???????????? %s", mVoiceRoomBean.getRoomName()) : extra;
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
     * ????????????????????????
     */
    private void setObSeatInfoChange() {
        disposableList.add(voiceRoomModel.obSeatInfoChange().subscribe(new Consumer<UiSeatModel>() {
            @Override
            public void accept(UiSeatModel uiSeatModel) throws Throwable {
                //???????????????????????????
                int index = uiSeatModel.getIndex();
                if (index == 0) {
                    mView.refreshRoomOwner(uiSeatModel);
                } else {
                    //???????????????????????????
                    mView.onSeatListChange(voiceRoomModel.getUiSeatModels());
                }
            }
        }));
    }

    /**
     * ????????????????????????
     */
    private void setRequestSeatListener() {
        voiceRoomModel.obRequestSeatListChange()
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        //???????????????????????????????????????
                        if (mView != null) {
                            mView.showUnReadRequestNumber(users.size());
                        }
                    }
                });
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * TODO ??????????????????????????????????????????????????????
     */
    private void setObMessageListener() {
        disposableList.add(RCChatRoomMessageManager.obMessageReceiveByRoomId(mVoiceRoomBean.getRoomId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageContent>() {
                    @Override
                    public void accept(MessageContent messageContent) throws Throwable {
                        //???????????????????????????
                        if (VoiceEventHelper.helper().isShowingMessage(messageContent)) {
                            // fix???????????? ??????pk??????
                            if (null != mView) mView.showMessage(messageContent, false);
                        }
                        if (messageContent instanceof RCChatroomGift || messageContent instanceof RCChatroomGiftAll) {
                            getGiftCount();
                        } else if (messageContent instanceof RCChatroomLike) {
                            if (null != mView) mView.showLikeAnimation();
                            return;
                        } else if (messageContent instanceof RCAllBroadcastMessage) {
                            AllBroadcastManager.getInstance().addMessage((RCAllBroadcastMessage) messageContent);
                        } else if (messageContent instanceof RCChatroomSeats) {
                            refreshRoomMember();
                        } else if (messageContent instanceof RCChatroomLocationMessage) {
                            VoiceEventHelper.helper().addMessage(messageContent);
                        } else if (messageContent instanceof CommandMessage) {
                            boolean show = !TextUtils.isEmpty(((CommandMessage) messageContent).getData());
                            refreshMusicView(show);
                        }
                    }
                }));
    }


    /**
     * ??????????????????
     *
     * @param position ??????????????? Recyclerview????????????????????????????????????Position+1
     */
    @Override
    public void enterSeatViewer(int position) {
        //????????????????????????
        if (voiceRoomModel.userInSeat()) {
            //????????????
            RCVoiceRoomEngine.getInstance().switchSeatTo(position + 1, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast(message);
                }
            });
        } else {
            //???????????????
            requestSeat(position + 1);
        }
    }

    /**
     * ????????????
     *
     * @param position
     */
    public void requestSeat(int position) {
        if (currentStatus == STATUS_ON_SEAT) {
            //??????????????????
            return;
        }
        //????????????????????????????????????????????????????????????
        if (currentStatus == STATUS_WAIT_FOR_SEAT && !voiceRoomModel.currentUIRoomInfo.isFreeEnterSeat()) {
            mView.showRevokeSeatRequest();
            return;
        }
        //???????????????????????????
        if (voiceRoomModel.currentUIRoomInfo.isFreeEnterSeat()) {
            int index = position;
            if (index == -1) {
                index = voiceRoomModel.getAvailableIndex();
            }
            if (index == -1) {
                mView.showToast("??????????????????");
                return;
            }
            RCVoiceRoomEngine.getInstance().enterSeat(index, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    mView.showToast("????????????");
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast(message);
                }
            });
        } else {
            SensorsUtil.instance().connectRequest(getRoomId(), mVoiceRoomBean.getRoomName(), RcEvent.VoiceRoom);
            RCVoiceRoomEngine.getInstance().requestSeat(new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    currentStatus = STATUS_WAIT_FOR_SEAT;
                    mView.changeStatus(STATUS_WAIT_FOR_SEAT);
                    mView.showToast("????????????????????????????????????");
                }

                @Override
                public void onError(int code, String message) {
                    mView.showToast("??????????????????");
                }
            });
        }
    }

    /**
     * ?????????????????? ??????
     */
    public void enterSeatOwner(UiSeatModel seatModel) {
        if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty || seatModel.getSeatStatus() ==
                RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking) {
            //????????????????????????????????????????????????
            if (emptySeatFragment == null) {
                emptySeatFragment = new EmptySeatFragment();
            }
            int seatStatus = seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking ? 1 : 0;
            emptySeatFragment.setData(seatModel.getIndex(), seatStatus, seatModel.isMute(), this);
            emptySeatFragment.setSeatActionClickListener(this);
            emptySeatFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
        } else if (seatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
            //???????????????????????????
        }
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     *
     * @param uiSeatModels
     */
    private synchronized void refreshCurrentStatus(List<UiSeatModel> uiSeatModels) {
        try {
            boolean inseat = false;
            for (UiSeatModel uiSeatModel : uiSeatModels) {
                if (!TextUtils.isEmpty(uiSeatModel.getUserId()) && !TextUtils.isEmpty(UserManager.get().getUserId()) &&
                        uiSeatModel.getUserId().equals(UserManager.get().getUserId())) {
                    //??????????????????
                    inseat = true;
                    break;
                }
            }
            if (inseat) {
                //??????????????????????????????
                currentStatus = STATUS_ON_SEAT;
                mView.changeStatus(currentStatus);
            } else {
                //???????????????????????????
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //???????????????????????????,????????????????????????????????????????????????????????????
                } else {
                    //???????????????????????????
                    currentStatus = STATUS_NOT_ON_SEAT;
                }
                // ???????????????????????????????????????????????????
                if (TextUtils.equals(getCreateUserId(), UserManager.get().getUserId())) {
                    refreshMusicView(false);
                }
            }
            mView.changeStatus(currentStatus);
        } catch (Exception e) {
            Log.e(TAG, "refreshCurrentStatus: " + e);
        }
    }


    /**
     * ?????????????????????
     */
    private void setObRoomEventChange() {
        disposableList.add(voiceRoomModel.obRoomEventChange().subscribe(new Consumer<Pair<String, ArrayList<String>>>() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void accept(Pair<String, ArrayList<String>> stringArrayListPair) throws Throwable {
                String first = stringArrayListPair.first;
                if (TextUtils.equals(first, EVENT_ADD_SHIELD)) {
                    Shield shield = new Shield();
                    String name = stringArrayListPair.second.get(0);
                    shield.setName(name);
                    VoiceEventHelper.helper().getShield().add(shield);
                } else if (TextUtils.equals(first, EVENT_DELETE_SHIELD)) {
                    Iterator<Shield> iterator = VoiceEventHelper.helper().getShield().iterator();
                    String shile = stringArrayListPair.second.get(0);
                    while (iterator.hasNext()) {
                        Shield x = iterator.next();
                        if (x.getName().equals(shile)) {
                            iterator.remove();
                        }
                    }
                } else if (TextUtils.equals(first, EVENT_REQUEST_SEAT_AGREE)) {
                    //?????????????????????
                    currentStatus = STATUS_ON_SEAT;
                    //????????????
                    voiceRoomModel.enterSeatIfAvailable("");
                    //????????????????????????????????????
                    mView.changeStatus(currentStatus);
                } else if (TextUtils.equals(first, EVENT_REQUEST_SEAT_REFUSE)) {
                    //?????????????????????
                    mView.showToast("???????????????????????????");
                    currentStatus = STATUS_NOT_ON_SEAT;
                    //????????????????????????????????????
                    mView.changeStatus(currentStatus);
                } else if (TextUtils.equals(first, EVENT_KICK_OUT_OF_SEAT)) {
                    //????????????
                    mView.showToast("?????????????????????");
                } else if (TextUtils.equals(first, EVENT_REQUEST_SEAT_CANCEL)) {
                    //??????????????????
                    currentStatus = STATUS_NOT_ON_SEAT;
                    mView.changeStatus(currentStatus);
                } else if (TextUtils.equals(first, EVENT_MANAGER_LIST_CHANGE)) {
                    //??????????????????????????????
                    MemberCache.getInstance().refreshAdminData(getmVoiceRoomBean().getRoomId());
                    Log.e(TAG, "accept: " + "EVENT_MANAGER_LIST_CHANGE");
                } else if (TextUtils.equals(first, EVENT_KICKED_OUT_OF_ROOM)) {
                    //??????????????????
                    ArrayList<String> second = stringArrayListPair.second;
                    if (second.get(1).equals(UserManager.get().getUserId())) {
                        KToast.show("?????????????????????");
                        leaveRoom();
                    }
                } else if (TextUtils.equals(first, EVENT_ROOM_CLOSE)) {
                    //?????????????????????
                    VRCenterDialog confirmDialog = new VRCenterDialog(((VoiceRoomFragment) mView).requireActivity(), null);
                    confirmDialog.replaceContent("?????????????????????", "", null, "??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveRoom();
                        }
                    }, null);
                    confirmDialog.setCancelable(false);
                    confirmDialog.show();
                } else if (TextUtils.equals(first, EVENT_BACKGROUND_CHANGE)) {
                    mView.setRoomBackground(stringArrayListPair.second.get(0));
                } else if (TextUtils.equals(first, EVENT_AGREE_MANAGE_PICK)) {
                    if (stringArrayListPair.second.get(0).equals(UserManager.get().getUserId())) {
                        KToast.show("??????????????????");
                    }
                } else if (TextUtils.equals(first, EVENT_REJECT_MANAGE_PICK)) {
                    if (stringArrayListPair.second.get(0).equals(UserManager.get().getUserId())) {
                        KToast.show("??????????????????");
                    }
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
     * ??????????????????????????????
     *
     * @param seatModel
     * @return
     */
    public SelfSettingFragment showNewSelfSettingFragment(UiSeatModel seatModel) {
        SelfSettingFragment selfSettingFragment = new SelfSettingFragment(seatModel, mVoiceRoomBean.getRoomId()
                , voiceRoomModel, UserManager.get());
        return selfSettingFragment;
    }

    /**
     * ????????????????????????????????????
     */
    public void onClickRoomOwnerView(FragmentManager fragmentManager) {
        if (voiceRoomModel.getUiSeatModels().size() > 0) {
            UiSeatModel uiSeatModel = voiceRoomModel.getUiSeatModels().get(0);
            if (uiSeatModel != null) {
                if (!TextUtils.isEmpty(uiSeatModel.getUserId()) && uiSeatModel.getUserId().equals(UserManager.get().getUserId())) {
                    //??????????????????
                    CreatorSettingFragment creatorSettingFragment = new CreatorSettingFragment(voiceRoomModel, uiSeatModel, mVoiceRoomBean.getCreateUser());
                    creatorSettingFragment.show(fragmentManager);
                } else {
                    //????????????????????????????????????
                    roomOwnerEnterSeat(false);
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param messageContent
     */
    public void sendMessage(MessageContent messageContent) {
        VoiceEventHelper.helper().sendMessage(messageContent);
    }


    /**
     * ???????????????
     */
    public void getShield() {
        OkApi.get(VRApi.getShield(mVoiceRoomBean.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Shield> list = result.getList(Shield.class);
                    VoiceEventHelper.helper().getShield().clear();
                    if (list != null) {
                        VoiceEventHelper.helper().getShield().addAll(list);
                    }
                }
            }
        });
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

    @Override
    public void clickInviteSeat(int seatIndex, User user, ClickCallback<Boolean> callback) {
        if (PKManager.get().getPkState().enableAction()) {
            VoiceEventHelper.helper().pickUserToSeat(user.getUserId(), callback);
        }
    }

    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        VoiceEventHelper.helper().acceptRequestSeat(userId, callback);
    }

    @Override
    public void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {
        SensorsUtil.instance().recallConnect(getRoomId(), mVoiceRoomBean.getRoomName(), RcEvent.VoiceRoom);
        VoiceEventHelper.helper().cancelRequestSeat(new ClickCallback<Boolean>() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void onResult(Boolean result, String msg) {
                if (callback == null) return;
                callback.onResult(result, msg);
                if (result) {
                    KToast.show("?????????????????????");
                    voiceRoomModel.sendRoomEvent(new Pair<>(EVENT_REQUEST_SEAT_CANCEL, new ArrayList<>()));
                } else {
                    //????????????????????????????????????????????????????????????
                    if (voiceRoomModel.userInSeat()) {
                        KToast.show("????????????????????????");
                    } else {
                        KToast.show(msg);
                    }
                }
            }
        });
    }

    @Override
    public void cancelInvitation(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        VoiceEventHelper.helper().kickUserFromRoom(user, callback);
    }

    @Override
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        VoiceEventHelper.helper().kickUserFromSeat(user, callback);
    }

    @Override
    public void clickMuteSeat(int seatIndex, boolean isMute, ClickCallback<Boolean> callback) {
        VoiceEventHelper.helper().muteSeat(seatIndex, isMute, callback);
    }


    @Override
    public void clickCloseSeat(int seatIndex, boolean isLock, ClickCallback<Boolean> callback) {
        VoiceEventHelper.helper().lockSeat(seatIndex, isLock, callback);
    }

    @Override
    public void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {

    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     */
    public void sendGift() {
        SensorsUtil.instance().giftClick(getRoomId(), mVoiceRoomBean.getRoomName(), RcEvent.VoiceRoom);
        ArrayList<Member> memberArrayList = new ArrayList<>();
        //??????????????????
        ArrayList<UiSeatModel> uiSeatModels = voiceRoomModel.getUiSeatModels();
        Logger.e(TAG, "uiSeatModels:" + uiSeatModels.size());
        for (UiSeatModel uiSeatModel : uiSeatModels) {
            Logger.e(TAG, "uiSeatModel:" + GsonUtil.obj2Json(uiSeatModel.getMember()));
            if (uiSeatModel.getIndex() == 0) {
                //???????????????????????????????????????????????????
                User user = MemberCache.getInstance().getMember(mVoiceRoomBean.getCreateUserId());
                Member member = null;
                if (user == null) {
                    member = new Member();
                    member.setUserName(mVoiceRoomBean.getCreateUserName());
                    member.setUserId(mVoiceRoomBean.getCreateUserId());
                } else {
                    member = Member.fromUser(user);
                }
                member.setSeatIndex(uiSeatModel.getIndex());
                memberArrayList.add(member);
                continue;
            }
//            if (uiSeatModel != null && uiSeatModel.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
//                //????????????????????????
//                Member member = new Member().toMember(mVoiceRoomBean.getCreateUser());
//                member.setSeatIndex(uiSeatModel.getIndex());
//                memberArrayList.add(member);
//            }
            String userId = uiSeatModel.getUserId();
            if (!TextUtils.isEmpty(userId)) {
                User user = MemberCache.getInstance().getMember(userId);
                Member member = Member.fromUser(user);
                member.setSeatIndex(uiSeatModel.getIndex());
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
        Logger.e(TAG, "getCreateUserId:" + getCreateUserId());
        Logger.e(TAG, "memberArrayList:" + GsonUtil.obj2Json(memberArrayList));
        mView.showSendGiftDialog(mVoiceRoomBean, getCreateUserId(), memberArrayList);
    }

    /**
     * ???????????? ???????????????????????????
     *
     * @param user
     */
    @Override
    public void clickSendGift(User user) {
        String userId = user.getUserId();
        Member member = Member.fromUser(user);
        if (!TextUtils.isEmpty(userId)) {
            Logger.e(TAG, "clickSendGift: userId = " + userId);
            ArrayList<UiSeatModel> uiSeatModels = voiceRoomModel.getUiSeatModels();
            int count = uiSeatModels.size();
            for (int i = 0; i < count; i++) {
                UiSeatModel m = uiSeatModels.get(i);
                if (userId.equals(m.getUserId())) {
                    member.setSeatIndex(i);
                }
            }
        }
        mView.showSendGiftDialog(mVoiceRoomBean, user.getUserId(), Arrays.asList(member));
    }

    /**
     * ??????????????????
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
     * ???????????????????????????fragment
     *
     * @param index
     */
    @Override
    public void showSeatOperationViewPagerFragment(int index, int seatIndex) {
        SeatOperationViewPagerFragment seatOperationViewPagerFragment
                = new SeatOperationViewPagerFragment(getRoomOwnerType(), null);
        seatOperationViewPagerFragment.setRequestSeats(voiceRoomModel.getRequestSeats());
        seatOperationViewPagerFragment.setInviteSeats(voiceRoomModel.getInviteSeats());
        seatOperationViewPagerFragment.setIndex(index);
        seatOperationViewPagerFragment.setObInviteSeatListChangeSuject(voiceRoomModel.obInviteSeatListChange());
        seatOperationViewPagerFragment.setObRequestSeatListChangeSuject(voiceRoomModel.obRequestSeatListChange());
        seatOperationViewPagerFragment.setSeatActionClickListener(this);
        seatOperationViewPagerFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
    }

    /**
     * ????????????????????????
     */
    @Override
    public void showRevokeSeatRequestFragment() {
        RevokeSeatRequestFragment revokeSeatRequestFragment = new RevokeSeatRequestFragment();
        revokeSeatRequestFragment.setSeatActionClickListener(this);
        revokeSeatRequestFragment.show(((VoiceRoomFragment) mView).getChildFragmentManager());
    }

    /**
     * ??????????????????????????????
     *
     * @param isCreate ???????????????
     * @param userId   ????????????ID
     */
    public void showPickReceivedDialog(boolean isCreate, String userId) {
        String pickName = isCreate ? "??????" : "?????????";
        confirmDialog = new VRCenterDialog(((VoiceRoomFragment) mView).getActivity(), null);
        confirmDialog.replaceContent("??????" + pickName + "????????????????????????????",
                "??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        voiceRoomModel.refuseInvite(userId);
                    }
                }, "??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //??????
                        voiceRoomModel.enterSeatIfAvailable(userId);
                        confirmDialog.dismiss();
                        if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                            //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                            cancelRequestSeat(null);
                        }
                    }
                }, null);
        confirmDialog.show();
    }

    public void leaveRoom() {
        leaveRoom(null);
    }

    /**
     * ??????????????????
     */
    public void leaveRoom(IRoomCallBack callback) {
        mView.showLoading("");
        VoiceEventHelper.helper().leaveRoom(new IRoomCallBack() {
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
     * ??????????????????
     */
    public void closeRoom() {
        mView.showLoading("??????????????????");
        MusicControlManager.getInstance().release();
//        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_ROOM_CLOSE, "", null);
        VoiceEventHelper.helper().leaveRoom(new IRoomCallBack() {
            @Override
            public void onSuccess() {
                deleteRoom();
            }

            @Override
            public void onError(int code, String message) {
                mView.dismissLoading();
                mView.showToast(message);
                deleteRoom();
            }
        });
    }

    private void deleteRoom() {
        //?????????????????????????????????????????????
        if (mVoiceRoomBean != null)
            SensorsUtil.instance().closeRoom(getRoomId(), mVoiceRoomBean.getRoomName(), "", RcEvent.VoiceRoom);
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

    /**
     * ????????????
     */
    public void roomOwnerEnterSeat(boolean fromJoinRoom) {
        RCVoiceRoomEngine.getInstance().enterSeat(0, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                mView.enterSeatSuccess();
//                if (!fromJoinRoom) {
                if (null == voiceRoomModel) return;
                UiSeatModel seatModel = voiceRoomModel.getUiSeatModels().get(0);
                if (null != seatModel && null != seatModel.getExtra()) {
                    boolean disable = seatModel.getExtra().isDisableRecording();
                    voiceRoomModel.creatorMuteSelf(disable);
                }
//                } else {
//                    if (null == voiceRoomModel) return;
//                    // ?????????????????? ?????????disableRecord????????????false
//                    boolean disable = RCVoiceRoomEngine.getInstance().isDisableAudioRecording();
//                    UiSeatModel seatModel = voiceRoomModel.getUiSeatModels().get(0);
//                    UiSeatModel.UiSeatModelExtra extra = new UiSeatModel.UiSeatModelExtra();
//                    extra.setDisableRecording(disable);
//                    seatModel.setExtra(extra);
//                    voiceRoomModel.creatorMuteSelf(disable);
//                }
            }

            @Override
            public void onError(int i, String message) {
                mView.showToast(message);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param notice
     */
    public void modifyNotice(String notice) {
        //???????????????????????????
        UiRoomModel currentUIRoomInfo = voiceRoomModel.currentUIRoomInfo;
        RCVoiceRoomInfo rcRoomInfo = currentUIRoomInfo.getRcRoomInfo();
        rcRoomInfo.setExtra(notice);
        RCVoiceRoomEngine.getInstance().setRoomInfo(rcRoomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                sendNoticeModifyMessage();
                //??????????????????
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(int i, String s) {
                //??????????????????
                Log.e(TAG, "onError: ");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //???????????????????????????
        unInitListener();
    }

    /**
     * ?????????????????????????????????
     */
    public void unInitListener() {
        for (Disposable disposable : disposableList) {
            disposable.dispose();
        }
        disposableList.clear();
        VoiceEventHelper.helper().removeRCVoiceRoomEventListener(voiceRoomModel);
//        EventBus.get().off(UPDATE_SHIELD, null);
    }

    /**
     * ?????????????????????
     */
    private void sendNoticeModifyMessage() {
        TextMessage tips = TextMessage.obtain("?????????????????????!");
        sendMessage(tips);
    }

    /**
     * ??????????????????
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
                new MutableLiveData<>(new RoomSeatSizeFun(voiceRoomModel.currentUIRoomInfo.getSeatCount() == 5 ? 1 : 0)),
                new MutableLiveData<>(new RoomShieldFun(0)),
                new MutableLiveData<>(new RoomMusicFun(0))
        );
        mView.showSettingDialog(funList);
    }

    /**
     * ???????????????
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
                //????????????
                setSeatMode(false);
            } else {
                //????????????
                setSeatMode(true);
            }
        } else if (fun instanceof RoomMuteAllFun) {
            if (fun.getStatus() == 1) {
                //????????????
                setAllSeatLock(false);
            } else {
                //????????????
                setAllSeatLock(true);
            }
        } else if (fun instanceof RoomLockAllSeatFun) {
            if (fun.getStatus() == 1) {
                //????????????
                lockOtherSeats(false);
            } else {
                //????????????
                lockOtherSeats(true);
            }
        } else if (fun instanceof RoomMuteFun) {
            if (fun.getStatus() == 1) {
                //????????????
                muteAllRemoteStreams(false);
            } else {
                //??????
                muteAllRemoteStreams(true);
            }
        } else if (fun instanceof RoomSeatSizeFun) {
            if (fun.getStatus() == 1) {
                //??????8?????????
                setSeatCount(9);
            } else {
                //??????4?????????
                setSeatCount(5);
            }
        } else if (fun instanceof RoomMusicFun) {
            //?????? ??????????????????????????????
            UiSeatModel seatInfoByUserId = voiceRoomModel.getSeatInfoByUserId(UserManager.get().getUserId());
            if (seatInfoByUserId != null && seatInfoByUserId.getSeatStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                //?????????????????????????????????
                mView.showMusicDialog();
            } else {
                mView.showToast("?????????????????????????????????");
            }
        }
        SensorsUtil.instance().settingClick(getRoomId(), mVoiceRoomBean.getRoomName(), fun.getText(), RoomFunIdUitls.convert(fun), RcEvent.VoiceRoom);
    }

    /**
     * ??????????????????
     *
     * @param seatCount
     */
    private void setSeatCount(int seatCount) {
        voiceRoomModel.currentUIRoomInfo.setSeatCount(seatCount);
        RCVoiceRoomEngine.getInstance().setRoomInfo(voiceRoomModel.currentUIRoomInfo.getRcRoomInfo(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //??????????????????
                RCChatroomSeats rcChatroomSeats = new RCChatroomSeats();
                rcChatroomSeats.setCount(seatCount - 1);
                RCChatRoomMessageManager.sendChatMessage(getmVoiceRoomBean().getRoomId(), rcChatroomSeats
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
     * ?????? ????????????
     *
     * @param isMute
     */
    private void muteAllRemoteStreams(boolean isMute) {
        RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute);
        voiceRoomModel.currentUIRoomInfo.setMute(isMute);
        VoiceEventHelper.helper().setMuteAllRemoteStreams(isMute);
        if (isMute) {
            KToast.show("??????????????????");
        } else {
            KToast.show("???????????????");
        }
        //?????????????????????????????????????????????????????????????????????????????????
    }

    /**
     * ????????????
     */
    private void lockOtherSeats(boolean isLockAll) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll, null);
        if (isLockAll) {
            KToast.show("?????????????????????");
        } else {
            KToast.show("???????????????");
        }
    }

    /**
     * ????????????
     *
     * @param isMuteAll
     */
    private void setAllSeatLock(boolean isMuteAll) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll, null);
        if (isMuteAll) {
            KToast.show("?????????????????????");
        } else {
            KToast.show("???????????????");
        }
    }

    /**
     * ?????????????????????
     */
    public void setSeatMode(boolean isFreeEnterSeat) {
        voiceRoomModel.currentUIRoomInfo.setFreeEnterSeat(isFreeEnterSeat);
        RCVoiceRoomEngine.getInstance().setRoomInfo(voiceRoomModel.currentUIRoomInfo.getRcRoomInfo(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                if (isFreeEnterSeat) {
                    KToast.show("???????????????????????????");
                } else {
                    KToast.show("???????????????????????????");
                }
            }

            @Override
            public void onError(int i, String s) {
                KToast.show(s);
            }
        });
    }

    /**
     * ??????????????????
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
                            KToast.show(isPrivate ? "????????????" : "????????????");
                            mVoiceRoomBean.setIsPrivate(isPrivate ? 1 : 0);
                            mVoiceRoomBean.setPassword(password);
                            IFun.BaseFun fun = item.getValue();
                            fun.setStatus(p);
                            item.setValue(fun);
                        } else {
                            KToast.show(isPrivate ? "????????????" : "????????????");
                        }
                    }
                });
    }


    /**
     * ??????????????????
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
                            KToast.show("????????????");
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
                            String message = result.getMessage();
                            KToast.show(!TextUtils.isEmpty(message) ? message : "????????????");
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        KToast.show(!TextUtils.isEmpty(msg) ? msg : "????????????");
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
                    //??????????????????
                    RCVoiceRoomEngine.getInstance()
                            .notifyVoiceRoom(EVENT_BACKGROUND_CHANGE, url, null);
                    KToast.show("????????????");
                } else {
                    KToast.show("????????????");
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show("????????????");
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
     * ??????????????????????????? ,????????????
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
     * ??????id??????????????????
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
     * ????????????????????????
     */
    public void refreshRoomMember() {
        MemberCache.getInstance().fetchData(getRoomId());
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param message
     */
    public void jumpRoom(RCAllBroadcastMessage message) {
        // ?????????????????????
        if (message == null || TextUtils.isEmpty(message.getRoomId()) || TextUtils.equals(message.getRoomId(), getRoomId())
                || voiceRoomModel.getSeatInfoByUserId(UserManager.get().getUserId()) != null
                || TextUtils.equals(UserManager.get().getUserId(), mVoiceRoomBean.getCreateUserId()))
            return;
        OkApi.get(VRApi.getRoomInfo(message.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                    if (roomBean != null) {
                        // ???????????????????????????????????????
                        if (roomBean.isPrivate()) {
                            inputPasswordDialog = new InputPasswordDialog(((VoiceRoomFragment) mView).requireContext(), false, new InputPasswordDialog.OnClickListener() {
                                @Override
                                public void clickCancel() {

                                }

                                @Override
                                public void clickConfirm(String password) {
                                    if (TextUtils.isEmpty(password)) {
                                        return;
                                    }
                                    if (password.length() < 4) {
                                        mView.showToast(UIKit.getResources().getString(R.string.text_please_input_four_number));
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
            leaveRoom(new IRoomCallBack() {
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
