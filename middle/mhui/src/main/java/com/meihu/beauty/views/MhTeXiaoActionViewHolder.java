package com.meihu.beauty.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meihu.beauty.R;
import com.meihu.beauty.adapter.MhTeXiaoActionAdapter;
import com.meihu.beauty.bean.MeiYanDataBean;
import com.meihu.beauty.bean.TeXiaoActionBean;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.ToastUtil;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.constant.ResourceUrl;
import com.meihu.beautylibrary.manager.MHBeautyManager;

import java.util.ArrayList;
import java.util.List;

public class MhTeXiaoActionViewHolder extends MhTeXiaoChildViewHolder implements OnItemClickListener<TeXiaoActionBean> {

    private final String TAG = MhTeXiaoActionViewHolder.class.getName();
    private MhTeXiaoActionAdapter adapter;


    public MhTeXiaoActionViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {

        List<MHCommonBean> beans = new ArrayList<>();

        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_no, R.mipmap.ic_texiao_action_no_0, R.mipmap.ic_texiao_action_no_1, "", MHSDK.TE_XIAO_ACTION_NONE, MHConfigConstants.TE_XIAO_DONG_ZUO_YUAN_TU, ""));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_head, R.mipmap.ic_texiao_action_taitou_0, R.mipmap.ic_texiao_action_taitou_1, "", MHSDK.TE_XIAO_ACTION_TAI_TOU, MHConfigConstants.TE_XIAO_DONG_ZUO_TAI_TOU, "face_057"));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_mouth, R.mipmap.ic_texiao_action_zhangzui_0, R.mipmap.ic_texiao_action_zhangzui_1, "", MHSDK.TE_XIAO_ACTION_ZHANG_ZUI, MHConfigConstants.TE_XIAO_DONG_ZUO_ZHANG_ZUI, "face_053"));
        beans.add(new TeXiaoActionBean(R.string.beauty_mh_texiao_action_eye, R.mipmap.ic_texiao_action_zhayan_0, R.mipmap.ic_texiao_action_zhayan_1, "", MHSDK.TE_XIAO_ACTION_ZHA_YAN, MHConfigConstants.TE_XIAO_DONG_ZUO_ZHA_YAN, "face_056"));

        beans = MHSDK.getFunctionItems(beans, MHConfigConstants.TE_XIAO, MHConfigConstants.TE_XIAO_DONG_ZUO_FUNCTION);

        List<TeXiaoActionBean> list = new ArrayList<>();
        MeiYanDataBean meiYanDataBean = MhDataManager.getInstance().getMeiYanDataBean();
        for (int i = 0; i < beans.size(); i++) {
            TeXiaoActionBean bean = (TeXiaoActionBean) beans.get(i);
            if (meiYanDataBean != null) {
                if (bean.getAction() == meiYanDataBean.getTieZhiAction()) {
                    bean.setChecked(true);
                }
            } else {
                //第一次进来的话，默认第一个选中
                if (i == 0) bean.setChecked(true);
            }
            list.add(bean);
        }

        RecyclerView recyclerView = (RecyclerView) mContentView;
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        adapter = new MhTeXiaoActionAdapter(mContext, list);
        adapter.setOnItemClickListener(this);
        adapter.setOnTieZhiActionClickListener(new OnTieZhiActionClickListener() {
            @Override
            public void OnTieZhiActionClick(int action) {
                MeiYanDataBean dataBean = MhDataManager.getInstance().getMeiYanDataBean();
                if (null != dataBean) dataBean.setTieZhiAction(action);
                if (mOnTieZhiActionClickListener != null) {
                    mOnTieZhiActionClickListener.OnTieZhiActionClick(action);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void setItemClick(int postion) {
        if (adapter != null) {
            adapter.setItemClick(postion);
        } else {
            Log.e(TAG, "setItemClick: ");
        }
    }

    @Override
    public void onItemClick(TeXiaoActionBean bean, int position) {
        int action = bean.getAction();
        if (action == 0) {
            enableUseFace(null);
            MhDataManager.getInstance().setTieZhi(null, null);
            if (mOnTieZhiActionListener != null) {
                mOnTieZhiActionListener.OnTieZhiAction(bean.getAction());
            }
        } else {
            String stickerName = bean.getStickerName();
            if (MhDataManager.isTieZhiDownloaded(stickerName)) {
                setTieZhi(bean, stickerName);
            } else {
                requestSticker(bean);
            }
        }
    }

    private void requestSticker(final TeXiaoActionBean bean) {

        if (mOnTieZhiActionDownloadListener != null) {
            mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(0);
        }

        MhDataManager.getTieZhiList(ResourceUrl.STICKER_THUMB_LIST_URL5, new CommonCallback<String>() {
            @Override
            public void callback(String jsonStr) {
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    JSONObject obj = JSON.parseObject(jsonStr);
                    List<TieZhiBean> list = JSON.parseArray(obj.getString("list"), TieZhiBean.class);
//                            if (list != null && list.size() > 0) {
//                                if (list.size() == 1){
//                                    String  actionStickerName = list.get(0).getName();
//                                    String resouce = list.get(0).getResource();
//                                    MhDataManager.getInstance().setActionStickerName(actionStickerName);
//                                    bean.setStickerName(actionStickerName);
//                                    bean.setResouce(resouce);
//                                    downloadSticker(bean);
//                                }
//                            }
                    if (list != null) {
                        for (TieZhiBean item : list) {
                            if (bean.getStickerName().equals(item.getName())) {
                                String name = item.getName();
                                String resouce = item.getResource();
                                bean.setStickerName(name);
                                bean.setResouce(resouce);
                                downloadSticker(bean);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadSticker(final TeXiaoActionBean bean) {
        final String stickerName = bean.getStickerName();
        final String resource = bean.getResouce();
        MhDataManager.downloadTieZhi(stickerName, resource, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean isSuccess) {
                if (isSuccess) {
                    setTieZhi(bean, stickerName);
                    if (mOnTieZhiActionListener != null) {
                        mOnTieZhiActionListener.OnTieZhiAction(bean.getAction());
                    }
                } else {
                    if (mOnTieZhiActionDownloadListener != null) {
                        mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(1);
                    }
                    ToastUtil.show(R.string.beauty_mh_009);
                }
            }
        });
    }

    private void setTieZhi(TeXiaoActionBean bean, String stickerName) {
        enableUseFace(stickerName);
        MhDataManager.getInstance().setTieZhi(stickerName, bean.getAction());
        if (mOnTieZhiActionDownloadListener != null) {
            mOnTieZhiActionDownloadListener.OnTieZhiActionDownload(1);
        }
        if (mOnTieZhiActionListener != null) {
            mOnTieZhiActionListener.OnTieZhiAction(bean.getAction());
        }
    }

    private void enableUseFace(String stickerName) {
        int useFace;
        if (TextUtils.isEmpty(stickerName)) {
            useFace = 0;
        } else {
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[4] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }
    }

}
