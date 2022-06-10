/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.ui.BaseActivity;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.SoftBoardUtil;
import com.basis.utils.SystemUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.loading.LoadTag;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.profile.region.Region;
import cn.rongcloud.profile.region.RegionActivity;
import cn.rongcloud.profile.webview.ActCommentWeb;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@Route(path = RouterPath.ROUTER_LOGIN)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private View login, ivChecked;
    private EditText etCode, etPhone;
    private Button btnCode;
    private boolean checked = false;
    private TextView bottomInfo, vsersion, vRegion;

    private Region region;

    @Override
    public int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        initView();
        initBottom();
    }

    public void initView() {
        getWrapBar().setHide(true).work();
        btnCode = getView(R.id.btn_get_verification_code);
        etCode = getView(R.id.et_verification_code);
        etPhone = getView(R.id.et_phone_number);
        vRegion = getView(R.id.tv_region);
        login = getView(R.id.btn_login);
        bottomInfo = getView(R.id.bottom_info);
        vsersion = getView(R.id.bottom_version);

        ivChecked = getView(R.id.iv_checked);
        ivChecked.setSelected(checked);
        ivChecked.setOnClickListener(this);

        btnCode.setOnClickListener(this);
        login.setOnClickListener(this);
        vRegion.setOnClickListener(this);
    }

    void initBottom() {
        // 国际环境才选择显示地区
        UIKit.setVisible(vRegion, AppConfig.get().isInternational());
        SpannableStringBuilder style = new SpannableStringBuilder();
        style.append("同意《注册条款》和《隐私政策》并新登录即注册开通融云开发者账号");
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                ActCommentWeb.openCommentWeb(activity, ApiConfig.REGISTER, "注册条款");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0099FF"));
            }
        }, 2, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ClickableSpan() {

            @Override
            public void onClick(@NonNull View view) {
                ActCommentWeb.openCommentWeb(activity, ApiConfig.PRIVACY, "注册条款");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#0099FF"));
            }
        }, 9, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bottomInfo.setText(style);
        bottomInfo.setMovementMethod(LinkMovementMethod.getInstance());

        String verName = SystemUtil.getVerName();
        vsersion.setText("融云 RTC " + verName);

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = etCode.getText().toString().trim();
                if (code.length() == 6) {
                    SoftBoardUtil.hideKeyboard(etCode);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.btn_get_verification_code == id) {
            String phone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                KToast.show(R.string.please_input_phone_number);
                return;
            }
            if (!checked) {
                KToast.show(getString(R.string.profile_tip_select_privacy));
                return;
            }
            String reg = null != region ? region.getRegion() : "86";
            sendCode(reg, phone);
        } else if (R.id.btn_login == id) {
            if (!checked) {
                KToast.show(getString(R.string.profile_tip_select_privacy));
                return;
            }
            String phone = etPhone.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                KToast.show(getString(R.string.please_input_verification_code));
                return;
            }
            String reg = null != region ? region.getRegion() : "86";
            login(reg, phone, code);
        } else if (R.id.tv_region == id) {
            RegionActivity.openRegionPage(this);
        } else if (R.id.iv_checked == id) {
            checked = !ivChecked.isSelected();
            ivChecked.setSelected(checked);
        }
    }

    void sendCode(String reg, String phone) {
        ProfileApi.sendVerificationCode(reg, phone, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (aBoolean) {
                    nextVerificationDuration(60 * 1000);
                }
            }
        });
    }

    private CountDownTimer timer;

    void nextVerificationDuration(long time) {
        btnCode.setEnabled(false);
        if (null != timer) {
            timer.cancel();
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnCode.setText(String.format(
                        getString(R.string.verification_code_send_already),
                        millisUntilFinished / 1000
                ));
            }

            @Override
            public void onFinish() {
                btnCode.setText(R.string.get_verification_code_again);
                btnCode.setEnabled(true);
            }
        };
        timer.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && RegionActivity.CODE_REGION == requestCode && null != data) {
            region = (Region) data.getSerializableExtra("region");
            if (null != region) {
                vRegion.setText("+" + region.getRegion());
            }
        }
    }

    void login(String reg, String phone, String code) {
        LoadTag tag = new LoadTag(activity, "登录中...");
        tag.show();
        ProfileApi.login(reg, phone, code, new IResultBack<User>() {
            @Override
            public void onResult(User user) {
                if (null != user) {
                    initRongIM(tag, user);
                } else {
                    if (null != tag) tag.dismiss();
                }
            }
        });
    }

    void initRongIM(LoadTag tag, User user) {
        if (!TextUtils.isEmpty(user.getImToken())) {
            RongIM.connect(user.getImToken(), new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String t) {
                    Logger.e(TAG, "connect#onSuccess:" + t);
                    if (null != tag) tag.dismiss();
                    ARouter.getInstance().build(RouterPath.ROUTER_MAIN).navigation();
                    finish();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode e) {
                    if (null != tag) tag.dismiss();
                    Logger.e(TAG, "connect#onError:" + GsonUtil.obj2Json(e));
                }

                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                    Logger.e(TAG, "connect#onDatabaseOpened:code = " + code);
                }
            });
        } else {
            if (null != tag) tag.dismiss();
        }
    }
}

