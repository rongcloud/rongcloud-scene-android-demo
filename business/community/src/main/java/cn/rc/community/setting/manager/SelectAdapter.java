package cn.rc.community.setting.manager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.List;

import cn.rc.community.home.ChannelUnderGroupAdapter;
import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;

/**
 * 选择频道适配器
 * 1.根节点的频道
 * 2.分组
 */
public class SelectAdapter extends RcyAdapter<ListBean, RcyHolder> {
    private IResultBack resultBack;

    public SelectAdapter(Context context, IResultBack<ChannelBean> resultBack) {
        super(context, R.layout.item_select_channel_group, R.layout.item_select_channel_channel);
        this.resultBack = resultBack;
    }

    @Override
    public int getItemLayoutId(ListBean item, int position) {
        return item instanceof GroupBean
                ? R.layout.item_select_channel_group
                : R.layout.item_select_channel_channel;
    }

    @Override
    public void convert(RcyHolder holder, ListBean s, int position, int layoutId) {
        if (R.layout.item_select_channel_group == layoutId) {
            convertGroup(holder, (GroupBean) s, position);
        } else if (R.layout.item_select_channel_channel == layoutId) {
            convertChannel(holder, (ChannelBean) s, position);
        }
    }

    public void convertChannel(RcyHolder holder, ChannelBean s, int position) {
        TextView channel = holder.getView(R.id.info);
        channel.setText(s.name);
        UIKit.setBoldText(channel, true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != resultBack) resultBack.onResult(s);
            }
        });
    }

    private String openChannel = "";

    void convertGroup(RcyHolder holder, GroupBean s, int position) {
        holder.setText(R.id.info, s.name);
        View left = holder.getView(R.id.left);
        RecyclerView rc_channel = holder.getView(R.id.rc_channel);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(s.name, openChannel)) {
                    openChannel = "";
                } else {
                    openChannel = s.name;
                }
                if (!TextUtils.isEmpty(openChannel)) {
                    view.setSelected(true);
                    rc_channel.setVisibility(View.VISIBLE);
                    listChannel(rc_channel, s.getChannelList());
                } else {
                    view.setSelected(false);
                    rc_channel.setVisibility(View.GONE);
                }
            }
        });
    }

    void listChannel(RecyclerView recyclerView, List<ChannelBean> channels) {
        if (!TextUtils.isEmpty(openChannel) && null != recyclerView) {
            if (null == recyclerView.getAdapter()) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new ChannelUnderGroupAdapter(context, R.layout.item_select_channel_channel, new OnConvertListener<ChannelBean>() {
                    @Override
                    public void onConvert(RcyHolder holder, ChannelBean s, int position) {
                        convertChannel(holder, s, position);
                    }
                }));
            }
            ChannelUnderGroupAdapter adapter = (ChannelUnderGroupAdapter) recyclerView.getAdapter();
            adapter.setData(channels, true);
        }
    }
}

