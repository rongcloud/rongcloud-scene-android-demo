package com.basis.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basis.utils.Logger;

import java.util.List;

/**
 * @author: BaiCQ
 * @ClassName: BaseFragment
 * @date: 2018/8/17
 * @Description: Fragment 的基类
 */
public abstract class BaseFragment extends Fragment implements IBasis {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity activity;
    private View layout;
    private boolean init = false;//init 和 onRefresh()的执行的先后问题

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
        UIStack.getInstance().add(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UIStack.getInstance().remove(this);
        init = false;
        Logger.e(TAG, "onDetach");
    }

    @Deprecated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(setLayoutId(), null);
        Logger.e(TAG, "onCreateView");
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.e(TAG, "onViewCreated");
        init();
        initListener();
        init = true;
    }

    @Override
    public abstract int setLayoutId();

    @Override
    public abstract void init();

    public void initListener() {
    }

    protected View getLayout() {
        return layout;
    }

    public String getTitle() {
        return "";
    }

    protected <T extends View> T getView(@IdRes int id) {
        return layout.findViewById(id);
    }

    /**
     * 首次刷新尽量先于init执行
     */
    @Override
    public void onRefresh(ICmd obj) {
        Logger.e(TAG, "onRefresh");
    }

    @Override
    public void onNetChange() {
        Logger.e(TAG, "onRefresh");
    }

    public boolean isInit() {
        return init;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        List<Fragment> fs = getChildFragmentManager().getFragments();
        for (Fragment f : fs) {
            f.onActivityResult(resultCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<Fragment> fs = getChildFragmentManager().getFragments();
        for (Fragment f : fs) {
            f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
