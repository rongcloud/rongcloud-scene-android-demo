package cn.rongcloud.voice.room;


import androidx.lifecycle.MutableLiveData;

import com.basis.ui.mvp.IBaseView;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.voice.model.UiSeatModel;
import io.rong.imlib.model.MessageContent;

public interface IVoiceRoomFragmentView extends IBaseView {

    void setRoomData(VoiceRoomBean voiceRoomBean);


    void enterSeatSuccess();


    /**
     * 通知指定坐席信息发生了改变，刷新之
     */
    void onSeatInfoChange(int index, UiSeatModel uiSeatModel);

    void onSeatListChange(List<UiSeatModel> uiSeatModelList);


    void changeStatus(int status);

    void showUnReadRequestNumber(int number);


    void showRevokeSeatRequest();

    void onSpeakingStateChanged(boolean isSpeaking);

    void onNetworkStatus(int i);

    void finish();


    void refreshRoomOwner(UiSeatModel uiSeatModel);


    void setNotice(String notice);

    void showMessage(MessageContent messageContent, boolean isRefresh);

    void showMessageList(List<MessageContent> messageContentList, boolean isRefresh);

    void showSettingDialog(List<MutableLiveData<IFun.BaseFun>> funList);

    void showSetPasswordDialog(MutableLiveData<IFun.BaseFun> item);

    void showSetRoomNameDialog(String name);

    void setVoiceName(String name);

    void showShieldDialog(String roomId);

    void showSelectBackgroundDialog(String url);

    void showNoticeDialog(boolean isEdit);

    void setRoomBackground(String url);

    void refreshSeat();

    void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members);

    void showUserSetting(Member member, UiSeatModel uiSeatModel);

    void showMusicDialog();

    void showFinishView();

    void showLikeAnimation();

    void setOnlineCount(int OnlineCount);

    void switchOtherRoom(String roomId);

    void setTitleFollow(boolean isFollow);

    void refreshMessageList();

    void refreshMusicView(boolean show, String name, String url);
}
