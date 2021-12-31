package cn.rongcloud.live.roomlist;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import cn.rong.combusis.ui.friend.FriendListFragment;
import cn.rong.combusis.ui.roomlist.AbsSwitchActivity;

/**
 * @author gyn
 * @date 2021/9/14
 */
public class LiveRoomListActivity extends AbsSwitchActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LiveRoomListActivity.class));
    }

    @Override
    public Fragment onCreateLeftFragment() {
        return LiveRoomListFragment.getInstance();
    }

    @Override
    public Fragment onCreateRightFragment() {
        return FriendListFragment.getInstance();
    }

    @Override
    public String[] onSetSwitchTitle() {
        return new String[]{"视频直播", "好友"};
    }
}
