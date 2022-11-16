package cn.rongcloud.voice.roomlist;

import androidx.fragment.app.Fragment;

import com.basis.ui.mvp.BasePresenter;

import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.roomlist.AbsRoomListFragment;


/**
 * @author gyn
 * @date 2021/9/14
 */
public class VoiceRoomListFragment extends AbsRoomListFragment {

    public static Fragment getInstance() {
        return new VoiceRoomListFragment();
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.VOICE_ROOM;
    }

    @Override
    public BasePresenter createPresent() {
        return null;
    }

    @Override
    public void initListener() {

    }

}
