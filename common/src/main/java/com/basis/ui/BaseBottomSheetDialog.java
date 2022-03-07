package com.basis.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.basis.R;
import com.basis.utils.Logger;
import com.basis.utils.ScreenUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


/**
 * Created by gyn on 2021/11/23
 */
public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = BaseBottomSheetDialog.class.getSimpleName();

    private @LayoutRes
    int layoutId;

    private BottomSheetBehavior<View> mBehavior;

    public BaseBottomSheetDialog(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), layoutId, null);
        dialog.setContentView(view);
        View parent = (View) view.getParent();
        mBehavior = BottomSheetBehavior.from(parent);
        mBehavior.setHideable(isHideable());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if (isFullScreen()) {
            mBehavior.setPeekHeight(ScreenUtil.getScreenHeight());
            parent.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    public abstract void initView();

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            manager.beginTransaction().remove(this).commit();
            super.show(manager, tag);
        } catch (Exception e) {
            Logger.e(TAG, e.getLocalizedMessage());
        }

    }

    /**
     * 是否可以拖动关闭
     *
     * @return
     */
    protected boolean isHideable() {
        return false;
    }

    /**
     * 是否全屏
     *
     * @return
     */
    protected boolean isFullScreen() {
        return false;
    }

    /**
     * 初始化监听
     */
    protected void initListener() {

    }
}
