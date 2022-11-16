package cn.rongcloud.radio.ui.room;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.radio.helper.RadioEventHelper;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.AbsRoomActivity;


/**
 * @author gyn
 * @date 2021/9/14
 */
@Route(path = RouterPath.ROUTER_RADIO_ROOM)
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

    @Override
    public void onLogout() {
        RadioEventHelper.getInstance().unRegister();
        super.onLogout();
    }
}
