package cn.rong.combusis.ui.room.fragment;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.recycle.RcyHolder;
import com.basis.adapter.recycle.RcySAdapter;
import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.ui.room.model.MemberCache;

/**
 * @author gyn
 * @date 2021/9/26
 */
public class MemberListFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView mRecyclerView;
    private RcySAdapter adapter;
    private String roomId;
    private OnClickUserListener mOnClickUserListener;

    public MemberListFragment(String roomId, OnClickUserListener onClickUserListener) {
        this(R.layout.fragment_member_list);
        this.roomId = roomId;
        this.mOnClickUserListener = onClickUserListener;
    }

    public MemberListFragment(int layoutId) {
        super(layoutId);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public void initView() {
        mRecyclerView = getView().findViewById(R.id.rv_member_list);
        getView().findViewById(R.id.iv_close).setOnClickListener(v -> {
            dismiss();
        });
        adapter = new RcySAdapter<User, RcyHolder>(getContext(), R.layout.item_member) {
            @Override
            public void convert(RcyHolder holder, User user, int position) {
                holder.setText(R.id.tv_member_name, user.getUserName());
                ImageLoaderUtil.INSTANCE.loadImage(getContext(), holder.getView(R.id.iv_member_portrait), user.getPortraitUrl(), R.drawable.default_portrait);
                holder.itemView.setOnClickListener(v -> {
                    if (mOnClickUserListener != null) {
//                        dismiss();
                        mOnClickUserListener.clickUser(user);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.setData(MemberCache.getInstance().getMemberList().getValue(), true);
        MemberCache.getInstance().getMemberList().observe(this, members -> {
            adapter.setData(members, true);
        });
        MemberCache.getInstance().fetchData(roomId);
    }

    public interface OnClickUserListener {
        void clickUser(User user);
    }
}
