package cn.rc.community.setting.manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.UltraGroupNotificationLeaveManager;
import cn.rc.community.setting.notify.NotificationsLeave;
import cn.rongcloud.config.ApiConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.IRongCoreEnum;

/**
 * 社区设置- 管理 - 编辑资料
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView ivPortrait;
    private CircleImageView ivThem;
    private TextView tvDefaultNotify;

    @Override
    public int setLayoutId() {
        return R.layout.activity_edite_profile;
    }

    private TextView tvName, tvDes;

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_edit_profile)).work();
        getView(R.id.ll_name).setOnClickListener(this);
        getView(R.id.ll_portrait).setOnClickListener(this);
        getView(R.id.ll_them).setOnClickListener(this);
        getView(R.id.ll_des).setOnClickListener(this);
        getView(R.id.ll_default_channel).setOnClickListener(this);
        getView(R.id.ll_notify_setting).setOnClickListener(this);
        getView(R.id.ll_sys_channel).setOnClickListener(this);
        tvName = getView(R.id.tv_community_name);
        TextView tvDefaultChannel = getView(R.id.tv_default_channel);
        tvDefaultNotify = getView(R.id.tv_notify_setting);
        TextView tvSysChannel = getView(R.id.tv_sys_channel);
        tvDes = getView(R.id.tv_community_des);
        ivPortrait = findViewById(R.id.iv_portrait);
        ivThem = findViewById(R.id.iv_them);
        CommunityHelper.communityDetailsLiveData.observe(this, new Observer<CommunityDetailsBean>() {
            @Override
            public void onChanged(CommunityDetailsBean communityDetailsBean) {
                if (communityDetailsBean == null) return;
                String joinChannelUid = communityDetailsBean.getJoinChannelUid();
                String msgChannelUid = communityDetailsBean.getMsgChannelUid();
                ArrayList<ChannelBean> channelBeans = new ArrayList<>();
                if (!TextUtils.isEmpty(joinChannelUid)) {
                    channelBeans.addAll(communityDetailsBean.getChannelList());
                    List<GroupBean> groupList = communityDetailsBean.getGroupList();
                    for (GroupBean groupBean : groupList) {
                        channelBeans.addAll(groupBean.getChannelList());
                    }
                    for (ChannelBean channelBean : channelBeans) {
                        if (TextUtils.equals(channelBean.getUid(), joinChannelUid)) {
                            tvDefaultChannel.setText(channelBean.getName());
                        }
                        if (TextUtils.equals(channelBean.getUid(), msgChannelUid)) {
                            tvSysChannel.setText(channelBean.getName());
                        }
                    }
                }
//                NotifyType notifyType = NotifyType.valued(communityDetailsBean.getNoticeType());
//                tvDefaultNotify.setText(notifyType.getDes());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvName.setText(CommunityHelper.getInstance().getCommunityDetailsBean().getName());
        tvDes.setText(CommunityHelper.getInstance().getCommunityDetailsBean().getRemark());
        ImageLoader.loadUrl(ivPortrait, CommunityHelper.getInstance().getCommunityDetailsBean().getPortrait(), R.drawable.cmu_default_portrait);
        ImageLoader.loadUrl(ivThem, CommunityHelper.getInstance().getCommunityDetailsBean().getCoverUrl(), R.drawable.cmu_default_portrait);
        UltraGroupNotificationLeaveManager.get().getUltraGroupConversationDefaultNotificationLevel(CommunityHelper.getInstance().getCommunityUid(), new IResultBack<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onResult(IRongCoreEnum.PushNotificationLevel level) {
                tvDefaultNotify.setText(NotificationsLeave.valued(level).getDes());
            }
        });
    }

    private final static int CODE_SELECT_CHANNEL_DEF = 10012;
    private final static int CODE_SELECT_CHANNEL_SYS = 10013;
    private final static int CODE_SELECT_THEMES = 10014;
    private final static int CODE_SELECT_PORTRAIT = 10015;


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.ll_name == id) {
            ProfileUpdateActivity.openProfileUpdate(activity,
                    ProfileUpdateActivity.UpdateType.CommunityName,
                    R.string.cmu_profile_update_name, CommunityHelper.getInstance().getCommunityDetailsBean().getName());
        } else if (R.id.ll_portrait == id) {
            openPictureSelector(CODE_SELECT_PORTRAIT);
        } else if (R.id.ll_them == id) {
            openPictureSelector(CODE_SELECT_THEMES);
        } else if (R.id.ll_des == id) {
            ProfileUpdateActivity.openProfileUpdate(activity,
                    ProfileUpdateActivity.UpdateType.CommunityDes,
                    R.string.cmu_profile_update_des,
                    CommunityHelper.getInstance().getCommunityDetailsBean().getRemark());
        } else if (R.id.ll_default_channel == id) {
            Intent i = new Intent(activity, SelectChannelActivity.class);
            i.putExtra("Code", CODE_SELECT_CHANNEL_DEF);
            activity.startActivityForResult(i, CODE_SELECT_CHANNEL_DEF);
        } else if (R.id.ll_notify_setting == id) {
            UIKit.startActivity(activity, ProfileNotifyActivity.class);
        } else if (R.id.ll_sys_channel == id) {
            Intent i = new Intent(activity, SelectChannelActivity.class);
            i.putExtra("Code", CODE_SELECT_CHANNEL_SYS);
            activity.startActivityForResult(i, CODE_SELECT_CHANNEL_SYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && data != null) {
            if (CODE_SELECT_PORTRAIT == requestCode || CODE_SELECT_THEMES == requestCode) {
                Uri selectImageUrl = data.getData();
                String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(activity, selectImageUrl);
                FileBody body = new FileBody("multipart/form-data", new File(realPathFromUri));
                OkApi.file(ApiConfig.FILE_UPLOAD, "file", body, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        String url = result.getBody().getAsString();
                        uploadImage(requestCode, url);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        KToast.show("头像上传失败");
                    }
                });
            }
        }
    }

    /**
     * 上传文件
     *
     * @param requestCode
     * @param url
     */
    private void uploadImage(int requestCode, String url) {
        CommunityDetailsBean communityDetailsBean = CommunityHelper.getInstance().getCommunityDetailsBean();
        CommunityDetailsBean temp = communityDetailsBean.clone();
        if (requestCode == CODE_SELECT_PORTRAIT) {
            temp.setPortrait(url);
        } else {
            temp.setCoverUrl(url);
        }
        temp.setUpdateType(Constants.UpdateType.UPDATE_TYPE_ALL.getUpdateTypeCode());
        CommunityHelper.getInstance().saveCommunityAll(temp, new IResultBack<Wrapper>() {
            @Override
            public void onResult(Wrapper wrapper) {
                if (wrapper.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_save_success));
                    ImageLoader.loadUrl(requestCode == CODE_SELECT_PORTRAIT ? ivPortrait : ivThem, ApiConfig.FILE_URL + url, R.drawable.cmu_default_portrait);
                } else {
                    KToast.show(wrapper.getMessage());
                }
            }
        });
    }


    /**
     * 打开相册
     */
    private void openPictureSelector(int mRequestCode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, mRequestCode);
    }
}
