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
 * @time 3:23 下午
 * 文字频道聊天界面
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
        //关联上本社区ID和本频道ID
        CommunityDetailsBean detailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        MessageManager.get().attachChannel(null != detailsBean ? detailsBean.getUid() : ""
                , CommunityHelper.getInstance().getChannelUid());
        //消息绑定显示控件
        MessageManager.get().attachView(rcMessageList);

        rcMessageList.addOnScrollListener(mScrollListener);
        //获取所有的未读消息
        present.getChannelUnreadCount();

        //根据当前加入状态来显示或者隐藏输入选项
        CommunityHelper.communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) return;
                CommunityDetailsBean.CommunityUserBean u = communityDetailsBean.getCommunityUser();
                int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
                inputPanelId.setVisibility(auditStatus == Constants.AuditStatus.JOINED.getCode() ? View.VISIBLE : View.GONE);
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
     * 给recyclerview 设置一个滑动监听，如果显示的最后一个item也是未读的，那么就同步一下未读状态
     */
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //当有三条消息不能显示的时候
            LinearLayoutManager layoutManager = (LinearLayoutManager) rcMessageList.getLayoutManager();
            int lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastCompletelyVisibleItemPosition > -1 && lastCompletelyVisibleItemPosition < layoutManager.getItemCount() - 3) {
                if (ivGoDown != null) ivGoDown.setVisibility(View.VISIBLE);
            } else ivGoDown.setVisibility(View.GONE);
        }

        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //如果滚动停止，判断最后可见的item的消息读取状态 ,并且读取当前未读数量的状态
            //停止滚动，如果第一条可见消息为第一条未读消息，那么清除状态
            if (newState == 0) {
                clearUnReadStatus();
            }
        }

        /**
         * 清除可见的状态
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
                    //第一条可见消息如果早于第一条未读消息
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
        //初次进来的时候，向下获取数据
        getLatestMessages();
    }


    /**
     * 获取最新的20条消息
     */
    private void getLatestMessages() {
        //滑动到最底部的最新一条消息
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
            //未读消息,获取到第一条未读消息的时间戳并跳转
            jumpFirstUnReadMessage();
        } else if (id == R.id.iv_go_down) {
            clearAllMessageUnreadStatus();
            getLatestMessages();
        }
    }

    /**
     * 未读消息跳转
     * status 1:如果已经存在了这条消息，直接跳转到这条消息，然后接收到的消息继续插入到底部
     * status 2:如果不存在这条消息，那么获取到这条消息，并且获取到这条消息向下的20条消息,然后更新列表
     */
    private void jumpFirstUnReadMessage() {
        MessageManager.get().getFirstUnReadMessage(new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (message != null) {
                    clearAllMessageUnreadStatus();
                    //判断列表中是否已经存在了这条消息，如果存在了就直接跳到这条消息
                    int i = MessageManager.get().containMessage(message);
                    if (i > -1) {
                        rcMessageList.scrollToPosition(i);
                        scrollToPosition(i);
                    } else {
                        //并且获取到这条消息向下的20条消息,然后更新列表
                        MessageManager.get().getMessages().clear();
                        rcMessageList.getAdapter().notifyDataSetChanged();
                        //定位到当前在这条
                        MessageManager.get().insertMessage(message, true, false);
                        //从上往下拿数据
                        loadData(message.getSentTime(), false, false);
                        //这种情况，接收到的消息不允许插入到底部了，只允许自己下拉
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
     * 定位到指定位置
     */
    private void jumpMessage(String messageUid) {
        if (TextUtils.isEmpty(messageUid)) return;
        MessageManager.get().getMessage(messageUid, new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (message != null) {
                    //判断列表中是否已经存在了这条消息，如果存在了就直接跳到这条消息
                    int i = MessageManager.get().containMessage(message);
                    if (i > -1) {
                        scrollToPosition(i);
                    } else {
                        //如果当前列表没有拿到这条已经存在的消息以及向下的20条
                        MessageManager.get().getMessages().clear();
                        rcMessageList.getAdapter().notifyDataSetChanged();
                        //定位到当前在这条
                        MessageManager.get().insertMessage(message, true, false);
                        //从上往下拿数据
                        loadData(message.getSentTime(), false, false);
                        //这种情况，接收到的消息不允许插入到底部了，只允许自己下拉
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
     * 滑动到指定位置
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
     * 长按消息item
     *
     * @param wrapperMessage 当前消息
     * @param position
     */
    @Override
    public void onItemLongClick(WrapperMessage wrapperMessage, int position) {
        //已经被撤销的消息和系统提示消息，已经游客 是不显示弹窗的
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
     * 频道里面其他人的输入状态
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
     * 点击消息项
     *
     * @param wrapperMessage
     * @param position
     */
    @Override
    public void onItemClick(WrapperMessage wrapperMessage, int position) {
        if (inputPanelId != null) inputPanelId.dismiss();
    }

    /**
     * 标注消息监听
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
     * 上啦加载更多资源
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
     * 下拉刷新获取更多资源
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
     * 获取消息
     *
     * @param messageSentTime 消息发送时间戳
     * @param isRefresh       是否为刷新
     * @param isScrollBottom  是否需要滑动到最底部
     */
    private void loadData(Long messageSentTime, boolean isRefresh, boolean isScrollBottom) {
        HistoryMessageOption.PullOrder pullOrder;
        if (isRefresh) {
            pullOrder = HistoryMessageOption.PullOrder.DESCEND;//从下往上拿消息
        } else {
            pullOrder = HistoryMessageOption.PullOrder.ASCEND;//从上往下拿消息
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
                    //没有更多数据了
                    refreshLayout.setNoMoreData(true);
                }
                if (messageList.size() == 0 || messageList.size() % pageNum > 0) {
                    //到底部了已经，可以继续插入数据了
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
