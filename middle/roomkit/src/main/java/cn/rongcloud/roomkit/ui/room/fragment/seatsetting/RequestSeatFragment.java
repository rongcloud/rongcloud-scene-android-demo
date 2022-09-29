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
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;

/**
 * 请求连麦fragment
 */
public class RequestSeatFragment extends BaseFragment {


    private RecyclerView rvList;
    private ArrayList<User> requestSeats;
    private RequestSeatAdapter requestSeatAdapter;
    private ImageView ivEmpty;
    private TextView tvEmpty;

    public RequestSeatFragment(ArrayList<User> requestSeats) {
        this.requestSeats = requestSeats;
    }

    @Override
    public void init() {
        ivEmpty = (ImageView) getView().findViewById(R.id.iv_empty);
        tvEmpty = (TextView) getView().findViewById(R.id.tv_empty);
        rvList = getView().findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestSeatAdapter = new RequestSeatAdapter(R.layout.item_seat_layout);
        requestSeatAdapter.setDiffCallback(new DIffUserCallBack());
        rvList.setAdapter(requestSeatAdapter);
        requestSeatAdapter.setNewInstance(requestSeats);
        checkEmpty(requestSeats);
    }

    /**
     * 获取父布局
     *
     * @return
     */
    private SeatOperationViewPagerFragment getSeatOperationViewPagerFragment() {
        return ((SeatOperationViewPagerFragment) getParentFragment());
    }

    public String getTitle() {
        return "申请列表";
    }

    /**
     * 刷新列表
     *
     * @param uiMemberModels
     */
    public void refreshData(List<User> uiMemberModels) {
        if (requestSeatAdapter != null) requestSeatAdapter.setList(uiMemberModels);
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

    @Override
    public int setLayoutId() {
        return R.layout.layout_list;
    }

    @Override
    public void initListener() {

    }

    class RequestSeatAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public RequestSeatAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, User user) {
            if (user == null) return;
            baseViewHolder.setText(R.id.tv_operation, "接受");
            baseViewHolder.setText(R.id.tv_member_name, user.getUserName());
            ImageView imageView = baseViewHolder.getView(R.id.iv_user_portrait);
            ImageLoader.loadUrl(imageView, user.getPortraitUrl(), R.drawable.default_portrait, ImageLoader.Size.S_100);
            RxView.clicks(baseViewHolder.getView(R.id.tv_operation)).throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Throwable {
                            acceptRequest(user.getUserId());
                        }
                    });
            TextView tvRejecet = baseViewHolder.getView(R.id.tv_reject);
            if (getSeatOperationViewPagerFragment().getRoomOwnerType() == RoomOwnerType.LIVE_OWNER) {
                tvRejecet.setVisibility(View.VISIBLE);
                baseViewHolder.setText(R.id.tv_reject, "拒绝");
                RxView.clicks(tvRejecet).throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(new Consumer<Unit>() {
                            @Override
                            public void accept(Unit unit) throws Throwable {
                                rejectRequest(user.getUserId());
                            }
                        });
            } else {
                tvRejecet.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 拒绝请求
     *
     * @param userId
     */
    private void rejectRequest(String userId) {
        getSeatOperationViewPagerFragment().getSeatActionClickListener().rejectRequestSeat(userId, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    getSeatOperationViewPagerFragment().dismiss();
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

    /**
     * 同意请求
     *
     * @param userId
     */
    private void acceptRequest(String userId) {
        getSeatOperationViewPagerFragment().getSeatActionClickListener().acceptRequestSeat(userId, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    getSeatOperationViewPagerFragment().dismiss();
                } else {
                    KToast.show(msg);
                }
            }
        });
    }

}
