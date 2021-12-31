package com.meihu.beauty.interfaces;

public interface IBeautyViewHolder {

    void show();

    void hide();

    boolean isShowed();

    void setVisibleListener(VisibleListener visibleListener);


    interface VisibleListener {
        void onVisibleChanged(boolean visible);
    }

}
