package cn.rongcloud.radio.ui.room;

import androidx.fragment.app.Fragment;

import cn.rong.combusis.provider.voiceroom.RoomType;
import cn.rong.combusis.ui.room.AbsRoomActivity;

/**
 * @author gyn
 * @date 2021/9/14
 */
public class RadioRoomActivity extends AbsRoomActivity {

    @Override
    protected void initRoom() {
    }

    @Override
    public Fragment getFragment(String roomId) {
        return RadioRoomFragment.getInstance(roomId);
    }

    @Override
    protected RoomType getRoomType() {
        return RoomType.RADIO_ROOM;
    }

}
