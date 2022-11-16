package cn.rongcloud.gameroom.ui.gamelist;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.mvp.BaseMvpFragment;
import com.basis.ui.mvp.BasePresenter;
import com.basis.utils.UiUtils;
import com.basis.widget.decoration.SpaceItemDecoration;
import com.basis.widget.dialog.VRCenterDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.FilterOption;
import cn.rongcloud.gameroom.model.GameCreateBean;
import cn.rongcloud.gameroom.model.GameRoomBean;
import cn.rongcloud.gameroom.model.GameRoomListBean;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2022/5/5
 */
public class GameListFragment extends BaseMvpFragment implements GameFilterDialog.OnFilterOptionListener {

    private RecyclerView rvGameList;
    private CheckBox cbGender;
    private CheckBox cbGame;
    private RecyclerView rvRoomList;
    private SmartRefreshLayout refreshLayout;
    private AppBarLayout appBarLayout;

    private GameFilterDialog genderFilterDialog;
    private GameFilterDialog gameFilterDialog;
    private CreateGameRoomDialog createGameRoomDialog;
    private VRCenterDialog confirmDialog;
    private InputPasswordDialog inputPasswordDialog;

    private GameAdapter gameAdapter;
    private GameRoomAdapter roomAdapter;

    private boolean isRefresh = true;

    private int appbarVerticalOffset = 0;
    private GameListViewModel gameListViewModel;
    private GameRoomListViewModel gameRoomListViewModel;

    public static Fragment getInstance() {
        return new GameListFragment();
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_game_list;
    }

    @Override
    public void init() {
        gameListViewModel = new ViewModelProvider(this).get(GameListViewModel.class);
        gameRoomListViewModel = new ViewModelProvider(this).get(GameRoomListViewModel.class);
        getView(R.id.iv_create_room).setOnClickListener(v -> {
            gameRoomListViewModel.createRoom();
        });
        rvGameList = (RecyclerView) getView(R.id.rv_game_list);
        cbGender = (CheckBox) getView(R.id.cb_gender);
        cbGame = (CheckBox) getView(R.id.cb_game);
        rvRoomList = (RecyclerView) getView(R.id.rv_room_list);
        // 游戏列表
        gameAdapter = new GameAdapter();
        rvGameList.setAdapter(gameAdapter);
        int space = UiUtils.dp2px(14);
        rvGameList.addItemDecoration(SpaceItemDecoration.Builder.start().setStartSpace(space).setEndSpace(space).build());
        gameAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                gameRoomListViewModel.fastJoin(gameAdapter.getItem(position));
            }
        });
        // 房间列表
        roomAdapter = new GameRoomAdapter();
        roomAdapter.getLoadMoreModule().setAutoLoadMore(true);
        roomAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadRoomList(false);
            }
        });
        rvRoomList.setAdapter(roomAdapter);
        roomAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                clickItem(roomAdapter.getItem(position), false);
            }
        });
        // 下拉刷新
        refreshLayout = (SmartRefreshLayout) getView(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadRoomList(true);
            }
        });

        appBarLayout = (AppBarLayout) getView(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            appbarVerticalOffset = Math.abs(verticalOffset);
        });


        // 性别筛选
        AppBarLayout.OnOffsetChangedListener genderListener = (appBarLayout, verticalOffset) -> {
            appbarVerticalOffset = Math.abs(verticalOffset);
            if (appbarVerticalOffset == appBarLayout.getTotalScrollRange()) {
                // cbGender.setChecked(!cbGender.isChecked());
                genderFilterDialog.show(cbGender);
                cbGame.setClickable(false);
            }
        };
        genderFilterDialog = new GameFilterDialog(getContext(), 0, gameListViewModel.getGenderOptionList(), true, this);
        genderFilterDialog.setOnDismissListener(() -> {
            cbGender.setChecked(false);
            cbGame.setClickable(true);
            appBarLayout.removeOnOffsetChangedListener(genderListener);
        });
        cbGender.setOnClickListener(v -> {
            if (cbGender.isChecked() && !cbGame.isChecked()) {
                appBarLayout.addOnOffsetChangedListener(genderListener);
                // appbar不是收起状态要先收起，再展开弹框
                if (appbarVerticalOffset == appBarLayout.getTotalScrollRange()) {
                    genderFilterDialog.show(cbGender);
                    cbGame.setClickable(false);
                } else {
                    appBarLayout.setExpanded(false, true);
                }
            }
        });
        // 游戏筛选
        AppBarLayout.OnOffsetChangedListener gameListener = (appBarLayout, verticalOffset) -> {
            appbarVerticalOffset = Math.abs(verticalOffset);
            if (appbarVerticalOffset == appBarLayout.getTotalScrollRange()) {
                gameFilterDialog.show(cbGame);
                cbGender.setClickable(false);
            }
        };
        gameFilterDialog = new GameFilterDialog(getContext(), 0, gameListViewModel.getGameOptionList(), true, this);
        gameFilterDialog.setOnDismissListener(() -> {
            cbGame.setChecked(false);
            cbGender.setClickable(true);
            appBarLayout.removeOnOffsetChangedListener(gameListener);
        });
        cbGame.setOnClickListener(v -> {
            if (cbGame.isChecked() && !cbGender.isChecked()) {
                appBarLayout.addOnOffsetChangedListener(gameListener);
                // appbar不是收起状态要先收起，再展开弹框
                if (appbarVerticalOffset == appBarLayout.getTotalScrollRange()) {
                    gameFilterDialog.show(cbGame);
                    cbGender.setClickable(false);
                } else {
                    appBarLayout.setExpanded(false, true);
                }
            }
        });


        observeDataChanged();
        // 加载数据
        gameListViewModel.loadGameList();
    }

    private void observeDataChanged() {
        gameListViewModel.getGameInfoList().observe(this, new Observer<List<RCGameInfo>>() {
            @Override
            public void onChanged(List<RCGameInfo> rcGameInfos) {
                if (rcGameInfos != null) {
                    gameAdapter.setNewInstance(rcGameInfos);
                }
            }
        });

        gameListViewModel.getGenderFilter().observe(this, new Observer<FilterOption<String>>() {
            @Override
            public void onChanged(FilterOption<String> integerFilterOption) {
                if (integerFilterOption != null) {
                    cbGender.setText(integerFilterOption.getTitle());
                    loadRoomList(true);
                }
            }
        });

        gameListViewModel.getGameFilter().observe(this, new Observer<FilterOption<RCGameInfo>>() {
            @Override
            public void onChanged(FilterOption<RCGameInfo> rcGameInfoFilterOption) {
                if (rcGameInfoFilterOption != null) {
                    cbGame.setText(rcGameInfoFilterOption.getTitle());
                    loadRoomList(true);
                }
            }
        });

        gameRoomListViewModel.gameRoomList.observe(this, new Observer<GameRoomListBean>() {
            @Override
            public void onChanged(GameRoomListBean gameRoomListBean) {
                if (gameRoomListBean.page == 1) {
                    roomAdapter.setNewInstance(gameRoomListBean.gameRoomList);
                    if (gameRoomListBean.gameRoomList == null || gameRoomListBean.gameRoomList.isEmpty()) {
                        roomAdapter.setEmptyView(R.layout.game_layout_empty);
                    }
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                    if (gameRoomListBean.gameRoomList == null || gameRoomListBean.gameRoomList.isEmpty()) {
                        roomAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        roomAdapter.addData(gameRoomListBean.gameRoomList);
                        roomAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }
            }
        });
        gameRoomListViewModel.loading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showLoading("");
                } else {
                    dismissLoading();
                }
            }
        });
        gameRoomListViewModel.gameCreateBean.observe(this, new Observer<GameCreateBean>() {
            @Override
            public void onChanged(GameCreateBean gameCreateBean) {
                if (gameCreateBean == null) {
                    showCreateRoomDialog();
                } else {
                    if (gameCreateBean.isCreate) {
                        roomAdapter.getData().add(0, gameCreateBean.gameRoomBean);
                        roomAdapter.notifyItemInserted(0);
                        clickItem(gameCreateBean.gameRoomBean, true);
                    } else if (gameCreateBean.isFastIn) {
                        launchRoomActivity(gameCreateBean.gameRoomBean.getRoomId(), false, true, gameCreateBean.fastInGameId);
                    } else {
                        if (TextUtils.equals(gameCreateBean.gameRoomBean.getCreateUserId(), UserManager.get().getUserId())) {
                            onCreateExist(gameCreateBean.gameRoomBean);
                        } else {
                            launchRoomActivity(gameCreateBean.gameRoomBean.getRoomId(), false, false, "");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoomList(true);
    }

    private void loadRoomList(boolean isRefresh) {
        gameRoomListViewModel.loadRoomList(isRefresh, gameListViewModel.getGender(), gameListViewModel.getGameId());
    }

    @Override
    public void onClickFilterFinish(List<FilterOption> optionList) {
        if (optionList != null && optionList.size() > 0) {
            FilterOption filterOption = optionList.get(0);
            if (filterOption.getData() instanceof String) {
                gameListViewModel.getGenderFilter().setValue(filterOption);
            } else if (filterOption.getData() instanceof RCGameInfo) {
                gameListViewModel.getGameFilter().setValue(filterOption);
            }
        }
    }

    private void showCreateRoomDialog() {
        createGameRoomDialog = new CreateGameRoomDialog(gameAdapter.getData(), new CreateGameRoomDialog.OnCreateGameRoomListener() {
            @Override
            public void onCreateGameRoom(RCGameInfo gameInfo, String name) {
                gameRoomListViewModel.createGameRoom(gameInfo, name, false);
            }
        });
        createGameRoomDialog.show(getChildFragmentManager());
    }

    public void clickItem(GameRoomBean item, boolean isCreate) {
        if (TextUtils.equals(item.getUserId(), UserManager.get().getUserId())) {
            launchRoomActivity(item.getRoomId(), isCreate, false, "");
        } else if (item.isPrivate()) {
            inputPasswordDialog =
                    new InputPasswordDialog(requireContext(), false, new InputPasswordDialog.OnClickListener() {
                        @Override
                        public void clickCancel() {

                        }

                        @Override
                        public void clickConfirm(String password) {
                            if (TextUtils.isEmpty(password)) {
                                return;
                            }
                            if (password.length() < 4) {
                                ToastUtils.s(requireContext(), requireContext().getString(cn.rongcloud.roomkit.R.string.text_please_input_four_number));
                                return;
                            }
                            if (TextUtils.equals(password, item.getPassword())) {
                                inputPasswordDialog.dismiss();
                                ArrayList list = new ArrayList();
                                list.add(item.getRoomId());
                                launchRoomActivity(item.getRoomId(), false, false, "");
                            } else {
                                showToast("密码错误");
                            }
                        }
                    });
            inputPasswordDialog.show();
        } else {
            launchRoomActivity(item.getRoomId(), false, false, "");
        }
    }

    private void launchRoomActivity(
            String roomId, boolean isCreate, boolean isFastIn, String gameId) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(roomId, () -> {
            IntentWrap.launchGameRoom(requireContext(), roomId, isCreate, isFastIn, gameId);
        });
    }

    public void onCreateExist(GameRoomBean gameRoomBean) {
        confirmDialog = new VRCenterDialog(requireActivity(), null);
        confirmDialog.replaceContent(getString(cn.rongcloud.roomkit.R.string.text_you_have_created_room), getString(cn.rongcloud.roomkit.R.string.cancel), null,
                getString(cn.rongcloud.roomkit.R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchRoomActivity(gameRoomBean.getRoomId(), false, false, "");
                        // OkApi.get(VRApi.deleteRoom(gameRoomBean.getRoomId()), null, new WrapperCallBack() {
                        //     @Override
                        //     public void onResult(Wrapper result) {
                        //         if (result.ok()) {
                        //
                        //         } else {
                        //         }
                        //     }
                        //
                        //     @Override
                        //     public void onError(int code, String msg) {
                        //         super.onError(code, msg);
                        //         dismissLoading();
                        //         KToast.show(msg);
                        //     }
                        // });
                    }
                }, null);
        confirmDialog.show();
    }


    @Override
    public BasePresenter createPresent() {
        return null;
    }
}