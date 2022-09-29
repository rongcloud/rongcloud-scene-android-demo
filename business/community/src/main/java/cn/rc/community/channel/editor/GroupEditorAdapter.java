package cn.rc.community.channel.editor;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.widget.dialog.DialogBuilder;

import java.util.List;

import cn.rc.community.CommunityAPI;
import cn.rc.community.R;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;

/**
 * 分组适配器
 */
public class GroupEditorAdapter extends RcySAdapter<GroupBean, RcyHolder> implements IDragCallback {


    public GroupEditorAdapter(Activity context) {
        super(context, R.layout.item_group_manager);
    }
    

    @Override
    public void convert(RcyHolder holder, GroupBean groupBean, int position) {
        TextView name = holder.getView(R.id.name);
        name.setText(groupBean.name);
        holder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogBuilder((Activity) context)
                        .setMessage("确定要删除 " + groupBean.name + "吗？")
                        .setEnableCancel(true)
                        .setSureBtnStyle(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onDeleteGroup(groupBean);
                            }
                        }).build().show();
            }
        });
        holder.setOnClickListener(R.id.iv_editor, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(context);
                editText.setBackground(ResUtil.getDrawable(R.drawable.shape_channel_type));
                editText.setHint(groupBean.name);
                editText.setPadding(20, 30, 20, 30);
                new DialogBuilder((Activity) context)
                        .setCustomerView(editText)
                        .setEnableCancel(true)
                        .setSureBtnStyle(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = editText.getText().toString().trim();
                                if (!TextUtils.isEmpty(name)) {
                                    groupBean.setName(name);
                                    notifyDataSetChanged();
                                } else {
                                    KToast.show("请输入新的名称");
                                }
                            }
                        }).build().show();
            }
        });
    }

    @Override
    public boolean itemTouchOnMove(int from, int to) {
        Logger.e("ChannelEditorAdapter", "onMove: from = " + from + " to = " + to);
        List<GroupBean> list = getData();
        int count = null != list ? list.size() : 0;
        if (count > Math.max(from, to)) {
            swap(list, from, to);
        }
        return false;
    }

    @Override
    public void onMoveComplete() {
//        notifyDataSetChanged();
    }

    public void swap(List<GroupBean> list, int from, int to) {
        Logger.e("ChannelEditorAdapter", "swap: from = " + from + " to = " + to);
        GroupBean temp = list.get(from);
        list.set(from, list.get(to));
        list.set(to, temp);
        notifyItemMoved(from, to);
        for (int i = 0; i < list.size(); i++) {
            ListBean t = list.get(i);
            Logger.e("ChannelEditorAdapter", "index " + i + " name = " + t.name);
        }
    }

    public void onDeleteGroup(GroupBean groupBean) {
        OkApi.post(CommunityAPI.Community_delete_group + groupBean.uid, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_delete_group_success));
                    getData().remove(groupBean);
                    notifyDataSetChanged();
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }
}
