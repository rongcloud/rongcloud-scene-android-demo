package cn.rc.community.home;

import android.content.Context;

import androidx.annotation.LayoutRes;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;

import cn.rc.community.OnConvertListener;

/**
 * 1.分组下频道的适配器
 * 2.和根节点下的频道ui一致，因此布局有group同级的channel的类型的item确定
 */
public class ChannelUnderGroupAdapter<T> extends RcySAdapter<T, RcyHolder> {
    private OnConvertListener<T> convert;

    public ChannelUnderGroupAdapter(Context context, @LayoutRes int layoutId, OnConvertListener<T> listener) {
        super(context, layoutId);
        this.convert = listener;
    }

    @Override
    public void convert(RcyHolder holder, T item, int position) {
        if (null != convert) convert.onConvert(holder, item, position);
    }
}
