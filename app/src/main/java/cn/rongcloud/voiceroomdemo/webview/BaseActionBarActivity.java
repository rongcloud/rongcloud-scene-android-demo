/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.webview;

import android.graphics.Color;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.rongcloud.common.base.BaseActivity;

public abstract class BaseActionBarActivity extends BaseActivity {

    protected void initDefalutActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setElevation(0f);
        actionBar.setDisplayHomeAsUpEnabled(true);//显示返回
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(17);
        textView.setTextColor(Color.parseColor("#333333"));
        LinearLayout actionbarLayout = new LinearLayout(this);
        actionBar.setCustomView(actionbarLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams mP = (ActionBar.LayoutParams) actionbarLayout.getLayoutParams();
        mP.gravity = mP.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK | Gravity.CENTER_HORIZONTAL;
        actionbarLayout.addView(textView);
        actionBar.setCustomView(actionbarLayout, mP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
