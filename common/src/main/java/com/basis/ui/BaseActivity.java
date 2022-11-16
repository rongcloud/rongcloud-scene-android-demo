package com.basis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.basis.utils.Logger;
import com.basis.utils.NetUtil;
import com.basis.widget.ActionWrapBar;
import com.basis.widget.dialog.DialogBuilder;
import com.basis.widget.dialog.IDialog;
import com.basis.widget.interfaces.IWrapBar;

import java.util.List;

/**
 * @author: BaiCQ
 * @ClassName: BaseActivity
 * @date: 2018/8/17
 * @Description: 基于IBasis实现的UI组件基类
 * 1.统一封装ActionBar
 * 2.针对常用的api的封装，如：getView()
 * 3.针对finish()相关的统一封装onBackCode()
 */
public abstract class BaseActivity extends AppCompatActivity implements IBasis {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity activity;
    private View layout;
    private IWrapBar wrapBar;
    private IDialog networkDialog;

    @Override
    protected void onDestroy() {
        UIStack.getInstance().remove(activity);
        super.onDestroy();
    }

    @Override
    @Deprecated
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        UIStack.getInstance().add(activity);
        layout = View.inflate(this, setLayoutId(), null);
        setContentView(layout);
        //init wapp
        wrapBar = new ActionWrapBar(activity).work();
        init();
    }

    public abstract int setLayoutId();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return wrapBar.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackCode();
            return true;
        } else {
            return wrapBar.onOptionsItemSelected(item);
        }
    }

    public IWrapBar getWrapBar() {
        return wrapBar;
    }

    @Override
    public abstract void init();

    public View getLayout() {
        return layout;
    }

    protected <T extends View> T getView(@IdRes int id) {
        return layout.findViewById(id);
    }

    @Override
    public void onRefresh(ICmd obj) {
    }

    @Override
    public void onNetChange() {
        if (!NetUtil.isNetworkAvailable()) {
            if (networkDialog == null) {
                networkDialog = new DialogBuilder(activity)
                        .setMessage("网络异常进行检查")
                        .setEnableTitle(true)
                        .defaultsStyle(null)
                        .build();
                networkDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        onBackCode();
    }

    /**
     * 统一处理activity 返回事件
     */
    public void onBackCode() {
        finish();
    }

    @Override
    public void onLogout() {
        finish();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (!checkFastClick(intent)) {
            super.startActivityForResult(intent, requestCode);
        }
    }

    private String lastTag;
    private long lastClickTime;

    /**
     * 检查是否重复跳转，不需要则重写方法并返回true
     */
    protected boolean checkFastClick(Intent intent) {
        boolean fastClick = false;
        String tag = "";
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        } else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        }
        long dv = System.currentTimeMillis() - lastClickTime;
        if (!TextUtils.isEmpty(tag) && tag.equals(lastTag) && dv <= 500) {
            fastClick = true;
        }
        if (!fastClick) {
            lastTag = tag;// 记录启动标记和时间
            lastClickTime = System.currentTimeMillis();
        }
        Logger.e(TAG, "fastClick = " + fastClick + " dv = " + dv);
        return fastClick;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fs = getSupportFragmentManager().getFragments();
        for (Fragment f : fs) {
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fs = getSupportFragmentManager().getFragments();
        for (Fragment f : fs) {
            f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}