/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */
package io.rong.dial;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.feedback.FeedbackHelper;
import cn.rongcloud.config.router.RouterPath;
import io.rong.callkit.BaseActionBarActivity;
import io.rong.callkit.R;
import io.rong.callkit.RongCallKit;
import io.rong.callkit.util.DateUtil;
import io.rong.dial.dialpad.DialpadFragment;
import io.rong.dial.dialpad.animation.AnimUtils;
import io.rong.dial.dialpad.widget.FloatingActionButtonController;

/**
 * 拨号界面
 */
@Route(path = RouterPath.ROUTER_CALL)
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
    private TextView tvTip;
    private View emptyTip;
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
        userId = UserManager.get().getUserId();
        String title = isVideo ? "视频通话" : "语音通话";
        initDefalutActionBar(title);
        initView();
        if (!TextUtils.isEmpty(userId)) {
            RecordManager.observeCache(new IResultBack<List<DialInfo>>() {
                @Override
                public void onResult(List<DialInfo> models) {
                    if (null != models) {
                        records.clear();
                        records.addAll(models);
                        refreshRecords(records);
                    }
                }
            });
        }
        FeedbackHelper.getHelper().registeFeedbackObservice(this);
    }

    private void refreshRecords(List<DialInfo> records) {
        Logger.e(TAG, "refreshViewByRecord:records = " + records);
        if (null != records && null != recyclerView.getAdapter()) {
            ((RcySAdapter<DialInfo, RcyHolder>) recyclerView.getAdapter()).setData(records, true);
        }
        refreshViewByRecord(records != null && !records.isEmpty());
    }

    private void refreshViewByRecord(boolean hasRecord) {
        Logger.e(TAG, "refreshViewByRecord:hasRecord = " + hasRecord);
        UIKit.setVisible(recyclerView, hasRecord);
        UIKit.setVisible(emptyTip, !hasRecord);
        if (null != tvTip)
            tvTip.setText(isVideo ? R.string.video_no_record_tip : R.string.audio_no_record_tip);
    }

    private void initView() {
        findViewById(R.id.ll_customer).setOnClickListener(this);
        final View floatingActionButtonContainer = findViewById(
                R.id.floating_action_button_container);
        emptyTip = findViewById(R.id.layout_empty);
        tvTip = findViewById(R.id.tv_no_record_tip);
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
                    if (!TextUtils.isEmpty(info.getHead()) && !info.getHead().equals(ApiConfig.FILE_URL)) {
                        Glide.with(DialActivity.this)
                                .load(info.getHead())
                                .placeholder(R.drawable.rc_default_portrait)
                                .override(100)
                                .into(head);
                    } else {
                        Glide.with(DialActivity.this)
                                .load(ApiConfig.DEFAULT_PORTRAIT_ULR)
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
        refreshViewByRecord(false);
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
            Uri data = Uri.parse("tel:" + ApiConfig.CUSTOMER_PHONE);
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
        if (num.equals(UserManager.get().getPhone())) {
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
            dialInfo.setDate(System.currentTimeMillis());
            RecordManager.save(dialInfo);
            hideDialpadFragment(true);
            RongCallKit.startSingleCall(DialActivity.this, dialInfo.getUserId(),
                    isVideo ? RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO
                            : RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
        } else {
            getUserIdByPhone(num);
        }

    }

    private void getUserIdByPhone(final String phone) {
        String url = ApiConfig.HOST + "/user/get/" + phone;
        OkApi.get(url, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    UserInfoModel user = result.get(UserInfoModel.class);
                    if (null != user) {
                        DialInfo dial = DialInfo.fromUserInfoModel(user);
                        RecordManager.save(dial);
                        UIKit.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideDialpadFragment(true);
                                RongCallKit.startSingleCall(DialActivity.this, user.getUid(),
                                        isVideo ? RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO
                                                : RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO);
                            }
                        });
                    } else {
                        KToast.show("号码未注册");
                    }
                } else {
                    KToast.show("号码未注册");
                }
            }
        });
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
