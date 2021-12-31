package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhMeiYanAdapter;
import com.meihu.beauty.bean.MeiYanBean;
import com.meihu.beauty.bean.MeiYanValueBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;

import java.util.ArrayList;
import java.util.List;

public class MhMeiYanBeautyViewHolder extends MhMeiYanChildViewHolder implements OnItemClickListener<MeiYanBean> {

    private MhMeiYanAdapter mAdapter;

    public MhMeiYanBeautyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new MeiYanBean(R.string.beauty_mh_no, R.mipmap.beauty_drawing_default, R.mipmap.beauty_drawing_sele, MHConfigConstants.MEI_YAN_YUAN_TU));
        beans.add(new MeiYanBean(R.string.beauty_mh_meibai, R.mipmap.beauty_white_default, R.mipmap.beauty_white_sele, MHConfigConstants.MEI_YAN_MEI_BAI));
        beans.add(new MeiYanBean(R.string.beauty_mh_mopi, R.mipmap.beauty_buffing_default, R.mipmap.beauty_buffing_sele, MHConfigConstants.MEI_YAN_MO_PI));
        beans.add(new MeiYanBean(R.string.beauty_mh_hongrun, R.mipmap.beauty_ruddy_default, R.mipmap.beauty_ruddy_sele, MHConfigConstants.MEI_YAN_HONG_RUN));
        beans.add(new MeiYanBean(R.string.beauty_mh_liangdu, R.mipmap.beauty_light_default, R.mipmap.beauty_light_sele, MHConfigConstants.MEI_YAN_LIANG_DU));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.MEI_YAN, MHConfigConstants.MEI_YAN_FUNCTION);

        List<MeiYanBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            MeiYanBean bean = (MeiYanBean) beans.get(i);
            list.add(bean);
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MhMeiYanAdapter(mContext, list);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(MeiYanBean bean, int position) {
        if (mActionListener == null) {
            return;
        }
        MeiYanValueBean valueBean = MhDataManager.getInstance().getMeiYanValue();
        if (valueBean == null) {
            return;
        }
        int name = bean.getName();
        if (name == R.string.beauty_mh_no) {
            mActionListener.changeProgress(false, 0, 0, name);
            valueBean.setMeiBai(0);
            valueBean.setMoPi(0);
            valueBean.setHongRun(0);
            MhDataManager.getInstance().useMeiYan().notifyMeiYanChanged();
        } else if (name == R.string.beauty_mh_meibai) {
            mActionListener.changeProgress(true, 9, valueBean.getMeiBai(), name);
        } else if (name == R.string.beauty_mh_mopi) {
            mActionListener.changeProgress(true, 9, valueBean.getMoPi(), name);
        } else if (name == R.string.beauty_mh_hongrun) {
            mActionListener.changeProgress(true, 9, valueBean.getHongRun(), name);
        } else if (name == R.string.beauty_mh_liangdu) {
            mActionListener.changeProgress(true, 100, valueBean.getLiangDu(), name);
        }
    }

    @Override
    public void onProgressChanged(float rate, int progress) {
        if (mAdapter == null) {
            return;
        }
        int name = mAdapter.getCheckedName();
        if (name == R.string.beauty_mh_meibai) {
            MhDataManager.getInstance().setMeiBai(progress);
        } else if (name == R.string.beauty_mh_mopi) {
            MhDataManager.getInstance().setMoPi(progress);
        } else if (name == R.string.beauty_mh_hongrun) {
            MhDataManager.getInstance().setHongRun(progress);
        } else if (name == R.string.beauty_mh_liangdu) {
            MhDataManager.getInstance().setLiangDu(progress);
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
        MeiYanBean bean = mAdapter.getCheckedBean();
        if (bean != null) {
            onItemClick(bean, 0);
        } else {
            if (mActionListener != null) {
                mActionListener.changeProgress(false, 0, 0, 0);
            }
        }
    }
}
