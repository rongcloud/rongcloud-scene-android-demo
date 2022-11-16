package com.basis.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.interfaces.IHolder;

/**
 * @author: BaiCQ
 * @createTime: 2017/2/26 15:08
 * @className: ViewHolder
 * @Description: 通用ViewHolder
 */
public class RcyHolder extends RecyclerView.ViewHolder implements IHolder<RcyHolder> {
    private SparseArray<View> viewArrays;

    public RcyHolder(View rootView) {
        super(rootView);
        viewArrays = new SparseArray();
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    @Override
    public <T extends View> T getView(int viewId) {
        View view = viewArrays.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewArrays.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public View rootView() {
        return itemView;
    }

    @SuppressLint("NewApi")
    @Override
    public RcyHolder setAlpha(int viewId, float value) {
        View view = getView(viewId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (null != view) view.setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            if (null != view) view.startAnimation(alpha);
        }
        return this;
    }

    @Override
    public RcyHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (null != view) view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public RcyHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        if (null != view) view.setTag(tag);
        return this;
    }

    @Override
    public RcyHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        if (null != view) view.setChecked(checked);
        return this;
    }

    @Override
    public RcyHolder setSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        if (null != view) view.setSelected(selected);
        return this;
    }

    @Override
    public RcyHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (text == null) text = "";
        if (null != tv) tv.setText(text);
        return this;
    }

    @Override
    public RcyHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        if (null != view) view.setTextColor(color);
        return this;
    }

    @Override
    public RcyHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (null != view) view.setImageBitmap(bitmap);
        return this;
    }

    @Override
    public RcyHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (null != view) view.setImageDrawable(drawable);
        return this;
    }

    @Override
    public RcyHolder setImageResource(int viewId, @DrawableRes int resource) {
        ImageView view = getView(viewId);
        if (null != view) view.setImageResource(resource);
        return this;
    }

    @Override
    public RcyHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        if (null != view) view.setBackgroundColor(color);
        return this;
    }

    @Override
    public RcyHolder setBackgroundResource(int viewId, int resource) {
        View view = getView(viewId);
        if (null != view) view.setBackgroundResource(resource);
        return this;
    }

    @Override
    public RcyHolder setOnClickListener(int viewId, View.OnClickListener ol) {
        View view = getView(viewId);
        if (null != view) view.setOnClickListener(ol);
        return this;
    }

    @Override
    public RcyHolder setOnLongClickListener(int viewId, View.OnLongClickListener longClickListener) {
        View view = getView(viewId);
        if (null != view) view.setOnLongClickListener(longClickListener);
        return null;
    }
}
