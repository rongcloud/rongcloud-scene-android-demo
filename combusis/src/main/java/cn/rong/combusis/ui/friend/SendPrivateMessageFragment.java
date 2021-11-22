package cn.rong.combusis.ui.friend;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;

import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.ui.friend.model.Friend;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;

/**
 * @author gyn
 * @date 2021/9/29
 */
public class SendPrivateMessageFragment extends BaseBottomSheetDialogFragment {

    private AppCompatButton mBtnSendMessage;
    private AppCompatTextView mTvMemberName;
    private CircleImageView mIvMemberPortrait;
    private Friend friend;

    public SendPrivateMessageFragment() {
        super(R.layout.fragment_send_private_message);
    }

    @Override
    public void initView() {
        mBtnSendMessage = (AppCompatButton) getView().findViewById(R.id.btn_send_message);
        mTvMemberName = (AppCompatTextView) getView().findViewById(R.id.tv_member_name);
        mIvMemberPortrait = (CircleImageView) getView().findViewById(R.id.iv_member_portrait);
        mBtnSendMessage.setOnClickListener(v -> {
            dismiss();
            RouteUtils.routeToConversationActivity(
                    requireContext(),
                    Conversation.ConversationType.PRIVATE,
                    friend.getUid()
            );
        });
        mTvMemberName.setText(friend.getName());
        ImageLoaderUtil.INSTANCE.loadImage(getContext(), mIvMemberPortrait, friend.getPortrait(), R.drawable.default_portrait);
    }


    public void showDialog(FragmentManager fragmentManager, Friend friend) {
        this.friend = friend;
        show(fragmentManager);
    }
}
