package cn.rongcloud.roomkit.ui.room.fragment.gift;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.utils.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.manager.AllBroadcastManager;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.widget.page.CustomerPageLayoutManager;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/10/9
 */
public class GiftFragment extends BaseBottomSheetDialog {

    private RecyclerView mRcyMember;
    private AppCompatButton mBtnSelectAll;
    private RecyclerView mVpPresent;
    private View mIndexFirst;
    private View mIndexSecond;
    private AppCompatButton mBtnNum;
    private AppCompatButton mBtnSend;

    private List<Gift> giftList = Arrays.asList(
            new Gift(1, R.drawable.ic_present_0, "小心心", 1, false),
            new Gift(2, R.drawable.ic_present_1, "话筒", 2, false),
            new Gift(3, R.drawable.ic_present_2, "麦克风", 5, false),
            new Gift(4, R.drawable.ic_present_3, "萌小鸡", 10, false),
            new Gift(5, R.drawable.ic_present_4, "手柄", 20, false),
            new Gift(6, R.drawable.ic_present_5, "奖杯", 50, false),
            new Gift(7, R.drawable.ic_present_6, "火箭", 100, true),
            new Gift(8, R.drawable.ic_present_7, "礼花", 200, true),
            new Gift(9, R.drawable.ic_present_8, "玫瑰花", 10, false),
            new Gift(10, R.drawable.ic_present_9, "吉他", 20, false)
    );
    private VoiceRoomBean mVoiceRoomBean;
    private List<Member> mMembers;
    private HashMap<String, Member> mMembersMap = new HashMap<>();
    private List<String> mSelectUserIds = new ArrayList<>();
    private Gift mCurrentGift;
    private RcySAdapter mMemberAdapter;
    private RcySAdapter mGiftAdapter;
    private int mGiftNum = 1;
    private OnSendGiftListener mOnSendGiftListener;
    private CountDownLatch latch;
    private List<Member> successMembers = new ArrayList<>();

    public GiftFragment(VoiceRoomBean voiceRoomBean, String selectUserId, OnSendGiftListener onSendGiftListener) {
        super(R.layout.fragment_gift);
        this.mVoiceRoomBean = voiceRoomBean;
        this.mOnSendGiftListener = onSendGiftListener;
        mSelectUserIds.clear();
        if (!TextUtils.isEmpty(selectUserId)) {
            mSelectUserIds.add(selectUserId);
        }
        mCurrentGift = giftList.get(0);
    }

    public void refreshMember(List<Member> members) {
        if (members == null) {
            members = new ArrayList<>();
        }
        this.mMembers = members;
        mMembersMap.clear();
        for (Member member : members) {
            mMembersMap.put(member.getUserId(), member);
        }
        if (mMemberAdapter != null) {
            mMemberAdapter.setData(mMembers, true);
            // 更新选中的人,如果选中的人已经下麦或离开房间就剔除选中
            if (mSelectUserIds != null && mSelectUserIds.size() > 0) {
                List<String> removes = new ArrayList<>();
                Member member;
                for (String id : mSelectUserIds) {
                    member = new Member();
                    member.setUserId(id);
                    if (!members.contains(member)) {
                        removes.add(id);
                    }
                }
                if (!removes.isEmpty()) {
                    mSelectUserIds.removeAll(removes);
                }
            } else {
                mSelectUserIds.clear();
            }
            updateBtnAll();
            updateEnableSend();
        }
    }

    @Override
    public void initView() {
        mRcyMember = getView().findViewById(R.id.rcy_member);
        mBtnSelectAll = getView().findViewById(R.id.btn_selectall);
        mVpPresent = getView().findViewById(R.id.vp_present);
        mIndexFirst = getView().findViewById(R.id.index_first);
        mIndexSecond = getView().findViewById(R.id.index_second);
        mBtnNum = getView().findViewById(R.id.btn_num);
        mBtnSend = getView().findViewById(R.id.btn_send);
        // 成员列表
        mMemberAdapter = new RcySAdapter<Member, RcyHolder>(getContext(), R.layout.item_gift_member) {
            @Override
            public void convert(RcyHolder holder, Member member, int position) {
                ImageView imageView = holder.getView(R.id.iv_member_head);
                ImageLoader.loadUrl(imageView, member.getPortraitUrl(), R.drawable.default_portrait);
                Logger.e("Gift","memberIndex = "+member.getSeatIndex());
                String name = "观众";
                if (TextUtils.equals(mVoiceRoomBean.getCreateUserId(), member.getUserId())) {
                    name = "房主";
                } else if (member.getSeatIndex() >= 0 && member.getSeatIndex() < Integer.MAX_VALUE) {
                    name = member.getSeatIndex() + "";
                }
                holder.setText(R.id.tv_member_name, name);
                holder.itemView.setSelected(mSelectUserIds.contains(member.getUserId()));
                holder.itemView.setOnClickListener(v -> {
                    updateSelected(member.getUserId());
                    updateBtnAll();
                    notifyDataSetChanged();
                });
            }
        };
        mRcyMember.setAdapter(mMemberAdapter);
        mMemberAdapter.setData(mMembers, true);
        // 礼物列表
        mGiftAdapter = new RcySAdapter<Gift, RcyHolder>(getContext(), R.layout.item_gift) {
            @Override
            public void convert(RcyHolder holder, Gift gift, int position) {
                holder.itemView.setSelected(gift.equals(mCurrentGift));
                holder.setImageResource(R.id.iv_present, gift.getIcon());
                holder.setText(R.id.tv_present_name, gift.getName());
                holder.setText(R.id.tv_present_price, gift.getPrice() + "");
                holder.setVisible(R.id.tv_all_broadcast, gift.isAllBroadcast());
                holder.itemView.setOnClickListener(v -> {
                    if (gift.equals(mCurrentGift)) {
                        mCurrentGift = null;
                    } else {
                        mCurrentGift = gift;
                    }
                    notifyDataSetChanged();
                    updateEnableSend();
                });
            }
        };
        mVpPresent.setHasFixedSize(true);
        CustomerPageLayoutManager layoutManager = new CustomerPageLayoutManager(
                2,
                4,
                CustomerPageLayoutManager.HORIZONTAL
        );
        layoutManager.setAllowContinuousScroll(false);
        layoutManager.setPageListener(new CustomerPageLayoutManager.PageListener() {
            @Override
            public void onPageSizeChanged(int pageSize) {
            }

            @Override
            public void onPageSelect(int pageIndex) {
                onGiftPageSelect(pageIndex);
            }

            @Override
            public void onItemVisible(int fromItem, int toItem) {
            }
        });
        mVpPresent.setLayoutManager(layoutManager);
//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
//        pagerSnapHelper.attachToRecyclerView(mVpPresent);
        mVpPresent.setAdapter(mGiftAdapter);
        mGiftAdapter.setData(giftList, true);

        updateEnableSend();
        updateBtnAll();
        updateBtnNum(false);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBtnSelectAll.setOnClickListener(v -> {
            /**
             * 已全选 -> selected = false 显示文字：'全选' 非全选
             * 非全选 -> selected = true 显示文字：'取消' 已全选
             */
            if (v.isSelected()) {
                v.setSelected(false);
                mBtnSelectAll.setText("取消");
                // 全选操作
                selectAll(mMembers);
            } else {
                v.setSelected(true);
                mBtnSelectAll.setText("全选");
                // 取消全选操作
                selectAll(null);
            }
            mRcyMember.getAdapter().notifyDataSetChanged();
        });

        mBtnSend.setOnClickListener(v -> {
            // 发送操作
            sendGift();
        });

        mBtnNum.setOnClickListener(v -> {
            new NumPopupWindow(getContext(), mGiftNum, num -> {
                mGiftNum = num;
                updateBtnNum(false);
            }).show(mBtnNum);
            updateBtnNum(true);
        });
    }

    private void sendGift() {
        boolean isAll = false;
        if (mSelectUserIds.size() == mMembers.size() && mSelectUserIds.size() != 1) {
            isAll = true;
        }
        latch = new CountDownLatch(mSelectUserIds.size());
        successMembers.clear();
        for (String userId : mSelectUserIds) {
            sendGift(userId);
            if (!isAll) {
                // 非全选每人发一条
                sendGiftBroadcast(userId, false);
            }
        }
        if (isAll) {
            // 全选只发一条全麦打赏的广播
            sendGiftBroadcast("", true);
        }
        boolean finalIsAll = isAll;
        new Thread(() -> {
            try {
                latch.await();
                UIKit.runOnUiThread(() -> {
                    sendGiftMessage(successMembers, finalIsAll);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendGift(String userId) {
        Map<String, Object> params = new OkParams()
                .add("roomId", mVoiceRoomBean.getRoomId())
                .add("giftId", mCurrentGift.getIndex())
                .add("toUid", userId)
                .add("num", mGiftNum)
                .build();
        OkApi.post(VRApi.SEND_GIFT, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    successMembers.add(mMembersMap.get(userId));
                }
                latch.countDown();
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                latch.countDown();
            }
        });
    }

    private void sendGiftBroadcast(String userId, boolean isAll) {
        if (mCurrentGift.isAllBroadcast()) {
            RCAllBroadcastMessage message = new RCAllBroadcastMessage();
            message.setUserId(UserManager.get().getUserId());
            message.setUserName(UserManager.get().getUserName());
            if (!isAll) {
                Member member = mMembersMap.get(userId);
                message.setTargetId(member.getUserId());
                message.setTargetName(member.getUserName());
            } else {
                message.setTargetId("");
                message.setTargetName("");
            }
            message.setGiftCount(mGiftNum + "");
            message.setGiftId(mCurrentGift.getIndex() + "");
            message.setGiftValue(mCurrentGift.getPrice() + "");
            message.setGiftName(mCurrentGift.getName());
            message.setRoomId(mVoiceRoomBean.getRoomId());
            message.setRoomType(mVoiceRoomBean.getRoomType() + "");
            message.setIsPrivate(mVoiceRoomBean.getIsPrivate() + "");
            Map<String, Object> params = new OkParams()
                    .add("fromUserId", UserManager.get().getUserId())
                    .add("objectName", "RC:RCGiftBroadcastMsg")
                    .add("content", message.toString())
                    .build();
            OkApi.post(VRApi.GIFT_BROADCAST, params, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (result.ok()) {
                        AllBroadcastManager.getInstance().addMessage(message);
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    super.onError(code, msg);
                }
            });
        }
    }

    /**
     * 礼物发送成功后发送消息
     *
     * @param members
     * @param isAll
     */
    private void sendGiftMessage(List<Member> members, boolean isAll) {
        List<MessageContent> messages = new ArrayList<>();
        if (isAll) {
            RCChatroomGiftAll all = new RCChatroomGiftAll();
            all.setUserId(UserManager.get().getUserId());
            all.setUserName(UserManager.get().getUserName());
            all.setGiftId(mCurrentGift.getIndex() + "");
            all.setGiftName(mCurrentGift.getName());
            all.setNumber(mGiftNum);
            all.setPrice(mCurrentGift.getPrice());
            messages.add(all);
        } else {
            RCChatroomGift gift;
            for (Member member : members) {
                gift = new RCChatroomGift();
                gift.setUserId(UserManager.get().getUserId());
                gift.setUserName(UserManager.get().getUserName());
                gift.setGiftId(mCurrentGift.getIndex() + "");
                gift.setGiftName(mCurrentGift.getName());
                gift.setNumber(mGiftNum + "");
                gift.setPrice(mCurrentGift.getPrice());
                gift.setTargetId(member.getUserId());
                gift.setTargetName(member.getUserName());
                messages.add(gift);
            }
        }
        // 回调回去结果
        if (mOnSendGiftListener != null) {
            mOnSendGiftListener.onSendGiftSuccess(messages);
        }
        if (members.size() == mSelectUserIds.size()) {
            ToastUtils.s(getContext(), "赠送成功");
        } else {
            ToastUtils.s(getContext(), "赠送异常");
        }
        dismiss();
    }

    /**
     * 已全选：文案 取消 isSelected = false
     * 非全选：文案 全选 isSelected = true
     */
    private void updateBtnAll() {
        if (mMembers.size() > 1) {
            mBtnSelectAll.setVisibility(View.VISIBLE);
            boolean isAll = false;
            if (mSelectUserIds.size() == mMembers.size()) {
                isAll = true;
            }
            mBtnSelectAll.setSelected(!isAll);
            mBtnSelectAll.setText(isAll ? "取消" : "全选");
        } else {
            mBtnSelectAll.setVisibility(View.GONE);
        }
    }

    /**
     * 更新礼物数量
     *
     * @param showPop
     */
    private void updateBtnNum(Boolean showPop) {
        mBtnNum.setText("x " + mGiftNum);
        int res = R.drawable.ic_up;
        if (showPop) res = R.drawable.ic_down;
        mBtnNum.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
        mBtnNum.setCompoundDrawablePadding(UiUtils.dp2px(1f));
    }

    /**
     * 发送是否可用
     */
    private void updateEnableSend() {
        boolean enable = false;
        if (mCurrentGift != null && mSelectUserIds != null && !mSelectUserIds.isEmpty()) {
            enable = true;
        }
        mBtnSend.setEnabled(enable);
        mBtnNum.setEnabled(enable);
        mBtnNum.setText("x " + mGiftNum);
    }

    /**
     * 更新选中的成员
     *
     * @param userId
     */
    private void updateSelected(String userId) {
        if (mSelectUserIds.contains(userId)) {
            mSelectUserIds.remove(userId);
        } else {
            mSelectUserIds.add(userId);
        }
        updateEnableSend();
    }

    /**
     * 指示器
     *
     * @param page
     */
    private void onGiftPageSelect(int page) {
        if (0 == page) {
            mIndexFirst.setBackgroundResource(R.drawable.bg_index_selected);
            mIndexSecond.setBackgroundResource(R.drawable.bg_index_nomal);
        } else {
            mIndexFirst.setBackgroundResource(R.drawable.bg_index_nomal);
            mIndexSecond.setBackgroundResource(R.drawable.bg_index_selected);
        }
    }

    /**
     * 全选或取消全选
     *
     * @param members
     */
    private void selectAll(List<Member> members) {
        mSelectUserIds.clear();
        if (members != null) {
            for (Member member : members) {
                mSelectUserIds.add(member.getUserId());
            }
        }
        updateEnableSend();
    }

    public interface OnSendGiftListener {
        void onSendGiftSuccess(List<MessageContent> messages);
    }
}
