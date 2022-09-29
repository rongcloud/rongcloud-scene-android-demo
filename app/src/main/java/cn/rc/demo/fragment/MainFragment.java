package cn.rc.demo.fragment;

import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.ui.PermissionFragment;
import com.basis.utils.KToast;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import cn.rc.demo.R;
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.UmengHelper;
import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;

@Route(path = RouterPath.FRAGMENT_HOME)
public class MainFragment extends PermissionFragment implements View.OnClickListener, ModuleHelper.OnModuleClickListener, UnReadMessageManager.IUnReadMessageObserver {
    private final static String TAG = "MainFragment";

    @Override
    public int setLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected String[] onCheckPermission() {
        return null;
    }


    @Override
    protected void onAccept(boolean accept) {
        if (accept) {
            checkAndRequestPermissions(LAUNCHER_PERMISSIONS, new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean aBoolean) {
                    if (aBoolean) initView();
                }
            });
        }
    }

    private View unread;
    private FrameLayout business;

    void initView() {
        unread = getView(R.id.tv_unread);
        getView(R.id.iv_message).setOnClickListener(this);

        business = getView(R.id.business);
        // 未读消息
        Conversation.ConversationType[] cs = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE};
        UnReadMessageManager.getInstance().addObserver(cs, this);
        // load module
        ModuleHelper.inflateView(business, this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_message == id) {
            RouteUtils.routeToSubConversationListActivity(activity, Conversation.ConversationType.PRIVATE, "消息");
        }
    }

    @Override
    public void onModuleClick(ModuleHelper.Module module) {
        String[] permissions;
        if (module.event == RcEvent.VoiceRoom || module.event == RcEvent.RadioRoom) {
            permissions = VOICE_PERMISSIONS;
        } else {
            if (Build.VERSION.SDK_INT >= 31) {
                permissions = CALL_PERMISSIONS_31;
            } else {
                permissions = CALL_PERMISSIONS;
            }
        }
        checkAndRequestPermissions(permissions, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (aBoolean) {
                    UmengHelper.get().event(module.event);
                    switch (module.event) {
                        case AudioCall:
                            if (MiniRoomManager.getInstance().isShowing()) {
                                KToast.show(R.string.text_please_exit_room);
                                return;
                            }
                            ARouter.getInstance().build(module.router).withBoolean("is_video", false).navigation();
                            break;
                        case VideoCall:
                            if (MiniRoomManager.getInstance().isShowing()) {
                                KToast.show(R.string.text_please_exit_room);
                                return;
                            }
                            ARouter.getInstance().build(module.router).withBoolean("is_video", true).navigation();
                            break;
                        default:
                            ARouter.getInstance().build(module.router).navigation();
                            break;
                    }

                } else {
                    KToast.show(getString(R.string.text_please_allow_permission));
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        UnReadMessageManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void onCountChanged(int count) {
        UIKit.setVisible(unread, count > 0);
    }
}
