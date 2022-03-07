package com.meihu.beauty.interfaces;

public interface IBeautyEffectListener {
    void onMeiYanChanged(int meiBai, boolean meiBaiChanged, int moPi, boolean moPiChanged, int hongRun, boolean hongRunChanged);

    void onFilterChanged(int filterName);

    boolean isFilterEnable();

    boolean isTieZhiEnable();
}
