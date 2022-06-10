package cn.rc.community.find;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.net.oklib.wrapper.interfaces.IPage;
import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.UiUtils;
import com.basis.widget.loading.LoadTag;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.R;
import cn.rc.community.bean.CommunityBean;
import cn.rc.community.channel.ConversionActivity;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.picture.decoration.GridSpacingItemDecoration;

@Route(path = RouterPath.FRAGMENT_FIND)
public class FindFragment extends BaseFragment {

    private RecyclerView ryCommunity;
    FindAdapter adapter;
    private SmartRefreshLayout refreshLayout;
    private View emptyView;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    public void init() {
        refreshLayout = getView(R.id.refresh);
        ryCommunity = getView(R.id.ry_community);
        emptyView = getView(R.id.tv_empty);
        ryCommunity.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(
                ((GridLayoutManager) ryCommunity.getLayoutManager()).getSpanCount(), UiUtils.dp2px(9), true);
        ryCommunity.addItemDecoration(itemDecoration);
        adapter = new FindAdapter(activity);
        ryCommunity.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getCommunity(false, true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getCommunity(false, false);
            }
        });
//        refreshLayout.autoRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }

    private int pageCount = 1;

    private void getCommunity(boolean show, boolean refresh) {
        Logger.e(TAG, "show = " + show + " refresh = " + refresh);
        pageCount++;
        if (refresh) pageCount = 1;
        Map<String, Object> params = new HashMap<>(4);
        params.put("pageNum", pageCount);
        params.put("pageSize", 10);
        LoadTag finalTag = show ? new LoadTag(activity, ResUtil.getString(R.string.basis_loading)) : null;
        OkApi.post(CommunityAPI.Community_find, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != finalTag) finalTag.dismiss();
                List<CommunityBean> communitys = result.getList("records", CommunityBean.class);
                if (null != adapter) adapter.setData(communitys, refresh);
                IPage page = result.getPage();
                if (null != refreshLayout) {
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                    if (null != page) {
                        refreshLayout.setEnableLoadMore(page.getPage() < page.getTotal());
                    }
                }
                emptyView.setVisibility(adapter.getData().size() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    public class FindAdapter extends RcySAdapter<CommunityBean, RcyHolder> {
        public FindAdapter(Context context) {
            super(context, R.layout.item_find_community);
        }

        @Override
        public void convert(RcyHolder holder, CommunityBean item, int position) {
            holder.setText(R.id.tv_community_name, item.getName());
            holder.setText(R.id.tv_remark, item.getRemark());
            holder.setText(R.id.tv_count, item.getPersonCount() + "人");
            ImageView portrait = holder.getView(R.id.iv_portrait_id);
            ImageView ivBackGroup = holder.getView(R.id.iv_background);
            ImageLoader.loadUrl(portrait, item.getPortrait(), R.color.app_color_white);
            ImageLoader.loadUrl(ivBackGroup, item.getCoverUrl(), R.color.basis_green);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConversionActivity.openConversion(activity, item.getCommunityUid());
                }
            });
        }
    }
}
