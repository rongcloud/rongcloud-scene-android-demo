package cn.rc.community.channel.TextChannel;


import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.basis.ui.mvp.BasePresenter;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.channel.InputPanelView;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.controller.interfaces.IManager;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import cn.rc.community.utils.UltraGroupUserManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.imlib.model.UltraGroupTypingStatusInfo;
import io.rong.message.ReferenceMessage;
import io.rong.message.TextMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/26
 * @time 11:59
 */
public class TextChannelPresent extends BasePresenter<ITextChannelView> implements ITextChannelPresent,
        InputPanelView.InputListener, IManager.OnMessageAttachListener, InputPanelView.BottomViewListener
        , IManager.OnChannelListener {


    private Disposable disposable;
    //是否允许插入到聊天列表中
    private boolean isAllowInsert = true;

    public TextChannelPresent(ITextChannelView mView, Lifecycle lifecycle) {
        super(mView, lifecycle);
        registerListener();
    }


    @Override
    public void getChannelUnreadCount() {
        MessageManager.get().getChannelUnreadCount(new IResultBack<Integer>() {
            @Override
            public void onResult(Integer integer) {
                if (mView != null) mView.refreshUnReadView(integer);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterListener();
        MessageManager.get().clearMessagesUnreadStatus(System.currentTimeMillis(), null);
    }

    private void registerListener() {
        //注册消息的监听
        MessageManager.get().setOnMessageAttachListener(this);
        MessageManager.get().setOnChannelListener(this);

    }

    private void unRegisterListener() {
        MessageManager.get().setOnMessageAttachListener(null);
        MessageManager.get().setOnChannelListener(null);
        //取消倒计时避免内存泄漏
        if (disposable != null) disposable.dispose();
    }


    /**
     * 点击发送按钮
     *
     * @param message
     */
    @Override
    public void onClickSend(TextMessage message) {
        MessageManager.get().sendMessage(message, mView.getEditText(), new SendMessageCallback() {

            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, int code, String reason) {
                Logger.e("onClickSend", code + reason);
                KToast.show(reason);
            }
        });
    }

    /**
     * 重新编辑消息
     *
     * @param message
     */
    @Override
    public void reEditMessage(WrapperMessage message) {
        MessageManager.get().editMessage(message, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                //更新适配器

            }
        });
    }

    /**
     * 引用消息
     *
     * @param wrapperMessage 被引用的消息
     * @param editSendText   引用的回复
     */
    @Override
    public void quoteMessage(WrapperMessage wrapperMessage, String editSendText) {
        Message message = wrapperMessage.getMessage();
        //构建引用消息
        ReferenceMessage referenceMessage = ReferenceMessage.obtainMessage(message.getSenderUserId(), message.getContent());
        referenceMessage.setEditSendText(editSendText);
        MentionedInfo mentionedInfo = new MentionedInfo();
        mentionedInfo.setType(MentionedInfo.MentionedType.PART);
        List<String> mentionedUserIdList = new ArrayList<>();
        mentionedUserIdList.add(message.getSenderUserId());
        mentionedInfo.setMentionedUserIdList(mentionedUserIdList);
        referenceMessage.setMentionedInfo(mentionedInfo);
        referenceMessage.setReferMsgUid(message.getUId());
        MessageManager.get().sendMessage(referenceMessage, mView.getEditText(), new SendMessageCallback() {
            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, int code, String reason) {
                Logger.e("quoteMessage", code + reason);
                KToast.show(reason);
            }
        });
    }

    @Override
    public boolean onClickEmoji() {
        return false;
    }


    /**
     * 绑定的新的消息类型
     *
     * @param objectName
     * @return
     */
    @Override
    public IManager.AttachedInfo onAttach(String objectName) {
        return null;
    }

    /**
     * 消息插入到本地监听
     *
     * @param message        被插入的消息
     * @param isScrollBottom 是否滑动到最底部
     */
    @Override
    public void onMessageAttach(WrapperMessage message, boolean isScrollBottom) {
        mView.refreshEmptyView();
        if (isScrollBottom) mView.scrollBottom();
    }

    @Override
    public void onReceivedMessage(Message message, ReceivedProfile profile) {
        if (isAllowInsert) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mView.getRecyclerView().getLayoutManager();
            MessageManager.get().insertMessage(message, true, layoutManager.findLastVisibleItemPosition()
                    == MessageManager.get().getMessages().size() - 1 ? true : false);
        }
    }

    @Override
    public void onKeyboardOpen(int keyBoardHeight) {
        mView.scrollBottom();
    }

    @Override
    public void onPluginShow() {
        mView.scrollBottom();
    }

    @Override
    public void onEditingStatusChanged(List<UltraGroupTypingStatusInfo> infoList) {
        if (disposable != null) disposable.dispose();//取消上次的倒计时
        if (infoList.size() > 0) {
            UltraGroupTypingStatusInfo info = infoList.get(0);
            UltraGroupUserManager.getInstance().getAsyn(info.getUserId(), new IResultBack<UltraGroupUserBean>() {
                @Override
                public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                    //开始倒计时
                    disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                            .take(6)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Throwable {
                                    mView.refreshEditStatusView(aLong == 5 ? View.GONE : View.VISIBLE, ultraGroupUserBean.getNickName() + " 等" + infoList.size() + "个人 " + "正在输入...");
                                }
                            });
                }
            });
        }
    }


    /**
     * 接收到的消息是否允许插入
     *
     * @param allowInsert
     */
    public void setAllowInsert(boolean allowInsert) {
        isAllowInsert = allowInsert;
    }

}
