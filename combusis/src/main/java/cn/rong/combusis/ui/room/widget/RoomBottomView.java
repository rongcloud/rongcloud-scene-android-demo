package cn.rong.combusis.ui.room.widget;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.UiUtils;
import com.vanniktech.emoji.EmojiPopup;

import cn.rong.combusis.R;
import cn.rong.combusis.common.utils.SoftKeyboardUtils;
import cn.rong.combusis.manager.AudioPlayManager;
import cn.rong.combusis.manager.AudioRecordManager;
import cn.rong.combusis.message.RCChatroomVoice;
import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.sdk.event.EventHelper;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imkit.utils.RongUtils;
import io.rong.imlib.IMLibExtensionModuleManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.HardwareResource;

/**
 * @author gyn
 * @date 2021/9/17
 */
public class RoomBottomView extends ConstraintLayout implements UnReadMessageManager.IUnReadMessageObserver {
    private View mRootView;
    /**
     * 发送文字的view
     */
    private RelativeLayout mSendMessageView;
    /**
     * 发送语音
     */
    private ImageView mSendVoiceMassageView;
    /**
     * 座位列表
     */
    private ImageView mSeatOrder;
    /**
     * 申请座位的人数
     */
    private TextView mSeatOrderNumber;
    /**
     * 设置
     */
    private ImageView mSettingView;
    /**
     * 私信
     */
    private ImageView mPrivateMessageView;
    /**
     * 私信条数
     */
    private TextView mPrivateMessageCountView;
    /**
     * 发送礼物
     */
    private ImageView mSendGiftView;
    /**
     * 发起pk或挂断pk
     */
    private ImageView mPkView;
    /**
     * 申请上麦
     */
    private ImageView mRequestSeatView;
    /**
     * 底部输入框整体
     */
    private ConstraintLayout mInputBar;
    /**
     * 输入框
     */
    private EditText mInputView;
    /**
     * emoji
     */
    private ImageView mEmojiView;
    /**
     * 发送按钮
     */
    private Button mSendButton;
    /**
     * emoji选择框
     */
    private EmojiPopup mEmojiPopup;

    private OnBottomOptionClickListener mOnBottomOptionClickListener;
    private AudioRecordManager audioRecordManager;

    private long showSoftKeyboardTime = 0L;
    private String roomId;
    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO};
            if (!PermissionCheckUtil.checkPermissions(
                    v.getContext(),
                    permissions
            ) && event.getAction() == MotionEvent.ACTION_DOWN
            ) {
                PermissionCheckUtil.requestPermissions(
                        ((FragmentActivity) v.getContext()),
                        permissions,
                        PermissionCheckUtil.REQUEST_CODE_ASK_PERMISSIONS
                );
                return true;
            }
            int[] location = new int[2];
            mSendVoiceMassageView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //在这里拦截外部的滑动事件
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (AudioPlayManager.getInstance().isPlaying()) {
                    AudioPlayManager.getInstance().stopPlay();
                }
                //判断正在视频通话和语音通话中不能进行语音消息发送
                RCVoiceSeatInfo seatInfo = EventHelper.helper().getSeatInfo(AccountStore.INSTANCE.getUserId());
                boolean isMic = RongUtils.phoneIsInUse(v.getContext()) || IMLibExtensionModuleManager.getInstance()
                        .onRequestHardwareResource(HardwareResource.ResourceType.VIDEO)
                        || IMLibExtensionModuleManager.getInstance()
                        .onRequestHardwareResource(HardwareResource.ResourceType.AUDIO);
                //如果麦克风被占用
                if (isMic) {
                    //不在麦位上，或者在麦位上但是没有被禁麦,
                    if (seatInfo == null || (seatInfo != null && !seatInfo.isMute())) {
                        EToast.showToast("麦克风被占用，不可发送语音");
                        return true;
                    }
                }
                audioRecordManager.startRecord(v.getRootView(), Conversation.ConversationType.PRIVATE, roomId);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getRawX() <= 0 || event.getRawX() > x + v.getWidth() || event.getRawY() < y) {
                    audioRecordManager.willCancelRecord();
                } else {
                    audioRecordManager.continueRecord();
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
                audioRecordManager.stopRecord();
            }
            return true;
        }
    };

    public RoomBottomView(@NonNull Context context) {
        this(context, null);
    }

    public RoomBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_room_bottom, this);
        initView();
    }

    private void initView() {
        mSendMessageView = mRootView.findViewById(R.id.rl_send_message_id);
        mSendVoiceMassageView = mRootView.findViewById(R.id.iv_send_voice_message_id);
        mSeatOrder = mRootView.findViewById(R.id.btn_seat_order);
        mSeatOrderNumber = mRootView.findViewById(R.id.tv_seat_order_operation_number);
        mSettingView = mRootView.findViewById(R.id.iv_room_setting);
        mPrivateMessageView = mRootView.findViewById(R.id.iv_send_message);
        mPrivateMessageCountView = mRootView.findViewById(R.id.tv_unread_message_number);
        mSendGiftView = mRootView.findViewById(R.id.iv_send_gift);
        mPkView = mRootView.findViewById(R.id.iv_request_pk);
        mPkView.setSelected(false);
        mRequestSeatView = mRootView.findViewById(R.id.iv_request_enter_seat);
        mInputBar = mRootView.findViewById(R.id.cl_input_bar);
        mInputView = mRootView.findViewById(R.id.et_message);
        mEmojiView = mRootView.findViewById(R.id.btn_emoji_keyboard);
        mSendButton = mRootView.findViewById(R.id.btn_send_message);
        mEmojiPopup = EmojiPopup
                .Builder
                .fromRootView(mRootView)
                .setOnEmojiPopupShownListener(() -> {
                    mEmojiView.setImageResource(R.drawable.ic_voice_room_keybroad);
                })
                .setOnEmojiPopupDismissListener(() -> {
                    mEmojiView.setImageResource(R.drawable.ic_voice_room_emoji);
                }).build(mInputView);
        // 点击消息区域
        mInputView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mInputBar.getVisibility() != VISIBLE) {
                    return;
                }
                if (hasFocus) {
                    showSoftKeyboardTime = System.currentTimeMillis();
                    SoftKeyboardUtils.showSoftKeyboard(mInputView);
                } else {
                    // 解决魅蓝Note5,获取焦点后系统立马又自动取消焦点，导致键盘不能弹出
                    // 这里看获取焦点又取消焦点的间隔小于500ms就再打开键盘
                    long diff = System.currentTimeMillis() - showSoftKeyboardTime;
                    if (diff < 500) {
                        showSoftKeyboardTime = 0;
                        SoftKeyboardUtils.showSoftKeyboard(mInputView);
                    }
                }
            }
        });
        mInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //发送消息
                    Editable msg = mInputView.getText();
                    if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(msg.toString().trim())) {
                        EToast.showToast("消息不能为空");
                        return false;
                    }
                    mOnBottomOptionClickListener.clickSendMessage(msg.toString().trim());
                    return true;
                }
                return false;
            }
        });
        mSendMessageView.setOnClickListener(v -> {
            mInputBar.setVisibility(VISIBLE);
            mInputView.requestFocus();
        });

        // 点击emoji
        mEmojiView.setOnClickListener(v -> {
            mEmojiPopup.toggle();
        });

        audioRecordManager = new AudioRecordManager();
        audioRecordManager.setOnSendVoiceMessageClickListener(new AudioRecordManager.OnSendVoiceMessageClickListener() {
            @Override
            public void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice) {
                mOnBottomOptionClickListener.onSendVoiceMessage(rcChatroomVoice);
            }
        });
        // 语音
        mSendVoiceMassageView.setOnTouchListener(onTouchListener);
        // 私密消息数量监听
        UnReadMessageManager.getInstance().addObserver(new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE}, this);
    }

    /**
     * 设置麦位申请人数
     *
     * @param number
     */
    public void setmSeatOrderNumber(int number) {
        if (number > 0) {
            mSeatOrderNumber.setText(number + "");
            mSeatOrderNumber.setVisibility(VISIBLE);
        } else {
            mSeatOrderNumber.setVisibility(GONE);
        }
    }

    /**
     * 隐藏输入框
     */
    public void hideSoftKeyboardAndInput() {
        if (mInputBar.getVisibility() == VISIBLE) {
            mInputBar.setVisibility(View.GONE);
            mInputView.clearFocus();
            SoftKeyboardUtils.hideSoftKeyboard(mInputView);
        }
    }

    /**
     * 设置申请上面的按钮的图
     */
    public void setRequestSeatImage(int drawable) {
        mRequestSeatView.setImageResource(drawable);
    }

    public void clearInput() {
        mInputView.setText("");
    }

    public void setData(RoomOwnerType roomOwnerType, OnBottomOptionClickListener onBottomOptionClickListener, String roomId) {
        this.roomId = roomId;
        setViewState(roomOwnerType);
        this.mOnBottomOptionClickListener = onBottomOptionClickListener;
        if (onBottomOptionClickListener != null) {
            mSettingView.setOnClickListener(v -> {
                onBottomOptionClickListener.clickSettings();
            });
            mPrivateMessageView.setOnClickListener(v -> {
                onBottomOptionClickListener.clickPrivateMessage();
            });
            mRequestSeatView.setOnClickListener(v -> {
                onBottomOptionClickListener.clickRequestSeat();
            });

            mSeatOrder.setOnClickListener(v -> {
                onBottomOptionClickListener.clickSeatOrder();
            });
            mSendButton.setOnClickListener(v -> {
                Editable msg = mInputView.getText();
                if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(msg.toString().trim())) {
                    EToast.showToast("消息不能为空");
                    return;
                }
                onBottomOptionClickListener.clickSendMessage(msg.toString().trim());
            });
            mPkView.setOnClickListener(v -> {
                onBottomOptionClickListener.clickPk();
            });
            mSendGiftView.setOnClickListener(v -> {
                onBottomOptionClickListener.onSendGift();
            });
        }
    }

//    public void refreshPkState() {
//        if (null != mPkView) {
//            IEventHelp.Type type = EventHelper.helper().getPKState();
//            Logger.e("refreshPkState", "state = " + type);
//            if (IEventHelp.Type.PK_NONE == type
//                    || IEventHelp.Type.PK_FINISH == type
//                    || IEventHelp.Type.PK_STOP == type) {// 可以发起邀请
//                mPkView.setImageResource(R.drawable.ic_request_pk);
//            } else if (IEventHelp.Type.PK_INVITE == type) {//邀请等 ->待
//                mPkView.setImageResource(R.drawable.ic_wait_enter_seat);
//            } else if (IEventHelp.Type.PK_GOING == type
//                    || IEventHelp.Type.PK_PUNISH == type
//                    || IEventHelp.Type.PK_START == type
//            ) {// pk中
//                mPkView.setImageResource(R.drawable.ic_pk_close);
//            }
//        }
//    }

    /**
     * 控制各种房间状态下按钮的显示
     *
     * @param roomOwnerType 房间所属类型
     */
    private void setViewState(RoomOwnerType roomOwnerType) {
        switch (roomOwnerType) {
            case VOICE_OWNER:
                mSeatOrder.setVisibility(VISIBLE);
//                mPkView.setVisibility(VISIBLE);
                mPkView.setVisibility(GONE);
                mSendGiftView.setVisibility(VISIBLE);
                mPrivateMessageView.setVisibility(VISIBLE);
                mRequestSeatView.setVisibility(GONE);
                mSettingView.setVisibility(VISIBLE);
                mSendVoiceMassageView.setVisibility(GONE);
                break;
            case VOICE_VIEWER:
                mSeatOrder.setVisibility(GONE);
                mPkView.setVisibility(GONE);
                mSendGiftView.setVisibility(VISIBLE);
                mPrivateMessageView.setVisibility(VISIBLE);
                mRequestSeatView.setVisibility(VISIBLE);
                mSettingView.setVisibility(GONE);
                mSendVoiceMassageView.setVisibility(VISIBLE);
                break;
            case RADIO_OWNER:
                mSeatOrder.setVisibility(GONE);
                mPkView.setVisibility(GONE);
                mSendGiftView.setVisibility(VISIBLE);
                mPrivateMessageView.setVisibility(VISIBLE);
                mSettingView.setVisibility(VISIBLE);
                mRequestSeatView.setVisibility(GONE);
                mSendVoiceMassageView.setVisibility(GONE);
                break;
            case RADIO_VIEWER:
                mSeatOrder.setVisibility(GONE);
                mPkView.setVisibility(GONE);
                mSendGiftView.setVisibility(VISIBLE);
                mPrivateMessageView.setVisibility(VISIBLE);
                mSettingView.setVisibility(GONE);
                mRequestSeatView.setVisibility(GONE);
                mSendVoiceMassageView.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    public void onCountChanged(int i) {
        mPrivateMessageCountView.setVisibility(i > 0 ? VISIBLE : GONE);
        mPrivateMessageCountView.setText(i < 99 ? String.valueOf(i) : "99+");
    }

    @Override
    protected void onDetachedFromWindow() {
        UnReadMessageManager.getInstance().removeObserver(this);
        super.onDetachedFromWindow();
    }

    public Point getGiftViewPoint() {
        int[] location = UiUtils.INSTANCE.getLocation(mSendGiftView);
        return new Point(location[0], location[1] - mSendGiftView.getHeight() / 2);
    }

    public interface OnBottomOptionClickListener {
        void clickSendMessage(String message);

        void clickPrivateMessage();

        void clickSeatOrder();

        void clickSettings();

        void clickPk();

        void clickRequestSeat();

        void onSendGift();

        void onSendVoiceMessage(RCChatroomVoice rcChatroomVoice);
    }
}
