package com.basis.adapter.interfaces;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;

/**
 * 通用holder接口
 */
public interface IHolder<T extends IHolder> {

    /**
     * 获取view
     *
     * @param viewId
     * @param <V>
     * @return view
     */
    <V extends View> V getView(int viewId);

    /**
     * 获取itemView
     */
    View rootView();

    /************************以下为辅助方法*********************/

    /**
     * 设置透明度
     */
    T setAlpha(int viewId, float value);

    /**
     * 设置visiable
     */
    T setVisible(int viewId, boolean visible);

    /**
     * 设置Tag
     */
    T setTag(int viewId, Object tag);

    T setChecked(int viewId, boolean checked);

    T setSelected(int viewId, boolean selected);

    /**
     * 设置text
     */
    T setText(int viewId, CharSequence text);

    /**
     * 设置textColor
     */
    T setTextColor(int viewId, int color);

    /** 加载image */
//    T loadUrl(int viewId, String url, int def);

    /**
     * 设置ImageDrawable
     */
    T setImageDrawable(int viewId, Drawable drawable);

    /**
     * 设置ImageResource
     */
    T setImageResource(int viewId, @DrawableRes int resource);

    /**
     * 设置ImageBitmap
     */
    T setImageBitmap(int viewId, Bitmap bitmap);

    /**
     * 设置BackgroundColor
     */
    T setBackgroundColor(int viewId, int color);

    /**
     * 设置BackgroundResource
     */
    T setBackgroundResource(int viewId, int resource);

    T setOnClickListener(int viewId, View.OnClickListener ol);

    T setOnLongClickListener(int viewId, View.OnLongClickListener longClickListener);
}
