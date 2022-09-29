package cn.rongcloud.beauty.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import cn.rongcloud.beauty.entity.BeautyCategory;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class BeautyFragmentAdapter extends FragmentStateAdapter {

    private List<BeautyCategory> beautyCategories;
    private List<BeautyCategory> defaultBeautyCategories;

    public BeautyFragmentAdapter(@NonNull Fragment fragment, List<BeautyCategory> beautyCategories, List<BeautyCategory> defaultBeautyCategories) {
        super(fragment);
        this.beautyCategories = beautyCategories;
        this.defaultBeautyCategories = defaultBeautyCategories;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return BeautyFragment.getInstance(beautyCategories.get(position), defaultBeautyCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return beautyCategories.size();
    }
}
