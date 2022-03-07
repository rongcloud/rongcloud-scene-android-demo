package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhMeiYanFilterAdapter;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.bean.MeiYanFilterBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;

import java.util.ArrayList;
import java.util.List;

public class MhMeiYanFilterViewHolder extends MhMeiYanChildViewHolder implements OnItemClickListener<MeiYanFilterBean> {

    public MhMeiYanFilterViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_no, R.mipmap.ic_filter_no, MHSDK.FILTER_NONE, false, MHConfigConstants.MEI_YAN_LV_JING_YUAN_TU));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_langman, R.mipmap.ic_filter_langman, MHSDK.FILTER_LANG_MAN, MHConfigConstants.MEI_YAN_LV_JING_LANG_MAN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_qingxin, R.mipmap.ic_filter_qingxin, MHSDK.FILTER_QING_XIN, MHConfigConstants.MEI_YAN_LV_JING_QING_XIN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_weimei, R.mipmap.ic_filter_weimei, MHSDK.FILTER_WEI_MEI, MHConfigConstants.MEI_YAN_LV_JING_WEI_MEI));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_fennen, R.mipmap.ic_filter_fennen, MHSDK.FILTER_FEN_NEN, MHConfigConstants.MEI_YAN_LV_JING_FEN_NEN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_huaijiu, R.mipmap.ic_filter_huaijiu, MHSDK.FILTER_HUAI_JIU, MHConfigConstants.MEI_YAN_LV_JING_HUAI_JIU));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_qingliang, R.mipmap.ic_filter_qingliang, MHSDK.FILTER_QING_LIANG, MHConfigConstants.MEI_YAN_LV_JING_LAN_DIAO));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_landiao, R.mipmap.ic_filter_landiao, MHSDK.FILTER_LAN_DIAO, MHConfigConstants.MEI_YAN_LV_JING_QING_LIANG));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_rixi, R.mipmap.ic_filter_rixi, MHSDK.FILTER_RI_XI, MHConfigConstants.MEI_YAN_LV_JING_RI_XI));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_chengshi, R.mipmap.ic_filter_chengshi, MHSDK.FILTER_CHENG_SHI, MHConfigConstants.MEI_YAN_LV_JING_CHENG_SHI));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_chulian, R.mipmap.ic_filter_chulian, MHSDK.FILTER_CHU_LIAN, MHConfigConstants.MEI_YAN_LV_JING_CHU_LIAN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_chuxin, R.mipmap.ic_filter_chuxin, MHSDK.FILTER_CHU_XIN, MHConfigConstants.MEI_YAN_LV_JING_CHU_XIN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_danse, R.mipmap.ic_filter_danse, MHSDK.FILTER_DAN_SE, MHConfigConstants.MEI_YAN_LV_JING_DAN_SE));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_fanchase, R.mipmap.ic_filter_fanchase, MHSDK.FILTER_FA_CHA_SE, MHConfigConstants.MEI_YAN_LV_JING_FAN_CHA_SE));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_hupo, R.mipmap.ic_filter_hupo, MHSDK.FILTER_HU_PO, MHConfigConstants.MEI_YAN_LV_JING_HU_PO));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_meiwei, R.mipmap.ic_filter_meiwei, MHSDK.FILTER_MEI_WEI, MHConfigConstants.MEI_YAN_LV_JING_MEI_WEI));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_mitaofen, R.mipmap.ic_filter_mitaofen, MHSDK.FILTER_MI_TAO_FEN, MHConfigConstants.MEI_YAN_LV_JING_MI_TAO_FEN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_naicha, R.mipmap.ic_filter_naicha, MHSDK.FILTER_NAI_CHA, MHConfigConstants.MEI_YAN_LV_JING_NAI_CHA));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_pailide, R.mipmap.ic_filter_pailide, MHSDK.FILTER_PAI_LI_DE, MHConfigConstants.MEI_YAN_LV_JING_PAI_LI_DE));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_wutuobang, R.mipmap.ic_filter_wutuobang, MHSDK.FILTER_WU_TUO_BANG, MHConfigConstants.MEI_YAN_LV_JING_WU_TUO_BANG));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_xiyou, R.mipmap.ic_filter_xiyou, MHSDK.FILTER_XI_YOU, MHConfigConstants.MEI_YAN_LV_JING_XI_YOU));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_filter_riza, R.mipmap.ic_filter_riza, MHSDK.FILTER_RI_ZA, MHConfigConstants.MEI_YAN_LV_JING_RI_ZA));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_heimao, R.mipmap.ic_filter_heimao, MHSDK.FILTER_HEI_MAO, MHConfigConstants.MEI_YAN_LV_JING_HEI_MAO));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_heibai, R.mipmap.ic_filter_heibai, MHSDK.FILTER_HEI_BAI, MHConfigConstants.MEI_YAN_LV_JING_HEI_BAI));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_bulukelin, R.mipmap.ic_filter_bulukelin, MHSDK.FILTER_BU_LU_KE_LIN, MHConfigConstants.MEI_YAN_LV_JING_BU_LU_KE_LIN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_pingjing, R.mipmap.ic_filter_pingjing, MHSDK.FILTER_PING_JING, MHConfigConstants.MEI_YAN_LV_JING_PING_JING));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_lengku, R.mipmap.ic_filter_lengku, MHSDK.FILTER_LENG_KU, MHConfigConstants.MEI_YAN_LV_JING_LENG_KU));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_kaiwen, R.mipmap.ic_filter_kaiwen, MHSDK.FILTER_KAI_WEN, MHConfigConstants.MEI_YAN_LV_JING_KAI_WEN));
        beans.add(new MeiYanFilterBean(R.string.beauty_mh_pro_filter_lianai, R.mipmap.ic_filter_lianai, MHSDK.FILTER_LIAN_AI, MHConfigConstants.MEI_YAN_LV_JING_LIAN_AI));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.MEI_YAN, MHConfigConstants.MEI_YAN_LV_JING_FUNCTION);
        MeiYanDataBean meiYanDataBean = MhDataManager.getInstance().getMeiYanDataBean();
        List<MeiYanFilterBean> list = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            MeiYanFilterBean bean = (MeiYanFilterBean) beans.get(i);
            list.add(bean);
            if (meiYanDataBean != null) {
                if (bean.getFilterRes() == meiYanDataBean.getFilterId()) {
                    bean.setChecked(true);
                }
            } else {
                //第一次进来的话，默认第一个选中
                if (i == 0) bean.setChecked(true);
            }
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        MhMeiYanFilterAdapter adapter = new MhMeiYanFilterAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(MeiYanFilterBean bean, int position) {
        MhDataManager.getInstance().setFilter(bean.getFilterRes());
    }


    @Override
    public void showSeekBar() {
        if (mActionListener != null) {
            mActionListener.changeProgress(false, 0, 0, 0);
        }
    }


}
