package com.meihu.beauty.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.bean.MeiYanFilterBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;

import java.util.List;

public class MhMeiYanFilterAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<MeiYanFilterBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private int mColor0;
    private int mColor1;
    private OnItemClickListener<MeiYanFilterBean> mOnItemClickListener;

    public MhMeiYanFilterAdapter(Context context, List<MeiYanFilterBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = Color.parseColor("#FF6C6C6C");
        mColor1 = ContextCompat.getColor(context, R.color.textColor2);
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
                    MeiYanFilterBean bean = mList.get(position);
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

    public void setOnItemClickListener(OnItemClickListener<MeiYanFilterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_2, viewGroup, false));
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
        TextView mName;
        View mCheck;
        FrameLayout itemGroup;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mCheck = itemView.findViewById(R.id.check);
            itemGroup = itemView.findViewById(R.id.item_group);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(MeiYanFilterBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(WordUtil.getString(MhDataManager.getInstance().getContext(), bean.getName()));
                mThumb.setImageResource(bean.getThumb());
            }
            if (bean.isChecked()) {
                mName.setTextColor(mColor1);
                if (itemGroup.getVisibility() != View.VISIBLE) {
                    itemGroup.setVisibility(View.VISIBLE);
                }
            } else {
                mName.setTextColor(mColor0);
                if (itemGroup.getVisibility() == View.VISIBLE) {
                    itemGroup.setVisibility(View.INVISIBLE);
                }
            }
        }

    }
}
