package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhHaHaAdapter;
import com.meihu.beauty.bean.HaHaBean;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;

public class MhTeXiaoHaHaViewHolder extends MhTeXiaoChildViewHolder implements OnItemClickListener<HaHaBean> {

    public MhTeXiaoHaHaViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new HaHaBean(MHSDK.HAHA_NONE, 0, R.mipmap.ic_mh_none, false, MHConfigConstants.TE_XIAO_HA_HA_JING_WU));
        beans.add(new HaHaBean(MHSDK.HAHA_WAIXING, R.string.beauty_mh_haha_waixingren, R.mipmap.ic_haha_waixingren, MHConfigConstants.TE_XIAO_HA_HA_JING_WAI_XING_REN));
        beans.add(new HaHaBean(MHSDK.HAHA_LI, R.string.beauty_mh_haha_li, R.mipmap.ic_haha_li, MHConfigConstants.TE_XIAO_HA_HA_JING_LI_LI_LIAN));
        beans.add(new HaHaBean(MHSDK.HAHA_SHOU, R.string.beauty_mh_haha_shou, R.mipmap.ic_haha_shou, MHConfigConstants.TE_XIAO_HA_HA_JING_SHOU_SHOU_LIAN));
        beans.add(new HaHaBean(MHSDK.HAHA_JING_XIANG, R.string.beauty_mh_haha_jingxiang, R.mipmap.ic_haha_jingxiang, MHConfigConstants.TE_XIAO_HA_HA_JING_JING_XIANG_LIAN));
        beans.add(new HaHaBean(MHSDK.HAHA_PIAN_DUAN, R.string.beauty_mh_haha_pianduan, R.mipmap.ic_haha_pianduan, MHConfigConstants.TE_XIAO_HA_HA_JING_PIAN_DUAN_LIAN));
        beans.add(new HaHaBean(MHSDK.HAHA_DAO_YING, R.string.beauty_mh_haha_daoying, R.mipmap.ic_haha_daoying, MHConfigConstants.TE_XIAO_HA_HA_JING_SHUI_MIAN_DAO_YING));
        beans.add(new HaHaBean(MHSDK.HAHA_LUO_XUAN, R.string.beauty_mh_haha_xuanzhuan, R.mipmap.ic_haha_xuanzhuan, MHConfigConstants.TE_XIAO_HA_HA_JING_LUO_XUAN_JING_MIAN));
        beans.add(new HaHaBean(MHSDK.HAHA_YU_YAN, R.string.beauty_mh_haha_yuyan, R.mipmap.ic_haha_yuyan, MHConfigConstants.TE_XIAO_HA_HA_JING_YU_YAN_XIANG_JI));
        beans.add(new HaHaBean(MHSDK.HAHA_ZUO_YOU, R.string.beauty_mh_haha_zuoyou, R.mipmap.ic_haha_zuoyou, MHConfigConstants.TE_XIAO_HA_HA_JING_ZUO_YOU_JING_XIANG));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.TE_XIAO, MHConfigConstants.TE_XIAO_HA_HA_JING_FUNCTION);
        MeiYanDataBean meiYanDataBean = MhDataManager.getInstance().getMeiYanDataBean();
        List<HaHaBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            HaHaBean bean = (HaHaBean) beans.get(i);
            if (meiYanDataBean != null) {
                if (meiYanDataBean.getHahaName() == bean.getId()) {
                    bean.setChecked(true);
                }
            } else {
                //第一次进来的话，默认第一个选中
                if (i == 0) bean.setChecked(true);
            }
            list.add(bean);
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        MhHaHaAdapter adapter = new MhHaHaAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(HaHaBean bean, int position) {

        int useFace;
        if (bean.getId() == MHSDK.HAHA_NONE) {
            useFace = 0;
        } else {
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[3] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }

        MhDataManager.getInstance().setHaHa(bean.getId(), true);
        MeiYanDataBean dataBean = MhDataManager.getInstance().getMeiYanDataBean();
        if (null != dataBean) {
            dataBean.setHahaName(bean.getId());
        }
    }


}
