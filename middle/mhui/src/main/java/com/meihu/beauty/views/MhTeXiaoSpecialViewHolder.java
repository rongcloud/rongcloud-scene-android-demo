package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhTeXiaoSpecialAdapter;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.bean.TeXiaoSpecialBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;

import java.util.ArrayList;
import java.util.List;

public class MhTeXiaoSpecialViewHolder extends MhTeXiaoChildViewHolder implements OnItemClickListener<TeXiaoSpecialBean> {

    public MhTeXiaoSpecialViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        List<MHCommonBean> beans = new ArrayList<>();

        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_NONE, R.string.beauty_mh_filter_no, R.mipmap.ic_tx_no, false, MHConfigConstants.TE_XIAO_WU));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_LING_HUN, R.string.beauty_mh_texiao_linghun, R.mipmap.ic_tx_linghun, MHConfigConstants.TE_XIAO_LING_HUN_CHU_QIAO));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_DOU_DONG, R.string.beauty_mh_texiao_doudong, R.mipmap.ic_tx_doudong, MHConfigConstants.TE_XIAO_DOU_DONG));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_SHAN_BAI, R.string.beauty_mh_texiao_shanbai, R.mipmap.ic_tx_shanbai, MHConfigConstants.TE_XIAO_SHAN_BAI));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_MAI_CI, R.string.beauty_mh_texiao_maoci, R.mipmap.ic_tx_maoci, MHConfigConstants.TE_XIAO_MAO_CI));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_HUAN_JUE, R.string.beauty_mh_texiao_huanjue, R.mipmap.ic_tx_huanjue, MHConfigConstants.TE_XIAO_HUAN_JIAO));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_MSK, R.string.beauty_mh_texiao_msk, R.mipmap.ic_tx_msk, MHConfigConstants.TE_XIAO_MA_SAI_KE_1));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_MSK_0, R.string.beauty_mh_texiao_msk_yuan, R.mipmap.ic_tx_msk_yuan, MHConfigConstants.TE_XIAO_MA_SAI_KE_2));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_MSK_3, R.string.beauty_mh_texiao_msk_san, R.mipmap.ic_tx_msk_san, MHConfigConstants.TE_XIAO_MA_SAI_KE_3));
        beans.add(new TeXiaoSpecialBean(MHSDK.SPECIAL_MSK_6, R.string.beauty_mh_texiao_msk_liu, R.mipmap.ic_tx_msk_liu, MHConfigConstants.TE_XIAO_MA_SAI_KE_4));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.TE_XIAO, MHConfigConstants.TE_XIAO_FUNCTION);

        List<TeXiaoSpecialBean> list = new ArrayList<>();
        MeiYanDataBean meiYanDataBean = MhDataManager.getInstance().getMeiYanDataBean();
        for (int i = 0; i < beans.size(); i++) {
            TeXiaoSpecialBean bean = (TeXiaoSpecialBean) beans.get(i);
            if (meiYanDataBean != null) {
                if (bean.getId() == meiYanDataBean.getTeXiaoId()) {
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
        MhTeXiaoSpecialAdapter adapter = new MhTeXiaoSpecialAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(TeXiaoSpecialBean bean, int position) {
        MhDataManager.getInstance().setTeXiao(bean.getId());
        MeiYanDataBean dataBean = MhDataManager.getInstance().getMeiYanDataBean();
        if (null != dataBean) {
            dataBean.setTeXiaoId(bean.getId());
        }
    }


}
