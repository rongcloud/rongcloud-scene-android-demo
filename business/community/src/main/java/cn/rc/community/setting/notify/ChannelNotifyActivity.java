package cn.rc.community.setting.notify;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;

import java.util.Arrays;
import java.util.List;

import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.UltraGroupNotificationLeaveManager;
import cn.rc.community.setting.CustomerBottomDialog;
import io.rong.imlib.IRongCoreEnum;

/**
 * 社区设置 - 通知 - 频道通知
 */
public class ChannelNotifyActivity extends BaseActivity {
    private final static List<String> ITEMS = Arrays.asList(
            NotificationsLeave.FOLLOW.getDes(),
            NotificationsLeave.All.getDes(),
            NotificationsLeave.ONLY.getDes(),
            NotificationsLeave.NONE.getDes());

    @Override
    public int setLayoutId() {
        return R.layout.activity_channel_notify;
    }

    RecyclerView rcGroup;
    GroupAdapter groupAdapter;

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_channel_notify_setting)).work();
        rcGroup = getView(R.id.rc_group);
        rcGroup.setLayoutManager(new LinearLayoutManager(activity));
        groupAdapter = new GroupAdapter(activity);
        List<GroupBean> groupList = CommunityHelper.getInstance().getCommunityDetailsBean().getGroupList();
        groupAdapter.setData(groupList, true);
        rcGroup.setAdapter(groupAdapter);
    }

    /**
     * 分组
     */
    public static class GroupAdapter extends RcyAdapter<GroupBean, RcyHolder> {

        public GroupAdapter(Context context) {
            super(context, R.layout.item_notify_setting_group);
        }

        @Override
        public int getItemLayoutId(GroupBean item, int position) {
            return R.layout.item_notify_setting_group;
        }

        @Override
        public void convert(RcyHolder holder, GroupBean data, int position, int layoutId) {
            holder.setText(R.id.group_name, data.name);
            RecyclerView channel = holder.getView(R.id.rc_channel);
            channel.setLayoutManager(new LinearLayoutManager(context));
            ChannelAdapter adapter = (ChannelAdapter) channel.getAdapter();
            if (null == adapter) {
                adapter = new ChannelAdapter(context);
                channel.setAdapter(adapter);
            }
            adapter.setData(data.getChannelList(), true);
        }
    }

    /**
     * 频道
     */
    public static class ChannelAdapter extends RcySAdapter<ChannelBean, RcyHolder> {

        public ChannelAdapter(Context context) {
            super(context, R.layout.item_notify_setting_channel);
        }

        @Override
        public void convert(RcyHolder holder, ChannelBean channelBean, int position) {
//            holder.setText(R.id.notify_setting, NotifyType.valued(channelBean.getNoticeType()).getDes());
            UltraGroupNotificationLeaveManager.get().getChannelNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), channelBean.getUid(), new IResultBack<IRongCoreEnum.PushNotificationLevel>() {
                @Override
                public void onResult(IRongCoreEnum.PushNotificationLevel level) {
                    holder.setText(R.id.notify_setting, NotificationsLeave.valued(level).getDes());
                }
            });
            holder.setText(R.id.info, channelBean.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomerBottomDialog((Activity) context, ITEMS)
                            .setOnItemClickListener(new CustomerBottomDialog.OnItemClickListener() {
                                @Override
                                public void onItemClick(int i) {
                                    String des = ITEMS.get(i);
                                    NotificationsLeave notificationsLeave = NotificationsLeave.valued(des);
                                    UltraGroupNotificationLeaveManager.get().setChannelNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), channelBean.getUid(), notificationsLeave.getLevel(), new IResultBack<Boolean>() {
                                        @Override
                                        public void onResult(Boolean aBoolean) {
                                            if (aBoolean) {
                                                KToast.show(ResUtil.getString(R.string.cmu_save_success));
                                                notifyItemChanged(position);
                                            }
                                        }
                                    });
//                                    NotifyType notifyType = NotifyType.valued(des);
//                                    updateNotify(channelBean, notifyType);
                                }
                            }).show();
                }
            });
        }

//        void updateNotify(ChannelBean channel, NotifyType notifyType) {
//            CommunityHelper.getInstance().updateChannelSetting(channel.getUid(), notifyType.getNoticeCode(), new IResultBack<Wrapper>() {
//                @Override
//                public void onResult(Wrapper result) {
//                    if (result.ok()) {
//                        channel.setNoticeType(notifyType.getNoticeCode());
//                        notifyDataSetChanged();
//                    }
//                }
//            });
//        }
    }
}
