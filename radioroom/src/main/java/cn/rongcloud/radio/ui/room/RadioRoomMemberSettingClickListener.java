package cn.rongcloud.radio.ui.room;

import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.MemberSettingFragment;

/**
 * @author gyn
 * @date 2021/9/28
 */
public interface RadioRoomMemberSettingClickListener extends MemberSettingFragment.OnMemberSettingClickListener {

    @Override
    default void clickSettingAdmin(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickInviteSeat(int seatIndex, User user, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickKickRoom(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickKickSeat(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickMuteSeat(int seatIndex, boolean isMute, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickCloseSeat(int seatIndex, boolean isLock, ClickCallback<Boolean> callback) {

    }

    @Override
    default void clickSendGift(User user) {

    }

    @Override
    default void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    default void rejectRequestSeat(String userId, ClickCallback<Boolean> callback) {

    }

    @Override
    default void cancelRequestSeat(ClickCallback<Boolean> callback) {

    }

    @Override
    default void switchToSeat(int seatIndex, ClickCallback<Boolean> callback) {

    }

    @Override
    default void cancelInvitation(String userId, ClickCallback<Boolean> callback) {

    }

}