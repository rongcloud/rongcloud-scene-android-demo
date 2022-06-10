package cn.rc.community.channel.create;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.ScreenUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.HashMap;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.CommunityStyleBottomDialog;
import cn.rc.community.R;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.channel.create.SelectGroupDialog;
import cn.rc.community.helper.CommunityHelper;

public class CreateChannelDialog extends CommunityStyleBottomDialog implements CommunityStyleBottomDialog.OnTitleClickListener {

    private GroupBean selectedGroup;//当前选中的群组

    private TextView groupName;
    private View channelText;
    private EditText etChannelName;
    private IResultBack<Boolean> resultBack;

    /**
     * 创建频道弹窗
     *
     * @param activity
     * @param selectedGroup 选中的所属分组
     * @param resultBack    弹框消失前触发回调
     */
    public CreateChannelDialog(Activity activity, GroupBean selectedGroup, IResultBack<Boolean> resultBack) {
        super(activity);
        this.selectedGroup = selectedGroup;
        this.resultBack = resultBack;
        View view = UIKit.inflate(R.layout.layout_create_channel_dialog);
        setCustomerContent(view);
        setTitleClickListener(this);
        setTitle(ResUtil.getString(R.string.cmu_create_channel));
        initView(view);
    }

    void initView(View view) {
        UIKit.getView(view, R.id.channel_doc).setOnClickListener(this);
        UIKit.getView(view, R.id.channel_voice).setOnClickListener(this);
        channelText = UIKit.getView(view, R.id.channel_text);
        etChannelName = UIKit.getView(view, R.id.et_name);
        channelText.setOnClickListener(this);
        channelText.setSelected(true);
        groupName = view.findViewById(R.id.group_name);
        groupName.setOnClickListener(this);
        refreshView();
    }

    private void refreshView() {
        if (selectedGroup == null) {
            groupName.setText(ResUtil.getString(R.string.cmu_str_channel_in_group) + ResUtil.getString(R.string.cmu_str_please_select_a_group));
        } else {
            groupName.setText(ResUtil.getString(R.string.cmu_str_channel_in_group) + selectedGroup.getName());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (R.id.group_name == id) {
            int height = dialog.getContentView().getMeasuredHeight();
            int py = height * 100 / ScreenUtil.getScreenHeight() + 1;
            new SelectGroupDialog(dialog.activity, CommunityHelper.getInstance().getCommunityDetailsBean().getGroupList(), py)
                    .setOnItemClickListener(new SelectGroupDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            selectedGroup = CommunityHelper.getInstance().getCommunityDetailsBean().getGroupList().get(position);
                            refreshView();
                        }
                    })
                    .show();
        } else if (R.id.channel_text == id) {
            channelText.setSelected(true);
        } else if (R.id.channel_voice == id) {
            KToast.show("该功能还在开发中，敬请期待");
        } else if (R.id.channel_doc == id) {
            KToast.show("该功能还在开发中，敬请期待");
        } else if (R.id.cancel == id) {
            if (null != dialog) dialog.dismiss();
        }
    }

    /**
     * 创建频道
     *
     * @param channelName 频道名字
     */
    private void createChannel(String channelName) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("communityUid", CommunityHelper.getInstance().getCommunityDetailsBean().getUid());
        if (null != selectedGroup) params.put("groupUid", selectedGroup.uid);
        params.put("name", channelName);
        OkApi.post(CommunityAPI.Community_create_channel, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    //创建频道成功
                    dialog.dismiss();
                    KToast.show(ResUtil.getString(R.string.cmu_crate_channel_success));
                } else {
                    KToast.show(ResUtil.getString(R.string.cmu_crate_channel_fail) + result.getMessage());
                }
                if (null != resultBack) resultBack.onResult(result.ok());
            }
        });
    }

    @Override
    public void onSureClick(CommunityStyleBottomDialog dialog) {
        String name = etChannelName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            KToast.show(R.string.cmu_tip_channel_name);
            return;
        }
        createChannel(name);
    }
}
