package com.meihu.beauty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.bean.TieZhiTypeBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;

import java.util.List;

public class TieZhiTitleAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<TieZhiTypeBean> mList;
    private int mColor0;
    private int mColor1;
    private int mCheckedPosition;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<TieZhiTypeBean> mOnItemClickListener;

    public TieZhiTitleAdapter(Context context, List<TieZhiTypeBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.textColor3);
        mColor1 = ContextCompat.getColor(context, R.color.global);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mList.get(position), position);
                }
            }
        };
    }


    public void setCheckedPosition(int position) {
        if (position != mCheckedPosition) {
            mList.get(position).setChecked(true);
            mList.get(mCheckedPosition).setChecked(false);
            notifyItemChanged(position, Constants.PAYLOAD);
            notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
            mCheckedPosition = position;
        }
    }

    public void setOnItemClickListener(OnItemClickListener<TieZhiTypeBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_tiezhi_title, viewGroup, false));
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

        TextView mName;
        View mIconPro;
        ImageView close;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mIconPro = itemView.findViewById(R.id.icon_pro);
            close = itemView.findViewById(R.id.close);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TieZhiTypeBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
//                if (bean.getId() == MHSDK.TIEZHI_NOONE){
//                        close.setVisibility(View.VISIBLE);
//                        return;
//                }
//                close.setVisibility(View.GONE);
                mName.setText(bean.getName());
                if (bean.isAdvance()) {
                    if (mIconPro.getVisibility() != View.VISIBLE) {
                        mIconPro.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mIconPro.getVisibility() == View.VISIBLE) {
                        mIconPro.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mName.setTextColor(bean.isChecked() ? mColor1 : mColor0);

        }
    }

}
