package com.basis.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.basis.R;
import com.basis.net.IRHolder;
import com.bcq.refresh.IRefresh;
import com.kit.UIKit;
import com.kit.utils.Logger;

public class RHolder implements IRHolder {
    protected final String TAG = "Controller # RHolder";
    protected View none;
    protected View show;
    protected IRefresh refresh;

    @Deprecated
    public RHolder(View show, View none, IRefresh refresh) {
        if (null == none || null == refresh) {
            throw new IllegalArgumentException("Views Show or None or Refresh Can Not Null !");
        }
        this.none = none;
        this.refresh = refresh;
        this.show = null != show ? show : (View) refresh;
    }

    /**
     * 使用布局视图 自动填充
     *
     * @param layout
     */
    public RHolder(View layout) {
        this.refresh = UIKit.getView(layout, R.id.basis_refresh);
        this.show = UIKit.getView(layout, R.id.basis_show_content);
        //未设置basis_refresh，以layout为跟节点，向下遍历两层视图树查找， 一般RefreshView的位置不会太深
        if (null == refresh) {
            refresh = UIKit.getFirstViewByClass(layout, IRefresh.class, -1);
        }
        if (null == refresh) {
            throw new IllegalArgumentException("Can Not Find Refresh View, Maybe The Id You Set is Not 'basis_refresh',");
        }

        if (null == show) show = (View) refresh;
        initializeNone((ViewGroup) show.getParent());
    }

    public RHolder(IRefresh refresh) {
        if (null == refresh) {
            throw new IllegalArgumentException("Views Show or None or Refresh Can Not Null !");
        }
        this.refresh = refresh;
        this.show = (View) refresh;
        initializeNone((ViewGroup) show.getParent());
    }

    @Override
    public IRefresh getRefresh() {
        return refresh;
    }

    @Override
    public View getNone() {
        return none;
    }

    @Override
    public View getShow() {
        return show;
    }

    @Override
    public final void showType(Type type) {
        reset();
        Logger.e(TAG, "showType: type = " + type);
        (Type.show == type ? show : none).setVisibility(View.VISIBLE);
    }

    /**
     * 动态填充并添加到parent中
     *
     * @param parent show的父控件
     */
    private void initializeNone(ViewGroup parent) {
        this.none = UIKit.inflate(onSetNoneLayoutId());
        Logger.e(TAG, "none/show parent:" + parent.getClass().getSimpleName());
        parent.addView(none, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    public int onSetNoneLayoutId() {
        return R.layout.basis_none;
    }

    public void reset() {
        show.setVisibility(View.GONE);
        none.setVisibility(View.GONE);
    }
}