package cn.rongcloud.voiceroomdemo.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.utils.KToast;
import com.kit.utils.ResUtil;
import com.rongcloud.common.net.ApiConstant;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.rong.combusis.VRCenterDialog;
import cn.rong.combusis.common.utils.UIKit;
import cn.rongcloud.voiceroomdemo.BuildConfig;
import cn.rongcloud.voiceroomdemo.R;

public class VersionHelper {
    private final static String TAG = "VersionHelper";
    private final static String HOST = ApiConstant.INSTANCE.getBASE_URL();
    private final static String LAST = HOST + "/appversion/latest";

    public static void checkVersion(Activity activity, boolean tip) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        Map<String, Object> params = new HashMap<>(2);
        params.put("platform", "Android");
        OkApi.get(LAST, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                if (tip) KToast.show("检查版本失败");
            }

            @Override
            public void onResult(Wrapper result) {
                Version version = result.get(Version.class);
                if (null == version) {
                    return;
                }
                if (isUpdate(version.version)) {
                    showUpdateDialog(weakReference, version);
                } else {
                    if (tip) KToast.show("当前已是最新版本");
                }
            }
        });
    }

    static boolean isUpdate(String lastVersionName) {
        try {
            String[] lastArray = lastVersionName.split("\\.");
            String[] currentArray = BuildConfig.VERSION_NAME.split("\\.");
            int size = Math.max(lastArray.length, currentArray.length);
            int l, c = 0;
            for (int i = 0; i < size; i++) {
                try {
                    l = Integer.parseInt(lastArray[i]);
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    l = 0;
                }
                try {
                    c = Integer.parseInt(currentArray[i]);
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    c = 0;
                }
                if (l > c) {
                    return true;
                } else if (l < c) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void showUpdateDialog(WeakReference<Activity> weakReference, Version version) {
        if (null == weakReference || null == weakReference.get()) return;
        VRCenterDialog centerDialog = new VRCenterDialog(weakReference.get(), null);
        TextView view = new TextView(weakReference.get());
        view.setTextColor(ResUtil.getColor(R.color.basis_color_secondary));
        view.setText(version.releaseNote);
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != centerDialog) centerDialog.dismiss();
            }
        };
        View.OnClickListener ok = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != centerDialog) centerDialog.dismiss();
                Uri uri = Uri.parse(version.downloadUrl);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIKit.getContext().startActivity(it);
            }
        };
        String title = "发现新的版本";
        if (!version.forceUpgrade) {
            centerDialog.replaceContent(title, -1, "取消", -1, cancel, "更新", -1, ok, view);
        } else {
            centerDialog.replaceContent(title, -1, "", -1, null, "更新", -1, ok, view);
        }
        centerDialog.show();
    }

    public static class Version {
        private String downloadUrl;
        private boolean forceUpgrade;
        private String platform;
        private String releaseNote;
        private String version;
    }
}
