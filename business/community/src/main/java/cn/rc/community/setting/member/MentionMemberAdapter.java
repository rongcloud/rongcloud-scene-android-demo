package cn.rc.community.setting.member;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;

import cn.rc.community.R;
import cn.rc.community.bean.MemberBean;
import cn.rongcloud.config.provider.user.UserProvider;
import io.rong.imkit.feature.mention.RongMentionManager;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/1
 * @time 18:52
 */
public class MentionMemberAdapter extends RcySAdapter<MemberBean.RecordsBean, RcyHolder> {

    public MentionMemberAdapter(Context context) {
        super(context, R.layout.item_mention_member);
    }

    @Override
    public void convert(RcyHolder holder, MemberBean.RecordsBean recordsBean, int position) {
        holder.setText(R.id.tv_member_name, recordsBean.getName());
        ImageLoader.loadUrl(holder.getView(R.id.left), recordsBean.getPortrait(), R.drawable.rc_default_portrait);
        holder.rootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProvider.provider().getAsyn(recordsBean.getUserUid(), new IResultBack<UserInfo>() {
                    @Override
                    public void onResult(UserInfo userInfo) {
                        RongMentionManager.getInstance().mentionMember(userInfo);
                        ((Activity) context).finish();
                    }
                });
            }
        });
    }
}
