package cn.rongcloud.roomkit.ui.room;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.basis.ui.mvp.BaseMvpFragment;
import com.basis.ui.mvp.BasePresenter;
import com.basis.utils.Logger;
import com.basis.widget.dialog.VRCenterDialog;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import io.rong.imkit.utils.PermissionCheckUtil;

/**
 * @author gyn
 * @date 2021/9/17
 */
public abstract class AbsRoomFragment<P extends BasePresenter> extends BaseMvpFragment<P> implements SwitchRoomListener {

    public static final String ROOM_ID = "ROOM_ID";

    // 是否执行了joinRoom
    private boolean isExecuteJoinRoom = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("==================================onCreate:" + getTag());
    }

    private int bottomMargin = 0;

    @Override
    public void preJoinRoom() {
        if (!isExecuteJoinRoom) {
            isExecuteJoinRoom = true;
            joinRoom();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MiniRoomManager.getInstance().close();
        Logger.d("==================================onStart:" + getTag());
    }

    @Override
    public void destroyRoom() {
        isExecuteJoinRoom = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // viewPager2的onPageSelected，当从列表点击最后一个时，viewpager2选中最后一个时，会先执行onPageSelected，才执行fragment的onCreate,
        // 导致addSwitchRoomListener没执行，joinRoom就不会执行，这里判断一下没执行再执行一下。
        preJoinRoom();
        Logger.d("==================================onResume:" + getTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("==================================onPause:" + getTag());
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("==================================onStop:" + getTag());
    }

    // 是否是请求开启悬浮窗权限的过程中
    private boolean checkingOverlaysPermission;

    public boolean checkDrawOverlaysPermission(boolean needOpenPermissionSetting) {
        if (Build.BRAND.toLowerCase().contains("xiaomi") || Build.VERSION.SDK_INT >= 23) {
            if (PermissionCheckUtil.canDrawOverlays(requireContext(), needOpenPermissionSetting)) {
                checkingOverlaysPermission = false;
                return true;
            } else {
                if (needOpenPermissionSetting && !Build.BRAND.toLowerCase().contains("xiaomi")) {
                    checkingOverlaysPermission = true;
                }
                return false;
            }
        } else {
            checkingOverlaysPermission = false;
            return true;
        }
    }

    public void showOpenOverlaysPermissionDialog() {
        VRCenterDialog dialog = new VRCenterDialog(requireActivity(), null);
        dialog.replaceContent(getString(R.string.text_open_suspend_permission), getString(R.string.cancel), null, getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + requireActivity().getPackageName()));
                requireActivity().startActivity(intent);
            }
        }, null);
        dialog.show();
    }

    @Override
    public void initListener() {
        addSwitchRoomListener();
    }

    @Override
    public void onDestroyView() {
        removeSwitchRoomListener();
        super.onDestroyView();
    }

}

