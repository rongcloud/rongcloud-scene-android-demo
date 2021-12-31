package com.basis.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basis.UIStack;
import com.basis.mvp.BasePresenter;
import com.basis.mvp.IBaseView;
import com.basis.net.LoadTag;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

/**
 * @author: BaiCQ
 * @ClassName: BaseFragment
 * @date: 2018/8/17
 * @Description: Fragment 的基类
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBasis, IBaseView {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity activity;
    private View layout;
    private boolean init = false;//init 和 onRefresh()的执行的先后问题
    public P present;
    private LoadTag mLoadTag;

    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
        UIStack.getInstance().add(this);
        mLoadTag = new LoadTag(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UIStack.getInstance().remove(this);
        init = false;
        Logger.e(TAG, "onDetach");
    }

    public abstract P createPresent();

    @Deprecated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(setLayoutId(), null);
        Logger.e(TAG, "onCreateView");
        return layout;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.e(TAG, "onViewCreated");
        present = createPresent();
        init();
        initListener();
        init = true;
    }

    @Override
    public abstract int setLayoutId();

    @Override
    public abstract void init();

    public abstract void initListener();

    protected View getLayout() {
        return layout;
    }

    protected <T extends View> T getView(@IdRes int id) {
        return layout.findViewById(id);
    }

    /**
     * 首次刷新尽量先于init执行
     */
    @Override
    public void onRefresh(Object obj) {
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
    public void showLoading(String msg) {
        mLoadTag.show(msg);
    }

    @Override
    public void dismissLoading() {
        mLoadTag.dismiss();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showToast(String message) {
        KToast.show(message);
    }
}
