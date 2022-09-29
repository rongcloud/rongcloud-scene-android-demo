package cn.rongcloud.roomkit.ui.friend;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.ImageLoader;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.friend.model.Friend;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;

/**
 * @author gyn
 * @date 2021/9/29
 */
public class SendPrivateMessageFragment extends BaseBottomSheetDialog {

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
        ImageLoader.loadUrl(mIvMemberPortrait, friend.getPortrait(), R.drawable.default_portrait);
    }


    public void showDialog(FragmentManager fragmentManager, Friend friend) {
        this.friend = friend;
        show(fragmentManager, SendPrivateMessageFragment.class.getSimpleName());
    }
}
