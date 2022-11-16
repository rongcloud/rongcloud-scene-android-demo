package io.rong.callkit.util;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;


import com.basis.utils.UIKit;

import io.rong.callkit.R;
import io.rong.calllib.RongCallCommon;

public class CallReasonUtil {

    private static String getString(@NonNull @StringRes int id) {
        return UIKit.getResources().getString(id);
    }

    /**
     * switch (reason) {
     * case RCCallDisconnectReasonCancel:
     * [self showToast:@"已取消"];
     * break;
     * case RCCallDisconnectReasonReject:
     * [self showToast:@"已拒绝"];
     * break;
     * case RCCallDisconnectReasonHangup:
     * case RCCallDisconnectReasonRemoteHangup:
     * [self showToast:@"通话结束"];
     * break;
     * case RCCallDisconnectReasonBusyLine:
     * [self showToast:@"忙碌中"];
     * break;
     * case RCCallDisconnectReasonNoResponse:
     * [self showToast:@"未接听"];
     * break;
     * case RCCallDisconnectReasonEngineUnsupported:
     * [self showToast:@"不支持当前引擎"];
     * break;
     * case RCCallDisconnectReasonNetworkError:
     * [self showToast:@"网络出错"];
     * break;
     * case RCCallDisconnectReasonRemoteCancel:
     * [self showToast:@"对方已取消"];
     * break;
     * case RCCallDisconnectReasonRemoteReject:
     * [self showToast:@"对方已拒绝"];
     * break;
     * case RCCallDisconnectReasonRemoteBusyLine:
     * [self showToast:@"对方忙碌中"];
     * break;
     * case RCCallDisconnectReasonRemoteNoResponse:
     * [self showToast:@"对方未接听"];
     * break;
     * case RCCallDisconnectReasonRemoteEngineUnsupported:
     * [self showToast:@"对方不支持当前引擎"];
     * break;
     * case RCCallDisconnectReasonRemoteNetworkError:
     * [self showToast:@"对方网络出错"];
     * break;
     * case RCCallDisconnectReasonAcceptByOtherClient:
     * [self showToast:@"其它端已接听"];
     * break;
     * case RCCallDisconnectReasonAddToBlackList:
     * [self showToast:@"您已被加入黑名单"];
     * break;
     * case RCCallDisconnectReasonDegrade:
     * [self showToast:@"您已被降级为观察者"];
     * break;
     * case RCCallDisconnectReasonKickedByServer:
     * [self showToast:@"禁止通话"];
     * break;
     * case RCCallDisconnectReasonMediaServerClosed:
     * [self showToast:@"音视频服务已关闭"];
     * break;
     * default:
     * break;
     * }
     *
     * @param reason
     */
    public static void showToastByReason(RongCallCommon.CallDisconnectedReason reason) {
        String text = null;
        switch (reason) {
            case CANCEL:
                text = getString(R.string.rc_voip_mo_cancel);
                break;
            case REJECT:
                text = getString(R.string.rc_voip_mo_reject);
                break;
            case NO_RESPONSE:
                text = getString(R.string.rc_voip_mo_no_response);
                break;
            case BUSY_LINE:
                text = getString(R.string.rc_voip_mo_business_line);
                break;
            case REMOTE_BUSY_LINE:
                text = getString(R.string.rc_voip_mt_busy_toast);
                break;
            case REMOTE_CANCEL:
                text = getString(R.string.rc_voip_mt_cancel);
                break;
            case REMOTE_REJECT:
                text = getString(R.string.rc_voip_mt_reject);
                break;
            case REMOTE_NO_RESPONSE:
                text = getString(R.string.rc_voip_mt_no_response);
                break;
            case NETWORK_ERROR:
                if (!CallKitUtils.isNetworkAvailable(UIKit.getContext())) {
                    text = getString(R.string.rc_voip_call_network_error);
                } else {
                    text = getString(R.string.rc_voip_call_terminalted);
                }
                break;
            case REMOTE_HANGUP:
            case HANGUP:
            case INIT_VIDEO_ERROR:
                text = getString(R.string.rc_voip_call_terminalted);
                break;
            case OTHER_DEVICE_HAD_ACCEPTED:
                text = getString(R.string.rc_voip_call_other);
                break;
        }
        if (text != null) {
            Toast.makeText(UIKit.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

}
