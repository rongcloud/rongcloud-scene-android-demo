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
import com.meihu.beauty.bean.TeXiaoWaterBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;

import java.util.List;

public class MhTeXiaoWaterAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<TeXiaoWaterBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private Drawable mCheckedDrawable;
    private OnItemClickListener<TeXiaoWaterBean> mOnItemClickListener;

    public MhTeXiaoWaterAdapter(Context context, List<TeXiaoWaterBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_water_check);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                mCheckedPosition = i;
            }
        }
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (position != mCheckedPosition) {
                    TeXiaoWaterBean bean = mList.get(position);
                    bean.setChecked(true);
                    mList.get(mCheckedPosition).setChecked(false);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<TeXiaoWaterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_texiao_water, viewGroup, false));
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

        ImageView mThumb;
        View mBg;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mBg = itemView.findViewById(R.id.bg);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TeXiaoWaterBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mThumb.setImageResource(bean.getThumb());
            }
            mBg.setBackground(bean.isChecked() ? mCheckedDrawable : null);
        }

    }
}
