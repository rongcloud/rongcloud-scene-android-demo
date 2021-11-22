package cn.rong.combusis.sdk.event.wrapper;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kit.UIKit;

public class EToast {

    public static void showToast(String info) {
        UIKit.runOnUiTherad(() -> Toast.makeText(UIKit.getContext(), info, Toast.LENGTH_SHORT).show());
    }

    public static void showToastWithLag(String withTag, String info) {
        showToast(info);
        String tag = TextUtils.isEmpty(withTag) ? "KToastTag" : withTag;
        Log.e(tag, info);
    }
}
