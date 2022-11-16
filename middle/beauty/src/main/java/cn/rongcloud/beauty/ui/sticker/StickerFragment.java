package cn.rongcloud.beauty.ui.sticker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.rongcloud.beauty.R;
import cn.rongcloud.beauty.base.BaseDelegate;
import cn.rongcloud.beauty.base.BaseListAdapter;
import cn.rongcloud.beauty.base.BaseViewHolder;
import cn.rongcloud.beauty.entity.StickerBean;
import cn.rongcloud.beauty.entity.StickerCategory;

/**
 * @author gyn
 * @date 2022/10/17
 */
public class StickerFragment extends Fragment {
    private static final String STICKER = "STICKER";
    private static final String SELECTED_STICKER = "SELECTED_STICKER";

    public static StickerFragment getInstance(StickerCategory stickerCategory, StickerBean stickerBean) {
        StickerFragment fragment = new StickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(STICKER, stickerCategory);
        bundle.putSerializable(SELECTED_STICKER, stickerBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    private StickerCategory stickerCategory;
    private BaseListAdapter<StickerBean> adapter;
    private StickerBean selectedSticker;
    private OnStickerClickListener onStickerClickListener;

    public void setOnStickerClickListener(OnStickerClickListener onStickerClickListener) {
        this.onStickerClickListener = onStickerClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stickerCategory = (StickerCategory) getArguments().getSerializable(STICKER);
        selectedSticker = (StickerBean) getArguments().getSerializable(SELECTED_STICKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.beauty_fragment_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (stickerCategory != null) {
            RecyclerView recyclerView = view.findViewById(R.id.rv_sticker);
            adapter = new BaseListAdapter<StickerBean>(stickerCategory.getStickerBeanList(), new BaseDelegate<StickerBean>() {
                @Override
                public void convert(int viewType, BaseViewHolder helper, StickerBean data, int position) {
                    // ImageLoader.loadLocal(helper.getView(R.id.iv_sticker), data.getImgUrl(), R.color.transparent);
                    Glide.with(view).load(data.getPreviewImgPath()).into((ImageView) helper.getView(R.id.iv_sticker));
                    helper.getView(R.id.iv_sticker).setSelected(selectedSticker != null && data.getId() == selectedSticker.getId());
                    helper.setVisible(R.id.iv_download, !data.hasDownload());
                    Animation rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                    if (data.isLoading()) {
                        helper.getView(R.id.iv_loading).startAnimation(rotateAnim);
                        helper.setVisible(R.id.iv_loading, true);
                        helper.setVisible(R.id.iv_download, false);
                        helper.getView(R.id.iv_sticker).setAlpha(0.6f);
                    } else {
                        helper.getView(R.id.iv_sticker).setAlpha(1f);
                        helper.setVisible(R.id.iv_loading, false);
                        helper.getView(R.id.iv_loading).clearAnimation();
                    }
                }

                @Override
                public void onItemClickListener(View view, StickerBean data, int position) {
                    super.onItemClickListener(view, data, position);
                    if (!data.hasDownload()) {
                        data.setLoading(true);
                    }
                    selectedSticker = data;
                    adapter.notifyDataSetChanged();
                    if (onStickerClickListener != null) {
                        data.setCategory(stickerCategory.getCategory());
                        onStickerClickListener.onClickSticker(data);
                    }
                }
            }, R.layout.beauty_item_sticker);
            recyclerView.setAdapter(adapter);
        }
    }

    public void refresh(StickerBean stickerBean) {
        selectedSticker = stickerBean;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public interface OnStickerClickListener {
        void onClickSticker(StickerBean stickerBean);
    }
}
