package cn.rc.community.setting.manager;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.List;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.ListBean;
import cn.rc.community.helper.CommunityHelper;

/**
 * 社区设置- 管理 - 编辑资料 - 选择频道
 */
public class SelectChannelActivity extends BaseActivity {

    private int code;

    @Override
    public int setLayoutId() {
        return R.layout.activity_select_channel;
    }

    private SelectAdapter adapter;
    private RecyclerView rcChannel;

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_profile_select_channel)).work();
        rcChannel = getView(R.id.rc_channel);
        code = getIntent().getIntExtra("Code", -1);
        adapter = new SelectAdapter(activity, new IResultBack<ChannelBean>() {
            @Override
            public void onResult(ChannelBean channel) {
                //得到默认进入的频道
                CommunityDetailsBean clone = CommunityHelper.getInstance().getCommunityDetailsBean().clone();
                switch (code) {
                    case 10012:
                        clone.setJoinChannelUid(channel.uid);
                        break;
                    case 10013:
                        clone.setMsgChannelUid(channel.uid);
                        break;
                }
                clone.setUpdateType(Constants.UpdateType.UPDATE_TYPE_ALL.getUpdateTypeCode());
                CommunityHelper.getInstance().saveCommunityAll(clone, new IResultBack<Wrapper>() {
                    @Override
                    public void onResult(Wrapper wrapper) {
                        //设置成功
                        if (wrapper.ok()) {
                            finish();
                        } else {
                            KToast.show(wrapper.getMessage());
                        }
                    }
                });
            }
        });
        rcChannel.setAdapter(adapter);
        List<ListBean> data = new ArrayList<>();
        CommunityDetailsBean communityDetailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        data.addAll(communityDetailsBean.getChannelList());
        data.addAll(communityDetailsBean.getGroupList());
        adapter.setData(data, true);
    }

}

