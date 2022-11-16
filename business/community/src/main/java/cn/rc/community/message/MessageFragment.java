package cn.rc.community.message;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.BaseFragment;
import com.basis.ui.CmdKey;
import com.basis.utils.ResUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.Arrays;
import java.util.List;

import cn.rc.community.R;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imlib.model.Conversation;

@Route(path = RouterPath.FRAGMENT_MESSAGE)
public class MessageFragment extends BaseFragment {
    // 是否有@我的功能
    private final static boolean HAS_AT = false;
    private final static MsgTabAdapter.MsgTab[] MSG_TABS =
            HAS_AT ? new MsgTabAdapter.MsgTab[]{
                    new MsgTabAdapter.MsgTab(ResUtil.getString(R.string.cmu_message_private)),
                    new MsgTabAdapter.MsgTab(ResUtil.getString(R.string.cmu_message_atme)),
                    new MsgTabAdapter.MsgTab(ResUtil.getString(R.string.cmu_message_system))}
                    : new MsgTabAdapter.MsgTab[]{
                    new MsgTabAdapter.MsgTab(ResUtil.getString(R.string.cmu_message_private)),
                    new MsgTabAdapter.MsgTab(ResUtil.getString(R.string.cmu_message_system))
            };
    private RecyclerView tabSwitch;
    private ViewPager2 vpSwitch;
    private MsgTabAdapter adapter;
    private UnReadMessageManager.IUnReadMessageObserver priObserver, sysObserver;
    private List<Fragment> fragments;

    @Override
    public void onDetach() {
        UnReadMessageManager.getInstance().removeObserver(priObserver);
        UnReadMessageManager.getInstance().removeObserver(sysObserver);
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (fragments.size() > adapter.getSelected()) {
            Fragment fragment = fragments.get(adapter.getSelected());
            if (fragment instanceof SystemMessageFragment) {
                SmartRefreshLayout refreshLayout = ((SystemMessageFragment) fragment).refreshLayout;
                if (null != refreshLayout) refreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void init() {
        tabSwitch = getView(R.id.tab_switch);
        vpSwitch = getView(R.id.vp_switch);
        // viewpage
        fragments = HAS_AT ?
                Arrays.asList(new PrivateConversationFragment(),
                        AtMeFragment.getInstance(),
                        SystemMessageFragment.getInstance()) :
                Arrays.asList(new PrivateConversationFragment(),
                        SystemMessageFragment.getInstance());
        vpSwitch.setAdapter(new VPAdapter(activity, fragments));
        // tab
        adapter = new MsgTabAdapter(activity);
        adapter.attach(tabSwitch, vpSwitch);
        adapter.setData(Arrays.asList(MSG_TABS), true);
        adapter.setSelected(0);
        // 监听私信消息未读数
        observeMessageUnreadCount();
    }

    void observeMessageUnreadCount() {
        // 私信未读消息
        Conversation.ConversationType[] ps = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE};
        UnReadMessageManager.getInstance().addObserver(ps, priObserver = new UnReadMessageManager.IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int count) {
                if (null != adapter) adapter.refreshUnreadCount(0, count);
            }
        });
        // 系统消息
        Conversation.ConversationType[] ss = new Conversation.ConversationType[]{Conversation.ConversationType.SYSTEM};
        UnReadMessageManager.getInstance().addObserver(ss, sysObserver = new UnReadMessageManager.IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int count) {
                if (null != adapter)
                    adapter.refreshUnreadCount(HAS_AT ? 2 : 1, count);
            }
        });
    }

    private boolean showReadPoint = false;

    @Override
    public void onRefresh(ICmd obj) {
        super.onRefresh(obj);
        if (null != obj && CmdKey.KEY_REFRESH.equals(obj.getKey())) {
            showReadPoint = obj.getObject();
            if (!showReadPoint && adapter != null) {
                adapter.refreshUnreadCount(0, 0);
                adapter.refreshUnreadCount(HAS_AT ? 2 : 1, 0);
            }
        }
    }
}
