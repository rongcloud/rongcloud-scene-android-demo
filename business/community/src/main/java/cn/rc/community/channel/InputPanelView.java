package cn.rc.community.channel;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.UIStack;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.SoftKeyboardUtils;
import com.basis.utils.UIKit;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.PluginAdapter;
import cn.rc.community.R;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.dialog.IMessageOperation;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.plugins.IPlugin;
import cn.rc.community.plugins.ImagePlugin;
import cn.rc.community.setting.member.SelectMentionMemberActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imkit.feature.mention.IMentionedInputListener;
import io.rong.imkit.feature.mention.RongMentionManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.ReferenceMessage;
import io.rong.message.TextMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/10
 * @time 6:55 ??????
 * ?????????view ??????
 */
public class InputPanelView extends FrameLayout implements View.OnClickListener, IMessageOperation {

    private InputListener inputListener;
    private BottomViewListener bottomViewListener;


    private AppCompatImageView ivExtendedFun;
    private AppCompatImageView ivEmoji;
    private EditText editText;
    private EmojiPopup mEmojiPopup;
    private RecyclerView rcPluginId;
    private AppCompatButton inputPanelSendBtn;
    private ArrayList<IPlugin> pluginModules;
    private RelativeLayout rootView;
    private LinearLayout llTextId;
    private ImageView ivTextIcon;
    private TextView tvMessageId;
    private ImageView ivDelete;
    private FrameLayout inputPanelAddOrSend;
    private AppCompatImageView ivMeme;
    private int editStatus = Constants.MessageEditStatus.EDIT_NORMAL_MESSAGE.getType();
    private WrapperMessage wrapperMessage;//????????????????????????
    private int inputStatus = InputStatus.editEnd;//????????????
    private boolean isShowPlugin = false;
    private boolean isKeyboardOpen = false;
    private View tvShutUp;


    public InputPanelView(Context context) {
        super(context);
    }

    public InputPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_input_pannel, this, false);
        addView(view);
        initView();
        initListener();
    }

    /**
     * ????????????????????????
     *
     * @param inputListener
     */
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setBottomViewListener(BottomViewListener bottomViewListener) {
        this.bottomViewListener = bottomViewListener;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public EditText getEditText() {
        return editText;
    }

    /**
     * ???????????????
     */
    private void initView() {
        ivExtendedFun = (AppCompatImageView) findViewById(R.id.iv_extended_fun);
        ivEmoji = (AppCompatImageView) findViewById(R.id.iv_meme);
        editText = (EditText) findViewById(R.id.et_id);
        tvShutUp = findViewById(R.id.tv_shut_up);
        rcPluginId = (RecyclerView) findViewById(R.id.rc_plugin_id);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        llTextId = (LinearLayout) findViewById(R.id.ll_text_id);
        ivTextIcon = (ImageView) findViewById(R.id.iv_text_icon);
        tvMessageId = (TextView) findViewById(R.id.tv_message_id);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        inputPanelAddOrSend = (FrameLayout) findViewById(R.id.input_panel_add_or_send);
        ivMeme = (AppCompatImageView) findViewById(R.id.iv_meme);
        inputPanelSendBtn = (AppCompatButton) findViewById(R.id.input_panel_send_btn);
        initFuns();
    }

    private void initListener() {
        mEmojiPopup = EmojiPopup
                .Builder
                .fromRootView(this)
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setOnEmojiPopupShownListener(() -> {
                    ivEmoji.setImageResource(R.drawable.svg_channel_keyboard);
                })
                .setOnSoftKeyboardCloseListener(() -> {
                    isKeyboardOpen = false;
                    //?????????????????? 
                    if (isShowPlugin) showPluginView();
                    if (editText.hasFocus()) editText.clearFocus();
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(int keyBoardHeight) {
                        isKeyboardOpen = true;
                        hidePluginView();
                        if (bottomViewListener != null)
                            bottomViewListener.onKeyboardOpen(keyBoardHeight);
                    }
                })
                .setOnEmojiPopupDismissListener(() -> {
                    ivEmoji.setImageResource(R.drawable.svg_text_channel_meme);
                }).build(editText);
        ivEmoji.setOnClickListener(v -> {
            if (inputListener != null) {
                boolean intercept = inputListener.onClickEmoji();
                if (intercept) {

                } else {
                    rcPluginId.setVisibility(GONE);
                    mEmojiPopup.toggle();
                }
            }
        });
        editText.setOnFocusChangeListener(mOnEditTextFocusChangeListener);
        editText.addTextChangedListener(mEditTextWatcher);
        ivExtendedFun.setOnClickListener(this);
        inputPanelSendBtn.setOnClickListener(this);
        ivDelete.setOnClickListener(this);

        RongMentionManager.getInstance().createInstance(Conversation.ConversationType.ULTRA_GROUP,
                CommunityHelper.getInstance().getChannelUid(), editText);
        RongMentionManager.getInstance().setMentionedInputListener(new IMentionedInputListener() {
            @Override
            public boolean onMentionedInput(Conversation.ConversationType conversationType, String targetId) {
                UIKit.startActivity(UIStack.getInstance().getTopActivity(), SelectMentionMemberActivity.class);
                return true;
            }
        });
        editText.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL
                                && event.getAction() == KeyEvent.ACTION_DOWN) {
                            int cursorPos = editText.getSelectionStart();
                            RongMentionManager.getInstance()
                                    .onDeleteClick(
                                            Conversation.ConversationType.ULTRA_GROUP,
                                            CommunityHelper.getInstance().getChannelUid(),
                                            editText,
                                            cursorPos);
                        }
                        return false;
                    }
                });
    }


    /**
     * ????????????????????????
     */
    private OnFocusChangeListener mOnEditTextFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (editText != null) {
                    int length = editText.getText().length();
                    if (length > 0) editText.setSelection(length);
                }
                SoftKeyboardUtils.showSoftKeyboard(editText);
            } else {
                if (isKeyboardOpen) SoftKeyboardUtils.hideSoftKeyboard(editText);
            }
        }
    };

    /**
     * ????????????
     */
    public void dismiss() {
        if (isKeyboardOpen) {
            SoftKeyboardUtils.hideSoftKeyboard(editText);
        }
        hidePluginView();
    }

    /**
     * ???????????????view??????????????????
     *
     * @param visibility
     */
    public void setShutUpViewVisibility(int visibility) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                if (tvShutUp != null) {
                    tvShutUp.setVisibility(visibility);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_extended_fun) {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            if (isKeyboardOpen) {
                isShowPlugin = true;
                editText.clearFocus();
            } else {
                //???????????????????????????????????????
                if (rcPluginId.getVisibility() == VISIBLE) {
                    hidePluginView();
                } else {
                    showPluginView();
                }
            }
        } else if (view.getId() == R.id.input_panel_send_btn) {
            if (inputListener != null) {
                if (editStatus == Constants.MessageEditStatus.EDIT_NORMAL_MESSAGE.getType()) {
                    TextMessage textMessage = TextMessage.obtain(editText.getText().toString());
                    inputListener.onClickSend(textMessage);

                } else if (editStatus == Constants.MessageEditStatus.EDIT_REEDIT_MESSAGE.getType()) {
                    Message message = wrapperMessage.getMessage();
                    if (message.getContent() instanceof TextMessage) {
                        //????????????????????????
                        ((TextMessage) message.getContent()).setContent(editText.getText().toString().trim());
                        inputListener.reEditMessage(wrapperMessage);
                    } else if (message.getContent() instanceof ReferenceMessage) {
                        ReferenceMessage referenceMessage = (ReferenceMessage) message.getContent();
                        referenceMessage.setEditSendText(editText.getText().toString().trim());
                        inputListener.reEditMessage(wrapperMessage);
                    }
                } else if (editStatus == Constants.MessageEditStatus.EDIT_QUOTE_MESSAGE.getType()) {
                    inputListener.quoteMessage(wrapperMessage, editText.getText().toString().trim());
                }
                editText.setText("");
                //??????????????????????????????
                llTextId.setVisibility(GONE);
                editStatus = Constants.MessageEditStatus.EDIT_NORMAL_MESSAGE.getType();
            }
        } else if (view.getId() == R.id.iv_delete) {
            //??????????????????????????????
            llTextId.setVisibility(GONE);
            editStatus = Constants.MessageEditStatus.EDIT_NORMAL_MESSAGE.getType();
        }
    }

    private TextWatcher mEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.length() == 0) {
                ivExtendedFun.setVisibility(VISIBLE);
                inputPanelSendBtn.setVisibility(GONE);
            } else {
                ivExtendedFun.setVisibility(GONE);
                inputPanelSendBtn.setVisibility(VISIBLE);
            }
            if (inputStatus == InputStatus.editEnd) {
                inputStatus = InputStatus.editing;
                //??????
                MessageManager.get().sendUltraGroupTypingStatus(null);
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .take(6)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Throwable {
                                if (aLong == 5) {
                                    inputStatus = InputStatus.editEnd;
                                }
                            }
                        });
            }

            int cursor, offset;
            if (count == 0) {
                cursor = start + before;
                offset = -before;
            } else {
                cursor = start;
                offset = count;
            }
            RongMentionManager.getInstance()
                    .onTextChanged(
                            getContext(),
                            Conversation.ConversationType.ULTRA_GROUP,
                            CommunityHelper.getInstance().getChannelUid(),
                            cursor,
                            offset,
                            s.toString(),
                            editText);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * ?????????????????????
     */
    private void initFuns() {
        rcPluginId.setLayoutManager(new GridLayoutManager(getContext(), 3));
        pluginModules = new ArrayList<>();
        pluginModules.add(new ImagePlugin());
        pluginModules.add(new ImagePlugin(ResUtil.getDrawable(R.drawable.svg_ic_video)
                , ResUtil.getString(R.string.cmu_str_video)));
//        pluginModules.add(new FilePlugin());
        PluginAdapter pluginAdapter = new PluginAdapter(pluginModules);
        rcPluginId.setAdapter(pluginAdapter);
        pluginAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                pluginModules.get(position).onClick(((Activity) getContext()), position);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (IPlugin pluginModule : pluginModules) {
            pluginModule.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * ????????????????????????
     *
     * @param iMessage
     */
    @Override
    public void edit(WrapperMessage iMessage) {
        editStatus = Constants.MessageEditStatus.EDIT_REEDIT_MESSAGE.getType();
        this.wrapperMessage = iMessage;
        ivTextIcon.setImageResource(R.drawable.svg_mini_edit);
        llTextId.setVisibility(VISIBLE);
        MessageContent msgContent = iMessage.getMessage().getContent();
        if (msgContent instanceof TextMessage) {
            String content = ((TextMessage) msgContent).getContent();
            tvMessageId.setText(content);
            editText.setText(content);
        } else if (msgContent instanceof ReferenceMessage) {
            String editSendText = ((ReferenceMessage) msgContent).getEditSendText();
            tvMessageId.setText(editSendText);
            editText.setText(editSendText);
        }
        editText.requestFocus();
    }

    /**
     * ??????????????????
     *
     * @param iMessage
     */
    @Override
    public void quote(WrapperMessage iMessage) {
        editStatus = Constants.MessageEditStatus.EDIT_QUOTE_MESSAGE.getType();
        this.wrapperMessage = iMessage;
        ivTextIcon.setImageResource(R.drawable.svg_mini_quote);
        llTextId.setVisibility(VISIBLE);
        MessageContent msgContent = iMessage.getMessage().getContent();
        tvMessageId.setText(MessageManager.get().messageToContent(msgContent));
        editText.setText("");
        editText.requestFocus();
    }

    /**
     * ??????????????????
     *
     * @param iMessage
     */
    @Override
    public void annotation(WrapperMessage iMessage) {
        this.wrapperMessage = iMessage;
        //????????????????????????
        HashMap map = new HashMap();
        map.put("channelUid", iMessage.getMessage().getChannelId());
        map.put("messageUid", iMessage.getMessage().getUId());
        OkApi.post(CommunityAPI.MARK_MSG, map, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show("????????????");
                    //?????????????????????
                } else {
                    KToast.show("????????????:" + result.getMessage());
                }
            }
        });
    }

    @Override
    public void cancelAnnotation(WrapperMessage iMessage) {
        OkApi.post(CommunityAPI.REMOVE_MARK_MSG + iMessage.getMessage().getUId(), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show("??????????????????");
                } else {
                    KToast.show("??????????????????:" + result.getMessage());
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    private void showPluginView() {
        rcPluginId.setVisibility(VISIBLE);
        isShowPlugin = false;
        if (bottomViewListener != null && rcPluginId.getVisibility() == VISIBLE)
            bottomViewListener.onPluginShow();
    }

    /**
     * ??????????????????
     */
    private void hidePluginView() {
        if (rcPluginId.getVisibility() == VISIBLE) rcPluginId.setVisibility(GONE);
    }

    /**
     * ??????????????????
     *
     * @param iMessage
     */
    @Override
    public void copy(WrapperMessage iMessage) {
        //???????????????????????????
        try {
            MessageContent msgContent = iMessage.getMessage().getContent();
            String content = null;
            if (msgContent instanceof TextMessage) {
                content = ((TextMessage) msgContent).getContent();
            } else if (msgContent instanceof ReferenceMessage) {
                content = ((ReferenceMessage) msgContent).getEditSendText();
            }
            ClipboardManager cm = (ClipboardManager) UIKit.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", content);
            cm.setPrimaryClip(mClipData);
            KToast.show(ResUtil.getString(R.string.cmu_str_copy_to_the_paste_board));
        } catch (Exception e) {
            KToast.show(e.toString());
        }
    }

    /**
     * ??????????????????
     *
     * @param iMessage
     */
    @Override
    public void delete(WrapperMessage iMessage) {
        MessageManager.get().deleteMessage(iMessage);
    }

    /**
     * ??????????????????
     *
     * @param iMessage
     */
    @Override
    public void reCall(WrapperMessage iMessage) {
        MessageManager.get().recallMessage(iMessage);
    }

    /**
     * ????????????
     */
    public interface InputListener {

        /**
         * ????????????
         *
         * @param message
         */
        void onClickSend(TextMessage message);

        void reEditMessage(WrapperMessage message);

        void quoteMessage(WrapperMessage message, String editSendText);

        boolean onClickEmoji();
    }

    public interface BottomViewListener {

        void onKeyboardOpen(int keyBoardHeight);

        void onPluginShow();
    }

    static class InputStatus {
        static int editing = 1;//????????????
        static int editEnd = 2;//????????????
    }
}
