package cn.rong.combusis.ui.room.fragment.gift;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.recycle.RcyHolder;
import com.basis.adapter.recycle.RcySAdapter;
import com.basis.ui.BasePopupWindow;
import com.rongcloud.common.utils.UiUtils;

import java.util.Arrays;

import cn.rong.combusis.R;
import cn.rong.combusis.common.ui.dialog.EditDialog;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2021/10/9
 */
public class NumPopupWindow extends BasePopupWindow {
    OnSelectNum onSelectNum;
    private AppCompatTextView mPopTitle;
    private RecyclerView mRcyPop;
    private EditDialog mEditDialog;
    private int selected = 1;

    public NumPopupWindow(Context context, int selected, OnSelectNum onSelectNum) {
        super(context, R.layout.popup_gift_num, UiUtils.INSTANCE.dp2Px(context, 160), UiUtils.INSTANCE.dp2Px(context, 30) * 6, false);
        this.onSelectNum = onSelectNum;
        this.selected = selected;
    }

    @Override
    protected void initView(@NonNull View content) {
        super.initView(content);

        mPopTitle = content.findViewById(R.id.pop_title);
        mRcyPop = content.findViewById(R.id.rcy_pop);

        mPopTitle.setText("自定义");
        RcySAdapter adapter = new RcySAdapter<Integer, RcyHolder>(content.getContext(), R.layout.item_gift_num) {
            @Override
            public void convert(RcyHolder holder, Integer integer, int position) {
                holder.setText(R.id.pop_info, String.valueOf(integer));
                holder.setSelected(R.id.pop_info, selected == integer);
                holder.itemView.setOnClickListener(v -> {
                    selected = integer;
                    dismiss();
                });
            }
        };
        mRcyPop.setAdapter(adapter);
        adapter.setData(Arrays.asList(999, 666, 99, 10, 1), true);

        setOnDismissListener(() -> {
            if (onSelectNum != null) {
                onSelectNum.selectNum(selected);
            }
        });
        mPopTitle.setOnClickListener(v -> {
            mEditDialog = new EditDialog(
                    getContentView().getContext(),
                    "自定义礼物数量",
                    "请输入数量",
                    "",
                    Integer.MAX_VALUE,
                    true,
                    () -> null,
                    s -> {
                        if (s != null && !s.isEmpty()) {
                            try {
                                selected = Integer.valueOf(s);
                                mEditDialog.dismiss();
                                dismiss();
                            } catch (NumberFormatException exception) {
                                ToastUtils.s(content.getContext(), "您输入的数量太大或不是数字");
                            }
                        }
                        return null;
                    }
            );
            mEditDialog.show();
        });
    }

    public void show(View anchor) {
        setFocusable(true);
        super.showAsDropUp(anchor, 2);
        setOutsideTouchable(false);
    }

    public interface OnSelectNum {
        void selectNum(int num);
    }
}
