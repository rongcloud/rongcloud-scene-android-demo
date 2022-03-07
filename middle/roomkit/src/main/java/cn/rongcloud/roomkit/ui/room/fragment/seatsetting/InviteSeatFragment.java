package cn.rongcloud.roomkit.ui.room.fragment.seatsetting;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;

/**
 * 邀请连麦fragment
 */
public class InviteSeatFragment extends BaseFragment {

    private RecyclerView rvList;
    private ImageView ivEmpty;
    private TextView tvEmpty;
    private ArrayList<User> inviteSeats;
    private InviteSeatAdapter inviteSeatAdapter;
    private int seatIndex = -1;

    public InviteSeatFragment(ArrayList<User> inviteSeats) {
        this.inviteSeats = inviteSeats;
    }

    @Override
    public int setLayoutId() {
        return R.layout.layout_list;
    }

    @Override
    public void init() {
        ivEmpty = (ImageView) getView().findViewById(R.id.iv_empty);
        tvEmpty = (TextView) getView().findViewById(R.id.tv_empty);
        rvList = getView().findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        inviteSeatAdapter = new InviteSeatAdapter(R.layout.item_seat_layout);
        rvList.setAdapter(inviteSeatAdapter);
        inviteSeatAdapter.setNewInstance(inviteSeats);
        checkEmpty(inviteSeats);
    }

    @Override
    public void initListener() {

    }

    @NonNull
    @Override
    public String getTitle() {
        return "邀请连麦";
    }

    /**
     * 获取父布局
     *
     * @return
     */
    private SeatOperationViewPagerFragment getSeatOperationViewPagerFragment() {
        return ((SeatOperationViewPagerFragment) getParentFragment());
    }

    /**
     * 刷新列表
     *
     * @param uiMemberModels
     */
    public void refreshData(List<User> uiMemberModels) {
        if (inviteSeatAdapter != null) inviteSeatAdapter.setList(uiMemberModels);
        checkEmpty(uiMemberModels);
    }

    private void checkEmpty(List<User> users) {
        if (ivEmpty == null || tvEmpty == null) {
            return;
        }
        if (users != null && users.size() > 0) {
            ivEmpty.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            ivEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void setInviteSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }


    class InviteSeatAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public InviteSeatAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, User user) {
            if (user == null) return;
            baseViewHolder.setText(R.id.tv_operation, "邀请");
            TextView tvRejecet = baseViewHolder.getView(R.id.tv_reject);
            tvRejecet.setVisibility(View.GONE);
            baseViewHolder.setText(R.id.tv_member_name, user.getUserName());
            ImageView imageView = baseViewHolder.getView(R.id.iv_user_portrait);
            ImageLoader.loadUrl(imageView, user.getPortraitUrl(), R.drawable.default_portrait, ImageLoader.Size.S_100);
            RxView.clicks(baseViewHolder.getView(R.id.tv_operation)).throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Throwable {
                            InviteSeat(user);
                        }
                    });
        }
    }

    private void InviteSeat(User user) {
        getSeatOperationViewPagerFragment().getSeatActionClickListener().clickInviteSeat(seatIndex, user, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                KToast.show(msg);
                if (result) {
                    getSeatOperationViewPagerFragment().dismiss();
                }
            }
        });
    }


}
