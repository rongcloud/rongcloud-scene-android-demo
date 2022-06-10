package cn.rc.community.setting.manager;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;

import java.util.Arrays;
import java.util.List;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.helper.CommunityHelper;

/**
 * 设置-管理-成员验证
 */
public class MemberVerifyActivity extends BaseActivity {

    private RecyclerView rc_checkbox;
    private NotifyAdapter adapter;
    private static CommunityDetailsBean communityDetailsBean;

    @Override
    public int setLayoutId() {
        return R.layout.activity_member_verify;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_member_verify)).work();
        rc_checkbox = getView(R.id.rc_checkbox);
        adapter = new NotifyAdapter(activity);
        rc_checkbox.setAdapter(adapter);
        communityDetailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        List<Verify> data = Arrays.asList(
                new Verify(ResUtil.getString(R.string.cmu_not_need_audit), false, communityDetailsBean.getNeedAudit()
                        == Constants.NeedAuditType.NOT_NEED_AUDIT_TYPE.getNeedAuditCode() ? true : false),
                new Verify(ResUtil.getString(R.string.cmu_need_audit), true, communityDetailsBean.getNeedAudit()
                        == Constants.NeedAuditType.NEED_AUDIT_TYPE.getNeedAuditCode() ? true : false));
        adapter.setData(data, true);
    }


    public static class NotifyAdapter extends RcySAdapter<Verify, RcyHolder> {

        public NotifyAdapter(Context context) {
            super(context, R.layout.item_notify_community);
        }

        @Override
        public void convert(RcyHolder holder, Verify s, int position) {
            holder.setText(R.id.tv_notify, s.des);
            holder.itemView.setSelected(s.isSelect);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityDetailsBean temp = CommunityHelper.getInstance().getCommunityDetailsBean().clone();
                    temp.setUpdateType(Constants.UpdateType.UPDATE_TYPE_ALL.getUpdateTypeCode());
                    temp.setNeedAudit(s.verify ? Constants.NeedAuditType.NEED_AUDIT_TYPE.getNeedAuditCode()
                            : Constants.NeedAuditType.NOT_NEED_AUDIT_TYPE.getNeedAuditCode());
                    CommunityHelper.getInstance().saveCommunityAll(temp, new IResultBack<Wrapper>() {
                        @Override
                        public void onResult(Wrapper wrapper) {
                            if (wrapper.ok()) {
                                for (Verify verify : getData()) {
                                    verify.isSelect = verify.des.equals(s.des) ? true : false;
                                }
                                notifyDataSetChanged();
                            } else {
                                KToast.show(wrapper.getMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    public static class Verify {
        String des;
        //是否需要审核
        boolean verify;
        //是否被选中 
        boolean isSelect;

        public Verify(String des, boolean verify, boolean isSelect) {
            this.des = des;
            this.verify = verify;
            this.isSelect = isSelect;
        }
    }
}
