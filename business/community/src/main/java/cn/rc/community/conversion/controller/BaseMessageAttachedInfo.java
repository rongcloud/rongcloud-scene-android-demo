package cn.rc.community.conversion.controller;

import android.text.TextUtils;

import com.basis.adapter.RcyHolder;

import cn.rc.community.OnConvertListener;
import cn.rc.community.conversion.controller.interfaces.IManager;

/**
 * 消息绑定信息的封装基类
 */
public abstract class BaseMessageAttachedInfo extends IManager.AttachedInfo implements OnConvertListener<WrapperMessage> {
    @Override
    public OnConvertListener<WrapperMessage> onSetConvertListener() {
        return this;
    }

    @Override
    public boolean checked() {
        return !TextUtils.isEmpty(onSetObjectName());
    }

    @Override
    public abstract void onConvert(RcyHolder holder, WrapperMessage message, int position);

    @Override
    public abstract int onSetLayout(WrapperMessage message);

    public abstract String onSetObjectName();
}