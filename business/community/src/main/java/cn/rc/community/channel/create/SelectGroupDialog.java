package cn.rc.community.channel.create;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.UIKit;
import com.basis.widget.dialog.BasisDialog;

import java.util.List;

import cn.rc.community.R;
import cn.rc.community.bean.GroupBean;

/**
 * 创建频道 -> 选择分组
 */
public class SelectGroupDialog {
    private final BasisDialog dialog;
    private GroupAdapter adapter;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SelectGroupDialog(Activity activity, List<GroupBean> groups, int presentY) {
        dialog = BasisDialog.bottom(activity, R.layout.layout_select_group_dialog, presentY);
        adapter = new GroupAdapter(activity, groups);
        adapter.dialog = dialog;
        RecyclerView rcItem = dialog.getView(R.id.rc_group);
        rcItem.setAdapter(adapter);
        UIKit.setBoldText(dialog.getView(R.id.title), true);
        dialog.getView(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public SelectGroupDialog setOnItemClickListener(OnItemClickListener listener) {
        if (null != adapter) adapter.listener = listener;
        return this;
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

    private static class GroupAdapter extends RcySAdapter<GroupBean, RcyHolder> {
        private OnItemClickListener listener;
        private BasisDialog dialog;

        public GroupAdapter(Context context, List<GroupBean> list) {
            super(context, R.layout.item_select_group);
            setData(list, true);
        }

        @Override
        public void convert(RcyHolder holder, GroupBean groupBean, int position) {
            holder.setText(R.id.group_name, groupBean.name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != listener) listener.onItemClick(position);
                    if (null != dialog) dialog.dismiss();
                }
            });
        }
    }
}
