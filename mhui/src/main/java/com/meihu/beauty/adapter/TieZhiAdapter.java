package com.meihu.beauty.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.bean.TieZhiBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.glide.ImgLoader;
import com.meihu.beauty.interfaces.CommonCallback;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

public class TieZhiAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<TieZhiBean> mList;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<TieZhiBean> mOnItemClickListener;
    private Drawable mCheckDrawable;
    private int mCheckedPosition = -1;


    public TieZhiAdapter(Context context, List<TieZhiBean> list) {
        mContext = context;
        mList = list;

        mList.addAll(Arrays.asList(new TieZhiBean(), new TieZhiBean(), new TieZhiBean(), new TieZhiBean(), new TieZhiBean()));

        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MhDataManager.getInstance().isTieZhiEnable()) {
                    return;
                }
                int position = (int) v.getTag();
                if (mCheckedPosition == position) {
                    return;
                }
                TieZhiBean bean = mList.get(position);
                if (bean.isDownLoaded()) {
                    //修改为贴纸为上次选中的状态
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).isChecked()) {
                            mList.get(i).setChecked(false);
                            notifyItemChanged(i, Constants.PAYLOAD);
                        }
                    }
                    bean.setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
//                    if (mCheckedPosition >= 0) {
//                        mList.get(mCheckedPosition).setChecked(false);
//                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
//                    }
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                } else {
                    bean.setDownLoading(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    final TieZhiBean finalBean = bean;
                    final int finalPosition = position;
                    MhDataManager.downloadTieZhi(bean.getName(), bean.getResource(), new CommonCallback<Boolean>() {
                        @Override
                        public void callback(Boolean isSuccess) {
                            finalBean.setDownLoading(false);
                            finalBean.setDownLoaded(isSuccess);
                            if (isSuccess) {
                                for (int i = 0; i < mList.size(); i++) {
                                    if (mList.get(i).isChecked()) {
                                        mList.get(i).setChecked(false);
                                        notifyItemChanged(i, Constants.PAYLOAD);
                                    }
                                }
                                finalBean.setChecked(true);
//                                if (mCheckedPosition >= 0) {
//                                    mList.get(mCheckedPosition).setChecked(false);
//                                    notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
//                                }
                                mCheckedPosition = finalPosition;
                                if (mOnItemClickListener != null) {
                                    mOnItemClickListener.onItemClick(finalBean, finalPosition);
                                }
                            } else {
                                ToastUtil.show(R.string.beauty_mh_009);
                            }
                            notifyItemChanged(finalPosition, Constants.PAYLOAD);
                        }
                    });
                }
            }
        };
        mCheckDrawable = ContextCompat.getDrawable(context, R.drawable.bg_tiezhi_check);
    }


    public void setCheckedPosition(int position) {
        if (position >= 0 && mList.get(position).getThumb() == null) {
            mList.get(position).setChecked(false);
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isChecked()) {
                mCheckedPosition = i;
            }
        }
        if (mCheckedPosition == position) {
            return;
        }
        if (position >= 0) {
            mList.get(position).setChecked(true);
            notifyItemChanged(position, Constants.PAYLOAD);
        }
        if (mCheckedPosition >= 0) {
            mList.get(mCheckedPosition).setChecked(false);
            notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
        }
        mCheckedPosition = position;
    }


    public void setOnItemClickListener(OnItemClickListener<TieZhiBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_tiezhi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        View mBg;
        ImageView mThumb;
        View mDownArrow;
        View mDownloading;


        public Vh(@NonNull View itemView) {
            super(itemView);
            mBg = itemView.findViewById(R.id.bg);
            mThumb = itemView.findViewById(R.id.thumb);
            mDownArrow = itemView.findViewById(R.id.down_arrow);
            mDownloading = itemView.findViewById(R.id.download_ing);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TieZhiBean bean, int position, Object payload) {

            if (bean.getThumb() == null) {
                mBg.setVisibility(View.INVISIBLE);
                return;
            }

            mBg.setVisibility(View.VISIBLE);

            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
            }
            mBg.setBackground(bean.isChecked() ? mCheckDrawable : null);
            if (bean.isDownLoading()) {
                if (mDownloading.getVisibility() != View.VISIBLE) {
                    mDownloading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mDownloading.getVisibility() == View.VISIBLE) {
                    mDownloading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isDownLoaded()) {
                if (mDownArrow.getVisibility() == View.VISIBLE) {
                    mDownArrow.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mDownArrow.getVisibility() != View.VISIBLE) {
                    mDownArrow.setVisibility(View.VISIBLE);
                }
            }

        }
    }

}
