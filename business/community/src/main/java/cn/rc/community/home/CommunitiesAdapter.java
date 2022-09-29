package cn.rc.community.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;

import java.util.List;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.CommunityBean;
import cn.rc.community.helper.CommunityHelper;

public class CommunitiesAdapter extends RcySAdapter<CommunityBean, RcyHolder> {

    OnItemSelectListener<CommunityBean> listener;
    //选中的社区ID
    private static String selectedUid;

    @Override
    public synchronized void setData(List<CommunityBean> list, boolean refresh) {
        CommunityBean communityBean = new CommunityBean("创建社区", Constants.Add_Action);
        list.add(communityBean);
        super.setData(list, refresh);
        int selectIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(list.get(i).getCommunityUid(), selectedUid)) {
                selectIndex = i;
                break;
            }
        }
        if (listener != null) {
            listener.onSelected(list.get(selectIndex));
        }
    }

    public CommunitiesAdapter(Context context, OnItemSelectListener<CommunityBean> listener) {
        super(context, R.layout.item_community_left);
        this.listener = listener;
    }

    @Override
    public void convert(RcyHolder holder, CommunityBean communityBean, int position) {
        if (position == 0) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.topMargin = 0;
        }
        if (TextUtils.equals(communityBean.getCommunityUid(), Constants.Add_Action)) {
            holder.setImageResource(R.id.icon, R.drawable.svg_add);
            holder.setVisible(R.id.tv_unread, false);
            holder.setVisible(R.id.iv_select, false);
        } else {
            if (TextUtils.equals(selectedUid, communityBean.getCommunityUid())) {
                holder.setVisible(R.id.iv_select, true);
            } else {
                holder.setVisible(R.id.iv_select, false);
            }
            ImageLoader.loadUrl(holder.getView(R.id.icon), communityBean.getPortrait(), R.drawable.cmu_default_portrait);
            holder.setVisible(R.id.tv_unread,
                    communityBean.showRedPoint()
                            && !TextUtils.equals(selectedUid, communityBean.getCommunityUid()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == getItemCount() - 1) {
                    if (null != listener) listener.onCreateCommunity();
                } else {
                    if (null != listener && !TextUtils.equals(communityBean.getCommunityUid(), selectedUid)) {
                        //需要移除掉未加入的社区
                        CommunityBean lastBrowseCommunityBean = CommunityHelper.getInstance().getLastBrowseCommunityBean();
                        if (lastBrowseCommunityBean != null) {
                            int i = getData().indexOf(lastBrowseCommunityBean);
                            if (i > -1) {
                                getData().remove(i);
                                notifyItemRemoved(i);
                            }
                            CommunityHelper.getInstance().clearBrowsingHistory();
                        }
                        communityBean.refreshLastUnReadCount();
                        listener.onSelected(communityBean);
                    }
                }
            }
        });
    }

    /**
     * 选中item
     *
     * @param position
     */
    public void selectItem(int position) {
        CommunityBean communityBean = getData().get(position);
        selectedUid = communityBean.getCommunityUid();
        // 处理红点切走后又显示的问题
        communityBean.refreshLastUnReadCount();
        notifyDataSetChanged();
    }

    /**
     * 获取当前已经存在的
     *
     * @return
     */
    public String getSelectedUid() {
        return selectedUid;
    }

    /**
     * 设置选中的社区ID
     *
     * @param id
     */
    public static void setSelectedUid(String id) {
        selectedUid = id;
    }

    public interface OnItemSelectListener<T> {
        void onSelected(T item);

        default void onCreateCommunity() {
            KToast.show("创建社区");
        }
    }
}
