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
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;

public class MhMeiYanShapeViewHolder extends MhMeiYanChildViewHolder implements OnItemClickListener<MeiYanBean> {

    private MhMeiYanAdapter mAdapter;

    public MhMeiYanShapeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();

        beans.add(new MeiYanBean(R.string.beauty_mh_no, R.mipmap.beauty_btn_drawing_default, R.mipmap.beauty_btn_originaldrawing, MHConfigConstants.MEI_YAN_MEI_XING_YUAN_TU));
        beans.add(new MeiYanBean(R.string.beauty_mh_dayan, R.mipmap.beauty_btn_eye_default, R.mipmap.beauty_btn_eye_sele, MHConfigConstants.MEI_YAN_MEI_XING_DA_YAN));
        beans.add(new MeiYanBean(R.string.beauty_mh_shoulian, R.mipmap.beauty_btn_face_default, R.mipmap.beauty_btn_face_sele, MHConfigConstants.MEI_YAN_MEI_XING_SHOU_LIAN));
        beans.add(new MeiYanBean(R.string.beauty_mh_zuixing, R.mipmap.beauty_btn_mouth_default, R.mipmap.beauty_btn_mouth_sele, MHConfigConstants.MEI_YAN_MEI_XING_ZUI_XING));
        beans.add(new MeiYanBean(R.string.beauty_mh_shoubi, R.mipmap.beauty_btn_thinnose_default, R.mipmap.beauty_btn_thinnose_sele, MHConfigConstants.MEI_YAN_MEI_XING_SHOU_BI));
        beans.add(new MeiYanBean(R.string.beauty_mh_xiaba, R.mipmap.beauty_btn_chin_default, R.mipmap.beauty_btn_chin_sele, MHConfigConstants.MEI_YAN_MEI_XING_XIA_BA));
        beans.add(new MeiYanBean(R.string.beauty_mh_etou, R.mipmap.beauty_btn_forehead_default, R.mipmap.beauty_btn_forehead_sele, MHConfigConstants.MEI_YAN_MEI_XING_E_TOU));
        beans.add(new MeiYanBean(R.string.beauty_mh_meimao, R.mipmap.beauty_btn_eyebrow_default, R.mipmap.beauty_btn_eyebrow_sele, MHConfigConstants.MEI_YAN_MEI_XING_MEI_MAO));
        beans.add(new MeiYanBean(R.string.beauty_mh_yanjiao, R.mipmap.beauty_btn_canth_default, R.mipmap.beauty_btn_canth_sele, MHConfigConstants.MEI_YAN_MEI_XING_YAN_JIAO));
        beans.add(new MeiYanBean(R.string.beauty_mh_yanju, R.mipmap.beauty_btn_eyespan_default, R.mipmap.beauty_btn_eyespan_sele, MHConfigConstants.MEI_YAN_MEI_XING_YAN_JU));
        beans.add(new MeiYanBean(R.string.beauty_mh_kaiyanjiao, R.mipmap.beauty_btn_openeye_default, R.mipmap.beauty_btn_openeye_sele, MHConfigConstants.MEI_YAN_MEI_XING_KAI_YAN_JIAO));
        beans.add(new MeiYanBean(R.string.beauty_mh_xuelian, R.mipmap.beauty_btn_cutface_default, R.mipmap.beauty_btn_cutface_sele, MHConfigConstants.MEI_YAN_MEI_XING_XUE_LIAN));
        beans.add(new MeiYanBean(R.string.beauty_mh_changbi, R.mipmap.beauty_btn_longnose_default, R.mipmap.beauty_btn_longnose_sele, MHConfigConstants.MEI_YAN_MEI_XING_CHANG_BI));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.MEI_YAN, MHConfigConstants.MEI_YAN_MEI_XING_FUNCION);

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

        int useFace;

        if (name == R.string.beauty_mh_no) {
            useFace = 0;
        } else {
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[1] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }

        if (name == R.string.beauty_mh_no) {
            mActionListener.changeProgress(false, 0, 0, 0);
            valueBean.setDaYan(0);
            valueBean.setMeiMao(0);
            valueBean.setYanJu(0);
            valueBean.setYanJiao(0);
            valueBean.setShouLian(0);
            valueBean.setZuiXing(0);
            valueBean.setShouBi(0);
            valueBean.setXiaBa(0);
            valueBean.setETou(0);
            valueBean.setChangBi(0);
            valueBean.setXueLian(0);
            valueBean.setKaiYanJiao(0);
            MhDataManager.getInstance().useMeiYan().notifyMeiYanChanged();
        } else if (name == R.string.beauty_mh_dayan) {
            mActionListener.changeProgress(true, 100, valueBean.getDaYan(), name);
        } else if (name == R.string.beauty_mh_meimao) {
            mActionListener.changeProgress(true, 100, valueBean.getMeiMao(), name);
        } else if (name == R.string.beauty_mh_yanju) {
            mActionListener.changeProgress(true, 100, valueBean.getYanJu(), name);
        } else if (name == R.string.beauty_mh_yanjiao) {
            mActionListener.changeProgress(true, 100, valueBean.getYanJiao(), name);
        } else if (name == R.string.beauty_mh_shoulian) {
            mActionListener.changeProgress(true, 100, valueBean.getShouLian(), name);
        } else if (name == R.string.beauty_mh_zuixing) {
            mActionListener.changeProgress(true, 100, valueBean.getZuiXing(), name);
        } else if (name == R.string.beauty_mh_shoubi) {
            mActionListener.changeProgress(true, 100, valueBean.getShouBi(), name);
        } else if (name == R.string.beauty_mh_xiaba) {
            mActionListener.changeProgress(true, 100, valueBean.getXiaBa(), name);
        } else if (name == R.string.beauty_mh_etou) {
            mActionListener.changeProgress(true, 100, valueBean.getETou(), name);
        } else if (name == R.string.beauty_mh_changbi) {
            mActionListener.changeProgress(true, 100, valueBean.getChangBi(), name);
        } else if (name == R.string.beauty_mh_xuelian) {
            mActionListener.changeProgress(true, 100, valueBean.getXueLian(), name);
        } else if (name == R.string.beauty_mh_kaiyanjiao) {
            mActionListener.changeProgress(true, 100, valueBean.getKaiYanJiao(), name);
        }
    }


    @Override
    public void onProgressChanged(float rate, int progress) {
        if (mAdapter == null) {
            return;
        }
        int name = mAdapter.getCheckedName();
        if (name == R.string.beauty_mh_dayan) {
            MhDataManager.getInstance().setDaYan(progress);
        } else if (name == R.string.beauty_mh_meimao) {
            MhDataManager.getInstance().setMeiMao(progress);
        } else if (name == R.string.beauty_mh_yanju) {
            MhDataManager.getInstance().setYanJu(progress);
        } else if (name == R.string.beauty_mh_yanjiao) {
            MhDataManager.getInstance().setYanJiao(progress);
        } else if (name == R.string.beauty_mh_shoulian) {
            MhDataManager.getInstance().setShouLian(progress);
        } else if (name == R.string.beauty_mh_zuixing) {
            MhDataManager.getInstance().setZuiXing(progress);
        } else if (name == R.string.beauty_mh_shoubi) {
            MhDataManager.getInstance().setShouBi(progress);
        } else if (name == R.string.beauty_mh_xiaba) {
            MhDataManager.getInstance().setXiaBa(progress);
        } else if (name == R.string.beauty_mh_etou) {
            MhDataManager.getInstance().setETou(progress);
        } else if (name == R.string.beauty_mh_changbi) {
            MhDataManager.getInstance().setChangBi(progress);
        } else if (name == R.string.beauty_mh_xuelian) {
            MhDataManager.getInstance().setXueLian(progress);
        } else if (name == R.string.beauty_mh_kaiyanjiao) {
            MhDataManager.getInstance().setKaiYanJiao(progress);
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
