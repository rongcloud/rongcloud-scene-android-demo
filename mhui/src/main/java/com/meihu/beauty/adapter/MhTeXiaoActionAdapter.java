package com.meihu.beauty.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.bean.TeXiaoActionBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.utils.DpUtil;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;

import java.util.List;

public class MhTeXiaoActionAdapter extends RecyclerView.Adapter {

    private final String TAG = MhTeXiaoActionAdapter.class.getName();
    private LayoutInflater mInflater;
    private List<TeXiaoActionBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition = -1;
    private int mColor0;
    private int mColor1;
    private OnItemClickListener<TeXiaoActionBean> mOnItemClickListener;
    private OnTieZhiActionClickListener mOnTieZhiActionClickListener;


    public MhTeXiaoActionAdapter(Context context, List<TeXiaoActionBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.textColor2);
        mColor1 = ContextCompat.getColor(context, R.color.global);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                mCheckedPosition = i;
            }
        }
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                TeXiaoActionBean bean = mList.get(position);
                if (mOnTieZhiActionClickListener != null) {
                    mOnTieZhiActionClickListener.OnTieZhiActionClick(bean.getAction());
                }
                setItemClick(position);
            }
        };
    }

    public void setOnTieZhiActionClickListener(OnTieZhiActionClickListener onTieZhiActionClickListener) {
        mOnTieZhiActionClickListener = onTieZhiActionClickListener;
    }

    public void setItemClick(int position) {
        if (position != mCheckedPosition) {
            TeXiaoActionBean bean = mList.get(position);
            bean.setChecked(true);
            notifyItemChanged(position, Constants.PAYLOAD);
            if (mCheckedPosition >= 0) {
                mList.get(mCheckedPosition).setChecked(false);
                notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
            }
            mCheckedPosition = position;
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(bean, position);
            }
        } else {
            Log.e(TAG, "setItemClick: ");
        }
    }

    public void setOnItemClickListener(OnItemClickListener<TeXiaoActionBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public int getCheckedName() {
        if (mCheckedPosition == -1) {
            return -1;
        }
        return mList.get(mCheckedPosition).getName();
    }

    public TeXiaoActionBean getCheckedBean() {
        if (mCheckedPosition == -1) {
            return null;
        }
        return mList.get(mCheckedPosition);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_4, viewGroup, false));
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

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TeXiaoActionBean bean, int position, Object payload) {
            String name = WordUtil.getString(MhDataManager.getInstance().getContext(), bean.getName());
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(name);
            }
            if (bean.isChecked()) {
                mName.setTextColor(mColor1);
                mThumb.setImageDrawable(bean.getDrawable1());
            } else {
                mName.setTextColor(mColor0);
                mThumb.setImageDrawable(bean.getDrawable0());
            }

            if (TextUtils.isEmpty(name)) {
                mName.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mThumb.getLayoutParams();
                layoutParams.setMargins(0, DpUtil.dp2px(-10), 0, 0);
                mThumb.setLayoutParams(layoutParams);
            } else {
                mName.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mThumb.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                mThumb.setLayoutParams(layoutParams);
            }

        }

    }
}
