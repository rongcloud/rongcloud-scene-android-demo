package com.meihu.beauty.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.TieZhiTitleAdapter;
import com.meihu.beauty.adapter.ViewPagerAdapter;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.bean.TieZhiTypeBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.constant.ResourceUrl;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;


public class MhTieZhiViewHolder extends AbsMhChildViewHolder implements View.OnClickListener, MhTieZhiChildViewHolder.ActionListener {

    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MhTieZhiChildViewHolder[] mViewHolders;
    private List<TieZhiTypeBean> mTypeList;
    private TieZhiTitleAdapter mTitleAdapter;
    private String mCheckedTieZhiName;

    public MhTieZhiViewHolder(Context context, ViewGroup parentView) {
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
        return R.layout.view_beauty_mh_tiezhi;
    }

    @Override
    public void init() {

//        findViewById(R.id.btn_hide).setOnClickListener(this);
//        findViewById(R.id.capture).setOnClickListener(this);
        RecyclerView titleRecyclerView = findViewById(R.id.title_recyclerView);
        ImageView imgClose = findViewById(R.id.close);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MhTieZhiChildViewHolder vh : mViewHolders) {
                    if (vh == null) continue;
                    vh.setCheckedPosition(-1);
                    if (mOnTieZhiClickListener != null) {
                        mOnTieZhiClickListener.OnTieZhiClick();
                    }
                    MhDataManager.getInstance().setTieZhi("", MHConfigConstants.TIE_ZHI_1);
                }
            }
        });

        List<MHCommonBean> typeList = new ArrayList<>();

        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_BASIC_STICKER, WordUtil.getString(MhDataManager.getInstance().getContext(), R.string.beauty_mh_005), false, MHConfigConstants.TIE_ZHI_1));
        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_PRO_STICKER, WordUtil.getString(MhDataManager.getInstance().getContext(), R.string.beauty_mh_006), true, MHConfigConstants.TIE_ZHI_2));
        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_BASIC_MASK, WordUtil.getString(MhDataManager.getInstance().getContext(), R.string.beauty_mh_007), false, MHConfigConstants.TIE_ZHI_3));
        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_PRO_MASK, WordUtil.getString(MhDataManager.getInstance().getContext(), R.string.beauty_mh_008), true, MHConfigConstants.TIE_ZHI_4));

//        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_BASIC_STICKER, WordUtil.getString(MhDataManager.getInstance().getContext(),R.string.beauty_mh_005), false, MHConfigConstants.TIE_ZHI_1,ResourceUrl.STICKER_THUMB_LIST_URL1));
//        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_PRO_STICKER, WordUtil.getString(MhDataManager.getInstance().getContext(),R.string.beauty_mh_006), true, MHConfigConstants.TIE_ZHI_2,ResourceUrl.STICKER_THUMB_LIST_URL2));
//        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_BASIC_MASK, WordUtil.getString(MhDataManager.getInstance().getContext(),R.string.beauty_mh_007), false,MHConfigConstants.TIE_ZHI_3,ResourceUrl.STICKER_THUMB_LIST_URL3));
//        typeList.add(new TieZhiTypeBean(MHSDK.TIEZHI_PRO_MASK, WordUtil.getString(MhDataManager.getInstance().getContext(),R.string.beauty_mh_008), true,MHConfigConstants.TIE_ZHI_4,ResourceUrl.STICKER_THUMB_LIST_URL4));

        TieZhiTypeBean tieZhiTypeBean = (TieZhiTypeBean) typeList.get(0);
        tieZhiTypeBean.setChecked(true);

        typeList = MHSDK.getFunctions(typeList, MHConfigConstants.TIE_ZHI);

        mTypeList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            TieZhiTypeBean bean = (TieZhiTypeBean) typeList.get(i);
            String url = MHSDK.getTieZhiUrl(bean.getKey());
            bean.setUrl(ResourceUrl.BASE_URL + url);
            mTypeList.add(bean);
        }
//        mTypeList.add(0,new TieZhiTypeBean(MHSDK.TIEZHI_NOONE));

        if (mTypeList.size() > 0) {
            titleRecyclerView.setLayoutManager(new GridLayoutManager(mContext, mTypeList.size(), GridLayoutManager.VERTICAL, false));
        }

        mTitleAdapter = new TieZhiTitleAdapter(mContext, mTypeList);
        mTitleAdapter.setOnItemClickListener(new OnItemClickListener<TieZhiTypeBean>() {
            @Override
            public void onItemClick(TieZhiTypeBean bean, int position) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position, true);
                }
            }
        });
        titleRecyclerView.setAdapter(mTitleAdapter);
        mViewPager = findViewById(R.id.mh_tiezhi_viewPager);
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
        mViewHolders = new MhTieZhiChildViewHolder[pageCount];
        loadPageData(0);

    }


    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        if (mViewHolders.length == 0) {
            return;
        }
        MhTieZhiChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                TieZhiTypeBean tieZhiTypeBean = mTypeList.get(position);
                vh = new MhTieZhiChildViewHolder(mContext, parent, mTypeList.get(position).getId(), tieZhiTypeBean.getKey(), tieZhiTypeBean.getUrl());
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.setActionListener(this);
            }
        }
        if (vh != null) {
            vh.loadData();
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


    @Override
    public void onTieZhiChecked(MhTieZhiChildViewHolder vh, TieZhiBean bean) {

        mCheckedTieZhiName = bean.getName();
        if (!TextUtils.isEmpty(mCheckedTieZhiName)) {
            if (mOnTieZhiClickListener != null) {
                mOnTieZhiClickListener.OnTieZhiClick();
            }
        }

        MhDataManager.getInstance().setTieZhi(bean.getName(), bean.getKey());

        for (MhTieZhiChildViewHolder viewHolder : mViewHolders) {
            if (viewHolder != null && viewHolder != vh) {
                viewHolder.setCheckedPosition(TextUtils.isEmpty(mCheckedTieZhiName) ? 0 : -1);
            }
        }

        int useFace;
        if (TextUtils.isEmpty(mCheckedTieZhiName)) {
            useFace = 0;
        } else {
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[0] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }

    }

    public void clearCheckedPosition() {
        for (MhTieZhiChildViewHolder viewHolder : mViewHolders) {
            if (viewHolder != null) {
                viewHolder.setCheckedPosition(-1);
            }
        }
    }

    @Override
    public String getCheckedTieZhiName() {
        return mCheckedTieZhiName;
    }

}
