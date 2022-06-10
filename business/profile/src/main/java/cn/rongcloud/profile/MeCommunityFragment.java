package cn.rongcloud.profile;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.profile.dialog.UnregisterDialog;
import cn.rongcloud.profile.webview.ActCommentWeb;


@Route(path = RouterPath.FRAGMENT_ME_COMMUNITY)
public class MeCommunityFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public int setLayoutId() {
        return R.layout.fragment_me_community;
    }

    private ImageView ivPortrait, ivSex;
    private TextView tv_name;

    @Override
    public void init() {
        ivPortrait = getView(R.id.iv_portrait);
        tv_name = getView(R.id.tv_name);
        ivSex = getView(R.id.sex);
        getView(R.id.setting).setOnClickListener(this);
        getView(R.id.ll_resister).setOnClickListener(this);
        getView(R.id.ll_private).setOnClickListener(this);
        getView(R.id.un_register).setOnClickListener(this);
        getView(R.id.ll_logout).setOnClickListener(this);
        resetProfileInfo();
    }

    private final static int CODE_SETTING = 10010;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.setting == id) {
            SettingCommunityActivity.startActivity(activity, CODE_SETTING);
        } else if (R.id.ll_resister == id) {
            ActCommentWeb.openCommentWeb(activity, ApiConfig.REGISTER, "注册条款");
        } else if (R.id.ll_private == id) {
            ActCommentWeb.openCommentWeb(activity, ApiConfig.PRIVACY, "隐私政策");
        } else if (R.id.un_register == id) {
            showUnregisterDialog();
        } else if (R.id.ll_logout == id) {
            UserManager.logout();
        }
    }

    UnregisterDialog unregisterDialog;

    public void showUnregisterDialog() {
        unregisterDialog = new UnregisterDialog(activity, v -> {
            unregisterDialog.dismiss();
            OkApi.post(ProfileApi.RESIGN, null, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (null != result && result.ok()) {
                        KToast.show("注销成功");
                        UserManager.logout();
                    }
                }
            });

        });
        unregisterDialog.show();
    }

    void resetProfileInfo() {
        User user = UserManager.get();
        if (null != user) {
            ivSex.setImageResource(Sex.man == user.getSex() ? R.drawable.svg_sex_man_outer : R.drawable.svg_sex_woman_outer);
            String url = user.getPortraitUrl();
            ImageLoader.loadUrl(ivPortrait, url, R.drawable.default_portrait, ImageLoader.Size.S_250);
            tv_name.setText(user.getUserName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && CODE_SETTING == requestCode) {
            resetProfileInfo();
        }
    }
}
