package cn.rc.community.setting.notify;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.ui.BaseActivity;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.Arrays;

import cn.rc.community.R;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.UltraGroupNotificationLeaveManager;
import io.rong.imlib.IRongCoreEnum;

/**
 * 社区设置 - 通知
 */
public class NotifyActivity extends BaseActivity implements View.OnClickListener {

    private NotificationsLeave level = NotificationsLeave.FOLLOW;

    @Override
    public int setLayoutId() {
        return R.layout.activity_notify;
    }

    private RecyclerView rc_checkbox;
    NotifyAdapter adapter;

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_notify_setting)).work();
        getView(R.id.channel_notify).setOnClickListener(this);
        rc_checkbox = getView(R.id.rc_checkbox);
        UltraGroupNotificationLeaveManager.get().getUltraGroupNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), new IResultBack<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onResult(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                Log.e("TAG", "onResult: ");
                level = NotificationsLeave.valued(pushNotificationLevel);
                adapter = new NotifyAdapter(activity);
                rc_checkbox.setAdapter(adapter);
                adapter.setData(Arrays.asList(NotificationsLeave.All, NotificationsLeave.ONLY, NotificationsLeave.NONE), true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.channel_notify == id) {
            String des = null == level ? NotificationsLeave.All.getDes() : level.getDes();
            UIKit.startActivityByBasis(activity, ChannelNotifyActivity.class, des);
        }
    }

    public class NotifyAdapter extends RcySAdapter<NotificationsLeave, RcyHolder> {

        //private NotifyType current;

        public NotifyAdapter(Context context) {
            super(context, R.layout.item_notify_community);
            //获取当前的免打扰级别

//            current = NotifyType.valued(CommunityHelper.getInstance().getCommunityUserBean().getNoticeType());
        }

        @Override
        public void convert(RcyHolder holder, NotificationsLeave notificationsLeave, int position) {
            //当前通知模式
            holder.setText(R.id.tv_notify, notificationsLeave.getDes());
            holder.itemView.setSelected(notificationsLeave.getLevel() == level.getLevel());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.itemView.isSelected()) {
                        holder.itemView.setSelected(true);
                        level = notificationsLeave;
                        UltraGroupNotificationLeaveManager.get().setUltraGroupNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), notificationsLeave.getLevel());
                        notifyDataSetChanged();

//                        updateNotify(notifyType);
                    }
                }
            });
        }

        void updateNotify(NotificationsLeave notificationsLeave) {
//            CommunityHelper.getInstance().updateUserSetting(UserManager.get().getUserId(), "noticeType", current.ordinal() + "", new IResultBack<Wrapper>() {
//                @Override
//                public void onResult(Wrapper result) {
//                    if (result.ok()) {
//                        current = notifyType;
//                        //更新当前社区的通知模式
//                        CommunityHelper.getInstance().getCommunityUserBean().setNoticeType(notifyType.getNoticeCode());
////                        UltraGroupNotificationManager.get().setUltraGroupNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), notifyType);
//                        notifyDataSetChanged();
//                    } else {
//                        KToast.show(result.getMessage());
//                    }
//                }
//            });
        }
    }
}
