package cn.rc.demo;

import android.app.Application;
import android.text.TextUtils;

import com.basis.utils.Logger;
import com.basis.utils.SystemUtil;
import com.meihu.beautylibrary.MHSDK;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.crashreport.CrashReport;

import cn.rc.community.CommunityModule;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.init.ModuleManager;
import cn.rongcloud.config.router.ARouterWrapper;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.gameroom.GameInit;
import cn.rongcloud.music.MusicInit;
import cn.rongcloud.pk.PKInit;
import cn.rongcloud.roomkit.RoomKitInit;
import io.rong.imlib.RongCoreClient;

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
                BuildConfig.BASE_SERVER_ADDRES,
                BuildConfig.BUSINESS_TOKEN,
                WalleChannelReader.getChannel(this),
                BuildConfig.INTERIAL,
                BuildConfig.RC_BUSI
        );
        Logger.e("渠道：" + WalleChannelReader.getChannel(this));
        MHSDK.init(this, BuildConfig.MH_APP_KEY);
        // init rong
        ModuleManager.manager().register(new RoomKitInit(), new MusicInit(), new PKInit(), new CommunityModule(), new GameInit());
        //初始化 bugly
        CrashReport.initCrashReport(this, BuildConfig.BUGLY_ID, BuildConfig.DEBUG);

        initSensorsData();

    }

    /**
     * 神策埋点
     */
    private void initSensorsData() {
        SensorsUtil.instance().init(this, BuildConfig.VERSION_NAME, BuildConfig.SENSORS_URL);
        //初始化公共属性
        String currentUserId = RongCoreClient.getInstance().getCurrentUserId();
        SensorsUtil.instance().registerSuperProperties(!TextUtils.isEmpty(currentUserId));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.stopLoop();
        ARouterWrapper.destory();
        ModuleManager.manager().unregister();
    }
}
