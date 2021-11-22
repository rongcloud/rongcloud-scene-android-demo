package com.basis;

import com.basis.net.DefauPage;
import com.basis.net.oklib.net.Page;

/**
 * 以下资源配置如下修改 覆盖即可
 * sgv 图片的默认color
 * <color name="basis_color_svg">#8A96A3</color>
 * baisi them的默认配置
 * <color name="basis_color_bar_bg">#008000</color>
 * <color name="basis_color_status_bg">#bb008000</color>
 * 标题栏高度，默认 56dp
 * <dimen name="basis_bar_size">@dimen/abc_action_bar_default_height_material</dimen>
 * 标题 text size 默认：17sp
 * <dimen name="basis_bar_text_size">17sp</dimen>
 * 标题栏文字包括menu颜色 默认：白色
 * <color name="basis_bar_text_color">@color/white</color>
 * action text size 默认：14sp
 * <dimen name="basis_menu_text_size">14sp</dimen>
 */
public class BasisHelper {
    private static Page defaultPage;

    //设置全局分页信息
    public static void setDefaultPage(Page defaultPage) {
        BasisHelper.defaultPage = defaultPage;
    }

    public static Page getPage() {
        if (null == defaultPage) {
            defaultPage = new DefauPage();
        }
        return defaultPage;
    }

    public static void setCustomerReceiver(String[] cusActions, CustomerReceiver receiver) {
        UIStack.getInstance().setCustomerReceiver(cusActions, receiver);
    }
}
