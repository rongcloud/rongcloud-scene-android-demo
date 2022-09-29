package cn.rongcloud.beauty.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.math.BigDecimal;
import java.util.List;

import cn.rongcloud.beauty.R;
import cn.rongcloud.beauty.dialog.BaseDialogFragment;
import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;
import cn.rongcloud.beauty.listener.BeautyDataSourceListener;
import cn.rongcloud.beauty.seekbar.DiscreteSeekBar;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class BeautyMainFragment extends BaseDialogFragment implements BeautyFragment.OnBeautyClickListener {
    private BeautyDataSourceListener beautyDataSourceListener;
    private ImageView mIvEnable;
    private DiscreteSeekBar mSeekBar;
    private ViewPager2 mVpBeauty;
    private TabLayout mTlTab;
    private List<BeautyCategory> categoryList;
    private List<BeautyCategory> defaultCategoryList;
    private BeautyFragment currentBeautyFragment;
    private BeautyBean currentBeauty;

    public static BeautyMainFragment getInstance(BeautyDataSourceListener beautyDataSourceListener) {
        return new BeautyMainFragment(beautyDataSourceListener);
    }

    public BeautyMainFragment(BeautyDataSourceListener beautyDataSourceListener) {
        this.beautyDataSourceListener = beautyDataSourceListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(R.layout.beauty_view_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    protected int setContentGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int setWindowAnimations() {
        return R.style.Basis_Style_Bottom_Menu_Anim;
    }

    private void initView(View view) {
        mIvEnable = (ImageView) view.findViewById(R.id.iv_enable);
        mSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seek_bar);
        mVpBeauty = (ViewPager2) view.findViewById(R.id.vp_beauty);
        mTlTab = (TabLayout) view.findViewById(R.id.tl_tab);
        mVpBeauty.setSaveEnabled(false);

        if (beautyDataSourceListener != null) {
            categoryList = beautyDataSourceListener.getCurrentBeautyData();
            defaultCategoryList = beautyDataSourceListener.getDefaultBeautyData();
        }

        if (categoryList != null && categoryList.size() > 0) {
            BeautyFragmentAdapter adapter = new BeautyFragmentAdapter(this, categoryList, defaultCategoryList);
            mVpBeauty.setAdapter(adapter);
            mVpBeauty.setUserInputEnabled(false);
            new TabLayoutMediator(mTlTab, mVpBeauty, true, false, new TabLayoutMediator.TabConfigurationStrategy() {
                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(categoryList.get(position).getName());
                }
            }).attach();
            mVpBeauty.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    Log.d("========", "onPageSelected");
                    findCurrentFragment();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
        }

        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (beautyDataSourceListener != null && currentBeauty != null) {
                    float percent = 1.0f * (value - mSeekBar.getMin()) / (mSeekBar.getMax() - mSeekBar.getMin());
                    float intensity = currentBeauty.getMin() + (currentBeauty.getMax() - currentBeauty.getMin()) * percent;
                    boolean isOpenChanged = currentBeauty.setIntensity(intensity);
                    if (isOpenChanged) {
                        currentBeautyFragment.notifyBeautyItem();
                    }
                    currentBeautyFragment.checkRecoverEnable();
                    beautyDataSourceListener.onSetBeauty(currentBeauty);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        mIvEnable.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if (beautyDataSourceListener != null) {
                    beautyDataSourceListener.onEnableBeauty(false);
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (beautyDataSourceListener != null) {
                    beautyDataSourceListener.onEnableBeauty(true);
                }
            }
            return true;
        });
    }

    // @Override
    // public void onDestroyView() {
    //     if (handler != null) {
    //         handler.removeMessages(0);
    //         handler = null;
    //     }
    //     super.onDestroyView();
    // }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            findCurrentFragment();
            return false;
        }
    });

    private void findCurrentFragment() {
        Log.d("========", "fffffffff");
        Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + mVpBeauty.getCurrentItem());
        if (fragment == null) {
            if (handler != null) {
                handler.sendEmptyMessageDelayed(0, 300);
            }
        } else {
            currentBeautyFragment = (BeautyFragment) fragment;
            currentBeautyFragment.setOnBeautyClickListener(BeautyMainFragment.this);
        }
    }

    @Override
    public void onClickBeauty(BeautyBean beautyBean) {
        currentBeauty = beautyBean;
        mSeekBar.setVisibility(beautyBean.hasIntensity() ? View.VISIBLE : View.GONE);
        if (beautyBean.hasIntensity()) {
            // 如果是双向调节,都默认成 -50 ~ 50
            if (beautyBean.isDoubleDirection()) {
                mSeekBar.setMax(50);
                mSeekBar.setMin(-50);
            } else {// 否则默认成 0 ~ 100
                mSeekBar.setMax(100);
                mSeekBar.setMin(0);
            }
            float percent = (beautyBean.getIntensity() - beautyBean.getMin()) / (beautyBean.getMax() - beautyBean.getMin());
            int current = new BigDecimal(((float) (mSeekBar.getMax() - mSeekBar.getMin()) * percent)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + mSeekBar.getMin();

            mSeekBar.setProgress(current);
        } else {
            if (beautyDataSourceListener != null && currentBeauty != null) {
                beautyDataSourceListener.onSetBeauty(currentBeauty);
            }
        }
    }

    @Override
    public void onRecoveryBeauty(List<BeautyBean> beautyBeanList) {
        if (beautyDataSourceListener != null) {
            for (BeautyBean beautyBean : beautyBeanList) {
                if (TextUtils.equals(currentBeauty.getKey(), beautyBean.getKey()) && currentBeauty.getIntensity() != beautyBean.getIntensity()) {
                    currentBeauty.setIntensity(beautyBean.getIntensity());
                    onClickBeauty(currentBeauty);
                }
                beautyDataSourceListener.onSetBeauty(beautyBean);
            }
        }
    }

    @Override
    protected int getDialogHeight() {
        return super.getDialogHeight();
    }

    @Override
    protected int getDialogWidth() {
        return getScreenWidth();
    }
}
