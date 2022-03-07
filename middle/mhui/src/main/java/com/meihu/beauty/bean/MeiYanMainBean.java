package com.meihu.beauty.bean;

import android.view.View;
import android.widget.LinearLayout;

import com.meihu.beautylibrary.bean.MHCommonBean;

public class MeiYanMainBean extends MHCommonBean {

    private View mView;
    private LinearLayout.LayoutParams mLayoutParams;

    public MeiYanMainBean(View view, LinearLayout.LayoutParams layoutParams, String key) {
        mView = view;
        mLayoutParams = layoutParams;
        mKey = key;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public LinearLayout.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public void setLayoutParams(LinearLayout.LayoutParams layoutParams) {
        mLayoutParams = layoutParams;
    }

}
