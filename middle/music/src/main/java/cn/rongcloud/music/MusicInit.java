package cn.rongcloud.music;

import android.app.Application;

import com.basis.utils.UIKit;
import com.hfopen.sdk.manager.HFOpenApi;

import cn.rongcloud.config.init.IModule;
import cn.rongcloud.corekit.api.RCSceneKitEngine;

/**
 * @author gyn
 * @date 2022/2/16
 */
public class MusicInit implements IModule {
    @Override
    public void onInit() {
        // 初始化kit
        RCSceneKitEngine.getInstance().initWithAppKey(UIKit.getContext(), "");
        // 初始化HiFive
        HFOpenApi.registerApp((Application) UIKit.getContext(), "");
        HFOpenApi.setVersion("V4.1.2");
    }

    @Override
    public void onUnInit() {

    }
}
