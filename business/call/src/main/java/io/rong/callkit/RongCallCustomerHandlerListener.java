/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit;

import android.content.Context;
import android.content.Intent;
import android.view.SurfaceView;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import java.util.ArrayList;
import java.util.List;

public interface RongCallCustomerHandlerListener {

    List<String> handleActivityResult(int requestCode, int resultCode, Intent data);

    void addMember(Context context, ArrayList<String> currentMemberIds);

    void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType);

    void onCallConnected(RongCallSession callSession, SurfaceView localVideo);

    void onCallDisconnected(
            RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason);

    void onCallMissed(RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason);
}
