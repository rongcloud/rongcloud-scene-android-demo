package com.meihu.beauty.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MeiYanTitleAdapter;
import com.meihu.beauty.adapter.ViewPagerAdapter;
import com.meihu.beauty.bean.MeiYanTypeBean;
import com.meihu.beauty.custom.TextSeekBar;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;

import java.util.ArrayList;
import java.util.List;

public class MhMeiYanViewHolder extends AbsMhChildViewHolder implements View.OnClickListener, TextSeekBar.ActionListener, MhMeiYanChildViewHolder.ActionListener {

    private TextSeekBar mSeekBar;
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MhMeiYanChildViewHolder[] mViewHolders;
    private List<MeiYanTypeBean> mTypeList;
    private MeiYanTitleAdapter mTitleAdapter;

    private MhMeiYanChildViewHolder mMhMeiYanChildViewHolder;

    public MhMeiYanViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void showSeekBar() {
        if (mMhMeiYanChildViewHolder != null) {
            mMhMeiYanChildViewHolder.showSeekBar();
        }
    }

    @Override
    public void hideSeekBar() {
        if (mSeekBar != null) {
            mSeekBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_beauty_mh_meiyan;
    }

    @Override
    public void init() {
//        findViewById(R.id.btn_hide).setOnClickListener(this);
        mSeekBar = findViewById(R.id.seek_bar);
        mSeekBar.setActionListener(this);

        List<MHCommonBean> typeList = new ArrayList<>();
        typeList.add(new MeiYanTypeBean(R.string.beauty_mh_010, MHConfigConstants.MEI_YAN_FUNCTION));
        typeList.add(new MeiYanTypeBean(R.string.beauty_mh_011, MHConfigConstants.MEI_YAN_MEI_XING_FUNCION));
        typeList.add(new MeiYanTypeBean(R.string.beauty_mh_012, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_FUNCTION));
        typeList.add(new MeiYanTypeBean(R.string.beauty_mh_013, MHConfigConstants.MEI_YAN_LV_JING_FUNCTION));

        MHCommonBean mhCommonBean = typeList.get(0);
        ((MeiYanTypeBean) mhCommonBean).setChecked(true);

        typeList = MHSDK.getFunctions(typeList, MHConfigConstants.MEI_YAN);

        mTypeList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            MeiYanTypeBean bean = (MeiYanTypeBean) typeList.get(i);
            mTypeList.add(bean);
        }

//        mTypeList = typeList;


        RecyclerView titleRecyclerView = findViewById(R.id.title_recyclerView);

        if (mTypeList.size() > 0) {
            titleRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mTypeList.size(), GridLayoutManager.VERTICAL, false));
        }

        mTitleAdapter = new MeiYanTitleAdapter(mContext, mTypeList);
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
        int pageCount = typeList.size();
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
        mViewHolders = new MhMeiYanChildViewHolder[pageCount];
        loadPageData(0);
    }


    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        if (mViewHolders.length == 0) {
            return;
        }
        MhMeiYanChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                vh = getMhMeiYanChildViewHolder(position, parent);
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.setActionListener(this);
            }
        }
        if (vh != null) {
            vh.loadData();
            vh.showSeekBar();
        }
        mMhMeiYanChildViewHolder = vh;
    }

    private MhMeiYanChildViewHolder getMhMeiYanChildViewHolder(int position, FrameLayout parent) {
        MhMeiYanChildViewHolder vh = null;
        String key = mTypeList.get(position).getKey();
        switch (key) {
            case MHConfigConstants.MEI_YAN_FUNCTION:
                vh = new MhMeiYanBeautyViewHolder(mContext, parent);
                break;
            case MHConfigConstants.MEI_YAN_MEI_XING_FUNCION:
                vh = new MhMeiYanShapeViewHolder(mContext, parent);
                break;
            case MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_FUNCTION://一键美颜
                vh = new MhMeiYanOneKeyViewHolder(mContext, parent);
                break;
            case MHConfigConstants.MEI_YAN_LV_JING_FUNCTION://滤镜
                vh = new MhMeiYanFilterViewHolder(mContext, parent);
                break;
        }
        return vh;
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

    @Override
    public void onProgressChanged(float rate, int progress) {
        if (mViewHolders != null && mViewPager != null) {
            MhMeiYanChildViewHolder vh = mViewHolders[mViewPager.getCurrentItem()];
            if (vh != null) {
                vh.onProgressChanged(rate, progress);
            }
        }
    }


    @Override
    public void changeProgress(boolean visible, int max, int progress, int text) {
        if (mSeekBar != null) {
            if (visible) {
                if (mSeekBar.getVisibility() != View.VISIBLE) {
                    mSeekBar.setVisibility(View.VISIBLE);
                }
                mSeekBar.setMax(max);
                mSeekBar.setProgress(progress);
                mSeekBar.setLeftText(text);
            } else {
                if (mSeekBar.getVisibility() != View.INVISIBLE) {
                    mSeekBar.setVisibility(View.GONE);
                }
            }
        }
    }
}
