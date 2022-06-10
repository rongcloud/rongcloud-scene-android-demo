package cn.rc.community.message;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.ui.BaseFragment;
import com.basis.wapper.IResultBack;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;

import cn.rc.community.R;


public abstract class AbsMessageFragment<T> extends BaseFragment {

    private RecyclerView ryMessage;
    private IAdapte<T, RcyHolder> adapter;
    protected SmartRefreshLayout refreshLayout;
    public View emptyView;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_abs_child_message;
    }

    @Override
    public void init() {
        ryMessage = getView(R.id.rc_message);
        refreshLayout = getView(R.id.refresh);
        emptyView = getView(R.id.tv_empty);
        adapter = onSetAdapter();
        if (null != adapter) {
            adapter.setRefreshView(ryMessage);
        }
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData(false, true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshData(false, false);
            }
        });
        refreshData(true, false);
    }

    public IAdapte<T, RcyHolder> getAdapter() {
        return adapter;
    }

    private void refreshData(boolean wait, boolean refresh) {
        onRefreshData(wait, refresh, new MessageResultBack<>(refresh, refreshLayout, adapter));
    }

    /**
     * 移除
     *
     * @param t
     */
    public void removeItem(T t) {
        if (adapter != null) adapter.removeItem(t);
    }


    public abstract IAdapte<T, RcyHolder> onSetAdapter();

    public abstract void onRefreshData(boolean wait, boolean refresh, MessageResultBack<T> resultBack);

    public static class MessageResultBack<T> implements IResultBack<List<T>> {
        private boolean refresh;
        private SmartRefreshLayout layout;
        private IAdapte<T, RcyHolder> adapte;

        MessageResultBack(boolean refresh, SmartRefreshLayout layout, IAdapte<T, RcyHolder> adapte) {
            this.refresh = refresh;
            this.adapte = adapte;
            this.layout = layout;
            if (refresh) {
                this.adapte.clear();
            }
        }

        @Override
        public void onResult(List<T> ts) {
            if (null != layout) {
                layout.finishRefresh();
                layout.finishLoadMore();
            }
            if (null != ts && null != adapte) {
                adapte.setData(ts, refresh);
            }
        }


        public void addData(T t, boolean isLast) {
            if (null != layout) {
                layout.finishRefresh();
                layout.finishLoadMore();
            }
            if (null != t && null != adapte) {
                adapte.insertItem(t, isLast);
            }
        }
    }
}
