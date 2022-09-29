package cn.rc.community.setting.manager;

import android.view.View;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.widget.dialog.VRCenterDialog;

import cn.rc.community.CommunityAPI;
import cn.rc.community.R;
import cn.rc.community.helper.CommunityHelper;

/**
 * 社区设置 - 管理
 */
public class ManagerActivity extends BaseActivity implements View.OnClickListener {

    private VRCenterDialog finishDiolog;

    @Override
    public int setLayoutId() {
        return R.layout.activity_manager;
    }


    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_community_manager)).work();
        getView(R.id.ll_first).setOnClickListener(this);
        getView(R.id.ll_second).setOnClickListener(this);
        getView(R.id.ll_third).setOnClickListener(this);
        getView(R.id.ll_fourth).setOnClickListener(this);
        getView(R.id.ll_fivth).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.ll_first == id) {
            UIKit.startActivity(activity, ProfileActivity.class);
        } else if (R.id.ll_second == id) {
            UIKit.startActivity(activity, MemberVerifyActivity.class);
        } else if (R.id.ll_third == id) {
            KToast.show("该功能敬请期待");
        } else if (R.id.ll_fourth == id) {
            KToast.show("该功能敬请期待");
        } else if (R.id.ll_fivth == id) {
            showFinishDialog();
        }
    }

    /**
     * 显示是否关闭房间弹窗
     */
    private void showFinishDialog() {
        if (finishDiolog == null) {
            finishDiolog = new VRCenterDialog(this, null);
            finishDiolog.replaceContent(ResUtil.getString(R.string.cmu_whether_to_disband_the_current_community), "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDiolog.dismiss();
                }
            }, "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destroyCommunity();
                }
            }, null);
        }
        finishDiolog.show();
    }

    /**
     * 解散社区成功，清除掉当前的社区信息
     */
    private void destroyCommunity() {
        OkApi.post(CommunityAPI.Community_delete + CommunityHelper.getInstance().getCommunityDetailsBean().getUid(), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_delete_community_success));
                    CommunityHelper.communityDetailsLiveData.postValue(null);
                    finish();
                } else {
                    KToast.show(ResUtil.getString(R.string.cmu_delete_community_fail) + result.getMessage());
                }
            }
        });
    }
}
