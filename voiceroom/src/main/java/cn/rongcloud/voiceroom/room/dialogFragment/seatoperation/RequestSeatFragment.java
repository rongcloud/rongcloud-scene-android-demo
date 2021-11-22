package cn.rongcloud.voiceroom.room.dialogFragment.seatoperation;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kit.utils.ImageLoader;
import com.rongcloud.common.base.BaseFragment;

import java.util.List;

import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rongcloud.voiceroom.R;
import cn.rongcloud.voiceroom.room.VoiceRoomModel;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 请求连麦fragment
 */
public class RequestSeatFragment extends BaseFragment {


    private RecyclerView rvList;
    private MyAdapter myAdapter;
    private VoiceRoomModel voiceRoomModel;

    public RequestSeatFragment(VoiceRoomModel voiceRoomModel) {
        super(R.layout.layout_list);
        this.voiceRoomModel = voiceRoomModel;
    }

    @Override
    public void initView() {
        rvList = (RecyclerView) getView().findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new MyAdapter();
        rvList.setAdapter(myAdapter);
        refreshData(voiceRoomModel.getRequestSeats());

        //监听一下变化方便及时去更新Ui
        voiceRoomModel.obRequestSeatListChange()
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        refreshData(users);
                    }
                });
    }

    @NonNull
    @Override
    public String getTitle() {
        return "申请连麦";
    }

    /**
     * 刷新列表
     *
     * @param uiMemberModels
     */
    public void refreshData(List<User> uiMemberModels) {
        myAdapter.refreshData(uiMemberModels);
    }

    /**
     * 同意麦位申请
     *
     * @param userId
     */
    private void acceptRequest(String userId) {
        //判断麦位是否已经满了，这里需要交给界面去处理，因为麦位信息是时时刻刻在变化的
        voiceRoomModel.acceptRequestSeat(userId, new ClickCallback<Boolean>() {
            @Override
            public void onResult(Boolean result, String msg) {
                if (result) {
                    ((SeatOperationViewPagerFragment) getParentFragment()).dismiss();
                } else {
                    EToast.showToast(msg);
                }
            }
        });
    }

    /**
     * 创建适配器
     */
    class MyAdapter extends BaseListAdapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(parent);
        }


        class MyViewHolder extends BaseViewHolder {

            public MyViewHolder(@NonNull ViewGroup parent) {
                super(parent);
            }

            @Override
            public void bindView(@NonNull User uiMemberModel, @NonNull View itemView) {
                ImageView iv_user_portrait = itemView.findViewById(R.id.iv_user_portrait);
                TextView tv_member_name = itemView.findViewById(R.id.tv_member_name);
                TextView tv_operation = itemView.findViewById(R.id.tv_operation);
                ImageLoader.loadUrl(iv_user_portrait, uiMemberModel.getPortraitUrl(), R.drawable.default_portrait, ImageLoader.Size.SZ_100);
                tv_member_name.setText(uiMemberModel.getUserName());
                tv_operation.setText("接受");
                tv_operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_operation.setEnabled(false);
                        acceptRequest(uiMemberModel.getUserId());
                    }
                });
            }
        }
    }

}
