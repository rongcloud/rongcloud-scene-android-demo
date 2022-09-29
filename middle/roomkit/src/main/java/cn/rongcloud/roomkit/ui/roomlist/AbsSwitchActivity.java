/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.ui.roomlist;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.basis.ui.BaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;

import cn.rongcloud.roomkit.R;
import io.rong.imkit.utils.StatusBarUtil;

public class AbsSwitchActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public int setLayoutId() {
        return R.layout.activity_abs_switch;
    }

    @Override
    public void init() {
        initView();
    }

    private ViewPager2 vp_switch;
    private TabLayout tab_switch;
    private int currentIndex = 0;

    protected void initView() {
        StatusBarUtil.setStatusBarFontIconDark(this, StatusBarUtil.TYPE_M, true);
        vp_switch = findViewById(R.id.vp_switch);

        tab_switch = findViewById(R.id.tab_switch);

        vp_switch.setCurrentItem(currentIndex);
        vp_switch.setAdapter(new VPAdapter(AbsSwitchActivity.this, Arrays.asList(onCreateLeftFragment(), onCreateRightFragment())));

        new TabLayoutMediator(tab_switch, vp_switch, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(onSetSwitchTitle()[position]);
            }
        }).attach();

        getView(R.id.fl_back).setOnClickListener(this);

        getWrapBar().setHide(true).work();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.fl_back == id) {
            onBackCode();
        }
    }

    public String[] onSetSwitchTitle() {
        return new String[]{"房间", "好友"};
    }

    public Fragment onCreateLeftFragment() {
        return null;
    }

    public Fragment onCreateRightFragment() {
        return null;
    }

}














