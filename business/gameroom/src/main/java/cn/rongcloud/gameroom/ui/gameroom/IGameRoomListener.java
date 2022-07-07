package cn.rongcloud.gameroom.ui.gameroom;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.model.GameRoomBean;
import cn.rongcloud.gameroom.model.SeatPlayer;
import cn.rongcloud.roomkit.ui.room.model.Member;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2022/5/6
 */
public interface IGameRoomListener {
    void onLoadRoomDetail(GameRoomBean gameRoomBean);

    void onLoadGame(String roomId, String gameId, String userId, String appCode);

    void showLoading(String msg);

    void dismissLoading();

    void showRoomFinished();

    void showMemberSetting(Member member, SeatPlayer seatPlayer, String roomCreatorId);

    void onSeatPlayerChanged(List<SeatPlayer> seatPlayerList);

    void showMessage(MessageContent messageContent, boolean isRefresh);

    void showMessageList(List<MessageContent> messageList, boolean isRefresh);

    void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members);

    void finishRoom();

    void setTitleFollow(boolean isFollow);

    void showPickReceivedDialog(boolean isCreate, String userId);

    void showInviteJoinGameDialog();

    void onRoomClosed();

    void onNetworkStatus(int delay, boolean isShow);

    void showEmptySeatDialog(SeatPlayer seatPlayer);

    void showSelfSettingDialog(SeatPlayer seatPlayer);

    void showInvitePlayerDialog();

    void setRoomName(String name);

    void refreshMusicView(boolean show);

    void onGameChanged(RCGameInfo gameInfo);

    void onGameStarted();

    void onInSeatChanged(boolean isEnter);

    void onMicSwitched(boolean isOn);

    void onSpeakingChanged(int seatIndex, boolean isSpeaking);
}
