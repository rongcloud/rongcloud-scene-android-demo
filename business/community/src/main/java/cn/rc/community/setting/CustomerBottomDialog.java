package cn.rc.community.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.widget.dialog.BasisDialog;

import java.util.List;

import cn.rc.community.R;

/**
 * 底部同一文字item样式的弹框
 */
public class CustomerBottomDialog {
    private final BasisDialog dialog;
    ItemAdapter adapter;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public CustomerBottomDialog(Activity activity, List<String> items) {
        dialog = BasisDialog.bottom(activity, R.layout.layout_customer_bottom_dialog, -1);
        adapter = new ItemAdapter(activity, items);
        adapter.listener = new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (null != listener) listener.onItemClick(position);
                dismiss();
            }
        };
        initView();
    }

    public CustomerBottomDialog setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    private void initView() {
        RecyclerView rcItem = dialog.getView(R.id.rc_items);
        rcItem.setAdapter(adapter);
        dialog.getView(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (null != dialog) {
            dialog.observeDismiss(listener);
        }
    }

    public void show() {
        if (null != dialog) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    private static class ItemAdapter extends RcySAdapter<String, RcyHolder> {
        private OnItemClickListener listener;

        public ItemAdapter(Context context, List<String> list) {
            super(context, R.layout.item_customer_bottom_dialog);
            setData(list, true);
        }

        @Override
        public void convert(RcyHolder holder, String s, int position) {
            holder.setText(R.id.item, s);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != listener) listener.onItemClick(position);
                }
            });
        }
    }
}
