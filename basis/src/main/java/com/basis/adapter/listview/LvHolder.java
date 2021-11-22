package com.basis.adapter.listview;

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

import com.basis.adapter.interfaces.IHolder;

/**
 * @author: BaiCQ
 * @createTime: 2017/2/26 15:08
 * @className: ViewHolder
 * @Description: 通用ViewHolder
 */
public class LvHolder implements IHolder<LvHolder> {
    private SparseArray<View> mViews;
    private View itemView;

    public LvHolder(View itemView) {
        this.itemView = itemView;
        mViews = new SparseArray();
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    @Override
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public View rootView() {
        return itemView;
    }

    @SuppressLint("NewApi")
    @Override
    public LvHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    @Override
    public LvHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public LvHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    @Override
    public LvHolder setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) getView(viewId);
        view.setChecked(checked);
        return this;
    }

    @Override
    public LvHolder setSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    @Override
    public LvHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    @Override
    public LvHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    @Override
    public LvHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    @Override
    public LvHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    @Override
    public LvHolder setImageResource(int viewId, int resource) {
        ImageView view = getView(viewId);
        view.setImageResource(resource);
        return this;
    }

    @Override
    public LvHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    @Override
    public LvHolder setBackgroundResource(int viewId, int resource) {
        View view = getView(viewId);
        view.setBackgroundResource(resource);
        return this;
    }

    @Override
    public LvHolder setOnClickListener(int viewId, View.OnClickListener ol) {
        View view = getView(viewId);
        view.setOnClickListener(ol);
        return this;
    }
}
