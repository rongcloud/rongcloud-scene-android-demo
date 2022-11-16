package cn.rongcloud.beauty.ui.sticker;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import cn.rongcloud.beauty.R;
import cn.rongcloud.beauty.dialog.BaseDialogFragment;
import cn.rongcloud.beauty.entity.StickerBean;
import cn.rongcloud.beauty.entity.StickerCategory;
import cn.rongcloud.beauty.listener.DataCallback;
import cn.rongcloud.beauty.listener.StickerDataSourceListener;

/**
 * @author gyn
 * @date 2022/10/14
 */
public class StickerMainFragment extends BaseDialogFragment implements StickerFragment.OnStickerClickListener {

    public static StickerMainFragment getInstance(StickerDataSourceListener stickerDataSourceListener) {
        return new StickerMainFragment(stickerDataSourceListener);
    }

    private StickerDataSourceListener stickerDataSourceListener;
    private ViewPager2 mVpSticker;
    private TabLayout mTlTab;
    private ImageView mIVClear;
    private List<StickerCategory> categoryList;
    private StickerBean selectedSticker;
    StickerFragmentAdapter fragmentAdapter;

    public StickerMainFragment(StickerDataSourceListener stickerDataSourceListener) {
        this.stickerDataSourceListener = stickerDataSourceListener;
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return LayoutInflater.from(getContext()).inflate(R.layout.beauty_sticker_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        mVpSticker = (ViewPager2) view.findViewById(R.id.vp_beauty);
        mTlTab = (TabLayout) view.findViewById(R.id.tl_tab);
        mVpSticker.setSaveEnabled(false);
        mIVClear = view.findViewById(R.id.iv_clear);

        if (stickerDataSourceListener != null) {
            selectedSticker = stickerDataSourceListener.getSelectedSticker();
            stickerDataSourceListener.getStickerCategories(new DataCallback<List<StickerCategory>>() {
                @Override
                public void onResult(List<StickerCategory> stickerCategories) {
                    categoryList = stickerCategories;
                    if (categoryList != null && categoryList.size() > 0) {
                        fragmentAdapter = new StickerFragmentAdapter(StickerMainFragment.this, categoryList, selectedSticker);
                        mVpSticker.setAdapter(fragmentAdapter);
                        new TabLayoutMediator(mTlTab, mVpSticker, true, false, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                tab.setText(categoryList.get(position).getName());
                            }
                        }).attach();
                    }
                }
            });
        }

        mIVClear.setOnClickListener(v -> {
            if (stickerDataSourceListener != null) {
                stickerDataSourceListener.onClearSticker();
                selectedSticker = null;
                if (fragmentAdapter != null) fragmentAdapter.setSelectedSticker(selectedSticker);
                refresh();
            }
        });
    }

    @Override
    protected int getDialogHeight() {
        return super.getDialogHeight();
    }

    @Override
    protected int getDialogWidth() {
        return getScreenWidth();
    }

    @Override
    protected int setContentGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int setWindowAnimations() {
        return R.style.Bottom_Dialog_Anim;
    }

    @Override
    public void onClickSticker(StickerBean stickerBean) {
        selectedSticker = stickerBean;
        if (fragmentAdapter != null) fragmentAdapter.setSelectedSticker(selectedSticker);
        if (stickerDataSourceListener != null)
            stickerDataSourceListener.onSelectSticker(stickerBean, new DataCallback<Boolean>() {
                @Override
                public void onResult(Boolean aBoolean) {
                    if (aBoolean) refresh();
                }
            });
    }

    public void refresh() {
        if (getView() != null && getView().isAttachedToWindow()) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof StickerFragment) {
                        ((StickerFragment) fragment).refresh(selectedSticker);
                    }
                }
            }
        }
    }
}
