package cn.rc.community.home;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.basis.ui.UIStack;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.VRCenterDialog;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;
import cn.rc.community.channel.ConversionActivity;
import cn.rc.community.channel.create.CreateChannelDialog;
import cn.rc.community.channel.editor.ChannelManagerDialog;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.setting.CommunitySettingDialog;
import cn.rc.community.utils.UltraUnReadMessageManager;
import cn.rc.community.weight.RoundRectangleView;

/**
 * 社区首页 -> 右侧详情
 */
public class CommunityDetailsFragment extends BaseFragment implements DetailsAdapter.OnDetailsClickListener, View.OnClickListener {
    public static final String CMD_REFRESH = "refresh_id";
    private ShapeableImageView ivCoverId;
    private Toolbar toolBarId;
    private TextView tvCommunityName;
    private TextView ivSetting;
    private RecyclerView rcCommunityId;
    private String targetId;
    private DetailsAdapter detailsAdapter;
    private ChannelManagerDialog channelManagerDialog;
    private TextView tvAuditStatusId;
    private CommunitySettingDialog communitySettingDialog;

    @Override
    public void onRefresh(ICmd obj) {
        super.onRefresh(obj);
        if (null != obj && CMD_REFRESH.equals(obj.getKey())) {
            targetId = obj.getObject();
            getCommunityDetails();
        }
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_community_details;
    }

    @Override
    public void init() {
        RoundRectangleView roundView = getView(R.id.roundView);
        roundView.setRoundRadius(10, 10, 0, 0);
        ivCoverId = getView(R.id.iv_cover_id);
        toolBarId = getView(R.id.tool_bar_id);
        tvCommunityName = getView(R.id.tv_community_name);
        ivSetting = getView(R.id.iv_setting);
        rcCommunityId = getView(R.id.rc_community_id);
        tvAuditStatusId = getView(R.id.tv_auditStatus_id);
        rcCommunityId.setLayoutManager(new LinearLayoutManager(activity));
        detailsAdapter = new DetailsAdapter(getContext(), this, targetId);
        rcCommunityId.setAdapter(detailsAdapter);
        UltraUnReadMessageManager.getInstance().addObserver(new UltraUnReadMessageManager.IUnReadMessageObserver() {
            @Override
            public void onChannelUnReadChanged(String targetId, String channelId, int count) {
                if (detailsAdapter != null && TextUtils.equals(targetId, CommunityDetailsFragment.this.targetId)) {
                    detailsAdapter.updateUnreadCount(channelId, count);
                }
            }

            @Override
            public void onUltraGroupUnReadChanged(String targetId, int count) {
                Log.e(TAG, "onUltraGroupUnReadChanged: ");
            }
        });

    }

    @Override
    public void initListener() {
        super.initListener();
        ivSetting.setOnClickListener(this::onClick);
        CommunityHelper.communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) return;
                tvCommunityName.setText(communityDetailsBean.getName());
                ImageLoader.loadUrl(ivCoverId, communityDetailsBean.getCoverUrl(), R.color.basis_green);
                ArrayList<ListBean> listBeans = new ArrayList<>();
                listBeans.addAll(communityDetailsBean.getChannelList());
                for (GroupBean groupBean : communityDetailsBean.getGroupList()) {
                    groupBean.setExpansion(true);
                }
                listBeans.addAll(communityDetailsBean.getGroupList());
                //给适配器绑定新的目标ID
                detailsAdapter.setTargetId(communityDetailsBean.getUid());
                detailsAdapter.setData(listBeans, true);
                CommunityDetailsBean.CommunityUserBean u = communityDetailsBean.getCommunityUser();
                int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
                changeAuditStatusView(auditStatus);
            }
        });
    }

    /**
     * 当前用户在社区的状态
     *
     * @param auditStatus
     */
    private void changeAuditStatusView(int auditStatus) {
        if (auditStatus == Constants.AuditStatus.JOINED.getCode()) {
            ivSetting.setText("");
            ivSetting.setCompoundDrawablesRelativeWithIntrinsicBounds(ResUtil.getDrawable(R.drawable.cmu_setting), null, null, null);
        } else {
            ivSetting.setText(ResUtil.getString(R.string.cmu_message_remove));
            ivSetting.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }
        tvAuditStatusId.setVisibility(View.VISIBLE);
        if (auditStatus == Constants.AuditStatus.AUDITING.getCode()) {
            tvAuditStatusId.setText(getResources().getString(R.string.cmu_auditing));
        } else if (auditStatus == Constants.AuditStatus.NOT_JOIN.getCode() || auditStatus == Constants.AuditStatus.AUDIT_FAILED.getCode()) {
            tvAuditStatusId.setText(getResources().getString(R.string.cmu_join));
            tvAuditStatusId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestJoin();
                }
            });
        } else if (auditStatus == Constants.AuditStatus.JOINED.getCode()) {
            tvAuditStatusId.setVisibility(View.GONE);
        }
//        else if (auditStatus == Constants.AuditStatus.AUDIT_FAILED.getCode()) {
//            tvAuditStatusId.setText(getResources().getString(R.string.cmu_refused));
//        }

    }

    /**
     * 申请加入社区
     */
    void requestJoin() {
        String communityUid = CommunityHelper.getInstance().getCommunityUid();
        OkApi.post(CommunityAPI.Community_Join + communityUid, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                //如果不需要审核，那么是直接加入成功的
                if (result.ok() && result.getBody() != null) {
                    int asInt = result.getBody().getAsInt();
                    CommunityDetailsBean communityDetailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
                    communityDetailsBean.getCommunityUser().setAuditStatus(asInt);
                    CommunityHelper.communityDetailsLiveData.postValue(communityDetailsBean);
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }

    /**
     * 获取社区的详情
     */
    private void getCommunityDetails() {
        CommunityHelper.getInstance().getCommunityDetails(targetId, null);
    }

    @Override
    public void onAddChannel(GroupBean groupBean) {
        if (CommunityHelper.getInstance().getCommunityDetailsBean() == null) {
            KToast.show("当前社区不存在，无法创建频道");
        }
        CreateChannelDialog createChannelDialog = new CreateChannelDialog(activity, groupBean, null);
        createChannelDialog.show();
    }

    @Override
    public void onEditorChannel(boolean group) {
        channelManagerDialog = new ChannelManagerDialog(activity, group);
        channelManagerDialog.setGroupDetails(detailsAdapter.getData());
        channelManagerDialog.show();
    }

    @Override
    public void jumpChannel(ChannelBean channel) {
        ConversionActivity.openChannel(activity, channel.getUid());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_setting) {
            CommunityDetailsBean.CommunityUserBean u = CommunityHelper.getInstance().getCommunityUserBean();
            int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
            if (auditStatus == Constants.AuditStatus.JOINED.getCode()) {
                if (communitySettingDialog == null) {
                    communitySettingDialog = new CommunitySettingDialog(this, activity, new IResultBack<CommunitySettingDialog.Refresh>() {
                        @Override
                        public void onResult(CommunitySettingDialog.Refresh refresh) {
                            Logger.e(TAG, "refresh = " + refresh);
                            if (CommunitySettingDialog.Refresh.details == refresh) {
                            } else if (CommunitySettingDialog.Refresh.list == refresh) {
                                Fragment fragment = getParentFragment();
                                Logger.e(TAG, "fragment = " + fragment.getClass().getSimpleName());
                                if (fragment instanceof BaseFragment) {
                                    ((BaseFragment) fragment).onRefresh(new RefreshCmd(CommunityFragment.CMD_REFRESH_LIST, null));
                                }
                            }
                        }
                    });
                }
                communitySettingDialog.show();
            } else {
                VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                confirmDialog.replaceContent("是否移除掉该社区?", ResUtil.getString(R.string.rc_cancel), null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommunityHelper.getInstance().clearBrowsingHistory();
                        CommunityHelper.communityDetailsLiveData.postValue(null);
                    }
                }, null);
                confirmDialog.show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
