//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.basis.imkit;

import android.content.Context;

public class RongConfigCenter {
    private static final String TAG = RongConfigCenter.class.getSimpleName();
    private static FeatureConfig sFeatureConfig = new FeatureConfig();

    public RongConfigCenter() {
    }

    public static void syncFromXml(Context context) {
        sFeatureConfig.initConfig(context);
    }

    public static FeatureConfig featureConfig() {
        return sFeatureConfig;
    }


}
