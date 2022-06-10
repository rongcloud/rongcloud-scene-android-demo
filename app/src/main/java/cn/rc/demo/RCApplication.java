package cn.rc.demo;

import android.app.Application;
import android.text.TextUtils;

import com.basis.utils.Logger;
import com.basis.utils.SystemUtil;
import com.meihu.beautylibrary.MHSDK;
import com.tencent.bugly.crashreport.CrashReport;

import cn.rc.community.CommunityModule;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.init.ModuleManager;
import cn.rongcloud.config.router.ARouterWrapper;
import cn.rongcloud.music.MusicInit;
import cn.rongcloud.pk.PKInit;
import cn.rongcloud.roomkit.RoomKitInit;

public class RCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String process = SystemUtil.getProcessName();
        // 过滤非主进程
        if (!TextUtils.equals(process, getPackageName())) {
            return;
        }
        initConfig();
        Logger.startLoop();
    }

    void initConfig() {
        ARouterWrapper.init(this);
        AppConfig.get().init(
                BuildConfig.APP_KEY,
                BuildConfig.UM_APP_KEY,
                "rcrtc",
                BuildConfig.BASE_SERVER_ADDRES,
                BuildConfig.BUSINESS_TOKEN,
                BuildConfig.INTERIAL,
                BuildConfig.RC_BUSI
        );

        MHSDK.init(this, BuildConfig.MH_APP_KEY);
        // init rong
        ModuleManager.manager().register(new RoomKitInit(), new MusicInit(), new PKInit(), new CommunityModule());
        //初始化 bugly
        CrashReport.initCrashReport(this, BuildConfig.BUGLY_ID, BuildConfig.DEBUG);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.stopLoop();
        ARouterWrapper.destory();
        ModuleManager.manager().unregister();
    }
}
