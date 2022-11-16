package cn.rc.community.setting.member;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.MemberBean;
import cn.rc.community.helper.CommunityHelper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/7
 * @time 6:16 下午
 * 社区成员列表
 */
public class MemberFragment extends BaseFragment {
    public static final String CMD_SEARCH = "search";
    private String title;
    private Constants.MemberType memberType;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvMemberList;
    private int pageSize = 20;
    private int pageNum = 1;
    private MemberAdapter adapter;
    private TotalMemberCountListener totalMemberCountListener;

    public MemberFragment(String title, Constants.MemberType memberType) {
        this.title = title;
        this.memberType = memberType;
        getData("", true);
    }

    @Override
    public int setLayoutId() {
        return R.layout.cmu_fragment_member_list;
    }

    @Override
    public void init() {
        initView();
        getData("", true);
    }


    @Override
    public String getTitle() {
        return title;
    }

    private void initView() {
        refreshLayout = getView(R.id.layout_refresh);
        rvMemberList = getView(R.id.rv_member_list);
        rvMemberList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MemberAdapter(getContext());
        rvMemberList.setAdapter(adapter);

        //设置刷新和加载
        // 下拉刷新和加载更多
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData("", true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getData("", false);
            }
        });
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
    }

    @Override
    public void onRefresh(ICmd obj) {
        super.onRefresh(obj);
        if (null != obj && CMD_SEARCH.equals(obj.getKey())) {
            String search = obj.getObject();
            if (!TextUtils.isEmpty(search)) {
                searchMember(search);
            }
        }
    }

    void searchMember(String search) {
        getData(search, true);
    }

    private void getData(String nickName, boolean isRefresh) {
        if (isRefresh) {
            pageNum = 1;//如果是刷新的话
        } else {
            pageNum++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("communityUid", CommunityHelper.getInstance().getCommunityDetailsBean().getUid());
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("selectType", memberType.getCode());
        if (!TextUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        OkApi.post(CommunityAPI.Community_User, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
                }
                if (result.ok()) {
                    MemberBean memberBean = result.get(MemberBean.class);
                    List<MemberBean.RecordsBean> records = memberBean.getRecords();
                    if (totalMemberCountListener != null) {
                        totalMemberCountListener.onTotalChange(memberBean.getTotal());
                    }
                    if (adapter == null) {
                        return;
                    }
                    if (isRefresh) {
                        adapter.setData(records, true);
                    } else {
                        adapter.getData().addAll(records);
                        adapter.notifyDataSetChanged();
                    }
                    //对20取余，如果大于0或者正好结果为0
                    if (records.size() % pageSize > 0 || records.size() == 0) {
                        refreshLayout.setEnableLoadMore(false);
                        refreshLayout.setNoMoreData(true);
                    } else {
                        refreshLayout.setEnableLoadMore(true);
                        refreshLayout.setNoMoreData(false);
                    }
                }
            }
        });
    }

    public void setTotalMemberCountListener(TotalMemberCountListener totalMemberCountListener) {
        this.totalMemberCountListener = totalMemberCountListener;
    }

    interface TotalMemberCountListener {
        void onTotalChange(int total);
    }
}
