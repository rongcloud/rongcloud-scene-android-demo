package cn.rc.community.dialog;


import static com.basis.utils.UIKit.getResources;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.widget.dialog.BottomDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.NewsOperationAdapter;
import cn.rc.community.R;
import cn.rc.community.bean.NewsOperation;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.helper.CommunityHelper;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ReferenceMessage;
import io.rong.message.TextMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/9
 * @time 6:02 下午
 * 消息操作
 */
public class MessageOperationFragment extends BottomDialog {

    private RecyclerView rlMemeId;
    private RecyclerView rlFun;
    private WrapperMessage message;
    private IMessageOperation iMessageOperation;
    private NewsOperationAdapter adapter;

    public MessageOperationFragment(Activity activity, WrapperMessage message) {
        super(activity);
        this.message = message;
        setContentView(R.layout.cmu_dialog_news_operation_layout, -1);
        initView();
    }

    public void setIMessageOperation(IMessageOperation iMessageOperation) {
        this.iMessageOperation = iMessageOperation;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rlMemeId = (RecyclerView) getContentView().findViewById(R.id.rl_meme_id);
        rlFun = (RecyclerView) getContentView().findViewById(R.id.rl_fun);
        rlFun.setLayoutManager(new GridLayoutManager(mActivity, 4));
        adapter = new NewsOperationAdapter();
        rlFun.setAdapter(adapter);
        initFuns();
        rlMemeId.setVisibility(View.GONE);
//        initMemes();
    }

    /**
     * 初始化表情包 TODO 1.0先不做表情
     */
//    private void initMemes() {
//        rlMemeId.setLayoutManager(new GridLayoutManager(mActivity, 6));
//        EmojiCategory[] categories = RoomKitInit.MyEmojiProvider.getEmojiProviderInstance().getCategories();
//        IosEmoji[] emojis = (IosEmoji[]) categories[0].getEmojis();
//        ArrayList<IosEmoji> iosEmojis = new ArrayList<>();
//        for (int i = 0; i < emojis.length; i++) {
//            if (i <= 5) {
//                iosEmojis.add(emojis[i]);
//            } else {
//                break;
//            }
//        }
//        EmojiAdapter emojiAdapter = new EmojiAdapter(iosEmojis);
//        rlMemeId.setAdapter(emojiAdapter);
//
//    }

    /**
     * 初始化操作功能
     */
    private void initFuns() {
        if (adapter != null)
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                    if (iMessageOperation == null) {
                        return;
                    }
                    NewsOperation item = adapter.getItem(position);
                    if (item.getIcon() == R.drawable.ic_edit) {
                        iMessageOperation.edit(message);
                    } else if (item.getIcon() == R.drawable.ic_quote) {
                        iMessageOperation.quote(message);
                    } else if (item.getIcon() == R.drawable.ic_copy) {
                        iMessageOperation.copy(message);
                    } else if (item.getIcon() == R.drawable.ic_delete) {
                        iMessageOperation.delete(message);
                    } else if (item.getIcon() == R.drawable.ic_annotation) {
                        if (TextUtils.equals(item.getName(), getResources().getString(R.string.cmu_cancel_str_annotation))) {
                            iMessageOperation.cancelAnnotation(message, true);
                        } else {
                            iMessageOperation.annotation(message);
                        }
                    } else if (item.getIcon() == R.drawable.ic_recall) {
                        iMessageOperation.reCall(message);
                        iMessageOperation.cancelAnnotation(message, false);
                    }
                    dismiss();
                }
            });
        //只有文字和引用消息可以编辑，并且必须是自己本人,并且自己没有被禁言
        MessageContent content = message.getMessage().getContent();
        if ((content instanceof TextMessage || content instanceof ReferenceMessage) &&
                TextUtils.equals(message.getMessage().getSenderUserId(), RongCoreClient.getInstance().getCurrentUserId()) &&
                !TextUtils.equals(CommunityHelper.getInstance().getShutUp() + "", Constants.SHUT_UP)) {
            adapter.addData(new NewsOperation(R.drawable.ic_edit, getResources().getString(R.string.cmu_str_edit)));
        }
        //自己没有被禁言才可以引用
        if (!TextUtils.equals(CommunityHelper.getInstance().getShutUp() + "", Constants.SHUT_UP)) {
            adapter.addData(new NewsOperation(R.drawable.ic_quote, getResources().getString(R.string.cmu_str_quote)));
        }
        //文字消息和引用消息可以复制
        if (content instanceof TextMessage || content instanceof ReferenceMessage) {
            adapter.addData(new NewsOperation(R.drawable.ic_copy, getResources().getString(R.string.cmu_str_copy)));
        }
        //只有社区创建者和当前消息的发送者可以撤销
        if (TextUtils.equals(message.getMessage().getSenderUserId(), RongCoreClient.getInstance().getCurrentUserId()) ||
                CommunityHelper.getInstance().isCreator()) {
            adapter.addData(new NewsOperation(R.drawable.ic_recall, getResources().getString(R.string.cmu_str_recall)));
        }
        //判断当前消息是否已经标注了
        if (CommunityHelper.getInstance().isCreator()) {
            OkApi.post(CommunityAPI.MARK_MSG_DETAILS + message.getMessage().getUId(), null, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (result.ok()) {
                        adapter.addData(new NewsOperation(R.drawable.ic_annotation, getResources().getString(R.string.cmu_cancel_str_annotation)));
                    } else {
                        adapter.addData(new NewsOperation(R.drawable.ic_annotation, getResources().getString(R.string.cmu_str_annotation)));
                    }
                }
            });
        }
    }

}
