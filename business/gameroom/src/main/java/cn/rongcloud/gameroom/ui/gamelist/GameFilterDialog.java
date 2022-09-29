package cn.rongcloud.gameroom.ui.gamelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.utils.UiUtils;
import com.basis.widget.BasePopupWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.FilterOption;
import cn.rongcloud.roomkit.widget.GridSpacingItemDecoration;
import io.rong.imkit.utils.StatusBarUtil;

/**
 * @author gyn
 * @date 2022/3/18
 */
public class GameFilterDialog<T> extends BasePopupWindow {

    private ConstraintLayout clContent;
    private Animation inAnimation;
    private Animation outAnimation;
    private OptionAdapter optionAdapter;
    private List<FilterOption<T>> optionList;
    private boolean singleSelect;
    private OnFilterOptionListener filterOptionListener;

    public GameFilterDialog(Context context, int height, List<FilterOption<T>> optionList, boolean singleSelect, OnFilterOptionListener filterOptionListener) {
        super(context, R.layout.dialog_game_filter, ViewGroup.LayoutParams.MATCH_PARENT, height, true);
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.pop_enter_anim);
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.pop_exit_anim);
        this.optionList = optionList;
        this.singleSelect = singleSelect;
        this.filterOptionListener = filterOptionListener;
    }

    @Override
    protected void initView(@NonNull View content) {
        super.initView(content);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.GameFilterDialog);
        clContent = content.findViewById(R.id.cl_content);
        clContent.setTranslationY(0);
        content.findViewById(R.id.fl_root_view).setOnClickListener(v -> dismiss());
        content.findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        content.findViewById(R.id.tv_finish).setOnClickListener(v -> {
            List<FilterOption> selected = new ArrayList<>();
            if (optionList != null) {
                for (FilterOption<T> filterOption : optionList) {
                    if (filterOption.isSelect()) {
                        selected.add(filterOption);
                    }
                }
            }
            filterOptionListener.onClickFilterFinish(selected);
            dismiss();
        });
        optionAdapter = new OptionAdapter();
        RecyclerView rvOption = content.findViewById(R.id.rv_option);
        rvOption.setAdapter(optionAdapter);
        rvOption.addItemDecoration(new GridSpacingItemDecoration(3, UiUtils.dp2px(10), false));
        optionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (optionList != null) {
                    FilterOption<T> filterOption = optionList.get(position);
                    if (singleSelect) {
                        if (filterOption.isSelect()) {
                            return;
                        }
                        for (int i = 0; i < optionList.size(); i++) {
                            optionList.get(i).setSelect(i == position);
                        }
                    } else {
                        optionList.get(position).setSelect(!filterOption.isSelect());
                    }
                    optionAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void show(View anchor) {
        if (!isShowing()) {
            int[] location = UiUtils.getLocation(anchor);
            int height = UiUtils.getScreenHeight(anchor.getContext()) - location[1] - anchor.getHeight() + StatusBarUtil.getStatusBarHeight(anchor.getContext());
            setHeight(height);
            super.showAsDropDown(anchor);
            clContent.startAnimation(inAnimation);
            optionAdapter.setNewInstance(optionList);
        }
    }

    @Override
    public void dismiss() {
        clContent.startAnimation(outAnimation);
        clContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                GameFilterDialog.super.dismiss();
            }
        }, 200);
    }

    public class OptionAdapter extends BaseQuickAdapter<FilterOption<T>, BaseViewHolder> {

        public OptionAdapter() {
            super(R.layout.game_item_option);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, FilterOption<T> filterOption) {
            baseViewHolder.setText(R.id.tv_option, filterOption.getTitle());
            baseViewHolder.getView(R.id.tv_option).setSelected(filterOption.isSelect());
        }
    }

    public interface OnFilterOptionListener {
        void onClickFilterFinish(List<FilterOption> optionList);
    }
}
