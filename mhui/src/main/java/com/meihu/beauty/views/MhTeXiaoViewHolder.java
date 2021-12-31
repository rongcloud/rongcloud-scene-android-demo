package com.meihu.beauty.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MeiYanTitleAdapter;
import com.meihu.beauty.adapter.ViewPagerAdapter;
import com.meihu.beauty.bean.MeiYanTypeBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionDownloadListener;
import com.meihu.beauty.interfaces.OnTieZhiActionListener;
import com.meihu.beauty.utils.WordUtil;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;

import java.util.ArrayList;
import java.util.List;

public class MhTeXiaoViewHolder extends AbsMhChildViewHolder implements View.OnClickListener {

    private final String TAG = MhTeXiaoViewHolder.class.getName();
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MhTeXiaoChildViewHolder[] mViewHolders;
    private MeiYanTitleAdapter mTitleAdapter;
    private TextView mTip;
    private List<MeiYanTypeBean> mList;


    public MhTeXiaoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void showSeekBar() {

    }

    @Override
    public void hideSeekBar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_beauty_mh_texiao;
    }

    @Override
    public void init() {
//        findViewById(R.id.btn_hide).setOnClickListener(this);
//        mTip = findViewById(R.id.tip);

        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new MeiYanTypeBean(R.string.beauty_mh_003, MHConfigConstants.TE_XIAO_FUNCTION));
        beans.add(new MeiYanTypeBean(R.string.beauty_mh_014, MHConfigConstants.TE_XIAO_SHUI_YIN_FUNCTION));
        beans.add(new MeiYanTypeBean(R.string.beauty_mh_015, MHConfigConstants.TE_XIAO_DONG_ZUO_FUNCTION));
        beans.add(new MeiYanTypeBean(R.string.beauty_mh_004, MHConfigConstants.TE_XIAO_HA_HA_JING_FUNCTION));

        MeiYanTypeBean meiYanTypeBean = (MeiYanTypeBean) beans.get(0);
        meiYanTypeBean.setChecked(true);

        beans = MHSDK.getFunctions(beans, MHConfigConstants.TE_XIAO);

        List<MeiYanTypeBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            MeiYanTypeBean bean = (MeiYanTypeBean) beans.get(i);
            list.add(bean);
        }

        mList = list;

        RecyclerView titleRecyclerView = findViewById(R.id.title_recyclerView);

        if (list.size() > 0) {
            titleRecyclerView.setLayoutManager(new GridLayoutManager(mContext, list.size(), GridLayoutManager.VERTICAL, false));
        }


        mTitleAdapter = new MeiYanTitleAdapter(mContext, list);
        mTitleAdapter.setOnItemClickListener(new OnItemClickListener<MeiYanTypeBean>() {
            @Override
            public void onItemClick(MeiYanTypeBean bean, int position) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position, true);
                }
            }
        });
        titleRecyclerView.setAdapter(mTitleAdapter);


        mViewPager = findViewById(R.id.mh_meiyan_viewPager);
        mViewList = new ArrayList<>();
        int pageCount = beans.size();
        for (int i = 0; i < pageCount; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        if (pageCount > 1) {
            mViewPager.setOffscreenPageLimit(pageCount - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mTitleAdapter != null) {
                    mTitleAdapter.setCheckedPosition(position);
                }
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewHolders = new MhTeXiaoChildViewHolder[pageCount];
        loadPageData(0);
    }

    private MhTeXiaoChildViewHolder getMhTeXiaoChildViewHolder(int position, FrameLayout parent) {
        MhTeXiaoChildViewHolder vh = null;
        String key = mList.get(position).getKey();
        switch (key) {
            case MHConfigConstants.TE_XIAO_FUNCTION:
                vh = new MhTeXiaoSpecialViewHolder(mContext, parent);
                break;
            case MHConfigConstants.TE_XIAO_SHUI_YIN_FUNCTION:
                vh = new MhTeXiaoWaterViewHolder(mContext, parent);
                break;
            case MHConfigConstants.TE_XIAO_DONG_ZUO_FUNCTION:
                vh = new MhTeXiaoActionViewHolder(mContext, parent);
                break;
            case MHConfigConstants.TE_XIAO_HA_HA_JING_FUNCTION:
                vh = new MhTeXiaoHaHaViewHolder(mContext, parent);
                break;
        }
        return vh;
    }

    private int getMhChildViewHolderPosition(String key) {
        int position = -1;
        for (int i = 0; i < mList.size(); i++) {
            String function = mList.get(i).getKey();
            if (function.equals(key)) {
                position = i;
            }
        }
        return position;
    }

    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        if (mViewHolders.length == 0) {
            return;
        }
        MhTeXiaoChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                vh = getMhTeXiaoChildViewHolder(position, parent);
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
            }
        }
        if (vh != null) {
            vh.loadData();
            vh.setOnTieZhiActionClickListener(new OnTieZhiActionClickListener() {
                @Override
                public void OnTieZhiActionClick(int action) {
                    if (mOnTieZhiActionClickListener != null) {
                        mOnTieZhiActionClickListener.OnTieZhiActionClick(action);
                    }
                }
            });
            vh.setOnTieZhiActionListener(new OnTieZhiActionListener() {
                @Override
                public void OnTieZhiAction(int action) {
                    if (mOnTieZhiActionListener != null) {
                        mOnTieZhiActionListener.OnTieZhiAction(action);
                    }
                }
            });
            vh.setOnTieZhiActionDownloadListener(new OnTieZhiActionDownloadListener() {
                @Override
                public void OnTieZhiActionDownload(int state) {
//                    showTieZhiDownloadTip(state);
                }
            });
        }
    }

    private void showTieZhiDownloadTip(int state) {
        if (state == 0) {
            mTip.setText(WordUtil.getString(mContext, R.string.beauty_mh_texiao_action_downloading));
            mTip.setVisibility(View.VISIBLE);
        } else {
            mTip.setVisibility(View.INVISIBLE);
            mTip.setText("");
        }
    }

    public void setActionItemClick(int action) {
        if (action == 0) {
            if (mViewHolders != null) {
                for (int i = 0; i < mViewHolders.length; i++) {
                    MhTeXiaoChildViewHolder mhTeXiaoChildViewHolder = mViewHolders[i];
                    if (mhTeXiaoChildViewHolder instanceof MhTeXiaoActionViewHolder) {
                        MhTeXiaoActionViewHolder mhTeXiaoActionViewHolder = (MhTeXiaoActionViewHolder) mhTeXiaoChildViewHolder;
                        mhTeXiaoActionViewHolder.setItemClick(0);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mIBeautyClickListener == null) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_hide) {
            mIBeautyClickListener.tabMain();
        }
    }
}
