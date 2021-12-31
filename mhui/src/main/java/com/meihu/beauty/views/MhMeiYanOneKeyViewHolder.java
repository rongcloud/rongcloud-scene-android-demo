package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhMeiYanOneKeyAdapter;
import com.meihu.beauty.bean.MeiYanOneKeyBean;
import com.meihu.beauty.bean.MeiYanValueBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;

public class MhMeiYanOneKeyViewHolder extends MhMeiYanChildViewHolder implements OnItemClickListener<MeiYanOneKeyBean> {

    private MhMeiYanOneKeyAdapter mAdapter;

    public MhMeiYanOneKeyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_no, R.mipmap.ic_onekey_no, true, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_YUAN_TU));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_biaozhun, R.mipmap.ic_onekey_biaozhun, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_BIAO_ZHUN));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_youya, R.mipmap.ic_onekey_youya, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_YOU_YA));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_jingzhi, R.mipmap.ic_onekey_jingzhi, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_JING_ZHI));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_keai, R.mipmap.ic_onekey_keai, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_KE_AI));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_ziran, R.mipmap.ic_onekey_ziran, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_ZI_RAN));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_wanghong, R.mipmap.ic_onekey_wanghong, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_WANG_HONG));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_tuosu, R.mipmap.ic_onekey_tuosu, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_TUO_SU));
        beans.add(new MeiYanOneKeyBean(R.string.beauty_mh_gaoya, R.mipmap.ic_onekey_gaoya, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_GAO_YA));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.MEI_YAN, MHConfigConstants.MEI_YAN_YI_JIAN_MEI_YAN_FUNCTION);

        List<MeiYanOneKeyBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            MeiYanOneKeyBean bean = (MeiYanOneKeyBean) beans.get(i);
            list.add(bean);
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MhMeiYanOneKeyAdapter(mContext, list);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(MeiYanOneKeyBean bean, int position) {
        if (mActionListener == null) {
            return;
        }
        int name = bean.getName();
        int useFace;

        if (name == R.string.beauty_mh_no) {
            mActionListener.changeProgress(false, 0, 0, 0);
            MhDataManager.getInstance().useMeiYan().notifyMeiYanChanged();
            useFace = 0;
        } else {
            mActionListener.changeProgress(true, 100, bean.getProgress(), bean.getName());
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[2] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }
    }


    @Override
    public void onProgressChanged(float rate, int progress) {
        if (mAdapter == null) {
            return;
        }
        MeiYanOneKeyBean bean = mAdapter.getCheckedBean();
        if (bean != null) {
            bean.setProgress(progress);
            MeiYanValueBean valueBean = bean.calculateValue(rate);
            MhDataManager.getInstance()
                    .setOneKeyValue(valueBean)
                    .useOneKey()
                    .notifyMeiYanChanged();
        }
    }


    @Override
    public void showSeekBar() {
        if (mAdapter == null) {
            if (mActionListener != null) {
                mActionListener.changeProgress(false, 0, 0, 0);
            }
            return;
        }
        MeiYanOneKeyBean bean = mAdapter.getCheckedBean();
        if (bean != null) {
            onItemClick(bean, 0);
        } else {
            if (mActionListener != null) {
                mActionListener.changeProgress(false, 0, 0, 0);
            }
        }
    }
}
