package com.meihu.beauty.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meihu.beauty.R;
import com.meihu.beauty.adapter.TieZhiAdapter;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;

import java.util.List;

public class MhTieZhiChildViewHolder extends AbsCommonViewHolder implements OnItemClickListener<TieZhiBean> {

    private int mId;
    private String mKey;
    private String mUrl;
    private RecyclerView mRecyclerView;
    private TieZhiAdapter mAdapter;
    private ActionListener mActionListener;

    public MhTieZhiChildViewHolder(Context context, ViewGroup parentView, int id, String key, String url) {
        super(context, parentView, id, key, url);
    }

    @Override
    protected void processArguments(Object... args) {
        mId = (int) args[0];
        mKey = (String) args[1];
        mUrl = (String) args[2];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_tiezhi_child;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) mContentView;
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public void loadData() {
        if (!mFirstLoadData) {
            return;
        }
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        MhDataManager.getTieZhiList(mUrl, new CommonCallback<String>() {
            @Override
            public void callback(String jsonStr) {
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    JSONObject obj = JSON.parseObject(jsonStr);
                    List<TieZhiBean> list = JSON.parseArray(obj.getString("list"), TieZhiBean.class);
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            TieZhiBean bean = list.get(i);
                            if (TextUtils.isEmpty(bean.getName())) {
                                list.remove(bean);
                                i--;
                                continue;
                            } else {
                                //默认选中上次的
                                String mTieZhiName = MhDataManager.getInstance().mTieZhiName;
                                if (!TextUtils.isEmpty(mTieZhiName))
                                    if (mTieZhiName.equals(bean.getName())) {
                                        bean.setChecked(true);
                                    }
                            }
                            bean.checkDownloaded();
                            bean.setKey(mKey);
                        }
                        mAdapter = new TieZhiAdapter(mContext, list);
                        mAdapter.setOnItemClickListener(MhTieZhiChildViewHolder.this);
                        if (mRecyclerView != null) {
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        if (mActionListener != null) {
                            if ("".equals(mActionListener.getCheckedTieZhiName())) {
                                setCheckedPosition(0);
                            }
                        }
                    }
                    mFirstLoadData = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setCheckedPosition(int position) {
        if (mAdapter != null) {
            mAdapter.setCheckedPosition(position);
        }
    }

    @Override
    public void onItemClick(TieZhiBean bean, int position) {
        if (mActionListener != null) {
            mActionListener.onTieZhiChecked(this, bean);
        }
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onTieZhiChecked(MhTieZhiChildViewHolder vh, TieZhiBean bean);

        String getCheckedTieZhiName();
    }
}
