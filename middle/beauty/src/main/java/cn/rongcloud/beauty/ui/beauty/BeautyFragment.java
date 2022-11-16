package cn.rongcloud.beauty.ui.beauty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.beauty.R;
import cn.rongcloud.beauty.base.BaseDelegate;
import cn.rongcloud.beauty.base.BaseListAdapter;
import cn.rongcloud.beauty.base.BaseViewHolder;
import cn.rongcloud.beauty.dialog.BaseDialogFragment;
import cn.rongcloud.beauty.dialog.ConfirmDialogFragment;
import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;
import cn.rongcloud.beauty.utils.DecimalUtils;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class BeautyFragment extends Fragment {
    private static final String BEAUTY = "BEAUTY";
    private static final String DEFAULT_BEAUTY = "DEFAULT_BEAUTY";

    public static BeautyFragment getInstance(BeautyCategory beautyCategory, BeautyCategory defaultBeautyCategory) {
        BeautyFragment fragment = new BeautyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BEAUTY, beautyCategory);
        bundle.putSerializable(DEFAULT_BEAUTY, defaultBeautyCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    private BeautyCategory beautyCategory;
    private BeautyBean selectedBeauty;
    private OnBeautyClickListener onBeautyClickListener;
    private BaseListAdapter<BeautyBean> adapter;
    private ConstraintLayout clRecovery;
    private Map<String, BeautyBean> originBeautyBeanMap;

    public void setOnBeautyClickListener(OnBeautyClickListener onBeautyClickListener) {
        this.onBeautyClickListener = onBeautyClickListener;
        if (onBeautyClickListener != null && selectedBeauty != null) {
            onBeautyClickListener.onClickBeauty(selectedBeauty);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beautyCategory = (BeautyCategory) getArguments().getSerializable(BEAUTY);
        BeautyCategory defaultBeautyCategory = (BeautyCategory) getArguments().getSerializable(DEFAULT_BEAUTY);
        if (defaultBeautyCategory != null && defaultBeautyCategory.getBeautyBeanList() != null) {
            originBeautyBeanMap = new HashMap<>();
            for (BeautyBean beautyBean : defaultBeautyCategory.getBeautyBeanList()) {
                originBeautyBeanMap.put(beautyBean.getKey(), beautyBean.copy());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.beauty_fragment_beauty, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (beautyCategory != null) {
            if (beautyCategory.getDefaultSelected() != null) {
                // 默认选中
                selectedBeauty = beautyCategory.getDefaultSelected();
            } else {
                // 没有默认选中，则选中第一个
                selectedBeauty = beautyCategory.getBeautyBeanList().get(0);
            }

            clRecovery = view.findViewById(R.id.cl_recovery);
            clRecovery.setVisibility(beautyCategory.isHasRecovery() ? View.VISIBLE : View.GONE);
            RecyclerView recyclerView = view.findViewById(R.id.rv_beauty);
            adapter = new BaseListAdapter<BeautyBean>(beautyCategory.getBeautyBeanList(), new BaseDelegate<BeautyBean>() {
                @Override
                public void convert(int viewType, BaseViewHolder helper, BeautyBean data, int position) {
                    helper.setImageResource(R.id.iv_beauty, data.isStand() ? data.getCloseRes() : data.getOpenRes());
                    helper.setText(R.id.tv_beauty, data.getDesRes());
                    helper.itemView.setSelected(TextUtils.equals(selectedBeauty.getKey(), data.getKey()));
                }

                @Override
                public void onItemClickListener(View view, BeautyBean data, int position) {
                    super.onItemClickListener(view, data, position);
                    selectedBeauty = data;
                    beautyCategory.setDefaultSelected(selectedBeauty);
                    adapter.notifyDataSetChanged();
                    if (onBeautyClickListener != null) {
                        onBeautyClickListener.onClickBeauty(data);
                    }
                }
            }, R.layout.beauty_item_beauty);
            recyclerView.setAdapter(adapter);
            if (selectedBeauty != null) {
                int index = beautyCategory.getBeautyBeanList().indexOf(selectedBeauty);
                if (index != -1) {
                    recyclerView.scrollToPosition(index);
                }
            }
            setRecoverEnable(false);
            if (onBeautyClickListener != null && selectedBeauty != null) {
                onBeautyClickListener.onClickBeauty(selectedBeauty);
            }

            clRecovery.setOnClickListener(v -> {
                ConfirmDialogFragment.newInstance(getString(R.string.dialog_reset_avatar_model),
                        new BaseDialogFragment.OnClickListener() {
                            @Override
                            public void onConfirm() {
                                recoveryBeauty();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show(getChildFragmentManager(), "ConfirmDialogFragment");
            });
            checkRecoverEnable();
        }
    }

    public void notifyBeautyItem() {
        if (beautyCategory != null) {
            int index = beautyCategory.getBeautyBeanList().indexOf(selectedBeauty);
            adapter.notifyItemChanged(index);
        }
    }

    /**
     * 重置还原按钮状态
     *
     * @param enable Boolean
     */
    private void setRecoverEnable(Boolean enable) {
        if (enable) {
            clRecovery.setAlpha(1f);
        } else {
            clRecovery.setAlpha(0.6f);
        }
        clRecovery.setEnabled(enable);
    }

    public boolean checkRecoverEnable() {
        if (originBeautyBeanMap != null) {

            if (!DecimalUtils.doubleEquals(selectedBeauty.getIntensity(), originBeautyBeanMap.get(selectedBeauty.getKey()).getIntensity())) {
                setRecoverEnable(true);
                return true;
            }
            for (BeautyBean beautyBean : beautyCategory.getBeautyBeanList()) {
                if (!DecimalUtils.doubleEquals(beautyBean.getIntensity(), originBeautyBeanMap.get(beautyBean.getKey()).getIntensity())) {
                    setRecoverEnable(true);
                    return true;
                }
            }
        }
        setRecoverEnable(false);
        return false;
    }

    // 恢复初始值
    private void recoveryBeauty() {
        if (onBeautyClickListener != null) {
            onBeautyClickListener.onRecoveryBeauty(new ArrayList<>(originBeautyBeanMap.values()));
            for (BeautyBean beautyBean : beautyCategory.getBeautyBeanList()) {
                beautyBean.setIntensity(originBeautyBeanMap.get(beautyBean.getKey()).getIntensity());
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public interface OnBeautyClickListener {
        void onClickBeauty(BeautyBean beautyBean);

        void onRecoveryBeauty(List<BeautyBean> beautyBeanList);
    }
}
