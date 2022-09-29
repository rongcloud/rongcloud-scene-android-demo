package cn.rongcloud.voice.room;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.AbsRoomActivity;
import cn.rongcloud.voice.room.helper.VoiceEventHelper;

/**
 * @author 李浩  语聊房重构
 * @date 2021/9/24
 */
@Route(path = RouterPath.ROUTER_VOICE_ROOM)
public class VoiceRoomActivity extends AbsRoomActivity {
    private boolean isCreate;

    @Override
    protected void initRoom() {
        isCreate = getIntent().getBooleanExtra(IntentWrap.KEY_IS_CREATE, false);
    }

    @Override
    public Fragment getFragment(String roomId) {
        return VoiceRoomFragment.getInstance(roomId, isCreate);
    }


    @Override
    protected RoomType getRoomType() {
        return RoomType.VOICE_ROOM;
    }

    @Override
    public void onLogout() {
        VoiceEventHelper.helper().unregeister();
        super.onLogout();
    }
}
