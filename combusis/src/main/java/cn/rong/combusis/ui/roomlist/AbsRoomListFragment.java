package cn.rong.combusis.ui.roomlist;

import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.rongcloud.common.utils.AccountStore;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rong.combusis.R;
import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.common.ui.dialog.InputPasswordDialog;
import cn.rong.combusis.intent.IntentWrap;
import cn.rong.combusis.provider.voiceroom.RoomType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.ui.OnItemClickRoomListListener;
import cn.rong.combusis.ui.room.widget.RecyclerViewAtVP2;
import cn.rong.combusis.widget.miniroom.MiniRoomManager;
import io.rong.imkit.picture.tools.ToastUtils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author gyn
 * @date 2021/9/15
 */
public abstract class AbsRoomListFragment extends BaseFragment
        implements OnItemClickRoomListListener<VoiceRoomBean>, CreateRoomDialog.CreateRoomCallBack {

    private RoomListAdapter mAdapter;
    private RecyclerViewAtVP2 mRoomList;
    private CreateRoomDialog mCreateRoomDialog;
    private ConfirmDialog confirmDialog;
    private SmartRefreshLayout refreshLayout;
    private View emptyView;
    private InputPasswordDialog inputPasswordDialog;

    private ActivityResultLauncher mLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result != null
                                && result.getData() != null
                                && result.getData().getData() != null
                                && mCreateRoomDialog != null) {
                            mCreateRoomDialog.setCoverUri(result.getData().getData());
                        }
                    });

    @Override
    public void init() {
        mRoomList = (RecyclerViewAtVP2) getView(R.id.xrv_room);
        refreshLayout = (SmartRefreshLayout) getView(R.id.layout_refresh);
        emptyView = (View) getView(R.id.layout_empty);
        getView(R.id.iv_create_room).setOnClickListener(v -> {
            createRoom();
        });
        mAdapter = new RoomListAdapter(getContext(), R.layout.item_room);
        mAdapter.setOnItemClickListener(this);
        mRoomList.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            loadRoomList(true);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            loadRoomList(false);
        });
        emptyView.setOnClickListener(v -> {
            loadRoomList(true);
        });
        checkUserRoom();
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_room_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoomList(true);
    }

    public abstract RoomType getRoomType();

    /**
     * 请求房间列表数据
     *
     * @param isRefresh 是否是刷新，否则是加载更多
     */
    private void loadRoomList(boolean isRefresh) {
        if (isRefresh) {
            refreshLayout.resetNoMoreData();
        }
        VoiceRoomProvider.provider()
                .loadPage(
                        isRefresh,
                        getRoomType(),
                        voiceRoomBeans -> {
                            mAdapter.setData(voiceRoomBeans, isRefresh);

                            if (VoiceRoomProvider.provider().getPage() <= 2) {
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadMore();
                            }

                            if (voiceRoomBeans != null && !voiceRoomBeans.isEmpty()) {
                                emptyView.setVisibility(View.GONE);
                            } else {
                                refreshLayout.setNoMoreData(true);
                                if (VoiceRoomProvider.provider().getPage() == 1) {
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
    }

    @Override
    public void onCreateSuccess(VoiceRoomBean voiceRoomBean) {
        mAdapter.getData().add(0, voiceRoomBean);
        mAdapter.notifyItemInserted(0);
        clickItem(voiceRoomBean, 0, true, Arrays.asList(voiceRoomBean));
    }

    @Override
    public void onCreateExist(VoiceRoomBean voiceRoomBean) {
        new ConfirmDialog(
                requireContext(),
                getString(R.string.text_you_have_created_room),
                true,
                "确定",
                "取消",
                () -> null,
                () -> {
                    jumpRoom(voiceRoomBean);
                    return null;
                })
                .show();
    }

    private void createRoom() {
        showLoading("");
        // 创建之前检查是否已有创建的房间
        OkApi.put(
                VRApi.ROOM_CREATE_CHECK,
                null,
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        dismissLoading();
                        if (result.ok()) {
                            showCreateRoomDialog();
                        } else if (result.getCode() == 30016) {
                            VoiceRoomBean voiceRoomBean = result.get(VoiceRoomBean.class);
                            if (voiceRoomBean != null) {
                                onCreateExist(voiceRoomBean);
                            } else {
                                showCreateRoomDialog();
                            }
                        }
                    }
                });
    }

    /**
     * 展示创建房间弹窗
     */
    private void showCreateRoomDialog() {
        if (getRoomType() != RoomType.LIVE_ROOM) {
            mCreateRoomDialog =
                    new CreateRoomDialog(
                            requireActivity(),
                            mLauncher,
                            getRoomType(),
                            AbsRoomListFragment.this);
            mCreateRoomDialog.show();
        } else {
            //如果是直播房，是直接进入直播间界面的
            ArrayList list = new ArrayList();
            list.add("-1");
            launchRoomActivity("", list, 0, true);
        }
    }

    @Override
    public void clickItem(
            VoiceRoomBean item,
            int position,
            boolean isCreate,
            List<VoiceRoomBean> voiceRoomBeans) {
        if (TextUtils.equals(item.getUserId(), AccountStore.INSTANCE.getUserId())) {
            ArrayList list = new ArrayList();
            list.add(item.getRoomId());
            launchRoomActivity(item.getRoomId(), list, 0, isCreate);
        } else if (item.isPrivate()) {
            inputPasswordDialog =
                    new InputPasswordDialog(requireContext(), false, () -> null,
                            s -> {
                                if (TextUtils.isEmpty(s)) {
                                    return null;
                                }
                                if (s.length() < 4) {
                                    ToastUtils.s(requireContext(), requireContext().getString(cn.rong.combusis.R.string.text_please_input_four_number));
                                    return null;
                                }
                                if (TextUtils.equals(s, item.getPassword())) {
                                    inputPasswordDialog.dismiss();
                                    ArrayList list = new ArrayList();
                                    list.add(item.getRoomId());
                                    launchRoomActivity(item.getRoomId(), list, 0, false);
                                } else {
                                    showToast("密码错误");
                                }
                                return null;
                            });
            inputPasswordDialog.show();
        } else {
            ArrayList<String> list = new ArrayList<>();
            for (VoiceRoomBean voiceRoomBean : voiceRoomBeans) {
                if (!voiceRoomBean.getCreateUserId().equals(AccountStore.INSTANCE.getUserId())
                        && !voiceRoomBean.isPrivate()) {
                    // 过滤掉上锁的房间和自己创建的房间
                    list.add(voiceRoomBean.getRoomId());
                }
            }
            int p = list.indexOf(item.getRoomId());
            if (p < 0) p = 0;
            launchRoomActivity(item.getRoomId(), list, p, false);
        }
    }

    private void launchRoomActivity(
            String roomId, ArrayList<String> roomIds, int position, boolean isCreate) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(roomId, () -> {
            IntentWrap.launchRoom(requireContext(), getRoomType(), roomIds, position, isCreate);
        });
    }

    /**
     * 检查用户之前是否在某个房间内
     */
    private void checkUserRoom() {
        if (MiniRoomManager.getInstance().isShowing()) {
            // 如果有小窗口存在的情况下，不显示
            return;
        }
        Map<String, Object> params = new HashMap<>(2);
        OkApi.get(VRApi.USER_ROOM_CHECK, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    VoiceRoomBean voiceRoomBean = result.get(VoiceRoomBean.class);
                    if (voiceRoomBean != null) {
                        // 说明已经在房间内了，那么给弹窗
                        confirmDialog = new ConfirmDialog(requireActivity(),
                                "您正在直播的房间中\n是否返回？", true, "确定", "取消",
                                new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        changeUserRoom();
                                        return null;
                                    }
                                },
                                new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        jumpRoom(voiceRoomBean);
                                        return null;
                                    }
                                });
                        confirmDialog.show();
                    }
                }
            }
        });
    }

    /**
     * 跳转到相应的房间
     *
     * @param voiceRoomBean
     */
    private void jumpRoom(VoiceRoomBean voiceRoomBean) {
        IntentWrap.launchRoom(requireContext(), voiceRoomBean.getRoomType(), voiceRoomBean.getRoomId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VoiceRoomProvider.provider().clear();
    }

    // 更改所属房间
    private void changeUserRoom() {
        HashMap<String, Object> params = new OkParams().add("roomId", "").build();
        OkApi.get(VRApi.USER_ROOM_CHANGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
            }
        });
    }
}
