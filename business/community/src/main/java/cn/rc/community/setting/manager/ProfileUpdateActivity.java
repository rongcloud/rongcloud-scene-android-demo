package cn.rc.community.setting.manager;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.interfaces.IWrapBar;

import java.util.HashMap;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelDetailsBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.helper.CommunityHelper;

/**
 * 社区设置- 管理 - 编辑资料- 修改社区名称
 */
public class ProfileUpdateActivity extends BaseActivity {

    public enum UpdateType {
        CommunityName,
        CommunityDes,
        ChannelName,
        ChannelDes
    }

    public static void openProfileUpdate(Activity activity, UpdateType type, int title, String last) {
        Intent i = new Intent(activity, ProfileUpdateActivity.class)
                .putExtra(UIKit.KEY_BASE, type.name())
                .putExtra(UIKit.KEY_BASE1, ResUtil.getString(title))
                .putExtra(UIKit.KEY_OBJ, last);
        activity.startActivity(i);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_profile_update;
    }

    private UpdateType updateType;
    private EditText etUpdateName, etUpdateDes;
    private TextView updateTitle;

    @Override
    public void init() {
        Intent i = getIntent();
        updateType = UpdateType.valueOf(i.getStringExtra(UIKit.KEY_BASE));
        String title = i.getStringExtra(UIKit.KEY_BASE1);
        String last = i.getStringExtra(UIKit.KEY_OBJ);
        boolean isChannel = UpdateType.ChannelName == updateType || UpdateType.ChannelDes == updateType;
        if (CommunityHelper.getInstance().isCreator()) {
            getWrapBar().addOptionMenu(ResUtil.getString(R.string.cmu_save_channel))
                    .setOnMenuSelectedListener(new IWrapBar.OnMenuSelectedListener() {
                        @Override
                        public void onItemSelected(int position) {
                            if (!isChannel) {
                                updateCommunity();
                            } else {
                                updateChannel();
                            }
                        }
                    });
        }
        getWrapBar().setTitle(title).work();
        etUpdateName = getView(R.id.et_update_name);
        etUpdateDes = getView(R.id.et_update_des);

        etUpdateDes.setFocusable(CommunityHelper.getInstance().isCreator());
        etUpdateDes.setFocusableInTouchMode(CommunityHelper.getInstance().isCreator());

        etUpdateName.setFocusable(CommunityHelper.getInstance().isCreator());
        etUpdateName.setFocusableInTouchMode(CommunityHelper.getInstance().isCreator());
        updateTitle = getView(R.id.update_title);
        // 控制显示ui
        boolean isName = UpdateType.ChannelName == updateType || UpdateType.CommunityName == updateType;

        UIKit.setVisible(updateTitle, isName);
        UIKit.setVisible(etUpdateName, isName);
        UIKit.setVisible(etUpdateDes, !isName);
        if (isName) {
            etUpdateName.setText(last);
        } else {
            etUpdateDes.setText(last);
        }
        // 名称 提示
        String tip = title.replace("修改", "");
        updateTitle.setText(tip);
    }

    private void updateCommunity() {
        CommunityDetailsBean communityDetailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        CommunityDetailsBean temp = communityDetailsBean.clone();
        if (UpdateType.CommunityName == updateType) {
            temp.setName(etUpdateName.getText().toString());
        } else {
            temp.setRemark(etUpdateDes.getText().toString());
        }
        temp.setUpdateType(Constants.UpdateType.UPDATE_TYPE_ALL.getUpdateTypeCode());
        CommunityHelper.getInstance().saveCommunityAll(temp, new IResultBack<Wrapper>() {
            @Override
            public void onResult(Wrapper wrapper) {
                if (wrapper.ok()) {
                    KToast.show(UpdateType.CommunityName == updateType ? "修改社区名称成功" : "修改社区简介成功");
                    finish();
                } else {
                    KToast.show(wrapper.getMessage());
                }
            }
        });
    }

    private void updateChannel() {
        ChannelDetailsBean details = CommunityHelper.channelDetailsLiveData.getValue();
        if (null != details) {
            boolean isName = UpdateType.ChannelName == updateType;
            String value = (isName ? etUpdateName : etUpdateDes).getText().toString().trim();
            Map<String, Object> params = new HashMap<>();
            params.put("uid", details.getUid());
            params.put(isName ? "name" : "remark", value);
            OkApi.post(CommunityAPI.Channel_Update, params, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (result.ok()) {
                        if (isName) {
                            details.setName(value);
                        } else {
                            details.setRemark(value);
                        }
                        CommunityHelper.channelDetailsLiveData.setValue(details);
                        KToast.show(R.string.cmu_update_success);
                        finish();
                    } else {
                        KToast.show(result.getMessage());
                    }
                }
            });
        }
    }
}
