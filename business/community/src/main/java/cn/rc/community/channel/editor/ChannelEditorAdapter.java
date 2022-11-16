package cn.rc.community.channel.editor;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;
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
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;

public class ChannelEditorAdapter extends RcyAdapter<ListBean, RcyHolder> implements IDragCallback {


    public ChannelEditorAdapter(Activity context) {
        super(context, R.layout.item_channel_group_title, R.layout.item_channel_manager);
    }


    @Override
    public int getItemLayoutId(ListBean item, int position) {
        if (item instanceof GroupBean) {
            return R.layout.item_channel_group_title;
        } else {
            return R.layout.item_channel_manager;
        }
    }

    @Override
    public void convert(RcyHolder holder, ListBean listBean, int position, int layoutId) {
        if (layoutId == R.layout.item_channel_group_title) {
            convertGroup(holder, (GroupBean) listBean, position);
        } else {
            convertChannel(holder, (ChannelBean) listBean, position);
        }
    }

    /**
     * 频道
     *
     * @param holder
     * @param channelBean
     * @param position
     */
    private void convertChannel(RcyHolder holder, ChannelBean channelBean, int position) {
        ImageView type = holder.getView(R.id.left);
        holder.setText(R.id.name, channelBean.getName());
        type.setImageResource(R.drawable.svg_channel_text_black);
        type.setBackgroundResource(R.drawable.bg_unread_gray_light);
        holder.setOnClickListener(R.id.iv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogBuilder((Activity) context)
                        .setMessage("确定要删除 " + channelBean.name + "吗？")
                        .setEnableCancel(true)
                        .setSureBtnStyle(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onDeleteChannel(channelBean);
                            }
                        }).build().show();
            }
        });
        holder.setOnClickListener(R.id.iv_editor, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(context);
                editText.setBackground(ResUtil.getDrawable(R.drawable.shape_channel_type));
                editText.setHint(channelBean.name);
                editText.setPadding(20, 30, 20, 30);
                new DialogBuilder((Activity) context)
                        .setCustomerView(editText)
                        .setEnableCancel(true)
                        .setSureBtnStyle(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = editText.getText().toString().trim();
                                if (!TextUtils.isEmpty(name)) {
                                    channelBean.setName(name);
                                    notifyDataSetChanged();
                                } else {
                                    KToast.show("请输入新的名称");
                                }
                            }
                        }).build().show();
            }
        });
    }

    /**
     * 分组标题
     *
     * @param holder
     * @param groupBean
     * @param position
     */
    private void convertGroup(RcyHolder holder, GroupBean groupBean, int position) {
        holder.setText(R.id.name, groupBean.getName());
    }


    @Override
    public boolean itemTouchOnMove(int from, int to) {
        Logger.e("ChannelEditorAdapter", "onMove: from = " + from + " to = " + to);
        List<ListBean> list = getData();
        int count = null != list ? list.size() : 0;
        if (count > Math.max(from, to)) {
            if (list.get(from) instanceof GroupBean) {
                return false;
            }
            swap(list, from, to);
        }
        return false;
    }

    @Override
    public void onMoveComplete() {
//        notifyDataSetChanged();
    }

    public void swap(List<ListBean> list, int from, int to) {
        Logger.e("ChannelEditorAdapter", "swap: from = " + from + " to = " + to);
        ListBean temp = list.get(from);
        list.set(from, list.get(to));
        list.set(to, temp);
        notifyItemMoved(from, to);
        for (int i = 0; i < list.size(); i++) {
            ListBean t = list.get(i);
            Logger.e("ChannelEditorAdapter", "index " + i + " name = " + t.name);
        }
    }

    public void onDeleteChannel(ChannelBean channelBean) {
        OkApi.post(CommunityAPI.Community_delete_channel + channelBean.uid, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_delete_channel_success));
                    getData().remove(channelBean);
                    notifyDataSetChanged();
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }
}
