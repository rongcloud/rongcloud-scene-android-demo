package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.UiUtils;

import java.util.List;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.OnItemClickListener;
import io.rong.imkit.picture.decoration.GridSpacingItemDecoration;


/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomSettingFragment extends BaseBottomSheetDialog {

    private AppCompatImageView mIvClose;
    private RecyclerView mRvFunctionList;

    private RcySAdapter adapter;
    private OnItemClickListener<MutableLiveData<IFun.BaseFun>> listener;
    private List<MutableLiveData<IFun.BaseFun>> funList;

    public RoomSettingFragment(OnItemClickListener<MutableLiveData<IFun.BaseFun>> listener) {
        super(R.layout.fragment_room_settings);
        this.listener = listener;
    }

    @Override
    public void initView() {
        mIvClose = (AppCompatImageView) getView().findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(v -> dismiss());
        mRvFunctionList = (RecyclerView) getView().findViewById(R.id.rv_function_list);
        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(
                ((GridLayoutManager) mRvFunctionList.getLayoutManager()).getSpanCount(),
                UiUtils.dp2px(20), true
        );
        mRvFunctionList.addItemDecoration(itemDecoration);
        adapter = new RcySAdapter<MutableLiveData<IFun.BaseFun>, RcyHolder>(getContext(), R.layout.item_room_setting) {
            @Override
            public void convert(RcyHolder holder, MutableLiveData<IFun.BaseFun> baseFun, int position) {
                baseFun.observe(RoomSettingFragment.this, (Observer<IFun.BaseFun>) baseFun1 -> {
                    holder.setImageDrawable(R.id.iv_icon, getResources().getDrawable(baseFun1.getIcon()));
                    holder.setText(R.id.tv_text, baseFun1.getText());
                    holder.itemView.setOnClickListener(v -> {
                        if (isDismiss(baseFun1)) {
                            dismissAllowingStateLoss();
                        }
                        if (listener != null) listener.clickItem(baseFun, position);
                    });
                });
            }
        };
        mRvFunctionList.setAdapter(adapter);
        adapter.setData(funList, true);
    }

    /**
     * 判断哪些点击事件需要关闭弹框
     */
    private boolean isDismiss(IFun.BaseFun baseFun) {
        if (baseFun instanceof RoomBackgroundFun
                || baseFun instanceof RoomMusicFun
                || baseFun instanceof RoomNameFun
                || baseFun instanceof RoomNoticeFun
                || baseFun instanceof RoomPauseFun
                || baseFun instanceof RoomShieldFun
                || baseFun instanceof RoomLockFun
                || baseFun instanceof RoomMuteFun
                || baseFun instanceof RoomMuteAllFun
                || baseFun instanceof RoomLockAllSeatFun
                || baseFun instanceof RoomSeatSizeFun
                || baseFun instanceof RoomSeatModeFun
                || baseFun instanceof RoomTagsFun
                || baseFun instanceof RoomBeautyFun
                || baseFun instanceof RoomBeautyMakeUpFun
                || baseFun instanceof RoomSpecialEffectsFun
                || baseFun instanceof RoomVideoSetFun) {
            return true;
        }
        return false;
    }

    public void show(FragmentManager fragmentManager, List<MutableLiveData<IFun.BaseFun>> funList) {
        this.funList = funList;
        show(fragmentManager, RoomSettingFragment.class.getSimpleName());
    }
}
