package cn.rongcloud.radio.ui.room;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.basis.mvp.BasePresenter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.utils.Logger;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.AudioManagerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
import cn.rong.combusis.common.utils.JsonUtils;
import cn.rong.combusis.common.utils.UIKit;
import cn.rong.combusis.intent.IntentWrap;
import cn.rong.combusis.message.RCAllBroadcastMessage;
import cn.rong.combusis.message.RCChatroomAdmin;
import cn.rong.combusis.message.RCChatroomBarrage;
import cn.rong.combusis.message.RCChatroomEnter;
import cn.rong.combusis.message.RCChatroomGift;
import cn.rong.combusis.message.RCChatroomGiftAll;
import cn.rong.combusis.message.RCChatroomKickOut;
import cn.rong.combusis.message.RCChatroomLeave;
import cn.rong.combusis.message.RCChatroomLike;
import cn.rong.combusis.message.RCChatroomLocationMessage;
import cn.rong.combusis.message.RCFollowMsg;
import cn.rong.combusis.message.RCRRCloseMessage;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.ui.OnItemClickListener;
import cn.rong.combusis.ui.room.dialog.shield.Shield;
import cn.rong.combusis.ui.room.fragment.BackgroundSettingFragment;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.CreatorSettingFragment;
import cn.rong.combusis.ui.room.fragment.gift.GiftFragment;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomBackgroundFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomLockFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomMusicFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNameFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomNoticeFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomPauseFun;
import cn.rong.combusis.ui.room.fragment.roomsetting.RoomShieldFun;
import cn.rong.combusis.ui.room.model.Member;
import cn.rong.combusis.ui.room.model.MemberCache;
import cn.rong.combusis.ui.room.widget.RoomSeatView;
import cn.rong.combusis.ui.room.widget.RoomTitleBar;
import cn.rongcloud.radio.R;
import cn.rongcloud.radio.helper.RadioEventHelper;
import cn.rongcloud.radio.helper.RadioRoomListener;
import cn.rongcloud.radioroom.IRCRadioRoomEngine;
import cn.rongcloud.radioroom.RCRadioRoomEngine;
import cn.rongcloud.radioroom.callback.RCRadioRoomCallback;
import cn.rongcloud.radioroom.callback.RCRadioRoomResultCallback;
import cn.rongcloud.radioroom.room.RCRadioRoomInfo;
import cn.rongcloud.rtc.base.RCRTCLiveRole;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/9/24
 */
public class RadioRoomPresenter extends BasePresenter<RadioRoomView>
        implements RadioRoomListener,
        RadioRoomMemberSettingClickListener,
        OnItemClickListener<MutableLiveData<IFun.BaseFun>>,
        BackgroundSettingFragment.OnSelectBackgroundListener,
        GiftFragment.OnSendGiftListener,
        CreatorSettingFragment.OnCreatorSettingClickListener,
        RoomTitleBar.OnFollowClickListener {
    private VoiceRoomBean mVoiceRoomBean;
    private String mRoomId = "";
    private RoomOwnerType mRoomOwnerType;
    // 是否已经在房间
    private boolean isInRoom = false;

    public RadioRoomPresenter(RadioRoomView mView, LifecycleOwner lifecycleOwner) {
        super(mView, lifecycleOwner.getLifecycle());
    }

    /**
     * 初始化
     *
     * @param roomId
     */
    public void init(String roomId) {
        this.mRoomId = roomId;
        isInRoom = TextUtils.equals(RadioEventHelper.getInstance().getRoomId(), roomId);
        Logger.d("==================================inRoom:" + isInRoom);
        mView.showLoading("");
        UIKit.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        getRoomInfo();
                    }
                },
                500);
    }

    /**
     * 获取房间信息
     */
    public void getRoomInfo() {
        OkApi.get(
                VRApi.getRoomInfo(mRoomId),
                null,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                            if (roomBean != null) {
                                mVoiceRoomBean = roomBean;
                                initRoomData(roomBean);
                            }
                        } else {
                            mView.dismissLoading();
                            if (result.getCode() == 30001) {
                                // 房间不存在了
                                mView.showFinishView();
                                RadioEventHelper.getInstance().leaveRoom(null);
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
     * 初始化房间数据
     *
     * @param voiceRoomBean
     */
    private void initRoomData(VoiceRoomBean voiceRoomBean) {
        Logger.d("================jjjjjjjjjjjjjjjj" + voiceRoomBean);
        if (voiceRoomBean != null) {
            this.mVoiceRoomBean = voiceRoomBean;

            mRoomOwnerType = VoiceRoomProvider.provider().getRoomOwnerType(voiceRoomBean);
            mView.setRoomData(voiceRoomBean, mRoomOwnerType);

            // 之前不在房间，加入房间
            if (!isInRoom) {
                leaveAndJoinRoom();
                Logger.d("=================== leave room and join room");
            } else {
                addListener();
                // 从最小化回来后刷新状态
                refreshMute();
                refreshSeatView();
                refreshRoomData();
                mView.dismissLoading();
                Logger.d("=================== refresh room");
            }
        }
    }

    private void addListener() {
        RadioEventHelper.getInstance().register(mRoomId);
        RadioEventHelper.getInstance().addRadioEventListener(this);
    }

    private void leaveAndJoinRoom() {
        RCRadioRoomEngine.getInstance()
                .leaveRoom(
                        new RCRadioRoomCallback() {
                            @Override
                            public void onSuccess() {
                                Logger.d("==============leave room success");
                                changeUserRoom("");
                                joinRoom();
                            }

                            @Override
                            public void onError(int i, String s) {
                                Logger.d("==============leave room error " + i + "-" + s);
                                joinRoom();
                            }
                        });
    }

    private void joinRoom() {
        addListener();

        RCRadioRoomInfo roomInfo =
                new RCRadioRoomInfo(
                        TextUtils.equals(getCreateUserId(), AccountStore.INSTANCE.getUserId())
                                ? RCRTCLiveRole.BROADCASTER
                                : RCRTCLiveRole.AUDIENCE);
        roomInfo.setRoomId(mRoomId);
        roomInfo.setRoomName(mVoiceRoomBean.getRoomName());
        RCRadioRoomEngine.getInstance()
                .joinRoom(
                        roomInfo,
                        new RCRadioRoomCallback() {
                            @Override
                            public void onSuccess() {
                                Logger.d("==============joinRoom onSuccess");
                                changeUserRoom(mRoomId);
                                // 房主上麦
                                if (mRoomOwnerType == RoomOwnerType.RADIO_OWNER) {
                                    enterSeat();
                                }
                                // 发送默认消息
                                sendDefaultMessage();
                                refreshRoomData();
                                mView.dismissLoading();
                            }

                            @Override
                            public void onError(int code, String message) {
                                Logger.e(
                                        "==============joinRoom onError,code:"
                                                + code
                                                + ",message:"
                                                + message);
                                mView.dismissLoading();
                            }
                        });
    }

    // 刷新房间内其他信息
    private void refreshRoomData() {
        // 获取房间内成员和管理员列表
        MemberCache.getInstance().fetchData(mRoomId);
        // 在线人数
        refreshRoomMemberCount();
        // 礼物数量
        getGiftCount();
        // 管理员变化后刷新消息列表
        MemberCache.getInstance()
                .getAdminList()
                .observe(
                        (LifecycleOwner) mView,
                        new Observer<List<String>>() {
                            @Override
                            public void onChanged(List<String> strings) {
                                mView.refreshMessageList();
                            }
                        });
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

    public User getCreateRoomUser() {
        return mVoiceRoomBean.getCreateUser();
    }

    public RoomOwnerType getRoomOwnerType() {
        return mRoomOwnerType;
    }

    /**
     * 房主上麦
     */
    public void enterSeat() {
        RCRadioRoomEngine.getInstance()
                .enterSeat(
                        new RCRadioRoomCallback() {
                            @Override
                            public void onSuccess() {
                                RadioEventHelper.getInstance().setInSeat(true);
                                Logger.d("==============enterSeat onSuccess");
                                mView.setSeatState(RoomSeatView.SeatState.NORMAL);
                                RCRadioRoomEngine.getInstance()
                                        .updateRadioRoomKV(
                                                IRCRadioRoomEngine.UpdateKey.RC_SUSPEND, "0", null);
                                RCRadioRoomEngine.getInstance()
                                        .updateRadioRoomKV(
                                                IRCRadioRoomEngine.UpdateKey.RC_SILENT, "0", null);
                                AudioManagerUtil.INSTANCE.choiceAudioModel();
                            }

                            @Override
                            public void onError(int code, String message) {
                                Logger.e(
                                        "==============enterSeat onError, code:"
                                                + code
                                                + ",message:"
                                                + message);
                                AudioManagerUtil.INSTANCE.choiceAudioModel();
                            }
                        });
    }

    /**
     * 房主下麦
     */
    public void leaveSeat() {
        MusicManager.get().stopPlayMusic();
        RCRadioRoomEngine.getInstance()
                .leaveSeat(
                        new RCRadioRoomCallback() {
                            @Override
                            public void onSuccess() {
                                mView.setSeatState(RoomSeatView.SeatState.LEAVE_SEAT);
                                AudioManagerUtil.INSTANCE.choiceAudioModel();
                            }

                            @Override
                            public void onError(int i, String s) {
                                AudioManagerUtil.INSTANCE.choiceAudioModel();
                            }
                        });
    }

    /**
     * 点击麦位头像
     */
    public void clickRoomSeat() {
        if (mRoomOwnerType == RoomOwnerType.RADIO_OWNER) {
            if (RadioEventHelper.getInstance().isInSeat()) {
                mView.showCreatorSetting(
                        RadioEventHelper.getInstance().isMute(), MusicManager.get().isPlaying(), getCreateRoomUser());
            } else {
                enterSeat();
            }
        } else {
            if (RadioEventHelper.getInstance().isInSeat()) {
                getUserInfo(getCreateUserId());
            }
        }
    }

    /**
     * 获取房间内礼物列表
     */
    private void getGiftCount() {
        OkApi.get(
                VRApi.getGiftList(mRoomId),
                null,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            Map<String, String> map = result.getMap();
                            try {
                                Long giftCount = Long.valueOf(map.get(getCreateUserId()));
                                if (giftCount != null) {
                                    mView.setGiftCount(giftCount);
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                });
    }

    /**
     * 刷新房间人数
     */
    public void refreshRoomMemberCount() {
        // 由于退出房间前先发离开的消息，但还没离开成功，这时候立即获取人数是错误的，因此延时获取一下人数
        UIKit.postDelayed(
                () -> {
                    RongIMClient.getInstance()
                            .getChatRoomInfo(
                                    mRoomId,
                                    0,
                                    ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC,
                                    new RongIMClient.ResultCallback<ChatRoomInfo>() {
                                        @Override
                                        public void onSuccess(ChatRoomInfo chatRoomInfo) {
                                            if (chatRoomInfo != null) {
                                                mView.setOnlineCount(
                                                        chatRoomInfo.getTotalMemberCount());
                                            }
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                        }
                                    });
                },
                500);
    }

    /**
     * 获取房间公告
     */
    public void getNotice(boolean isModify) {
        RCRadioRoomEngine.getInstance()
                .getRadioRoomValue(
                        IRCRadioRoomEngine.UpdateKey.RC_NOTICE,
                        new RCRadioRoomResultCallback<String>() {
                            @Override
                            public void onSuccess(String s) {
                                mView.showNotice(s, isModify);
                            }

                            @Override
                            public void onError(int i, String s) {
                                mView.showNotice(
                                        String.format("欢迎来到%s。", mVoiceRoomBean.getRoomName()),
                                        isModify);
                            }
                        });
    }

    /**
     * 修改房间公告
     *
     * @param notice
     */
    public void modifyNotice(String notice) {
        RCRadioRoomEngine.getInstance()
                .updateRadioRoomKV(IRCRadioRoomEngine.UpdateKey.RC_NOTICE, notice, null);
    }

    /**
     * 发送默认消息
     */
    private void sendDefaultMessage() {
        if (mVoiceRoomBean != null) {
            // 清空所有消息
            mView.addToMessageList(null, true);
            // 默认消息
            RCChatroomLocationMessage welcome = new RCChatroomLocationMessage();
            welcome.setContent(String.format("欢迎来到 %s", mVoiceRoomBean.getRoomName()));
            sendMessage(welcome);
            RCChatroomLocationMessage tips = new RCChatroomLocationMessage();
            tips.setContent("感谢使用融云 RTC 语音房，请遵守相关法规，不要传播低俗、暴力等不良信息。欢迎您把使用过程中的感受反馈给我们。");
            sendMessage(tips);
            // 发送进入房间的消息
            RCChatroomEnter enter = new RCChatroomEnter();
            enter.setUserId(AccountStore.INSTANCE.getUserId());
            enter.setUserName(AccountStore.INSTANCE.getUserName());
            sendMessage(enter);
            RadioEventHelper.getInstance().setSendDefaultMessage(true);
        }
    }

    /**
     * 发送文字消息
     *
     * @param msg 消息内容
     */
    public void sendMessage(String msg) {
        RCChatroomBarrage barrage = new RCChatroomBarrage();
        barrage.setContent(msg);
        barrage.setUserId(AccountStore.INSTANCE.getUserId());
        barrage.setUserName(AccountStore.INSTANCE.getUserName());
        getShield(
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            List<Shield> shields = result.getList(Shield.class);
                            boolean isContains = false;
                            if (shields != null) {
                                for (Shield shield : shields) {
                                    if (msg.contains(shield.getName())) {
                                        isContains = true;
                                        break;
                                    }
                                }
                            }
                            if (isContains) {
                                mView.addToMessageList(barrage, false);
                            } else {
                                sendMessage(barrage);
                            }
                        } else {
                            sendMessage(barrage);
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        sendMessage(barrage);
                    }
                });
    }

    /**
     * 设置弹框数据
     */
    public void showSettingDialog() {
        List<MutableLiveData<IFun.BaseFun>> funList =
                Arrays.asList(
                        new MutableLiveData<>(new RoomLockFun(mVoiceRoomBean.isPrivate() ? 1 : 0)),
                        new MutableLiveData<>(new RoomNameFun(0)),
                        new MutableLiveData<>(new RoomNoticeFun(0)),
                        new MutableLiveData<>(new RoomBackgroundFun(0)),
                        new MutableLiveData<>(new RoomShieldFun(0)),
                        new MutableLiveData<>(new RoomMusicFun(0)),
                        new MutableLiveData<>(new RoomPauseFun(0)));
        mView.showSettingDialog(funList);
    }

    /**
     * 发送消息
     *
     * @param messageContent
     */
    public void sendMessage(MessageContent messageContent) {
        RadioEventHelper.getInstance().sendMessage(messageContent);
    }

    private boolean isSelf(String userId) {
        return TextUtils.equals(userId, AccountStore.INSTANCE.getUserId());
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
    private void getShield(WrapperCallBack wrapperCallBack) {
        OkApi.get(VRApi.getShield(mRoomId), null, wrapperCallBack);
    }

    /**
     * 暂停直播
     */
    public void pauseRadioLive() {
        RCRadioRoomEngine.getInstance()
                .leaveSeat(
                        new RCRadioRoomCallback() {
                            @Override
                            public void onSuccess() {
                                mView.setSeatState(RoomSeatView.SeatState.OWNER_PAUSE);
                                // 发暂停通知
                                RCRadioRoomEngine.getInstance()
                                        .updateRadioRoomKV(
                                                IRCRadioRoomEngine.UpdateKey.RC_SUSPEND, "1", null);
                                OkApi.get(VRApi.stopRoom(mRoomId), null, new WrapperCallBack() {
                                    @Override
                                    public void onResult(Wrapper result) {

                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {
                            }
                        });
    }

    public void switchRoom() {
        RadioEventHelper.getInstance().switchRoom();
    }

    public void leaveRoom() {
        mView.showLoading("");
        RadioEventHelper.getInstance()
                .leaveRoom(
                        new RadioEventHelper.LeaveRoomCallback() {
                            @Override
                            public void leaveFinish() {
                                mView.dismissLoading();
                                mView.finish();
                            }
                        });
    }

    public void closeRoom() {
        mView.showLoading("");
        RadioEventHelper.getInstance()
                .closeRoom(
                        mRoomId,
                        new RadioEventHelper.CloseRoomCallback() {
                            @Override
                            public void onSuccess() {
                                mView.dismissLoading();
                                mView.finish();
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
    public void setRoomPassword(
            boolean isPrivate, String password, MutableLiveData<IFun.BaseFun> item) {
        int p = isPrivate ? 1 : 0;
        OkApi.put(
                VRApi.ROOM_PASSWORD,
                new OkParams()
                        .add("roomId", mRoomId)
                        .add("isPrivate", p)
                        .add("password", password)
                        .build(),
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
        OkApi.put(
                VRApi.ROOM_NAME,
                new OkParams().add("roomId", mRoomId).add("name", name).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            mView.showToast("修改成功");
                            mView.setRadioName(name);
                            mVoiceRoomBean.setRoomName(name);
                            RCRadioRoomEngine.getInstance()
                                    .updateRadioRoomKV(
                                            IRCRadioRoomEngine.UpdateKey.RC_ROOM_NAME, name, null);
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
    public void clickKickRoom(User user, ClickCallback<Boolean> callback) {
        if (mVoiceRoomBean == null) {
            return;
        }
        MemberCache.getInstance().removeMember(user);
        RCChatroomKickOut kickOut = new RCChatroomKickOut();
        kickOut.setUserId(AccountStore.INSTANCE.getUserId());
        kickOut.setUserName(AccountStore.INSTANCE.getUserName());
        kickOut.setTargetId(user.getUserId());
        kickOut.setTargetName(user.getUserName());
        sendMessage(kickOut);
        callback.onResult(true, "");
    }

    @Override
    public void clickSettingAdmin(User user, ClickCallback<Boolean> callback) {
        if (mVoiceRoomBean == null) {
            return;
        }
        boolean isAdmin = !MemberCache.getInstance().isAdmin(user.getUserId());
        HashMap<String, Object> params =
                new OkParams()
                        .add("roomId", mRoomId)
                        .add("userId", user.getUserId())
                        .add("isManage", isAdmin)
                        .build();
        // 先请求 设置/取消 管理员
        OkApi.put(
                VRApi.ADMIN_MANAGE,
                params,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            if (isAdmin) {
                                MemberCache.getInstance().addAdmin(user.getUserId());
                            } else {
                                MemberCache.getInstance().removeAdmin(user.getUserId());
                            }
                            RCChatroomAdmin admin = new RCChatroomAdmin();
                            admin.setAdmin(isAdmin);
                            admin.setUserId(user.getUserId());
                            admin.setUserName(user.getUserName());
                            // 成功后发送管理变更的消息
                            sendMessage(admin);
                            callback.onResult(true, "");
                        } else {
                            mView.showToast(result.getMessage());
                            callback.onResult(true, result.getMessage());
                        }
                    }
                });
    }

    /**
     * 点击底部送礼物，电台房只能给房主送，语聊房要把麦位上所有用户都返回，并且赋值麦位号
     */
    public void sendGift() {
        mView.showSendGiftDialog(
                mVoiceRoomBean,
                getCreateUserId(),
                Arrays.asList(new Member().toMember(getCreateRoomUser())));
    }

    /**
     * 点击个人信息里的送礼物，送给某个特定的人
     *
     * @param user
     */
    @Override
    public void clickSendGift(User user) {
        mView.showSendGiftDialog(
                mVoiceRoomBean, user.getUserId(), Arrays.asList(new Member().toMember(user)));
    }

    @Override
    public void clickFollow(boolean isFollow, RCFollowMsg followMsg) {
        if (isFollow) {
            sendMessage(followMsg);
        }
        mView.setTitleFollow(isFollow);
    }

    /**
     * 根据id获取用户信息
     */
    public void getUserInfo(String userId) {
        OkApi.post(
                VRApi.GET_USER,
                new OkParams().add("userIds", new String[]{userId}).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            List<Member> members = result.getList(Member.class);
                            if (members != null && members.size() > 0) {
                                mView.showUserSetting(members.get(0));
                            }
                        }
                    }
                });
    }

    @Override
    public void onMessageReceived(Message message) {
        MessageContent content = message.getContent();
        Logger.d(
                "==============onMessageReceived: "
                        + content.getClass()
                        + JsonUtils.toJson(content));

        if (content instanceof RCChatroomGift || content instanceof RCChatroomGiftAll) {
            // 刷新礼物数
            getGiftCount();
        } else if (content instanceof RCChatroomAdmin) {
            // 刷新房间管理列表
            MemberCache.getInstance().refreshAdminData(mRoomId);
        } else if (content instanceof RCChatroomKickOut) {
            // 如果踢出的是自己，就离开房间
            String targetId = ((RCChatroomKickOut) content).getTargetId();
            if (TextUtils.equals(targetId, AccountStore.INSTANCE.getUserId())) {
                leaveRoom();
                mView.showToast("你已被踢出房间");
            } else {
                refreshRoomMemberCount();
            }
        } else if (content instanceof RCChatroomLike) {
            mView.showLikeAnimation();
            return;
        } else if (content instanceof RCChatroomBarrage
                && isSelf(((RCChatroomBarrage) content).getUserId())) {
        } else if (content instanceof RCRRCloseMessage) {
            mView.showRoomCloseDialog();
            return;
        } else if (content instanceof RCChatroomEnter) {
            refreshRoomMemberCount();
        } else if (content instanceof RCChatroomLeave) {
            refreshRoomMemberCount();
            if (TextUtils.equals(((RCChatroomLeave) content).getUserId(), getCreateUserId())) {
                mView.setSeatState(RoomSeatView.SeatState.LEAVE_SEAT);
            }
            return;
        }

        // 显示到弹幕列表
        if (RadioEventHelper.getInstance().isShowingMessage(message)) {
            mView.addToMessageList(content, false);
        }
    }

    @Override
    public void onRadioRoomKVUpdate(IRCRadioRoomEngine.UpdateKey updateKey, String s) {
        Logger.d("===============" + updateKey.getValue() + "=====" + s);
        switch (updateKey) {
            case RC_ROOM_NAME:
                mView.setRadioName(s);
                break;
            case RC_SPEAKING:
                boolean isSpeaking = TextUtils.equals(s, "1");
                mView.setSpeaking(isSpeaking);
                break;
            case RC_SUSPEND:
                refreshSeatView();
                break;
            case RC_BGNAME:
                mView.setRoomBackground(s);
                break;
            case RC_SEATING:
                refreshSeatView();
                break;
            case RC_SILENT:
                refreshMute();
                break;
        }
    }

    /**
     * 刷新座位状态
     */
    private void refreshSeatView() {
        if (RadioEventHelper.getInstance().isSuspend()) {
            if (isSelf(getCreateUserId())) {
                mView.setSeatState(RoomSeatView.SeatState.OWNER_PAUSE);
            } else {
                mView.setSeatState(RoomSeatView.SeatState.VIEWER_PAUSE);
            }
        } else {
            if (RadioEventHelper.getInstance().isInSeat()) {
                mView.setSeatState(RoomSeatView.SeatState.NORMAL);
            } else {
                mView.setSeatState(RoomSeatView.SeatState.LEAVE_SEAT);
            }
        }
    }

    private void refreshMute() {
        mView.setSeatMute(RadioEventHelper.getInstance().isMute());
    }

    /**
     * 设置里面的设置选项
     *
     * @param item
     * @param position
     */
    @Override
    public void clickItem(MutableLiveData<IFun.BaseFun> item, int position) {
        IFun.BaseFun fun = item.getValue();
        if (fun instanceof RoomPauseFun) {
            pauseRadioLive();
        } else if (fun instanceof RoomNoticeFun) {
            getNotice(true);
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
            mView.showShieldDialog(mRoomId);
        } else if (fun instanceof RoomMusicFun) {
            if (RadioEventHelper.getInstance().isInSeat()) {
                mView.showMusicDialog();
            } else {
                mView.showToast("请先上麦之后再播放音乐");
            }
        }
    }

    @Override
    public void selectBackground(String url) {
        OkApi.put(
                VRApi.ROOM_BACKGROUND,
                new OkParams().add("roomId", mRoomId).add("backgroundUrl", url).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            mVoiceRoomBean.setBackgroundUrl(url);
                            mView.setRoomBackground(url);
                            RCRadioRoomEngine.getInstance()
                                    .updateRadioRoomKV(
                                            IRCRadioRoomEngine.UpdateKey.RC_BGNAME, url, null);
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

    @Override
    public void onDestroy() {
        RadioEventHelper.getInstance().removeRadioEventListener(this);
        Logger.d("====================" + "radio room destroy, remove listener" + mView);
        super.onDestroy();
    }

    @Override
    public void clickLeaveSeat() {
        leaveSeat();
    }

    @Override
    public void clickMuteSelf(boolean isMute) {
        RCRadioRoomEngine.getInstance().muteSelf(isMute);
        RCRadioRoomEngine.getInstance()
                .updateRadioRoomKV(
                        IRCRadioRoomEngine.UpdateKey.RC_SILENT, isMute ? "1" : "0", null);
    }

    @Override
    public void onLoadMessageHistory(List<Message> messages) {
        List<MessageContent> contents = new ArrayList<>();
        for (Message message : messages) {
            contents.add(message.getContent());
        }
        mView.addAllToMessageList(contents, true);
    }

    // 更改所属房间
    public void changeUserRoom(String roomId) {
        HashMap<String, Object> params = new OkParams().add("roomId", roomId).build();
        OkApi.get(
                VRApi.USER_ROOM_CHANGE,
                params,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            Log.e("TAG", "changeUserRoom: " + result.getBody());
                        }
                    }
                });
    }

    /**
     * 点击全局广播后跳转到相应的房间
     *
     * @param message
     */
    public void jumpRoom(RCAllBroadcastMessage message) {
        // 当前房间不跳转,房主不能跳转
        if (message == null
                || TextUtils.isEmpty(message.getRoomId())
                || TextUtils.equals(message.getRoomId(), mRoomId)
                || isSelf(mVoiceRoomBean.getUserId())) return;
        OkApi.get(
                VRApi.getRoomInfo(message.getRoomId()),
                null,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        if (result.ok()) {
                            VoiceRoomBean roomBean = result.get(VoiceRoomBean.class);
                            if (roomBean != null) {
                                // 房间有密码需要弹框验证密码
                                if (roomBean.isPrivate()) {
                                    new InputPasswordDialog(
                                            ((RadioRoomFragment) mView).requireContext(),
                                            false,
                                            () -> null,
                                            s -> {
                                                if (TextUtils.isEmpty(s)) {
                                                    return null;
                                                }
                                                if (s.length() < 4) {
                                                    mView.showToast(
                                                            UIKit.getResources()
                                                                    .getString(
                                                                            R.string
                                                                                    .text_please_input_four_number));
                                                    return null;
                                                }
                                                if (TextUtils.equals(
                                                        s, roomBean.getPassword())) {
                                                    jumpOtherRoom(
                                                            roomBean.getRoomType(),
                                                            roomBean.getRoomId());
                                                } else {
                                                    mView.showToast("密码错误");
                                                }
                                                return null;
                                            })
                                            .show();
                                } else {
                                    jumpOtherRoom(roomBean.getRoomType(), roomBean.getRoomId());
                                }
                            }
                        } else {
                            mView.dismissLoading();
                            if (result.getCode() == 30001) {
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

    private void jumpOtherRoom(int roomType, final String roomId) {
        if (VoiceRoomProvider.provider().contains(roomId)) {
            mView.switchOtherRoom(roomId);
        } else {
            mView.showLoading("");
            RadioEventHelper.getInstance()
                    .leaveRoom(
                            new RadioEventHelper.LeaveRoomCallback() {
                                @Override
                                public void leaveFinish() {
                                    mView.dismissLoading();
                                    mView.finish();
                                    IntentWrap.launchRoom(
                                            ((RadioRoomFragment) mView).requireContext(),
                                            roomType,
                                            roomId);
                                }
                            });
        }
    }
}
