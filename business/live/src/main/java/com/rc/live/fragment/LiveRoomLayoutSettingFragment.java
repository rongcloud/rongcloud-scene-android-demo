package com.rc.live.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rc.live.R;
import com.rc.live.inter.LiveLayoutSettingCallBack;

import java.util.ArrayList;

import cn.rongcloud.liveroom.api.RCLiveMixType;
import cn.rongcloud.liveroom.manager.RCDataManager;
import cn.rongcloud.roomkit.ui.room.fragment.seatsetting.SeatOperationViewPagerFragment;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/19
 * @time 11:43 上午
 * 布局设置
 */
public class LiveRoomLayoutSettingFragment extends BaseFragment {

    private RecyclerView rcLayoutSetting;
    private LiveRoomLayoutSettingFragment.layoutAdapter layoutAdapter;
    private ArrayList<RCLiveMixType> rcLiveMixTypes;
    private LiveLayoutSettingCallBack liveLayoutSettingCallBack;

    /**
     * 获取父布局
     *
     * @return
     */
    private SeatOperationViewPagerFragment getSeatOperationViewPagerFragment() {
        return ((SeatOperationViewPagerFragment) getParentFragment());
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_live_layout_setting;
    }

    @Override
    public void init() {
        rcLayoutSetting = (RecyclerView) getView().findViewById(R.id.rc_layout_setting);
        rcLayoutSetting.setLayoutManager(new GridLayoutManager(getContext(), 2));
        layoutAdapter = new layoutAdapter(R.layout.item_live_layout);
        rcLayoutSetting.setAdapter(layoutAdapter);

        rcLiveMixTypes = new ArrayList<>();
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeOneToOne);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeOneToSix);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeGridTwo);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeGridThree);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeGridFour);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeGridSeven);
        rcLiveMixTypes.add(RCLiveMixType.RCMixTypeGridNine);
        layoutAdapter.setList(rcLiveMixTypes);
        layoutAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //回传给界面，让界面去设置
                getSeatOperationViewPagerFragment().dismiss();
                if (liveLayoutSettingCallBack != null) {
                    liveLayoutSettingCallBack.setupMixType(rcLiveMixTypes.get(position));
                }
            }
        });
    }

    @Override
    public void initListener() {

    }

    @NonNull
    @Override
    public String getTitle() {
        return "布局设置";
    }

    public void setLiveLayoutSettingCallBack(LiveLayoutSettingCallBack liveLayoutSettingCallBack) {
        this.liveLayoutSettingCallBack = liveLayoutSettingCallBack;
    }

    class layoutAdapter extends BaseQuickAdapter<RCLiveMixType, BaseViewHolder> {

        public layoutAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder baseViewHolder, RCLiveMixType rcLiveMixType) {
            boolean isSelect = rcLiveMixType.getValue() == RCDataManager.get().getMixType();
            View view = baseViewHolder.getView(R.id.rl_mix_type);
            //判断当前是否为选中
            if (isSelect) {
                baseViewHolder.setTextColor(R.id.tv_type_id, Color.parseColor("#EF499A"));
                view.setBackground(getContext().getDrawable(R.drawable.shape_set_live_layout_bg_selected));
            } else {
                baseViewHolder.setTextColor(R.id.tv_type_id, Color.parseColor("#FFFFFF"));
                view.setBackground(getContext().getDrawable(R.drawable.shape_set_live_layout_bg_unselected));
            }
            TextView tvType = baseViewHolder.getView(R.id.tv_type_id);
            switch (rcLiveMixType) {
                case RCMixTypeOneToOne:
                    baseViewHolder.setText(R.id.tv_type_id, "默认");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_default_selsected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_default_unselsected), null, null, null);
                    }
                    break;
                case RCMixTypeOneToSix:
                    baseViewHolder.setText(R.id.tv_type_id, "浮窗");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_one_to_six_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_one_to_six_unselected), null, null, null);
                    }
                    break;
                case RCMixTypeGridTwo:
                    baseViewHolder.setText(R.id.tv_type_id, "双人");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_two_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_two_unselected), null, null, null);
                    }
                    break;
                case RCMixTypeGridThree:
                    baseViewHolder.setText(R.id.tv_type_id, "三人");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_three_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_three_unselected), null, null, null);
                    }
                    break;
                case RCMixTypeGridFour:
                    baseViewHolder.setText(R.id.tv_type_id, "四宫格");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_four_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_four_unselected), null, null, null);
                    }
                    break;
                case RCMixTypeGridSeven:
                    baseViewHolder.setText(R.id.tv_type_id, "七宫格");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_seven_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_seven_unselected), null, null, null);
                    }
                    break;
                case RCMixTypeGridNine:
                    baseViewHolder.setText(R.id.tv_type_id, "九宫格");
                    if (isSelect) {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_nine_selected), null, null, null);
                    } else {
                        tvType.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.icon_grid_nine_unselected), null, null, null);
                    }
                    break;
            }
        }
    }
}
