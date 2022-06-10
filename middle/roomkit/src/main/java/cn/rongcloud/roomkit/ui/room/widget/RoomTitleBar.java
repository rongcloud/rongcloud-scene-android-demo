package cn.rongcloud.roomkit.ui.room.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.ImageLoader;
import com.basis.utils.UiUtils;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.model.Member;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.core.Observable;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2021/9/17
 */
public class RoomTitleBar extends ConstraintLayout {
    private View mRootView;
    private TextView mNameTextView;
    private TextView mIDTextView;
    private TextView mOnlineTextView;
    private TextView mDelayTextView;
    private ImageButton mMenuButton;
    private View mLeftView;
    private TextView mFollowTextView;
    private Member member;
    private OnFollowClickListener onFollowClickListener;
    private boolean isShowFollow = false;
    private int mDelay = 0;
    private CircleImageView mCreaterImageview;
    private RoomOwnerType roomOwnerType;
    private TextView tvRoomOnlineCount;

    public RoomTitleBar(@NonNull Context context) {
        this(context, null);
    }

    public RoomTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_room_title_bar, this);
        initView();
    }

    private void initView() {
        mNameTextView = mRootView.findViewById(R.id.tv_room_name);
        mLeftView = mRootView.findViewById(R.id.cl_left);
        mIDTextView = mRootView.findViewById(R.id.tv_room_id);
        mOnlineTextView = mRootView.findViewById(R.id.tv_room_online);
        mDelayTextView = mRootView.findViewById(R.id.tv_room_delay);
        mMenuButton = mRootView.findViewById(R.id.btn_menu);
        mFollowTextView = mRootView.findViewById(R.id.tv_follow);
        mCreaterImageview = mRootView.findViewById(R.id.iv_creater_id);
        tvRoomOnlineCount = mRootView.findViewById(R.id.tv_room_online_count);
        mFollowTextView.setOnClickListener(v -> {
            follow();
        });

    }

    public Observable setOnLineMemberClickListener() {
        return RxView.clicks(tvRoomOnlineCount).throttleFirst(1, TimeUnit.SECONDS);
    }

    public Observable setOnMemberClickListener() {
        return RxView.clicks(mLeftView).throttleFirst(1, TimeUnit.SECONDS);
    }

    public Observable setOnMenuClickListener() {
        return RxView.clicks(mMenuButton).throttleFirst(1, TimeUnit.SECONDS);
    }

    public void setData(RoomOwnerType roomOwnerType, String name, int id, String roomUserId, OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
        this.roomOwnerType = roomOwnerType;
        setViewState();
        if (roomOwnerType == RoomOwnerType.LIVE_OWNER || roomOwnerType == RoomOwnerType.LIVE_VIEWER) {
            //如果是直播房，那么房间名直接显示创建者的ID
            setCreatorName(name);
        } else {
            setRoomName(name);
        }
        setRoomId(id);
        getFollowStatus(roomUserId);
    }

    public void setCreatorName(String creatorName) {
        mNameTextView.setText(creatorName);
    }

    public void setCreatorPortrait(String portrait) {
        ImageLoader.loadUrl(mCreaterImageview, portrait, R.drawable.default_portrait);
    }

    public void setRoomId(int id) {
        mIDTextView.setText(String.format("ID %s", id));
    }

    public void setRoomName(String name) {
        mNameTextView.setText(name);
    }

    public void setOnlineNum(int num) {
        mOnlineTextView.setText(String.format("在线 %s", num));
        tvRoomOnlineCount.setText(num + "");
    }

//    /**
//     * 是否在麦位上
//     */
//    public void setIsLinkSeat(boolean isLinkSeat){
//        if (roomOwnerType!=null&&roomOwnerType.equals(RoomOwnerType.LIVE_VIEWER)) {
//            if (isLinkSeat){
//                mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_live_room));
//            }else {
//                mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
//            }
//        }
//    }

    public void setDelay(int delay) {
        setDelay(delay, true);
    }

    public void setDelay(int delay, boolean isShow) {
        if (isShow) {
            if (mDelay == delay) {
                return;
            }
            mDelay = delay;
            mDelayTextView.setVisibility(View.VISIBLE);
            mDelayTextView.setText(delay + "ms");
            int leftPicId;
            if (delay < 100) {
                leftPicId = R.drawable.ic_room_delay_1;
            } else if (delay < 299) {
                leftPicId = R.drawable.ic_room_delay_2;
            } else {
                leftPicId = R.drawable.ic_room_delay_3;
            }
            mDelayTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(leftPicId, 0, 0, 0);
        } else {
            mDelayTextView.setVisibility(View.GONE);
        }
    }

    private void getFollowStatus(String roomUserId) {
        if (TextUtils.equals(roomUserId, UserManager.get().getUserId())) {
            isShowFollow = false;
            setFollow(false);
            return;
        }
        isShowFollow = true;
        OkApi.post(VRApi.GET_USER, new OkParams().add("userIds", new String[]{roomUserId}).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Member> members = result.getList(Member.class);
                    if (members != null && members.size() > 0) {
                        member = members.get(0);
                        setFollow(member.isFollow());
                    }
                }
            }
        });
    }

    public void setFollow(boolean isFollow) {
        if (isShowFollow) {
            mFollowTextView.setVisibility(VISIBLE);
            mLeftView.setPadding(0, 0, UiUtils.dp2px(6), 0);
            if (isFollow) {
                mFollowTextView.setText("已关注");
                mFollowTextView.setBackgroundResource(R.drawable.btn_titlebar_followed);
            } else {
                mFollowTextView.setText("关注");
                mFollowTextView.setBackgroundResource(R.drawable.bg_voice_room_send_button);
            }
        } else {
            mFollowTextView.setVisibility(GONE);
            mLeftView.setPadding(0, 0, UiUtils.dp2px(20), 0);
        }
    }

    /**
     * 关注
     */
    private void follow() {
        if (member == null) {
            return;
        }
        boolean isFollow = !this.member.isFollow();
        OkApi.get(VRApi.followUrl(member.getUserId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    if (isFollow) {
                        ToastUtils.s(getContext(), "关注成功");
                        if (onFollowClickListener != null) {
                            RCFollowMsg followMsg = new RCFollowMsg();
                            followMsg.setUser(UserManager.get());
                            followMsg.setTargetUser(member.toUser());
                            onFollowClickListener.clickFollow(true, followMsg);
                        }
                    } else {
                        ToastUtils.s(getContext(), "取消关注成功");
                        if (onFollowClickListener != null) {
                            onFollowClickListener.clickFollow(false, null);
                        }
                    }
                    member.setStatus(isFollow ? 1 : 0);
                    setFollow(isFollow);
                } else {
                    if (isFollow) {
                        ToastUtils.s(getContext(), "关注失败");
                    } else {
                        ToastUtils.s(getContext(), "取消关注失败");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (isFollow) {
                    ToastUtils.s(getContext(), "关注失败");
                } else {
                    ToastUtils.s(getContext(), "取消关注失败");
                }
            }
        });
    }

    /**
     * 设置不同房间的状态
     */
    public void setViewState() {
        LayoutParams layoutParams = (LayoutParams) mNameTextView.getLayoutParams();
        switch (roomOwnerType) {
            case LIVE_OWNER:
                layoutParams.bottomToTop = mDelayTextView.getId();
                mNameTextView.setLayoutParams(layoutParams);
                mDelayTextView.setVisibility(VISIBLE);
                mCreaterImageview.setVisibility(VISIBLE);
                mLeftView.setBackgroundResource(R.drawable.bg_live_room_title_left);
                mOnlineTextView.setVisibility(GONE);
                tvRoomOnlineCount.setVisibility(VISIBLE);
                mIDTextView.setVisibility(GONE);
                mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_live_room));
                setmLeftViewMarginStart(getResources().getDimensionPixelOffset(R.dimen.dimen_room_padding));
                break;
            case LIVE_VIEWER:
                mDelayTextView.setVisibility(GONE);
                mCreaterImageview.setVisibility(VISIBLE);
                mLeftView.setBackgroundResource(R.drawable.bg_live_room_title_left);
                mOnlineTextView.setVisibility(GONE);
                mIDTextView.setVisibility(GONE);
                tvRoomOnlineCount.setVisibility(VISIBLE);
                setmLeftViewMarginStart(getResources().getDimensionPixelOffset(R.dimen.dimen_room_padding));
                mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
                break;
            default:
                //非直播房
                layoutParams.bottomToTop = mIDTextView.getId();
                mNameTextView.setLayoutParams(layoutParams);
                mCreaterImageview.setVisibility(GONE);
                mLeftView.setBackgroundResource(R.drawable.bg_room_title_left);
                mOnlineTextView.setVisibility(VISIBLE);
                mIDTextView.setVisibility(VISIBLE);
                tvRoomOnlineCount.setVisibility(GONE);
                mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
                setmLeftViewMarginStart(0);
                break;
        }
    }

    /**
     * 设置距离右边的边距
     *
     * @param marginStart
     */
    private void setmLeftViewMarginStart(int marginStart) {
        LayoutParams layoutParams = (LayoutParams) mLeftView.getLayoutParams();
        layoutParams.setMarginStart(marginStart);
        mLeftView.setLayoutParams(layoutParams);
    }

    public interface OnFollowClickListener {
        void clickFollow(boolean isFollow, RCFollowMsg followMsg);
    }
}
