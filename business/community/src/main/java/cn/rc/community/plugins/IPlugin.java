package cn.rc.community.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/11
 * @time 6:08 下午
 * 功能接口
 */
public interface IPlugin {
    Drawable obtainDrawable(Context context);

    String obtainTitle(Context context);

    void onClick(Activity activity, int index);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
