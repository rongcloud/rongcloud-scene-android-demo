package cn.rongcloud.roomkit.ui.friend;


import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.ui.friend.model.Friend;

public class FriendListFragment extends BaseFragment implements FriendAdapter.OnFollowClickListener {
    private FriendAdapter mAdapter;
    private int mType = 2;// 1 我关注的 2 我的粉丝
    private SendPrivateMessageFragment sendPrivateMessageFragment;
    private RecyclerView mFriendRecyclerView;
    private SmartRefreshLayout refreshLayout;
    private TextView emptyView;
    private int page = 1;

    public static FriendListFragment getInstance() {
        return new FriendListFragment();
    }

    @Override
    public void init() {
        RadioGroup radioGroup = getView().findViewById(R.id.rg_friend);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_follower) {
                mType = 2;
                mAdapter.setType(mType);
                loadData(true);
            } else if (checkedId == R.id.rb_follow) {
                mType = 1;
                mAdapter.setType(mType);
                loadData(true);
            }
        });
        mFriendRecyclerView =  getView(R.id.rv_friend);
        refreshLayout = (SmartRefreshLayout) getView(R.id.layout_refresh);
        emptyView = (TextView) getView(R.id.tv_empty);
        mAdapter = new FriendAdapter(getContext(), R.layout.item_friend);
        mAdapter.setType(mType);
        mAdapter.setOnFollowClickListener(this);
        mFriendRecyclerView.setAdapter(mAdapter);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData(true);
            }
        });

        sendPrivateMessageFragment = new SendPrivateMessageFragment();
    }


    private void loadData(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        }
        Map<String, Object> params = new HashMap<>(8);
        params.put("type", mType);
        params.put("page", page);
        params.put("size", 10);
        OkApi.get(VRApi.FOLLOW_LIST, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<Friend> friends = result.getList("list", Friend.class);
                if (friends != null && !friends.isEmpty()) {
                    mAdapter.setData(friends, isRefresh);
                    if (page == 1) {
                        refreshLayout.finishRefresh();
                    } else {
                        refreshLayout.finishLoadMore();
                    }
                    emptyView.setVisibility(View.GONE);
                    page++;
                } else {
                    refreshLayout.setNoMoreData(true);
                    if (page == 1) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(true);
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_friend_list;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void clickFollow(Friend friend) {
        Friend.FollowStatus status = friend.getFollowStatus(mType);
        friend.changeFollowStatus(mType);
        mAdapter.notifyDataSetChanged();

        OkApi.get(VRApi.followUrl(friend.getUid()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (!result.ok()) {
                    friend.setFollowStatus(status);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int code, String msg) {
                friend.setFollowStatus(status);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void clickItem(Friend friend) {
        sendPrivateMessageFragment.showDialog(getChildFragmentManager(), friend);
    }

}
