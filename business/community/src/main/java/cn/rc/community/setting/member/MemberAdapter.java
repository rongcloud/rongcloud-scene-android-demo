package cn.rc.community.setting.member;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.UIStack;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.VRCenterDialog;

import java.util.Arrays;
import java.util.List;

import cn.rc.community.CommunityStyleBottomDialog;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.MemberBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.setting.CustomerBottomDialog;
import io.rong.imlib.RongCoreClient;

public class MemberAdapter extends RcySAdapter<MemberBean.RecordsBean, RcyHolder> implements IMemberSetting {
    private static List<String> ITEMS;

    public MemberAdapter(Context context) {
        super(context, R.layout.cmu_item_member);
    }

    @Override
    public void convert(RcyHolder holder, MemberBean.RecordsBean recordsBean, int position) {
        holder.setText(R.id.tv_member_name, recordsBean.getName());
        holder.setVisible(R.id.tv_identity, recordsBean.isCreatorFlag());
        holder.setVisible(R.id.more, CommunityHelper.getInstance().isCreator());
        holder.setOnClickListener(R.id.more, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunityHelper.getInstance().getCommunityDetailsBean().getId();
                showMemberManagerDialog(recordsBean);
            }
        });
        ImageLoader.loadUrl(holder.getView(R.id.left), recordsBean.getPortrait(), R.drawable.rc_default_portrait);
    }

    private void showMemberManagerDialog(MemberBean.RecordsBean member) {
        //如果本人是创建者
        if (TextUtils.equals(member.getUserUid(), RongCoreClient.getInstance().getCurrentUserId())) {
            ITEMS = Arrays.asList(ResUtil.getString(R.string.cmu_member_update_nick)
            );
        } else {
            ITEMS = Arrays.asList(
//                ResUtil.getString(R.string.cmu_member_exchage_group),
                    ResUtil.getString(R.string.cmu_member_update_nick),
                    member.getShutUp() == 0 ?
                            ResUtil.getString(R.string.cmu_member_no_speaking) : ResUtil.getString(R.string.cmu_member_cancel_no_speaking),
//                ResUtil.getString(R.string.cmu_member_closure_all),
                    ResUtil.getString(R.string.cmu_member_kick_out));
        }
        new CustomerBottomDialog((Activity) context, ITEMS)
                .setOnItemClickListener(new CustomerBottomDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String s = ITEMS.get(position);
                        if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_exchage_group))) {
                            ExchangedGroup(member);
                        } else if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_update_nick))) {
                            ChangeNickName(member);
                        } else if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_no_speaking))) {
                            // Unknown Format Conversion Exception
                            String title = String.format(ResUtil.getString(R.string.cmu_is_no_speaking), member.getName());
                            VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                            confirmDialog.replaceContent(title, ResUtil.getString(R.string.rc_cancel), null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChangeSpeakStatus(member, Constants.SHUT_UP);
                                }
                            }, null);
                            confirmDialog.show();
                        } else if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_cancel_no_speaking))) {
                            ChangeSpeakStatus(member, Constants.NOT_SHUT_UP);
                        } else if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_closure_all))) {
                            Blocked(member);
                        } else if (TextUtils.equals(s, ResUtil.getString(R.string.cmu_member_kick_out))) {
                            String title = String.format(ResUtil.getString(R.string.cmu_member_is_kick_out), member.getName());
                            VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                            confirmDialog.replaceContent(title, ResUtil.getString(R.string.rc_cancel), null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    KickOut(member);
                                }
                            }, null);
                            confirmDialog.show();
                        }
                    }
                }).show();
    }


    @Override
    public void ExchangedGroup(MemberBean.RecordsBean member) {
        KToast.show("该功能正在开发中，尽请期待");
    }

    @Override
    public void ChangeNickName(MemberBean.RecordsBean member) {
        showNickDialog(member);
    }

    @Override
    public void ChangeSpeakStatus(MemberBean.RecordsBean member, String shutUp) {
        CommunityHelper.getInstance().updateUserSetting(member.getUserUid(),
                "shutUp", shutUp, new IResultBack<Wrapper>() {
                    @Override
                    public void onResult(Wrapper wrapper) {
                        if (wrapper.ok()) {
                            member.setShutUp(Integer.parseInt(shutUp));
                            switch (shutUp) {
                                case Constants.SHUT_UP:
                                    KToast.show(ResUtil.getString(R.string.cmu_shut_up_success));
                                    break;
                                case Constants.NOT_SHUT_UP:
                                    KToast.show(ResUtil.getString(R.string.cmu_cancel_shut_up_success));
                                    break;
                            }

                        } else {
                            KToast.show(wrapper.getMessage());
                        }
                    }
                });
    }

    @Override
    public void Blocked(MemberBean.RecordsBean member) {
        KToast.show("该功能正在开发中，尽请期待");
    }

    @Override
    public void KickOut(MemberBean.RecordsBean member) {
        CommunityHelper.getInstance().updateUserSetting(member.getUserUid(),
                "status", Constants.MemberStatus.KNOCKOUT.getCode() + "", new IResultBack<Wrapper>() {
                    @Override
                    public void onResult(Wrapper wrapper) {
                        if (wrapper.ok()) {
                            int i = getData().indexOf(member);
                            getData().remove(i);
                            notifyItemRemoved(i);
                            KToast.show(ResUtil.getString(R.string.cmu_kick_out_success));
                        } else {
                            KToast.show(wrapper.getMessage());
                        }
                    }
                });
    }


    /**
     * 显示修改昵称弹窗
     */
    private void showNickDialog(MemberBean.RecordsBean member) {
        View view = UIKit.inflate(R.layout.layout_editor);
        EditText editText = UIKit.getView(view, R.id.editor);
        editText.setText(member.getName());
        CommunityStyleBottomDialog communityStyleBottomDialog = new CommunityStyleBottomDialog((Activity) context);
        communityStyleBottomDialog.setCustomerContent(editText)
                .setTitle(ResUtil.getString(R.string.cmu_change_nick_name_in_community))
                .setTitleClickListener(new CommunityStyleBottomDialog.OnTitleClickListener() {
                    @Override
                    public void onSureClick(CommunityStyleBottomDialog dialog) {
                        String name = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            KToast.show(ResUtil.getString(R.string.cmu_please_enter_user_name));
                            return;
                        }
                        CommunityHelper.getInstance().updateUserSetting(member.getUserUid(), "nickName", name, new IResultBack<Wrapper>() {
                            @Override
                            public void onResult(Wrapper result) {
                                if (result.ok()) {
                                    communityStyleBottomDialog.dismiss();
                                    int i = getData().indexOf(member);
                                    member.setName(name);
                                    notifyItemChanged(i);
                                    KToast.show(ResUtil.getString(R.string.cmu_has_change_nickname));
                                } else {
                                    KToast.show(result.getMessage());
                                }
                            }
                        });
                    }
                }).show();
    }
}