package cn.rc.community.message;

import static cn.rc.community.Constants.MemberStatus.AUDITING;
import static cn.rc.community.Constants.MemberStatus.KNOCKOUT;
import static cn.rc.community.Constants.MemberStatus.NOT_PASS_AUDIT;
import static cn.rc.community.Constants.MemberStatus.PASS_AUDIT;
import static cn.rc.community.Constants.MemberStatus.QUIT;
import static cn.rc.community.message.sysmsg.CommunityType.disabled;
import static cn.rc.community.message.sysmsg.CommunityType.enabled;

import android.text.TextUtils;
import android.view.View;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.interfaces.IManager;
import cn.rc.community.message.sysmsg.CommunityDeleteMsg;
import cn.rc.community.message.sysmsg.CommunitySysNoticeMsg;
import cn.rc.community.message.sysmsg.CommunityType;
import cn.rongcloud.config.provider.user.UserProvider;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * 系统消息
 */
public class SystemMessageFragment extends AbsMessageFragment<Message> implements OnConvertListener<Message> {
    private final static String[] OBJ_TAGS = new String[]{
            ObjectName.COMMUNITY_SYSTEM_NOTICE,
            ObjectName.COMMUNITY_DELETE
    };

    public static SystemMessageFragment getInstance() {
        return new SystemMessageFragment();
    }


    @Override
    public void init() {
        super.init();
    }


    @Override
    public IAdapte<Message, RcyHolder> onSetAdapter() {
        return new MessageAdapter<>(activity, R.layout.item_sys_message, this);
    }

    private long lastMessageTime = 0;

    /**
     * 收到的消息都是针对自己的系统消息
     *
     * @param wait
     * @param refresh
     * @param resultBack
     */
    @Override
    public void onRefreshData(boolean wait, boolean refresh, MessageResultBack<Message> resultBack) {
        if (refresh) {
            lastMessageTime = 0;
        }
        MessageManager.get().getSystemMessage(lastMessageTime, OBJ_TAGS, new IResultBack<List<Message>>() {
            @Override
            public void onResult(List<Message> messages) {
                int size1 = messages.size();
                for (int i = 0; i < messages.size(); i++) {
                    Message message = messages.get(i);
                    MessageContent content = message.getContent();
                    if (content instanceof CommunitySysNoticeMsg && (((CommunitySysNoticeMsg) content).getType() == disabled ||
                            ((CommunitySysNoticeMsg) content).getType() == enabled)) {
                        messages.remove(i);
                        i--;
                    }
                }
                int size = null == messages ? 0 : messages.size();
                if (size1 % IManager.COUNT > 0 || size1 == 0) {
                    refreshLayout.setEnableLoadMore(false);
                } else {
                    refreshLayout.setEnableLoadMore(true);
                }
                MessageManager.get().clearSystemMessagesUnreadStatus();
                if (resultBack != null) {
                    if (refresh) {
                        resultBack.onResult(messages);
                    } else {
                        if (null != messages) {
                            for (Message message : messages) {
                                resultBack.addData(message, true);
                            }
                        } else {
                            resultBack.onResult(null);
                        }
                    }
                }
                if (size > 0) {
                    lastMessageTime = messages.get(size - 1).getSentTime();
                }
                emptyView.setVisibility(getAdapter().getData().size() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onConvert(RcyHolder holder, Message message, int position) {
        Logger.e(TAG, "obj = " + message.getObjectName() + " content = " + GsonUtil.obj2Json(message.getContent()));
        holder.setVisible(R.id.tv_name, false);
        holder.setText(R.id.tv_time, MessageManager.get().messageToDate(message));
        switch (message.getObjectName()) {
            case ObjectName.COMMUNITY_DELETE:
                //解散消息
                CommunityDeleteMsg communityDeleteMsg = (CommunityDeleteMsg) message.getContent();
                covertDeleteMsg(holder, communityDeleteMsg);
                break;
            case ObjectName.COMMUNITY_SYSTEM_NOTICE:
                //系统提示消息
                CommunitySysNoticeMsg msg = (CommunitySysNoticeMsg) message.getContent();
                covertSysNoticeMsg(holder, msg);
                handleType(holder, message);
                break;
            default:
//                content = GsonUtil.obj2Json(message.getContent());
                break;
        }
    }

    private void covertDeleteMsg(RcyHolder holder, CommunityDeleteMsg communityDeleteMsg) {
        holder.setVisible(R.id.tv_name, true);
        holder.setText(R.id.tv_name, "系统提示");
        holder.setText(R.id.tv_last, "[" + communityDeleteMsg.getCommunityUid() + "]已被解散");
        holder.setImageDrawable(R.id.portrait, ResUtil.getDrawable(R.drawable.svg_sys_msg));
    }

    private void covertSysNoticeMsg(RcyHolder holder, CommunitySysNoticeMsg communitySysNoticeMsg) {
        switch (communitySysNoticeMsg.getType()) {
            case joined://已加入社区
            case kick://被踢出社区
            case left://已离开某个社区
            case rejected:
            case deleted:
                holder.setText(R.id.tv_last, communitySysNoticeMsg.getMessage());
                holder.setImageDrawable(R.id.portrait, ResUtil.getDrawable(R.drawable.svg_sys_msg));
                break;
            case request:
                //申请加入某个社区
                holder.setText(R.id.tv_last, communitySysNoticeMsg.getMessage());
                String fromUserId = communitySysNoticeMsg.getFromUserId();
                UserProvider.provider().getAsyn(fromUserId, new IResultBack<UserInfo>() {
                    @Override
                    public void onResult(UserInfo user) {
                        if (null != user) {
                            holder.setVisible(R.id.tv_name, true);
                            holder.setText(R.id.tv_name, user.getName());
                            ImageLoader.loadUri(holder.getView(R.id.portrait), user.getPortraitUri(), R.drawable.cmu_default_portrait);
                        }
                    }
                });
            case disabled:
            case enabled:
                break;
        }
    }

    // 0代表申请加入社区、1代表加入社区后通知消息、2代表退出社区的通知消息，3代表被踢出社区的通知消息
    void handleType(RcyHolder holder, Message message) {
        CommunitySysNoticeMsg msg = (CommunitySysNoticeMsg) message.getContent();
        String extra = message.getExtra();
        boolean request = CommunityType.request == msg.getType();
        //判断当前用户是否已经在社区里面了,如果已经在超级群中了
        Logger.e(TAG, "type = " + msg.getType() + " extra = " + extra + " request = " + request);
        if (request) {
            holder.setVisible(R.id.bottom, true);
            holder.setVisible(R.id.reject, false);
            holder.setVisible(R.id.ok, false);
            holder.setVisible(R.id.info, false);
            if (TextUtils.isEmpty(extra) || TextUtils.equals("0", extra)) {// 未处理
                Map<String, Object> params = new HashMap<>(4);
                params.put("userUid", msg.getFromUserId());
                params.put("communityUid", msg.getCommunityUid());
                OkApi.post(CommunityAPI.Community_User_info, params, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        UltraGroupUserBean ultraGroupUserBean = result.get(UltraGroupUserBean.class);
                        if (ultraGroupUserBean != null) {
                            if ((ultraGroupUserBean.getStatus() == NOT_PASS_AUDIT.getCode() ||
                                    ultraGroupUserBean.getStatus() == KNOCKOUT.getCode() ||
                                    ultraGroupUserBean.getStatus() == QUIT.getCode()) ||
                                    ultraGroupUserBean.getStatus() == AUDITING.getCode()) {
                                holder.setVisible(R.id.reject, true);
                                holder.setVisible(R.id.ok, true);
                            } else if (ultraGroupUserBean.getStatus() == PASS_AUDIT.getCode()) {
                                //已经在当前社区里面了
                                holder.setVisible(R.id.info, true);
                                holder.setText(R.id.info, "已同意");
                                saveMessage(message, 1);
                            }
                        } else {
                            //如果没有拿到信息，
                            holder.setVisible(R.id.reject, true);
                            holder.setVisible(R.id.ok, true);
                        }
                    }
                });
                holder.setOnClickListener(R.id.reject, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        responseRequest(true, message);
                    }
                });
                holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        responseRequest(false, message);
                    }
                });
            } else {// 已处理
                holder.setVisible(R.id.info, true);
                if (TextUtils.equals("1", extra)) {// 已同意
                    holder.setText(R.id.info, "已同意");
                } else if (TextUtils.equals("2", extra)) {// 已拒绝
                    holder.setText(R.id.info, "已拒绝");
                }
            }
        } else {
            holder.setVisible(R.id.bottom, false);
        }
    }

    void responseRequest(boolean reject, Message message) {
        Map<String, Object> params = new HashMap<>();
        CommunitySysNoticeMsg msg = (CommunitySysNoticeMsg) message.getContent();
        params.put("communityUid", msg.getCommunityUid());
        params.put("userUid", msg.getFromUserId());
        params.put("status", reject ? 2 : 3);//2:审核未通过,3:审核通过,4:退出，5：被踢出
        OkApi.post(CommunityAPI.Community_update_user_info, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    // 修改message extra 字段 保存处理状态 可以落库
                    // 落库状态：0：未处理 1：已同意 2：已拒绝
                    saveMessage(message, reject ? 2 : 1);
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }

    private void saveMessage(Message message, int status) {
        RongCoreClient.getInstance().setMessageExtra(message.getMessageId(), String.valueOf(status), new IRongCoreCallback.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                //修改完成
                message.setExtra(String.valueOf(status));
                getAdapter().updateItem(message);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                Logger.e(TAG, "setMessageExtra onError: code = " + e.getValue() + ": " + e.getMessage());
            }
        });
    }
}
