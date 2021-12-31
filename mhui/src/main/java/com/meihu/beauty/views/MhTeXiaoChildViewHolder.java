package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import com.meihu.beauty.R;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionDownloadListener;
import com.meihu.beauty.interfaces.OnTieZhiActionListener;

public abstract class MhTeXiaoChildViewHolder extends AbsCommonViewHolder {

    protected OnTieZhiActionClickListener mOnTieZhiActionClickListener;
    protected OnTieZhiActionListener mOnTieZhiActionListener;
    protected OnTieZhiActionDownloadListener mOnTieZhiActionDownloadListener;


    public MhTeXiaoChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_meiyan_child;
    }


    public void setOnTieZhiActionClickListener(OnTieZhiActionClickListener onTieZhiActionClickListener) {
        mOnTieZhiActionClickListener = onTieZhiActionClickListener;
    }

    public void setOnTieZhiActionListener(OnTieZhiActionListener onTieZhiActionListener) {
        mOnTieZhiActionListener = onTieZhiActionListener;
    }

    public void setOnTieZhiActionDownloadListener(OnTieZhiActionDownloadListener onTieZhiActionDownloadListener) {
        mOnTieZhiActionDownloadListener = onTieZhiActionDownloadListener;
    }

}
