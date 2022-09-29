package cn.rongcloud.gameroom.ui.gameroom;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;

/**
 * @author gyn
 * @date 2022/5/18
 */
public class InvitePlayerDialog extends BaseBottomSheetDialog {

    private RecyclerView mRvList;

    private boolean isAdmin;
    private boolean isCaptain;
    InvitePlayerAdapter adapter;

    public InvitePlayerDialog(boolean isAdmin, boolean isCaptain) {
        super(R.layout.game_dialog_invite_player);
        this.isAdmin = isAdmin;
        this.isCaptain = isCaptain;
    }

    @Override
    public void initView() {
        mRvList = (RecyclerView) getView().findViewById(R.id.rv_list);

        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InvitePlayerAdapter();
        mRvList.setAdapter(adapter);
        adapter.addChildClickViewIds(R.id.tv_invite_game, R.id.tv_invite_seat);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                User user = (User) adapter.getData().get(position);
                int id = view.getId();
                if (id == R.id.tv_invite_game) {
                    GameEventHelper.getInstance().clickInvitedGame(user, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            if (!result) {
                                KToast.show(msg);
                            }
                        }
                    });
                } else if (id == R.id.tv_invite_seat) {
                    GameEventHelper.getInstance().clickInviteSeat(0, user, new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            if (!result) {
                                KToast.show(msg);
                            }
                        }
                    });
                }
            }
        });

        View emptyView = View.inflate(getContext(), R.layout.game_layout_empty, null);
        TextView tvEmpty = emptyView.findViewById(R.id.tv_empty);
        tvEmpty.setText("暂无用户");
        tvEmpty.setTextColor(Color.WHITE);

        String mUid = UserManager.get().getUserId();
        MemberCache.getInstance().getMemberList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                List<User> showUsers = new ArrayList<>();
                if (users != null && !users.isEmpty()) {
                    for (User user : users) {
                        // 自己不显示在列表中
                        if (TextUtils.equals(mUid, user.getUserId())) {
                            continue;
                        }
                        if (isAdmin) {
                            if (isCaptain) {
                                // 在麦位且在游戏中的不显示
                                if (GameEventHelper.getInstance().isInSeat(user.getUserId())
                                        && GameEventHelper.getInstance().isInGame(user.getUserId())) {
                                    continue;
                                }
                            } else {
                                if (GameEventHelper.getInstance().isInSeat(user.getUserId())) {
                                    continue;
                                }
                            }
                        } else {
                            // 在游戏中不显示
                            if (GameEventHelper.getInstance().isInGame(user.getUserId())) {
                                continue;
                            }
                        }
                        showUsers.add(user);
                    }
                }
                adapter.setNewInstance(showUsers);
                if (showUsers.isEmpty()) {
                    adapter.setEmptyView(emptyView);
                }
            }
        });
    }

    private class InvitePlayerAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public InvitePlayerAdapter() {
            super(R.layout.game_item_invite_player);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, User member) {
            ImageLoader.loadUrl(baseViewHolder.getView(R.id.iv_user_portrait), member.getPortraitUrl(), R.drawable.default_portrait);
            baseViewHolder.setText(R.id.tv_member_name, member.getUserName());
            baseViewHolder.setGone(R.id.tv_invite_game, !isCaptain);
            baseViewHolder.setGone(R.id.tv_invite_seat, !isAdmin);
        }
    }
}
