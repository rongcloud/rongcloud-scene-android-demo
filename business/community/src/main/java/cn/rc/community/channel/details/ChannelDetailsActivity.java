package cn.rc.community.channel.details;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelDetailsBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.setting.manager.ProfileUpdateActivity;

/**
 * 频道会话-> 频道详情
 */
public class ChannelDetailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvChannelName;
    private TextView tvChannelIntro;

    @Override
    public int setLayoutId() {
        return R.layout.activity_channel_details;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_channel_details)).work();
        initView();
    }

    private ChannelDetailsBean detailsBean;

    private void initView() {
        tvChannelName = findViewById(R.id.tv_channel_name);
        tvChannelIntro = findViewById(R.id.tv_channel_intro);
        CommunityHelper.channelDetailsLiveData.observe(this, new Observer<ChannelDetailsBean>() {
            @Override
            public void onChanged(ChannelDetailsBean channelDetailsBean) {
                detailsBean = channelDetailsBean;
                if (detailsBean == null) {
                    return;
                }
                tvChannelName.setText(detailsBean.getName());
                if (!TextUtils.isEmpty(detailsBean.getRemark()))
                    tvChannelIntro.setText(detailsBean.getRemark());
            }
        });
        CommunityHelper.getInstance().getChannelDetails(CommunityHelper.getInstance().getChannelUid(), null);
        tvChannelName.setOnClickListener(this);
        tvChannelIntro.setOnClickListener(this);

        getView(R.id.tv_tagged_information).setOnClickListener(this);
        // 1.0暂时不做
        getView(R.id.tv_only_specific_watch).setOnClickListener(this);
        getView(R.id.only_send).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.tv_channel_intro == id) {
            ProfileUpdateActivity.openProfileUpdate(activity,
                    ProfileUpdateActivity.UpdateType.ChannelDes,
                    R.string.cmu_channel_intro, tvChannelIntro.getText().toString());
        } else if (R.id.tv_channel_name == id) {
            ProfileUpdateActivity.openProfileUpdate(activity,
                    ProfileUpdateActivity.UpdateType.ChannelName,
                    R.string.cmu_channel_name,
                    tvChannelName.getText().toString());
        } else if (R.id.tv_tagged_information == id) {
            MarkMessageActivity.openMarkMessage(activity, CommunityHelper.getInstance().getChannelUid(), Constants.markMessageRequestCode);
        } else if (R.id.tv_only_specific_watch == id) {
            KToast.show("该功能敬请期待");
        } else if (R.id.only_send == id) {
            KToast.show("该功能敬请期待");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.markMessageRequestCode && resultCode == Constants.markMessageResultCode) {
            setResult(resultCode, data);
            this.finish();
        }
    }
}
