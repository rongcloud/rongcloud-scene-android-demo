package cn.rc.community.conversion;

import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.MessageManager;
import dalvik.system.DexFile;

/**
 * 反射实现自动注册指定包下的AttachedInfo
 * 满足以下条件，可自动注册
 * 1.需将自定义的AttachedInfo继承BaseMessageAttachedInfo
 * 2.cn.rc.community.conversion.convert包下
 */
public class RegisterHelper {
    private static String TAG = "RegisterHelper";
    private static String PATH_COVERT = "cn.rc.community.conversion.convert";

    /**
     * 动态注册convert
     */
    public static void autoRegisterCovert() {
        List<Class<?>> cs = getClasses(PATH_COVERT);
        try {
            for (Class<?> s : cs) {
                Logger.e(TAG, "s = " + s.getSimpleName());
                Object obj = s.newInstance();
                if (obj instanceof BaseMessageAttachedInfo) {
                    BaseMessageAttachedInfo info = (BaseMessageAttachedInfo) obj;
                    MessageManager.registerAttachInfo(info);
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> getClasses(String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        try {
            String packageCodePath = UIKit.getContext().getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            String regExp = "^" + packageName + ".\\w+$";
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                if (className.matches(regExp)) {
                    classes.add(Class.forName(className));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }
}
