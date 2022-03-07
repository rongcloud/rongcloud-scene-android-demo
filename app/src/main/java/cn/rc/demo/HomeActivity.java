package cn.rc.demo;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.PermissionActivity;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.feedback.IFeedback;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imlib.model.Conversation;

@Route(path = RouterPath.ROUTER_MAIN)
public class HomeActivity extends PermissionActivity implements UnReadMessageManager.IUnReadMessageObserver {
    private final static String TAG = "HomeActivity";
    private final static HomeBottomBar[] barTitles = new HomeBottomBar[]{
            new HomeBottomBar("首页", R.drawable.selector_bar_home, RouterPath.FRAGMENT_HOME, false, true),
            new HomeBottomBar("发现", R.drawable.selector_bar_find, RouterPath.FRAGMENT_FIND),
            new HomeBottomBar("我的", R.drawable.selector_bar_me, RouterPath.FRAGMENT_ME)
    };

    @Override
    public int setLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected String[] onCheckPermission() {
        return LAUNCHER_PERMISSIONS;
    }

    private RecyclerView bottomBar;
    private HomeBarAdapter adapter;

    @Override
    protected void onAccept(boolean accept) {
        getWrapBar().setHide(true).work();
        bottomBar = getView(R.id.bottom_bar);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomBar.setLayoutManager(manager);
        adapter = new HomeBarAdapter(activity, R.id.contain);
        bottomBar.setAdapter(adapter);

        // clone 元数据
        List<HomeBottomBar> data = new ArrayList<>();
        for (HomeBottomBar bar : barTitles) {
            data.add(bar.clone());
        }
        adapter.setData(data, true);
        // 未读消息
        Conversation.ConversationType[] cs = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE};
        UnReadMessageManager.getInstance().addObserver(cs, this);
    }


    @Override
    protected void onDestroy() {
        UnReadMessageManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void onCountChanged(int count) {
        if (null != adapter) {
            List<HomeBottomBar> data = adapter.getData();
            if (null != data && !data.isEmpty()) {
                HomeBottomBar bar = data.get(0);
                if (!bar.selected) {
                    bar.hasRedPoint = count > 0;
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }
}