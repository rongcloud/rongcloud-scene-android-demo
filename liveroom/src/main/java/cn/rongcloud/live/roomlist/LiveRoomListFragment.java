package cn.rongcloud.live.roomlist;

import androidx.fragment.app.Fragment;

import com.basis.mvp.BasePresenter;

import cn.rong.combusis.provider.voiceroom.RoomType;
import cn.rong.combusis.ui.roomlist.AbsRoomListFragment;

/**
 * @author gyn
 * @date 2021/9/14
 */
public class LiveRoomListFragment extends AbsRoomListFragment {

    public static Fragment getInstance() {
        return new LiveRoomListFragment();
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.LIVE_ROOM;
    }

    @Override
    public BasePresenter createPresent() {
        return null;
    }

    @Override
    public void initListener() {

    }

}
