package cn.rongcloud.gameroom.ui.gamelist;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.roomkit.ui.friend.FriendListFragment;
import cn.rongcloud.roomkit.ui.roomlist.AbsSwitchActivity;
import io.rong.imkit.utils.StatusBarUtil;

/**
 * @author gyn
 * @date 2022/3/9
 */
@Route(path = RouterPath.ROUTER_GAME_LIST)
public class GameListActivity extends AbsSwitchActivity {

    public String[] onSetSwitchTitle() {
        return new String[]{"游戏房", "好友"};
    }

    public Fragment onCreateLeftFragment() {
        return GameListFragment.getInstance();
    }

    public Fragment onCreateRightFragment() {
        return FriendListFragment.getInstance();
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.title_bar).setBackgroundResource(R.color.game_bg_page);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.game_bg_page));
    }
}
