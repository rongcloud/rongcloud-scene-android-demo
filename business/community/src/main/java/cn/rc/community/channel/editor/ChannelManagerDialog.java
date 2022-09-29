package cn.rc.community.channel.editor;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyAdapter;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.List;

import cn.rc.community.CommunityStyleBottomDialog;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.GroupBean;
import cn.rc.community.bean.ListBean;
import cn.rc.community.helper.CommunityHelper;

public class ChannelManagerDialog extends CommunityStyleBottomDialog implements CommunityStyleBottomDialog.OnTitleClickListener {
    private boolean group;
    private GroupEditorAdapter groupEditorAdapter;
    private ChannelEditorAdapter channelEditorAdapter;
    private ItemTouchHelper helper;

    public ChannelManagerDialog(Activity activity, boolean group) {
        super(activity, 70);
        this.group = group;
        View view = UIKit.inflate(R.layout.layout_channel_editor_dialog);
        setCustomerContent(view);
        setTitleClickListener(this);
        setTitle(ResUtil.getString(group ? R.string.cmu_group_manager : R.string.cmu_channel_manager));
        initView(view);
    }

    private RecyclerView rcChannel;

    void initView(View view) {
        rcChannel = UIKit.getView(view, R.id.rc_channel);
        rcChannel.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        if (group) {
            groupEditorAdapter = new GroupEditorAdapter(dialog.activity);
            rcChannel.setAdapter(groupEditorAdapter);
            helper = new ItemTouchHelper(new ItemTouchCallback(groupEditorAdapter));
        } else {
            channelEditorAdapter = new ChannelEditorAdapter(dialog.activity);
            rcChannel.setAdapter(channelEditorAdapter);
            helper = new ItemTouchHelper(new ItemTouchCallback(channelEditorAdapter));
        }
        helper.attachToRecyclerView(rcChannel);

    }

    /**
     * 设置数据源
     *
     * @param groupDetails
     */
    public void setGroupDetails(List<ListBean> groupDetails) {
        List<ListBean> datas = new ArrayList<>();
        if (group) {
            //如果是分组
            for (ListBean groupDetail : groupDetails) {
                if (groupDetail instanceof GroupBean) {
                    datas.add((GroupBean) groupDetail);
                }
            }
        } else {
            //如果是频道编辑的话
            for (ListBean groupDetail : groupDetails) {
                if (groupDetail != null && groupDetail instanceof GroupBean) {
                    datas.add(groupDetail);
                    List<ChannelBean> channelList = ((GroupBean) groupDetail).getChannelList();
                    if (channelList != null)
                        for (ChannelBean channelBean : channelList) {
                            datas.add(channelBean);
                        }
                } else {
                    datas.add(groupDetail);
                }
            }
        }
        getAdapter().setData(datas, true);
    }

    public RcyAdapter getAdapter() {
        return group ? groupEditorAdapter : channelEditorAdapter;
    }

    @Override
    public void onSureClick(CommunityStyleBottomDialog dialog) {
        saveChannelAndGroup();
    }

    /**
     * 保存分组频道
     */
    private void saveChannelAndGroup() {
        CommunityDetailsBean temp = CommunityHelper.getInstance().getCommunityDetailsBean().clone();
        if (group) {
            //如果是分组的话
            List<GroupBean> groupBeanList = groupEditorAdapter.getData();
            temp.setGroupList(groupBeanList);
        } else {
            //如果是频道的话
            List<ListBean> listBeanList = channelEditorAdapter.getData();
            //先判断没有分组的频道数据
            ArrayList<ChannelBean> channelBeanArrayList = new ArrayList<>();
            //分组数据
            ArrayList<GroupBean> groupBeanArrayList = new ArrayList<>();
            boolean isNotInGroup = true;
            for (ListBean listBean : listBeanList) {
                if (isNotInGroup && listBean instanceof ChannelBean) {
                    channelBeanArrayList.add((ChannelBean) listBean);
                } else if (listBean instanceof GroupBean) {
                    isNotInGroup = false;//只要遇到了分组，说明已经不在分组里面了
                }

                if (!isNotInGroup && listBean instanceof GroupBean) {
                    ArrayList<ChannelBean> channelBeans = new ArrayList<>();
                    ((GroupBean) listBean).setChannelList(channelBeans);//清空当前的群组的频道
                    groupBeanArrayList.add((GroupBean) listBean);
                }
            }
            boolean isContain = false;
            for (GroupBean groupBean : groupBeanArrayList) {
                for (ListBean listBean : listBeanList) {
                    if (listBean instanceof GroupBean)
                        isContain = listBean.uid.equals(groupBean.uid) ? true : false;
                    if (isContain && listBean instanceof ChannelBean) {
                        groupBean.getChannelList().add((ChannelBean) listBean);
                    }
                }
            }
            temp.setGroupList(groupBeanArrayList);
            temp.setChannelList(channelBeanArrayList);
        }
        Logger.d("保存频道和分组设置");
        temp.setUpdateType(group ? Constants.UpdateType.UPDATE_TYPE_GROUP.getUpdateTypeCode()
                : Constants.UpdateType.UPDATE_TYPE_CHANNEL.getUpdateTypeCode());
        CommunityHelper.getInstance().saveCommunityAll(temp, new IResultBack<Wrapper>() {
            @Override
            public void onResult(Wrapper wrapper) {
                if (wrapper.ok()) {
                    dismiss();
                }
            }
        });
    }
}
