package com.meihu.beauty.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhMakeupAdapter;
import com.meihu.beauty.bean.MakeupBean;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;

public class MhMakeupViewHolder extends AbsMhChildViewHolder implements View.OnClickListener, OnItemClickListener<MakeupBean> {

    private boolean mMakeupLipstick;
    private boolean mMakeupEyelash;
    private boolean mMakeupEyeliner;
    private boolean mMakeupEyebrow;
    private boolean mMakeupBlush;

    public MhMakeupViewHolder(Context context, ViewGroup parentView) {
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
        return R.layout.view_beauty_mh_makeup;
    }

    @Override
    public void init() {

//        findViewById(R.id.btn_hide).setOnClickListener(this);

        List<MHCommonBean> beans = new ArrayList<>();
        beans.add(new MakeupBean(R.string.beauty_mh_makeup_none, R.mipmap.makeup_drawing_default, R.mipmap.makeup_drawing_sele, MHSDK.MAKEUP_NONE, MHConfigConstants.MEI_ZHUANG_YUAN_TU));
        beans.add(new MakeupBean(R.string.beauty_mh_makeup_jiemao, R.mipmap.makeup_eyelash_default, R.mipmap.makeup_eyelash_sele, MHSDK.MAKEUP_EYELASH, MHConfigConstants.MEI_ZHUANG_JIE_MAO));
        beans.add(new MakeupBean(R.string.beauty_mh_makeup_chuncai, R.mipmap.makeup_lipstick_default, R.mipmap.makeup_lipstick_sele, MHSDK.MAKEUP_LIPSTICK, MHConfigConstants.MEI_ZHUANG_CHUN_CAI));
        beans.add(new MakeupBean(R.string.beauty_mh_makeup_saihong, R.mipmap.makeup_blush_default, R.mipmap.makeup_blush_sele, MHSDK.MAKEUP_BLUSH, MHConfigConstants.MEI_ZHUANG_SAI_HONG));
//        list.add(new MakeupBean(R.string.beauty_mh_makeup_yanxian, R.mipmap.ic_makeup_yanxian_0, R.mipmap.ic_makeup_yanxian_1,MHSDK.MAKEUP_EYELINER));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.MEI_ZHUANG, MHConfigConstants.MEI_ZHUANG_FUNCTION);

        List<MakeupBean> list = new ArrayList<>();
        MeiYanDataBean meiYanDataBean = MhDataManager.getInstance().getMeiYanDataBean();
        Logger.e("meiYanDataBean = " + GsonUtil.obj2Json(meiYanDataBean));
        for (int i = 0; i < beans.size(); i++) {
            MakeupBean bean = (MakeupBean) beans.get(i);
            //美妆状态为上次默认选中的
            if (meiYanDataBean != null)
                switch (bean.getMakeupId()) {
                    case MHSDK.MAKEUP_NONE:
                        break;
                    case MHSDK.MAKEUP_EYELASH:
                        bean.setChecked(meiYanDataBean.isMakeupEyelash());
                        break;
                    case MHSDK.MAKEUP_LIPSTICK:
                        bean.setChecked(meiYanDataBean.isMakeupLipstick());
                        break;
                    case MHSDK.MAKEUP_BLUSH:
                        bean.setChecked(meiYanDataBean.isMakeupBlush());
                        break;
                }
            list.add(bean);
        }

        RecyclerView recyclerView = findViewById(R.id.makeup_recyclerView);
        if (list.size() > 0) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, list.size(), GridLayoutManager.VERTICAL, false));
        }
        MhMakeupAdapter adapter = new MhMakeupAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
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

    private boolean getMakeupEnable() {
        return mMakeupLipstick || mMakeupEyelash || mMakeupEyeliner || mMakeupEyebrow || mMakeupBlush;
    }

    private void setMakeupEnable(MakeupBean bean) {
        boolean enable = bean.isChecked();
        switch (bean.getMakeupId()) {
            case MHSDK.MAKEUP_NONE:
                mMakeupLipstick = false;
                mMakeupEyelash = false;
                mMakeupEyeliner = false;
                mMakeupEyebrow = false;
                mMakeupBlush = false;
                break;
            case MHSDK.MAKEUP_LIPSTICK:
                mMakeupLipstick = enable;
                break;
            case MHSDK.MAKEUP_EYELASH:
                mMakeupEyelash = enable;
                break;
            case MHSDK.MAKEUP_EYELINER:
                mMakeupEyeliner = enable;
                break;
            case MHSDK.MAKEUP_EYEBROW:
                mMakeupEyebrow = enable;
                break;
            case MHSDK.MAKEUP_BLUSH:
                mMakeupBlush = enable;
                break;
        }
    }

    @Override
    public void onItemClick(MakeupBean bean, int position) {

        int useFace = 0;
        if (bean.getMakeupId() == MHSDK.MAKEUP_NONE) {
            useFace = 0;
        } else {
            setMakeupEnable(bean);
            boolean enable = getMakeupEnable();
            if (enable) {
                useFace = 1;
            } else {
                useFace = 0;
            }
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[5] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }

        MhDataManager.getInstance().setMakeup(bean.getMakeupId(), bean.isChecked());
        saveState(bean);
    }

    void saveState(MakeupBean bean) {
        MeiYanDataBean dataBean = MhDataManager.getInstance().getMeiYanDataBean();
        if (null != dataBean) {
            switch (bean.getMakeupId()) {
                case MHSDK.MAKEUP_NONE:
                    dataBean.setMakeupBlush(false);
                    dataBean.setMakeupEyelash(false);
                    dataBean.setMakeupLipstick(false);
                    break;
                case MHSDK.MAKEUP_EYELASH:
                    dataBean.setMakeupEyelash(true);
                    break;
                case MHSDK.MAKEUP_LIPSTICK:
                    dataBean.setMakeupLipstick(true);
                    break;
                case MHSDK.MAKEUP_BLUSH:
                    dataBean.setMakeupBlush(true);
                    break;
            }
        }
    }
}
