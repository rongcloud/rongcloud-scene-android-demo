package cn.rc.community.home;

import static cn.rc.community.helper.CommunityHelper.communityDetailsLiveData;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.CommunityBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.IUltraGroupChangeListener;
import cn.rc.community.helper.IUltraGroupUserEventListener;
import cn.rc.community.helper.UltraGroupCenter;
import cn.rc.community.utils.UltraUnReadMessageManager;
import cn.rongcloud.config.router.RouterPath;

/**
 * 社区首页
 */
@Route(path = RouterPath.FRAGMENT_COMMUNITY)
public class CommunityFragment extends BaseFragment implements
        CommunitiesAdapter.OnItemSelectListener<CommunityBean>, IUltraGroupChangeListener,
        UltraUnReadMessageManager.IUnReadMessageObserver, IUltraGroupUserEventListener {

    @Override
    public int setLayoutId() {
        return R.layout.fragment_community;
    }

    private RecyclerView rcLeft;// rcRight;
    private CommunitiesAdapter leftAdapter;
    //已经选中的社区
//    private CommunityBean selectCommunity;

    @Override
    public void init() {
        UltraUnReadMessageManager.getInstance().addObserver(this);
        rcLeft = getView(R.id.rc_left);
        //社区列表
        rcLeft.setLayoutManager(new LinearLayoutManager(activity));
        leftAdapter = new CommunitiesAdapter(activity, this);
        rcLeft.setAdapter(leftAdapter);
        //获取社区列表 
        refreshCommunityList();
        CommunityHelper.getInstance().addIUltraGroupChangeListener(this);
        CommunityHelper.getInstance().addIUltraGroupUserEventListener(this);

        communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) {
                    CommunitiesAdapter.setSelectedUid("");
                    refreshCommunityList();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        UltraGroupCenter.getInstance().removeIUltraGroupChangeListener(this);
        super.onDestroy();
    }

    public final static String CMD_REFRESH_LIST = "refresh_community_list";

    @Override
    public void onRefresh(ICmd obj) {
        super.onRefresh(obj);
        if (null != obj && CMD_REFRESH_LIST.equals(obj.getKey())) {
            refreshCommunityList();
        }
    }

    /**
     * 刷新左侧列表
     */
    private void refreshCommunityList() {
        Map<String, Object> params = new HashMap<>();
        OkApi.post(CommunityAPI.Community_list, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<CommunityBean> records = result.getList("records", CommunityBean.class);
                    CommunityBean lastBrowseCommunityBean = CommunityHelper.getInstance().getLastBrowseCommunityBean();
                    //判读最后浏览过的社区是否已经加入了
                    if (lastBrowseCommunityBean != null) {
                        records.add(0, lastBrowseCommunityBean);
                    }
                    records = bindUnreadCount(records);
                    leftAdapter.setData(records, true);
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        refreshCommunityList();
    }

    private final static String DETAIL_TAG = "CommunityDetailsFragment";
    private final static String CREATE_TAG = "CommunityNoneFragment";


    @Override
    public void onSelected(CommunityBean communityBean) {
//        selectCommunity = communityBean;
        if (Constants.Add_Action.equals(communityBean.getCommunityUid())) {
            showCreateView(false);
            return;
        }
        if (leftAdapter != null) {
            int i = leftAdapter.getData().indexOf(communityBean);
            if (i > -1) {
                leftAdapter.selectItem(i);
            }
        }
        if (isAdded() && !isDetached()) {
            CommunityDetailsFragment fragment = (CommunityDetailsFragment) getChildFragmentManager().findFragmentByTag(DETAIL_TAG);
            if (null == fragment) {
                //创建 Fragment
                fragment = new CommunityDetailsFragment();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fl_container, fragment, DETAIL_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
            fragment.onRefresh(new RefreshCmd(CommunityDetailsFragment.CMD_REFRESH, communityBean.getCommunityUid()));
        }
    }


    @Override
    public void onCreateCommunity() {
        showCreateView(true);
    }

    public void showCreateView(boolean click) {
        if (!click) {
            if (isAdded()) {
                //创建选择fragment
                CommunityNoneFragment communityNoneFragment = new CommunityNoneFragment();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fl_container, communityNoneFragment, CREATE_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        } else {
            UIKit.startActivity(activity, CreateCommunityActivity.class);
        }
    }

    /**
     * 社区信息发生了改变
     *
     * @param targetId 社区ID
     */
    @Override
    public void onUltraGroupChanged(String targetId) {
        refreshCommunityList();
    }

    @Override
    public void onChannelDeleted(String[] channelIds) {

    }

    /**
     * 社区被解散了 1.如果是本人当前所在的社区，那么去掉选中
     * 2.如果不是本人当前所在的社区，保持选中
     *
     * @param targetId 社区ID
     */
    @Override
    public void onUltraGroupDelete(String targetId) {
        if (TextUtils.equals(targetId, CommunityHelper.getInstance().getCommunityUid())) {
            leftAdapter.setSelectedUid("");
        }
        //清除掉当前社区信息
        communityDetailsLiveData.postValue(null);
        refreshCommunityList();
    }

    @Override
    public void onChannelUnReadChanged(String targetId, String channelId, int count) {

    }

    private Map<String, Integer> unreadMap = new HashMap<>();

    @Override
    public void onUltraGroupUnReadChanged(String targetId, int count) {
        if (null != unreadMap) {
            unreadMap.remove(targetId);
            unreadMap.put(targetId, count);
        }
        bindUnreadCount(leftAdapter.getData());
        leftAdapter.notifyDataSetChanged();
    }

    private List<CommunityBean> bindUnreadCount(List<CommunityBean> items) {
        Logger.e(TAG, "unreadMap = " + GsonUtil.obj2Json(unreadMap));
        if (null != unreadMap && !unreadMap.isEmpty()) {
            int size = null == items ? 0 : items.size();
            String selectedUid = leftAdapter.getSelectedUid();
            for (int i = 0; i < size; i++) {
                CommunityBean item = items.get(i);
                String id = item.getCommunityUid();
                Integer count = unreadMap.get(id);
                item.setUnread(null == count ? 0 : count);
                if (TextUtils.equals(item.getCommunityUid(), selectedUid)) {
                    item.refreshLastUnReadCount();
                }
            }
        }
        return items;
    }

    /**
     * 当自己被踢出
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       踢人的提示
     */
    @Override
    public void onKickOut(String targetId, String fromUserId, String hint) {
        if (TextUtils.equals(targetId, CommunityHelper.getInstance().getCommunityUid())) {
            leftAdapter.setSelectedUid("");
            //清除掉当前的社区信息
            communityDetailsLiveData.postValue(null);
        }
        refreshCommunityList();
    }

    /**
     * 当自己加入某个社区,一种是自己不审核加入了，那么就应该跳到这个社区
     * 第二种是审核通过了
     * 第三种是创建成果
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       提示
     */
    @Override
    public void onJoined(String targetId, String fromUserId, String hint) {
        refreshCommunityList();
    }

    @Override
    public void onRejected(String targetId, String fromUserId, String hint) {

    }

    /**
     * 当自己离开了某个社区
     *
     * @param targetId   社区ID
     * @param fromUserId 操作人ID
     * @param hint
     */
    @Override
    public void onLeft(String targetId, String fromUserId, String hint) {
        if (TextUtils.equals(targetId, CommunityHelper.getInstance().getCommunityUid())) {
            leftAdapter.setSelectedUid("");
            //清除掉当前的社区信息
            communityDetailsLiveData.postValue(null);
        }
        refreshCommunityList();
    }

    @Override
    public void onRequestJoin(String targetId, String fromUserId, String hint) {

    }

    @Override
    public void onBeForbidden(String targetId, String fromUserId, String toUserId, String hint) {

    }

    @Override
    public void onCancelForbidden(String targetId, String fromUserId, String toUserId, String hint) {

    }

}
