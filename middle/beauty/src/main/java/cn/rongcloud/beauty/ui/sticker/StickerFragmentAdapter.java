package cn.rongcloud.beauty.ui.sticker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import cn.rongcloud.beauty.entity.StickerBean;
import cn.rongcloud.beauty.entity.StickerCategory;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class StickerFragmentAdapter extends FragmentStateAdapter {

    private List<StickerCategory> stickerCategories;
    private StickerBean selectedSticker;
    private StickerFragment.OnStickerClickListener onStickerClickListener;

    public StickerFragmentAdapter(@NonNull Fragment fragment, List<StickerCategory> stickerCategories, StickerBean selectedSticker) {
        super(fragment);
        this.stickerCategories = stickerCategories;
        this.selectedSticker = selectedSticker;
        if (fragment instanceof StickerFragment.OnStickerClickListener) {
            onStickerClickListener = (StickerFragment.OnStickerClickListener) fragment;
        }
    }

    public void setSelectedSticker(StickerBean selectedSticker) {
        this.selectedSticker = selectedSticker;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        StickerFragment fragment = StickerFragment.getInstance(stickerCategories.get(position), selectedSticker);
        fragment.setOnStickerClickListener(onStickerClickListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return stickerCategories.size();
    }
}
