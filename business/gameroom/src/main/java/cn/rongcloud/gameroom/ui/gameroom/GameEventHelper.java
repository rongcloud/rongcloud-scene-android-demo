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
    // 游戏玩家座位信息
    private List<SeatPlayer> mSeatPlayers;
    // 加入游戏的玩家id
    private List<String> mJoinedPlayerIds;
    // 准备游戏的玩家id
    private List<String> mReadyPlayerIds;
    // 游戏中的玩家id
    private List<String> mPlayingPlayerIds;
    // 麦位信息
    private List<RCVoiceSeatInfo> mSeatInfos;
    // 队长的uid
    private volatile String mCaptainId;
    // 说话的状态
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
     * 初始化注册监听
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
        // 游戏状态监听
        RCGameEngine.getInstance().setGameStateListener(this);
        // 游戏玩家监听
        RCGameEngine.getInstance().setGamePlayerStateListener(this);
        // 拉取房间信息
        getRoomInfo();
        // 获取房间屏蔽词
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
     * 获取房间信息
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
     * 避免中途未调用离开房间再加入时加入失败
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
     * 根据id获取用户信息
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
     * 创建或加入房间
     */
    private void createOrJoinRoom() {
        // 房间事件监听,放在这里是因为之前调用了leaveRoom会把事件置空
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(this);
        // 这里为了支持游戏内语音识别，需设置固定采样率16K，单声道
        // 如果想采用高采样率，可以自己在数据回调中对数据重采样为16K单声道
        // RCGameEngine.getInstance().pushAudio(); 这个方法只支持16K单声道的PCM数据
        RCRTCConfig rcrtcConfig = RCRTCConfig.Builder.create()
                // 设置16K采样率
                .setAudioSampleRate(16000)
                // 关闭立体声
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
                    // 刷新房间成员信息
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
                    // 刷新房间成员信息
                    MemberCache.getInstance().fetchData(mRoomId);
                    VMLog.d(TAG, "==============加入房间成功");
                }

                @Override
                public void onError(int i, String s) {
                    VMLog.e(TAG, "==============加入房间失败 code：" + i + " msg: " + s);
                }
            });
        }
    }

    // 登录游戏服务器->加载游戏
    private void initAndLoadGame() {
        // 请求开发者服务器登录用户，获取appCode
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
     * 找出上哪个麦位
     */
    private void readyEnterSeat() {
        boolean isInSeat = false;
        int seatIndex = -1;
        RCVoiceSeatInfo seatInfo;
        for (int i = 0; i < mSeatInfos.size(); i++) {
            seatInfo = mSeatInfos.get(i);
            // 判断自己是否在麦位上
            if (TextUtils.equals(seatInfo.getUserId(), mUserId)) {
                isInSeat = true;
                // 找到第一个空麦位
            } else if (seatInfo.getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
                if (seatIndex == -1) {
                    seatIndex = i;
                }
            }
        }
        // 已经在麦位上不再上麦
        if (isInSeat) {
            return;
        }
        if (seatIndex == -1) {
            KToast.show("当前麦位不足，无法加入游戏");
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
                KToast.show("上麦成功");
                dismissLoading();
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                KToast.show("上麦失败：" + s);
                dismissLoading();
                if (callback != null)
                    callback.onError(i, s);
            }
        });
    }

    // 语聊房SDK内部AudioScenario采用的MUSIC_CHATROOM，这里改成DEFAULT可以屏蔽掉游戏的背景音乐被播放出去
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
     * 不同角色点击麦位
     *
     * @param seatPlayer
     */
    public void clickSeat(SeatPlayer seatPlayer) {
        // 空麦位的情况
        if (seatPlayer.playerState == SeatPlayer.PlayerState.EMPTY) {
            // 自己不在麦位点击空麦位
            if (!isInSeat(mUserId)) {
                // 麦位锁定了不能上
                if (seatPlayer.isLock) {
                    // 如果是房主，弹出麦位操作弹框
                    if (mRoomOwnerType == RoomOwnerType.GAME_OWNER) {
                        if (mGameRoomListener != null) {
                            mGameRoomListener.showEmptySeatDialog(seatPlayer);
                        }
                    } else {
                        KToast.show("该座位已锁定");
                    }
                } else {
                    enterSeat(seatPlayer.seatIndex, null);
                }
            } else {// 自己在麦位上
                // 房主点空麦位，弹麦位操作弹框
                if (mRoomOwnerType == RoomOwnerType.GAME_OWNER) {
                    if (mGameRoomListener != null) {
                        mGameRoomListener.showEmptySeatDialog(seatPlayer);
                    }
                } else if (isCaptain(mUserId)) {
                    //自己是队长，弹邀请游戏的列表的弹框

                }
                // 其他角色点击空麦位无反应
            }
        } else {// 非空麦位
            // 点击自己,弹出对自己操作的弹框
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
     * 送礼物
     */
    public void sendGift() {
        ArrayList<Member> memberArrayList = new ArrayList<>();
        // 房主放在第一个
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
        // 房主放在第一个，先拿出来
        Member creator = memberArrayList.remove(0);
        Comparator<Member> comparator = new Comparator<Member>() {
            @Override
            public int compare(Member o1, Member o2) {
                return o1.getSeatIndex() - o2.getSeatIndex();
            }
        };
        // 其他人按照麦位号排序
        Collections.sort(memberArrayList, comparator);
        memberArrayList.add(0, creator);

        Logger.d(TAG, "memberArrayList:" + GsonUtil.obj2Json(memberArrayList));
        if (mGameRoomListener != null) {
            mGameRoomListener.showSendGiftDialog(mGameRoomBean, mGameRoomBean.getCreateUserId(), memberArrayList);
        }
    }

    /**
     * 修改房间公告
     *
     * @param notice
     */
    public void modifyNotice(String notice) {
        //判断公告是否有显示
        mRcVoiceRoomInfo.setExtra(notice);
        RCVoiceRoomEngine.getInstance().setRoomInfo(mRcVoiceRoomInfo, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                TextMessage tips = TextMessage.obtain("房间公告已更新!");
                sendMessage(tips);
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
                        .add("roomId", mRoomId)
                        .add("isPrivate", p)
                        .add("password", password).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            KToast.show(isPrivate ? "设置成功" : "取消成功");
                            mGameRoomBean.setIsPrivate(isPrivate ? 1 : 0);
                            mGameRoomBean.setPassword(password);
                            IFun.BaseFun fun = item.getValue();
                            fun.setStatus(p);
                            item.setValue(fun);
                        } else {
                            KToast.show(isPrivate ? "设置失败" : "取消失败");
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
                        .add("roomId", mRoomId)
                        .add("name", name)
                        .build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            KToast.show("修改成功");
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
                            KToast.show(!TextUtils.isEmpty(message) ? message : "修改失败");
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        KToast.show(!TextUtils.isEmpty(msg) ? msg : "修改失败");
                    }
                });
    }

    /**
     * 获取可用麦位索引
     *
     * @return 可用麦位索引
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
            return String.format("欢迎来到 %s", mGameRoomBean.getRoomName());
        }
    }

    public String getRoomName() {
        if (mGameRoomBean != null) {
            return mGameRoomBean.getRoomName();
        }
        return "";
    }

    /**
     * 房主和管理能主动切换游戏
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
        KToast.show("当前游戏已切换");
        if (mGameRoomListener != null) {
            mGameRoomListener.onGameChanged(gameInfo);
        }
        // 新麦位数
        int newSeatCount = Math.max(MIN_SEAT_COUNT, mGameInfo.getMaxSeat());
        // 自己是否在麦位上
        boolean isSelfInSeat = isInSeat(mUserId);
        // 是否要改变麦位数量
        boolean isChangeSeatCount = newSeatCount != mSeatCount;
        if (isSelfInSeat) {
            if (isRoomOwner() && !isChangeSeatCount) {
                // 房主，不需要改变麦位数量的情况下不下麦，不做操作
            } else {
                leaveSeat();
            }
        }
        // 负责切换游戏的人发出通知去切换游戏，并且改变麦位数量等
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
     * 更改座位数量
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
                // 房主之前在麦位上要自动上麦
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
     * 登录开发者服务器获取code
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

    //更改所属房间
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
     * 获取屏蔽词
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
     * 全麦锁麦
     *
     * @param isMuteAll
     */
    public void setAllSeatLock(boolean isMuteAll) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(isMuteAll, null);
        if (isMuteAll) {
            KToast.show("全部麦位已静音");
        } else {
            KToast.show("已解锁全麦");
        }
    }

    /**
     * 全麦锁座
     */
    public void lockOtherSeats(boolean isLockAll) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(isLockAll, null);
        if (isLockAll) {
            KToast.show("全部座位已锁定");
        } else {
            KToast.show("已解锁全座");
        }
    }


    /**
     * 静音 取消静音
     *
     * @param isMute
     */
    public void muteAllRemoteStreams(boolean isMute) {
        RCVoiceRoomEngine.getInstance().muteAllRemoteStreams(isMute);
        isMuteAll = isMute;
        if (isMute) {
            KToast.show("扬声器已静音");
        } else {
            KToast.show("已取消静音");
        }
    }

    /**
     * 刷新音乐播放的小窗UI
     *
     * @param show 是否显示
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
     * 刷新座位信息
     * 座位排序：玩家（队长-普通玩家）非玩家（房主-管理-观众）
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
            // 排序
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
            // 空座位
            if (seatPlayer.playerState == SeatPlayer.PlayerState.EMPTY) {
                emptyGameSeat.add(seatPlayer);
            } else {// 非空座位
                // 玩家座位
                if (mJoinedPlayerIds.contains(seatPlayer.userId)) {
                    inGameSeat.add(seatPlayer);
                } else {//非玩家座位
                    outGameSeat.add(seatPlayer);
                }
            }
        }

        Comparator<SeatPlayer> inSeatComparator = new Comparator<SeatPlayer>() {
            @Override
            public int compare(SeatPlayer o1, SeatPlayer o2) {
                // 队长在前
                if (o1.isCaptain != o2.isCaptain) {
                    return o1.isCaptain ? -1 : 1;
                }
                // 管理在前`
                if (o1.isAdmin != o2.isAdmin) {
                    return o1.isAdmin ? -1 : 1;
                }
                return 0;
            }
        };

        Comparator<SeatPlayer> outSeatComparator = new Comparator<SeatPlayer>() {
            @Override
            public int compare(SeatPlayer o1, SeatPlayer o2) {
                // 房主在前
                if (isRoomOwner(o1.userId) != isRoomOwner(o2.userId)) {
                    return isRoomOwner(o1.userId) ? -1 : 1;
                }
                // 管理在前`
                if (o1.isAdmin != o2.isAdmin) {
                    return o1.isAdmin ? -1 : 1;
                }
                return 0;
            }
        };

        // Comparator<SeatPlayer> emptySeatComparator = new Comparator<SeatPlayer>() {
        //     @Override
        //     public int compare(SeatPlayer o1, SeatPlayer o2) {
        //         // 非锁定的座位在前
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
     * 检查是否需要更换队长
     * 队长转移：（房主-管理-玩家）
     */
    public boolean checkChangeCaptain() {
        Logger.e("=======================" + GsonUtil.obj2Json(mJoinedPlayerIds));
        // 谁目前是队长谁负责转移队长,如果需要转移的话
        if (isCaptain(mUserId)) {
            String newCaptainId = "";
            // 加入游戏的有房主则队长应该是房主
            if (mJoinedPlayerIds.contains(mGameRoomBean.getCreateUserId())) {
                newCaptainId = mGameRoomBean.getCreateUserId();
            } else {
                // 房主不在找出第一个管理
                for (String playerId : mJoinedPlayerIds) {
                    if (MemberCache.getInstance().isAdmin(playerId)) {
                        newCaptainId = playerId;
                        break;
                    }
                }
                // 不存在管理，取第一个为队长
                if (TextUtils.isEmpty(newCaptainId) && mJoinedPlayerIds.size() > 0) {
                    newCaptainId = mJoinedPlayerIds.get(0);
                }
            }
            // 找出的队长id和当前的不一致则需要更换队长
            if (!TextUtils.isEmpty(newCaptainId) && !TextUtils.equals(mCaptainId, newCaptainId)) {
                RCGameEngine.getInstance().setCaptain(newCaptainId, null);
                return true;
            }
        }
        return false;
    }


    /**
     * 进入房间后发送默认的消息
     */
    private void sendSystemMessage() {
        if (mGameRoomBean != null) {
            if (mGameRoomListener != null) mGameRoomListener.showMessage(null, true);

            // 默认消息
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("欢迎来到 %s", mGameRoomBean.getRoomName()));
            if (mGameRoomListener != null) mGameRoomListener.showMessage(welcome, false);

            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("感谢使用融云 RTC 游戏房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。");
            if (mGameRoomListener != null) mGameRoomListener.showMessage(tips, false);
            Logger.d("=================发送了默认消息");
            // 广播消息
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(UserManager.get().getUserId());
            enter.setUserName(UserManager.get().getUserName());
            sendMessage(enter);
        }
    }

    public void sendMessage(MessageContent messageContent) {
        //先判断是否包含了屏蔽词
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
                //如果是包含了敏感词，只发送到自己的公屏上，不真正的发出去
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
                    ToastUtils.s(UIKit.getContext(), "发送失败");
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
     * 离开房间
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

    // 退出加入游戏，为了实现跨状态退出需依次执行
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
     * 房主关闭房间
     */
    public void closeRoom() {
        showLoading("正在关闭房间");
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
        //房主关闭房间，调用删除房间接口
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
            //同意
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
        KToast.show(disable ? "已开启麦克风" : "已关闭麦克风");
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
     * 下麦必须下游戏
     */
    public void leaveSeat() {
        RCVoiceRoomEngine.getInstance().leaveSeat(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                exitGame();
                // RCGameEngine.getInstance().endGame(null);
                KToast.show("下麦成功");
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
     * 上传游戏开始状态
     */
    public void uploadGameStatus() {
        // 队长上传，其他人就不用上传了
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
        // 创建者第一次创建房间并加载游戏完成后，自动上麦并准备
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
        // 游戏开始后回调
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
        // 设置数据流监听
        RCRTCEngine.getInstance().getDefaultAudioStream().setRecordAudioDataListener(dataListener);
    }

    private void stopVoiceRecorder() {
        // 移除数据流监听
        if (RCRTCEngine.getInstance().getDefaultAudioStream() != null) {
            RCRTCEngine.getInstance().getDefaultAudioStream().setRecordAudioDataListener(null);
        }
    }

    private IRCRTCAudioDataListener dataListener = new IRCRTCAudioDataListener() {
        // 这里每10ms回调一次，如果想一次性发送更多数据，可自己合并数据再调用pushAudio
        @Override
        public byte[] onAudioFrame(RCRTCAudioFrame rcRTCAudioFrame) {
            // 如果没设置16K采样率单声道，可以在这里拦截数据做重采样
            ByteBuffer input = ByteBuffer.wrap(rcRTCAudioFrame.getBytes());
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * 传入的音频切片数据必须是：PCM格式，采样率：16000， 采样位数：16， 声道数： MONO
                     * 100ms必须是音频切片长度的整数倍。切片长度可以是：10ms, 20ms, 50ms, 100ms
                     * dataLength一定要是有效数据长度，否则精确性有影响
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
        // 过期后需要调登录重新获取code并更新
        login(new LoginCallback() {
            @Override
            public void onSuccess(String code) {
                // 更新appCode
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
            // 当踢人时，碰碰会把玩家所有状态依次回调，但其他游戏只回调playerIn，所以在这里移除下其他状态
            mJoinedPlayerIds.remove(uid);
            mReadyPlayerIds.remove(uid);
            mPlayingPlayerIds.remove(uid);
        }
        // 如果是加入游戏，且是自己则进行上麦操作
        if (isIn && TextUtils.equals(uid, mUserId)) {
            readyEnterSeat();
        }
        // 有人加入或退出都要检测是否要转移队长
        checkChangeCaptain();
        // 如果是队长离开游戏
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
        // 队长改变再调用刷新
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
            // 如果在答题，关键词不为null，且不是自己发的，且命中了，那么则显示***
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

    @Override
    public void onRoomNotificationReceived(String name, String content) {
        if (TextUtils.equals(name, GameConstant.EVENT_AGREE_MANAGE_PICK)) {
            if (TextUtils.equals(content, mUserId)) {
                KToast.show("用户连线成功");
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_REJECT_MANAGE_PICK)) {
            if (TextUtils.equals(content, mUserId)) {
                KToast.show("用户拒绝邀请");
            }
        } else if (TextUtils.equals(name, GameConstant.EVENT_INVITED_JOIN_GAME)) {
            // 邀请的是自己，需要弹框
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
            // 是否是房主邀请
            boolean isCrete = TextUtils.equals(userId, mGameRoomBean.getCreateUserId());
            mGameRoomListener.showPickReceivedDialog(isCrete, userId);
        }
    }

    @Override
    public void onKickSeatReceived(int i) {
        KToast.show("您已被抱下麦位");
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
            KToast.show("用户连线成功");
        }
    }

    @Override
    public void onInvitationRejected(String s) {
        if (TextUtils.equals(s, mUserId)) {
            KToast.show("用户拒绝邀请");
        }
    }

    @Override
    public void onInvitationCancelled(String s) {

    }

    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        if (TextUtils.equals(targetId, mUserId)) {
            KToast.show("你已被踢出房间");
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

    @Override
    public void onUserAudioRecordingDisable(String roomId, String userId, boolean disable) {

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

    @Override
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().kickUserFromRoom(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //踢出房间成功以后，要发送消息给被踢出的人
                RCChatroomKickOut kickOut = new RCChatroomKickOut();
                kickOut.setUserId(UserManager.get().getUserId());
                kickOut.setUserName(UserManager.get().getUserName());
                kickOut.setTargetId(user.getUserId());
                kickOut.setTargetName(user.getUserName());
                RCChatRoomMessageManager.sendChatMessage(mRoomId, kickOut, true, null, null);
                callback.onResult(true, "踢出成功");
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
            KToast.show("该用户已经上麦");
            return;
        }
        if (getAvailableSeatIndex() < 0) {
            callback.onResult(false, "麦位已满");
            return;
        }
        RCVoiceRoomEngine.getInstance().pickUserToSeat(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //邀请成功,集合会跟着变化
                callback.onResult(true, "邀请成功");
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
                        //座位禁麦成功
                        callback.onResult(true, "");
                        if (isMute) {
                            KToast.show("此麦位已闭麦");
                        } else {
                            KToast.show("已取消闭麦");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        //座位禁麦失败
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
                //锁座位成功
                callback.onResult(true, isClose ? "座位已关闭" : "座位已开启");
            }

            @Override
            public void onError(int i, String s) {
                //锁座位失败
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
        // 换我上麦,即先把当前麦位的人踢下去，然后自己上麦
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
            KToast.show("该用户已经加入游戏");
            return;
        }
        if (mGameState == RCGameState.PLAYING) {
            KToast.show("游戏中不能邀请玩家");
            return;
        }
        if (getAvailableSeatIndex() < 0 && !isInGame(user.getUserId())) {
            callback.onResult(false, "麦位已满");
            return;
        }
        notifyRoom(GameConstant.EVENT_INVITED_JOIN_GAME, user.getUserId());
        callback.onResult(true, "");
    }

    @Override
    public void clickKickGame(User user, ClickCallback<Boolean> callback) {
        if (mGameState == RCGameState.PLAYING) {
            KToast.show("游戏中不能踢人");
            return;
        }
        RCGameEngine.getInstance().kickPlayer(user.getUserId(), null);
        callback.onResult(true, "");
    }
}
