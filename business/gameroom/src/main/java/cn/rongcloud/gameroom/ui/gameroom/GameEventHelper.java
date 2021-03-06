package cn.rongcloud.gameroom.ui.gameroom;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

import androidx.lifecycle.MutableLiveData;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.gamelib.api.RCGameEngine;
import cn.rongcloud.gamelib.api.interfaces.RCGamePlayerStateListener;
import cn.rongcloud.gamelib.api.interfaces.RCGameStateListener;
import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gamelib.model.RCGameLoadingStage;
import cn.rongcloud.gamelib.model.RCGameSettle;
import cn.rongcloud.gamelib.model.RCGameState;
import cn.rongcloud.gamelib.utils.VMLog;
import cn.rongcloud.gameroom.api.GameApi;
import cn.rongcloud.gameroom.model.GameRoomBean;
import cn.rongcloud.gameroom.model.LoginGameBean;
import cn.rongcloud.gameroom.model.SeatPlayer;
import cn.rongcloud.music.MusicApi;
import cn.rongcloud.music.MusicBean;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.manager.AllBroadcastManager;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
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
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.fragment.MemberSettingFragment;
import cn.rongcloud.roomkit.ui.room.fragment.gift.GiftFragment;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockAllSeatFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteAllFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomMuteFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.callback.IRCRTCAudioDataListener;
import cn.rongcloud.rtc.api.stream.RCRTCMicOutputStream;
import cn.rongcloud.rtc.base.RCRTCAudioFrame;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandMessage;
import io.rong.message.TextMessage;

/**
 * @author gyn
 * @date 2022/5/5
 */
public class GameEventHelper implements RCVoiceRoomEventListener, RCGameStateListener, RCGamePlayerStateListener, MemberSettingFragment.OnMemberSettingClickListener, GiftFragment.OnSendGiftListener {
    private static final String TAG = GameEventHelper.class.getSimpleName();
    private static final int MIN_SEAT_COUNT = 9;
    private String mRoomId;
    private boolean isCreate;
    private boolean isFastIn;
    private String mFastInGameId;
    private GameRoomBean mGameRoomBean;
    private RCGameInfo mGameInfo;
    private String mAppCode;
    private String mUserId;
    private IGameRoomListener mGameRoomListener;
    private RoomOwnerType mRoomOwnerType;
    private List<Shield> mShields;
    // ????????????????????????
    private List<SeatPlayer> mSeatPlayers;
    // ?????????????????????id
    private List<String> mJoinedPlayerIds;
    // ?????????????????????id
    private List<String> mReadyPlayerIds;
    // ??????????????????id
    private List<String> mPlayingPlayerIds;
    // ????????????
    private List<RCVoiceSeatInfo> mSeatInfos;
    // ?????????uid
    private volatile String mCaptainId;
    // ???????????????
    private SparseBooleanArray mSpeakingArray;
    private int mSeatCount = MIN_SEAT_COUNT;
    private boolean isFirstLoadedGame = true;
    private Object object = new Object();
    private RCVoiceRoomInfo mRcVoiceRoomInfo;
    private boolean isMuteAll = false;
    private RCGameState mGameState = RCGameState.LOADING;
    private String mKeyword = "";
    private boolean isInSeat;

    private static class Holder {

        private static final GameEventHelper INSTANCE = new GameEventHelper();
    }


    public static GameEventHelper getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * ?????????????????????
     */
    public void init(String roomId, boolean isCreate, boolean isFastIn, String fastInGameId) {
        this.mRoomId = roomId;
        this.isCreate = isCreate;
        this.mUserId = UserManager.get().getUserId();
        this.isFastIn = isFastIn;
        this.mFastInGameId = fastInGameId;
        mShields = new ArrayList<>();
        mSeatPlayers = new ArrayList<>();
        mSeatInfos = new ArrayList<>();
        mJoinedPlayerIds = new ArrayList<>();
        mReadyPlayerIds = new ArrayList<>();
        mPlayingPlayerIds = new ArrayList<>();
        mSpeakingArray = new SparseBooleanArray();
        // ??????????????????
        RCGameEngine.getInstance().setGameStateListener(this);
        // ??????????????????
        RCGameEngine.getInstance().setGamePlayerStateListener(this);
        // ??????????????????
        getRoomInfo();
        // ?????????????????????
        getShields();
    }

    public void setGameRoomListener(IGameRoomListener gameRoomListener) {
        this.mGameRoomListener = gameRoomListener;
    }

    public void unInit() {
        MusicControlManager.getInstance().release();
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(null);
        this.mGameRoomListener = null;
        mGameRoomBean = null;
        mGameInfo = null;
        mShields = null;
        mSeatPlayers = null;
        mSeatInfos = null;
        mReadyPlayerIds.clear();
        mJoinedPlayerIds.clear();
        mPlayingPlayerIds.clear();
        mSpeakingArray.clear();
        mCaptainId = "";
        isMuteAll = false;
        isFirstLoadedGame = true;
        mKeyword = "";
    }

    /**
     * ??????????????????
     */
    private void getRoomInfo() {
        OkApi.get(VRApi.getRoomInfo(mRoomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    GameRoomBean roomBean = result.get(GameRoomBean.class);
                    if (roomBean != null) {
                        mGameRoomBean = roomBean;
                        mGameInfo = roomBean.getGameInfo();
                        mRoomOwnerType = TextUtils.equals(mUserId, mGameRoomBean.getCreateUserId()) ? RoomOwnerType.GAME_OWNER : RoomOwnerType.GAME_VIEWER;
                        if (mGameRoomListener != null) {
                            mGameRoomListener.onLoadRoomDetail(mGameRoomBean);
                        }
                        mSeatCount = Math.max(MIN_SEAT_COUNT, mGameInfo.getMaxSeat());
                        preLeaveRoom();
                    }
                } else {
                    if (result.getCode() == 30001) {
                        changeUserRoom("");
                        if (mGameRoomListener != null) {
                            mGameRoomListener.showRoomFinished();
                        }
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
     * ?????????????????????????????????????????????????????????
     */
    private void preLeaveRoom() {
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                createOrJoinRoom();
            }

            @Override
            public void onError(int i, String s) {
                createOrJoinRoom();
            }
        });
    }

    /**
     * ??????id??????????????????
     */
    public void preShowMemberSetting(String userId) {
        OkApi.post(VRApi.GET_USER, new OkParams().add("userIds", new String[]{userId}).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Member> members = result.getList(Member.class);
                    if (members != null && members.size() > 0) {
                        if (mGameRoomListener != null) {
                            Member member = members.get(0);
                            SeatPlayer seatPlayer = null;
                            for (SeatPlayer mSeatPlayer : mSeatPlayers) {
                                if (TextUtils.equals(member.getUserId(), mSeatPlayer.userId)) {
                                    seatPlayer = mSeatPlayer;
                                    break;
                                }
                            }
                            mGameRoomListener.showMemberSetting(members.get(0), seatPlayer, mGameRoomBean.getCreateUserId());
                        }
                    }
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void createOrJoinRoom() {
        // ??????????????????,????????????????????????????????????leaveRoom??????????????????
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(this);
        // ??????????????????????????????????????????????????????????????????16K????????????
        // ?????????????????????????????????????????????????????????????????????????????????16K?????????
        // RCGameEngine.getInstance().pushAudio(); ?????????????????????16K????????????PCM??????
        RCRTCConfig rcrtcConfig = RCRTCConfig.Builder.create()
                // ??????16K?????????
                .setAudioSampleRate(16000)
                // ???????????????
                .enableStereo(false)
                .build();
        if (isCreate) {
            RCVoiceRoomInfo rcVoiceRoomInfo = new RCVoiceRoomInfo();
            rcVoiceRoomInfo.setRoomName(mGameRoomBean.getRoomName());
            rcVoiceRoomInfo.setSeatCount(mSeatCount);
            rcVoiceRoomInfo.setFreeEnterSeat(true);
            rcVoiceRoomInfo.setLockAll(false);
            rcVoiceRoomInfo.setMuteAll(false);
            RCVoiceRoomEngine.getInstance().createAndJoinRoom(rcrtcConfig, mGameRoomBean.getRoomId(), rcVoiceRoomInfo, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    initAndLoadGame();
                    if (mRoomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true);
                    }
                    sendSystemMessage();
                    resetAudioQuality();
                    // ????????????????????????
                    MemberCache.getInstance().fetchData(mRoomId);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        } else {
            RCVoiceRoomEngine.getInstance().joinRoom(rcrtcConfig, mGameRoomBean.getRoomId(), new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    initAndLoadGame();
                    if (mRoomOwnerType != RoomOwnerType.VOICE_OWNER) {
                        refreshMusicView(true);
                    }
                    sendSystemMessage();
                    resetAudioQuality();
                    // ????????????????????????
                    MemberCache.getInstance().fetchData(mRoomId);
                    VMLog.d(TAG, "==============??????????????????");
                }

                @Override
                public void onError(int i, String s) {
                    VMLog.e(TAG, "==============?????????????????? code???" + i + " msg: " + s);
                }
            });
        }
    }

    // ?????????????????????->????????????
    private void initAndLoadGame() {
        // ?????????????????????????????????????????????appCode
        login(new LoginCallback() {
            @Override
            public void onSuccess(String code) {
                mAppCode = code;
                if (mGameRoomListener != null) {
                    if (isFastIn && !TextUtils.isEmpty(mFastInGameId) && !TextUtils.equals(mGameInfo.getGameId(), mFastInGameId)) {
                        fastInSwitchGame();
                    } else {
                        mGameRoomListener.onLoadGame(mGameRoomBean.getRoomId(), mGameInfo.getGameId(), mUserId, mAppCode);
                    }
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    private void fastInSwitchGame() {
        OkApi.get(GameApi.GAME_LIST, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<RCGameInfo> gameInfoList = result.getList(RCGameInfo.class);
                if (gameInfoList != null) {
                    for (RCGameInfo gameInfo : gameInfoList) {
                        if (TextUtils.equals(gameInfo.getGameId(), mFastInGameId)) {
                            if (mGameRoomListener != null)
                                mGameRoomListener.onLoadGame(mGameRoomBean.getRoomId(), mGameInfo.getGameId(), mUserId, mAppCode);
                            switchGame(gameInfo, true);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void readyEnterSeat() {
        boolean isInSeat = false;
        int seatIndex = -1;
        RCVoiceSeatInfo seatInfo;
        for (int i = 0; i < mSeatInfos.size(); i++) {
            seatInfo = mSeatInfos.get(i);
            // ??????????????????????????????
            if (TextUtils.equals(seatInfo.getUserId(), mUserId)) {
                isInSeat = true;
                // ????????????????????????
            } else if (seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
                if (seatIndex == -1) {
                    seatIndex = i;
                }
            }
        }
        // ??????????????????????????????
        if (isInSeat) {
            return;
        }
        if (seatIndex == -1) {
            KToast.show("???????????????????????????????????????");
            exitGame();
            return;
        }
        enterSeat(seatIndex, null);
    }

    private void enterSeat(int index, RCVoiceRoomCallback callback) {
        showLoading("");
        // Logger.e("=========================AudioScenario=" + RCRTCEngine.getInstance().getDefaultAudioStream().getAudioScenario() + ",isMute=" + RCRTCEngine.getInstance().getDefaultAudioStream().isMute());

        RCVoiceRoomEngine.getInstance().enterSeat(index, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                resetAudioQuality();
                KToast.show("????????????");
                dismissLoading();
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                KToast.show("???????????????" + s);
                dismissLoading();
                if (callback != null)
                    callback.onError(i, s);
            }
        });
    }

    // ?????????SDK??????AudioScenario?????????MUSIC_CHATROOM???????????????DEFAULT???????????????????????????????????????????????????
    private void resetAudioQuality() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                RCRTCMicOutputStream defaultAudioStream = RCRTCEngine.getInstance().getDefaultAudioStream();
                if (defaultAudioStream != null) {
                    defaultAudioStream.setAudioQuality(RCRTCParamsType.AudioQuality.MUSIC_HIGH, RCRTCParamsType.AudioScenario.DEFAULT);
                    SeatPlayer seatPlayer = getSeatPlayer(mUserId);
                    if (seatPlayer != null) {
                        defaultAudioStream.mute(seatPlayer.isMute);
                    }
                }
            }
        }).start();
    }

    /**
     * ????????????????????????
     *
     * @param seatPlayer
     */
    public void clickSeat(SeatPlayer seatPlayer) {
        // ??????????????????
        if (seatPlayer.playerState == SeatPlayer.PlayerState.EMPTY) {
            // ?????????????????????????????????
            if (!isInSeat(mUserId)) {
                // ????????????????????????
                if (seatPlayer.isLock) {
                    // ??????????????????????????????????????????
                    if (mRoomOwnerType == RoomOwnerType.GAME_OWNER) {
                        if (mGameRoomListener != null) {
                            mGameRoomListener.showEmptySeatDialog(seatPlayer);
                        }
                    } else {
                        KToast.show("??????????????????");
                    }
                } else {
                    enterSeat(seatPlayer.seatIndex, null);
                }
            } else {// ??????????????????
                // ??????????????????????????????????????????
                if (mRoomOwnerType == RoomOwnerType.GAME_OWNER) {
                    if (mGameRoomListener != null) {
                        mGameRoomListener.showEmptySeatDialog(seatPlayer);
                    }
                } else if (isCaptain(mUserId)) {
                    //???????????????????????????????????????????????????

                }
                // ????????????????????????????????????
            }
        } else {// ????????????
            // ????????????,??????????????????????????????
            if (TextUtils.equals(mUserId, seatPlayer.userId)) {
                if (mGameRoomListener != null) {
                    mGameRoomListener.showSelfSettingDialog(seatPlayer);
                }
            } else {
                preShowMemberSetting(seatPlayer.userId);
            }
        }
    }

    /**
     * ?????????
     */
    public void sendGift() {
        ArrayList<Member> memberArrayList = new ArrayList<>();
        // ?????????????????????
        Member member = Member.fromUser(mGameRoomBean.getCreateUser());
        memberArrayList.add(member);
        for (SeatPlayer seatPlayer : mSeatPlayers) {
            if (seatPlayer.playerState != SeatPlayer.PlayerState.EMPTY) {
                User user = MemberCache.getInstance().getMember(seatPlayer.userId);
                if (user != null) {
                    member = Member.fromUser(user);
                    member.setSeatIndex(seatPlayer.seatIndex);
                    if (memberArrayList.contains(member)) {
                        memberArrayList.remove(0);
                        memberArrayList.add(0, member);
                    } else {
                        memberArrayList.add(member);
                    }
                }
            }
        }
        // ????????????????????????????????????
        Member creator = memberArrayList.remove(0);
        Comparator<Member> comparator = new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getSeatIndex() - o2.getSeatIndex();
            }
        };
        // ??????????????????????????????
        Collections.sort(memberArrayList, comparator);
        memberArrayList.add(0, creator);

        Logger.d(TAG, "memberArrayList:" + GsonUtil.obj2Json(memberArrayList));
        if (mGameRoomListener != null) {
            mGameRoomListener.showSendGiftDialog(mGameRoomBean, mGameRoomBean.getCreateUserId(), memberArrayList);
        }
    }

    /**
     * ??????????????????
     *
     * @param notice
     */
    public void modifyNotice(String notice) {
        //???????????????????????????
        mRcVoiceRoomInfo.setExtra(notice);
        RCVoiceRoomEngine.getInstance().setRoomInfo(mRcVoiceRoomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                TextMessage tips = TextMessage.obtain("?????????????????????!");
                sendMessage(tips);
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
                        .add("roomId", mRoomId)
                        .add("isPrivate", p)
                        .add("password", password).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            KToast.show(isPrivate ? "????????????" : "????????????");
                            mGameRoomBean.setIsPrivate(isPrivate ? 1 : 0);
                            mGameRoomBean.setPassword(password);
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
                        .add("roomId", mRoomId)
                        .add("name", name)
                        .build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            KToast.show("????????????");
                            if (mGameRoomListener != null) {
                                mGameRoomListener.setRoomName(name);
                            }
                            mGameRoomBean.setRoomName(name);
                            mRcVoiceRoomInfo.setRoomName(name);
                            RCVoiceRoomEngine.getInstance().setRoomInfo(mRcVoiceRoomInfo, new RCVoiceRoomCallback() {
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

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    public int getAvailableSeatIndex() {
        synchronized (object) {
            int availableIndex = -1;
            for (int i = 0; i < mSeatInfos.size(); i++) {
                RCVoiceSeatInfo seat = mSeatInfos.get(i);
                if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                    availableIndex = i;
                    break;
                }
            }
            return availableIndex;
        }
    }

    public String getNotice() {
        if (mRcVoiceRoomInfo != null && !TextUtils.isEmpty(mRcVoiceRoomInfo.getExtra())) {
            return mRcVoiceRoomInfo.getExtra();
        } else {
            return String.format("???????????? %s", mGameRoomBean.getRoomName());
        }
    }

    public String getRoomName() {
        if (mGameRoomBean != null) {
            return mGameRoomBean.getRoomName();
        }
        return "";
    }

    /**
     * ????????????????????????????????????
     *
     * @param gameInfo
     * @param isSelfSwitch
     */
    public void switchGame(RCGameInfo gameInfo, boolean isSelfSwitch) {
        mGameInfo = gameInfo;
        mGameRoomBean.setGameInfo(gameInfo);
        exitGame();
        RCGameEngine.getInstance().endGame(null);
        RCGameEngine.getInstance().switchGame(gameInfo.getGameId());
        KToast.show("?????????????????????");
        if (mGameRoomListener != null) {
            mGameRoomListener.onGameChanged(gameInfo);
        }
        // ????????????
        int newSeatCount = Math.max(MIN_SEAT_COUNT, mGameInfo.getMaxSeat());
        // ????????????????????????
        boolean isSelfInSeat = isInSeat(mUserId);
        // ???????????????????????????
        boolean isChangeSeatCount = newSeatCount != mSeatCount;
        if (isSelfInSeat) {
            if (isRoomOwner() && !isChangeSeatCount) {
                // ????????????????????????????????????????????????????????????????????????
            } else {
                leaveSeat();
            }
        }
        // ?????????????????????????????????????????????????????????????????????????????????
        if (isSelfSwitch) {
            if (isChangeSeatCount) {
                changeSeatCount(newSeatCount, isSelfInSeat);
            }
            notifyRoom(GameConstant.EVENT_SWITCH_GAME, GsonUtil.obj2Json(gameInfo));
            uploadSwitchGame(gameInfo.getGameId());
        }
        mCaptainId = "";
        mPlayingPlayerIds.clear();
        mJoinedPlayerIds.clear();
        mReadyPlayerIds.clear();
    }

    public boolean isRoomOwner() {
        return getRoomOwnerType() == RoomOwnerType.GAME_OWNER;
    }

    /**
     * ??????????????????
     *
     * @param seatCount
     * @param isSelfInSeat
     */
    private void changeSeatCount(int seatCount, boolean isSelfInSeat) {
        mSeatCount = seatCount;
        mRcVoiceRoomInfo.setSeatCount(seatCount);
        RCVoiceRoomEngine.getInstance().setRoomInfo(mRcVoiceRoomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                // ???????????????????????????????????????
                if (isRoomOwner() && isSelfInSeat) {
                    readyEnterSeat();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void uploadSwitchGame(String gameId) {
        Map<String, Object> params = OkParams.Builder()
                .add("gameId", gameId)
                .add("roomId", mRoomId)
                .build();
        OkApi.post(GameApi.SWITCH_GAME, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {

            }
        });
    }

    /**
     * ??????????????????????????????code
     *
     * @param callback
     */
    private void login(LoginCallback callback) {
        OkApi.post(GameApi.GAME_LOGIN_URL, OkParams.Builder().add("userId", mUserId).build(), new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                Log.e("==========", result.getBody().toString());
                if (result.ok()) {
                    LoginGameBean loginBean = result.get(LoginGameBean.class);
                    if (loginBean != null) {
                        callback.onSuccess(loginBean.code);
                    } else {
                        callback.onError();
                    }
                } else {
                    callback.onError();
                }
            }
        });
    }

    //??????????????????
    public void changeUserRoom(String roomId) {
        HashMap<String, Object> params = OkParams.Builder()
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
     * ???????????????
     */
    public void getShields() {
        OkApi.get(VRApi.getShield(mRoomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Shield> list = result.getList(Shield.class);
                    mShields.clear();
                    if (list != null) {
                        mShields.addAll(list);
                    }
                }
            }
        });
    }

    /**
     * ????????????
     *
     * @param isMuteAll
     */
    public void setAllSeatLock(boolean isMuteAll) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll, null);
        if (isMuteAll) {
            KToast.show("?????????????????????");
        } else {
            KToast.show("???????????????");
        }
    }

    /**
     * ????????????
     */
    public void lockOtherSeats(boolean isLockAll) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll, null);
        if (isLockAll) {
            KToast.show("?????????????????????");
        } else {
            KToast.show("???????????????");
        }
    }


    /**
     * ?????? ????????????
     *
     * @param isMute
     */
    public void muteAllRemoteStreams(boolean isMute) {
        RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute);
        isMuteAll = isMute;
        if (isMute) {
            KToast.show("??????????????????");
        } else {
            KToast.show("???????????????");
        }
    }

    /**
     * ???????????????????????????UI
     *
     * @param show ????????????
     */
    private void refreshMusicView(boolean show) {
        if (show) {
            MusicApi.getPlayingMusic(mRoomId, new IResultBack<MusicBean>() {
                @Override
                public void onResult(MusicBean musicBean) {
                    if (mGameRoomListener != null) {
                        if (musicBean != null) {
                            mGameRoomListener.refreshMusicView(true);
                        } else {
                            mGameRoomListener.refreshMusicView(false);
                        }
                    }
                }
            });
        } else {
            if (mGameRoomListener != null) {
                mGameRoomListener.refreshMusicView(false);
            }
        }
    }

    public int getSeatCount() {
        return mSeatCount;
    }

    public boolean isInSeat(String uid) {
        for (SeatPlayer mSeatPlayer : mSeatPlayers) {
            if (TextUtils.equals(uid, mSeatPlayer.userId)) {
                return true;
            }
        }
        return false;
    }

    public SeatPlayer getSeatPlayer(String uid) {
        for (SeatPlayer mSeatPlayer : mSeatPlayers) {
            if (TextUtils.equals(uid, mSeatPlayer.userId)) {
                return mSeatPlayer;
            }
        }
        return null;
    }

    public boolean isCaptain(String uid) {
        return TextUtils.equals(uid, mCaptainId);
    }

    public boolean isInGame(String uid) {
        return mJoinedPlayerIds.contains(uid);
    }

    public boolean isInGaming() {
        return mGameState == RCGameState.PLAYING;
    }

    public boolean isRoomOwner(String uid) {
        return TextUtils.equals(uid, mGameRoomBean.getCreateUserId());
    }

    public List<MutableLiveData<IFun.BaseFun>> getSettingFunction() {
        List<MutableLiveData<IFun.BaseFun>> funList = Arrays.asList(
                new MutableLiveData<>(new RoomLockFun(mGameRoomBean.isPrivate() ? 1 : 0)),
                new MutableLiveData<>(new RoomNameFun(0)),
                new MutableLiveData<>(new RoomNoticeFun(0)),
                new MutableLiveData<>(new RoomMuteAllFun(mRcVoiceRoomInfo.isMuteAll() ? 1 : 0)),
                new MutableLiveData<>(new RoomLockAllSeatFun(mRcVoiceRoomInfo.isLockAll() ? 1 : 0)),
                new MutableLiveData<>(new RoomMuteFun(isMuteAll ? 1 : 0)),
                new MutableLiveData<>(new RoomShieldFun(0)),
                new MutableLiveData<>(new RoomMusicFun(0))
        );
        return funList;
    }

    /**
     * ??????????????????
     * ??????????????????????????????-?????????????????????????????????-??????-?????????
     */
    public void refreshSeatPlayers() {
        synchronized (object) {
            if (mGameRoomListener == null) {
                return;
            }
            mSeatPlayers.clear();
            SeatPlayer seatPlayer;
            RCVoiceSeatInfo seatInfo;
            for (int i = 0; i < mSeatInfos.size(); i++) {
                seatInfo = mSeatInfos.get(i);
                seatPlayer = new SeatPlayer();
                seatPlayer.seatIndex = i;
                seatPlayer.isMute = seatInfo.isMute();
                if (seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing) {
                    seatPlayer.userId = seatInfo.getUserId();
                    if (mPlayingPlayerIds.contains(seatPlayer.userId)) {
                        seatPlayer.playerState = SeatPlayer.PlayerState.PLAY;
                    } else if (mReadyPlayerIds.contains(seatPlayer.userId)) {
                        seatPlayer.playerState = SeatPlayer.PlayerState.READY;
                    } else {
                        seatPlayer.playerState = SeatPlayer.PlayerState.IDLE;
                    }
                    seatPlayer.isLock = false;
                    seatPlayer.isCaptain = TextUtils.equals(mCaptainId, seatPlayer.userId);
                    seatPlayer.isAdmin = TextUtils.equals(seatPlayer.userId, mGameRoomBean.getCreateUserId())
                            || MemberCache.getInstance().isAdmin(seatPlayer.userId);
                } else {
                    seatPlayer.playerState = SeatPlayer.PlayerState.EMPTY;
                    seatPlayer.isLock = seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusLocking;
                    seatPlayer.isCaptain = false;
                    seatPlayer.isAdmin = false;
                }
                mSeatPlayers.add(seatPlayer);
            }
            // ??????
            mSeatPlayers = sortSeatPlayers(mSeatPlayers);
            mGameRoomListener.onSeatPlayerChanged(mSeatPlayers);
            // Logger.e("===============refresh seat " + GsonUtil.obj2Json(mSeatPlayers));
        }
    }

    private synchronized List<SeatPlayer> sortSeatPlayers(List<SeatPlayer> seatPlayerList) {
        List<SeatPlayer> sortedSeatPlayers = new ArrayList<>();
        List<SeatPlayer> inGameSeat = new ArrayList<>();
        List<SeatPlayer> outGameSeat = new ArrayList<>();
        List<SeatPlayer> emptyGameSeat = new ArrayList<>();
        for (SeatPlayer seatPlayer : seatPlayerList) {
            // ?????????
            if (seatPlayer.playerState == SeatPlayer.PlayerState.EMPTY) {
                emptyGameSeat.add(seatPlayer);
            } else {// ????????????
                // ????????????
                if (mJoinedPlayerIds.contains(seatPlayer.userId)) {
                    inGameSeat.add(seatPlayer);
                } else {//???????????????
                    outGameSeat.add(seatPlayer);
                }
            }
        }

        Comparator<SeatPlayer> inSeatComparator = new Comparator<SeatPlayer>() {
            @Override
            public int compare(SeatPlayer o1, SeatPlayer o2) {
                // ????????????
                if (o1.isCaptain != o2.isCaptain) {
                    return o1.isCaptain ? -1 : 1;
                }
                // ????????????`
                if (o1.isAdmin != o2.isAdmin) {
                    return o1.isAdmin ? -1 : 1;
                }
                return 0;
            }
        };

        Comparator<SeatPlayer> outSeatComparator = new Comparator<SeatPlayer>() {
            @Override
            public int compare(SeatPlayer o1, SeatPlayer o2) {
                // ????????????
                if (isRoomOwner(o1.userId) != isRoomOwner(o2.userId)) {
                    return isRoomOwner(o1.userId) ? -1 : 1;
                }
                // ????????????`
                if (o1.isAdmin != o2.isAdmin) {
                    return o1.isAdmin ? -1 : 1;
                }
                return 0;
            }
        };

        // Comparator<SeatPlayer> emptySeatComparator = new Comparator<SeatPlayer>() {
        //     @Override
        //     public int compare(SeatPlayer o1, SeatPlayer o2) {
        //         // ????????????????????????
        //         if (o1.isLock != o2.isLock) {
        //             return o1.isLock ? 1 : -1;
        //         }
        //         return 0;
        //     }
        // };

        Collections.sort(inGameSeat, inSeatComparator);
        Collections.sort(outGameSeat, outSeatComparator);
        // Collections.sort(emptyGameSeat, emptySeatComparator);
        sortedSeatPlayers.addAll(inGameSeat);
        sortedSeatPlayers.addAll(outGameSeat);
        sortedSeatPlayers.addAll(emptyGameSeat);
        return sortedSeatPlayers;
    }

    /**
     * ??????????????????????????????
     * ????????????????????????-??????-?????????
     */
    public boolean checkChangeCaptain() {
        Logger.e("=======================" + GsonUtil.obj2Json(mJoinedPlayerIds));
        // ???????????????????????????????????????,????????????????????????
        if (isCaptain(mUserId)) {
            String newCaptainId = "";
            // ????????????????????????????????????????????????
            if (mJoinedPlayerIds.contains(mGameRoomBean.getCreateUserId())) {
                newCaptainId = mGameRoomBean.getCreateUserId();
            } else {
                // ?????????????????????????????????
                for (String playerId : mJoinedPlayerIds) {
                    if (MemberCache.getInstance().isAdmin(playerId)) {
                        newCaptainId = playerId;
                        break;
                    }
                }
                // ???????????????????????????????????????
                if (TextUtils.isEmpty(newCaptainId) && mJoinedPlayerIds.size() > 0) {
                    newCaptainId = mJoinedPlayerIds.get(0);
                }
            }
            // ???????????????id??????????????????????????????????????????
            if (!TextUtils.isEmpty(newCaptainId) && !TextUtils.equals(mCaptainId, newCaptainId)) {
                RCGameEngine.getInstance().setCaptain(newCaptainId, null);
                return true;
            }
        }
        return false;
    }


    /**
     * ????????????????????????????????????
     */
    private void sendSystemMessage() {
        if (mGameRoomBean != null) {
            if (mGameRoomListener != null) mGameRoomListener.showMessage(null, true);

            // ????????????
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("???????????? %s", mGameRoomBean.getRoomName()));
            if (mGameRoomListener != null) mGameRoomListener.showMessage(welcome, false);

            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("?????????????????? RTC ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            if (mGameRoomListener != null) mGameRoomListener.showMessage(tips, false);
            Logger.d("=================?????????????????????");
            // ????????????
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(UserManager.get().getUserId());
            enter.setUserName(UserManager.get().getUserName());
            sendMessage(enter);
        }
    }

    public void sendMessage(MessageContent messageContent) {
        //?????????????????????????????????
        boolean isContains = false;
        if (mShields != null) {
            if (messageContent instanceof RCChatroomBarrage) {
                for (Shield shield : mShields) {
                    if (((RCChatroomBarrage) messageContent).getContent().contains(shield.getName())) {
                        isContains = true;
                        break;
                    }
                }
            }
            if (isContains) {
                //????????????????????????????????????????????????????????????????????????????????????
                Message message = new Message();
                message.setConversationType(Conversation.ConversationType.CHATROOM);
                message.setContent(messageContent);
                onMessageReceived(message);
                return;
            }
        }
        RongCoreClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, mRoomId, messageContent, "", "", new IRongCoreCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
            }

            @Override
            public void onSuccess(Message message) {
                onMessageReceived(message);
                Logger.d("=============sendChatRoomMessage:success");
            }

            @Override
            public void onError(Message message, IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (messageContent instanceof RCChatroomBarrage || messageContent instanceof RCChatroomVoice) {
                    ToastUtils.s(UIKit.getContext(), "????????????");
                }
                Logger.e("=============" + coreErrorCode.code + ":" + coreErrorCode.msg);
            }
        });
        if (messageContent instanceof RCChatroomBarrage) {
            // if (TextUtils.equals(mKeyword, ((RCChatroomBarrage) messageContent).getContent())) {
            RCGameEngine.getInstance().hitKeyword(((RCChatroomBarrage) messageContent).getContent(), null);
            // }
        }
    }

    public RoomOwnerType getRoomOwnerType() {
        return mRoomOwnerType;
    }

    /**
     * ????????????
     */
    public void leaveRoom() {
        showLoading("");
        exitGame();
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                changeUserRoom("");
                dismissLoading();
                if (mGameRoomListener != null) {
                    mGameRoomListener.finishRoom();
                    unInit();
                }
            }

            @Override
            public void onError(int i, String s) {
                dismissLoading();
                KToast.show(s);
            }
        });
    }

    // ???????????????????????????????????????????????????????????????
    private void exitGame() {
        if (mPlayingPlayerIds.contains(mUserId)) {
            RCGameEngine.getInstance().cancelPlayGame(null);
        }
        if (mReadyPlayerIds.contains(mUserId)) {
            RCGameEngine.getInstance().cancelReadyGame(null);
        }
        if (mJoinedPlayerIds.contains(mUserId)) {
            RCGameEngine.getInstance().cancelJoinGame(null);
        }
    }

    /**
     * ??????????????????
     */
    public void closeRoom() {
        showLoading("??????????????????");
        RCGameEngine.getInstance().endGame(null);
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                changeUserRoom("");
                deleteRoom();
            }

            @Override
            public void onError(int i, String s) {
                dismissLoading();
                KToast.show(s);
                deleteRoom();
            }
        });
    }

    private void deleteRoom() {
        //?????????????????????????????????????????????
        OkApi.get(VRApi.deleteRoom(mGameRoomBean.getRoomId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    dismissLoading();
                    if (mGameRoomListener != null) {
                        mGameRoomListener.finishRoom();
                        unInit();
                    }
                } else {
                    dismissLoading();
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                dismissLoading();
                KToast.show(msg);
            }
        });
    }

    private void refreshRoomMember() {
        MemberCache.getInstance().refreshMemberData(mRoomId);
    }

    private void showLoading(String msg) {
        if (mGameRoomListener != null) mGameRoomListener.showLoading(msg);
    }

    private void dismissLoading() {
        if (mGameRoomListener != null) mGameRoomListener.dismissLoading();
    }

    public void responseInviteEnterSeat(boolean accept, String userId) {
        if (accept) {
            //??????
            // notifyRoom(GameConstant.EVENT_AGREE_MANAGE_PICK, userId);
            readyEnterSeat();
            RCVoiceRoomEngine.getInstance().acceptInvitation(userId, null);
        } else {
            // notifyRoom(GameConstant.EVENT_REJECT_MANAGE_PICK, userId);
            RCVoiceRoomEngine.getInstance().rejectInvitation(userId, null);
        }

    }

    public void muteSeat(int index, boolean isMute) {
        RCVoiceRoomEngine.getInstance().muteSeat(index, isMute, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void changeRecord() {
        boolean disable = RCVoiceRoomEngine.getInstance().isDisableAudioRecording();
        RCVoiceRoomEngine.getInstance().disableAudioRecording(!disable);
        KToast.show(disable ? "??????????????????" : "??????????????????");
        if (mGameRoomListener != null) {
            mGameRoomListener.onMicSwitched(disable);
        }
        if (!disable) {
            UIKit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyRoom(GameConstant.EVENT_CLOSE_MIC, mUserId);
                }
            }, 1000);
        }
    }

    /**
     * ?????????????????????
     */
    public void leaveSeat() {
        RCVoiceRoomEngine.getInstance().leaveSeat(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                exitGame();
                // RCGameEngine.getInstance().endGame(null);
                KToast.show("????????????");
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void responseInviteJoinGame(boolean accept) {
        if (accept) {
            RCGameEngine.getInstance().joinGame(null);
        }
    }

    public void notifyRoom(String name, String content) {
        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(name, content, null);
    }

    /**
     * ????????????????????????
     */
    public void uploadGameStatus() {
        // ??????????????????????????????????????????
        if (!isCaptain(mUserId)) {
            return;
        }
        int gameStatus = 0;
        if (mGameState == RCGameState.IDLE) {
            gameStatus = 1;
        } else if (mGameState == RCGameState.PLAYING) {
            gameStatus = 2;
        }
        if (gameStatus == 0) {
            return;
        }
        OkApi.post(GameApi.GAME_STATE, OkParams.Builder().add("roomId", mRoomId).add("gameStatus", gameStatus).build(), new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {

            }
        });
    }

    @Override
    public void onGameLoadingProgress(RCGameLoadingStage rcGameLoadingStage, int i, int i1) {

    }

    @Override
    public void onGameLoaded() {
        // ??????????????????????????????????????????????????????????????????????????????
        if ((isCreate && isFirstLoadedGame) || (!isCreate && isFastIn && isFirstLoadedGame)) {
            isFirstLoadedGame = false;
            RCGameEngine.getInstance().joinGame(null);
            RCGameEngine.getInstance().readyGame(null);
        }
    }

    @Override
    public void onGameDestroyed() {
        stopVoiceRecorder();
    }

    @Override
    public void onReceivePublicMessage(String s) {
        Message message = new Message();
        message.setContent(TextMessage.obtain(s));
        message.setConversationType(Conversation.ConversationType.CHATROOM);
        onMessageReceived(message);
    }

    @Override
    public void onKeywordToHit(String s) {
        mKeyword = s;
    }

    @Override
    public void onGameStateChanged(RCGameState rcGameState) {
        mGameState = rcGameState;
        uploadGameStatus();
        // ?????????????????????
        if (mGameState == RCGameState.PLAYING && mGameRoomListener != null) {
            mGameRoomListener.onGameStarted();
        }
        if (mGameState == RCGameState.IDLE) {
            mKeyword = "";
            checkChangeCaptain();
        }
    }

    @Override
    public void onGameASRChanged(boolean isOpen) {
        if (isOpen) {
            startVoiceRecorder();
        } else {
            stopVoiceRecorder();
        }
    }

    private void startVoiceRecorder() {
        // ?????????????????????
        RCRTCEngine.getInstance().getDefaultAudioStream().setRecordAudioDataListener(dataListener);
    }

    private void stopVoiceRecorder() {
        // ?????????????????????
        if (RCRTCEngine.getInstance().getDefaultAudioStream() != null) {
            RCRTCEngine.getInstance().getDefaultAudioStream().setRecordAudioDataListener(null);
        }
    }

    private IRCRTCAudioDataListener dataListener = new IRCRTCAudioDataListener() {
        // ?????????10ms????????????????????????????????????????????????????????????????????????????????????pushAudio
        @Override
        public byte[] onAudioFrame(RCRTCAudioFrame rcRTCAudioFrame) {
            // ???????????????16K????????????????????????????????????????????????????????????
            ByteBuffer input = ByteBuffer.wrap(rcRTCAudioFrame.getBytes());
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * ???????????????????????????????????????PCM?????????????????????16000??? ???????????????16??? ???????????? MONO
                     * 100ms??????????????????????????????????????????????????????????????????10ms, 20ms, 50ms, 100ms
                     * dataLength?????????????????????????????????????????????????????????
                     */
                    RCGameEngine.getInstance().pushAudio(input, input.limit());
                    // Log.e("===============", input.limit() + " " + rcRTCAudioFrame.getChannels() + " " + rcRTCAudioFrame.getSampleRate());
                }
            });
            return rcRTCAudioFrame.getBytes();
        }
    };

    @Override
    public void onMicrophoneChanged(boolean b) {

    }

    @Override
    public void onExpireCode() {
        // ????????????????????????????????????code?????????
        login(new LoginCallback() {
            @Override
            public void onSuccess(String code) {
                // ??????appCode
                RCGameEngine.getInstance().updateAppCode(code);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onGameSettle(RCGameSettle rcGameSettle) {
        Logger.d("==============onGameSettle " + GsonUtil.obj2Json(rcGameSettle));
    }

    @Override
    public void onPlayerIn(String uid, boolean isIn, int i) {
        if (isIn) {
            if (!mJoinedPlayerIds.contains(uid)) {
                mJoinedPlayerIds.add(uid);
            }
        } else {
            // ????????????????????????????????????????????????????????????????????????????????????playerIn???????????????????????????????????????
            mJoinedPlayerIds.remove(uid);
            mReadyPlayerIds.remove(uid);
            mPlayingPlayerIds.remove(uid);
        }
        // ?????????????????????????????????????????????????????????
        if (isIn && TextUtils.equals(uid, mUserId)) {
            readyEnterSeat();
        }
        // ??????????????????????????????????????????????????????
        checkChangeCaptain();
        // ???????????????????????????
        if (!isIn && TextUtils.equals(uid, mCaptainId)) {
            mCaptainId = "";
        }
        refreshSeatPlayers();
    }

    @Override
    public void onPlayerCaptain(String uid, boolean isCaptain) {
        String newCaptainId = "";
        if (isCaptain) {
            newCaptainId = uid;
        } else if (TextUtils.equals(uid, mCaptainId)) {
            newCaptainId = "";
        } else {
            newCaptainId = mCaptainId;
        }
        // ???????????????????????????
        if (!TextUtils.equals(newCaptainId, mCaptainId)) {
            mCaptainId = newCaptainId;
            refreshSeatPlayers();
        }
    }

    @Override
    public void onPlayerReady(String uid, boolean isReady) {
        if (isReady) {
            if (!mReadyPlayerIds.contains(uid)) {
                mReadyPlayerIds.add(uid);
            }
        } else {
            mReadyPlayerIds.remove(uid);
        }
        refreshSeatPlayers();
    }

    @Override
    public void onPlayerPlaying(String uid, boolean isPlaying) {
        if (isPlaying) {
            if (!mPlayingPlayerIds.contains(uid)) {
                mPlayingPlayerIds.add(uid);
            }
        } else {
            mPlayingPlayerIds.remove(uid);
        }
        refreshSeatPlayers();
    }

    @Override
    public void onPlayerChangeSeat(String s, int i, int i1) {

    }

    @Override
    public void onPlayerDieStatus(String s, boolean b) {

    }

    @Override
    public void onPlayerTurnStatus(String s, boolean b) {

    }

    @Override
    public void onRoomKVReady() {
    }

    @Override
    public void onRoomDestroy() {
        if (mGameRoomListener != null) mGameRoomListener.onRoomClosed();
    }

    @Override
    public void onRoomInfoUpdate(RCVoiceRoomInfo rcVoiceRoomInfo) {
        mRcVoiceRoomInfo = rcVoiceRoomInfo;
        if (mGameRoomListener != null) {
            mGameRoomListener.setRoomName(mRcVoiceRoomInfo.getRoomName());
        }
    }

    @Override
    public void onSeatInfoUpdate(List<RCVoiceSeatInfo> list) {
        mSeatInfos = list;
        refreshSeatPlayers();
    }

    @Override
    public void onUserEnterSeat(int i, String s) {
        if (TextUtils.equals(s, mUserId)) {
            isInSeat = true;
            if (mGameRoomListener != null)
                mGameRoomListener.onInSeatChanged(true);
        }
    }

    @Override
    public void onUserLeaveSeat(int i, String s) {
        if (TextUtils.equals(s, mUserId)) {
            isInSeat = false;
            if (mGameRoomListener != null)
                mGameRoomListener.onInSeatChanged(false);
        }
    }

    @Override
    public void onSeatMute(int i, boolean b) {

    }

    @Override
    public void onSeatLock(int i, boolean b) {

    }

    @Override
    public void onAudienceEnter(String s) {
        refreshRoomMember();
    }

    @Override
    public void onAudienceExit(String s) {
        UIKit.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshRoomMember();
            }
        }, 1000);
    }

    @Override
    public void onSpeakingStateChanged(int i, int i1) {
        // Logger.e("============" + i + " " + i1 + " " + mSpeakingArray);
        boolean isSpeaking = i1 > 0;
        if (mSpeakingArray.get(i) != isSpeaking) {
            mSpeakingArray.put(i, isSpeaking);
            if (mGameRoomListener != null) {
                mGameRoomListener.onSpeakingChanged(i, isSpeaking);
            }
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof CommandMessage) {
            boolean show = !TextUtils.isEmpty(((CommandMessage) messageContent).getData());
            refreshMusicView(show);
        } else if (messageContent instanceof RCAllBroadcastMessage) {
            AllBroadcastManager.getInstance().addMessage((RCAllBroadcastMessage) messageContent);
        } else if (messageContent instanceof RCChatroomSeats) {
            refreshRoomMember();
        } else if (messageContent instanceof RCChatroomAdmin) {
            // addMessage(messageContent);
            MemberCache.getInstance().refreshAdminData(mRoomId);
        } else if (messageContent instanceof RCChatroomBarrage) {
            // ?????????????????????????????????null?????????????????????????????????????????????????????????***
            if (!TextUtils.isEmpty(mKeyword) && !TextUtils.equals(((RCChatroomBarrage) messageContent).getUserId(), mUserId)
                    && TextUtils.equals(mKeyword, ((RCChatroomBarrage) messageContent).getContent())) {
                ((RCChatroomBarrage) messageContent).setContent(mKeyword.replaceAll(".", "*"));
            }
        }

        if (isShowingMessage(messageContent)) {
            if (mGameRoomListener != null) mGameRoomListener.showMessage(messageContent, false);
        }
    }

    /**
     * ???????????????????????????????????????
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

    @Override
    public void onRoomNotificationReceived(String name, String content) {
        if (TextUtils.equals(name, GameConstant.EVENT_AGREE_MANAGE_PICK)) {
            if (TextUtils.equals(content, mUserId)) {
                KToast.show("??????????????????");
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_REJECT_MANAGE_PICK)) {
            if (TextUtils.equals(content, mUserId)) {
                KToast.show("??????????????????");
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_INVITED_JOIN_GAME)) {
            // ?????????????????????????????????
            if (TextUtils.equals(content, mUserId)) {
                if (mGameRoomListener != null) mGameRoomListener.showInviteJoinGameDialog();
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_SWITCH_GAME)) {
            RCGameInfo gameInfo = GsonUtil.json2Obj(content, RCGameInfo.class);
            if (gameInfo != null) {
                switchGame(gameInfo, false);
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_ADD_SHIELD)) {
            Shield shield = new Shield();
            shield.setName(content);
            mShields.add(shield);
        } else if (TextUtils.equals(name, GameConstant.EVENT_DELETE_SHIELD)) {
            Iterator<Shield> iterator = mShields.iterator();
            Shield s;
            while (iterator.hasNext()) {
                s = iterator.next();
                if (s.getName().equals(content)) {
                    iterator.remove();
                }
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_CLOSE_MIC)) {
            for (SeatPlayer player : mSeatPlayers) {
                if (TextUtils.equals(content, player.userId)) {
                    onSpeakingStateChanged(player.seatIndex, 0);
                    break;
                }
            }
        }
    }

    @Override
    public void onPickSeatReceivedFrom(String userId) {
        if (mGameRoomListener != null) {
            // ?????????????????????
            boolean isCrete = TextUtils.equals(userId, mGameRoomBean.getCreateUserId());
            mGameRoomListener.showPickReceivedDialog(isCrete, userId);
        }
    }

    @Override
    public void onKickSeatReceived(int i) {
        KToast.show("?????????????????????");
        exitGame();
    }

    @Override
    public void onRequestSeatAccepted() {

    }

    @Override
    public void onRequestSeatRejected() {

    }

    @Override
    public void onRequestSeatListChanged() {

    }

    @Override
    public void onInvitationReceived(String invitationId, String sendUserId, String content) {
    }

    @Override
    public void onInvitationAccepted(String s) {
        if (TextUtils.equals(s, mUserId)) {
            KToast.show("??????????????????");
        }
    }

    @Override
    public void onInvitationRejected(String s) {
        if (TextUtils.equals(s, mUserId)) {
            KToast.show("??????????????????");
        }
    }

    @Override
    public void onInvitationCancelled(String s) {

    }

    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        if (TextUtils.equals(targetId, mUserId)) {
            KToast.show("?????????????????????");
            leaveRoom();
        }
    }

    @Override
    public void onNetworkStatus(int i) {
        if (mGameRoomListener != null) {
            mGameRoomListener.onNetworkStatus(i, isInSeat);
        }
    }

    @Override
    public void onPKGoing(RCPKInfo rcpkInfo) {

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

    private interface LoginCallback {
        void onSuccess(String code);

        void onError();
    }


    @Override
    public void clickSettingAdmin(User user, ClickCallback<Boolean> callback) {
        if (mGameRoomBean == null) {
            return;
        }
        boolean isAdmin = !MemberCache.getInstance().isAdmin(user.getUserId());
        HashMap<String, Object> params = new OkParams()
                .add("roomId", mGameRoomBean.getRoomId())
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
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().kickUserFromRoom(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //????????????????????????????????????????????????????????????
                RCChatroomKickOut kickOut = new RCChatroomKickOut();
                kickOut.setUserId(UserManager.get().getUserId());
                kickOut.setUserName(UserManager.get().getUserName());
                kickOut.setTargetId(user.getUserId());
                kickOut.setTargetName(user.getUserName());
                RCChatRoomMessageManager.sendChatMessage(mRoomId, kickOut, true, null, null);
                callback.onResult(true, "????????????");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void clickSendGift(User user) {
        String userId = user.getUserId();
        Member member = Member.fromUser(user);
        if (!TextUtils.isEmpty(userId)) {
            Logger.d(TAG, "clickSendGift: userId = " + userId);
            for (SeatPlayer mSeatPlayer : mSeatPlayers) {
                if (TextUtils.equals(mSeatPlayer.userId, userId)) {
                    member.setSeatIndex(mSeatPlayer.seatIndex);
                    break;
                }
            }
        }
        if (mGameRoomListener != null)
            mGameRoomListener.showSendGiftDialog(mGameRoomBean, user.getUserId(), Arrays.asList(member));
    }


    @Override
    public void onSendGiftSuccess(List<MessageContent> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (MessageContent message : messages) {
                sendMessage(message);
            }
        }
    }


    @Override
    public void clickFollow(boolean isFollow, RCFollowMsg followMsg) {
        if (isFollow) {
            sendMessage(followMsg);
        }
        if (mGameRoomListener != null)
            mGameRoomListener.setTitleFollow(isFollow);
    }

    @Override
    public void clickInviteSeat(int seatIndex, User user, ClickCallback<Boolean> callback) {
        if (isInSeat(user.getUserId())) {
            KToast.show("?????????????????????");
            return;
        }
        if (getAvailableSeatIndex() < 0) {
            callback.onResult(false, "????????????");
            return;
        }
        RCVoiceRoomEngine.getInstance().pickUserToSeat(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //????????????,?????????????????????
                callback.onResult(true, "????????????");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    public void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {

    }

    @Override
    public void cancelInvitation(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    public void clickKickSeat(User user, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().kickUserFromSeat(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                callback.onResult(true, "");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void clickMuteSeat(int seatIndex, boolean isMute, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance()
                .muteSeat(seatIndex, isMute, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //??????????????????
                        callback.onResult(true, "");
                        if (isMute) {
                            KToast.show("??????????????????");
                        } else {
                            KToast.show("???????????????");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        //??????????????????
                        callback.onResult(false, s);
                    }
                });
    }

    @Override
    public void clickCloseSeat(int seatIndex, boolean isClose, ClickCallback<
            Boolean> callback) {

        RCVoiceRoomEngine.getInstance().lockSeat(seatIndex, isClose, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //???????????????
                callback.onResult(true, isClose ? "???????????????" : "???????????????");
            }

            @Override
            public void onError(int i, String s) {
                //???????????????
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {

    }

    @Override
    public void switchSelfEnterSeat(Member member, int seatIndex, ClickCallback<
            Boolean> callback) {
        // ????????????,?????????????????????????????????????????????????????????
        showLoading("");
        RCVoiceRoomEngine.getInstance().kickUserFromSeat(member.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                enterSeat(seatIndex, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        if (callback != null)
                            callback.onResult(true, "");
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (callback != null)
                            callback.onResult(false, s);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                dismissLoading();
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void clickInvitedGame(User user, ClickCallback<Boolean> callback) {
        if (isInGame(user.getUserId())) {
            KToast.show("???????????????????????????");
            return;
        }
        if (mGameState == RCGameState.PLAYING) {
            KToast.show("???????????????????????????");
            return;
        }
        if (getAvailableSeatIndex() < 0 && !isInGame(user.getUserId())) {
            callback.onResult(false, "????????????");
            return;
        }
        notifyRoom(GameConstant.EVENT_INVITED_JOIN_GAME, user.getUserId());
        callback.onResult(true, "");
    }

    @Override
    public void clickKickGame(User user, ClickCallback<Boolean> callback) {
        if (mGameState == RCGameState.PLAYING) {
            KToast.show("?????????????????????");
            return;
        }
        RCGameEngine.getInstance().kickPlayer(user.getUserId(), null);
        callback.onResult(true, "");
    }
}
