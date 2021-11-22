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
    public default void clickSettingAdmin(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickInviteSeat(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickKickRoom(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickKickSeat(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickMuteSeat(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickCloseSeat(User user, ClickCallback<Boolean> callback) {

    }

    @Override
    public default void clickSendGift(User user) {

    }
}