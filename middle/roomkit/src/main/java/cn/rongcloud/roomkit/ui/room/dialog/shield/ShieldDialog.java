package cn.rongcloud.roomkit.ui.room.dialog.shield;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.BottomDialog;
import com.basis.widget.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * 屏蔽词
 */
public class ShieldDialog extends BottomDialog {
    private final static FlowLayout.LayoutParams TAG_LAYOUT_PARAMS =
            new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    static {
        TAG_LAYOUT_PARAMS.setMargins(20, 10, 20, 10);
    }

    private final String TAG_ADD = "@TAG_ADD";
    private List<Shield> shields = new ArrayList<>();
    private int max_tag = 10;
    private ViewGroup flowLayout;
    //    private EditorDialog editorDialog;
    private String mRoomId;
    private EditorDialog editorDialog;
    private OnShieldDialogListener onShieldDialogListener;

    public ShieldDialog(Activity activity, String roomId, int max, OnShieldDialogListener onShieldDialogListener) {
        super(activity);
        this.max_tag = max;
        this.mRoomId = roomId;
        this.onShieldDialogListener = onShieldDialogListener;
        setContentView(R.layout.dialog_shield, 50);
        shields.add(Shield.buildDefault());
        initView();
    }

    TextView title;
    String titlePre;

    private void initView() {
        flowLayout = UIKit.getView(getContentView(), R.id.flow_tag);
        title = UIKit.getView(getContentView(), R.id.setting);
        titlePre = UIKit.getResources().getString(R.string.shield_setting);
        getShield();
        addView();
    }

    public ShieldDialog setRoomId(String roomId) {
        this.mRoomId = roomId;
        return this;
    }


    private void addView() {
        //往容器内添加TagView数据
        if (flowLayout != null) {
            flowLayout.removeAllViews();
        }
        int count = shields.size();
        //修改标题
        int tagSize = count;
        if (shields.contains(Shield.buildDefault())) {
            tagSize = count - 1;
        }
        String text = titlePre + " (" + tagSize + "/" + max_tag + ")";
        title.setText(text);
        for (int i = 0; i < count; i++) {
            final View tagView;
            Shield shield = shields.get(i);
            if (!shield.isDefault()) {// tag
                tagView = UIKit.inflate(R.layout.layout_tag);
                TextView tv = UIKit.getView(tagView, R.id.tv_tag);
                tv.setText(shield.getName());
                UIKit.getView(tagView, R.id.iv_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteShield(shield);
                    }
                });
            } else {// add
                tagView = UIKit.inflate(R.layout.layout_tag_add);
                tagView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (shields.size() >= max_tag + 1) {
                            KToast.show("最多只能添加" + max_tag + "个屏蔽词");
                            return;
                        }
                        showAddTag();
                    }
                });
            }
            flowLayout.addView(tagView, TAG_LAYOUT_PARAMS);
        }
    }

    private void getShield() {
        OkApi.get(VRApi.getShield(mRoomId), null, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    List<Shield> shields = result.getList(Shield.class);
                    if (shields != null && shields.size() > 0) {
                        ShieldDialog.this.shields.clear();
                        if (shields.size() < max_tag) {
                            ShieldDialog.this.shields.add(Shield.buildDefault());
                        }
                        ShieldDialog.this.shields.addAll(shields);
                        addView();
                    }
                }
            }
        });
    }

    private void showAddTag() {
        if (editorDialog == null) {
            editorDialog = new EditorDialog(mActivity, new IResultBack<String>() {
                @Override
                public void onResult(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        boolean isContains = false;
                        for (Shield shield : shields) {
                            if (TextUtils.equals(shield.getName(), s)) {
                                isContains = true;
                                break;
                            }
                        }
                        if (isContains) {
                            ToastUtils.s(getDialog().getContext(), "该屏蔽词已添加！");
                        } else {
                            addShield(s);
                        }
                    }
                }
            });
        }
        editorDialog.show();
    }

    private void addShield(final String s) {
        OkApi.post(VRApi.ADD_SHIELD, new OkParams().add("roomId", mRoomId).add("name", s).build(), new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    getShield();
                    Shield shield = result.get(Shield.class);
                    if (shield != null) {
                        shields.add(shield);
                        addView();
                        if (onShieldDialogListener != null)
                            onShieldDialogListener.onAddShield(s, shields);
                        // //添加屏蔽词成功，通知房间其他人去获取屏蔽词
                        // RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_ADD_SHIELD, s, null);
                        // //通知自己获取屏蔽词
                        // EventBus.get().emit(UPDATE_SHIELD, shields);
                    }
                } else {
                    ToastUtils.s(getDialog().getContext(), "添加敏感词失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                ToastUtils.s(getDialog().getContext(), "添加敏感词失败");
            }
        });
    }

    private void deleteShield(Shield shield) {
        OkApi.get(VRApi.deleteShield(shield.getId()), null, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    shields.remove(shield);
                    if (shields.size() < max_tag) {
                        if (shields.size() > 0 && !shields.get(0).isDefault()) {
                            //第一个不是默认加的情况下，添加上
                            ShieldDialog.this.shields.add(0, Shield.buildDefault());
                        }
                    }
                    addView();
                    if (onShieldDialogListener != null)
                        onShieldDialogListener.onDeleteShield(shield, shields);
                    // RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_DELETE_SHIELD, shield.getName(), null);

                    // EventBus.get().emit(UPDATE_SHIELD, shields);
                } else {
                    ToastUtils.s(getDialog().getContext(), "删除敏感词失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                ToastUtils.s(getDialog().getContext(), "删除敏感词失败");
            }
        });
    }

    public interface OnShieldDialogListener {
        void onAddShield(String shield, List<Shield> shields);

        void onDeleteShield(Shield shield, List<Shield> shields);
    }
}
