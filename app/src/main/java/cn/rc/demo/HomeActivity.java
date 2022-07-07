package cn.rc.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.CmdKey;
import com.basis.ui.PermissionActivity;
import com.basis.ui.UIStack;
import com.basis.utils.Logger;
import com.basis.utils.NotificationUtil;
import com.basis.utils.UiUtils;
import com.basis.widget.dialog.VRCenterDialog;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import cn.rc.demo.check.TIPHelper;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.utils.StatusBarUtil;
import io.rong.imlib.model.Conversation;

@Route(path = RouterPath.ROUTER_MAIN)
public class HomeActivity extends PermissionActivity implements UnReadMessageManager.IUnReadMessageObserver {
    private final static String TAG = "HomeActivity";
    private final static HomeBottomBar[] barTitles = new HomeBottomBar[]{
            // new HomeBottomBar("社区", R.drawable.selector_bar_community, RouterPath.FRAGMENT_COMMUNITY, false, true),
            new HomeBottomBar("娱乐", R.drawable.selector_bar_home, RouterPath.FRAGMENT_HOME, false, false),
            // new HomeBottomBar("发现", R.drawable.selector_bar_find, RouterPath.FRAGMENT_FIND),
            new HomeBottomBar("消息", R.drawable.selector_bar_message, RouterPath.FRAGMENT_MESSAGE),
            new HomeBottomBar("我的", R.drawable.selector_bar_me, RouterPath.FRAGMENT_ME_COMMUNITY)
    };

    private static int MSG_INDEX = 0;

    private TabLayout tabLayout;
    private ViewPager2 container;
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            //判断当前启动的activity在任务栈中存在
            //若当前启动的activity是首页则关闭，即代表是点击了home键重新启动了activity
            finish();
        }
        if (!NotificationUtil.isNotifyEnabled(this)) {
            VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
            confirmDialog.replaceContent("监测到您未打开通知栏权限，可点击前往设置打开", "取消", null, "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationUtil.jumpToSetting(getApplication());
                }
            }, null);
            confirmDialog.setCancelable(false);
            confirmDialog.show();
        }
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected String[] onCheckPermission() {
        return LAUNCHER_PERMISSIONS;
    }


    @Override
    protected void onAccept(boolean accept) {
        getWrapBar().setHide(true).work();
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarFontIconDark(this, StatusBarUtil.TYPE_M, true);

        tabLayout = getView(R.id.tab_home);
        container = getView(R.id.vp_home);
        homeAdapter = new HomeAdapter(this, barTitles);
        container.setAdapter(homeAdapter);
        container.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, container, true, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(barTitles[position].title);
                tab.setIcon(barTitles[position].icon);
            }
        }).attach();

        for (int i = 0; i < barTitles.length; i++) {
            if (TextUtils.equals(barTitles[i].router, RouterPath.FRAGMENT_MESSAGE)) {
                MSG_INDEX = i;
                break;
            }
        }

        // 未读消息
        Conversation.ConversationType[] cs = new Conversation.ConversationType[]{
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.SYSTEM};
        UnReadMessageManager.getInstance().addObserver(cs, this);
        TIPHelper.showTipDialog(this);

    }


    @Override
    public void onRefresh(ICmd obj) {
        // 动态切换
        if (null != obj && CmdKey.KEY_HOME_SWITCH.equals(obj.getKey())) {
            String route = obj.getObject();
            for (int i = 0; i < barTitles.length; i++) {
                if (TextUtils.equals(barTitles[i].router, route)) {
                    container.setCurrentItem(i, false);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        UnReadMessageManager.getInstance().removeObserver(this);
        super.onDestroy();
    }


    @Override
    public void onCountChanged(int count) {
        Logger.e(TAG, "onCountChanged: count= " + count);
        if (tabLayout != null && tabLayout.getTabAt(MSG_INDEX) != null) {
            if (count > 0) {
                BadgeDrawable badgeDrawable = tabLayout.getTabAt(MSG_INDEX).getOrCreateBadge();
                badgeDrawable.setBackgroundColor(getResources().getColor(R.color.color_main_unread_point));
                badgeDrawable.setVerticalOffset(UiUtils.dp2px(5));
            } else {
                tabLayout.getTabAt(MSG_INDEX).removeBadge();
            }
        }
    }
}