package cn.rongcloud.config.router;

import android.app.Application;

import com.alibaba.android.arouter.BuildConfig;
import com.alibaba.android.arouter.launcher.ARouter;

public class ARouterWrapper {

    public static void init(Application application) {
        if (true) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(application);
    }

    public static void destory(){
        ARouter.getInstance().destroy();
    }

    public static void jump(String path){

    }

}
