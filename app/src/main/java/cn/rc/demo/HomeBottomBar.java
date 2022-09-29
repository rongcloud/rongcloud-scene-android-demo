package cn.rc.demo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class HomeBottomBar implements Cloneable {
    protected String title;
    protected @DrawableRes
    int icon;
    protected String router;
    protected boolean hasRedPoint;
    protected boolean selected;

    public HomeBottomBar(String title, int icon, String router) {
        this(title, icon, router, false, false);
    }

    public HomeBottomBar(String title, int icon, String router, boolean hasRedPoint, boolean selected) {
        this.title = title;
        this.icon = icon;
        this.router = router;
        this.hasRedPoint = hasRedPoint;
        this.selected = selected;
    }

    @NonNull
    @Override
    protected HomeBottomBar clone() {
        try {
            return (HomeBottomBar) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}