package cn.rc.community.setting.member;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.ResUtil;
import com.basis.widget.SearchEditText;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

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
 * @date 2022/5/1
 * @time 18:41
 * 选择需要艾特的对象
 */
public class SelectMentionMemberActivity extends BaseActivity {
    private androidx.appcompat.widget.LinearLayoutCompat search;
    private com.basis.widget.SearchEditText etSearch;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout refreshLayout;
    private androidx.recyclerview.widget.RecyclerView rvMemberList;
    private MentionMemberAdapter adapter;

    private int pageSize = 20;
    private int pageNum = 1;
    private TextView tv_empty;

    @Override
    public int setLayoutId() {
        return R.layout.cmu_activity_select_mention_member;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_member)).work();
        //拿到当前社区的所有用户
        initView();
    }

    private void initView() {
        search = (LinearLayoutCompat) findViewById(R.id.search);
        etSearch = (SearchEditText) findViewById(R.id.et_search);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        rvMemberList = (RecyclerView) findViewById(R.id.rv_member_list);
        tv_empty = (TextView) findViewById(R.id.tv_empty);

        etSearch.setOnSearchListener(new SearchEditText.OnSearchListener() {
            @Override
            public void onSearch(String search) {
                if (TextUtils.isEmpty(search)) {
                    return;
                }
                etSearch.setText("");
                getData(search, true);
            }
        });

        rvMemberList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MentionMemberAdapter(this);
        rvMemberList.setAdapter(adapter);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getData("", false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData("", true);
            }
        });
        getData("", true);
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
        params.put("selectType", Constants.MemberType.ALL.getCode());
        if (!TextUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        OkApi.post(CommunityAPI.Community_User, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                if (result.ok()) {
                    MemberBean memberBean = result.get(MemberBean.class);
                    List<MemberBean.RecordsBean> records = memberBean.getRecords();
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
                tv_empty.setVisibility(adapter.getData().size() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }
}
