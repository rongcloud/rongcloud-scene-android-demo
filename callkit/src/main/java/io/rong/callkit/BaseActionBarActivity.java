/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.rong.imkit.utils.language.RongConfigurationManager;

public class BaseActionBarActivity extends AppCompatActivity {

    private CompositeDisposable  compositeDisposable = new CompositeDisposable();

    @Override
    protected void attachBaseContext(Context newBase) {
        Context newContext =
                RongConfigurationManager.getInstance().getConfigurationContext(newBase);
        super.attachBaseContext(newContext);
    }

    protected void initDefalutActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setElevation(0f);
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_dialer_light)));
        actionBar.setDisplayHomeAsUpEnabled(true);//显示返回
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(18);
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

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    public void addDisposable(Disposable ... disposables){
        compositeDisposable.addAll(disposables);
    }
}
