package cn.rc.community.channel.TextChannel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.ui.UIStack;
import com.basis.ui.mvp.BaseMvpFragment;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.UiUtils;
import com.basis.wapper.IResultBack;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.UltraKvKey;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.channel.InputPanelView;
import cn.rc.community.channel.details.MarkMessageActivity;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.controller.interfaces.IMessageAdapter;
import cn.rc.community.dialog.MessageOperationFragment;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import io.reactivex.rxjava3.disposables.Disposable;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.HistoryMessageOption;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.RecallNotificationMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/10
 * @time 3:23 ??????
 * ????????????????????????
 */

public class TextChannelFragment extends BaseMvpFragment<TextChannelPresent> implements View.OnClickListener
        , IMessageAdapter.OnItemLongClickListener<WrapperMessage>, IMessageAdapter.OnItemClickListener<WrapperMessage>
        , OnRefreshLoadMoreListener, ITextChannelView {

    private InputPanelView inputPanelId;
    private RecyclerView rcMessageList;
    private TextView tvEmpty;
    private SmartRefreshLayout refreshLayout;
    private int pageNum = 20;
    private TextView tvEditingStatusId;
    private TextView tvMarkId;
    private ImageView ivCloseMarkId;
    private LinearLayout llMarkId;
    private TextView tvUnread;
    private ImageView ivGoDown;
    private LinearLayoutManager linearLayoutManager;

    public static TextChannelFragment getInstance() {
        TextChannelFragment textChannelFragment = new TextChannelFragment();
        return textChannelFragment;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_text_channel;
    }

    @Override
    public void init() {
        inputPanelId = (InputPanelView) getView().findViewById(R.id.input_pannel_id);
        rcMessageList = (RecyclerView) getView().findViewById(R.id.rc_message_list);
        tvEmpty = (TextView) getView().findViewById(R.id.tv_empty);
        refreshLayout = (SmartRefreshLayout) getView().findViewById(R.id.layout_refresh);
        tvEditingStatusId = (TextView) getView().findViewById(R.id.tv_editingStatus_id);
        tvMarkId = (TextView) getView().findViewById(R.id.tv_mark_id);
        ivCloseMarkId = (ImageView) getView().findViewById(R.id.iv_close_mark_id);
        llMarkId = (LinearLayout) getView().findViewById(R.id.ll_mark_id);
        tvUnread = (TextView) getView().findViewById(R.id.tv_unread);
        ivGoDown = (ImageView) getView().findViewById(R.id.iv_go_down);
        linearLayoutManager = new LinearLayoutManager(requireContext());
        rcMessageList.setLayoutManager(linearLayoutManager);
        //??????????????????ID????????????ID
        CommunityDetailsBean detailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        MessageManager.get().attachChannel(null != detailsBean ? detailsBean.getUid() : ""
                , CommunityHelper.getInstance().getChannelUid());
        //????????????????????????
        MessageManager.get().attachView(rcMessageList);

        rcMessageList.addOnScrollListener(mScrollListener);
        //???????????????????????????
        present.getChannelUnreadCount();

        //?????????????????????????????????????????????????????????
        CommunityHelper.communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) return;
                CommunityDetailsBean.CommunityUserBean u = communityDetailsBean.getCommunityUser();
                int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) inputPanelId.getLayoutParams();
                layoutParams.bottomMargin = auditStatus == Constants.AuditStatus.JOINED.getCode() ? UiUtils.dp2px(10) : UiUtils.dp2px(30);
                inputPanelId.setLayoutParams(layoutParams);
                inputPanelId.setVisibility(auditStatus == Constants.AuditStatus.JOINED.getCode() ? View.VISIBLE : View.INVISIBLE);
                refreshShutUpView();
            }
        });

        rcMessageList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (inputPanelId != null) inputPanelId.dismiss();
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        CommunityHelper.communityDetailsLiveData.removeObservers(this);
        CommunityHelper.markMessageLiveData.removeObservers(this);
        CommunityHelper.markMessageLiveData = new MutableLiveData<>();
        super.onDestroy();
    }

    /**
     * ???recyclerview ??????????????????????????????????????????????????????item???????????????????????????????????????????????????
     */
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //???????????????????????????????????????
            LinearLayoutManager layoutManager = (LinearLayoutManager) rcMessageList.getLayoutManager();
            int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastCompletelyVisibleItemPosition > -1 && lastCompletelyVisibleItemPosition < layoutManager.getItemCount() - 3) {
                if (ivGoDown != null) ivGoDown.setVisibility(View.VISIBLE);
            } else ivGoDown.setVisibility(View.GONE);
        }

        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //??????????????????????????????????????????item????????????????????? ,???????????????????????????????????????
            //???????????????????????????????????????????????????????????????????????????????????????
            if (newState == 0) {
                clearUnReadStatus();
            }
        }

        /**
         * ?????????????????????
         */
        private void clearUnReadStatus() {
            RecyclerView.LayoutManager layoutManager = rcMessageList.getLayoutManager();
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if (firstVisibleItemPosition == -1) return;
            List<WrapperMessage> messages = MessageManager.get().getMessages();
            Message firstVisibleMessage = messages.get(firstVisibleItemPosition).getMessage();
            MessageManager.get().getFirstUnReadMessage(new IResultBack<Message>() {
                @Override
                public void onResult(Message message) {
                    //??????????????????????????????????????????????????????
                    if (message != null && firstVisibleMessage.getSentTime() < message.getSentTime()) {
                        clearAllMessageUnreadStatus();
                    }
                }
            });
        }
    };

    public void clearAllMessageUnreadStatus() {
        MessageManager.get().clearAllMessageUnreadStatus(null);
        refreshUnReadView(0);
    }

    @Override
    public void initListener() {
        super.initListener();
        inputPanelId.setInputListener(present);
        inputPanelId.setBottomViewListener(present);
        ivCloseMarkId.setOnClickListener(this::onClick);
        llMarkId.setOnClickListener(this::onClick);
        tvUnread.setOnClickListener(this::onClick);
        ivGoDown.setOnClickListener(this::onClick);
        MessageManager.get().setOnMessageLongClick(this::onItemLongClick);
        MessageManager.get().setOnMessageClick(this::onItemClick);
        refreshLayout.setOnRefreshLoadMoreListener(this);
        CommunityHelper.markMessageLiveData.observe(this, markMessageObserver);
        //??????????????????????????????????????????
        getLatestMessages();
    }


    /**
     * ???????????????20?????????
     */
    private void getLatestMessages() {
        //???????????????????????????????????????
        MessageManager.get().getMessages().clear();
        rcMessageList.getAdapter().notifyDataSetChanged();
        loadData(0L, true, true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_close_mark_id) {
            llMarkId.setVisibility(View.GONE);
        } else if (id == R.id.ll_mark_id) {
            MarkMessageActivity.openMarkMessage(getActivity(), CommunityHelper.getInstance().getChannelUid(), Constants.markMessageRequestCode);
        } else if (id == R.id.tv_unread) {
            //????????????,???????????????????????????????????????????????????
            jumpFirstUnReadMessage();
        } else if (id == R.id.iv_go_down) {
            clearAllMessageUnreadStatus();
            getLatestMessages();
        }
    }

    /**
     * ??????????????????
     * status 1:???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * status 2:????????????????????????????????????????????????????????????????????????????????????????????????20?????????,??????????????????
     */
    private void jumpFirstUnReadMessage() {
        MessageManager.get().getFirstUnReadMessage(new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (message != null) {
                    clearAllMessageUnreadStatus();
                    //?????????????????????????????????????????????????????????????????????????????????????????????
                    int i = MessageManager.get().containMessage(message);
                    if (i > -1) {
                        rcMessageList.scrollToPosition(i);
                        scrollToPosition(i);
                    } else {
                        //????????????????????????????????????20?????????,??????????????????
                        MessageManager.get().getMessages().clear();
                        rcMessageList.getAdapter().notifyDataSetChanged();
                        //????????????????????????
                        MessageManager.get().insertMessage(message, true, false);
                        //?????????????????????
                        loadData(message.getSentTime(), false, false);
                        //????????????????????????????????????????????????????????????????????????????????????
                        present.setAllowInsert(false);
                        Dispatcher.get().dispatch(new Runnable() {
                            @Override
                            public void run() {
                                scrollToPosition(0);
                            }
                        }, 1000);
                    }
                } else {
                    KToast.show(ResUtil.getString(R.string.cmu_text_the_first_unread_message_was_not_obtained));
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void jumpMessage(String messageUid) {
        if (TextUtils.isEmpty(messageUid)) return;
        MessageManager.get().getMessage(messageUid, new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (message != null) {
                    //?????????????????????????????????????????????????????????????????????????????????????????????
                    int i = MessageManager.get().containMessage(message);
                    if (i > -1) {
                        scrollToPosition(i);
                    } else {
                        //????????????????????????????????????????????????????????????????????????20???
                        MessageManager.get().getMessages().clear();
                        rcMessageList.getAdapter().notifyDataSetChanged();
                        //????????????????????????
                        MessageManager.get().insertMessage(message, true, false);
                        //?????????????????????
                        loadData(message.getSentTime(), false, false);
                        //????????????????????????????????????????????????????????????????????????????????????
                        present.setAllowInsert(false);
                        Dispatcher.get().dispatch(new Runnable() {
                            @Override
                            public void run() {
                                scrollToPosition(0);
                            }
                        }, 1000);
                    }
                }
            }
        });
    }


    /**
     * ?????????????????????
     *
     * @param i
     */
    public void scrollToPosition(int i) {
        rcMessageList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onGlobalLayout() {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
                alphaAnimation.setDuration(800);
                alphaAnimation.setRepeatCount(2);
                alphaAnimation.setRepeatMode(Animation.RESTART);
                View view = linearLayoutManager.findViewByPosition(i);
                if (view != null)
                    view.setAnimation(alphaAnimation);
                Drawable background = view.getBackground();
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        view.setBackgroundResource(R.color.basis_grayish);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setBackground(background);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                alphaAnimation.startNow();
                rcMessageList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (rcMessageList != null) {
            rcMessageList.scrollToPosition(i);
        }
    }


    @Override
    public void refreshEmptyView() {
        tvEmpty.setVisibility(MessageManager.get().getMessages().size() == 0 ? View.VISIBLE : View.GONE);
    }


    @Override
    public void refreshShutUpView() {
        int shutUp = CommunityHelper.getInstance().getShutUp();
        if (shutUp == Integer.parseInt(Constants.SHUT_UP)) {
            inputPanelId.setShutUpViewVisibility(View.VISIBLE);
        } else {
            inputPanelId.setShutUpViewVisibility(View.GONE);
        }
    }

    /**
     * ????????????item
     *
     * @param wrapperMessage ????????????
     * @param position
     */
    @Override
    public void onItemLongClick(WrapperMessage wrapperMessage, int position) {
        //???????????????????????????????????????????????????????????? ?????????????????????
        CommunityDetailsBean.CommunityUserBean u = CommunityHelper.getInstance().getCommunityUserBean();
        int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
        MessageContent messageContent = wrapperMessage.getMessage().getContent();
        if (!(messageContent instanceof RecallNotificationMessage
                || messageContent instanceof ChannelNoticeMsg
                || auditStatus == Constants.AuditStatus.NOT_JOIN.getCode())) {
            MessageOperationFragment newsOperationFragment
                    = new MessageOperationFragment(UIStack.getInstance().getTopActivity(), wrapperMessage);
            newsOperationFragment.setIMessageOperation(inputPanelId);
            newsOperationFragment.show();
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param visible
     * @param hint
     */
    @Override
    public void refreshEditStatusView(int visible, String hint) {
        tvEditingStatusId.setVisibility(visible);
        tvEditingStatusId.setText(hint);
    }

    /**
     * ???????????????
     *
     * @param wrapperMessage
     * @param position
     */
    @Override
    public void onItemClick(WrapperMessage wrapperMessage, int position) {
        if (inputPanelId != null) inputPanelId.dismiss();
    }

    /**
     * ??????????????????
     */
    private Observer<Message> markMessageObserver = new Observer<Message>() {
        @Override
        public void onChanged(Message message) {
            Dispatcher.get().dispatch(new Runnable() {
                @Override
                public void run() {
                    if (llMarkId == null) return;
                    if (null != message) {
                        llMarkId.setVisibility(View.VISIBLE);
                        if (null != tvMarkId) {
                            tvMarkId.setText(MessageManager.get().messageToContent(message.getContent()));
                        }
                    } else {
                        llMarkId.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    /**
     * ????????????????????????
     *
     * @param refreshLayout
     */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        List<WrapperMessage> messages = MessageManager.get().getMessages();
        if (messages.size() > 0) {
            long sentTime = messages.get(messages.size() - 1).getMessage().getSentTime();
            loadData(sentTime, false, false);
        } else {
            loadData(0L, false, false);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param refreshLayout
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        List<WrapperMessage> messages = MessageManager.get().getMessages();
        if (messages.size() > 0) {
            long sentTime = messages.get(0).getMessage().getSentTime();
            loadData(sentTime, true, false);
        } else {
            loadData(0L, true, false);
        }
    }

    /**
     * ????????????
     *
     * @param messageSentTime ?????????????????????
     * @param isRefresh       ???????????????
     * @param isScrollBottom  ??????????????????????????????
     */
    private void loadData(Long messageSentTime, boolean isRefresh, boolean isScrollBottom) {
        HistoryMessageOption.PullOrder pullOrder;
        if (isRefresh) {
            pullOrder = HistoryMessageOption.PullOrder.DESCEND;//?????????????????????
        } else {
            pullOrder = HistoryMessageOption.PullOrder.ASCEND;//?????????????????????
        }
        MessageManager.get().getMessages(messageSentTime, pageNum, pullOrder, isScrollBottom, new IRongCoreCallback.IGetMessageCallbackEx() {
            @Override
            public void onComplete(List<Message> messageList, long syncTimestamp, boolean hasMoreMsg, IRongCoreEnum.CoreErrorCode errorCode) {
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
                if (!hasMoreMsg) {
                    //?????????????????????
                    refreshLayout.setNoMoreData(true);
                }
                if (messageList.size() == 0 || messageList.size() % pageNum > 0) {
                    //????????????????????????????????????????????????
                    present.setAllowInsert(true);
                }
                refreshEmptyView();
            }

            @Override
            public void onFail(IRongCoreEnum.CoreErrorCode errorCode) {
                Logger.e(TAG, errorCode);
                if (isRefresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.markMessageRequestCode && resultCode == Constants.markMessageResultCode) {
            if (data != null) {
                String messageUid = data.getStringExtra(UltraKvKey.MarkMessageKey);
                jumpMessage(messageUid);
            }
        } else {
            if (inputPanelId != null)
                inputPanelId.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void scrollBottom() {
        int itemCount = rcMessageList.getAdapter().getItemCount();
        if (itemCount > 0) {
            rcMessageList.scrollToPosition(itemCount - 1);
        }
    }

    @Override
    public TextChannelPresent createPresent() {
        return new TextChannelPresent(this, getLifecycle());
    }

    @Override
    public void refreshUnReadView(int unReadCount) {
        tvUnread.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
        tvUnread.setText(String.format(ResUtil.getString(R.string.cmu_unread_message_count),
                unReadCount > 999 ? "999+" : unReadCount).trim());
    }

    @Override
    public RecyclerView getRecyclerView() {
        return rcMessageList;
    }

    @Override
    public EditText getEditText() {
        if (inputPanelId != null) {
            return inputPanelId.getEditText();
        }
        return null;
    }

}
