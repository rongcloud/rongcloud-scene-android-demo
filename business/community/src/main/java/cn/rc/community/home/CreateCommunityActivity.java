package cn.rc.community.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.BitmapBody;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.ResUtil;
import com.basis.widget.loading.LoadTag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.rc.community.CommunityAPI;
import cn.rc.community.R;
import cn.rc.community.helper.CommunityHelper;
import cn.rongcloud.config.ApiConfig;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.common.FileUtils;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

/**
 * 社区 -> 创建社区
 */
public class CreateCommunityActivity extends BaseActivity implements View.OnClickListener {
    private EditText etName;
    private CircleImageView ivIcon;

    private String path;

    private int[] themeArray = new int[]{
            R.drawable.cmu_community_theme_1,
            R.drawable.cmu_community_theme_2,
            R.drawable.cmu_community_theme_3,
            R.drawable.cmu_community_theme_4,
            R.drawable.cmu_community_theme_5,
            R.drawable.cmu_community_theme_6,
            R.drawable.cmu_community_theme_7,
            R.drawable.cmu_community_theme_8,
            R.drawable.cmu_community_theme_9,
            R.drawable.cmu_community_theme_10

    };
    private Bitmap themeBitmap;
    private String name;
    private LoadTag mLoading;

    @Override
    public int setLayoutId() {
        return R.layout.activity_create_community;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_create_title)).work();
        etName = getView(R.id.et_name);
        ivIcon = findViewById(R.id.iv_icon);
        getView(R.id.create).setOnClickListener(this);
        getView(R.id.iv_camera).setOnClickListener(this);

        mLoading = new LoadTag(this, this.getString(R.string.cmu_text_creating_community));

        int themeId = themeArray[new Random().nextInt(themeArray.length)];
        themeBitmap = BitmapFactory.decodeResource(getResources(), themeId);
        ivIcon.setImageBitmap(themeBitmap);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.create == id) {
            name = etName.getText().toString().trim();
            mLoading.show();
            if (TextUtils.isEmpty(name)) {
                UserInfo u = RongUserInfoManager.getInstance().getCurrentUserInfo();
                name = String.format(ResUtil.getString(R.string.cmu_create_default_name),
                        null != u ? u.getName() : "");
            }
            if (TextUtils.isEmpty(path)) {
                //如果没有选取图片，那么就随机设置图片的
                BitmapBody body = new BitmapBody(null, themeBitmap);
                OkApi.bitmap(ApiConfig.FILE_UPLOAD, "file", body, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        String url = result.getBody().getAsString();
                        if (result.ok() && !TextUtils.isEmpty(url)) {
                            createCommunity(url);
                        } else {
                            KToast.show(result.getMessage());
                            mLoading.dismiss();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        KToast.show(msg);
                        mLoading.dismiss();
                    }
                });
            } else {
                File file = new File(path);
                if (FileUtils.getFileSize(file) > 5 * 1024 * 1024) {
                    KToast.show("图片文件最大支持5M");
                    mLoading.dismiss();
                    return;
                }
                FileBody body = new FileBody("multipart/form-data", file);
                OkApi.file(ApiConfig.FILE_UPLOAD, "file", body, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        String url = result.getBody().getAsString();
                        if (result.ok() && !TextUtils.isEmpty(url)) {
                            createCommunity(url);
                        } else {
                            KToast.show(result.getMessage());
                            mLoading.dismiss();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        KToast.show(msg);
                        mLoading.dismiss();
                    }
                });
            }
        } else if (R.id.iv_camera == id) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10000);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == Activity.RESULT_OK) {
            Uri selectImageUrl = data.getData();
            path = RealPathFromUriUtils.getRealPathFromUri(activity, selectImageUrl);
            if (null != selectImageUrl) {
                ImageLoader.loadUri(ivIcon, selectImageUrl, R.drawable.cmu_default_portrait);
            }
        }
    }

    /**
     * 创建社区
     *
     * @param url
     */
    private void createCommunity(String url) {
        //先保存一下当前的URI
        Map<String, Object> params = new HashMap<>(2);
        params.put("name", name);
        if (!TextUtils.isEmpty(url)) params.put("portrait", url);
        mLoading.show();
        OkApi.post(CommunityAPI.Community_Create, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    //房间创建成功
                    KToast.show("社区创建成功");
                    CommunityHelper.communityDetailsLiveData.postValue(null);
                    finish();
                } else {
                    KToast.show(result.getMessage());
                }
                mLoading.dismiss();
            }
        });
    }
}
