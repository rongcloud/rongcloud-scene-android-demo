package com.rc.live.room;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.BitmapBody;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseFragment;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.SoftKeyboardUtils;
import com.basis.widget.ChineseLengthFilter;
import com.basis.widget.loading.LoadTag;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rc.live.R;
import com.rc.live.helper.LiveEventHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.rongcloud.beauty.data.BeautyDataManager;
import cn.rongcloud.beauty.data.BeautyManager;
import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;
import cn.rongcloud.beauty.listener.BeautyDataSourceListener;
import cn.rongcloud.beauty.ui.BeautyMainFragment;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.liveroom.weight.RCLiveView;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import io.rong.imkit.utils.StatusBarUtil;

/**
 * lihao
 * 创建直播房fragment
 */
public class CreateLiveRoomFragment extends BaseFragment implements View.OnClickListener {


    private RelativeLayout rlSettingId;
    private AppCompatImageView ivBack;
    private AppCompatEditText etRoomName;
    private RadioGroup rgIsPublic;
    private AppCompatRadioButton rbPrivate;
    private AppCompatRadioButton rbPublic;
    private AppCompatButton btnStartLive;
    private AppCompatTextView tvTurnCamera;
    private AppCompatTextView tvTags;
    private AppCompatTextView tvBeauty;
    private AppCompatTextView tvBeautyMakeup;
    private AppCompatTextView tvEffects;


    private RoundedImageView ivRoomCover;
    private String mCoverUrl;
    private String mPassword = "";
    private InputPasswordDialog mInputPasswordDialog;
    private LoadTag mLoading;
    private RoomType mRoomType = RoomType.LIVE_ROOM;
    private CreateRoomCallBack mCreateRoomCallBack;
    private BeautyMainFragment beautyMainFragment;
    private ActivityResultLauncher mLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result != null
                                && result.getData() != null
                                && result.getData().getData() != null
                        ) {

                            setCoverUri(result.getData().getData());
                        }
                    });
    private GestureDetector mGestureDetector;
    private View rl_content;
    private int[] themeArray = new int[]{
            R.drawable.img_room_theme_1,
            R.drawable.img_room_theme_2,
            R.drawable.img_room_theme_3,
            R.drawable.img_room_theme_4,
            R.drawable.img_room_theme_5,
            R.drawable.img_room_theme_6
    };
    private Bitmap themeBitmap;

    public static CreateLiveRoomFragment getInstance() {
        Bundle bundle = new Bundle();
        CreateLiveRoomFragment fragment = new CreateLiveRoomFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_create_liveroom_layout;
    }

    /**
     * 设置创建监听
     *
     * @param mCreateRoomCallBack
     */
    public void setCreateRoomCallBack(CreateRoomCallBack mCreateRoomCallBack) {
        this.mCreateRoomCallBack = mCreateRoomCallBack;
    }

    @Override
    public void init() {
        mLoading = new LoadTag(requireActivity(), requireActivity().getString(R.string.text_creating_room));
        initView();
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                SoftKeyboardUtils.hideSoftKeyboard(etRoomName);
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
    }

    /**
     * 避免出现摄像头被占用的情况
     */
    @Override
    public void onResume() {
        super.onResume();
        //开始准备直播事项，比如打开摄像头
        LiveEventHelper.getInstance().register("-1");
        LiveEventHelper.getInstance().prepare(new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    mCreateRoomCallBack.prepareSuccess(RCLiveEngine.getInstance().preview());
                }
            }
        });
    }

    @Override
    public void initListener() {
        //房间封面
        ivRoomCover.setOnClickListener(this::onClick);
        //开始直播
        btnStartLive.setOnClickListener(this::onClick);
        //翻转
        tvTurnCamera.setOnClickListener(this::onClick);
        //贴纸
        // tvTags.setOnClickListener(this::onClick);
        //美颜
        tvBeauty.setOnClickListener(this::onClick);
        //美妆
        // tvBeautyMakeup.setOnClickListener(this::onClick);
        //特效
        // tvEffects.setOnClickListener(this::onClick);

        ivBack.setOnClickListener(this::onClick);
    }

    private void initView() {
        rl_content = getView().findViewById(R.id.rl_content);
        rl_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        rlSettingId = (RelativeLayout) getView().findViewById(R.id.rl_setting_id);
        ivBack = (AppCompatImageView) getView().findViewById(R.id.iv_back);
        etRoomName = (AppCompatEditText) getView().findViewById(R.id.et_room_name);
        etRoomName.setFilters(new InputFilter[]{new ChineseLengthFilter(20)});
        rgIsPublic = (RadioGroup) getView().findViewById(R.id.rg_is_public);
        rbPrivate = (AppCompatRadioButton) getView().findViewById(R.id.rb_private);
        rbPublic = (AppCompatRadioButton) getView().findViewById(R.id.rb_public);
        btnStartLive = (AppCompatButton) getView().findViewById(R.id.btn_start_live);
        tvTurnCamera = (AppCompatTextView) getView().findViewById(R.id.tv_turn_camera);
        tvTags = (AppCompatTextView) getView().findViewById(R.id.tv_tags);
        tvBeauty = (AppCompatTextView) getView().findViewById(R.id.tv_beauty);
        tvBeautyMakeup = (AppCompatTextView) getView().findViewById(R.id.tv_beauty_makeup);
        tvEffects = (AppCompatTextView) getView().findViewById(R.id.tv_effects);
        ivRoomCover = (RoundedImageView) getView().findViewById(R.id.iv_room_cover);
        rlSettingId.setPadding(0, StatusBarUtil.getStatusBarHeight(requireContext()), 0, 0);

        int themeId = themeArray[new Random().nextInt(themeArray.length)];
        themeBitmap = BitmapFactory.decodeResource(getResources(), themeId);
        ivRoomCover.setImageBitmap(themeBitmap);

        beautyMainFragment = BeautyMainFragment.getInstance(new BeautyDataSourceListener() {
            @Override
            public List<BeautyCategory> getDefaultBeautyData() {
                return BeautyDataManager.getDefaultBeautyData();
            }

            @Override
            public List<BeautyCategory> getCurrentBeautyData() {
                return BeautyDataManager.getCurrentBeautyData();
            }

            @Override
            public void onEnableBeauty(boolean enable) {
                BeautyManager.setEnable(enable);
            }

            @Override
            public void onSetBeauty(BeautyBean beauty) {
                BeautyManager.setBeautyIntensity(beauty);
            }
        });
    }


    /**
     * 选择图片
     */
    private void startPicSelectActivity() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        if (mLauncher != null) {
            mLauncher.launch(intent);
        }
    }

    /**
     * 设置封面
     *
     * @param coverUri
     */
    public void setCoverUri(Uri coverUri) {
        this.mCoverUrl = RealPathFromUriUtils.getRealPathFromUri(requireContext(), coverUri);
        ImageLoader.loadUri(ivRoomCover, coverUri, R.drawable.ic_create_voice_room_default_cover);
    }


    /**
     * 创建房间前逻辑判断
     */
    private void preCreateRoom() {
        // 房间名检测
        String roomName = etRoomName.getText() == null ? "" : etRoomName.getText().toString();
        if (TextUtils.isEmpty(roomName)) {
            KToast.show(requireActivity().getString(R.string.please_input_room_name));
            return;
        }
        // 私密房密码检测
        if (rbPrivate.isChecked() && TextUtils.isEmpty(mPassword)) {
            mInputPasswordDialog = new InputPasswordDialog(requireContext(), false, new InputPasswordDialog.OnClickListener() {
                @Override
                public void clickCancel() {

                }

                @Override
                public void clickConfirm(String password) {
                    if (TextUtils.isEmpty(password)) {
                        return;
                    }
                    if (password.length() < 4) {
                        KToast.show(requireActivity().getString(R.string.text_please_input_four_number));
                        return;
                    }
                    mPassword = password;
                    mInputPasswordDialog.dismiss();
                    uploadThemePic(roomName);
                }
            });
            mInputPasswordDialog.show();
        } else {
            uploadThemePic(roomName);
        }
    }

    private void uploadThemePic(String roomName) {
        // 选择本地图片后，先上传本地图片
        if (!TextUtils.isEmpty(mCoverUrl)) {
            mLoading.show();
            FileBody body = new FileBody("multipart/form-data", new File(mCoverUrl));
            OkApi.file(VRApi.FILE_UPLOAD, "file", body, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    String url = result.getBody().getAsString();
                    if (result.ok() && !TextUtils.isEmpty(url)) {
                        createRoom(roomName, VRApi.FILE_PATH + url);
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
        } else if (themeBitmap != null) {
            mLoading.show();
            BitmapBody body = new BitmapBody(null, themeBitmap);
            OkApi.bitmap(VRApi.FILE_UPLOAD, "file", body, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    String url = result.getBody().getAsString();
                    if (result.ok() && !TextUtils.isEmpty(url)) {
                        createRoom(roomName, VRApi.FILE_PATH + url);
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
            mLoading.show();
            createRoom(roomName, "");
        }
    }


    /**
     * 创建房间
     *
     * @param roomName
     * @param themeUrl
     */
    private void createRoom(String roomName, String themeUrl) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", roomName);
        params.put("themePictureUrl", themeUrl);
        params.put("isPrivate", rbPrivate.isChecked() ? 1 : 0);
        params.put("password", mPassword);
        params.put("kv", new ArrayList());
        params.put("roomType", mRoomType.getType());
        OkApi.post(VRApi.ROOM_CREATE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (mCreateRoomCallBack != null) {
                    VoiceRoomBean voiceRoomBean = result.get(VoiceRoomBean.class);
                    if (result.ok() && voiceRoomBean != null) {
                        SensorsUtil.instance().createRoom(voiceRoomBean.getRoomId(), voiceRoomBean.getRoomName(), rbPrivate.isChecked(),
                                true, true, mRoomType.convertToRcEvent());
                        mCreateRoomCallBack.onCreateSuccess(voiceRoomBean);
                    } else if (30016 == result.getCode() && voiceRoomBean != null) {
                        mCreateRoomCallBack.onCreateExist(voiceRoomBean);
                    } else {
                        KToast.show(result.getMessage());
                    }
                }
                mLoading.dismiss();
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(msg);
                mLoading.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_room_cover) {
            startPicSelectActivity();
        } else if (id == R.id.btn_start_live) {
            preCreateRoom();
        } else if (id == R.id.tv_turn_camera) {
            RCLiveEngine.getInstance().switchCamera(null);
        } else if (id == R.id.tv_tags) {
        } else if (id == R.id.tv_beauty) {
            beautyMainFragment.show(getChildFragmentManager(), "BeautyMainFragment-1");
        } else if (id == R.id.tv_beauty_makeup) {

        } else if (id == R.id.tv_effects) {
        } else if (id == ivBack.getId()) {
            LiveEventHelper.getInstance().unRegister();
            requireActivity().finish();
        }
    }


    public interface CreateRoomCallBack {
        void onCreateSuccess(VoiceRoomBean voiceRoomBean);

        void onCreateExist(VoiceRoomBean voiceRoomBean);

        void prepareSuccess(RCLiveView rcLiveVideoView);
    }
}
