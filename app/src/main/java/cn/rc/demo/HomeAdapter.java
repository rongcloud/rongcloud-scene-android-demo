package cn.rc.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author gyn
 * @date 2022/5/30
 */
public class HomeAdapter extends FragmentStateAdapter {
    private HomeBottomBar[] barTitles;

    public HomeAdapter(@NonNull FragmentActivity fragmentActivity, HomeBottomBar[] homeBottomBars) {
        super(fragmentActivity);
        barTitles = homeBottomBars;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (Fragment) ARouter.getInstance().build(barTitles[position].router).navigation();
    }

    @Override
    public int getItemCount() {
        return barTitles.length;
    }
}
