package cn.rc.demo.check;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.basis.utils.DateFt;
import com.basis.utils.DateUtil;
import com.basis.utils.ResUtil;
import com.basis.utils.SharedPreferUtil;
import com.basis.widget.VRCenterDialog;

import java.lang.ref.WeakReference;
import java.util.Date;

import cn.rc.demo.R;


public class TIPHelper {
    private final static String TAG = "TIPHelper";
    private final static String TIP_DATE = "tip_date";

    public static void showTipDialog(Activity activity) {
        String last = SharedPreferUtil.get(TIP_DATE);
        String current = DateUtil.date2String(new Date(), DateFt.yMd);
        if (!TextUtils.equals(last, current)) {
            SharedPreferUtil.set(TIP_DATE, current);
            showTipDialog(new WeakReference<>(activity));
        }
    }

    static void showTipDialog(WeakReference<Activity> weakReference) {
        if (null == weakReference || null == weakReference.get()) return;
        VRCenterDialog centerDialog = new VRCenterDialog(weakReference.get(), null);
        TextView view = new TextView(weakReference.get());
        view.setTextColor(ResUtil.getColor(R.color.basis_color_secondary));
        view.setText("您正在使用融云 RTC，融云提醒您谨防诈骗，不要轻信涉钱信息。");
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
            }
        };
        String title = "重要提示";
        centerDialog.replaceContent(title, -1, "", -1, null, "确定", R.color.basis_color_secondary, ok, view);
        centerDialog.show();
    }
}
