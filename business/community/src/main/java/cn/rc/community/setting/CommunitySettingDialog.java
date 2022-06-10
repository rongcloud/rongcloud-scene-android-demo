package cn.rc.community.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.BasisDialog;

import java.util.HashMap;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.CommunityStyleBottomDialog;
import cn.rc.community.R;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.channel.create.CreateChannelDialog;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.setting.manager.ManagerActivity;
import cn.rc.community.setting.member.MemberActivity;
import cn.rc.community.setting.notify.NotifyActivity;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 社区设置 底部弹框
 */
public class CommunitySettingDialog implements View.OnClickListener {

    private BasisDialog dialog;
    private LifecycleOwner lifecycleOwner;
    private CircleImageView communityIcon;
    private IResultBack<Refresh> resultBack;
    private Refresh needRefresh = Refresh.none;
    private DialogInterface.OnDismissListener listener;


    public enum Refresh {
        details, none, list
    }

    public CommunitySettingDialog(LifecycleOwner lifecycleOwner, Activity activity, IResultBack<Refresh> resultBack) {
        dialog = BasisDialog.bottom(activity, R.layout.layout_community_setting_dialog, -1);
        dialog.setCanceledOnTouchOutside(true);
        dialog.observeDismiss(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (null != listener) listener.onDismiss(dialogInterface);
                if (null != resultBack) resultBack.onResult(needRefresh);
            }
        });
        this.lifecycleOwner = lifecycleOwner;
        this.resultBack = resultBack;
        initView();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.listener = listener;
    }

    public void show() {
        //拿到最新的数据
        CommunityHelper.getInstance().getCommunityDetails(CommunityHelper.getInstance().getCommunityUid(), null);
        if (null != dialog) {
            dialog.show();
        }
    }

    private TextView communityName, communityNum, memberCount;

    void initView() {
        communityName = dialog.getView(R.id.community_name);
        communityNum = dialog.getView(R.id.community_num);
        communityIcon = dialog.getView(R.id.community_icon);
        memberCount = dialog.getView(R.id.tv_member_count);
        UIKit.setBoldText(communityName, true);

        dialog.getView(R.id.notify).setOnClickListener(this);
        dialog.getView(R.id.manager).setOnClickListener(this);

        dialog.getView(R.id.member).setOnClickListener(this);
        dialog.getView(R.id.nick_name).setOnClickListener(this);
        dialog.getView(R.id.create_group).setOnClickListener(this);
        dialog.getView(R.id.create_channel).setOnClickListener(this);
        dialog.getView(R.id.leave).setOnClickListener(this);

        CommunityHelper.communityDetailsLiveData.observe(lifecycleOwner, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) {
                    dialog.dismiss();
                    return;
                }
                Log.e("TAG", "onChanged: ");
                communityName.setText(communityDetailsBean.getName());
                communityNum.setText(ResUtil.getString(R.string.cmu_community_uid) + communityDetailsBean.getUid());
                memberCount.setText(ResUtil.getString(R.string.cmu_member) + " ( " + communityDetailsBean.getPersonCount() + " )");
                ImageLoader.loadUrl(communityIcon, communityDetailsBean.getPortrait(), R.drawable.cmu_default_portrait);
                // 权限: 管理 创建分组 频道 退出社区
                UIKit.setVisible(dialog.getView(R.id.manager), CommunityHelper.getInstance().isCreator());
                UIKit.setVisible(dialog.getView(R.id.leave), !CommunityHelper.getInstance().isCreator());
                UIKit.setVisible(dialog.getView(R.id.admin), CommunityHelper.getInstance().isCreator());
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.notify == id) {
            UIKit.startActivity(dialog.activity, NotifyActivity.class);
        } else if (R.id.manager == id) {
            UIKit.startActivity(dialog.activity, ManagerActivity.class);
        } else if (R.id.member == id) {
            UIKit.startActivity(dialog.activity, MemberActivity.class);
        } else if (R.id.nick_name == id) {
            showNickDialog();
        } else if (R.id.create_group == id) {
            showCreateGroupDialog();
        } else if (R.id.create_channel == id) {
            new CreateChannelDialog(dialog.activity, null, new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean aBoolean) {
                    if (aBoolean) {
                        needRefresh = Refresh.details;
                    }
                }
            }).show();
        } else if (R.id.leave == id) {
            leave();
        }
    }

    private void showNickDialog() {
        View view = UIKit.inflate(R.layout.layout_editor);
        EditText editText = UIKit.getView(view, R.id.editor);
        editText.setText(CommunityHelper.getInstance().getNickName());
        new CommunityStyleBottomDialog(dialog.activity)
                .setCustomerContent(editText)
                .setTitle(ResUtil.getString(R.string.cmu_nick_name_in_community))
                .setTitleClickListener(new CommunityStyleBottomDialog.OnTitleClickListener() {
                    @Override
                    public void onSureClick(CommunityStyleBottomDialog dialog) {
                        String name = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            KToast.show(ResUtil.getString(R.string.cmu_please_enter_user_name));
                            return;
                        }
                        CommunityHelper.getInstance().updateUserSetting(UserManager.get().getUserId(), "nickName", name, new IResultBack<Wrapper>() {
                            @Override
                            public void onResult(Wrapper result) {
                                if (result.ok()) {
                                    needRefresh = Refresh.details;
                                    dialog.dismiss();
                                    CommunityHelper.getInstance().setNickName(name);
                                    KToast.show(ResUtil.getString(R.string.cmu_save_success));
                                } else {
                                    KToast.show(result.getMessage());
                                }
                            }
                        });
                    }
                }).show();
    }


    private void showCreateGroupDialog() {
        View view = UIKit.inflate(R.layout.layout_editor);
        EditText editText = UIKit.getView(view, R.id.editor);
        editText.setHint(R.string.cmu_input_group_name);
        new CommunityStyleBottomDialog(dialog.activity)
                .setCustomerContent(editText)
                .setTitle(ResUtil.getString(R.string.cmu_create_group))
                .setTitleClickListener(new CommunityStyleBottomDialog.OnTitleClickListener() {
                    @Override
                    public void onSureClick(CommunityStyleBottomDialog dialog) {
                        String name = editText.getText().toString().trim();
                        if (!TextUtils.isEmpty(name)) {
                            createGroup(name, dialog);
                        } else {
                            KToast.show(R.string.cmu_input_group_name);
                        }
                    }
                }).show();
    }

    private void createGroup(String groupName, CommunityStyleBottomDialog dialog) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUid", CommunityHelper.getInstance().getCommunityUid());
        params.put("name", groupName);
        OkApi.post(CommunityAPI.Community_create_group, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_create_group_success));
                    needRefresh = Refresh.details;
                    if (null != dialog) dialog.dismiss();
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }

    private void leave() {
        User user = UserManager.get();
        if (null == user || TextUtils.isEmpty(user.getUserId())) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("communityUid", CommunityHelper.getInstance().getCommunityUid());
        params.put("userUid", user.getUserId());
        params.put("status", 4);//2:审核未通过,3:审核通过,4:退出，5：被踢出
        OkApi.post(CommunityAPI.Community_update_user_info, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_leave_community_success));
                    needRefresh = Refresh.list;
                    if (null != dialog) dialog.dismiss();
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }
}
