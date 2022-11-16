package cn.rongcloud.radio.ui.room;


import androidx.lifecycle.MutableLiveData;

import com.basis.ui.mvp.IBaseView;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.widget.RoomSeatView;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/9/24
 */
public interface RadioRoomView extends IBaseView {

    void setRoomData(VoiceRoomBean voiceRoomBean, RoomOwnerType roomOwnerType);

    void setOnlineCount(int num);

    void addToMessageList(MessageContent messageContent, boolean isRefresh);

    void addAllToMessageList(List<MessageContent> messageContents, boolean isRefresh);

    void finish();

    void setSpeaking(boolean speaking);

    void setRadioName(String name);

    void showNotice(String notice, boolean isModify);

    void setSeatState(RoomSeatView.SeatState seatState);

    void setSeatMute(boolean isMute);

    void showSettingDialog(List<MutableLiveData<IFun.BaseFun>> funList);

    void showSetPasswordDialog(MutableLiveData<IFun.BaseFun> item);

    void showSetRoomNameDialog(String name);

    void showSelectBackgroundDialog(String url);

    void setRoomBackground(String url);

    void showShieldDialog(String roomId);

    void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members);

    void setGiftCount(Long count);

    void showUserSetting(Member member);

    void showLikeAnimation();

    void showCreatorSetting(boolean isMute, boolean isPlayingMusic, User user);

    void showMusicDialog();

    void showRoomCloseDialog();

    void showFinishView();

    void switchOtherRoom(String roomId);

    void refreshMessageList();

    void setTitleFollow(boolean isFollow);

    void refreshMusicView(boolean show, String name, String url);
}
