package com.basis.widget.interfaces;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * ActionBar 包装类
 */
public interface IWrapBar<T extends IWrapBar> {
    int DEFAULT_ELEVATION = 12;

    T setHide(boolean hide);

    T setBackHide(boolean hide);

    T setTitle(@StringRes int title);

    T setElevation(float elevation);

    T setTitle(String title);

    T setTitleAndGravity(String title, int gravity);

    T setBackIcon(@DrawableRes int res);

    T setOnMenuSelectedListener(OnMenuSelectedListener listener);

    T addOptionMenu(String title);

    T addOptionMenu(String title, @DrawableRes int icon);

    T work();

    boolean onCreateOptionsMenu(@NonNull Menu menu);

    boolean onOptionsItemSelected(MenuItem item);

    interface OnMenuSelectedListener {
        void onItemSelected(int position);
    }

    class OpMenu {
        private String title;
        private int icon;
        private int index = 0;

        public OpMenu(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public int getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
