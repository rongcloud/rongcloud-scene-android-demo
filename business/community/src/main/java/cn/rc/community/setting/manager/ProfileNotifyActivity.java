package cn.rc.community.setting.manager;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;

import java.util.Arrays;
import java.util.List;

import cn.rc.community.R;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.UltraGroupNotificationLeaveManager;
import cn.rc.community.setting.notify.NotificationsLeave;
import io.rong.imlib.IRongCoreEnum;

/**
 * 设置-管理-成员验证
 */
public class ProfileNotifyActivity extends BaseActivity {

    private static List<NotificationsLeave> data = Arrays.asList(
            NotificationsLeave.All,
            NotificationsLeave.ONLY);
    private RecyclerView rc_checkbox;
    private NotifyAdapter adapter;
    private NotificationsLeave current;

    @Override
    public int setLayoutId() {
        return R.layout.activity_profile_notify;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_profile_default_notify)).work();
        rc_checkbox = getView(R.id.rc_checkbox);
        UltraGroupNotificationLeaveManager.get().getUltraGroupConversationDefaultNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), new IResultBack<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onResult(IRongCoreEnum.PushNotificationLevel level) {
                //得到默认级别
                current = NotificationsLeave.valued(level);
                adapter = new NotifyAdapter(activity);
                rc_checkbox.setAdapter(adapter);
                adapter.setData(data, true);
            }
        });
    }

    public class NotifyAdapter extends RcySAdapter<NotificationsLeave, RcyHolder> {

        public NotifyAdapter(Context context) {
            super(context, R.layout.item_notify_community);
//            current = NotifyType.valued(CommunityHelper.getInstance().getCommunityDetailsBean().getNoticeType());
        }

        @Override
        public void convert(RcyHolder holder, NotificationsLeave s, int position) {
            holder.setText(R.id.tv_notify, s.getDes());
            holder.itemView.setSelected(null != current && current.getLevel() == s.getLevel());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.itemView.isSelected()) {
//                        CommunityDetailsBean clone = CommunityHelper.getInstance().getCommunityDetailsBean().clone();
//                        clone.setUpdateType(Constants.UpdateType.UPDATE_TYPE_ALL.getUpdateTypeCode());
//                        clone.setNoticeType(s.getNoticeCode());
//                        CommunityHelper.getInstance().saveCommunityAll(clone, new IResultBack<Wrapper>() {
//                            @Override
//                            public void onResult(Wrapper wrapper) {
//                                if (wrapper.ok()) {
//                                    holder.itemView.setSelected(true);
//                                    current = s;
//                                    notifyDataSetChanged();
//                                } else {
//                                    KToast.show(wrapper.getMessage());
//                                }
//                            }
//                        });
                        UltraGroupNotificationLeaveManager.get().setUltraGroupConversationDefaultNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), s.getLevel(), new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (aBoolean) {
                                    current = s;
                                    KToast.show(ResUtil.getString(R.string.cmu_save_success));
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
