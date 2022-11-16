package cn.rc.community;

import com.basis.adapter.RcyHolder;

public interface OnConvertListener<T> {
    void onConvert(RcyHolder holder, T t, int position);
}