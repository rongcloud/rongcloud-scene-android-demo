package cn.rc.community.conversion.convert;

import android.net.Uri;
import android.text.TextUtils;

import com.basis.adapter.RcyHolder;
import com.basis.utils.DateUtil;
import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;

import cn.rc.community.R;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.utils.UltraGroupUserManager;
import cn.rongcloud.config.provider.user.UserProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/20
 * @time 15:29
 * 撤销消息
 */
public class RecallNotificationMessageAttachedInfo extends BaseMessageAttachedInfo {
    @Override
    public void onConvert(RcyHolder holder, WrapperMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                UserInfo userInfo = message.getContent().getUserInfo();
                if (userInfo != null) {
                    //将用户带的信息更新到缓存中
                    if (TextUtils.isEmpty(ultraGroupUserBean.getNickName()))
                        ultraGroupUserBean.setNickName(userInfo.getName());
                    if (TextUtils.isEmpty(ultraGroupUserBean.getPortrait()))
                        ultraGroupUserBean.setPortrait(userInfo.getPortraitUri().toString());
                }
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUri(circleImageView, Uri.parse(ultraGroupUserBean.getPortrait()), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(((RecallNotificationMessage) message.getContent()).getRecallTime()));
    }

    @Override
    public int onSetLayout(WrapperMessage message) {
        return R.layout.item_chatroom_left_recall;
    }

    @Override
    public String onSetObjectName() {
        return ObjectName.RECALL_TAG;
    }
}
