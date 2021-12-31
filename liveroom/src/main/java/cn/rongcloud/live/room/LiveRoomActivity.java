package cn.rongcloud.live.room;


import androidx.fragment.app.Fragment;

import com.meihu.beauty.utils.MhDataManager;

import cn.rong.combusis.intent.IntentWrap;
import cn.rong.combusis.provider.voiceroom.RoomType;
import cn.rong.combusis.ui.room.AbsRoomActivity;

/**
 * @author lihao1
 * @date 2021/9/14
 */
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
}
