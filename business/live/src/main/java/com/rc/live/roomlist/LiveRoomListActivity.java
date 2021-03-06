package com.rc.live.roomlist;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.roomkit.ui.friend.FriendListFragment;
import cn.rongcloud.roomkit.ui.roomlist.AbsSwitchActivity;


/**
 * @author gyn
 * @date 2021/9/14
 */
@Route(path = RouterPath.ROUTER_LIVE_LIST)
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
