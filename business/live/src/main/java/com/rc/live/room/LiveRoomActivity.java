package com.rc.live.room;


import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.utils.NetUtil;
import com.meihu.beauty.utils.MhDataManager;
import com.rc.live.helper.LiveEventHelper;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.AbsRoomActivity;


/**
 * @author lihao1
 * @date 2021/9/14
 */
@Route(path = RouterPath.ROUTER_LIVE_ROOM)
public class LiveRoomActivity extends AbsRoomActivity {

    boolean isCreate;

    @Override
    protected void initRoom() {
        //初始化
        MhDataManager.getInstance().create(this.getApplicationContext());
        isCreate = getIntent().getBooleanExtra(IntentWrap.KEY_IS_CREATE, false);
    }

    @Override
    public Fragment getFragment(String roomId) {
        return LiveRoomFragment.getInstance(roomId, isCreate);
    }

    @Override
    protected RoomType getRoomType() {
        return RoomType.LIVE_ROOM;
    }

    @Override
    protected void onDestroy() {
        MhDataManager.getInstance().release();
        super.onDestroy();
    }

    @Override
    public void onNetChange() {
        super.onNetChange();
        if (NetUtil.isNetworkAvailable()) {
            if (PKManager.get().getPkState().isInPk()) {
                PKManager.get().refreshPKFromServer();
            }
        }
    }

    @Override
    public void onLogout() {
        LiveEventHelper.getInstance().removeLiveRoomListeners();
        LiveEventHelper.getInstance().unRegister();
        super.onLogout();
    }
}
