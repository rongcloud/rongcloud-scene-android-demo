package cn.rongcloud.roomkit.ui.room.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.FragmentManager;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.room.model.Member;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.picture.tools.ToastUtils;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;

/**
 * @author gyn
 * @date 2021/9/27
 */
public class MemberSettingFragment extends BaseBottomSheetDialog {
    private static final String TAG = MemberSettingFragment.class.getSimpleName();
    private Guideline mGlBg;
    private CircleImageView mIvMemberPortrait;
    private AppCompatTextView mTvMemberName;
    private AppCompatTextView mTvSeatPosition;
    private ConstraintLayout mClButtons;
    private AppCompatButton mBtnSendGift;
    private AppCompatButton mBtnSendMessage;
    private ConstraintLayout mClMemberSetting;
    private LinearLayout mLlInvitedSeat;
    private LinearLayout mLlKickSeat;
    private LinearLayout mLlCloseSeat;
    private LinearLayout mLlMuteSeat;
    private AppCompatImageView mIvMuteSeat;
    private AppCompatTextView mTvMuteSeat;
    private LinearLayout mLlKickRoom;
    private LinearLayout mLlKickGame;
    private LinearLayout mLlSelfEnterSeat;
    private LinearLayout mLlInvitedGame;
    private RelativeLayout mRlSettingAdmin;
    private AppCompatTextView mTvSettingAdmin;
    private AppCompatButton mBtnFollow;

    private RoomOwnerType mRoomOwnerType;
    //???????????????
    boolean isMute = true;
    private String roomUserId;
    private OnMemberSettingClickListener mOnMemberSettingClickListener;

    // ?????????????????????????????????
    boolean memberIsOnSeat = false;
    // ????????????????????????
    boolean selfIsOnSeat = false;
    // ?????????????????????
    boolean selfIsCaptain = false;
    // ???????????????????????????????????????
    boolean memberIsInGame = false;
    // ?????????????????????
    boolean isGamePlaying = false;
    //???????????????
    int seatPosition = 1;
    private Member member;

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public void setSeatPosition(int seatPosition) {
        this.seatPosition = seatPosition;
    }


    public void setMemberIsOnSeat(boolean memberIsOnSeat) {
        this.memberIsOnSeat = memberIsOnSeat;
    }

    public void setSelfAndMemberInfo(boolean selfIsOnSeat, boolean selfIsCaptain, boolean memberIsInGame, boolean isGamePlaying) {
        this.selfIsOnSeat = selfIsOnSeat;
        this.selfIsCaptain = selfIsCaptain;
        this.memberIsInGame = memberIsInGame;
        this.isGamePlaying = isGamePlaying;
    }

    public MemberSettingFragment(RoomOwnerType roomOwnerType, OnMemberSettingClickListener onMemberSettingClickListener) {
        super(R.layout.fragment_member_setting);
        this.mRoomOwnerType = roomOwnerType;
        this.mOnMemberSettingClickListener = onMemberSettingClickListener;
    }

    @Override
    public void initView() {
        mGlBg = (Guideline) getView().findViewById(R.id.gl_bg);
        mIvMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        mTvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        mTvSeatPosition = (AppCompatTextView) getView().findViewById(R.id.tv_seat_position);
        mClButtons = (ConstraintLayout) getView().findViewById(R.id.cl_buttons);
        mBtnSendGift = (AppCompatButton) getView().findViewById(R.id.btn_send_gift);
        mBtnSendMessage = (AppCompatButton) getView().findViewById(R.id.btn_send_message);
        mClMemberSetting = (ConstraintLayout) getView().findViewById(R.id.cl_member_setting);
        mLlInvitedSeat = (LinearLayout) getView().findViewById(R.id.ll_invited_seat);
        mLlKickSeat = (LinearLayout) getView().findViewById(R.id.ll_kick_seat);
        mLlCloseSeat = (LinearLayout) getView().findViewById(R.id.ll_close_seat);
        mLlMuteSeat = (LinearLayout) getView().findViewById(R.id.ll_mute_seat);
        mIvMuteSeat = (AppCompatImageView) getView().findViewById(R.id.iv_mute_seat);
        mTvMuteSeat = (AppCompatTextView) getView().findViewById(R.id.tv_mute_seat);
        mLlKickRoom = (LinearLayout) getView().findViewById(R.id.ll_kick_room);
        mLlKickGame = (LinearLayout) getView().findViewById(R.id.ll_kick_game);
        mLlSelfEnterSeat = (LinearLayout) getView().findViewById(R.id.ll_self_enter_seat);
        mLlInvitedGame = (LinearLayout) getView().findViewById(R.id.ll_invited_game);
        mRlSettingAdmin = (RelativeLayout) getView().findViewById(R.id.rl_setting_admin);
        mTvSettingAdmin = (AppCompatTextView) getView().findViewById(R.id.tv_setting_admin);
        mBtnFollow = (AppCompatButton) getView().findViewById(R.id.btn_follow);

        refreshView();
    }

    @Override
    public void initListener() {
        super.initListener();
        if (mOnMemberSettingClickListener == null) {
            return;
        }
        mRlSettingAdmin.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickSettingAdmin(member.toUser(), (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show("????????????");
                }
            });
        });
        mLlInvitedSeat.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickInviteSeat(-1, member.toUser(), (result, msg) -> {
                KToast.show(msg);
                if (result) {
                    dismiss();
                }
            });
        });
        mLlKickRoom.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickKickRoom(member.toUser(), (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show(msg);
                }
            });
        });
        mLlKickSeat.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickKickSeat(member.toUser(), (result, msg) -> {
                if (result) {
                    dismiss();
                    KToast.show("????????????????????????");
                } else {
                    KToast.show(msg);
                }
            });
        });
        mLlMuteSeat.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickMuteSeat(seatPosition, !isMute, (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show(msg);
                }
            });
        });
        mLlCloseSeat.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickCloseSeat(seatPosition, true, (result, msg) -> {
                if (result) {
                    dismiss();
                }
                KToast.show(msg);
            });
        });
        mBtnSendGift.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickSendGift(member.toUser());
            dismiss();
        });
        mLlSelfEnterSeat.setOnClickListener(v -> {
            mOnMemberSettingClickListener.switchSelfEnterSeat(member, seatPosition, (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show(msg);
                }
            });
        });

        mLlInvitedGame.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickInvitedGame(member.toUser(), (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show(msg);
                }
            });
        });
        mLlKickGame.setOnClickListener(v -> {
            mOnMemberSettingClickListener.clickKickGame(member.toUser(), (result, msg) -> {
                if (result) {
                    dismiss();
                } else {
                    KToast.show(msg);
                }
            });
        });
        mBtnSendMessage.setOnClickListener(v -> {
            dismiss();
            RouteUtils.routeToConversationActivity(
                    requireContext(),
                    Conversation.ConversationType.PRIVATE,
                    member.getUserId()
            );
        });
        mBtnFollow.setOnClickListener(v -> {
            follow();
        });
    }

    public void show(FragmentManager fragmentManager, User user, String roomUserId) {
        this.member = Member.fromUser(user);
        this.roomUserId = roomUserId;
        show(fragmentManager, TAG);
    }

    public void show(FragmentManager fragmentManager, Member member, String roomUserId) {
        this.member = member;
        this.roomUserId = roomUserId;
        show(fragmentManager, TAG);
    }

    private void refreshView() {

        // ????????????????????????
        boolean selfIsAdmin = MemberCache.getInstance().isAdmin(UserManager.get().getUserId());
        // ?????????????????????????????????
        boolean memberIsAdmin = MemberCache.getInstance().isAdmin(member.getUserId());
        // ??????????????????????????????
        boolean memberIsOwner = TextUtils.equals(roomUserId, member.getUserId());

        // ???????????????
        ImageLoader.loadUrl(mIvMemberPortrait, member.getPortraitUrl(), R.drawable.default_portrait);
        mTvMemberName.setText(member.getUserName());
        // ??????????????????
        setSeatPosition(memberIsOnSeat);
        // ?????????????????????
        refreshSettingAdmin(memberIsAdmin);
        // ????????????????????????view??????
        refreshBottomView(selfIsAdmin, memberIsAdmin, memberIsOnSeat, memberIsOwner);

        refreshFollow(member.isFollow());
    }

    private void refreshBottomView(boolean selfIsAdmin, boolean memberIsAdmin, boolean memberIsOnSeat, boolean memberIsOwner) {
        mLlInvitedSeat.setVisibility(View.GONE);
        mLlKickSeat.setVisibility(View.GONE);
        mLlSelfEnterSeat.setVisibility(View.GONE);
        mLlCloseSeat.setVisibility(View.GONE);
        mLlMuteSeat.setVisibility(View.GONE);
        mLlKickRoom.setVisibility(View.GONE);
        mLlInvitedGame.setVisibility(View.GONE);
        mLlKickGame.setVisibility(View.GONE);
        switch (mRoomOwnerType) {
            case VOICE_OWNER:
            case LIVE_OWNER:
                if (memberIsOwner) {
                    mClButtons.setVisibility(View.INVISIBLE);
                    mClMemberSetting.setVisibility(View.GONE);
                    mTvSeatPosition.setVisibility(View.INVISIBLE);
                    mRlSettingAdmin.setVisibility(View.GONE);
                    break;
                }
                mClMemberSetting.setVisibility(View.VISIBLE);
                // ?????????
                if (memberIsOnSeat) {
                    mLlKickSeat.setVisibility(View.VISIBLE);
                    mLlMuteSeat.setVisibility(View.VISIBLE);
                    mLlCloseSeat.setVisibility(View.VISIBLE);
                    //??????????????????
                    if (isMute) {
                        mIvMuteSeat.setImageResource(R.drawable.ic_room_setting_unmute_all);
                        mTvMuteSeat.setText("????????????");
                    } else {
                        mIvMuteSeat.setImageResource(R.drawable.ic_member_setting_mute_seat);
                        mTvMuteSeat.setText("????????????");
                    }
                } else {
                    mLlInvitedSeat.setVisibility(View.VISIBLE);
                }
                // ????????????
                mLlKickRoom.setVisibility(View.VISIBLE);
                break;
            case LIVE_VIEWER:
            case VOICE_VIEWER:
                // ???????????????????????????,??????????????????,?????????????????????
                if (selfIsAdmin && memberIsAdmin || memberIsOwner) {
                    mClMemberSetting.setVisibility(View.GONE);
                } else if (selfIsAdmin) { // ???????????????????????????????????????
                    mClMemberSetting.setVisibility(View.VISIBLE);
                    // ????????????
                    mLlKickRoom.setVisibility(View.VISIBLE);
                    // ???????????????
                    if (memberIsOnSeat) {
                        mLlKickSeat.setVisibility(View.VISIBLE);
                    } else {
                        mLlInvitedSeat.setVisibility(View.VISIBLE);
                    }
                } else {
                    // ?????????????????????????????????
                    mClMemberSetting.setVisibility(View.GONE);
                }
                break;
            case RADIO_OWNER:
                mClMemberSetting.setVisibility(View.VISIBLE);
                // ????????????
                mLlKickRoom.setVisibility(View.VISIBLE);
                break;
            case RADIO_VIEWER:
                // ???????????????????????????,??????????????????,?????????????????????
                if (selfIsAdmin && memberIsAdmin || memberIsOwner) {
                    mClMemberSetting.setVisibility(View.GONE);
                } else if (selfIsAdmin) { // ???????????????????????????????????????
                    mClMemberSetting.setVisibility(View.VISIBLE);
                    // ????????????
                    mLlKickRoom.setVisibility(View.VISIBLE);
                } else {
                    // ?????????????????????????????????
                    mClMemberSetting.setVisibility(View.GONE);
                }
                break;

            case GAME_OWNER:
                mTvSeatPosition.setVisibility(View.GONE);
                if (memberIsOwner) {
                    mClButtons.setVisibility(View.INVISIBLE);
                    mClMemberSetting.setVisibility(View.GONE);
                    mRlSettingAdmin.setVisibility(View.GONE);
                    break;
                }
                mClMemberSetting.setVisibility(View.VISIBLE);
                // ?????????
                if (memberIsOnSeat) {
                    mLlKickSeat.setVisibility(View.VISIBLE);
                    mLlMuteSeat.setVisibility(View.VISIBLE);
                    mLlCloseSeat.setVisibility(View.VISIBLE);
                    //??????????????????
                    if (isMute) {
                        mIvMuteSeat.setImageResource(R.drawable.ic_room_setting_unmute_all);
                        mTvMuteSeat.setText("????????????");
                    } else {
                        mIvMuteSeat.setImageResource(R.drawable.ic_member_setting_mute_seat);
                        mTvMuteSeat.setText("????????????");
                    }
                    if (!selfIsOnSeat) {
                        mLlSelfEnterSeat.setVisibility(View.VISIBLE);
                    }

                } else {
                    mLlInvitedSeat.setVisibility(View.VISIBLE);
                }
                // ????????????
                mLlKickRoom.setVisibility(View.VISIBLE);
                // ??????????????????????????????
                if (selfIsCaptain && !isGamePlaying) {
                    if (memberIsInGame) {
                        mLlKickGame.setVisibility(View.VISIBLE);
                    } else {
                        mLlInvitedGame.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case GAME_VIEWER:
                mTvSeatPosition.setVisibility(View.GONE);
                // ???????????????????????????,??????????????????,?????????????????????
                if (selfIsAdmin && memberIsAdmin || memberIsOwner) {
                    mClMemberSetting.setVisibility(View.GONE);
                } else if (selfIsAdmin) { // ???????????????????????????????????????
                    mClMemberSetting.setVisibility(View.VISIBLE);
                    // ????????????
                    mLlKickRoom.setVisibility(View.VISIBLE);
                    // ???????????????
                    if (memberIsOnSeat) {
                        mLlKickSeat.setVisibility(View.VISIBLE);
                        // ????????????????????????????????????
                        if (!selfIsOnSeat) {
                            mLlSelfEnterSeat.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mLlInvitedSeat.setVisibility(View.VISIBLE);
                    }
                } else {
                    // ?????????????????????????????????
                    mClMemberSetting.setVisibility(View.GONE);
                }
                // ??????????????????????????????
                if (selfIsCaptain && !isGamePlaying) {
                    mClMemberSetting.setVisibility(View.VISIBLE);
                    if (memberIsInGame) {
                        mLlKickGame.setVisibility(View.VISIBLE);
                    } else {
                        mLlInvitedGame.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    private void refreshFollow(boolean isFollow) {
        if (isFollow) {
            mBtnFollow.setText("?????????");
        } else {
            mBtnFollow.setText("??????");
        }
    }

    private void refreshSettingAdmin(boolean memberIsAdmin) {
        switch (mRoomOwnerType) {
            case VOICE_OWNER:
            case RADIO_OWNER:
            case LIVE_OWNER:
            case GAME_OWNER:
                mRlSettingAdmin.setVisibility(View.VISIBLE);
                if (memberIsAdmin) {
                    mTvSettingAdmin.setText("????????????");
                    mTvSettingAdmin.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_member_setting_is_admin,
                            0,
                            0,
                            0
                    );
                } else {
                    mTvSettingAdmin.setText("????????????");
                    mTvSettingAdmin.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_member_setting_not_admin,
                            0,
                            0,
                            0
                    );
                }
                break;
            default:
                mRlSettingAdmin.setVisibility(View.GONE);
                break;
        }
    }


    private void setSeatPosition(boolean memberIsOnSeat) {
        if (memberIsOnSeat) {
            mTvSeatPosition.setVisibility(View.VISIBLE);
            // TODO???????????????????????????

            mTvSeatPosition.setText(String.format("%s ?????????", seatPosition));
        } else {
            mTvSeatPosition.setVisibility(View.GONE);
        }
    }

    /**
     * ??????
     */
    private void follow() {
        boolean isFollow = !member.isFollow();
        OkApi.get(VRApi.followUrl(member.getUserId()), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    if (isFollow) {
                        ToastUtils.s(getContext(), "????????????");
                        if (mOnMemberSettingClickListener != null) {
                            RCFollowMsg followMsg = new RCFollowMsg();
                            followMsg.setUser(UserManager.get());
                            followMsg.setTargetUser(member.toUser());
                            mOnMemberSettingClickListener.clickFollow(true, followMsg);
                        }
                    } else {
                        ToastUtils.s(getContext(), "??????????????????");
                        if (mOnMemberSettingClickListener != null) {
                            mOnMemberSettingClickListener.clickFollow(false, null);
                        }
                    }
                    member.setStatus(isFollow ? 1 : 0);
                    refreshFollow(isFollow);
                    dismiss();
                } else {
                    if (isFollow) {
                        ToastUtils.s(getContext(), "????????????");
                    } else {
                        ToastUtils.s(getContext(), "??????????????????");
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (isFollow) {
                    ToastUtils.s(getContext(), "????????????");
                } else {
                    ToastUtils.s(getContext(), "??????????????????");
                }
            }
        });
    }

    public interface OnMemberSettingClickListener extends SeatActionClickListener {
        /**
         * ???????????????
         *
         * @param user
         * @param callback
         */
        void clickSettingAdmin(User user, ClickCallback<Boolean> callback);

        /**
         * ????????????
         *
         * @param user
         * @param callback
         */
        void clickKickRoom(User user, ClickCallback<Boolean> callback);


        /**
         * ????????????
         *
         * @param user
         */
        void clickSendGift(User user);

        /**
         * ??????
         *
         * @param followMsg
         */
        void clickFollow(boolean isFollow, RCFollowMsg followMsg);
    }
}
