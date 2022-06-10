package cn.rc.community.channel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.ui.BaseFragment;
import com.basis.ui.UIStack;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.VRCenterDialog;
import com.basis.widget.interfaces.IWrapBar;

import java.util.Arrays;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelDetailsBean;
import cn.rc.community.bean.CommunityBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.channel.TextChannel.TextChannelFragment;
import cn.rc.community.channel.details.ChannelDetailsActivity;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.IUltraGroupChangeListener;
import cn.rc.community.helper.UltraGroupCenter;
import cn.rc.community.home.CommunitiesAdapter;
import cn.rongcloud.config.router.RouterPath;

/**
 * 超级群聊天界面
 */
public class ConversionActivity extends BaseActivity implements IUltraGroupChangeListener {

    public final static String CHANNEL_ID = "channelId";
    public final static String KET_TYPE = "conversion_type";
    private android.widget.RelativeLayout rlAuditId;
    private TextView btnAuditStatus;
    private TextView tv_id;

    /**
     * 进入默认频道
     *
     * @param activity
     * @param communityId
     */
    public static void openConversion(Activity activity, String communityId) {
        CommunityHelper.getInstance().getCommunityDetails(communityId, new IResultBack<CommunityDetailsBean>() {
            @Override
            public void onResult(CommunityDetailsBean detailsBean) {
                if (null != detailsBean) {
                    String joinChannelUid = detailsBean.getJoinChannelUid();
                    if (!TextUtils.isEmpty(joinChannelUid)) {
                        openChannel(activity, joinChannelUid);
                        //设置选中的ID
                        CommunitiesAdapter.setSelectedUid(detailsBean.getUid());
                    }
                } else {
                    KToast.show("该社区已经解散！");
                }
            }
        });
    }

    public static void openChannel(Activity activity, String channelUid) {
        Intent intent = new Intent(activity, ConversionActivity.class);
        intent.putExtra(CHANNEL_ID, channelUid);
        intent.putExtra(KET_TYPE, 0);
        activity.startActivity(intent);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_conversion;
    }

    private String conversionId;

    @Override
    public void init() {
        Intent i = getIntent();
        conversionId = i.getStringExtra(CHANNEL_ID);
        rlAuditId = (RelativeLayout) findViewById(R.id.rl_audit_id);
        btnAuditStatus = findViewById(R.id.btn_audit_status);
        tv_id = (TextView) findViewById(R.id.tv_id);
        initTitleBar();
        //监听社区信息
        CommunityHelper.communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean != null) {
                    changeView(communityDetailsBean);
                }
            }
        });
        //进入当前会话页面，获取频道详情，并且拿到当前频道的标注消息并监听
        CommunityHelper.getInstance().getChannelDetails(conversionId, new IResultBack<ChannelDetailsBean>() {
            @Override
            public void onResult(ChannelDetailsBean channelDetailsBean) {
                if (channelDetailsBean != null) {
                    int type = i.getIntExtra(KET_TYPE, 0);
                    initFragment(type);
                    getWrapBar().setTitleAndGravity(channelDetailsBean.getName(), Gravity.LEFT).work();
                    CommunityHelper.getInstance().getNewsMarkMessages();
                }
            }
        });
        UltraGroupCenter.getInstance().addIUltraGroupChangeListener(this);
    }


    private void initTitleBar() {
        getWrapBar()
                .addOptionMenu("", R.drawable.ic_more)
                .setOnMenuSelectedListener(new IWrapBar.OnMenuSelectedListener() {
                    @Override
                    public void onItemSelected(int position) {
                        UIKit.startActivityForResult(activity, ChannelDetailsActivity.class, Constants.markMessageRequestCode);
                    }
                }).work();
    }

    private BaseFragment currentFragment;

    public void initFragment(int type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        currentFragment = getFragmentByType(type);
        fragmentTransaction.replace(R.id.container, currentFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public BaseFragment getFragment() {
        return currentFragment;
    }

    protected BaseFragment getFragmentByType(int type) {
        if (0 == type) {
            return TextChannelFragment.getInstance();
        } else {
            return null;
        }
    }

    /**
     * 刷新底部view
     *
     * @param communityDetailsBean
     */
    private void changeView(CommunityDetailsBean communityDetailsBean) {
        CommunityDetailsBean.CommunityUserBean u = communityDetailsBean.getCommunityUser();
        tv_id.setText(ResUtil.getString(R.string.cmu_join_the_community_to_interact));
        int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
        if (auditStatus == Constants.AuditStatus.AUDITING.getCode()) {
            rlAuditId.setVisibility(View.VISIBLE);
            btnAuditStatus.setBackground(getResources().getDrawable(R.drawable.shape_sold_bg_blue_2dp_border));
            btnAuditStatus.setTextColor(Color.parseColor("#0099FF"));
            btnAuditStatus.setText(getResources().getString(R.string.cmu_auditing));
        } else if (auditStatus == Constants.AuditStatus.NOT_JOIN.getCode() || auditStatus == Constants.AuditStatus.AUDIT_FAILED.getCode()) {
            rlAuditId.setVisibility(View.VISIBLE);
            btnAuditStatus.setBackground(getResources().getDrawable(R.drawable.shape_sold_bg_blue));
            btnAuditStatus.setTextColor(Color.parseColor("#FFFFFF"));
            btnAuditStatus.setText(getResources().getString(R.string.cmu_join));
            btnAuditStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestJoin();
                }
            });
            if (auditStatus == Constants.AuditStatus.AUDIT_FAILED.getCode()) {
                tv_id.setText(ResUtil.getString(R.string.cmu_restart_join));
            }
        } else if (auditStatus == Constants.AuditStatus.JOINED.getCode()) {
            rlAuditId.setVisibility(View.GONE);
        }
        //只要是没有加入的社区，那么添加到浏览记录中
        if (auditStatus != Constants.AuditStatus.JOINED.getCode()) {
            CommunityBean communityBean = new CommunityBean(communityDetailsBean.getName(), communityDetailsBean.getUid());
            communityBean.setPortrait(communityDetailsBean.getPortrait());
            CommunityHelper.getInstance().addLastBrowseCommunityBean(communityBean);
        }
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


    @Override
    protected void onDestroy() {
        CommunityHelper.communityDetailsLiveData.removeObservers(this);
        UltraGroupCenter.getInstance().removeIUltraGroupChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onUltraGroupChanged(String targetId) {

    }

    @Override
    public void onChannelDeleted(String[] channelIds) {
        if (channelIds != null && Arrays.asList(channelIds).contains(CommunityHelper.getInstance().getChannelUid())) {
            //如果当前频道被删除了，那么给弹窗
            Dispatcher.get().dispatch(new Runnable() {
                @Override
                public void run() {
                    VRCenterDialog deleteDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                    deleteDialog.replaceContent("当前频道已被删除", "", null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //结束所有界面回到首页
                            ARouter.getInstance().build(RouterPath.ROUTER_MAIN).navigation();
                        }
                    }, null);
                    deleteDialog.show();
                }
            });
        }
    }

    @Override
    public void onUltraGroupDelete(String targetId) {

    }

}
