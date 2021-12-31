package cn.rong.combusis.ui.room.fragment.seatsetting;

import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.SeatActionClickListener;

/**
 * 撤销麦位fragment
 */
public class RevokeSeatRequestFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {


    private AppCompatTextView btnCancelRequest;
    private AppCompatTextView btnCancel;
    private SeatActionClickListener seatActionClickListener;


    public RevokeSeatRequestFragment() {
        super(R.layout.fragment_revoke_seat_request);
    }

    /**
     * 设置回调接口
     *
     * @param seatActionClickListener
     */
    public void setSeatActionClickListener(SeatActionClickListener seatActionClickListener) {
        this.seatActionClickListener = seatActionClickListener;
    }

    @Override
    public void initView() {
        btnCancelRequest = (AppCompatTextView) getView().findViewById(R.id.btn_cancel_request);
        btnCancel = (AppCompatTextView) getView().findViewById(R.id.btn_cancel);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnCancel.setOnClickListener(this::onClick);
        btnCancelRequest.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
        } else if (id == R.id.btn_cancel_request) {
            if (seatActionClickListener != null) {
                seatActionClickListener.cancelRequestSeat(new ClickCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean result, String msg) {
                        if (result) {
                            dismiss();
                        }
                    }
                });
            }
        }
    }
}
