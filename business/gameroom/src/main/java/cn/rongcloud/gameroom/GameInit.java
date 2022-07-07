package cn.rongcloud.gameroom;

import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.Locale;

import cn.rongcloud.config.init.IModule;
import cn.rongcloud.gamelib.api.RCGameEngine;
import cn.rongcloud.gamelib.callback.RCGameCallback;
import cn.rongcloud.gamelib.error.RCGameError;
import cn.rongcloud.gamelib.model.RCGameConfig;

/**
 * @author gyn
 * @date 2022/5/6
 */
public class GameInit implements IModule {
    private static final String TAG = GameInit.class.getSimpleName();
    // 游戏appId和appKey
    private String appId = "1496435759618818049";
    private String appKey = "YS7NZ6rUAnbi0DruJJiUCmcH1AkCrQk6";

    @Override
    public void onInit() {
        RCGameConfig gameConfig = RCGameConfig.builder().setGameLanguage(Locale.CHINA).setDebug(BuildConfig.DEBUG).build();
        RCGameEngine.getInstance().init(UIKit.getContext(), appId, appKey, gameConfig, new RCGameCallback() {
            @Override
            public void onSuccess() {
                Logger.d(TAG, "初始化游戏引擎成功");
            }

            @Override
            public void onError(int i, RCGameError rcGameError) {
                Logger.d(TAG, "初始化游戏引擎失败：" + rcGameError.getMessage());
            }
        });
    }

    @Override
    public void onUnInit() {

    }
}
