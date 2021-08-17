/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */
package io.rong.callkit;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.bcq.adapter.recycle.RcyHolder;
import com.bcq.adapter.recycle.RcySAdapter;
import com.bumptech.glide.Glide;
import com.rongcloud.common.dao.database.DatabaseManager;
import com.rongcloud.common.dao.entities.CallRecordEntityKt;
import com.rongcloud.common.dao.model.query.CallRecordModel;
import com.rongcloud.common.net.ApiConstant;
import com.rongcloud.common.utils.AccountStore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.rong.combusis.feedback.FeedbackHelper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.rong.callkit.dialpad.DialInfo;
import io.rong.callkit.dialpad.DialpadFragment;
import io.rong.callkit.dialpad.animation.AnimUtils;
import io.rong.callkit.dialpad.widget.FloatingActionButtonController;
import io.rong.callkit.net.model.UserInfoModel;
import io.rong.callkit.util.DateUtil;
import io.rong.callkit.util.UserInfoProvider;

/**
 * 拨号界面
 */
public class DialActivity extends BaseActionBarActivity implements View.OnClickListener, DialpadFragment.DialpadListener {
    @VisibleForTesting
    public static final String TAG = "DialActivity";
    public static final String KEY_VIDEO = "is_video";
    public static final String KEY_ID = "user_id";
    public static final String KEY_TOKEN = "token";
    public static final String TAG_DIALPAD_FRAGMENT = "dialpad";
    private FloatingActionButtonController mFloatingActionButtonController;
    private ImageButton floatingActionButton;
    private RecyclerView recyclerView;
    private boolean isVideo = false;
    private String userId;
    private List<DialInfo> records = new ArrayList<>();

    public static void openDilapadPage(Activity activity, boolean video) {
        activity.startActivity(new Intent(activity, DialActivity.class)
                .putExtra(KEY_VIDEO, video));
    }

    @Override
    protected void onDestroy() {
        FeedbackHelper.getHelper().unregisteObservice();
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialtacts_activity);
        isVideo = getIntent().getBooleanExtra(KEY_VIDEO, false);
        userId = AccountStore.INSTANCE.getUserId();
        String title = isVideo ? "视频通话" : "语音通话";
        initDefalutActionBar(title);
        initView();
        addDisposable(DatabaseManager.INSTANCE.obCallRecordList(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CallRecordModel>>() {
                    @Override
                    public void accept(List<CallRecordModel> models) {
                        records.clear();
                        int size = null == models ? 0 : models.size();
                        DialInfo dialInfo;
                        for (int i = 0; i < size; i++) {
                            CallRecordModel model = models.get(i);
                            dialInfo = new DialInfo();
                            if (model.getDirection() == CallRecordEntityKt.DIRECTION_CALL) {
                                dialInfo.setPhone(TextUtils.isEmpty(model.getPeerNumber()) ? model.getPeerNumberFromInfo() : model.getPeerNumber());
                                dialInfo.setUserId(model.getPeerId());
                                dialInfo.setDate(model.getDate());
                                dialInfo.setHead(model.getPeerPortrait());
                            } else {
                                dialInfo.setPhone(TextUtils.isEmpty(model.getCallerNumber()) ? model.getCallNumberFromInfo() : model.getCallerNumber());
                                dialInfo.setUserId(model.getCallerId());
                                dialInfo.setDate(model.getDate());
                                dialInfo.setHead(model.getCallPortrait());
                            }

                            records.add(dialInfo);
                        }
                        refreshRecords(records);
                    }
                }));
        FeedbackHelper.getHelper().registeFeedbackObservice(this);
    }

    private void refreshRecords(List<DialInfo> records) {
        if (null != records && null != recyclerView.getAdapter()) {
            ((RcySAdapter) recyclerView.getAdapter()).setData(records, true);
        }
    }

    private void initView() {
        findViewById(R.id.ll_customer).setOnClickListener(this);
        final View floatingActionButtonContainer = findViewById(
                R.id.floating_action_button_container);
        floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(this);
        mFloatingActionButtonController = new FloatingActionButtonController(this,
                floatingActionButtonContainer, floatingActionButton);
        recyclerView = findViewById(R.id.rc_refresh);
        recyclerView.setAdapter(new RcySAdapter<DialInfo, RcyHolder>(this, R.layout.layout_dialpad_item) {
            @Override
            public void convert(RcyHolder holder, final DialInfo info, int position) {
                holder.setText(R.id.tv_number, info.getPhone());
                holder.setText(R.id.tv_date, DateUtil.getRecordDate(info.getDate()));
                ImageView head = holder.getView(R.id.iv_head);
                if (null != head) {
                    if (!TextUtils.isEmpty(info.getHead()) && !info.getHead().equals(ApiConstant.INSTANCE.getFILE_URL())) {
                        Glide.with(DialActivity.this)
                                .load(info.getHead())
                                .placeholder(R.drawable.rc_default_portrait)
                                .override(100)
                                .into(head);
                    } else {
                        Glide.with(DialActivity.this)
                                .load(ApiConstant.INSTANCE.getDEFAULT_PORTRAIT_ULR())
                                .placeholder(R.drawable.rc_default_portrait)
                                .override(100)
                                .into(head);
                    }
                }
                holder.rootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DialActivity) context).onRecordItemClick(info.getPhone());
                    }
                });
                holder.rootView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        removeItem(info);
                        notifyDataSetChanged();
                        return true;
                    }
                });
            }
        });
        //默认显示弹框
        showDialpadFragment("");
    }

    private void onRecordItemClick(String phone) {
        if (!mIsDialpadShown) {
            showDialpadFragment(phone);
        } else {
            dialpadFragment.setInputNum(phone);
        }
    }

    boolean mIsDialpadShown;

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.floating_action_button) {
            if (!mIsDialpadShown) {
                showDialpadFragment("");
            }
        } else if (R.id.ll_customer == resId) {//专属客户经理
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + ApiConstant.CUSTOMER_PHONE);
            intent.setData(data);
            startActivity(intent);
        }
    }

    @Override
    public void onInputFiltter(Editable input) {
        String filter = input.toString().trim();
        if (TextUtils.isEmpty(filter)) {
            refreshRecords(records);
            return;
        }
        int size = null == records ? 0 : records.size();
        if (size > 0) {
            List<DialInfo> fits = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                DialInfo info = records.get(i);
                if (!TextUtils.isEmpty(info.getPhone()) && info.getPhone().startsWith(filter)) {
                    fits.add(info);
                }
            }
            if (!fits.isEmpty()) {
                refreshRecords(fits);
            }
        }
    }

    @Override
    public void onDialpad(String num) {
        if (TextUtils.isEmpty(num)) {
            return;
        }
        if (num.equals(AccountStore.INSTANCE.getPhone())) {
            Toast.makeText(DialActivity.this, "不可拨打自己", Toast.LENGTH_LONG).show();
            return;
        }
        DialInfo dialInfo = null;
        int size = records.size();
        for (int i = 0; i < size; i++) {
            DialInfo temp = records.get(i);
            if (num.equals(temp.getPhone())) {
                dialInfo = temp;
            }
        }
        if (null != dialInfo) {
            DatabaseManager.INSTANCE.insertCallRecordAndMemberInfo(
                    userId,
                    "",
                    AccountStore.INSTANCE.getUserName(),
                    AccountStore.INSTANCE.getUserPortrait(),
                    dialInfo.getUserId(),
                    dialInfo.getPhone(),
                    "",
                    dialInfo.getHead(),//拼接前缀
                    new Date().getTime(),
                    0,
                    isVideo ? CallRecordEntityKt.VIDEO_SINGLE_CALL : CallRecordEntityKt.AUDIO_SINGLE_CALL,
                    CallRecordEntityKt.DIRECTION_CALL
            );
            hideDialpadFragment(true);
            RongCallKit.startSingleCall(DialActivity.this, dialInfo.getUserId(),
                    isVideo ? RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO
                            : RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
        } else {
            getUserIdByPhone(num);
        }

    }

    private void getUserIdByPhone(final String phone) {
        addDisposable(UserInfoProvider
                .getInstance()
                .getUserInfoByPhoneNumber(phone)
                .subscribe(new Consumer<UserInfoModel>() {
                    @Override
                    public void accept(UserInfoModel userInfoModel) throws Throwable {
                        if (userInfoModel.getCode() == ApiConstant.REQUEST_SUCCESS_CODE) {
                            final UserInfoModel.UserInfo userInfo = userInfoModel.getData();
                            if (userInfo == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DialActivity.this, "号码未注册", Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            DatabaseManager.INSTANCE.insertCallRecordAndMemberInfo(
                                    userId,
                                    "",
                                    "",
                                    "",
                                    userInfo.getUid(),
                                    phone,
                                    "",
                                    TextUtils.isEmpty(userInfo.getPortrait()) ? ApiConstant.INSTANCE.getDEFAULT_PORTRAIT_ULR()
                                            : ApiConstant.INSTANCE.getFILE_URL() + userInfo.getPortrait(),//拼接前缀
                                    new Date().getTime(),
                                    0,
                                    isVideo ? CallRecordEntityKt.VIDEO_SINGLE_CALL : CallRecordEntityKt.AUDIO_SINGLE_CALL,
                                    CallRecordEntityKt.DIRECTION_CALL
                            );
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideDialpadFragment(true);
                                    RongCallKit.startSingleCall(DialActivity.this, userInfo.getUid(),
                                            isVideo ? RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO
                                                    : RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                                }
                            });

                        }
                    }
                }));
    }

    private DialpadFragment dialpadFragment;

    private void showDialpadFragment(String defInput) {
        if (mIsDialpadShown) {
            return;
        }
        mIsDialpadShown = true;
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (dialpadFragment == null) {
            dialpadFragment = new DialpadFragment();
            Bundle bundle = new Bundle();
            bundle.putString(DialpadFragment.DEFAU_INPUT, defInput);
            dialpadFragment.setArguments(bundle);
            DialpadFragment.dialpadListener = new WeakReference<DialpadFragment.DialpadListener>(DialActivity.this);
            ft.add(R.id.dialtacts_container, dialpadFragment, TAG_DIALPAD_FRAGMENT);
        } else {
            ft.show(dialpadFragment);
            dialpadFragment.setInputNum(defInput);
        }
        ft.commitAllowingStateLoss();
        mFloatingActionButtonController.scaleOut();
        floatingActionButton.setImageResource(R.drawable.fab_ic_call);
    }

    public void hideDialpadFragment(boolean clearDialpad) {
        if (dialpadFragment == null || dialpadFragment.getView() == null) {
            return;
        }
        if (clearDialpad) {
            // Temporarily disable accessibility when we clear the dialpad, since it should be
            // invisible and should not announce anything.
            dialpadFragment.getDigitsWidget().setImportantForAccessibility(
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            dialpadFragment.clearDialpad();
            dialpadFragment.getDigitsWidget().setImportantForAccessibility(
                    View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        }
        if (!mIsDialpadShown) {
            return;
        }
        mIsDialpadShown = false;
        commitDialpadFragmentHide();
        floatingActionButton.setImageResource(R.drawable.fab_ic_dial);
    }

    private void commitDialpadFragmentHide() {
        if (dialpadFragment != null && !dialpadFragment.isHidden()) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(dialpadFragment);
            ft.commit();
        }
        mFloatingActionButtonController.scaleIn(AnimUtils.NO_DELAY);
    }

}
