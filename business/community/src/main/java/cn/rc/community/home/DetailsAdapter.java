package cn.rc.community.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;
import cn.rc.community.conversion.sdk.UltraGroupApi;
import cn.rc.community.helper.CommunityHelper;

public class DetailsAdapter extends RcyAdapter<ListBean, RcyHolder> {

    private OnDetailsClickListener listener;
    private String targetId;
    //布局缓存类，方便刷新 
    private HashMap<String, RcyHolder> holderMaps = new HashMap<>();

    public DetailsAdapter(Context context, OnDetailsClickListener listener, String targetId) {
        super(context,
                R.layout.item_community_group,
                R.layout.item_channel);
        this.listener = listener;
        this.targetId = targetId;
    }

    @Override
    public synchronized void setData(List<ListBean> list, boolean refresh) {
        //每次新的数据源刷新之前都直接更新缓存
        holderMaps.clear();
        super.setData(list, refresh);
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @Override
    public int getItemLayoutId(ListBean item, int position) {
        return item instanceof GroupBean ? R.layout.item_community_group : R.layout.item_channel;
    }

    @Override
    public void convert(RcyHolder holder, ListBean listBean, int position, int layoutId) {
        //先看看是否已经有缓存数据了，创建一个所属于的分组
        if (R.layout.item_community_group == layoutId) {
            convertGroup(holder, (GroupBean) listBean, position);
        } else if (R.layout.item_channel == layoutId) {
            //如果直接是频道，那么缓存好当前的就可以了
            holderMaps.put(listBean.getUid(), holder);
            convertChannel(holder, (ChannelBean) listBean, position);
        }
    }


    /**
     * @param holder
     * @param groupBean
     * @param position  分组的位置
     */
    void convertGroup(RcyHolder holder, GroupBean groupBean, int position) {
        holder.setText(R.id.info, groupBean.name);
        View left = holder.getView(R.id.left);
        left.setSelected(groupBean.isExpansion());
        RecyclerView rc_channel = holder.getView(R.id.rc_channel);
        if (groupBean.isExpansion()) {
            listChannel(rc_channel, groupBean);
        } else {
            rc_channel.setVisibility(View.GONE);
        }
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupBean.setExpansion(!groupBean.isExpansion());
                view.setSelected(groupBean.isExpansion());
                rc_channel.setVisibility(groupBean.isExpansion() ? View.VISIBLE : View.GONE);
                if (groupBean.getChannelList() != null && groupBean.getChannelList().size() > 0 && rc_channel.getVisibility() == View.VISIBLE) {
                    listChannel(rc_channel, groupBean);
                }
            }
        });
        holder.setVisible(R.id.group_add, CommunityHelper.getInstance().isCreator());
        holder.setOnClickListener(R.id.group_add, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) listener.onAddChannel(groupBean);
            }
        });
        holder.setOnLongClickListener(R.id.ll_group, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != listener && CommunityHelper.getInstance().isCreator())
                    listener.onEditorChannel(true);
                return false;
            }
        });
    }

    /**
     * @param holder
     * @param channelBean
     * @param position    频道在分组的位置
     */
    public void convertChannel(RcyHolder holder, ChannelBean channelBean, int position) {
        TextView channel = holder.getView(R.id.info);
        channel.setText(channelBean.name);
        UIKit.setBoldText(channel, true);
        UltraGroupApi.getApi().getChannelUnreadCount(targetId, channelBean.getUid(), new IResultBack<Integer>() {
            @Override
            public void onResult(Integer integer) {
                int count = null == integer ? 0 : integer;
                holder.setVisible(R.id.count, count > 0);
                holder.setText(R.id.count, count + "");
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != listener && CommunityHelper.getInstance().isCreator())
                    listener.onEditorChannel(false);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) listener.jumpChannel(channelBean);
            }
        });

    }

    /**
     * 分组下面的频道
     *
     * @param recyclerView
     * @param groupBean
     */
    void listChannel(RecyclerView recyclerView, GroupBean groupBean) {
        if (null != recyclerView) {
            recyclerView.setVisibility(View.VISIBLE);
            if (null == recyclerView.getAdapter()) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                ChannelUnderGroupAdapter adapter = new ChannelUnderGroupAdapter(context, R.layout.item_channel, new OnConvertListener<ChannelBean>() {
                    @Override
                    public void onConvert(RcyHolder holder, ChannelBean s, int position) {
                        holderMaps.put(s.getUid(), holder);
                        convertChannel(holder, s, position);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
            ((RcySAdapter) recyclerView.getAdapter()).setData(groupBean.getChannelList(), true);
        }
    }

    /**
     * 实时刷新每个频道未读消息数量
     *
     * @param channelId
     */
    public void updateUnreadCount(String channelId, int count) {
        //找到当前消息的未读数量，去刷新当前消息所在的频道
        for (Map.Entry<String, RcyHolder> stringRcyHolderEntry : holderMaps.entrySet()) {
            if (TextUtils.equals(stringRcyHolderEntry.getKey(), channelId)) {
                RcyHolder rcyHolder = stringRcyHolderEntry.getValue();
                if (rcyHolder != null)
                    rcyHolder.setVisible(R.id.count, count > 0);
                rcyHolder.setText(R.id.count, count + "");
                break;
            }
        }
    }


    public interface OnDetailsClickListener {
        void onAddChannel(GroupBean groupBean);

        void onEditorChannel(boolean isGroup);

        void jumpChannel(ChannelBean channelBean);
    }
}
