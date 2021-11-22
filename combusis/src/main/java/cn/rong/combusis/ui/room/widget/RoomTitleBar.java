package cn.rong.combusis.ui.room.widget;

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
import com.jakewharton.rxbinding4.view.RxView;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.UiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.rong.combusis.R;
import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.message.RCFollowMsg;
import cn.rong.combusis.ui.room.model.Member;
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
    private ConstraintLayout mLeftView;
    private TextView mFollowTextView;
    private Member member;
    private OnFollowClickListener onFollowClickListener;
    private boolean isShowFollow = false;
    private int mDelay = 0;

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
        mFollowTextView.setOnClickListener(v -> {
            follow();
        });
    }

    public Observable setOnMemberClickListener() {
        return RxView.clicks(mLeftView).throttleFirst(1, TimeUnit.SECONDS);
    }

    public Observable setOnMenuClickListener() {
        return RxView.clicks(mMenuButton).throttleFirst(1, TimeUnit.SECONDS);
    }

    public void setData(String name, int id, String roomUserId, OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
        setRoomName(name);
        setRoomId(id);
        getFollowStatus(roomUserId);
    }

    public void setRoomId(int id) {
        mIDTextView.setText(String.format("ID %s", id));
    }

    public void setRoomName(String name) {
        mNameTextView.setText(name);
    }

    public void setOnlineNum(int num) {
        mOnlineTextView.setText(String.format("在线 %s", num));
    }

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
        if (TextUtils.equals(roomUserId, AccountStore.INSTANCE.getUserId())) {
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
            mLeftView.setPadding(getResources().getDimensionPixelOffset(R.dimen.dimen_room_padding), 0, UiUtils.INSTANCE.dp2Px(getContext(), 6), 0);
            if (isFollow) {
                mFollowTextView.setText("已关注");
                mFollowTextView.setBackgroundResource(R.drawable.btn_titlebar_followed);
            } else {
                mFollowTextView.setText("关注");
                mFollowTextView.setBackgroundResource(R.drawable.bg_voice_room_send_button);
            }
        } else {
            mFollowTextView.setVisibility(GONE);
            mLeftView.setPadding(getResources().getDimensionPixelOffset(R.dimen.dimen_room_padding), 0, UiUtils.INSTANCE.dp2Px(getContext(), 20), 0);
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
                            followMsg.setUser(AccountStore.INSTANCE.toUser());
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

    public interface OnFollowClickListener {
        void clickFollow(boolean isFollow, RCFollowMsg followMsg);
    }
}
