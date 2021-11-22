/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import io.rong.imkit.utils.language.RongConfigurationManager;

public class BaseNoActionBarActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        Context newContext =
                RongConfigurationManager.getInstance().getConfigurationContext(newBase);
        super.attachBaseContext(newContext);
    }
}
